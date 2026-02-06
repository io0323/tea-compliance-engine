package com.teacompliance.service;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.repository.ComplianceResultRepository;
import com.teacompliance.repository.ComplianceRuleRepository;
import com.teacompliance.service.evaluation.RuleEvaluationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 最適化されたコンプライアンス評価エンジン
 * 
 * キャッシュ戦略とパフォーマンス改善を実装
 */
@Service
public class ComplianceEvaluationEngineOptimized {
    
    private static final Logger log = LoggerFactory.getLogger(ComplianceEvaluationEngineOptimized.class);
    
    private final ComplianceRuleRepository ruleRepository;
    private final ComplianceResultRepository resultRepository;
    private final List<RuleEvaluationStrategy> strategies;
    
    // キャッシュ用のフィールド
    private volatile Map<ComplianceRule.RuleType, RuleEvaluationStrategy> strategyMap;
    private volatile List<ComplianceRule> cachedRules;
    private volatile long rulesCacheTimestamp = 0;
    private static final long RULES_CACHE_TTL = 300_000; // 5分
    
    // スレッドセーフなキャッシュ
    private final Map<Long, Boolean> shippableCache = new ConcurrentHashMap<>();
    
    public ComplianceEvaluationEngineOptimized(ComplianceRuleRepository ruleRepository, 
                                       ComplianceResultRepository resultRepository,
                                       List<RuleEvaluationStrategy> strategies) {
        this.ruleRepository = ruleRepository;
        this.resultRepository = resultRepository;
        this.strategies = strategies;
    }
    
    @PostConstruct
    public void init() {
        initializeStrategies();
    }
    
    /**
     * 戦略マップを初期化（遅延初期化）
     */
    private void initializeStrategies() {
        if (strategyMap == null) {
            synchronized (this) {
                if (strategyMap == null) {
                    strategyMap = strategies.stream()
                        .collect(Collectors.toMap(
                            RuleEvaluationStrategy::getSupportedRuleType,
                            Function.identity()
                        ));
                }
            }
        }
    }
    
    /**
     * ルールをキャッシュから取得（TTL付き）
     */
    private List<ComplianceRule> getCachedRules() {
        long currentTime = System.currentTimeMillis();
        if (cachedRules == null || (currentTime - rulesCacheTimestamp) > RULES_CACHE_TTL) {
            synchronized (this) {
                if (cachedRules == null || (currentTime - rulesCacheTimestamp) > RULES_CACHE_TTL) {
                    cachedRules = ruleRepository.findAllOrderedBySeverityAndType();
                    rulesCacheTimestamp = currentTime;
                    log.debug("ルールキャッシュを更新しました - ルール数: {}", cachedRules.size());
                }
            }
        }
        return cachedRules;
    }
    
    /**
     * 指定された茶葉ロットに対して全ルールを評価する（最適化版）
     * 
     * @param teaLot 評価対象の茶葉ロット
     * @return 評価結果リスト
     */
    @Transactional
    @Cacheable(value = "complianceEvaluation", key = "#teaLot.id")
    public List<ComplianceResult> evaluateTeaLot(TeaLot teaLot) {
        initializeStrategies();
        
        List<ComplianceRule> rules = getCachedRules();
        log.info("茶葉ロット {} の評価開 - 適用ルール数: {}", teaLot.getLotCode(), rules.size());
        
        // 並列ストリームで評価処理を並列化
        List<ComplianceResult> results = rules.parallelStream()
            .map(rule -> evaluateRule(teaLot, rule))
            .collect(Collectors.toList());
        
        // バルク保存でパフォーマンス向上
        List<ComplianceResult> savedResults = resultRepository.saveAll(results);
        
        // 出荷可否をキャッシュ付きで判定
        boolean shippable = isShippableCached(teaLot.getId());
        log.info("茶葉ロット {} の評価完了 - 出荷{}: {}", 
            teaLot.getLotCode(), 
            shippable ? "可能" : "不可",
            getEvaluationSummary(savedResults));
        
        return savedResults;
    }
    
    /**
     * 個別ルールを評価する
     * 
     * @param teaLot 評価対象の茶葉ロット
     * @param rule 適用するルール
     * @return 評価結果
     */
    private ComplianceResult evaluateRule(TeaLot teaLot, ComplianceRule rule) {
        RuleEvaluationStrategy strategy = strategyMap.get(rule.getRuleType());
        
        if (strategy == null) {
            log.warn("ルールタイプ {} の評価戦略が見つかりません: {}", rule.getRuleType(), rule.getRuleCode());
            return createErrorResult(teaLot, rule, "評価戦略が見つかりません");
        }
        
        try {
            RuleEvaluationStrategy.EvaluationResult evaluationResult = strategy.evaluate(teaLot, rule);
            
            ComplianceResult result = new ComplianceResult();
            result.setTeaLotId(teaLot.getId());
            result.setRuleCode(rule.getRuleCode());
            result.setResult(evaluationResult.isPassed() ? ComplianceResult.EvaluationResult.PASS : ComplianceResult.EvaluationResult.FAIL);
            result.setSeverity(rule.getSeverity());
            result.setMessage(evaluationResult.getMessage());
            result.setEvaluatedAt(LocalDateTime.now());
            
            return result;
                
        } catch (Exception e) {
            log.error("ルール評価中にエラー発生: {}", rule.getRuleCode(), e);
            return createErrorResult(teaLot, rule, "評価中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    /**
     * キャッシュ付きで出荷可否を判定
     * 
     * @param teaLotId 茶葉ロットID
     * @return 出荷可能な場合はtrue
     */
    @Cacheable(value = "shippableStatus", key = "#teaLotId")
    public boolean isShippableCached(Long teaLotId) {
        return shippableCache.computeIfAbsent(teaLotId, this::calculateShippable);
    }
    
    /**
     * 出荷可否を計算
     */
    private boolean calculateShippable(Long teaLotId) {
        List<ComplianceResult> results = resultRepository.findByTeaLotId(teaLotId);
        
        // BLOCKレベルの違反がある場合は即時不合格
        boolean hasBlockViolation = results.stream()
            .anyMatch(r -> r.getSeverity() == ComplianceRule.Severity.BLOCK && 
                           r.getResult() == ComplianceResult.EvaluationResult.FAIL);
        
        if (hasBlockViolation) {
            return false;
        }
        
        // FAILの数をカウント
        long failCount = results.stream()
            .filter(r -> r.getResult() == ComplianceResult.EvaluationResult.FAIL)
            .count();
        
        return failCount == 0;
    }
    
    /**
     * 従来の出荷可否判定メソッド（互換性のため）
     */
    public boolean isShippable(Long teaLotId) {
        return isShippableCached(teaLotId);
    }
    
    /**
     * キャッシュをクリア
     */
    @CacheEvict(value = {"complianceEvaluation", "shippableStatus"}, allEntries = true)
    public void clearCache() {
        shippableCache.clear();
        cachedRules = null;
        rulesCacheTimestamp = 0;
        log.info("評価エンジンのキャッシュをクリアしました");
    }
    
    /**
     * 特定茶葉ロットのキャッシュをクリア
     */
    @CacheEvict(value = {"complianceEvaluation", "shippableStatus"}, key = "#teaLotId")
    public void clearTeaLotCache(Long teaLotId) {
        shippableCache.remove(teaLotId);
        log.debug("茶葉ロット {} のキャッシュをクリアしました", teaLotId);
    }
    
    /**
     * エラー結果を作成
     */
    private ComplianceResult createErrorResult(TeaLot teaLot, ComplianceRule rule, String errorMessage) {
        ComplianceResult result = new ComplianceResult();
        result.setTeaLotId(teaLot.getId());
        result.setRuleCode(rule.getRuleCode());
        result.setResult(ComplianceResult.EvaluationResult.ERROR);
        result.setSeverity(rule.getSeverity());
        result.setMessage(errorMessage);
        result.setEvaluatedAt(LocalDateTime.now());
        return result;
    }
    
    /**
     * 評価サマリーを取得
     */
    private String getEvaluationSummary(List<ComplianceResult> results) {
        long passCount = results.stream()
            .filter(r -> r.getResult() == ComplianceResult.EvaluationResult.PASS)
            .count();
        long failCount = results.stream()
            .filter(r -> r.getResult() == ComplianceResult.EvaluationResult.FAIL)
            .count();
        long blockCount = results.stream()
            .filter(r -> r.getSeverity() == ComplianceRule.Severity.BLOCK && 
                           r.getResult() == ComplianceResult.EvaluationResult.FAIL)
            .count();
        
        return String.format("合格: %d, 不合格: %d (BLOCK: %d)", passCount, failCount, blockCount);
    }
}
