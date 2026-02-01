package com.teacompliance.service.evaluation;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import org.springframework.stereotype.Component;

/**
 * 香りスコアルール評価戦略
 * 
 * 茶葉の香りスコアに関するコンプライアンスを評価する
 */
@Component
public class AromaEvaluationStrategy implements RuleEvaluationStrategy {
    
    @Override
    public EvaluationResult evaluate(TeaLot teaLot, ComplianceRule rule) {
        Integer actualAroma = teaLot.getAromaScore();
        Double threshold = rule.getThreshold();
        ComplianceRule.ComparisonOperator operator = rule.getOperator();
        
        boolean passed = compareValues(actualAroma.doubleValue(), threshold, operator);
        
        String message = String.format(
            "香りスコアチェック: 実測値 %d点 %s 基準値 %.0f点 - %s",
            actualAroma,
            operator.getSymbol(),
            threshold,
            passed ? "合格" : "不合格"
        );
        
        return new EvaluationResult(passed, message, actualAroma.doubleValue());
    }
    
    @Override
    public ComplianceRule.RuleType getSupportedRuleType() {
        return ComplianceRule.RuleType.AROMA;
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
