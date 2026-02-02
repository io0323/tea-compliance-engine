package com.teacompliance.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.repository.TeaLotRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class TeaComplianceIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private TeaLotRepository teaLotRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        teaLotRepository.deleteAll();
    }
    
    @Test
    @DisplayName("茶葉ロット登録からコンプライアンスチェックまでの一連の流れが正常に動作すること")
    void testCompleteWorkflow_Success() throws Exception {
        // 1. 茶葉ロット登録
        TeaLotRequest request = new TeaLotRequest();
        request.setLotCode("TL-2024-001");
        request.setOrigin("静岡県");
        request.setVariety("やぶきた");
        request.setMoisture(8.5);
        request.setPesticideLevel(0.3);
        request.setAromaScore(75);
        request.setProducedAt(LocalDate.of(2024, 5, 15));
        
        // 茶葉ロット登録リクエスト
        String response = mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$.origin").value("静岡県"))
                .andReturn().getResponse().getContentAsString();
        
        // 登録された茶葉ロットのIDを取得
        Long teaLotId = objectMapper.readTree(response).get("id").asLong();
        
        // 2. 全茶葉ロット取得確認
        mockMvc.perform(get("/api/tea-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].lotCode").value("TL-2024-001"));
        
        // 3. IDで茶葉ロット取得確認
        mockMvc.perform(get("/api/tea-lots/" + teaLotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teaLotId))
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"));
        
        // 4. ロットコードで茶葉ロット取得確認
        mockMvc.perform(get("/api/tea-lots/by-code/TL-2024-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"));
        
        // 5. 産地で検索確認
        mockMvc.perform(get("/api/tea-lots/by-origin")
                .param("origin", "静岡県"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].origin").value("静岡県"));
        
        // 6. 品種で検索確認
        mockMvc.perform(get("/api/tea-lots/by-variety")
                .param("variety", "やぶきた"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].variety").value("やぶきた"));
        
        // 7. コンプライアンスチェック実行
        mockMvc.perform(post("/api/compliance/check/" + teaLotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teaLotId").value(teaLotId))
                .andExpect(jsonPath("$.lotCode").value("TL-2024-001"))
                .andExpect(jsonPath("$.shippable").isBoolean())
                .andExpect(jsonPath("$.summary.totalRules").isNumber())
                .andExpect(jsonPath("$.results").isArray());
        
        // 8. 評価結果取得確認
        mockMvc.perform(get("/api/compliance/results/" + teaLotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    @DisplayName("複数の茶葉ロットを登録して検索機能を確認すること")
    void testMultipleTeaLots_SearchAndFilter() throws Exception {
        // 複数の茶葉ロットを登録
        TeaLotRequest request1 = createTeaLotRequest("TL-2024-001", "静岡県", "やぶきた", 8.5, 0.3, 75);
        TeaLotRequest request2 = createTeaLotRequest("TL-2024-002", "鹿児島県", "ゆたかみどり", 9.2, 0.6, 68);
        TeaLotRequest request3 = createTeaLotRequest("TL-2024-003", "静岡県", "かおりわせ", 7.8, 0.2, 82);
        
        // 茶葉ロット登録
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());
        
        // 全件取得確認（3件）
        mockMvc.perform(get("/api/tea-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
        
        // 産地で検索（静岡県：2件）
        mockMvc.perform(get("/api/tea-lots/by-origin")
                .param("origin", "静岡県"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        
        // 産地で検索（鹿児島県：1件）
        mockMvc.perform(get("/api/tea-lots/by-origin")
                .param("origin", "鹿児島県"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
        
        // 品種で検索（やぶきた：1件）
        mockMvc.perform(get("/api/tea-lots/by-variety")
                .param("variety", "やぶきた"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    @DisplayName("バリデーションエラーが適切に処理されること")
    void testValidationErrors() throws Exception {
        // 不正なリクエスト（必須項目缺失）
        TeaLotRequest invalidRequest = new TeaLotRequest();
        invalidRequest.setLotCode(""); // 空文字列
        invalidRequest.setOrigin(""); // 空文字列
        invalidRequest.setVariety(""); // 空文字列
        // 他の必須項目はnullのまま
        
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        // 不正なロットコード形式
        TeaLotRequest invalidLotCodeRequest = createTeaLotRequest("INVALID-CODE", "静岡県", "やぶきた", 8.5, 0.3, 75);
        
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLotCodeRequest)))
                .andExpect(status().isBadRequest());
        
        // 範囲外の水分量
        TeaLotRequest invalidMoistureRequest = createTeaLotRequest("TL-2024-999", "静岡県", "やぶきた", 150.0, 0.3, 75);
        
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMoistureRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("存在しないリソースへのアクセスが404エラーを返すこと")
    void testNotFoundErrors() throws Exception {
        // 存在しない茶葉ロットID
        mockMvc.perform(get("/api/tea-lots/999"))
                .andExpect(status().isNotFound());
        
        // 存在しないロットコード
        mockMvc.perform(get("/api/tea-lots/by-code/TL-9999-999"))
                .andExpect(status().isNotFound());
        
        // 存在しない茶葉ロットIDでコンプライアンスチェック
        mockMvc.perform(post("/api/compliance/check/999"))
                .andExpect(status().isNotFound());
        
        // 存在しない茶葉ロットIDで評価結果取得
        mockMvc.perform(get("/api/compliance/results/999"))
                .andExpect(status().isOk()) // 空リストを返す
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    @DisplayName("重複ロットコード登録が適切に処理されること")
    void testDuplicateLotCode() throws Exception {
        TeaLotRequest request = createTeaLotRequest("TL-2024-001", "静岡県", "やぶきた", 8.5, 0.3, 75);
        
        // 1回目の登録（成功）
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        
        // 2回目の登録（重複エラー）
        mockMvc.perform(post("/api/tea-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    
    private TeaLotRequest createTeaLotRequest(String lotCode, String origin, String variety, 
                                              Double moisture, Double pesticideLevel, Integer aromaScore) {
        TeaLotRequest request = new TeaLotRequest();
        request.setLotCode(lotCode);
        request.setOrigin(origin);
        request.setVariety(variety);
        request.setMoisture(moisture);
        request.setPesticideLevel(pesticideLevel);
        request.setAromaScore(aromaScore);
        request.setProducedAt(LocalDate.of(2024, 5, 15));
        return request;
    }
}
