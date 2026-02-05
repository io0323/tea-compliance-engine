package com.teacompliance.controller;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
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
}
