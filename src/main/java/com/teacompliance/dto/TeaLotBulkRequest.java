package com.teacompliance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 茶葉ロット一括登録リクエスト
 */
public class TeaLotBulkRequest {
    
    @NotEmpty(message = "茶葉ロットリストは必須です")
    @Size(min = 1, max = 100, message = "一度に登録できる茶葉ロットは1件以上100件以下です")
    @Valid
    private List<TeaLotRequest> teaLots;
    
    public List<TeaLotRequest> getTeaLots() {
        return teaLots;
    }
    
    public void setTeaLots(List<TeaLotRequest> teaLots) {
        this.teaLots = teaLots;
    }
}
