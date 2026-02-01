package com.teacompliance.service;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.repository.ComplianceResultRepository;
import com.teacompliance.repository.ComplianceRuleRepository;
import com.teacompliance.service.evaluation.RuleEvaluationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * コンプライアンス評価エンジン
 * 
 * 茶葉ロットに対して全ルールを評価し、結果を保存する
 */
@Service
public class ComplianceEvaluationEngine {
    
    private static final Logger log = LoggerFactory.getLogger(ComplianceEvaluationEngine.class);
    
    private final ComplianceRuleRepository ruleRepository;
    private final ComplianceResultRepository resultRepository;
    private final List<RuleEvaluationStrategy> strategies;
    
    private Map<ComplianceRule.RuleType, RuleEvaluationStrategy> strategyMap;
    
    public ComplianceEvaluationEngine(ComplianceRuleRepository ruleRepository, 
                                   ComplianceResultRepository resultRepository,
                                   List<RuleEvaluationStrategy> strategies) {
        this.ruleRepository = ruleRepository;
        this.resultRepository = resultRepository;
        this.strategies = strategies;
    }
    
    /**
     * 初期化処理
     * Strategy パターンのマッピングを構築
     */
    private void initializeStrategies() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                    RuleEvaluationStrategy::getSupportedRuleType,
                    Function.identity()
                ));
        }
    }
    
    /**
     * 指定された茶葉ロットに対して全ルールを評価する
     * 
     * @param teaLot 評価対象の茶葉ロット
     * @return 評価結果リスト
     */
    @Transactional
    public List<ComplianceResult> evaluateTeaLot(TeaLot teaLot) {
        initializeStrategies();
        
        List<ComplianceRule> rules = ruleRepository.findAllOrderedBySeverityAndType();
        log.info("茶葉ロット {} の評価開始 - 適用ルール数: {}", teaLot.getLotCode(), rules.size());
        
        List<ComplianceResult> results = rules.stream()
            .map(rule -> evaluateRule(teaLot, rule))
            .collect(Collectors.toList());
        
        // 評価結果を保存
        List<ComplianceResult> savedResults = resultRepository.saveAll(results);
        
        // 出荷可否をログ出力
        boolean shippable = isShippable(teaLot.getId());
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
     * エラー結果を作成する
     * 
     * @param teaLot 茶葉ロット
     * @param rule ルール
     * @param errorMessage エラーメッセージ
     * @return エラー評価結果
     */
    private ComplianceResult createErrorResult(TeaLot teaLot, ComplianceRule rule, String errorMessage) {
        ComplianceResult result = new ComplianceResult();
        result.setTeaLotId(teaLot.getId());
        result.setRuleCode(rule.getRuleCode());
        result.setResult(ComplianceResult.EvaluationResult.FAIL);
        result.setSeverity(ComplianceRule.Severity.BLOCK);
        result.setMessage("評価エラー: " + errorMessage);
        result.setEvaluatedAt(LocalDateTime.now());
        return result;
    }
    
    /**
     * 出荷可能か判定する
     * 
     * @param teaLotId 茶葉ロットID
     * @return true: 出荷可能, false: 出荷不可
     */
    public boolean isShippable(Long teaLotId) {
        return resultRepository.isShippable(teaLotId);
    }
    
    /**
     * 評価サマリーを取得する
     * 
     * @param results 評価結果リスト
     * @return サマリー文字列
     */
    private String getEvaluationSummary(List<ComplianceResult> results) {
        long passCount = results.stream()
            .filter(r -> r.getResult() == ComplianceResult.EvaluationResult.PASS)
            .count();
        long failCount = results.size() - passCount;
        long blockCount = results.stream()
            .filter(r -> r.getSeverity() == ComplianceRule.Severity.BLOCK && r.getResult() == ComplianceResult.EvaluationResult.FAIL)
            .count();
        
        return String.format("合格: %d, 不合格: %d (BLOCK: %d)", passCount, failCount, blockCount);
    }
}
