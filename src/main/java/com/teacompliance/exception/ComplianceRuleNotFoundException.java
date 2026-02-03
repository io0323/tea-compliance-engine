package com.teacompliance.exception;

/**
 * コンプライアンスルールが見つからない場合の例外
 */
public class ComplianceRuleNotFoundException extends TeaComplianceException {
    
    public ComplianceRuleNotFoundException(String ruleCode) {
        super("TC_003", "コンプライアンスルールが見つかりません: ルールコード=" + ruleCode);
    }
}
