package com.teacompliance.repository;

import com.teacompliance.domain.ComplianceRule;
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
    
    /**
     * ルールコードで検索
     * 
     * @param ruleCode ルールコード
     * @return コンプライアンスルール
     */
    Optional<ComplianceRule> findByRuleCode(String ruleCode);
    
    /**
     * ルールタイプで検索
     * 
     * @param ruleType ルールタイプ
     * @return コンプライアンスルールリスト
     */
    List<ComplianceRule> findByRuleType(ComplianceRule.RuleType ruleType);
    
    /**
     * 重要度レベルで検索
     * 
     * @param severity 重要度レベル
     * @return コンプライアンスルールリスト
     */
    List<ComplianceRule> findBySeverity(ComplianceRule.Severity severity);
    
    /**
     * 有効な全ルールを取得（有効フラグがある場合）
     * 
     * @return コンプライアンスルールリスト
     */
    @Query("SELECT r FROM ComplianceRule r ORDER BY r.severity DESC, r.ruleType ASC")
    List<ComplianceRule> findAllOrderedBySeverityAndType();
    
    /**
     * 指定された重要度以上のルールを取得
     * 
     * @param severity 重要度レベル
     * @return コンプライアンスルールリスト
     */
    @Query("SELECT r FROM ComplianceRule r WHERE r.severity >= :severity ORDER BY r.severity DESC")
    List<ComplianceRule> findBySeverityGreaterThanEqual(@Param("severity") ComplianceRule.Severity severity);
}
