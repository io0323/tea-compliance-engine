package com.teacompliance.repository;

import com.teacompliance.domain.TeaLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 茶葉ロットリポジトリ
 */
@Repository
public interface TeaLotRepository extends JpaRepository<TeaLot, Long> {
    
    /**
     * ロットコードで検索
     * 
     * @param lotCode ロットコード
     * @return 茶葉ロット
     */
    Optional<TeaLot> findByLotCode(String lotCode);
    
    /**
     * 産地で検索
     * 
     * @param origin 産地
     * @return 茶葉ロットリスト
     */
    List<TeaLot> findByOrigin(String origin);
    
    /**
     * 品種で検索
     * 
     * @param variety 品種
     * @return 茶葉ロットリスト
     */
    List<TeaLot> findByVariety(String variety);
    
    /**
     * 指定された水分量より高いロットを検索
     * 
     * @param moisture 水分量
     * @return 茶葉ロットリスト
     */
    @Query("SELECT t FROM TeaLot t WHERE t.moisture > :moisture")
    List<TeaLot> findByMoistureGreaterThan(@Param("moisture") Double moisture);
    
    /**
     * 指定された農薬レベルより高いロットを検索
     * 
     * @param pesticideLevel 農薬レベル
     * @return 茶葉ロットリスト
     */
    @Query("SELECT t FROM TeaLot t WHERE t.pesticideLevel > :pesticideLevel")
    List<TeaLot> findByPesticideLevelGreaterThan(@Param("pesticideLevel") Double pesticideLevel);
}
