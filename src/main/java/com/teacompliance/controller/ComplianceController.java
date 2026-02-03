package com.teacompliance.controller;

import com.teacompliance.domain.ComplianceResult;
import com.teacompliance.dto.ComplianceCheckResponse;
import com.teacompliance.service.ComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
@Tag(name = "コンプライアンス評価", description = "茶葉ロットのコンプライアンスチェックと結果取得API")
public class ComplianceController {
    
    private static final Logger log = LoggerFactory.getLogger(ComplianceController.class);
    
    private final ComplianceService complianceService;
    
    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }
    
    @PostMapping(value = "/check/{teaLotId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "コンプライアンスチェック実行", description = "指定された茶葉ロットに対して全ルールを評価する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "評価成功", 
                    content = @Content(schema = @Schema(implementation = ComplianceCheckResponse.class))),
        @ApiResponse(responseCode = "404", description = "茶葉ロットが存在しない"),
        @ApiResponse(responseCode = "500", description = "評価中にエラーが発生")
    })
    public ResponseEntity<ComplianceCheckResponse> checkCompliance(
            @Parameter(description = "茶葉ロットID", required = true)
            @PathVariable Long teaLotId) {
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
    
    @GetMapping(value = "/results/{teaLotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "評価結果取得", description = "指定された茶葉ロットの評価結果一覧を取得する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取得成功"),
        @ApiResponse(responseCode = "500", description = "データ取得中にエラーが発生")
    })
    public ResponseEntity<List<ComplianceResult>> getComplianceResults(
            @Parameter(description = "茶葉ロットID", required = true)
            @PathVariable Long teaLotId) {
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
