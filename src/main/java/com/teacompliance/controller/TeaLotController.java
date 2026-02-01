package com.teacompliance.controller;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.service.TeaLotService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tea-lots")
public class TeaLotController {
    
    private static final Logger log = LoggerFactory.getLogger(TeaLotController.class);
    
    private final TeaLotService teaLotService;
    
    public TeaLotController(TeaLotService teaLotService) {
        this.teaLotService = teaLotService;
    }
    
    @PostMapping
    public ResponseEntity<TeaLot> registerTeaLot(@Valid @RequestBody TeaLotRequest request) {
        log.info("茶葉ロット登録リクエスト: {}", request.getLotCode());
        
        try {
            TeaLot teaLot = teaLotService.registerTeaLot(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(teaLot);
        } catch (IllegalArgumentException e) {
            log.warn("茶葉ロット登録失敗: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<TeaLot>> getAllTeaLots() {
        List<TeaLot> teaLots = teaLotService.getAllTeaLots();
        return ResponseEntity.ok(teaLots);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TeaLot> getTeaLotById(@PathVariable Long id) {
        Optional<TeaLot> teaLot = teaLotService.getTeaLotById(id);
        return teaLot.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-code/{lotCode}")
    public ResponseEntity<TeaLot> getTeaLotByLotCode(@PathVariable String lotCode) {
        Optional<TeaLot> teaLot = teaLotService.getTeaLotByLotCode(lotCode);
        return teaLot.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-origin")
    public ResponseEntity<List<TeaLot>> getTeaLotsByOrigin(@RequestParam String origin) {
        List<TeaLot> teaLots = teaLotService.getTeaLotsByOrigin(origin);
        return ResponseEntity.ok(teaLots);
    }
    
    @GetMapping("/by-variety")
    public ResponseEntity<List<TeaLot>> getTeaLotsByVariety(@RequestParam String variety) {
        List<TeaLot> teaLots = teaLotService.getTeaLotsByVariety(variety);
        return ResponseEntity.ok(teaLots);
    }
}
