package net.sf.l2j.botmanager.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для менеджера кэширования.
 */
public class CacheManagerTest {
    
    private CacheManager cacheManager;
    
    @BeforeEach
    public void setUp() {
        cacheManager = CacheManager.getInstance();
        cacheManager.clear();
    }
    
    @AfterEach
    public void tearDown() {
        cacheManager.clear();
    }
    
    @Test
    public void testBasicPutAndGet() {
        String key = "testKey";
        String value = "testValue";
        
        cacheManager.put(key, value);
        
        Object retrieved = cacheManager.get(key);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }
    
    @Test
    public void testTypedGet() {
        String key = "testKey";
        String value = "testValue";
        
        cacheManager.put(key, value);
        
        String retrieved = cacheManager.get(key, String.class);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
        
        // Тест с неправильным типом
        Integer wrongType = cacheManager.get(key, Integer.class);
        assertNull(wrongType);
    }
    
    @Test
    public void testPutWithTtl() {
        String key = "testKey";
        String value = "testValue";
        long ttl = 100; // 100ms
        
        cacheManager.put(key, value, ttl);
        
        // Значение должно быть доступно сразу
        Object retrieved = cacheManager.get(key);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
        
        // Ждем истечения TTL
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Значение должно истечь
        retrieved = cacheManager.get(key);
        assertNull(retrieved);
    }
    
    @Test
    public void testRemove() {
        String key = "testKey";
        String value = "testValue";
        
        cacheManager.put(key, value);
        
        // Проверяем, что значение есть
        Object retrieved = cacheManager.get(key);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
        
        // Удаляем
        Object removed = cacheManager.remove(key);
        assertNotNull(removed);
        assertEquals(value, removed);
        
        // Проверяем, что значение удалено
        retrieved = cacheManager.get(key);
        assertNull(retrieved);
    }
    
    @Test
    public void testContainsKey() {
        String key = "testKey";
        String value = "testValue";
        
        // Ключ не должен существовать
        assertFalse(cacheManager.containsKey(key));
        
        cacheManager.put(key, value);
        
        // Ключ должен существовать
        assertTrue(cacheManager.containsKey(key));
        
        // Удаляем
        cacheManager.remove(key);
        
        // Ключ не должен существовать
        assertFalse(cacheManager.containsKey(key));
    }
    
    @Test
    public void testClear() {
        // Добавляем несколько значений
        cacheManager.put("key1", "value1");
        cacheManager.put("key2", "value2");
        cacheManager.put("key3", "value3");
        
        // Проверяем, что значения есть
        assertEquals(3, cacheManager.size());
        assertNotNull(cacheManager.get("key1"));
        assertNotNull(cacheManager.get("key2"));
        assertNotNull(cacheManager.get("key3"));
        
        // Очищаем
        cacheManager.clear();
        
        // Проверяем, что кэш пуст
        assertEquals(0, cacheManager.size());
        assertNull(cacheManager.get("key1"));
        assertNull(cacheManager.get("key2"));
        assertNull(cacheManager.get("key3"));
    }
    
    @Test
    public void testSize() {
        assertEquals(0, cacheManager.size());
        
        cacheManager.put("key1", "value1");
        assertEquals(1, cacheManager.size());
        
        cacheManager.put("key2", "value2");
        assertEquals(2, cacheManager.size());
        
        cacheManager.remove("key1");
        assertEquals(1, cacheManager.size());
        
        cacheManager.clear();
        assertEquals(0, cacheManager.size());
    }
    
    @Test
    public void testStats() {
        // Очищаем кэш перед тестом
        cacheManager.clear();
        
        // Добавляем несколько значений
        cacheManager.put("key1", "value1");
        cacheManager.put("key2", "value2");
        
        // Получаем значения (попадания)
        cacheManager.get("key1");
        cacheManager.get("key2");
        
        // Пытаемся получить несуществующий ключ (промах)
        cacheManager.get("key3");
        
        CacheStats stats = cacheManager.getStats();
        assertNotNull(stats);
        assertEquals(2, stats.getSize());
        assertEquals(2, stats.getHits());
        assertEquals(1, stats.getMisses());
        assertEquals(3, stats.getTotalRequests());
        assertTrue(stats.getHitRate() > 0);
    }
    
    @Test
    public void testDetailedStats() {
        // Очищаем кэш перед тестом
        cacheManager.clear();
        
        cacheManager.put("key1", "value1");
        cacheManager.get("key1");
        cacheManager.get("key2"); // промах
        
        String detailedStats = cacheManager.getDetailedStats();
        assertNotNull(detailedStats);
        assertTrue(detailedStats.contains("Cache Stats"));
        assertTrue(detailedStats.contains("Size=1"));
        assertTrue(detailedStats.contains("Hits=1"));
        assertTrue(detailedStats.contains("Misses=1"));
    }
    
    @Test
    public void testNullKey() {
        cacheManager.put(null, "value");
        assertNull(cacheManager.get(null));
        assertFalse(cacheManager.containsKey(null));
    }
    
    @Test
    public void testNullValue() {
        cacheManager.put("key", null);
        assertNull(cacheManager.get("key"));
    }
    
    @Test
    public void testEmptyKey() {
        cacheManager.put("", "value");
        assertNull(cacheManager.get(""));
        assertFalse(cacheManager.containsKey(""));
    }
    
    @Test
    public void testConcurrentAccess() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String key = "key_" + threadId + "_" + j;
                    String value = "value_" + threadId + "_" + j;
                    
                    cacheManager.put(key, value);
                    cacheManager.get(key);
                }
            });
        }
        
        // Запускаем все потоки
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Ждем завершения всех потоков
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Проверяем, что кэш работает корректно
        CacheStats stats = cacheManager.getStats();
        assertTrue(stats.getHits() > 0);
        assertTrue(stats.getTotalRequests() > 0);
    }
}
