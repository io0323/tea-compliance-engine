package com.teacompliance.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TeaLotTest {
    
    private TeaLot teaLot;
    
    @BeforeEach
    void setUp() {
        teaLot = new TeaLot();
        teaLot.setId(1L);
        teaLot.setLotCode("TL-2024-001");
        teaLot.setOrigin("静岡県");
        teaLot.setVariety("やぶきた");
        teaLot.setMoisture(8.5);
        teaLot.setPesticideLevel(0.3);
        teaLot.setAromaScore(75);
        teaLot.setProducedAt(LocalDate.of(2024, 5, 15));
    }
    
    @Test
    @DisplayName("茶葉ロットの基本情報が正しく設定されること")
    void testTeaLotBasicInfo() {
        assertThat(teaLot.getId()).isEqualTo(1L);
        assertThat(teaLot.getLotCode()).isEqualTo("TL-2024-001");
        assertThat(teaLot.getOrigin()).isEqualTo("静岡県");
        assertThat(teaLot.getVariety()).isEqualTo("やぶきた");
        assertThat(teaLot.getMoisture()).isEqualTo(8.5);
        assertThat(teaLot.getPesticideLevel()).isEqualTo(0.3);
        assertThat(teaLot.getAromaScore()).isEqualTo(75);
        assertThat(teaLot.getProducedAt()).isEqualTo(LocalDate.of(2024, 5, 15));
    }
    
    @Test
    @DisplayName("全項目コンストラクタが正しく動作すること")
    void testAllArgsConstructor() {
        LocalDate producedDate = LocalDate.of(2024, 6, 1);
        TeaLot newTeaLot = new TeaLot(
            2L, "TL-2024-002", "鹿児島県", "ゆたかみどり",
            9.2, 0.6, 68, producedDate
        );
        
        assertThat(newTeaLot.getId()).isEqualTo(2L);
        assertThat(newTeaLot.getLotCode()).isEqualTo("TL-2024-002");
        assertThat(newTeaLot.getOrigin()).isEqualTo("鹿児島県");
        assertThat(newTeaLot.getVariety()).isEqualTo("ゆたかみどり");
        assertThat(newTeaLot.getMoisture()).isEqualTo(9.2);
        assertThat(newTeaLot.getPesticideLevel()).isEqualTo(0.6);
        assertThat(newTeaLot.getAromaScore()).isEqualTo(68);
        assertThat(newTeaLot.getProducedAt()).isEqualTo(producedDate);
    }
    
    @Test
    @DisplayName("デフォルトコンストラクタでインスタンスが生成されること")
    void testDefaultConstructor() {
        TeaLot emptyTeaLot = new TeaLot();
        
        assertThat(emptyTeaLot).isNotNull();
        assertThat(emptyTeaLot.getId()).isNull();
        assertThat(emptyTeaLot.getLotCode()).isNull();
        assertThat(emptyTeaLot.getOrigin()).isNull();
        assertThat(emptyTeaLot.getVariety()).isNull();
        assertThat(emptyTeaLot.getMoisture()).isNull();
        assertThat(emptyTeaLot.getPesticideLevel()).isNull();
        assertThat(emptyTeaLot.getAromaScore()).isNull();
        assertThat(emptyTeaLot.getProducedAt()).isNull();
    }
    
    @Test
    @DisplayName("セッターで値が正しく更新されること")
    void testSetters() {
        teaLot.setLotCode("TL-2024-999");
        teaLot.setOrigin("京都府");
        teaLot.setVariety("宇治在来");
        teaLot.setMoisture(7.8);
        teaLot.setPesticideLevel(0.2);
        teaLot.setAromaScore(82);
        teaLot.setProducedAt(LocalDate.of(2024, 7, 1));
        
        assertThat(teaLot.getLotCode()).isEqualTo("TL-2024-999");
        assertThat(teaLot.getOrigin()).isEqualTo("京都府");
        assertThat(teaLot.getVariety()).isEqualTo("宇治在来");
        assertThat(teaLot.getMoisture()).isEqualTo(7.8);
        assertThat(teaLot.getPesticideLevel()).isEqualTo(0.2);
        assertThat(teaLot.getAromaScore()).isEqualTo(82);
        assertThat(teaLot.getProducedAt()).isEqualTo(LocalDate.of(2024, 7, 1));
    }
    
    @Test
    @DisplayName("equalsとhashCodeが正しく動作すること")
    void testEqualsAndHashCode() {
        TeaLot anotherTeaLot = new TeaLot();
        anotherTeaLot.setId(1L);
        anotherTeaLot.setLotCode("TL-2024-001");
        anotherTeaLot.setOrigin("静岡県");
        anotherTeaLot.setVariety("やぶきた");
        anotherTeaLot.setMoisture(8.5);
        anotherTeaLot.setPesticideLevel(0.3);
        anotherTeaLot.setAromaScore(75);
        anotherTeaLot.setProducedAt(LocalDate.of(2024, 5, 15));
        
        assertThat(teaLot).isEqualTo(anotherTeaLot);
        assertThat(teaLot.hashCode()).isEqualTo(anotherTeaLot.hashCode());
    }
    
    @Test
    @DisplayName("toStringが正しく動作すること")
    void testToString() {
        String teaLotString = teaLot.toString();
        
        assertThat(teaLotString).contains("TL-2024-001");
        assertThat(teaLotString).contains("静岡県");
        assertThat(teaLotString).contains("やぶきた");
    }
}
