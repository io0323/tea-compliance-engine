package com.teacompliance.repository;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.ComplianceRule;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * コンプライアンス評価結果リポジトリ
 */
@Repository
public interface ComplianceResultRepository extends JpaRepository<ComplianceResult, Long> {
    
    @Cacheable("complianceResults")
    List<ComplianceResult> findByTeaLotId(Long teaLotId);
    
    @Cacheable("complianceResults")
    Optional<ComplianceResult> findByTeaLotIdAndRuleCode(Long teaLotId, String ruleCode);
    
    @Cacheable("complianceResults")
    List<ComplianceResult> findByResult(ComplianceResult.EvaluationResult result);
    
    @Cacheable("complianceResults")
    List<ComplianceResult> findBySeverity(ComplianceRule.Severity severity);
    
    @Cacheable("complianceResults")
    @Query("SELECT cr FROM ComplianceResult cr WHERE cr.teaLotId = :teaLotId AND cr.severity = 'BLOCK' AND cr.result = 'FAIL'")
    List<ComplianceResult> findBlockFailuresByTeaLotId(@Param("teaLotId") Long teaLotId);
    
    @Cacheable("shippableStatus")
    @Query("SELECT CASE WHEN COUNT(cr) = 0 THEN true ELSE false END FROM ComplianceResult cr WHERE cr.teaLotId = :teaLotId AND cr.severity = 'BLOCK' AND cr.result = 'FAIL'")
    boolean isShippable(@Param("teaLotId") Long teaLotId);
    
    @Override
    @CacheEvict(value = {"complianceResults", "shippableStatus"}, allEntries = true)
    <S extends ComplianceResult> S save(S entity);
    
    @Override
    @CacheEvict(value = {"complianceResults", "shippableStatus"}, allEntries = true)
    <S extends ComplianceResult> List<S> saveAll(Iterable<S> entities);
}
