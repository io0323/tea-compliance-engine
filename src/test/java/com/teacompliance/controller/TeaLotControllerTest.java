package com.teacompliance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.exception.DuplicateTeaLotException;
import com.teacompliance.service.TeaLotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TeaLotController.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.cache.type=none",
    "spring.main.allow-bean-definition-overriding=true"
})
class TeaLotControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TeaLotService teaLotService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TeaLot validTeaLot;
    private TeaLotRequest validRequest;
    
    @BeforeEach
    void setUp() {
        validTeaLot = new TeaLot();
        validTeaLot.setId(1L);
        validTeaLot.setLotCode("TL-2024-001");
        validTeaLot.setOrigin("静岡県");
        validTeaLot.setVariety("やぶきた");
        validTeaLot.setMoisture(8.5);
        validTeaLot.setPesticideLevel(0.3);
        validTeaLot.setAromaScore(75);
        validTeaLot.setProducedAt(LocalDate.of(2024, 5, 15));
        
        validRequest = new TeaLotRequest();
        validRequest.setLotCode("TL-2024-001");
        validRequest.setOrigin("静岡県");
        validRequest.setVariety("やぶきた");
        validRequest.setMoisture(8.5);
        validRequest.setPesticideLevel(0.3);
        validRequest.setAromaScore(75);
        validRequest.setProducedAt(LocalDate.of(2024, 5, 15));
    }
    
    @Test
    @DisplayName("新しい茶葉ロットが正常に登録されること")
    void testRegisterTeaLot_Success() throws Exception {
        // Given
        when(teaLotService.registerTeaLot(any(TeaLotRequest.class))).thenReturn(validTeaLot);
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$.origin").value("静岡県"))
                .andExpect(jsonPath("$.variety").value("やぶきた"))
                .andExpect(jsonPath("$.moisture").value(8.5))
                .andExpect(jsonPath("$.pesticideLevel").value(0.3))
                .andExpect(jsonPath("$.aromaScore").value(75));
    }
    
    @Test
    @DisplayName("重複するロットコードの場合に409エラーが返されること")
    void testRegisterTeaLot_DuplicateLotCode() throws Exception {
        // Given
        when(teaLotService.registerTeaLot(any(TeaLotRequest.class)))
            .thenThrow(new DuplicateTeaLotException("TL-2024-001"));
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());
    }
    
    @Test
    @DisplayName("バリデーションエラーの場合に400エラーが返されること")
    void testRegisterTeaLot_ValidationError() throws Exception {
        // Given
        TeaLotRequest invalidRequest = new TeaLotRequest();
        invalidRequest.setLotCode(""); // 空文字列
        invalidRequest.setOrigin("");
        invalidRequest.setVariety("");
        // 他の必須項目もnullのまま
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("全茶葉ロットが正常に取得されること")
    void testGetAllTeaLots() throws Exception {
        // Given
        List<TeaLot> teaLots = Arrays.asList(validTeaLot);
        when(teaLotService.getAllTeaLots()).thenReturn(teaLots);
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].lotCode").value("TL-2024-001"));
    }
    
    @Test
    @DisplayName("IDで茶葉ロットが正常に取得されること")
    void testGetTeaLotById_Success() throws Exception {
        // Given
        when(teaLotService.getTeaLotById(1L)).thenReturn(Optional.of(validTeaLot));
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"));
    }
    
    @Test
    @DisplayName("存在しないIDで検索した場合に404エラーが返されること")
    void testGetTeaLotById_NotFound() throws Exception {
        // Given
        when(teaLotService.getTeaLotById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("ロットコードで茶葉ロットが正常に取得されること")
    void testGetTeaLotByLotCode_Success() throws Exception {
        // Given
        when(teaLotService.getTeaLotByLotCode("TL-2024-001")).thenReturn(Optional.of(validTeaLot));
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-code/TL-2024-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$.origin").value("静岡県"));
    }
    
    @Test
    @DisplayName("存在しないロットコードで検索した場合に404エラーが返されること")
    void testGetTeaLotByLotCode_NotFound() throws Exception {
        // Given
        when(teaLotService.getTeaLotByLotCode("TL-9999-999")).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-code/TL-9999-999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("産地で茶葉ロットが正常に検索されること")
    void testGetTeaLotsByOrigin() throws Exception {
        // Given
        List<TeaLot> teaLots = Arrays.asList(validTeaLot);
        when(teaLotService.getTeaLotsByOrigin("静岡県")).thenReturn(teaLots);
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-origin")
                .param("origin", "静岡県"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].origin").value("静岡県"));
    }
    
    @Test
    @DisplayName("品種で茶葉ロットが正常に検索されること")
    void testGetTeaLotsByVariety() throws Exception {
        // Given
        List<TeaLot> teaLots = Arrays.asList(validTeaLot);
        when(teaLotService.getTeaLotsByVariety("やぶきた")).thenReturn(teaLots);
        
        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-variety")
                .param("variety", "やぶきた"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].variety").value("やぶきた"));
    }
    
    @Test
    @DisplayName("リクエストJSONが不正な場合に400エラーが返されること")
    void testRegisterTeaLot_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Content-Typeが不正な場合に415エラーが返されること")
    void testRegisterTeaLot_InvalidContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.TEXT_PLAIN)
                .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }
    
    @Test
    @DisplayName("必須フィールドが欠落している場合に400エラーが返されること")
    void testRegisterTeaLot_MissingRequiredFields() throws Exception {
        // Given - 空のリクエスト
        TeaLotRequest emptyRequest = new TeaLotRequest();
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("ロットコードが空文字列の場合に400エラーが返されること")
    void testRegisterTeaLot_EmptyLotCode() throws Exception {
        // Given
        TeaLotRequest invalidRequest = new TeaLotRequest();
        invalidRequest.setLotCode(""); // 空文字列
        invalidRequest.setOrigin("静岡県");
        invalidRequest.setVariety("やぶきた");
        invalidRequest.setMoisture(8.5);
        invalidRequest.setPesticideLevel(0.3);
        invalidRequest.setAromaScore(75);
        invalidRequest.setProducedAt(LocalDate.of(2024, 5, 15));
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("水分量が範囲外の場合に400エラーが返されること")
    void testRegisterTeaLot_InvalidMoistureRange() throws Exception {
        // Given
        TeaLotRequest invalidRequest = new TeaLotRequest();
        invalidRequest.setLotCode("TL-2024-999");
        invalidRequest.setOrigin("静岡県");
        invalidRequest.setVariety("やぶきた");
        invalidRequest.setMoisture(150.0); // 範囲外
        invalidRequest.setPesticideLevel(0.3);
        invalidRequest.setAromaScore(75);
        invalidRequest.setProducedAt(LocalDate.of(2024, 5, 15));
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
