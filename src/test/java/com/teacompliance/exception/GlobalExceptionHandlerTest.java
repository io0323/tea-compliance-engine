package com.teacompliance.exception;

import com.teacompliance.dto.TeaLotRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("グローバル例外ハンドラーのテスト")
class GlobalExceptionHandlerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @DisplayName("TeaLotNotFoundExceptionが適切に処理されること")
    void testHandleTeaLotNotFoundException() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tea-lots/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("MethodArgumentNotValidExceptionが適切に処理されること")
    void testHandleMethodArgumentNotValidException() throws Exception {
        // Given
        TeaLotRequest invalidRequest = new TeaLotRequest();
        invalidRequest.setLotCode(""); // 空文字列
        invalidRequest.setOrigin(""); // 空文字列
        invalidRequest.setVariety(""); // 空文字列
        // 他の必須項目はnullのまま
        
        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lotCode\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("存在しない茶葉ロットIDでコンプライアンスチェックした場合に404が返されること")
    void testHandleComplianceCheckNotFound() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/compliance/check/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("存在しない茶葉ロットIDで評価結果取得した場合に空配列が返されること")
    void testHandleComplianceResultsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/compliance/results/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    @DisplayName("存在しないロットコードで検索した場合に404が返されること")
    void testHandleTeaLotByCodeNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-lot-code/TL-9999-999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
