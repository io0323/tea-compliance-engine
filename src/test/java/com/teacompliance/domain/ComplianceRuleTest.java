package com.teacompliance.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ComplianceRuleTest {
    
    private ComplianceRule rule;
    
    @BeforeEach
    void setUp() {
        rule = new ComplianceRule();
        rule.setId(1L);
        rule.setRuleCode("MOISTURE_001");
        rule.setDescription("水分量基準（JAS規格）");
        rule.setRuleType(ComplianceRule.RuleType.MOISTURE);
        rule.setThreshold(9.0);
        rule.setOperator(ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL);
        rule.setSeverity(ComplianceRule.Severity.BLOCK);
    }
    
    @Test
    @DisplayName("コンプライアンスルールの基本情報が正しく設定されること")
    void testComplianceRuleBasicInfo() {
        assertThat(rule.getId()).isEqualTo(1L);
        assertThat(rule.getRuleCode()).isEqualTo("MOISTURE_001");
        assertThat(rule.getDescription()).isEqualTo("水分量基準（JAS規格）");
        assertThat(rule.getRuleType()).isEqualTo(ComplianceRule.RuleType.MOISTURE);
        assertThat(rule.getThreshold()).isEqualTo(9.0);
        assertThat(rule.getOperator()).isEqualTo(ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL);
        assertThat(rule.getSeverity()).isEqualTo(ComplianceRule.Severity.BLOCK);
    }
    
    @Test
    @DisplayName("全項目コンストラクタが正しく動作すること")
    void testAllArgsConstructor() {
        ComplianceRule newRule = new ComplianceRule(
            2L, "PESTICIDE_001", "残留農薬基準（簡易モデル）",
            ComplianceRule.RuleType.PESTICIDE, 0.5,
            ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL,
            ComplianceRule.Severity.BLOCK
        );
        
        assertThat(newRule.getId()).isEqualTo(2L);
        assertThat(newRule.getRuleCode()).isEqualTo("PESTICIDE_001");
        assertThat(newRule.getDescription()).isEqualTo("残留農薬基準（簡易モデル）");
        assertThat(newRule.getRuleType()).isEqualTo(ComplianceRule.RuleType.PESTICIDE);
        assertThat(newRule.getThreshold()).isEqualTo(0.5);
        assertThat(newRule.getOperator()).isEqualTo(ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL);
        assertThat(newRule.getSeverity()).isEqualTo(ComplianceRule.Severity.BLOCK);
    }
    
    @Test
    @DisplayName("比較演算子のシンボルが正しく取得できること")
    void testComparisonOperatorSymbols() {
        assertThat(ComplianceRule.ComparisonOperator.GREATER_THAN.getSymbol()).isEqualTo(">");
        assertThat(ComplianceRule.ComparisonOperator.LESS_THAN.getSymbol()).isEqualTo("<");
        assertThat(ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL.getSymbol()).isEqualTo(">=");
        assertThat(ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL.getSymbol()).isEqualTo("<=");
    }
    
    @Test
    @DisplayName("ルールタイプの列挙値が正しく定義されていること")
    void testRuleTypeEnum() {
        ComplianceRule.RuleType[] types = ComplianceRule.RuleType.values();
        assertThat(types).hasSize(3);
        assertThat(types).contains(
            ComplianceRule.RuleType.MOISTURE,
            ComplianceRule.RuleType.PESTICIDE,
            ComplianceRule.RuleType.AROMA
        );
    }
    
    @Test
    @DisplayName("重要度レベルの列挙値が正しく定義されていること")
    void testSeverityEnum() {
        ComplianceRule.Severity[] severities = ComplianceRule.Severity.values();
        assertThat(severities).hasSize(3);
        assertThat(severities).contains(
            ComplianceRule.Severity.INFO,
            ComplianceRule.Severity.WARNING,
            ComplianceRule.Severity.BLOCK
        );
    }
    
    @Test
    @DisplayName("デフォルトコンストラクタでインスタンスが生成されること")
    void testDefaultConstructor() {
        ComplianceRule emptyRule = new ComplianceRule();
        
        assertThat(emptyRule).isNotNull();
        assertThat(emptyRule.getId()).isNull();
        assertThat(emptyRule.getRuleCode()).isNull();
        assertThat(emptyRule.getDescription()).isNull();
        assertThat(emptyRule.getRuleType()).isNull();
        assertThat(emptyRule.getThreshold()).isNull();
        assertThat(emptyRule.getOperator()).isNull();
        assertThat(emptyRule.getSeverity()).isNull();
    }
    
    @Test
    @DisplayName("セッターで値が正しく更新されること")
    void testSetters() {
        rule.setRuleCode("AROMA_001");
        rule.setDescription("香りスコア基準（社内品質ルール）");
        rule.setRuleType(ComplianceRule.RuleType.AROMA);
        rule.setThreshold(60.0);
        rule.setOperator(ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL);
        rule.setSeverity(ComplianceRule.Severity.WARNING);
        
        assertThat(rule.getRuleCode()).isEqualTo("AROMA_001");
        assertThat(rule.getDescription()).isEqualTo("香りスコア基準（社内品質ルール）");
        assertThat(rule.getRuleType()).isEqualTo(ComplianceRule.RuleType.AROMA);
        assertThat(rule.getThreshold()).isEqualTo(60.0);
        assertThat(rule.getOperator()).isEqualTo(ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL);
        assertThat(rule.getSeverity()).isEqualTo(ComplianceRule.Severity.WARNING);
    }
}
