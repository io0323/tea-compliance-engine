package com.teacompliance.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 茶葉ロット一括処理レスポンス
 */
public class TeaLotBulkResponse {
    
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<TeaLotBulkResult> results;
    private LocalDateTime processedAt;
    
    public TeaLotBulkResponse() {
        this.processedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    
    public int getFailureCount() {
        return failureCount;
    }
    
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    
    public List<TeaLotBulkResult> getResults() {
        return results;
    }
    
    public void setResults(List<TeaLotBulkResult> results) {
        this.results = results;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    /**
     * 一括処理結果
     */
    public static class TeaLotBulkResult {
        private int index;
        private String lotCode;
        private boolean success;
        private String message;
        private Long teaLotId;
        
        // Getters and Setters
        public int getIndex() {
            return index;
        }
        
        public void setIndex(int index) {
            this.index = index;
        }
        
        public String getLotCode() {
            return lotCode;
        }
        
        public void setLotCode(String lotCode) {
            this.lotCode = lotCode;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Long getTeaLotId() {
            return teaLotId;
        }
        
        public void setTeaLotId(Long teaLotId) {
            this.teaLotId = teaLotId;
        }
    }
}
