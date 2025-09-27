package net.sf.l2j.botmanager.performance;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.actions.MoveAction;
import net.sf.l2j.botmanager.actions.AttackAction;
import net.sf.l2j.botmanager.actions.CastAction;
import net.sf.l2j.botmanager.actions.LootAction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Тесты для PoolManager
 */
public class PoolManagerTest {
    
    private PoolManager poolManager;
    
    @BeforeEach
    void setUp() {
        poolManager = PoolManager.getInstance();
        poolManager.clearAllPools();
    }
    
    @Test
    @DisplayName("Тест Singleton паттерна")
    void testSingleton() {
        PoolManager instance1 = PoolManager.getInstance();
        PoolManager instance2 = PoolManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("Тест регистрации пула")
    void testRegisterPool() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        
        assertTrue(poolManager.hasPool(TestObject.class));
        assertEquals(1, poolManager.getPoolCount());
    }
    
    @Test
    @DisplayName("Тест регистрации пула с размером по умолчанию")
    void testRegisterPoolWithDefaultSize() {
        poolManager.registerPool(TestObject.class, () -> new TestObject());
        
        assertTrue(poolManager.hasPool(TestObject.class));
        assertEquals(1, poolManager.getPoolCount());
    }
    
    @Test
    @DisplayName("Тест удаления пула")
    void testRemovePool() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        assertTrue(poolManager.hasPool(TestObject.class));
        
        boolean removed = poolManager.removePool(TestObject.class);
        assertTrue(removed);
        assertFalse(poolManager.hasPool(TestObject.class));
        assertEquals(0, poolManager.getPoolCount());
    }
    
    @Test
    @DisplayName("Тест получения объекта из пула")
    void testAcquireObject() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        
        TestObject obj = poolManager.acquire(TestObject.class);
        assertNotNull(obj);
        assertTrue(obj instanceof TestObject);
    }
    
    @Test
    @DisplayName("Тест возврата объекта в пул")
    void testReleaseObject() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        
        TestObject obj = poolManager.acquire(TestObject.class);
        boolean released = poolManager.release(obj);
        
        assertTrue(released);
    }
    
    @Test
    @DisplayName("Тест переиспользования объекта")
    void testObjectReuse() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        
        TestObject obj1 = poolManager.acquire(TestObject.class);
        poolManager.release(obj1);
        
        TestObject obj2 = poolManager.acquire(TestObject.class);
        assertSame(obj1, obj2);
    }
    
    @Test
    @DisplayName("Тест предварительного заполнения пула")
    void testPrefillPool() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 10);
        
        int prefillCount = poolManager.prefillPool(TestObject.class, 5);
        assertEquals(5, prefillCount);
    }
    
    @Test
    @DisplayName("Тест очистки пула")
    void testClearPool() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        poolManager.prefillPool(TestObject.class, 3);
        
        poolManager.clearPool(TestObject.class);
        String stats = poolManager.getPoolStatistics(TestObject.class);
        assertTrue(stats.contains("Available: 0"));
    }
    
    @Test
    @DisplayName("Тест очистки всех пулов")
    void testClearAllPools() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        poolManager.registerPool(AnotherTestObject.class, () -> new AnotherTestObject(), 5);
        
        poolManager.clearAllPools();
        assertEquals(0, poolManager.getTotalAvailableObjects());
    }
    
    @Test
    @DisplayName("Тест статистики пула")
    void testPoolStatistics() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        poolManager.prefillPool(TestObject.class, 3);
        
        String stats = poolManager.getPoolStatistics(TestObject.class);
        assertNotNull(stats);
        assertTrue(stats.contains("Available: 3"));
        assertTrue(stats.contains("Created: 3"));
    }
    
    @Test
    @DisplayName("Тест общей статистики")
    void testAllStatistics() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        poolManager.registerPool(AnotherTestObject.class, () -> new AnotherTestObject(), 5);
        
        String stats = poolManager.getAllStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Total Pools: 2"));
        assertTrue(stats.contains("Pool: TestObject"));
        assertTrue(stats.contains("Pool: AnotherTestObject"));
    }
    
    @Test
    @DisplayName("Тест сброса статистики")
    void testResetStatistics() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        poolManager.prefillPool(TestObject.class, 3);
        
        poolManager.resetAllStatistics();
        assertEquals(0, poolManager.getTotalCreatedObjects());
    }
    
    @Test
    @DisplayName("Тест общего коэффициента переиспользования")
    void testOverallReuseRatio() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        
        TestObject obj = poolManager.acquire(TestObject.class);
        poolManager.release(obj);
        poolManager.acquire(TestObject.class); // Переиспользуем
        
        double reuseRatio = poolManager.getOverallReuseRatio();
        assertTrue(reuseRatio > 0.0);
    }
    
    @Test
    @DisplayName("Тест операции с объектом из пула")
    void testWithPooledObject() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        
        TestObject result = poolManager.withPooledObject(TestObject.class, obj -> {
            assertNotNull(obj);
            return obj; // Возвращаем тот же объект
        });
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("Тест многопоточности")
    void testConcurrency() throws InterruptedException {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 10);
        
        int threadCount = 5;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        TestObject obj = poolManager.acquire(TestObject.class);
                        if (obj != null) {
                            poolManager.release(obj);
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertEquals(threadCount * operationsPerThread, successCount.get());
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - BotContext")
    void testWithBotContext() {
        BotContext context = poolManager.acquire(BotContext.class);
        assertNotNull(context);
        assertTrue(context instanceof BotContext);
        
        boolean released = poolManager.release(context);
        assertTrue(released);
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - MoveAction")
    void testWithMoveAction() {
        MoveAction action = poolManager.acquire(MoveAction.class);
        assertNotNull(action);
        assertTrue(action instanceof MoveAction);
        
        boolean released = poolManager.release(action);
        assertTrue(released);
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - AttackAction")
    void testWithAttackAction() {
        AttackAction action = poolManager.acquire(AttackAction.class);
        assertNotNull(action);
        assertTrue(action instanceof AttackAction);
        
        boolean released = poolManager.release(action);
        assertTrue(released);
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - CastAction")
    void testWithCastAction() {
        CastAction action = poolManager.acquire(CastAction.class);
        assertNotNull(action);
        assertTrue(action instanceof CastAction);
        
        boolean released = poolManager.release(action);
        assertTrue(released);
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - LootAction")
    void testWithLootAction() {
        LootAction action = poolManager.acquire(LootAction.class);
        assertNotNull(action);
        assertTrue(action instanceof LootAction);
        
        boolean released = poolManager.release(action);
        assertTrue(released);
    }
    
    @Test
    @DisplayName("Тест обработки ошибок")
    void testErrorHandling() {
        // Тест получения объекта из несуществующего пула
        assertThrows(IllegalStateException.class, () -> {
            poolManager.acquire(TestObject.class);
        });
        
        // Тест возврата объекта в несуществующий пул
        TestObject obj = new TestObject();
        boolean released = poolManager.release(obj);
        assertFalse(released);
        
        // Тест регистрации пула с null параметрами
        assertThrows(IllegalArgumentException.class, () -> {
            poolManager.registerPool(null, () -> new TestObject(), 5);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            poolManager.registerPool(TestObject.class, null, 5);
        });
    }
    
    @Test
    @DisplayName("Тест завершения работы")
    void testShutdown() {
        poolManager.registerPool(TestObject.class, () -> new TestObject(), 5);
        poolManager.prefillPool(TestObject.class, 3);
        
        poolManager.shutdown();
        assertEquals(0, poolManager.getPoolCount());
        assertEquals(0, poolManager.getTotalAvailableObjects());
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================
    
    /**
     * Тестовый объект для пула
     */
    private static class TestObject {
        private boolean inUse = false;
        
        public void markInUse() {
            this.inUse = true;
        }
        
        public void markFree() {
            this.inUse = false;
        }
        
        public boolean isInUse() {
            return inUse;
        }
    }
    
    /**
     * Другой тестовый объект для пула
     */
    private static class AnotherTestObject {
        private boolean inUse = false;
        
        public void markInUse() {
            this.inUse = true;
        }
        
        public void markFree() {
            this.inUse = false;
        }
        
        public boolean isInUse() {
            return inUse;
        }
    }
}
