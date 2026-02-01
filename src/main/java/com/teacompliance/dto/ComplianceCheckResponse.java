package com.teacompliance.dto;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.ComplianceRule;

import java.time.LocalDateTime;
import java.util.List;

public class ComplianceCheckResponse {
    
    private Long teaLotId;
    private String lotCode;
    private boolean shippable;
    private EvaluationSummary summary;
    private List<RuleResult> results;
    private LocalDateTime checkedAt;
    
    // デフォルトコンストラクタ
    public ComplianceCheckResponse() {}
    
    // GetterとSetter
    public Long getTeaLotId() { return teaLotId; }
    public void setTeaLotId(Long teaLotId) { this.teaLotId = teaLotId; }
    
    public String getLotCode() { return lotCode; }
    public void setLotCode(String lotCode) { this.lotCode = lotCode; }
    
    public boolean isShippable() { return shippable; }
    public void setShippable(boolean shippable) { this.shippable = shippable; }
    
    public EvaluationSummary getSummary() { return summary; }
    public void setSummary(EvaluationSummary summary) { this.summary = summary; }
    
    public List<RuleResult> getResults() { return results; }
    public void setResults(List<RuleResult> results) { this.results = results; }
    
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    
    public static class EvaluationSummary {
        private int totalRules;
        private int passedRules;
        private int failedRules;
        private int blockFailures;
        private int warnings;
        private int infos;
        
        // デフォルトコンストラクタ
        public EvaluationSummary() {}
        
        // GetterとSetter
        public int getTotalRules() { return totalRules; }
        public void setTotalRules(int totalRules) { this.totalRules = totalRules; }
        
        public int getPassedRules() { return passedRules; }
        public void setPassedRules(int passedRules) { this.passedRules = passedRules; }
        
        public int getFailedRules() { return failedRules; }
        public void setFailedRules(int failedRules) { this.failedRules = failedRules; }
        
        public int getBlockFailures() { return blockFailures; }
        public void setBlockFailures(int blockFailures) { this.blockFailures = blockFailures; }
        
        public int getWarnings() { return warnings; }
        public void setWarnings(int warnings) { this.warnings = warnings; }
        
        public int getInfos() { return infos; }
        public void setInfos(int infos) { this.infos = infos; }
    }
    
    public static class RuleResult {
        private String ruleCode;
        private String description;
        private ComplianceRule.RuleType ruleType;
        private ComplianceResult.EvaluationResult result;
        private ComplianceRule.Severity severity;
        private String message;
        private LocalDateTime evaluatedAt;
        
        // デフォルトコンストラクタ
        public RuleResult() {}
        
        // GetterとSetter
        public String getRuleCode() { return ruleCode; }
        public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public ComplianceRule.RuleType getRuleType() { return ruleType; }
        public void setRuleType(ComplianceRule.RuleType ruleType) { this.ruleType = ruleType; }
        
        public ComplianceResult.EvaluationResult getResult() { return result; }
        public void setResult(ComplianceResult.EvaluationResult result) { this.result = result; }
        
        public ComplianceRule.Severity getSeverity() { return severity; }
        public void setSeverity(ComplianceRule.Severity severity) { this.severity = severity; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
        public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
    }
}
