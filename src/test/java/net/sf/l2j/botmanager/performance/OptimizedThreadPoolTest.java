package net.sf.l2j.botmanager.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Тесты для оптимизированного пула потоков.
 */
public class OptimizedThreadPoolTest {
    
    private OptimizedThreadPool threadPool;
    
    @BeforeEach
    public void setUp() {
        threadPool = OptimizedThreadPool.getInstance();
        threadPool.initialize();
    }
    
    @AfterEach
    public void tearDown() {
        threadPool.shutdown();
    }
    
    @Test
    public void testSubmitRunnable() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        
        Future<?> future = threadPool.submit(() -> counter.incrementAndGet());
        assertNotNull(future);
        
        // Ждем завершения
        future.get(5, TimeUnit.SECONDS);
        
        assertEquals(1, counter.get());
    }
    
    @Test
    public void testSubmitCallable() throws Exception {
        Future<String> future = threadPool.submit(() -> "test result");
        assertNotNull(future);
        
        String result = future.get(5, TimeUnit.SECONDS);
        assertEquals("test result", result);
    }
    
    @Test
    public void testSchedule() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        
        Future<?> future = threadPool.schedule(() -> counter.incrementAndGet(), 100);
        assertNotNull(future);
        
        // Ждем завершения
        future.get(1, TimeUnit.SECONDS);
        
        assertEquals(1, counter.get());
    }
    
    @Test
    public void testScheduleAtFixedRate() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        
        Future<?> future = threadPool.scheduleAtFixedRate(() -> counter.incrementAndGet(), 50, 100);
        assertNotNull(future);
        
        // Ждем несколько выполнений
        Thread.sleep(300);
        
        // Отменяем задачу
        future.cancel(true);
        
        // Проверяем, что задача выполнялась несколько раз
        assertTrue(counter.get() >= 2);
    }
    
    @Test
    public void testStats() {
        ThreadPoolStats stats = threadPool.getStats();
        assertNotNull(stats);
        
        assertTrue(stats.getCorePoolSize() > 0);
        assertTrue(stats.getMaxPoolSize() > 0);
        assertTrue(stats.getCurrentPoolSize() >= 0);
        assertTrue(stats.getActiveThreads() >= 0);
        assertTrue(stats.getTotalTasks() >= 0);
        assertTrue(stats.getCompletedTasks() >= 0);
        assertTrue(stats.getQueueSize() >= 0);
        assertTrue(stats.getSubmittedTasks() >= 0);
        assertTrue(stats.getRejectedTasks() >= 0);
    }
    
    @Test
    public void testDetailedStats() {
        String detailedStats = threadPool.getDetailedStats();
        assertNotNull(detailedStats);
        assertTrue(detailedStats.contains("Thread Pool Statistics"));
        assertTrue(detailedStats.contains("Core Pool Size"));
        assertTrue(detailedStats.contains("Max Pool Size"));
        assertTrue(detailedStats.contains("Active Threads"));
    }
    
    @Test
    public void testConcurrentExecution() throws Exception {
        int numTasks = 10;
        AtomicInteger counter = new AtomicInteger(0);
        Future<?>[] futures = new Future[numTasks];
        
        // Запускаем несколько задач одновременно
        for (int i = 0; i < numTasks; i++) {
            futures[i] = threadPool.submit(() -> {
                try {
                    Thread.sleep(100);
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Ждем завершения всех задач
        for (Future<?> future : futures) {
            future.get(2, TimeUnit.SECONDS);
        }
        
        assertEquals(numTasks, counter.get());
    }
    
    @Test
    public void testNullTask() {
        Future<?> future = threadPool.submit((Runnable) null);
        assertNull(future);
        
        Future<String> callableFuture = threadPool.submit((java.util.concurrent.Callable<String>) null);
        assertNull(callableFuture);
        
        Future<?> scheduledFuture = threadPool.schedule(null, 100);
        assertNull(scheduledFuture);
        
        Future<?> periodicFuture = threadPool.scheduleAtFixedRate(null, 100, 200);
        assertNull(periodicFuture);
    }
    
    @Test
    public void testTaskWithException() throws Exception {
        Future<?> future = threadPool.submit(() -> {
            throw new RuntimeException("Test exception");
        });
        
        assertNotNull(future);
        
        // Задача должна завершиться с исключением
        assertThrows(Exception.class, () -> future.get(1, TimeUnit.SECONDS));
    }
    
    @Test
    public void testUtilization() {
        ThreadPoolStats stats = threadPool.getStats();
        
        double utilization = stats.getUtilization();
        assertTrue(utilization >= 0.0);
        assertTrue(utilization <= 1.0);
        
        double utilizationPercent = stats.getUtilizationPercent();
        assertTrue(utilizationPercent >= 0.0);
        assertTrue(utilizationPercent <= 100.0);
    }
    
    @Test
    public void testCompletionRate() throws Exception {
        // Выполняем несколько задач
        for (int i = 0; i < 5; i++) {
            Future<?> future = threadPool.submit(() -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            future.get(1, TimeUnit.SECONDS);
        }
        
        ThreadPoolStats stats = threadPool.getStats();
        
        double completionRate = stats.getCompletionRate();
        assertTrue(completionRate >= 0.0);
        assertTrue(completionRate <= 1.0);
        
        double completionRatePercent = stats.getCompletionRatePercent();
        assertTrue(completionRatePercent >= 0.0);
        assertTrue(completionRatePercent <= 100.0);
    }
    
    @Test
    public void testRejectionRate() {
        ThreadPoolStats stats = threadPool.getStats();
        
        double rejectionRate = stats.getRejectionRate();
        assertTrue(rejectionRate >= 0.0);
        assertTrue(rejectionRate <= 1.0);
        
        double rejectionRatePercent = stats.getRejectionRatePercent();
        assertTrue(rejectionRatePercent >= 0.0);
        assertTrue(rejectionRatePercent <= 100.0);
    }
    
    @Test
    public void testShortStats() {
        ThreadPoolStats stats = threadPool.getStats();
        String shortStats = stats.getShortStats();
        
        assertNotNull(shortStats);
        assertTrue(shortStats.contains("Pool:"));
        assertTrue(shortStats.contains("threads"));
        assertTrue(shortStats.contains("tasks"));
    }
    
    @Test
    public void testToString() {
        ThreadPoolStats stats = threadPool.getStats();
        String toString = stats.toString();
        
        assertNotNull(toString);
        assertEquals(stats.getShortStats(), toString);
    }
}
