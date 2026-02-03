package com.teacompliance.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * グローバル例外ハンドラー
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(TeaLotNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTeaLotNotFoundException(
            TeaLotNotFoundException ex, WebRequest request) {
        
        log.warn("茶葉ロットが見つかりません: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getErrorCode(),
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    
    @ExceptionHandler(DuplicateTeaLotException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateTeaLotException(
            DuplicateTeaLotException ex, WebRequest request) {
        
        log.warn("茶葉ロット重複エラー: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.CONFLICT,
            ex.getErrorCode(),
            "茶葉ロットが既に存在します: " + ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    
    @ExceptionHandler(ComplianceRuleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleComplianceRuleNotFoundException(
            ComplianceRuleNotFoundException ex, WebRequest request) {
        
        log.warn("コンプライアンスルールが見つかりません: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getErrorCode(),
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    
    @ExceptionHandler(ComplianceEvaluationException.class)
    public ResponseEntity<Map<String, Object>> handleComplianceEvaluationException(
            ComplianceEvaluationException ex, WebRequest request) {
        
        log.error("コンプライアンス評価エラー: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getErrorCode(),
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        log.warn("不正なJSON形式: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "TC_001",
            "Invalid JSON: " + ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaTypeError(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {
        
        log.warn("サポートされていないContent-Type: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "TC_002",
            "Content-Type not supported: " + ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("バリデーションエラー: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "TC_003",
            "Validation failed: " + ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("不正な引数エラー: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "TC_005",
            "不正なリクエスト: " + ex.getMessage(),
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("予期せぬエラーが発生しました: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "TC_999",
            "予期せぬエラーが発生しました",
            request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    
    private Map<String, Object> createErrorResponse(
            HttpStatus status, String errorCode, String message, String path) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("errorCode", errorCode);
        body.put("message", message);
        body.put("path", path);
        
        return body;
    }
}
