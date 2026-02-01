package com.teacompliance.controller;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.dto.ComplianceCheckResponse;
import com.teacompliance.service.ComplianceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {
    
    private static final Logger log = LoggerFactory.getLogger(ComplianceController.class);
    
    private final ComplianceService complianceService;
    
    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }
    
    @PostMapping("/check/{teaLotId}")
    public ResponseEntity<ComplianceCheckResponse> checkCompliance(@PathVariable Long teaLotId) {
        log.info("コンプライアンスチェック開始: teaLotId={}", teaLotId);
        
        try {
            ComplianceCheckResponse response = complianceService.checkCompliance(teaLotId);
            log.info("コンプライアンスチェック完了: teaLotId={}, shippable={}", teaLotId, response.isShippable());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("コンプライアンスチェック失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("コンプライアンスチェック中にエラー発生: teaLotId={}", teaLotId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/results/{teaLotId}")
    public ResponseEntity<List<ComplianceResult>> getComplianceResults(@PathVariable Long teaLotId) {
        log.debug("評価結果取得: teaLotId={}", teaLotId);
        
        try {
            List<ComplianceResult> results = complianceService.getComplianceResults(teaLotId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("評価結果取得中にエラー発生: teaLotId={}", teaLotId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
