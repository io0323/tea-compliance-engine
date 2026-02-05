package com.teacompliance.service.evaluation;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("香り評価戦略のテスト")
class AromaEvaluationStrategyTest {
    
    private AromaEvaluationStrategy strategy;
    private TeaLot teaLot;
    private ComplianceRule rule;
    
    @BeforeEach
    void setUp() {
        strategy = new AromaEvaluationStrategy();
        teaLot = new TeaLot();
        teaLot.setAromaScore(75);
        
        rule = new ComplianceRule();
        rule.setRuleType(ComplianceRule.RuleType.AROMA);
        rule.setThreshold(80.0);
        rule.setOperator(ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL);
    }
    
    @Test
    @DisplayName("香りスコアが基準値以上の場合は合格")
    void testEvaluate_HighScore_ShouldPass() {
        // Given
        teaLot.setAromaScore(85);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("85点");
        assertThat(result.getActualValue()).isEqualTo(85.0);
    }
    
    @Test
    @DisplayName("香りスコアが基準値未満の場合は不合格")
    void testEvaluate_LowScore_ShouldFail() {
        // Given
        teaLot.setAromaScore(65);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getMessage()).contains("65点");
        assertThat(result.getActualValue()).isEqualTo(65.0);
    }
    
    @Test
    @DisplayName("香りスコアが境界値の場合は合格")
    void testEvaluate_BoundaryScore_ShouldPass() {
        // Given
        teaLot.setAromaScore(80);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("80点");
        assertThat(result.getActualValue()).isEqualTo(80.0);
    }
    
    @Test
    @DisplayName("サポートするルールタイプがAROMAであること")
    void testGetSupportedRuleType() {
        // When
        ComplianceRule.RuleType ruleType = strategy.getSupportedRuleType();
        
        // Then
        assertThat(ruleType).isEqualTo(ComplianceRule.RuleType.AROMA);
    }
}
