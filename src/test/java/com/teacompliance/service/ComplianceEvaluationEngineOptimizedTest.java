package com.teacompliance.service;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.repository.ComplianceResultRepository;
import com.teacompliance.repository.ComplianceRuleRepository;
import com.teacompliance.service.evaluation.RuleEvaluationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 最適化されたコンプライアンス評価エンジンのテスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ComplianceEvaluationEngineOptimizedのテスト")
class ComplianceEvaluationEngineOptimizedTest {
    
    @Mock
    private ComplianceRuleRepository ruleRepository;
    
    @Mock
    private ComplianceResultRepository resultRepository;
    
    @Mock
    private RuleEvaluationStrategy strategy1;
    
    @Mock
    private RuleEvaluationStrategy strategy2;
    
    @InjectMocks
    private ComplianceEvaluationEngineOptimized evaluationEngine;
    
    private TeaLot testTeaLot;
    private List<ComplianceRule> testRules;
    private List<RuleEvaluationStrategy> strategies;
    
    @BeforeEach
    void setUp() {
        testTeaLot = new TeaLot();
        testTeaLot.setId(1L);
        testTeaLot.setLotCode("TL-2024-001");
        testTeaLot.setOrigin("静岡県");
        testTeaLot.setVariety("一番茶");
        testTeaLot.setMoisture(4.5);
        testTeaLot.setPesticideLevel(0.8);
        testTeaLot.setAromaScore(8);
        testTeaLot.setProducedAt(LocalDateTime.of(2024, 5, 15, 10, 30));
        
        // テスト用ルール
        ComplianceRule rule1 = new ComplianceRule();
        rule1.setRuleCode("RULE001");
        rule1.setRuleType(ComplianceRule.RuleType.MOISTURE);
        rule1.setSeverity(ComplianceRule.Severity.BLOCK);
        rule1.setThresholdMin(0.0);
        rule1.setThresholdMax(6.0);
        
        ComplianceRule rule2 = new ComplianceRule();
        rule2.setRuleCode("RULE002");
        rule2.setRuleType(ComplianceRule.RuleType.PESTICIDE);
        rule2.setSeverity(ComplianceRule.Severity.WARNING);
        rule2.setThresholdMin(0.0);
        rule2.setThresholdMax(2.0);
        
        testRules = Arrays.asList(rule1, rule2);
        
        strategies = Arrays.asList(strategy1, strategy2);
        
        // リフレクションでstrategiesフィールドを設定
        ReflectionTestUtils.setField(evaluationEngine, "strategies", strategies);
    }
    
    @Test
    @DisplayName("戦略マップが正しく初期化されること")
    void testStrategyMapInitialization() {
        // When
        when(strategy1.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.MOISTURE);
        when(strategy2.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.PESTICIDE);
        when(ruleRepository.findAllOrderedBySeverityAndType()).thenReturn(testRules);
        
        // Then
        List<ComplianceResult> results = evaluationEngine.evaluateTeaLot(testTeaLot);
        
        // 検証
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(ruleRepository, times(1)).findAllOrderedBySeverityAndType();
        verify(resultRepository, times(1)).saveAll(any());
    }
    
    @Test
    @DisplayName("キャッシュが機能すること")
    void testCachingFunctionality() {
        // Given
        when(strategy1.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.MOISTURE);
        when(strategy2.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.PESTICIDE);
        when(ruleRepository.findAllOrderedBySeverityAndType()).thenReturn(testRules);
        
        // When - 1回目の呼び出し
        List<ComplianceResult> results1 = evaluationEngine.evaluateTeaLot(testTeaLot);
        
        // Then - 1回目はrepositoryを呼び出す
        verify(ruleRepository, times(1)).findAllOrderedBySeverityAndType();
        
        // When - 2回目の呼び出し
        List<ComplianceResult> results2 = evaluationEngine.evaluateTeaLot(testTeaLot);
        
        // Then - 2回目はrepositoryを呼び出さない（キャッシュから取得）
        verify(ruleRepository, times(1)).findAllOrderedBySeverityAndType(); // 呼び出し回数は増えない
        
        assertEquals(results1.size(), results2.size());
    }
    
    @Test
    @DisplayName("出荷可否キャッシュが機能すること")
    void testShippableCaching() {
        // Given
        when(strategy1.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.MOISTURE);
        when(strategy2.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.PESTICIDE);
        when(ruleRepository.findAllOrderedBySeverityAndType()).thenReturn(testRules);
        
        // 模擬の評価結果
        ComplianceResult passResult = new ComplianceResult();
        passResult.setTeaLotId(1L);
        passResult.setResult(ComplianceResult.EvaluationResult.PASS);
        
        ComplianceResult failResult = new ComplianceResult();
        failResult.setTeaLotId(1L);
        failResult.setResult(ComplianceResult.EvaluationResult.FAIL);
        failResult.setSeverity(ComplianceRule.Severity.WARNING);
        
        when(resultRepository.findByTeaLotId(1L)).thenReturn(Arrays.asList(passResult, failResult));
        
        // When - 1回目の呼び出し
        boolean shippable1 = evaluationEngine.isShippableCached(1L);
        
        // Then
        assertTrue(shippable1);
        verify(resultRepository, times(1)).findByTeaLotId(1L);
        
        // When - 2回目の呼び出し
        boolean shippable2 = evaluationEngine.isShippableCached(1L);
        
        // Then - 2回目はrepositoryを呼び出さない（キャッシュから取得）
        verify(resultRepository, times(1)).findByTeaLotId(1L); // 呼び出し回数は増えない
        
        assertEquals(shippable1, shippable2);
    }
    
    @Test
    @DisplayName("キャッシュクリアが機能すること")
    void testCacheClear() {
        // Given
        when(strategy1.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.MOISTURE);
        when(strategy2.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.PESTICIDE);
        when(ruleRepository.findAllOrderedBySeverityAndType()).thenReturn(testRules);
        
        // When - 事前に評価を実行
        evaluationEngine.evaluateTeaLot(testTeaLot);
        evaluationEngine.isShippableCached(1L);
        
        // When - キャッシュをクリア
        evaluationEngine.clearCache();
        
        // When - 再度評価を実行
        evaluationEngine.evaluateTeaLot(testTeaLot);
        
        // Then - repositoryが再度呼び出される
        verify(ruleRepository, times(2)).findAllOrderedBySeverityAndType(); // 2回呼び出される
    }
    
    @Test
    @DisplayName("BLOCK違反がある場合は出荷不可となること")
    void testBlockViolationMakesNotShippable() {
        // Given
        when(strategy1.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.MOISTURE);
        when(strategy2.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.PESTICIDE);
        when(ruleRepository.findAllOrderedBySeverityAndType()).thenReturn(testRules);
        
        // BLOCK違反の結果
        ComplianceResult blockResult = new ComplianceResult();
        blockResult.setTeaLotId(1L);
        blockResult.setResult(ComplianceResult.EvaluationResult.FAIL);
        blockResult.setSeverity(ComplianceRule.Severity.BLOCK);
        
        when(resultRepository.findByTeaLotId(1L)).thenReturn(Arrays.asList(blockResult));
        
        // When
        boolean shippable = evaluationEngine.isShippableCached(1L);
        
        // Then
        assertFalse(shippable);
    }
    
    @Test
    @DisplayName("戦略が見つからない場合はエラー結果が返されること")
    void testMissingStrategyReturnsError() {
        // Given
        when(strategy1.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.MOISTURE);
        when(strategy2.getSupportedRuleType()).thenReturn(ComplianceRule.RuleType.PESTICIDE);
        
        // サポートされていないルールタイプ
        ComplianceRule unsupportedRule = new ComplianceRule();
        unsupportedRule.setRuleCode("RULE999");
        unsupportedRule.setRuleType(ComplianceRule.RuleType.AROMA); // サポートされていない
        
        when(ruleRepository.findAllOrderedBySeverityAndType()).thenReturn(Arrays.asList(unsupportedRule));
        
        // When
        List<ComplianceResult> results = evaluationEngine.evaluateTeaLot(testTeaLot);
        
        // Then
        assertEquals(1, results.size());
        assertEquals(ComplianceResult.EvaluationResult.ERROR, results.get(0).getResult());
        assertTrue(results.get(0).getMessage().contains("評価戦略が見つかりません"));
    }
}
