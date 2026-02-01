package com.teacompliance.repository;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.domain.ComplianceRule;
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
    
    /**
     * 茶葉ロットIDで評価結果を検索
     * 
     * @param teaLotId 茶葉ロットID
     * @return 評価結果リスト
     */
    List<ComplianceResult> findByTeaLotId(Long teaLotId);
    
    /**
     * 茶葉ロットIDとルールコードで評価結果を検索
     * 
     * @param teaLotId 茶葉ロットID
     * @param ruleCode ルールコード
     * @return 評価結果
     */
    Optional<ComplianceResult> findByTeaLotIdAndRuleCode(Long teaLotId, String ruleCode);
    
    /**
     * 評価結果で検索
     * 
     * @param result 評価結果
     * @return 評価結果リスト
     */
    List<ComplianceResult> findByResult(ComplianceResult.EvaluationResult result);
    
    /**
     * 重要度レベルで検索
     * 
     * @param severity 重要度レベル
     * @return 評価結果リスト
     */
    List<ComplianceResult> findBySeverity(ComplianceRule.Severity severity);
    
    /**
     * 茶葉ロットIDでBLOCKレベルの評価結果を検索
     * 
     * @param teaLotId 茶葉ロットID
     * @return BLOCKレベルの評価結果リスト
     */
    @Query("SELECT cr FROM ComplianceResult cr WHERE cr.teaLotId = :teaLotId AND cr.severity = 'BLOCK' AND cr.result = 'FAIL'")
    List<ComplianceResult> findBlockFailuresByTeaLotId(@Param("teaLotId") Long teaLotId);
    
    /**
     * 茶葉ロットの出荷可否を判定
     * 
     * @param teaLotId 茶葉ロットID
     * @return true: 出荷可能, false: 出荷不可
     */
    @Query("SELECT CASE WHEN COUNT(cr) = 0 THEN true ELSE false END FROM ComplianceResult cr WHERE cr.teaLotId = :teaLotId AND cr.severity = 'BLOCK' AND cr.result = 'FAIL'")
    boolean isShippable(@Param("teaLotId") Long teaLotId);
}
