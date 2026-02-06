package com.teacompliance.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ComplianceResultTest {
    
    private ComplianceResult result;
    
    @BeforeEach
    void setUp() {
        result = new ComplianceResult();
        result.setId(1L);
        result.setTeaLotId(1L);
        result.setRuleCode("MOISTURE_001");
        result.setResult(ComplianceResult.EvaluationResult.PASS);
        result.setSeverity(ComplianceRule.Severity.INFO);
        result.setMessage("水分量チェック: 合格");
        result.setEvaluatedAt(LocalDateTime.of(2024, 5, 15, 10, 30, 0));
    }
    
    @Test
    @DisplayName("コンプライアンス結果の基本情報が正しく設定されること")
    void testComplianceResultBasicInfo() {
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTeaLotId()).isEqualTo(1L);
        assertThat(result.getRuleCode()).isEqualTo("MOISTURE_001");
        assertThat(result.getResult()).isEqualTo(ComplianceResult.EvaluationResult.PASS);
        assertThat(result.getSeverity()).isEqualTo(ComplianceRule.Severity.INFO);
        assertThat(result.getMessage()).isEqualTo("水分量チェック: 合格");
        assertThat(result.getEvaluatedAt()).isEqualTo(LocalDateTime.of(2024, 5, 15, 10, 30, 0));
    }
    
    @Test
    @DisplayName("全項目コンストラクタが正しく動作すること")
    void testAllArgsConstructor() {
        LocalDateTime evaluatedAt = LocalDateTime.of(2024, 5, 20, 14, 45, 0);
        ComplianceResult newResult = new ComplianceResult(
            2L, 2L, "PESTICIDE_001",
            ComplianceResult.EvaluationResult.FAIL,
            ComplianceRule.Severity.BLOCK,
            "残留農薬チェック: 不合格",
            evaluatedAt
        );
        
        assertThat(newResult.getId()).isEqualTo(2L);
        assertThat(newResult.getTeaLotId()).isEqualTo(2L);
        assertThat(newResult.getRuleCode()).isEqualTo("PESTICIDE_001");
        assertThat(newResult.getResult()).isEqualTo(ComplianceResult.EvaluationResult.FAIL);
        assertThat(newResult.getSeverity()).isEqualTo(ComplianceRule.Severity.BLOCK);
        assertThat(newResult.getMessage()).isEqualTo("残留農薬チェック: 不合格");
        assertThat(newResult.getEvaluatedAt()).isEqualTo(evaluatedAt);
    }
    
    @Test
    @DisplayName("評価結果の列挙値が正しく定義されていること")
    void testEvaluationResultEnum() {
        ComplianceResult.EvaluationResult[] results = ComplianceResult.EvaluationResult.values();
        assertThat(results).hasSize(3);
        assertThat(results).contains(
                ComplianceResult.EvaluationResult.PASS,
                ComplianceResult.EvaluationResult.FAIL,
                ComplianceResult.EvaluationResult.ERROR
        );
    }
    
    @Test
    @DisplayName("デフォルトコンストラクタでインスタンスが生成されること")
    void testDefaultConstructor() {
        ComplianceResult emptyResult = new ComplianceResult();
        
        assertThat(emptyResult).isNotNull();
        assertThat(emptyResult.getId()).isNull();
        assertThat(emptyResult.getTeaLotId()).isNull();
        assertThat(emptyResult.getRuleCode()).isNull();
        assertThat(emptyResult.getResult()).isNull();
        assertThat(emptyResult.getSeverity()).isNull();
        assertThat(emptyResult.getMessage()).isNull();
        assertThat(emptyResult.getEvaluatedAt()).isNull();
    }
    
    @Test
    @DisplayName("セッターで値が正しく更新されること")
    void testSetters() {
        result.setTeaLotId(3L);
        result.setRuleCode("AROMA_001");
        result.setResult(ComplianceResult.EvaluationResult.FAIL);
        result.setSeverity(ComplianceRule.Severity.WARNING);
        result.setMessage("香りスコアチェック: 不合格");
        result.setEvaluatedAt(LocalDateTime.of(2024, 5, 25, 16, 20, 0));
        
        assertThat(result.getTeaLotId()).isEqualTo(3L);
        assertThat(result.getRuleCode()).isEqualTo("AROMA_001");
        assertThat(result.getResult()).isEqualTo(ComplianceResult.EvaluationResult.FAIL);
        assertThat(result.getSeverity()).isEqualTo(ComplianceRule.Severity.WARNING);
        assertThat(result.getMessage()).isEqualTo("香りスコアチェック: 不合格");
        assertThat(result.getEvaluatedAt()).isEqualTo(LocalDateTime.of(2024, 5, 25, 16, 20, 0));
    }
    
    @Test
    @DisplayName("BLOCKレベルのFAIL結果を作成できること")
    void testCreateBlockFailureResult() {
        ComplianceResult blockFailure = new ComplianceResult();
        blockFailure.setTeaLotId(1L);
        blockFailure.setRuleCode("MOISTURE_001");
        blockFailure.setResult(ComplianceResult.EvaluationResult.FAIL);
        blockFailure.setSeverity(ComplianceRule.Severity.BLOCK);
        blockFailure.setMessage("水分量超過: 出荷不可");
        blockFailure.setEvaluatedAt(LocalDateTime.now());
        
        assertThat(blockFailure.getResult()).isEqualTo(ComplianceResult.EvaluationResult.FAIL);
        assertThat(blockFailure.getSeverity()).isEqualTo(ComplianceRule.Severity.BLOCK);
        assertThat(blockFailure.getMessage()).contains("出荷不可");
    }
}
