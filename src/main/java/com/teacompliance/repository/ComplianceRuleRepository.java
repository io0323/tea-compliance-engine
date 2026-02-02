package com.teacompliance.repository;

import com.teacompliance.domain.ComplianceRule;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * コンプライアンスルールリポジトリ
 */
@Repository
public interface ComplianceRuleRepository extends JpaRepository<ComplianceRule, Long> {
    
    @Cacheable("complianceRules")
    Optional<ComplianceRule> findByRuleCode(String ruleCode);
    
    @Cacheable("complianceRules")
    List<ComplianceRule> findByRuleType(ComplianceRule.RuleType ruleType);
    
    @Cacheable("complianceRules")
    List<ComplianceRule> findBySeverity(ComplianceRule.Severity severity);
    
    @Cacheable("complianceRules")
    @Query("SELECT r FROM ComplianceRule r ORDER BY r.severity DESC, r.ruleType ASC")
    List<ComplianceRule> findAllOrderedBySeverityAndType();
    
    @Cacheable("complianceRules")
    @Query("SELECT r FROM ComplianceRule r WHERE r.severity >= :severity ORDER BY r.severity DESC")
    List<ComplianceRule> findBySeverityGreaterThanEqual(@Param("severity") ComplianceRule.Severity severity);
}
