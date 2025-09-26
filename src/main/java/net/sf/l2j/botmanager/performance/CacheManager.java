package net.sf.l2j.botmanager.performance;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * Менеджер кэширования для оптимизации производительности.
 * 
 * Предоставляет кэширование часто используемых данных,
 * автоматическую очистку устаревших записей и статистику кэша.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class CacheManager {
    
    private static final Logger logger = Logger.getLogger(CacheManager.class);
    private static CacheManager instance;
    
    /** Кэш данных */
    private final Map<String, CacheEntry> cache;
    
    /** Планировщик для очистки кэша */
    private final ScheduledExecutorService cleanupScheduler;
    
    /** Статистика кэша */
    private final AtomicLong hits;
    private final AtomicLong misses;
    private final AtomicLong evictions;
    
    /** Настройки кэша */
    private final long defaultTtl;
    private final int maxSize;
    private final long cleanupInterval;
    
    /** Флаг активности */
    private volatile boolean active;
    
    /**
     * Конструктор.
     */
    private CacheManager() {
        this.cache = new ConcurrentHashMap<>();
        this.cleanupScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CacheCleanup");
            t.setDaemon(true);
            return t;
        });
        
        this.hits = new AtomicLong(0);
        this.misses = new AtomicLong(0);
        this.evictions = new AtomicLong(0);
        
        // Настройки по умолчанию
        this.defaultTtl = 300_000; // 5 минут
        this.maxSize = 1000;
        this.cleanupInterval = 60_000; // 1 минута
        
        this.active = true;
        
        // Запускаем очистку кэша
        startCleanupTask();
    }
    
    /**
     * Получить экземпляр менеджера кэша.
     * 
     * @return экземпляр менеджера кэша
     */
    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }
    
    /**
     * Получить значение из кэша.
     * 
     * @param key ключ
     * @return значение или null если не найдено
     */
    public Object get(String key) {
        if (!active || key == null || key.isEmpty()) {
            return null;
        }
        
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            misses.incrementAndGet();
            return null;
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            evictions.incrementAndGet();
            misses.incrementAndGet();
            return null;
        }
        
        hits.incrementAndGet();
        return entry.getValue();
    }
    
    /**
     * Получить значение из кэша с типизацией.
     * 
     * @param key ключ
     * @param type тип значения
     * @return значение или null если не найдено
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        Object value = get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Сохранить значение в кэш.
     * 
     * @param key ключ
     * @param value значение
     */
    public void put(String key, Object value) {
        if (key == null || key.isEmpty() || value == null) {
            return;
        }
        put(key, value, defaultTtl);
    }
    
    /**
     * Сохранить значение в кэш с TTL.
     * 
     * @param key ключ
     * @param value значение
     * @param ttl время жизни в миллисекундах
     */
    public void put(String key, Object value, long ttl) {
        if (!active || key == null || key.isEmpty() || value == null) {
            return;
        }
        
        // Проверяем размер кэша
        if (cache.size() >= maxSize) {
            evictOldest();
        }
        
        long expireTime = System.currentTimeMillis() + ttl;
        CacheEntry entry = new CacheEntry(value, expireTime);
        cache.put(key, entry);
        
        logger.debug("Cached value for key: " + key + " (TTL: " + ttl + "ms)");
    }
    
    /**
     * Удалить значение из кэша.
     * 
     * @param key ключ
     * @return удаленное значение или null
     */
    public Object remove(String key) {
        if (!active || key == null || key.isEmpty()) {
            return null;
        }
        
        CacheEntry entry = cache.remove(key);
        return entry != null ? entry.getValue() : null;
    }
    
    /**
     * Очистить весь кэш.
     */
    public void clear() {
        cache.clear();
        hits.set(0);
        misses.set(0);
        evictions.set(0);
        logger.info("Cache cleared");
    }
    
    /**
     * Проверить наличие ключа в кэше.
     * 
     * @param key ключ
     * @return true если ключ существует и не истек
     */
    public boolean containsKey(String key) {
        if (!active || key == null || key.isEmpty()) {
            return false;
        }
        
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            if (entry != null) {
                cache.remove(key);
                evictions.incrementAndGet();
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Получить размер кэша.
     * 
     * @return размер кэша
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Получить статистику кэша.
     * 
     * @return статистика кэша
     */
    public CacheStats getStats() {
        long hitCount = hits.get();
        long missCount = misses.get();
        long totalRequests = hitCount + missCount;
        double hitRate = totalRequests > 0 ? (double) hitCount / totalRequests : 0.0;
        
        return new CacheStats(
            cache.size(),
            hitCount,
            missCount,
            hitRate,
            evictions.get()
        );
    }
    
    /**
     * Получить детальную статистику кэша.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        CacheStats stats = getStats();
        return String.format(
            "Cache Stats: Size=%d, Hits=%d, Misses=%d, Hit Rate=%.2f%%, Evictions=%d",
            stats.getSize(),
            stats.getHits(),
            stats.getMisses(),
            stats.getHitRate() * 100,
            stats.getEvictions()
        );
    }
    
    /**
     * Остановить менеджер кэша.
     */
    public void shutdown() {
        active = false;
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Cache manager shutdown");
    }
    
    /**
     * Запустить задачу очистки кэша.
     */
    private void startCleanupTask() {
        cleanupScheduler.scheduleWithFixedDelay(
            this::cleanupExpiredEntries,
            cleanupInterval,
            cleanupInterval,
            TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * Очистить истекшие записи.
     */
    private void cleanupExpiredEntries() {
        if (!active) {
            return;
        }
        
        int cleaned = 0;
        long currentTime = System.currentTimeMillis();
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpired(currentTime)) {
                cache.remove(entry.getKey());
                cleaned++;
                evictions.incrementAndGet();
            }
        }
        
        if (cleaned > 0) {
            logger.debug("Cleaned " + cleaned + " expired cache entries");
        }
    }
    
    /**
     * Удалить самую старую запись.
     */
    private void evictOldest() {
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().getCreatedTime() < oldestTime) {
                oldestTime = entry.getValue().getCreatedTime();
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
            evictions.incrementAndGet();
        }
    }
    
    /**
     * Запись кэша.
     */
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;
        private final long createdTime;
        
        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
            this.createdTime = System.currentTimeMillis();
        }
        
        public Object getValue() {
            return value;
        }
        
        public long getCreatedTime() {
            return createdTime;
        }
        
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        public boolean isExpired(long currentTime) {
            return currentTime > expireTime;
        }
    }
}
