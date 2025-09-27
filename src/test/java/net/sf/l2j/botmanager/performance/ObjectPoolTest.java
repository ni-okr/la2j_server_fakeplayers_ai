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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Тесты для ObjectPool
 */
public class ObjectPoolTest {
    
    private ObjectPool<TestObject> pool;
    private TestObjectFactory factory;
    
    @BeforeEach
    void setUp() {
        factory = new TestObjectFactory();
        pool = new ObjectPool<>(factory, 10);
    }
    
    @Test
    @DisplayName("Тест создания пула")
    void testPoolCreation() {
        assertNotNull(pool);
        assertEquals(10, pool.getMaxSize());
        assertEquals(0, pool.getAvailableCount());
        assertEquals(0, pool.getCreatedCount());
        assertEquals(0.0, pool.getReuseRatio());
    }
    
    @Test
    @DisplayName("Тест получения объекта из пула")
    void testAcquireObject() {
        TestObject obj = pool.acquire();
        
        assertNotNull(obj);
        assertTrue(factory.wasCalled());
        assertEquals(1, pool.getCreatedCount());
        assertEquals(0, pool.getAvailableCount());
    }
    
    @Test
    @DisplayName("Тест возврата объекта в пул")
    void testReleaseObject() {
        TestObject obj = pool.acquire();
        boolean released = pool.release(obj);
        
        assertTrue(released);
        assertEquals(1, pool.getAvailableCount());
        assertEquals(1, pool.getCreatedCount());
    }
    
    @Test
    @DisplayName("Тест переиспользования объекта")
    void testObjectReuse() {
        TestObject obj1 = pool.acquire();
        pool.release(obj1);
        
        TestObject obj2 = pool.acquire();
        
        assertSame(obj1, obj2);
        assertEquals(1, pool.getCreatedCount());
        assertEquals(0, pool.getAvailableCount());
    }
    
    @Test
    @DisplayName("Тест максимального размера пула")
    void testMaxPoolSize() {
        List<TestObject> objects = new ArrayList<>();
        
        // Создаем больше объектов чем максимальный размер пула
        for (int i = 0; i < 15; i++) {
            TestObject obj = pool.acquire();
            objects.add(obj);
        }
        
        // Возвращаем все объекты
        for (TestObject obj : objects) {
            pool.release(obj);
        }
        
        // Должно быть доступно только максимальное количество
        assertEquals(10, pool.getAvailableCount());
        assertEquals(10, pool.getCreatedCount());
    }
    
    @Test
    @DisplayName("Тест предварительного заполнения пула")
    void testPrefillPool() {
        int prefillCount = pool.prefill(5);
        
        assertEquals(5, prefillCount);
        assertEquals(5, pool.getAvailableCount());
        assertEquals(5, pool.getCreatedCount());
    }
    
    @Test
    @DisplayName("Тест предварительного заполнения с превышением максимума")
    void testPrefillExceedsMax() {
        int prefillCount = pool.prefill(15);
        
        assertEquals(10, prefillCount);
        assertEquals(10, pool.getAvailableCount());
        assertEquals(10, pool.getCreatedCount());
    }
    
    @Test
    @DisplayName("Тест очистки пула")
    void testClearPool() {
        pool.prefill(5);
        pool.clear();
        
        assertEquals(0, pool.getAvailableCount());
        assertEquals(0, pool.getCreatedCount());
    }
    
    @Test
    @DisplayName("Тест статистики пула")
    void testPoolStatistics() {
        pool.prefill(3);
        
        String stats = pool.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Available: 3"));
        assertTrue(stats.contains("Created: 3"));
        assertTrue(stats.contains("Max Size: 10"));
    }
    
    @Test
    @DisplayName("Тест сброса статистики")
    void testResetStatistics() {
        pool.prefill(3);
        pool.resetStatistics();
        
        assertEquals(0, pool.getCreatedCount());
        assertEquals(3, pool.getAvailableCount()); // Объекты остаются в пуле
    }
    
    @Test
    @DisplayName("Тест коэффициента переиспользования")
    void testReuseRatio() {
        TestObject obj = pool.acquire();
        pool.release(obj);
        pool.acquire(); // Переиспользуем объект
        
        double reuseRatio = pool.getReuseRatio();
        assertTrue(reuseRatio > 0.0);
    }
    
    @Test
    @DisplayName("Тест многопоточности")
    void testConcurrency() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        TestObject obj = pool.acquire();
                        if (obj != null) {
                            pool.release(obj);
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
        ObjectPool<BotContext> contextPool = new ObjectPool<>(
            () -> new BotContext(1), 5
        );
        
        BotContext context = contextPool.acquire();
        assertNotNull(context);
        assertEquals(1, context.getBotId());
        
        boolean released = contextPool.release(context);
        assertTrue(released);
        assertEquals(1, contextPool.getAvailableCount());
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - MoveAction")
    void testWithMoveAction() {
        ObjectPool<MoveAction> actionPool = new ObjectPool<>(
            () -> new MoveAction(), 5
        );
        
        MoveAction action = actionPool.acquire();
        assertNotNull(action);
        
        boolean released = actionPool.release(action);
        assertTrue(released);
        assertEquals(1, actionPool.getAvailableCount());
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - AttackAction")
    void testWithAttackAction() {
        ObjectPool<AttackAction> actionPool = new ObjectPool<>(
            () -> new AttackAction(), 5
        );
        
        AttackAction action = actionPool.acquire();
        assertNotNull(action);
        
        boolean released = actionPool.release(action);
        assertTrue(released);
        assertEquals(1, actionPool.getAvailableCount());
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - CastAction")
    void testWithCastAction() {
        ObjectPool<CastAction> actionPool = new ObjectPool<>(
            () -> new CastAction(), 5
        );
        
        CastAction action = actionPool.acquire();
        assertNotNull(action);
        
        boolean released = actionPool.release(action);
        assertTrue(released);
        assertEquals(1, actionPool.getAvailableCount());
    }
    
    @Test
    @DisplayName("Тест с реальными объектами - LootAction")
    void testWithLootAction() {
        ObjectPool<LootAction> actionPool = new ObjectPool<>(
            () -> new LootAction(), 5
        );
        
        LootAction action = actionPool.acquire();
        assertNotNull(action);
        
        boolean released = actionPool.release(action);
        assertTrue(released);
        assertEquals(1, actionPool.getAvailableCount());
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================
    
    /**
     * Тестовый объект для пула
     */
    private static class TestObject {
        private boolean inUse = false;
        private final int id;
        
        public TestObject(int id) {
            this.id = id;
        }
        
        public void markInUse() {
            this.inUse = true;
        }
        
        public void markFree() {
            this.inUse = false;
        }
        
        public boolean isInUse() {
            return inUse;
        }
        
        public int getId() {
            return id;
        }
    }
    
    /**
     * Фабрика для тестовых объектов
     */
    private static class TestObjectFactory implements java.util.function.Supplier<TestObject> {
        private int counter = 0;
        private boolean called = false;
        
        @Override
        public TestObject get() {
            called = true;
            return new TestObject(++counter);
        }
        
        public boolean wasCalled() {
            return called;
        }
    }
}
