package com.teacompliance.service.evaluation;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;

/**
 * ルール評価戦略インターフェース
 * 
 * Strategy パターンにより、各ルールタイプの評価ロジックをカプセル化する
 */
public interface RuleEvaluationStrategy {
    
    /**
     * ルールを評価する
     * 
     * @param teaLot 評価対象の茶葉ロット
     * @param rule 適用するルール
     * @return 評価結果
     */
    EvaluationResult evaluate(TeaLot teaLot, ComplianceRule rule);
    
    /**
     * 対応するルールタイプを取得
     * 
     * @return ルールタイプ
     */
    ComplianceRule.RuleType getSupportedRuleType();
    
    /**
     * 評価結果を保持するクラス
     */
    class EvaluationResult {
        private final boolean passed;
        private final String message;
        private final Double actualValue;
        
        public EvaluationResult(boolean passed, String message, Double actualValue) {
            this.passed = passed;
            this.message = message;
            this.actualValue = actualValue;
        }
        
        public boolean isPassed() {
            return passed;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Double getActualValue() {
            return actualValue;
        }
    }
}
