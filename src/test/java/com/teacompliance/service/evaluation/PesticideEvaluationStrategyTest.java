package com.teacompliance.service.evaluation;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("農薬評価戦略のテスト")
class PesticideEvaluationStrategyTest {
    
    private PesticideEvaluationStrategy strategy;
    private TeaLot teaLot;
    private ComplianceRule rule;
    
    @BeforeEach
    void setUp() {
        strategy = new PesticideEvaluationStrategy();
        teaLot = new TeaLot();
        teaLot.setPesticideLevel(5.0);
        
        rule = new ComplianceRule();
        rule.setRuleType(ComplianceRule.RuleType.PESTICIDE);
        rule.setThreshold(5.0);
        rule.setOperator(ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL);
    }
    
    @Test
    @DisplayName("農薬レベルが基準値以下の場合は合格")
    void testEvaluate_LowPesticide_ShouldPass() {
        // Given
        teaLot.setPesticideLevel(3.0);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("3.0");
        assertThat(result.getActualValue()).isEqualTo(3.0);
    }
    
    @Test
    @DisplayName("農薬レベルが基準値を超える場合は不合格")
    void testEvaluate_HighPesticide_ShouldFail() {
        // Given
        teaLot.setPesticideLevel(8.0);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getMessage()).contains("8.0");
        assertThat(result.getActualValue()).isEqualTo(8.0);
    }
    
    @Test
    @DisplayName("農薬レベルが境界値の場合は合格")
    void testEvaluate_BoundaryPesticide_ShouldPass() {
        // Given
        teaLot.setPesticideLevel(5.0);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("5.0");
        assertThat(result.getActualValue()).isEqualTo(5.0);
    }
    
    @Test
    @DisplayName("サポートするルールタイプがPESTICIDEであること")
    void testGetSupportedRuleType() {
        // When
        ComplianceRule.RuleType ruleType = strategy.getSupportedRuleType();
        
        // Then
        assertThat(ruleType).isEqualTo(ComplianceRule.RuleType.PESTICIDE);
    }
}
