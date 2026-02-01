package com.teacompliance.domain;

import jakarta.persistence.*;

/**
 * コンプライアンスルールエンティティ
 * 
 * 茶葉の品質・法令基準を定義するルール
 */
@Entity
@Table(name = "compliance_rules")
public class ComplianceRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String ruleCode;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;
    
    @Column(nullable = false)
    private Double threshold;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComparisonOperator operator;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;
    
    // デフォルトコンストラクタ
    public ComplianceRule() {}
    
    // 全項目コンストラクタ
    public ComplianceRule(Long id, String ruleCode, String description, 
                         RuleType ruleType, Double threshold, ComparisonOperator operator, Severity severity) {
        this.id = id;
        this.ruleCode = ruleCode;
        this.description = description;
        this.ruleType = ruleType;
        this.threshold = threshold;
        this.operator = operator;
        this.severity = severity;
    }
    
    // GetterとSetter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public RuleType getRuleType() { return ruleType; }
    public void setRuleType(RuleType ruleType) { this.ruleType = ruleType; }
    
    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
    
    public ComparisonOperator getOperator() { return operator; }
    public void setOperator(ComparisonOperator operator) { this.operator = operator; }
    
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    
    /**
     * ルールタイプを定義する列挙型
     */
    public enum RuleType {
        MOISTURE,    // 水分量
        PESTICIDE,   // 残留農薬
        AROMA        // 香りスコア
    }
    
    /**
     * 比較演算子を定義する列挙型
     */
    public enum ComparisonOperator {
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN_OR_EQUAL("<=");
        
        private final String symbol;
        
        ComparisonOperator(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
    
    /**
     * 重要度レベルを定義する列挙型
     */
    public enum Severity {
        INFO,     // 情報
        WARNING,  // 警告
        BLOCK     // 出荷不可
    }
}
