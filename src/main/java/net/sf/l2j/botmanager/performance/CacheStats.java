package net.sf.l2j.botmanager.performance;

/**
 * Статистика кэша.
 * 
 * Содержит метрики производительности кэша:
 * размер, количество попаданий/промахов, коэффициент попаданий, количество вытеснений.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class CacheStats {
    
    private final int size;
    private final long hits;
    private final long misses;
    private final double hitRate;
    private final long evictions;
    
    /**
     * Конструктор.
     * 
     * @param size размер кэша
     * @param hits количество попаданий
     * @param misses количество промахов
     * @param hitRate коэффициент попаданий (0.0 - 1.0)
     * @param evictions количество вытеснений
     */
    public CacheStats(int size, long hits, long misses, double hitRate, long evictions) {
        this.size = size;
        this.hits = hits;
        this.misses = misses;
        this.hitRate = hitRate;
        this.evictions = evictions;
    }
    
    /**
     * Получить размер кэша.
     * 
     * @return размер кэша
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Получить количество попаданий.
     * 
     * @return количество попаданий
     */
    public long getHits() {
        return hits;
    }
    
    /**
     * Получить количество промахов.
     * 
     * @return количество промахов
     */
    public long getMisses() {
        return misses;
    }
    
    /**
     * Получить коэффициент попаданий.
     * 
     * @return коэффициент попаданий (0.0 - 1.0)
     */
    public double getHitRate() {
        return hitRate;
    }
    
    /**
     * Получить коэффициент попаданий в процентах.
     * 
     * @return коэффициент попаданий в процентах
     */
    public double getHitRatePercent() {
        return hitRate * 100.0;
    }
    
    /**
     * Получить количество вытеснений.
     * 
     * @return количество вытеснений
     */
    public long getEvictions() {
        return evictions;
    }
    
    /**
     * Получить общее количество запросов.
     * 
     * @return общее количество запросов
     */
    public long getTotalRequests() {
        return hits + misses;
    }
    
    /**
     * Получить краткую статистику.
     * 
     * @return краткая статистика
     */
    public String getShortStats() {
        return String.format("Cache: %d entries, %.1f%% hit rate (%d/%d), %d evictions",
            size, getHitRatePercent(), hits, getTotalRequests(), evictions);
    }
    
    /**
     * Получить детальную статистику.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        return String.format(
            "Cache Statistics:\n" +
            "  Size: %d entries\n" +
            "  Hits: %d\n" +
            "  Misses: %d\n" +
            "  Total Requests: %d\n" +
            "  Hit Rate: %.2f%%\n" +
            "  Evictions: %d",
            size, hits, misses, getTotalRequests(), getHitRatePercent(), evictions
        );
    }
    
    @Override
    public String toString() {
        return getShortStats();
    }
}
