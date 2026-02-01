package com.teacompliance.service.evaluation;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import org.springframework.stereotype.Component;

/**
 * 残留農薬ルール評価戦略
 * 
 * 茶葉の残留農薬レベルに関するコンプライアンスを評価する
 */
@Component
public class PesticideEvaluationStrategy implements RuleEvaluationStrategy {
    
    @Override
    public EvaluationResult evaluate(TeaLot teaLot, ComplianceRule rule) {
        Double actualPesticide = teaLot.getPesticideLevel();
        Double threshold = rule.getThreshold();
        ComplianceRule.ComparisonOperator operator = rule.getOperator();
        
        boolean passed = compareValues(actualPesticide, threshold, operator);
        
        String message = String.format(
            "残留農薬チェック: 実測値 %.2f ppm %s 基準値 %.2f ppm - %s",
            actualPesticide,
            operator.getSymbol(),
            threshold,
            passed ? "合格" : "不合格"
        );
        
        return new EvaluationResult(passed, message, actualPesticide);
    }
    
    @Override
    public ComplianceRule.RuleType getSupportedRuleType() {
        return ComplianceRule.RuleType.PESTICIDE;
    }
    
    /**
     * 値を比較する
     * 
     * @param actual 実測値
     * @param threshold 基準値
     * @param operator 比較演算子
     * @return 比較結果
     */
    private boolean compareValues(Double actual, Double threshold, ComplianceRule.ComparisonOperator operator) {
        return switch (operator) {
            case GREATER_THAN -> actual > threshold;
            case LESS_THAN -> actual < threshold;
            case GREATER_THAN_OR_EQUAL -> actual >= threshold;
            case LESS_THAN_OR_EQUAL -> actual <= threshold;
        };
    }
}
