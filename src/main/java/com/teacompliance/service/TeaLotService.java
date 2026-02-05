package com.teacompliance.service;

import com.teacompliance.domain.TeaLot;
import com.teacompliance.dto.TeaLotRequest;
import com.teacompliance.exception.DuplicateTeaLotException;
import com.teacompliance.repository.TeaLotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        // 重複チェックを改善
        if (teaLotRepository.existsByLotCode(request.getLotCode())) {
            log.warn("茶葉ロットが既に存在します: {}", request.getLotCode());
            throw new DuplicateTeaLotException(request.getLotCode());
        }
        
        TeaLot teaLot = new TeaLot();
        teaLot.setLotCode(request.getLotCode());
        teaLot.setOrigin(request.getOrigin());
        teaLot.setVariety(request.getVariety());
        teaLot.setMoisture(request.getMoisture());
        teaLot.setPesticideLevel(request.getPesticideLevel());
        teaLot.setAromaScore(request.getAromaScore());
        teaLot.setProducedAt(request.getProducedAt());
        
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
}
