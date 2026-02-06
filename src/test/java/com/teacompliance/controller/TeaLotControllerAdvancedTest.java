package com.teacompliance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotBulkRequest;
import com.teacompliance.dto.TeaLotBulkResponse;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.dto.TeaLotSearchCriteria;
import com.teacompliance.service.TeaLotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TeaLotControllerの高度な機能テスト
 */
@AutoConfigureWebMvc
@DisplayName("TeaLotController高度な機能テスト")
class TeaLotControllerAdvancedTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TeaLotService teaLotService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TeaLotRequest validRequest;
    private TeaLot testTeaLot;
    
    @BeforeEach
    void setUp() {
        validRequest = new TeaLotRequest();
        validRequest.setLotCode("TL-2024-001");
        validRequest.setOrigin("静岡県");
        validRequest.setVariety("一番茶");
        validRequest.setMoisture(4.5);
        validRequest.setPesticideLevel(0.8);
        validRequest.setAromaScore(8);
        validRequest.setProducedAt(LocalDate.of(2024, 5, 15));
        
        testTeaLot = new TeaLot();
        testTeaLot.setId(1L);
        testTeaLot.setLotCode("TL-2024-001");
        testTeaLot.setOrigin("静岡県");
        testTeaLot.setVariety("一番茶");
        testTeaLot.setMoisture(4.5);
        testTeaLot.setPesticideLevel(0.8);
        testTeaLot.setAromaScore(8);
        testTeaLot.setProducedAt(LocalDate.of(2024, 5, 15));
    }
    
    @Test
    @DisplayName("茶葉ロット一括登録が成功すること")
    void testBulkRegisterSuccess() throws Exception {
        // Given
        TeaLotBulkRequest bulkRequest = new TeaLotBulkRequest();
        bulkRequest.setTeaLots(Arrays.asList(validRequest));
        
        TeaLot savedTeaLot = new TeaLot();
        savedTeaLot.setId(1L);
        savedTeaLot.setLotCode("TL-2024-001");
        
        when(teaLotService.registerTeaLot(any(TeaLotRequest.class))).thenReturn(savedTeaLot);
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(0))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].success").value(true))
                .andExpect(jsonPath("$.results[0].lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$.results[0].teaLotId").value(1));
    }
    
    @Test
    @DisplayName("茶葉ロット一括登録で一部失敗した場合のレスポンスが正しいこと")
    void testBulkRegisterPartialFailure() throws Exception {
        // Given
        TeaLotBulkRequest bulkRequest = new TeaLotBulkRequest();
        bulkRequest.setTeaLots(Arrays.asList(validRequest));
        
        when(teaLotService.registerTeaLot(any(TeaLotRequest.class)))
                .thenThrow(new RuntimeException("登録失敗"));
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.successCount").value(0))
                .andExpect(jsonPath("$.failureCount").value(1))
                .andExpect(jsonPath("$.results[0].success").value(false))
                .andExpect(jsonPath("$.results[0].message").value("登録失敗: 登録失敗"));
    }
    
    @Test
    @DisplayName("複合条件検索が成功すること")
    void testAdvancedSearchSuccess() throws Exception {
        // Given
        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setOrigin("静岡県");
        criteria.setMinMoisture(3.0);
        criteria.setMaxMoisture(5.0);
        criteria.setMaxPesticideLevel(1.0);
        criteria.setSortBy("producedAt");
        criteria.setSortDirection("desc");
        
        List<TeaLot> searchResults = Arrays.asList(testTeaLot);
        when(teaLotService.searchByCriteria(any(TeaLotSearchCriteria.class)))
                .thenReturn(searchResults);
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$[0].origin").value("静岡県"));
    }
    
    @Test
    @DisplayName("ページング検索が成功すること")
    void testPagedSearchSuccess() throws Exception {
        // Given
        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setOrigin("静岡県");
        criteria.setVariety("一番茶");
        
        List<TeaLot> searchResults = Arrays.asList(testTeaLot);
        Page<TeaLot> pageResult = mock(Page.class);
        when(pageResult.getNumberOfElements()).thenReturn(1);
        when(pageResult.getTotalElements()).thenReturn(1L);
        
        when(teaLotService.searchByCriteria(any(TeaLotSearchCriteria.class), any()))
                .thenReturn(pageResult);
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/search/page")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria))
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @DisplayName("部分一致検索が成功すること")
    void testPartialMatchSearch() throws Exception {
        // Given
        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setLotCode("TL-2024"); // 部分一致
        
        List<TeaLot> searchResults = Arrays.asList(testTeaLot);
        when(teaLotService.searchByCriteria(any(TeaLotSearchCriteria.class)))
                .thenReturn(searchResults);
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].lotCode").value("TL-2024-001"));
        
        verify(teaLotService).searchByCriteria(argThat(c -> 
                "TL-2024".equals(c.getLotCode())));
    }
    
    @Test
    @DisplayName("日付範囲検索が成功すること")
    void testDateRangeSearch() throws Exception {
        // Given
        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setProducedAfter(LocalDate.of(2024, 1, 1));
        criteria.setProducedBefore(LocalDate.of(2024, 12, 31));
        
        List<TeaLot> searchResults = Arrays.asList(testTeaLot);
        when(teaLotService.searchByCriteria(any(TeaLotSearchCriteria.class)))
                .thenReturn(searchResults);
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].lotCode").value("TL-2024-001"));
        
        verify(teaLotService).searchByCriteria(argThat(c -> 
                LocalDate.of(2024, 1, 1).equals(c.getProducedAfter()) &&
                LocalDate.of(2024, 12, 31).equals(c.getProducedBefore())));
    }
    
    @Test
    @DisplayName("バリデーションエラーが適切に処理されること")
    void testValidationErrorHandling() throws Exception {
        // Given
        TeaLotBulkRequest bulkRequest = new TeaLotBulkRequest();
        TeaLotRequest invalidRequest = new TeaLotRequest();
        invalidRequest.setLotCode(""); // 空文字列
        bulkRequest.setTeaLots(Arrays.asList(invalidRequest));
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isBadRequest());
    }
}
