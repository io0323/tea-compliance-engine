package com.teacompliance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * パフォーマンス監視ユーティリティ
 * 
 * メソッド実行時間やキャッシュヒット率を監視
 */
@Component
public class PerformanceMonitor {
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitor.class);
    
    // メソッド実行時間の記録
    private final ConcurrentHashMap<String, AtomicLong> executionCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> totalExecutionTime = new ConcurrentHashMap<>();
    
    // キャッシュ統計
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    
    /**
     * メソッド実行時間を計測
     * 
     * @param methodName メソッド名
     * @param operation 処理
     * @return 処理結果
     */
    public <T> T measureExecution(String methodName, ThrowingSupplier<T> operation) {
        LocalDateTime startTime = LocalDateTime.now();
        long startNano = System.nanoTime();
        
        try {
            T result = operation.get();
            
            long endNano = System.nanoTime();
            long executionTimeNano = endNano - startNano;
            double executionTimeMs = executionTimeNano / 1_000_000.0;
            
            // 統計を更新
            executionCounts.computeIfAbsent(methodName, k -> new AtomicLong(0)).incrementAndGet();
            totalExecutionTime.computeIfAbsent(methodName, k -> new AtomicLong(0)).addAndGet(executionTimeNano);
            
            // ログ出力（100msを超える場合）
            if (executionTimeMs > 100) {
                log.warn("パフォーマンス警告: {} が {:.2f}ms で実行されました", methodName, executionTimeMs);
            } else {
                log.debug("パフォーマンス計測: {} が {:.2f}ms で実行されました", methodName, executionTimeMs);
            }
            
            return result;
            
        } catch (Exception e) {
            long endNano = System.nanoTime();
            double executionTimeMs = (endNano - startNano) / 1_000_000.0;
            log.error("メソッド実行エラー: {} が {:.2f}ms で失敗 - {}", methodName, executionTimeMs, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * キャッシュヒットを記録
     */
    public void recordCacheHit() {
        cacheHits.incrementAndGet();
        log.debug("キャッシュヒット - ヒット数: {}, ミス数: {}", cacheHits.get(), cacheMisses.get());
    }
    
    /**
     * キャッシュミスを記録
     */
    public void recordCacheMiss() {
        cacheMisses.incrementAndGet();
        log.debug("キャッシュミス - ヒット数: {}, ミス数: {}", cacheHits.get(), cacheMisses.get());
    }
    
    /**
     * キャッシュヒット率を取得
     * 
     * @return ヒット率（0-100）
     */
    public double getCacheHitRate() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (double) hits / total * 100.0;
    }
    
    /**
     * メソッドの平均実行時間を取得
     * 
     * @param methodName メソッド名
     * @return 平均実行時間（ms）
     */
    public double getAverageExecutionTime(String methodName) {
        AtomicLong count = executionCounts.get(methodName);
        AtomicLong totalTime = totalExecutionTime.get(methodName);
        
        if (count == null || totalTime == null || count.get() == 0) {
            return 0.0;
        }
        
        return (double) totalTime.get() / count.get() / 1_000_000.0;
    }
    
    /**
     * パフォーマンス統計をログ出力
     */
    public void logPerformanceStats() {
        log.info("=== パフォーマンス統計 ===");
        log.info("キャッシュヒット率: {:.2f}% (ヒット: {}, ミス: {})", 
                getCacheHitRate(), cacheHits.get(), cacheMisses.get());
        
        log.info("メソッド実行統計:");
        executionCounts.forEach((method, count) -> {
            double avgTime = getAverageExecutionTime(method);
            log.info("  {}: {}回呼び出し, 平均実行時間: {:.2f}ms", method, count.get(), avgTime);
        });
        log.info("=== 統計終了 ===");
    }
    
    /**
     * 統計をリセット
     */
    public void reset() {
        executionCounts.clear();
        totalExecutionTime.clear();
        cacheHits.set(0);
        cacheMisses.set(0);
        log.info("パフォーマンス統計をリセットしました");
    }
    
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
