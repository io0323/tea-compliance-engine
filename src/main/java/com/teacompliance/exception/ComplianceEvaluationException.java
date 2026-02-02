package com.teacompliance.exception;

/**
 * コンプライアンス評価中にエラーが発生した場合の例外
 */
public class ComplianceEvaluationException extends TeaComplianceException {
    
    public ComplianceEvaluationException(String message) {
        super("TC_004", "コンプライアンス評価エラー: " + message);
    }
    
    public ComplianceEvaluationException(String message, Throwable cause) {
        super("TC_004", "コンプライアンス評価エラー: " + message, cause);
    }
}
