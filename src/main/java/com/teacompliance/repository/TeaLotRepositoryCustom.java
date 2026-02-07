package com.teacompliance.repository;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 茶葉ロットリポジトリのカスタムメソッド
 */
public interface TeaLotRepositoryCustom {
    
    /**
     * 複合条件で茶葉ロットを検索
     * 
     * @param criteria 検索条件
     * @param pageable ページング情報
     * @return 検索結果
     */
    Page<TeaLot> searchByCriteria(TeaLotSearchCriteria criteria, Pageable pageable);
    
    /**
     * 複合条件で茶葉ロットを検索（ページングなし）
     * 
     * @param criteria 検索条件
     * @return 検索結果
     */
    List<TeaLot> searchByCriteria(TeaLotSearchCriteria criteria);
}
