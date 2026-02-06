package com.teacompliance.controller;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.dto.TeaLotBulkRequest;
import com.teacompliance.dto.TeaLotBulkResponse;
import com.teacompliance.dto.TeaLotSearchCriteria;
import com.teacompliance.exception.TeaLotNotFoundException;
import com.teacompliance.service.TeaLotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tea-lots")
@Tag(name = "茶葉ロット管理", description = "茶葉ロットの登録・検索API")
public class TeaLotController {
    
    private static final Logger log = LoggerFactory.getLogger(TeaLotController.class);
    
    private final TeaLotService teaLotService;
    
    public TeaLotController(TeaLotService teaLotService) {
        this.teaLotService = teaLotService;
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "茶葉ロット登録", description = "新しい茶葉ロットを登録する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "登録成功", 
                    content = @Content(schema = @Schema(implementation = TeaLot.class))),
        @ApiResponse(responseCode = "400", description = "バリデーションエラー"),
        @ApiResponse(responseCode = "409", description = "ロットコード重複")
    })
    public ResponseEntity<TeaLot> registerTeaLot(
            @Parameter(description = "登録する茶葉ロット情報", required = true)
            @Valid @RequestBody TeaLotRequest request) {
        log.info("茶葉ロット登録リクエスト: {}", request.getLotCode());
        
        try {
            TeaLot teaLot = teaLotService.registerTeaLot(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(teaLot);
        } catch (IllegalArgumentException e) {
            log.warn("茶葉ロット登録失敗: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "全茶葉ロット取得", description = "登録されている全ての茶葉ロットを取得する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取得成功")
    })
    public ResponseEntity<List<TeaLot>> getAllTeaLots() {
        List<TeaLot> teaLots = teaLotService.getAllTeaLots();
        return ResponseEntity.ok(teaLots);
    }
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "IDで茶葉ロット取得", description = "指定されたIDの茶葉ロットを取得する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取得成功"),
        @ApiResponse(responseCode = "404", description = "茶葉ロットが存在しない")
    })
    public ResponseEntity<TeaLot> getTeaLotById(
            @Parameter(description = "茶葉ロットID", required = true)
            @PathVariable Long id) {
        return teaLotService.getTeaLotById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new TeaLotNotFoundException("ID: " + id));
    }
    
    @GetMapping(value = "/by-lot-code/{lotCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "ロットコードで茶葉ロット取得", description = "指定されたロットコードの茶葉ロットを取得する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取得成功"),
        @ApiResponse(responseCode = "404", description = "茶葉ロットが存在しない")
    })
    public ResponseEntity<TeaLot> getTeaLotByLotCode(
            @Parameter(description = "ロットコード", required = true)
            @PathVariable String lotCode) {
        return teaLotService.getTeaLotByLotCode(lotCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new TeaLotNotFoundException("ロットコード: " + lotCode));
    }
    
    @GetMapping(value = "/by-origin/{origin}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "産地で茶葉ロット検索", description = "指定された産地の茶葉ロットを検索する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "検索成功")
    })
    public ResponseEntity<List<TeaLot>> getTeaLotsByOrigin(
            @Parameter(description = "産地", required = true)
            @PathVariable String origin) {
        List<TeaLot> teaLots = teaLotService.getTeaLotsByOrigin(origin);
        return ResponseEntity.ok(teaLots);
    }
    
    @GetMapping(value = "/by-variety/{variety}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "品種で茶葉ロット検索", description = "指定された品種の茶葉ロットを検索する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "検索成功")
    })
    public ResponseEntity<List<TeaLot>> getTeaLotsByVariety(
            @Parameter(description = "品種", required = true)
            @PathVariable String variety) {
        List<TeaLot> teaLots = teaLotService.getTeaLotsByVariety(variety);
        return ResponseEntity.ok(teaLots);
    }
    
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "茶葉ロット削除", description = "指定されたIDの茶葉ロットを削除する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "削除成功"),
        @ApiResponse(responseCode = "404", description = "茶葉ロットが存在しない")
    })
    public ResponseEntity<Void> deleteTeaLot(
            @Parameter(description = "茶葉ロットID", required = true)
            @PathVariable Long id) {
        if (!teaLotService.getTeaLotById(id).isPresent()) {
            throw new TeaLotNotFoundException("ID: " + id);
        }
        
        teaLotService.deleteTeaLot(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping(value = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "茶葉ロット一括登録", description = "複数の茶葉ロットを一括で登録する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "登録成功"),
        @ApiResponse(responseCode = "400", description = "バリデーションエラー"),
        @ApiResponse(responseCode = "409", description = "重複エラー")
    })
    public ResponseEntity<TeaLotBulkResponse> registerTeaLotsBulk(
            @Parameter(description = "茶葉ロット一括登録リクエスト", required = true)
            @Valid @RequestBody TeaLotBulkRequest request) {
        
        log.info("茶葉ロット一括登録リクエスト: {}件", request.getTeaLots().size());
        
        TeaLotBulkResponse response = new TeaLotBulkResponse();
        response.setTotalCount(request.getTeaLots().size());
        
        List<TeaLotBulkResponse.TeaLotBulkResult> results = new java.util.ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (int i = 0; i < request.getTeaLots().size(); i++) {
            TeaLotRequest teaLotRequest = request.getTeaLots().get(i);
            TeaLotBulkResponse.TeaLotBulkResult result = new TeaLotBulkResponse.TeaLotBulkResult();
            result.setIndex(i);
            result.setLotCode(teaLotRequest.getLotCode());
            
            try {
                TeaLot savedTeaLot = teaLotService.registerTeaLot(teaLotRequest);
                result.setSuccess(true);
                result.setMessage("登録成功");
                result.setTeaLotId(savedTeaLot.getId());
                successCount++;
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("登録失敗: " + e.getMessage());
                failureCount++;
            }
            
            results.add(result);
        }
        
        response.setResults(results);
        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        
        log.info("茶葉ロット一括登録完了 - 成功: {}件, 失敗: {}件", successCount, failureCount);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "茶葉ロット高度検索", description = "複合条件で茶葉ロットを検索する")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "検索成功"),
        @ApiResponse(responseCode = "400", description = "バリデーションエラー")
    })
    public ResponseEntity<List<TeaLot>> searchTeaLots(
            @Parameter(description = "検索条件", required = true)
            @Valid @RequestBody TeaLotSearchCriteria criteria) {
        
        log.info("茶葉ロット高度検索: {}", criteria);
        
        List<TeaLot> results = teaLotService.searchByCriteria(criteria);
        
        log.info("茶葉ロット高度検索完了 - {}件", results.size());
        return ResponseEntity.ok(results);
    }
    
    @PostMapping(value = "/search/page", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "茶葉ロット高度検索（ページング）", description = "複合条件で茶葉ロットを検索する（ページング対応）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "検索成功"),
        @ApiResponse(responseCode = "400", description = "バリデーションエラー")
    })
    public ResponseEntity<Page<TeaLot>> searchTeaLotsPage(
            @Parameter(description = "検索条件", required = true)
            @Valid @RequestBody TeaLotSearchCriteria criteria,
            @Parameter(description = "ページ番号", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "ページサイズ", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("茶葉ロット高度検索（ページング）: {}, page: {}, size: {}", criteria, page, size);
        
        // ソート条件を構築
        Sort sort = Sort.by(
            "asc".equalsIgnoreCase(criteria.getSortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC,
            criteria.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TeaLot> results = teaLotService.searchByCriteria(criteria, pageable);
        
        log.info("茶葉ロット高度検索完了 - {}件（全{}件）", results.getNumberOfElements(), results.getTotalElements());
        return ResponseEntity.ok(results);
    }
}
