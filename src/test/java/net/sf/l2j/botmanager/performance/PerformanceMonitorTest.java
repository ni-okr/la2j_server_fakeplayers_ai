package net.sf.l2j.botmanager.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для монитора производительности.
 */
public class PerformanceMonitorTest {
    
    private PerformanceMonitor monitor;
    
    @BeforeEach
    public void setUp() {
        monitor = PerformanceMonitor.getInstance();
        monitor.reset();
    }
    
    @AfterEach
    public void tearDown() {
        monitor.reset();
    }
    
    @Test
    public void testPerformanceContext() {
        PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
        
        assertNotNull(context);
        assertEquals("testOperation", context.getOperationName());
        assertTrue(context.getStartTime() > 0);
    }
    
    @Test
    public void testOperationRecording() {
        PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
        
        // Имитируем выполнение операции
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        monitor.endOperation(context);
        
        PerformanceMetrics metrics = monitor.getMetrics("testOperation");
        assertNotNull(metrics);
        assertEquals("testOperation", metrics.getOperationName());
        assertEquals(1, metrics.getExecutionCount());
        assertTrue(metrics.getTotalExecutionTime() > 0);
        assertTrue(metrics.getAverageExecutionTime() > 0);
    }
    
    @Test
    public void testMultipleOperations() {
        // Выполняем несколько операций
        for (int i = 0; i < 5; i++) {
            PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            monitor.endOperation(context);
        }
        
        PerformanceMetrics metrics = monitor.getMetrics("testOperation");
        assertNotNull(metrics);
        assertEquals(5, metrics.getExecutionCount());
        assertTrue(metrics.getTotalExecutionTime() > 0);
        assertTrue(metrics.getAverageExecutionTime() > 0);
        assertTrue(metrics.getMinExecutionTime() > 0);
        assertTrue(metrics.getMaxExecutionTime() > 0);
    }
    
    @Test
    public void testDirectExecutionTimeRecording() {
        monitor.recordExecutionTime("testOperation", 1000000); // 1ms в наносекундах
        
        PerformanceMetrics metrics = monitor.getMetrics("testOperation");
        assertNotNull(metrics);
        assertEquals(1, metrics.getExecutionCount());
        assertEquals(1000000, metrics.getTotalExecutionTime());
        assertEquals(1000000, metrics.getAverageExecutionTime());
    }
    
    @Test
    public void testMemoryUsageRecording() {
        monitor.recordMemoryUsage(1024 * 1024); // 1MB
        
        OverallPerformanceStats stats = monitor.getOverallStats();
        assertEquals(1024 * 1024, stats.getCurrentMemoryUsage());
        assertEquals(1024 * 1024, stats.getPeakMemoryUsage());
    }
    
    @Test
    public void testOverallStats() {
        // Выполняем несколько операций
        for (int i = 0; i < 3; i++) {
            PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            monitor.endOperation(context);
        }
        
        OverallPerformanceStats stats = monitor.getOverallStats();
        assertNotNull(stats);
        assertEquals(3, stats.getTotalOperations());
        assertTrue(stats.getAverageExecutionTime() > 0);
        assertTrue(stats.getMonitoredOperations() > 0);
    }
    
    @Test
    public void testDetailedStats() {
        PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        monitor.endOperation(context);
        
        String detailedStats = monitor.getDetailedStats();
        assertNotNull(detailedStats);
        assertTrue(detailedStats.contains("Performance Monitor Statistics"));
        assertTrue(detailedStats.contains("testOperation"));
    }
    
    @Test
    public void testReset() {
        // Выполняем операцию
        PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
        monitor.endOperation(context);
        
        // Проверяем, что метрики есть
        PerformanceMetrics metrics = monitor.getMetrics("testOperation");
        assertNotNull(metrics);
        assertEquals(1, metrics.getExecutionCount());
        
        // Сбрасываем
        monitor.reset();
        
        // Проверяем, что метрики сброшены
        metrics = monitor.getMetrics("testOperation");
        assertNull(metrics);
        
        OverallPerformanceStats stats = monitor.getOverallStats();
        assertEquals(0, stats.getTotalOperations());
    }
    
    @Test
    public void testMonitoringEnabled() {
        assertTrue(monitor.isMonitoringEnabled());
        
        monitor.setMonitoringEnabled(false);
        assertFalse(monitor.isMonitoringEnabled());
        
        // При выключенном мониторинге операции не должны записываться
        PerformanceMonitor.PerformanceContext context = monitor.startOperation("testOperation");
        monitor.endOperation(context);
        
        PerformanceMetrics metrics = monitor.getMetrics("testOperation");
        assertNull(metrics);
        
        monitor.setMonitoringEnabled(true);
        assertTrue(monitor.isMonitoringEnabled());
    }
    
    @Test
    public void testNullOperationName() {
        PerformanceMonitor.PerformanceContext context = monitor.startOperation(null);
        assertNotNull(context);
        assertEquals(0, context.getStartTime());
        
        monitor.endOperation(context);
        
        // Не должно быть записано никаких метрик
        OverallPerformanceStats stats = monitor.getOverallStats();
        assertEquals(0, stats.getTotalOperations());
    }
    
    @Test
    public void testEmptyOperationName() {
        PerformanceMonitor.PerformanceContext context = monitor.startOperation("");
        assertNotNull(context);
        assertEquals(0, context.getStartTime());
        
        monitor.endOperation(context);
        
        // Не должно быть записано никаких метрик
        OverallPerformanceStats stats = monitor.getOverallStats();
        assertEquals(0, stats.getTotalOperations());
    }
}
