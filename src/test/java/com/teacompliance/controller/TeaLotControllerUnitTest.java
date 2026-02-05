package com.teacompliance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.exception.DuplicateTeaLotException;
import com.teacompliance.exception.TeaLotNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TeaLotController.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.cache.type=none",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "logging.level.org.springframework.web=ERROR"
})
@DisplayName("TeaLotController単体テスト")
class TeaLotControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeaLotService teaLotService;

    @Autowired
    private ObjectMapper objectMapper;

    private TeaLotRequest validRequest;
    private TeaLot existingTeaLot;

    @BeforeEach
    void setUp() {
        validRequest = new TeaLotRequest();
        validRequest.setLotCode("TL-2024-001");
        validRequest.setOrigin("鹿児島");
        validRequest.setVariety("一番茶");
        validRequest.setMoisture(4.5);
        validRequest.setPesticideLevel(0.8);
        validRequest.setAromaScore(8);
        validRequest.setProducedAt(LocalDate.of(2024, 5, 15));

        existingTeaLot = new TeaLot();
        existingTeaLot.setId(1L);
        existingTeaLot.setLotCode("TL-2024-001");
        existingTeaLot.setOrigin("鹿児島");
        existingTeaLot.setVariety("一番茶");
        existingTeaLot.setMoisture(4.5);
        existingTeaLot.setPesticideLevel(0.8);
        existingTeaLot.setAromaScore(8);
        existingTeaLot.setProducedAt(LocalDate.of(2024, 5, 15));
    }

    @Test
    @DisplayName("茶葉ロット登録 - 正常系")
    void registerTeaLot_Success() throws Exception {
        // Given
        when(teaLotService.registerTeaLot(any(TeaLotRequest.class))).thenReturn(existingTeaLot);

        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$.origin").value("鹿児島"))
                .andExpect(jsonPath("$.variety").value("一番茶"))
                .andExpect(jsonPath("$.moisture").value(4.5))
                .andExpect(jsonPath("$.pesticideLevel").value(0.8))
                .andExpect(jsonPath("$.aromaScore").value(8));
    }

    @Test
    @DisplayName("茶葉ロット登録 - 重複エラー")
    void registerTeaLot_DuplicateLotCode() throws Exception {
        // Given
        when(teaLotService.registerTeaLot(any(TeaLotRequest.class)))
            .thenThrow(new DuplicateTeaLotException("TL-2024-001"));

        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("TC_004"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("茶葉ロット登録 - バリデーションエラー（ロットコード必須）")
    void registerTeaLot_ValidationError_LotCodeRequired() throws Exception {
        // Given
        validRequest.setLotCode(null);

        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("TC_003"))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @DisplayName("茶葉ロット登録 - バリデーションエラー（水分率範囲外）")
    void registerTeaLot_ValidationError_MoistureOutOfRange() throws Exception {
        // Given
        validRequest.setMoisture(15.0); // 上限超過（10%超過）

        // When & Then
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("TC_003"))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @DisplayName("全茶葉ロット取得 - 正常系")
    void getAllTeaLots_Success() throws Exception {
        // Given
        List<TeaLot> teaLots = Arrays.asList(existingTeaLot);
        when(teaLotService.getAllTeaLots()).thenReturn(teaLots);

        // When & Then
        mockMvc.perform(get("/api/tea-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].lotCode").value("TL-2024-001"));
    }

    @Test
    @DisplayName("IDで茶葉ロット取得 - 存在する場合")
    void getTeaLotById_Exists() throws Exception {
        // Given
        when(teaLotService.getTeaLotById(1L)).thenReturn(Optional.of(existingTeaLot));

        // When & Then
        mockMvc.perform(get("/api/tea-lots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"));
    }

    @Test
    @DisplayName("IDで茶葉ロット取得 - 存在しない場合")
    void getTeaLotById_NotExists() throws Exception {
        // Given
        when(teaLotService.getTeaLotById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tea-lots/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TC_001"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("ロットコードで茶葉ロット取得 - 存在する場合")
    void getTeaLotByLotCode_Exists() throws Exception {
        // Given
        when(teaLotService.getTeaLotByLotCode("TL-2024-001")).thenReturn(Optional.of(existingTeaLot));

        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-lot-code/TL-2024-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"));
    }

    @Test
    @DisplayName("ロットコードで茶葉ロット取得 - 存在しない場合")
    void getTeaLotByLotCode_NotExists() throws Exception {
        // Given
        when(teaLotService.getTeaLotByLotCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-lot-code/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TC_001"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("産地で茶葉ロット取得 - 正常系")
    void getTeaLotsByOrigin_Success() throws Exception {
        // Given
        List<TeaLot> teaLots = Arrays.asList(existingTeaLot);
        when(teaLotService.getTeaLotsByOrigin("鹿児島")).thenReturn(teaLots);

        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-origin/鹿児島"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].origin").value("鹿児島"));
    }

    @Test
    @DisplayName("品種で茶葉ロット取得 - 正常系")
    void getTeaLotsByVariety_Success() throws Exception {
        // Given
        List<TeaLot> teaLots = Arrays.asList(existingTeaLot);
        when(teaLotService.getTeaLotsByVariety("一番茶")).thenReturn(teaLots);

        // When & Then
        mockMvc.perform(get("/api/tea-lots/by-variety/一番茶"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].variety").value("一番茶"));
    }

    @Test
    @DisplayName("茶葉ロット削除 - 正常系")
    void deleteTeaLot_Success() throws Exception {
        // Given
        when(teaLotService.getTeaLotById(1L)).thenReturn(Optional.of(existingTeaLot));
        when(teaLotService.deleteTeaLot(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/tea-lots/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("茶葉ロット削除 - 存在しない場合")
    void deleteTeaLot_NotExists() throws Exception {
        // Given
        when(teaLotService.getTeaLotById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/tea-lots/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TC_001"))
                .andExpect(jsonPath("$.message").exists());
    }
}
