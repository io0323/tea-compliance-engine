package com.teacompliance.service;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.exception.DuplicateTeaLotException;
import com.teacompliance.repository.TeaLotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 茶葉ロットサービス（キャッシュ対応版）
 */
@Service
public class TeaLotServiceWithCache {
    
    private static final Logger log = LoggerFactory.getLogger(TeaLotServiceWithCache.class);
    
    private final TeaLotRepository teaLotRepository;
    
    public TeaLotServiceWithCache(TeaLotRepository teaLotRepository) {
        this.teaLotRepository = teaLotRepository;
    }
    
    @Transactional
    @CacheEvict(value = {"teaLots", "teaLotById", "teaLotByLotCode", "teaLotsByOrigin", "teaLotsByVariety"}, allEntries = true)
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
    
    @Cacheable(value = "teaLots", key = "'all'")
    public List<TeaLot> getAllTeaLots() {
        log.debug("全茶葉ロットをデータベースから取得します");
        return teaLotRepository.findAll();
    }
    
    @Cacheable(value = "teaLotById", key = "#id")
    public Optional<TeaLot> getTeaLotById(Long id) {
        log.debug("IDで茶葉ロットを取得します: {}", id);
        return teaLotRepository.findById(id);
    }
    
    @Cacheable(value = "teaLotByLotCode", key = "#lotCode")
    public Optional<TeaLot> getTeaLotByLotCode(String lotCode) {
        log.debug("ロットコードで茶葉ロットを取得します: {}", lotCode);
        return teaLotRepository.findByLotCode(lotCode);
    }
    
    @Cacheable(value = "teaLotsByOrigin", key = "#origin")
    public List<TeaLot> getTeaLotsByOrigin(String origin) {
        log.debug("産地で茶葉ロットを取得します: {}", origin);
        return teaLotRepository.findByOrigin(origin);
    }
    
    @Cacheable(value = "teaLotsByVariety", key = "#variety")
    public List<TeaLot> getTeaLotsByVariety(String variety) {
        log.debug("品種で茶葉ロットを取得します: {}", variety);
        return teaLotRepository.findByVariety(variety);
    }
    
    @Transactional
    @CacheEvict(value = {"teaLots", "teaLotById", "teaLotByLotCode", "teaLotsByOrigin", "teaLotsByVariety"}, allEntries = true)
    public boolean deleteTeaLot(Long id) {
        if (teaLotRepository.existsById(id)) {
            teaLotRepository.deleteById(id);
            log.info("茶葉ロットを削除しました: ID={}", id);
            return true;
        }
        return false;
    }
}
