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
class TeaLotServiceTest {
    
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
        validRequest.setOrigin("静岡県");
        validRequest.setVariety("やぶきた");
        validRequest.setMoisture(8.5);
        validRequest.setPesticideLevel(0.3);
        validRequest.setAromaScore(75);
        validRequest.setProducedAt(LocalDate.of(2024, 5, 15));
        
        existingTeaLot = new TeaLot();
        existingTeaLot.setId(1L);
        existingTeaLot.setLotCode("TL-2024-001");
        existingTeaLot.setOrigin("静岡県");
        existingTeaLot.setVariety("やぶきた");
        existingTeaLot.setMoisture(8.5);
        existingTeaLot.setPesticideLevel(0.3);
        existingTeaLot.setAromaScore(75);
        existingTeaLot.setProducedAt(LocalDate.of(2024, 5, 15));
    }
    
    @Test
    @DisplayName("新しい茶葉ロットが正常に登録されること")
    void testRegisterTeaLot_Success() {
        // Given
        when(teaLotRepository.existsByLotCode("TL-2024-001")).thenReturn(false);
        when(teaLotRepository.save(any(TeaLot.class))).thenReturn(existingTeaLot);
        
        // When
        TeaLot result = teaLotService.registerTeaLot(validRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLotCode()).isEqualTo("TL-2024-001");
        assertThat(result.getOrigin()).isEqualTo("静岡県");
        assertThat(result.getVariety()).isEqualTo("やぶきた");
        
        verify(teaLotRepository).existsByLotCode("TL-2024-001");
        verify(teaLotRepository).save(any(TeaLot.class));
    }
    
    @Test
    @DisplayName("重複するロットコードの場合に例外がスローされること")
    void testRegisterTeaLot_DuplicateLotCode() {
        // Given
        when(teaLotRepository.existsByLotCode("TL-2024-001")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> teaLotService.registerTeaLot(validRequest))
            .isInstanceOf(DuplicateTeaLotException.class)
            .hasMessageContaining("TL-2024-001");
        
        verify(teaLotRepository).existsByLotCode("TL-2024-001");
        verify(teaLotRepository, never()).save(any(TeaLot.class));
    }
    
    @Test
    @DisplayName("全茶葉ロットが正常に取得されること")
    void testGetAllTeaLots() {
        // Given
        List<TeaLot> expectedLots = Arrays.asList(existingTeaLot);
        when(teaLotRepository.findAll()).thenReturn(expectedLots);
        
        // When
        List<TeaLot> result = teaLotService.getAllTeaLots();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLotCode()).isEqualTo("TL-2024-001");
        verify(teaLotRepository).findAll();
    }
    
    @Test
    @DisplayName("IDで茶葉ロットが正常に取得されること")
    void testGetTeaLotById_Success() {
        // Given
        when(teaLotRepository.findById(1L)).thenReturn(Optional.of(existingTeaLot));
        
        // When
        Optional<TeaLot> result = teaLotService.getTeaLotById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getLotCode()).isEqualTo("TL-2024-001");
        verify(teaLotRepository).findById(1L);
    }
    
    @Test
    @DisplayName("存在しないIDで検索した場合に空のOptionalが返されること")
    void testGetTeaLotById_NotFound() {
        // Given
        when(teaLotRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<TeaLot> result = teaLotService.getTeaLotById(999L);
        
        // Then
        assertThat(result).isEmpty();
        verify(teaLotRepository).findById(999L);
    }
    
    @Test
    @DisplayName("ロットコードで茶葉ロットが正常に取得されること")
    void testGetTeaLotByLotCode_Success() {
        // Given
        when(teaLotRepository.findByLotCode("TL-2024-001")).thenReturn(Optional.of(existingTeaLot));
        
        // When
        Optional<TeaLot> result = teaLotService.getTeaLotByLotCode("TL-2024-001");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getLotCode()).isEqualTo("TL-2024-001");
        verify(teaLotRepository).findByLotCode("TL-2024-001");
    }
    
    @Test
    @DisplayName("存在しないロットコードで検索した場合に空のOptionalが返されること")
    void testGetTeaLotByLotCode_NotFound() {
        // Given
        when(teaLotRepository.findByLotCode("TL-9999-999")).thenReturn(Optional.empty());
        
        // When
        Optional<TeaLot> result = teaLotService.getTeaLotByLotCode("TL-9999-999");
        
        // Then
        assertThat(result).isEmpty();
        verify(teaLotRepository).findByLotCode("TL-9999-999");
    }
    
    @Test
    @DisplayName("産地で茶葉ロットが正常に検索されること")
    void testGetTeaLotsByOrigin() {
        // Given
        List<TeaLot> expectedLots = Arrays.asList(existingTeaLot);
        when(teaLotRepository.findByOrigin("静岡県")).thenReturn(expectedLots);
        
        // When
        List<TeaLot> result = teaLotService.getTeaLotsByOrigin("静岡県");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrigin()).isEqualTo("静岡県");
        verify(teaLotRepository).findByOrigin("静岡県");
    }
    
    @Test
    @DisplayName("品種で茶葉ロットが正常に検索されること")
    void testGetTeaLotsByVariety() {
        // Given
        List<TeaLot> expectedLots = Arrays.asList(existingTeaLot);
        when(teaLotRepository.findByVariety("やぶきた")).thenReturn(expectedLots);
        
        // When
        List<TeaLot> result = teaLotService.getTeaLotsByVariety("やぶきた");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVariety()).isEqualTo("やぶきた");
        verify(teaLotRepository).findByVariety("やぶきた");
    }
    
    @Test
    @DisplayName("存在しない産地で検索した場合に空リストが返されること")
    void testGetTeaLotsByOrigin_Empty() {
        // Given
        when(teaLotRepository.findByOrigin("存在しない産地")).thenReturn(Arrays.asList());
        
        // When
        List<TeaLot> result = teaLotService.getTeaLotsByOrigin("存在しない産地");
        
        // Then
        assertThat(result).isEmpty();
        verify(teaLotRepository).findByOrigin("存在しない産地");
    }
}
