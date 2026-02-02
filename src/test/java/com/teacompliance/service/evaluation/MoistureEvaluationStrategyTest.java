package com.teacompliance.service.evaluation;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoistureEvaluationStrategyTest {
    
    private MoistureEvaluationStrategy strategy;
    private TeaLot teaLot;
    private ComplianceRule rule;
    
    @BeforeEach
    void setUp() {
        strategy = new MoistureEvaluationStrategy();
        
        teaLot = new TeaLot();
        teaLot.setLotCode("TL-2024-001");
        teaLot.setMoisture(8.5);
        
        rule = new ComplianceRule();
        rule.setRuleCode("MOISTURE_001");
        rule.setRuleType(ComplianceRule.RuleType.MOISTURE);
        rule.setThreshold(9.0);
        rule.setOperator(ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL);
        rule.setSeverity(ComplianceRule.Severity.BLOCK);
    }
    
    @Test
    @DisplayName("水分量が基準値以下の場合に合格と判定されること")
    void testEvaluate_MoistureBelowThreshold_Pass() {
        // Given
        teaLot.setMoisture(8.0); // 9.0 <= 9.0
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("合格");
        assertThat(result.getActualValue()).isEqualTo(8.0);
    }
    
    @Test
    @DisplayName("水分量が基準値と等しい場合に合格と判定されること")
    void testEvaluate_MoistureEqualsThreshold_Pass() {
        // Given
        teaLot.setMoisture(9.0); // 9.0 <= 9.0
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("合格");
        assertThat(result.getActualValue()).isEqualTo(9.0);
    }
    
    @Test
    @DisplayName("水分量が基準値を超える場合に不合格と判定されること")
    void testEvaluate_MoistureAboveThreshold_Fail() {
        // Given
        teaLot.setMoisture(9.5); // 9.5 > 9.0
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getMessage()).contains("不合格");
        assertThat(result.getActualValue()).isEqualTo(9.5);
    }
    
    @Test
    @DisplayName("比較演算子GREATER_THANで正しく評価されること")
    void testEvaluate_GreaterThanOperator() {
        // Given
        rule.setOperator(ComplianceRule.ComparisonOperator.GREATER_THAN);
        rule.setThreshold(8.0);
        teaLot.setMoisture(8.5); // 8.5 > 8.0
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("合格");
    }
    
    @Test
    @DisplayName("比較演算子LESS_THANで正しく評価されること")
    void testEvaluate_LessThanOperator() {
        // Given
        rule.setOperator(ComplianceRule.ComparisonOperator.LESS_THAN);
        rule.setThreshold(9.0);
        teaLot.setMoisture(8.5); // 8.5 < 9.0
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("合格");
    }
    
    @Test
    @DisplayName("比較演算子GREATER_THAN_OR_EQUALで正しく評価されること")
    void testEvaluate_GreaterThanOrEqualOperator() {
        // Given
        rule.setOperator(ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL);
        rule.setThreshold(8.0);
        teaLot.setMoisture(8.0); // 8.0 >= 8.0
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getMessage()).contains("合格");
    }
    
    @Test
    @DisplayName("サポートするルールタイプがMOISTUREであること")
    void testGetSupportedRuleType() {
        // When
        ComplianceRule.RuleType result = strategy.getSupportedRuleType();
        
        // Then
        assertThat(result).isEqualTo(ComplianceRule.RuleType.MOISTURE);
    }
    
    @Test
    @DisplayName("評価メッセージに適切な情報が含まれること")
    void testEvaluateMessage_Content() {
        // Given
        teaLot.setMoisture(8.2);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        String message = result.getMessage();
        assertThat(message).contains("水分量チェック");
        assertThat(message).contains("8.2%");
        assertThat(message).contains("<=");
        assertThat(message).contains("9.0%");
        assertThat(message).contains("合格");
    }
    
    @Test
    @DisplayName("境界値テスト：基準値ギリギリで合格すること")
    void testEvaluate_BoundaryValue_Pass() {
        // Given
        rule.setThreshold(8.0);
        teaLot.setMoisture(8.0);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getActualValue()).isEqualTo(8.0);
    }
    
    @Test
    @DisplayName("境界値テスト：基準値を0.01超えた場合に不合格すること")
    void testEvaluate_BoundaryValue_Fail() {
        // Given
        rule.setThreshold(8.0);
        teaLot.setMoisture(8.01);
        
        // When
        RuleEvaluationStrategy.EvaluationResult result = strategy.evaluate(teaLot, rule);
        
        // Then
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getActualValue()).isEqualTo(8.01);
    }
}
