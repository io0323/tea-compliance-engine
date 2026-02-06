package com.teacompliance.service;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.dto.TeaLotSearchCriteria;
import com.teacompliance.exception.DuplicateTeaLotException;
import com.teacompliance.repository.TeaLotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 茶葉ロットサービス
 */
@Service
public class TeaLotService {
    
    private static final Logger log = LoggerFactory.getLogger(TeaLotService.class);
    
    private final TeaLotRepository teaLotRepository;
    
    public TeaLotService(TeaLotRepository teaLotRepository) {
        this.teaLotRepository = teaLotRepository;
    }
    
    @Transactional
    public TeaLot registerTeaLot(TeaLotRequest request) {
        TeaLot teaLot = new TeaLot();
        teaLot.setLotCode(request.getLotCode());
        teaLot.setOrigin(request.getOrigin());
        teaLot.setVariety(request.getVariety());
        teaLot.setMoisture(request.getMoisture());
        teaLot.setPesticideLevel(request.getPesticideLevel());
        teaLot.setAromaScore(request.getAromaScore());
        teaLot.setProducedAt(request.getProducedAt());
        
        // Check for duplicate after creating the entity
        if (teaLotRepository.existsByLotCode(request.getLotCode())) {
            throw new DuplicateTeaLotException(request.getLotCode());
        }
        
        TeaLot savedLot = teaLotRepository.save(teaLot);
        log.info("茶葉ロットを登録しました: {}", savedLot.getLotCode());
        
        return savedLot;
    }
    
    public List<TeaLot> getAllTeaLots() {
        return teaLotRepository.findAll();
    }
    
    public Optional<TeaLot> getTeaLotById(Long id) {
        return teaLotRepository.findById(id);
    }
    
    public Optional<TeaLot> getTeaLotByLotCode(String lotCode) {
        return teaLotRepository.findByLotCode(lotCode);
    }
    
    public List<TeaLot> getTeaLotsByOrigin(String origin) {
        return teaLotRepository.findByOrigin(origin);
    }
    
    public List<TeaLot> getTeaLotsByVariety(String variety) {
        return teaLotRepository.findByVariety(variety);
    }
    
    @Transactional
    public boolean deleteTeaLot(Long id) {
        if (teaLotRepository.existsById(id)) {
            teaLotRepository.deleteById(id);
            log.info("茶葉ロットを削除しました: ID={}", id);
            return true;
        }
        return false;
    }
    
    /**
     * 複合条件で茶葉ロットを検索
     * 
     * @param criteria 検索条件
     * @return 検索結果
     */
    public List<TeaLot> searchByCriteria(TeaLotSearchCriteria criteria) {
        log.info("複合条件検索: {}", criteria);
        List<TeaLot> results = teaLotRepository.searchByCriteria(criteria);
        log.info("複合条件検索完了 - {}件", results.size());
        return results;
    }
    
    /**
     * 複合条件で茶葉ロットを検索（ページング対応）
     * 
     * @param criteria 検索条件
     * @param pageable ページング情報
     * @return 検索結果
     */
    public Page<TeaLot> searchByCriteria(TeaLotSearchCriteria criteria, Pageable pageable) {
        log.info("複合条件検索（ページング）: {}, {}", criteria, pageable);
        Page<TeaLot> results = teaLotRepository.searchByCriteria(criteria, pageable);
        log.info("複合条件検索完了 - {}件（全{}件）", results.getNumberOfElements(), results.getTotalElements());
        return results;
    }
}
