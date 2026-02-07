package com.teacompliance.repository;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotSearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("TeaLotRepository searchByCriteriaのテスト")
class TeaLotRepositorySearchTest {

    @Autowired
    private TeaLotRepository teaLotRepository;

    @Test
    @DisplayName("複合条件検索（ページング）でtotalElementsが全件取得にならず、ソートも適用されること")
    void searchByCriteriaPaged_UsesCountQueryAndAppliesSort() {
        // Given
        teaLotRepository.save(new TeaLot(null, "TL-2024-001", "静岡県", "一番茶", 4.5, 0.8, 80, LocalDate.of(2024, 5, 10)));
        teaLotRepository.save(new TeaLot(null, "TL-2024-002", "静岡県", "一番茶", 4.2, 0.7, 75, LocalDate.of(2024, 5, 12)));
        teaLotRepository.save(new TeaLot(null, "TL-2024-003", "静岡県", "一番茶", 4.1, 0.6, 70, LocalDate.of(2024, 5, 14)));

        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setOrigin("静岡県");
        criteria.setVariety("一番茶");
        criteria.setSortBy("producedAt");
        criteria.setSortDirection("desc");

        PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "producedAt"));

        // When
        Page<TeaLot> page = teaLotRepository.searchByCriteria(criteria, pageable);

        // Then
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getNumberOfElements());
        assertEquals("TL-2024-003", page.getContent().get(0).getLotCode());
        assertEquals("TL-2024-002", page.getContent().get(1).getLotCode());
    }

    @Test
    @DisplayName("ロットコード部分一致検索が機能すること")
    void searchByCriteriaLotCodeLike_Works() {
        // Given
        teaLotRepository.save(new TeaLot(null, "TL-2024-010", "静岡県", "一番茶", 4.5, 0.8, 80, LocalDate.of(2024, 5, 10)));
        teaLotRepository.save(new TeaLot(null, "TL-2023-999", "静岡県", "一番茶", 4.5, 0.8, 80, LocalDate.of(2023, 5, 10)));

        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setLotCode("2024");

        // When
        List<TeaLot> results = teaLotRepository.searchByCriteria(criteria);

        // Then
        assertEquals(1, results.size());
        assertEquals("TL-2024-010", results.get(0).getLotCode());
    }

    @Test
    @DisplayName("不正なsortByでも例外にならずデフォルトソートにフォールバックすること")
    void searchByCriteriaInvalidSortBy_FallsBackToDefault() {
        // Given
        teaLotRepository.save(new TeaLot(null, "TL-2024-020", "静岡県", "一番茶", 4.5, 0.8, 80, LocalDate.of(2024, 5, 10)));
        teaLotRepository.save(new TeaLot(null, "TL-2024-021", "静岡県", "一番茶", 4.5, 0.8, 80, LocalDate.of(2024, 5, 12)));

        TeaLotSearchCriteria criteria = new TeaLotSearchCriteria();
        criteria.setOrigin("静岡県");
        criteria.setVariety("一番茶");
        criteria.setSortBy("__invalid__");
        criteria.setSortDirection("desc");

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<TeaLot> page = teaLotRepository.searchByCriteria(criteria, pageable);

        // Then
        assertEquals(2, page.getTotalElements());
        assertEquals("TL-2024-021", page.getContent().get(0).getLotCode());
        assertEquals("TL-2024-020", page.getContent().get(1).getLotCode());
    }
}
