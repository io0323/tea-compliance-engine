package com.teacompliance.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

/**
 * 高度なキャッシュ設定
 * 
 * パフォーマンス最適化のためのキャッシュ戦略を定義
 */
@Configuration
@EnableCaching
public class AdvancedCacheConfig {
    
    @Bean
    @Primary
    public CacheManager advancedCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // キャッシュ名を定義
        cacheManager.setCacheNames(Arrays.asList(
            // 茶葉ロット関連
            "teaLots",
            "teaLotById", 
            "teaLotByLotCode",
            "teaLotsByOrigin",
            "teaLotsByVariety",
            "teaLotSearchResults",
            
            // コンプライアンス関連
            "complianceRules", 
            "complianceResults",
            "complianceEvaluation",
            "shippableStatus",
            "ruleEvaluationStrategies",
            
            // 検索関連
            "searchResults",
            "advancedSearchResults"
        ));
        
        // パフォーマンス最適化設定
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
