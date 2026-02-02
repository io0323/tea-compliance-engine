package com.teacompliance.exception;

/**
 * TeaComplianceアプリケーションの基底例外クラス
 */
public class TeaComplianceException extends RuntimeException {
    
    private final String errorCode;
    
    public TeaComplianceException(String message) {
        super(message);
        this.errorCode = "TC_ERROR";
    }
    
    public TeaComplianceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TC_ERROR";
    }
    
    public TeaComplianceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public TeaComplianceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
