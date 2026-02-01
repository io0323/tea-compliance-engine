package com.teacompliance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;

public class TeaLotRequest {
    
    @NotBlank(message = "ロットコードは必須です")
    private String lotCode;
    
    @NotBlank(message = "産地は必須です")
    private String origin;
    
    @NotBlank(message = "品種は必須です")
    private String variety;
    
    @NotNull(message = "水分量は必須です")
    @Min(value = 0, message = "水分量は0以上である必要があります")
    @Max(value = 100, message = "水分量は100以下である必要があります")
    private Double moisture;
    
    @NotNull(message = "農薬レベルは必須です")
    @Min(value = 0, message = "農薬レベルは0以上である必要があります")
    private Double pesticideLevel;
    
    @NotNull(message = "香りスコアは必須です")
    @Min(value = 0, message = "香りスコアは0以上である必要があります")
    @Max(value = 100, message = "香りスコアは100以下である必要があります")
    private Integer aromaScore;
    
    @NotNull(message = "生産日は必須です")
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
