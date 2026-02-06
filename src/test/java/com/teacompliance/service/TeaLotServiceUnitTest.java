package com.teacompliance.service;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.exception.DuplicateTeaLotException;
import com.teacompliance.exception.TeaLotNotFoundException;
import com.teacompliance.repository.TeaLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeaLotService単体テスト")
class TeaLotServiceUnitTest {

    @Mock
    private TeaLotRepository teaLotRepository;

    @InjectMocks
    private TeaLotService teaLotService;

    private TeaLotRequest validRequest;
    private TeaLot existingTeaLot;

    @BeforeEach
    void setUp() {
        validRequest = new TeaLotRequest();
        validRequest.setLotCode("TL-2024-001");
        validRequest.setOrigin("鹿児島");
        validRequest.setVariety("一番茶");
        validRequest.setMoisture(4.5);
        validRequest.setPesticideLevel(0.8);
        validRequest.setAromaScore(8);
        validRequest.setProducedAt(LocalDate.of(2024, 5, 15));

        existingTeaLot = new TeaLot();
        existingTeaLot.setId(1L);
        existingTeaLot.setLotCode("TL-2024-001");
        existingTeaLot.setOrigin("鹿児島");
        existingTeaLot.setVariety("一番茶");
        existingTeaLot.setMoisture(4.5);
        existingTeaLot.setPesticideLevel(0.8);
        existingTeaLot.setAromaScore(8);
        existingTeaLot.setProducedAt(LocalDate.of(2024, 5, 15));
    }

    @Test
    @DisplayName("茶葉ロット登録 - 正常系")
    void registerTeaLot_Success() {
        // Given
        when(teaLotRepository.existsByLotCode(validRequest.getLotCode())).thenReturn(false);
        when(teaLotRepository.save(any(TeaLot.class))).thenReturn(existingTeaLot);

        // When
        TeaLot result = teaLotService.registerTeaLot(validRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLotCode()).isEqualTo(validRequest.getLotCode());
        assertThat(result.getOrigin()).isEqualTo(validRequest.getOrigin());
        verify(teaLotRepository, times(1)).existsByLotCode(validRequest.getLotCode());
        verify(teaLotRepository, times(1)).save(any(TeaLot.class));
    }

    @Test
    @DisplayName("茶葉ロット登録 - 重複エラー")
    void registerTeaLot_DuplicateLotCode() {
        // Given
        when(teaLotRepository.existsByLotCode(validRequest.getLotCode())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> teaLotService.registerTeaLot(validRequest))
            .isInstanceOf(DuplicateTeaLotException.class)
            .hasMessageContaining(validRequest.getLotCode());
        
        verify(teaLotRepository, times(1)).existsByLotCode(validRequest.getLotCode());
        verify(teaLotRepository, never()).save(any(TeaLot.class));
    }

    @Test
    @DisplayName("全茶葉ロット取得 - 正常系")
    void getAllTeaLots_Success() {
        // Given
        List<TeaLot> teaLots = Arrays.asList(existingTeaLot);
        when(teaLotRepository.findAll()).thenReturn(teaLots);

        // When
        List<TeaLot> result = teaLotService.getAllTeaLots();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLotCode()).isEqualTo(existingTeaLot.getLotCode());
        verify(teaLotRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("IDで茶葉ロット取得 - 存在する場合")
    void getTeaLotById_Exists() {
        // Given
        when(teaLotRepository.findById(1L)).thenReturn(Optional.of(existingTeaLot));

        // When
        Optional<TeaLot> result = teaLotService.getTeaLotById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(teaLotRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("IDで茶葉ロット取得 - 存在しない場合")
    void getTeaLotById_NotExists() {
        // Given
        when(teaLotRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<TeaLot> result = teaLotService.getTeaLotById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(teaLotRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("ロットコードで茶葉ロット取得 - 存在する場合")
    void getTeaLotByLotCode_Exists() {
        // Given
        when(teaLotRepository.findByLotCode("TL-2024-001")).thenReturn(Optional.of(existingTeaLot));

        // When
        Optional<TeaLot> result = teaLotService.getTeaLotByLotCode("TL-2024-001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getLotCode()).isEqualTo("TL-2024-001");
        verify(teaLotRepository, times(1)).findByLotCode("TL-2024-001");
    }

    @Test
    @DisplayName("ロットコードで茶葉ロット取得 - 存在しない場合")
    void getTeaLotByLotCode_NotExists() {
        // Given
        when(teaLotRepository.findByLotCode("INVALID")).thenReturn(Optional.empty());

        // When
        Optional<TeaLot> result = teaLotService.getTeaLotByLotCode("INVALID");

        // Then
        assertThat(result).isEmpty();
        verify(teaLotRepository, times(1)).findByLotCode("INVALID");
    }

    @Test
    @DisplayName("産地で茶葉ロット取得 - 正常系")
    void getTeaLotsByOrigin_Success() {
        // Given
        List<TeaLot> teaLots = Arrays.asList(existingTeaLot);
        when(teaLotRepository.findByOrigin("鹿児島")).thenReturn(teaLots);

        // When
        List<TeaLot> result = teaLotService.getTeaLotsByOrigin("鹿児島");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrigin()).isEqualTo("鹿児島");
        verify(teaLotRepository, times(1)).findByOrigin("鹿児島");
    }

    @Test
    @DisplayName("品種で茶葉ロット取得 - 正常系")
    void getTeaLotsByVariety_Success() {
        // Given
        List<TeaLot> teaLots = Arrays.asList(existingTeaLot);
        when(teaLotRepository.findByVariety("一番茶")).thenReturn(teaLots);

        // When
        List<TeaLot> result = teaLotService.getTeaLotsByVariety("一番茶");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVariety()).isEqualTo("一番茶");
        verify(teaLotRepository, times(1)).findByVariety("一番茶");
    }
}
