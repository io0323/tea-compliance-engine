package com.teacompliance.config;

import com.teacompliance.domain.ComplianceRule;
import com.teacompliance.domain.TeaLot;
import com.teacompliance.repository.ComplianceRuleRepository;
import com.teacompliance.repository.TeaLotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final TeaLotRepository teaLotRepository;
    private final ComplianceRuleRepository ruleRepository;
    
    public DataInitializer(TeaLotRepository teaLotRepository, ComplianceRuleRepository ruleRepository) {
        this.teaLotRepository = teaLotRepository;
        this.ruleRepository = ruleRepository;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (teaLotRepository.count() == 0) {
            initializeTeaLots();
        }
        
        if (ruleRepository.count() == 0) {
            initializeComplianceRules();
        }
        
        log.info("初期データ投入完了 - 茶葉ロット: {}件, ルール: {}件", 
            teaLotRepository.count(), ruleRepository.count());
    }
    
    private void initializeTeaLots() {
        log.info("茶葉ロット初期データ投入開始");
        
        TeaLot[] teaLots = {
            createTeaLot("TL-2024-001", "静岡県", "やぶきた", 8.5, 0.3, 75, LocalDate.of(2024, 5, 15)),
            createTeaLot("TL-2024-002", "鹿児島県", "ゆたかみどり", 9.2, 0.6, 68, LocalDate.of(2024, 5, 20)),
            createTeaLot("TL-2024-003", "京都府", "宇治在来", 7.8, 0.2, 82, LocalDate.of(2024, 5, 10)),
            createTeaLot("TL-2024-004", "三重県", "かおりわせ", 10.1, 0.4, 55, LocalDate.of(2024, 5, 25)),
            createTeaLot("TL-2024-005", "奈良県", "なつみどり", 8.9, 0.1, 71, LocalDate.of(2024, 5, 18))
        };
        
        for (TeaLot teaLot : teaLots) {
            teaLotRepository.save(teaLot);
            log.debug("茶葉ロット登録: {}", teaLot.getLotCode());
        }
        
        log.info("茶葉ロット初期データ投入完了: {}件", teaLots.length);
    }
    
    private TeaLot createTeaLot(String lotCode, String origin, String variety, 
                               Double moisture, Double pesticideLevel, Integer aromaScore, LocalDate producedAt) {
        TeaLot teaLot = new TeaLot();
        teaLot.setLotCode(lotCode);
        teaLot.setOrigin(origin);
        teaLot.setVariety(variety);
        teaLot.setMoisture(moisture);
        teaLot.setPesticideLevel(pesticideLevel);
        teaLot.setAromaScore(aromaScore);
        teaLot.setProducedAt(producedAt);
        return teaLot;
    }
    
    private void initializeComplianceRules() {
        log.info("コンプライアンスルール初期データ投入開始");
        
        ComplianceRule[] rules = {
            createComplianceRule("MOISTURE_001", "水分量基準（JAS規格）", 
                                ComplianceRule.RuleType.MOISTURE, 9.0, 
                                ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL, 
                                ComplianceRule.Severity.BLOCK),
                                
            createComplianceRule("PESTICIDE_001", "残留農薬基準（簡易モデル）", 
                                ComplianceRule.RuleType.PESTICIDE, 0.5, 
                                ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL, 
                                ComplianceRule.Severity.BLOCK),
                                
            createComplianceRule("AROMA_001", "香りスコア基準（社内品質ルール）", 
                                ComplianceRule.RuleType.AROMA, 60.0, 
                                ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL, 
                                ComplianceRule.Severity.WARNING),
                                
            createComplianceRule("MOISTURE_002", "水分量警告基準", 
                                ComplianceRule.RuleType.MOISTURE, 8.5, 
                                ComplianceRule.ComparisonOperator.LESS_THAN_OR_EQUAL, 
                                ComplianceRule.Severity.WARNING),
                                
            createComplianceRule("AROMA_002", "香りスコア優良基準", 
                                ComplianceRule.RuleType.AROMA, 80.0, 
                                ComplianceRule.ComparisonOperator.GREATER_THAN_OR_EQUAL, 
                                ComplianceRule.Severity.INFO)
        };
        
        for (ComplianceRule rule : rules) {
            ruleRepository.save(rule);
            log.debug("コンプライアンスルール登録: {} - {}", rule.getRuleCode(), rule.getDescription());
        }
        
        log.info("コンプライアンスルール初期データ投入完了: {}件", rules.length);
    }
    
    private ComplianceRule createComplianceRule(String ruleCode, String description, 
                                               ComplianceRule.RuleType ruleType, Double threshold,
                                               ComplianceRule.ComparisonOperator operator, 
                                               ComplianceRule.Severity severity) {
        ComplianceRule rule = new ComplianceRule();
        rule.setRuleCode(ruleCode);
        rule.setDescription(description);
        rule.setRuleType(ruleType);
        rule.setThreshold(threshold);
        rule.setOperator(operator);
        rule.setSeverity(severity);
        return rule;
    }
}
