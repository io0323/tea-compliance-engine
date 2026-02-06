package com.teacompliance.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 茶葉ロットエンティティ
 * 
 * 茶葉の生産ロット情報を管理する
 */
@Entity
@Table(name = "tea_lots", indexes = {
    @Index(name = "idx_tea_lot_lot_code", columnList = "lot_code"),
    @Index(name = "idx_tea_lot_origin", columnList = "origin"),
    @Index(name = "idx_tea_lot_variety", columnList = "variety"),
    @Index(name = "idx_tea_lot_produced_at", columnList = "produced_at"),
    @Index(name = "idx_tea_lot_moisture", columnList = "moisture"),
    @Index(name = "idx_tea_lot_pesticide", columnList = "pesticide_level"),
    @Index(name = "idx_tea_lot_origin_variety", columnList = "origin, variety"),
    @Index(name = "idx_tea_lot_composite", columnList = "origin, variety, produced_at")
})
public class TeaLot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String lotCode;
    
    @Column(nullable = false)
    private String origin;
    
    @Column(nullable = false)
    private String variety;
    
    @Column(nullable = false)
    private Double moisture;
    
    @Column(nullable = false)
    private Double pesticideLevel;
    
    @Column(nullable = false)
    private Integer aromaScore;
    
    @Column(nullable = false)
    private LocalDate producedAt;
    
    // デフォルトコンストラクタ
    public TeaLot() {}
    
    // 全項目コンストラクタ
    public TeaLot(Long id, String lotCode, String origin, String variety, 
                  Double moisture, Double pesticideLevel, Integer aromaScore, LocalDate producedAt) {
        this.id = id;
        this.lotCode = lotCode;
        this.origin = origin;
        this.variety = variety;
        this.moisture = moisture;
        this.pesticideLevel = pesticideLevel;
        this.aromaScore = aromaScore;
        this.producedAt = producedAt;
    }
    
    // GetterとSetter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeaLot teaLot = (TeaLot) o;
        return lotCode != null ? lotCode.equals(teaLot.lotCode) : teaLot.lotCode == null;
    }
    
    @Override
    public int hashCode() {
        return lotCode != null ? lotCode.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "TeaLot{" +
                "id=" + id +
                ", lotCode='" + lotCode + '\'' +
                ", origin='" + origin + '\'' +
                ", variety='" + variety + '\'' +
                ", moisture=" + moisture +
                ", pesticideLevel=" + pesticideLevel +
                ", aromaScore=" + aromaScore +
                ", producedAt=" + producedAt +
                '}';
    }
}
