package com.teacompliance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teacompliance.validation.ValidLotCode;
import com.teacompliance.validation.ValidPesticideLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TeaLotRequest {
    
    @NotBlank(message = "ロットコードは必須です")
    @ValidLotCode
    private String lotCode;
    
    @NotBlank(message = "産地は必須です")
    @Size(min = 1, max = 50, message = "産地は1文字以上50文字以下である必要があります")
    private String origin;
    
    @NotBlank(message = "品種は必須です")
    @Size(min = 1, max = 50, message = "品種は1文字以上50文字以下である必要があります")
    private String variety;
    
    @NotNull(message = "水分量は必須です")
    @DecimalMin(value = "0.0", message = "水分量は0%以上である必要があります")
    @DecimalMax(value = "100.0", message = "水分量は100%以下である必要があります")
    private Double moisture;
    
    @NotNull(message = "農薬レベルは必須です")
    @ValidPesticideLevel(max = 10.0)
    private Double pesticideLevel;
    
    @NotNull(message = "香りスコアは必須です")
    @Min(value = 0, message = "香りスコアは0点以上である必要があります")
    @Max(value = 100, message = "香りスコアは100点以下である必要があります")
    private Integer aromaScore;
    
    @NotNull(message = "生産日は必須です")
    @PastOrPresent(message = "生産日は今日以前である必要があります")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate producedAt;
    
    // デフォルトコンストラクタ
    public TeaLotRequest() {}
    
    // GetterとSetter
    public String getLotCode() { return lotCode; }
    public void setLotCode(String lotCode) { this.lotCode = lotCode; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    
    public Double getMoisture() { return moisture; }
    public void setMoisture(Double moisture) { this.moisture = moisture; }
    
    public Double getPesticideLevel() { return pesticideLevel; }
    public void setPesticideLevel(Double pesticideLevel) { this.pesticideLevel = pesticideLevel; }
    
    public Integer getAromaScore() { return aromaScore; }
    public void setAromaScore(Integer aromaScore) { this.aromaScore = aromaScore; }
    
    public LocalDate getProducedAt() { return producedAt; }
    public void setProducedAt(LocalDate producedAt) { this.producedAt = producedAt; }
}
