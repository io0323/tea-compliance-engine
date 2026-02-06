package com.teacompliance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;

/**
 * 茶葉ロット検索条件
 */
public class TeaLotSearchCriteria {
    
    private String origin;
    private String variety;
    private String lotCode;
    
    @Min(value = 0, message = "水分量の最小値は0%以上です")
    @Max(value = 10, message = "水分量の最大値は10%以下です")
    private Double minMoisture;
    
    @Min(value = 0, message = "水分量の最小値は0%以上です")
    @Max(value = 10, message = "水分量の最大値は10%以下です")
    private Double maxMoisture;
    
    @Min(value = 0, message = "農薬レベルの最小値は0以上です")
    @Max(value = 5, message = "農薬レベルの最大値は5以下です")
    private Double maxPesticideLevel;
    
    private LocalDate producedAfter;
    private LocalDate producedBefore;
    
    private String sortBy = "producedAt";
    private String sortDirection = "desc";
    
    // Getters and Setters
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getVariety() {
        return variety;
    }
    
    public void setVariety(String variety) {
        this.variety = variety;
    }
    
    public String getLotCode() {
        return lotCode;
    }
    
    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }
    
    public Double getMinMoisture() {
        return minMoisture;
    }
    
    public void setMinMoisture(Double minMoisture) {
        this.minMoisture = minMoisture;
    }
    
    public Double getMaxMoisture() {
        return maxMoisture;
    }
    
    public void setMaxMoisture(Double maxMoisture) {
        this.maxMoisture = maxMoisture;
    }
    
    public Double getMaxPesticideLevel() {
        return maxPesticideLevel;
    }
    
    public void setMaxPesticideLevel(Double maxPesticideLevel) {
        this.maxPesticideLevel = maxPesticideLevel;
    }
    
    public LocalDate getProducedAfter() {
        return producedAfter;
    }
    
    public void setProducedAfter(LocalDate producedAfter) {
        this.producedAfter = producedAfter;
    }
    
    public LocalDate getProducedBefore() {
        return producedBefore;
    }
    
    public void setProducedBefore(LocalDate producedBefore) {
        this.producedBefore = producedBefore;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
