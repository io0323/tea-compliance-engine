package com.teacompliance.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * コンプライアンス評価結果エンティティ
 * 
 * 茶葉ロットに対するルール評価結果を保存する
 */
@Entity
@Table(name = "compliance_results")
public class ComplianceResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long teaLotId;
    
    @Column(nullable = false)
    private String ruleCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationResult result;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceRule.Severity severity;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(nullable = false)
    private LocalDateTime evaluatedAt;
    
    // デフォルトコンストラクタ
    public ComplianceResult() {}
    
    // 全項目コンストラクタ
    public ComplianceResult(Long id, Long teaLotId, String ruleCode, 
                          EvaluationResult result, ComplianceRule.Severity severity, 
                          String message, LocalDateTime evaluatedAt) {
        this.id = id;
        this.teaLotId = teaLotId;
        this.ruleCode = ruleCode;
        this.result = result;
        this.severity = severity;
        this.message = message;
        this.evaluatedAt = evaluatedAt;
    }
    
    // GetterとSetter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTeaLotId() { return teaLotId; }
    public void setTeaLotId(Long teaLotId) { this.teaLotId = teaLotId; }
    
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    
    public EvaluationResult getResult() { return result; }
    public void setResult(EvaluationResult result) { this.result = result; }
    
    public ComplianceRule.Severity getSeverity() { return severity; }
    public void setSeverity(ComplianceRule.Severity severity) { this.severity = severity; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
    
    /**
     * 評価結果を定義する列挙型
     */
    public enum EvaluationResult {
        PASS,  // 合格
        FAIL,   // 不合格
        ERROR   // エラー
    }
}
