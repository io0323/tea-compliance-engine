package com.teacompliance.service;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.ComplianceCheckResponse;
import com.teacompliance.repository.ComplianceResultRepository;
import com.teacompliance.repository.ComplianceRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComplianceService {
    
    private static final Logger log = LoggerFactory.getLogger(ComplianceService.class);
    
    private final ComplianceEvaluationEngine evaluationEngine;
    private final TeaLotService teaLotService;
    private final ComplianceResultRepository resultRepository;
    private final ComplianceRuleRepository ruleRepository;
    
    public ComplianceService(ComplianceEvaluationEngine evaluationEngine,
                           TeaLotService teaLotService,
                           ComplianceResultRepository resultRepository,
                           ComplianceRuleRepository ruleRepository) {
        this.evaluationEngine = evaluationEngine;
        this.teaLotService = teaLotService;
        this.resultRepository = resultRepository;
        this.ruleRepository = ruleRepository;
    }
    
    public ComplianceCheckResponse checkCompliance(Long teaLotId) {
        TeaLot teaLot = teaLotService.getTeaLotById(teaLotId)
            .orElseThrow(() -> new IllegalArgumentException("茶葉ロットが見つかりません: " + teaLotId));
        
        List<ComplianceResult> results = evaluationEngine.evaluateTeaLot(teaLot);
        
        ComplianceCheckResponse response = new ComplianceCheckResponse();
        response.setTeaLotId(teaLotId);
        response.setLotCode(teaLot.getLotCode());
        response.setShippable(evaluationEngine.isShippable(teaLotId));
        response.setResults(convertToRuleResults(results));
        response.setSummary(createEvaluationSummary(results));
        response.setCheckedAt(java.time.LocalDateTime.now());
        
        return response;
    }
    
    public List<ComplianceResult> getComplianceResults(Long teaLotId) {
        return resultRepository.findByTeaLotId(teaLotId);
    }
    
    private List<ComplianceCheckResponse.RuleResult> convertToRuleResults(List<ComplianceResult> results) {
        Map<String, com.teacompliance.domain.ComplianceRule> ruleMap = ruleRepository.findAll().stream()
            .collect(Collectors.toMap(com.teacompliance.domain.ComplianceRule::getRuleCode, rule -> rule));
        
        return results.stream().map(result -> {
            ComplianceCheckResponse.RuleResult ruleResult = new ComplianceCheckResponse.RuleResult();
            ruleResult.setRuleCode(result.getRuleCode());
            
            com.teacompliance.domain.ComplianceRule rule = ruleMap.get(result.getRuleCode());
            if (rule != null) {
                ruleResult.setDescription(rule.getDescription());
                ruleResult.setRuleType(rule.getRuleType());
            }
            
            ruleResult.setResult(result.getResult());
            ruleResult.setSeverity(result.getSeverity());
            ruleResult.setMessage(result.getMessage());
            ruleResult.setEvaluatedAt(result.getEvaluatedAt());
            
            return ruleResult;
        }).collect(Collectors.toList());
    }
    
    private ComplianceCheckResponse.EvaluationSummary createEvaluationSummary(List<ComplianceResult> results) {
        ComplianceCheckResponse.EvaluationSummary summary = new ComplianceCheckResponse.EvaluationSummary();
        
        summary.setTotalRules(results.size());
        summary.setPassedRules((int) results.stream()
            .filter(r -> r.getResult() == ComplianceResult.EvaluationResult.PASS).count());
        summary.setFailedRules((int) results.stream()
            .filter(r -> r.getResult() == ComplianceResult.EvaluationResult.FAIL).count());
        summary.setBlockFailures((int) results.stream()
            .filter(r -> r.getSeverity() == com.teacompliance.domain.ComplianceRule.Severity.BLOCK 
                && r.getResult() == ComplianceResult.EvaluationResult.FAIL).count());
        summary.setWarnings((int) results.stream()
            .filter(r -> r.getSeverity() == com.teacompliance.domain.ComplianceRule.Severity.WARNING).count());
        summary.setInfos((int) results.stream()
            .filter(r -> r.getSeverity() == com.teacompliance.domain.ComplianceRule.Severity.INFO).count());
        
        return summary;
    }
}
