package net.sf.l2j.botmanager.performance;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;

/**
 * Монитор производительности системы.
 * 
 * Отслеживает метрики производительности, время выполнения операций,
 * использование памяти и другие ключевые показатели.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class PerformanceMonitor {
    
    private static final Logger logger = Logger.getLogger(PerformanceMonitor.class);
    private static PerformanceMonitor instance;
    
    /** Метрики производительности */
    private final Map<String, PerformanceMetrics> metrics;
    
    /** Общие счетчики */
    private final AtomicLong totalOperations;
    private final AtomicLong totalExecutionTime;
    private final AtomicLong peakMemoryUsage;
    private final AtomicLong currentMemoryUsage;
    
    /** Флаг активности мониторинга */
    private volatile boolean monitoringEnabled;
    
    /**
     * Конструктор.
     */
    private PerformanceMonitor() {
        this.metrics = new ConcurrentHashMap<>();
        this.totalOperations = new AtomicLong(0);
        this.totalExecutionTime = new AtomicLong(0);
        this.peakMemoryUsage = new AtomicLong(0);
        this.currentMemoryUsage = new AtomicLong(0);
        this.monitoringEnabled = true;
    }
    
    /**
     * Получить экземпляр монитора.
     * 
     * @return экземпляр монитора
     */
    public static synchronized PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }
    
    /**
     * Начать измерение производительности операции.
     * 
     * @param operationName название операции
     * @return контекст измерения
     */
    public PerformanceContext startOperation(String operationName) {
        if (!monitoringEnabled || operationName == null || operationName.isEmpty()) {
            return new PerformanceContext(operationName, 0);
        }
        
        PerformanceMetrics operationMetrics = metrics.computeIfAbsent(operationName, 
            k -> new PerformanceMetrics(operationName));
        
        return new PerformanceContext(operationName, System.nanoTime());
    }
    
    /**
     * Завершить измерение производительности операции.
     * 
     * @param context контекст измерения
     */
    public void endOperation(PerformanceContext context) {
        if (!monitoringEnabled || context.getStartTime() == 0) {
            return;
        }
        
        long executionTime = System.nanoTime() - context.getStartTime();
        String operationName = context.getOperationName();
        
        PerformanceMetrics operationMetrics = metrics.get(operationName);
        if (operationMetrics != null) {
            operationMetrics.recordExecution(executionTime);
        }
        
        totalOperations.incrementAndGet();
        totalExecutionTime.addAndGet(executionTime);
        
        // Обновляем использование памяти
        updateMemoryUsage();
    }
    
    /**
     * Записать метрику времени выполнения.
     * 
     * @param operationName название операции
     * @param executionTime время выполнения в наносекундах
     */
    public void recordExecutionTime(String operationName, long executionTime) {
        if (!monitoringEnabled) {
            return;
        }
        
        PerformanceMetrics operationMetrics = metrics.computeIfAbsent(operationName, 
            k -> new PerformanceMetrics(operationName));
        
        operationMetrics.recordExecution(executionTime);
        totalOperations.incrementAndGet();
        totalExecutionTime.addAndGet(executionTime);
    }
    
    /**
     * Записать метрику использования памяти.
     * 
     * @param memoryUsage использование памяти в байтах
     */
    public void recordMemoryUsage(long memoryUsage) {
        if (!monitoringEnabled) {
            return;
        }
        
        currentMemoryUsage.set(memoryUsage);
        
        // Обновляем пиковое использование
        long current = currentMemoryUsage.get();
        long peak = peakMemoryUsage.get();
        while (current > peak && !peakMemoryUsage.compareAndSet(peak, current)) {
            peak = peakMemoryUsage.get();
        }
    }
    
    /**
     * Получить метрики для операции.
     * 
     * @param operationName название операции
     * @return метрики операции
     */
    public PerformanceMetrics getMetrics(String operationName) {
        return metrics.get(operationName);
    }
    
    /**
     * Получить общую статистику производительности.
     * 
     * @return общая статистика
     */
    public OverallPerformanceStats getOverallStats() {
        long totalOps = totalOperations.get();
        long totalTime = totalExecutionTime.get();
        long avgExecutionTime = totalOps > 0 ? totalTime / totalOps : 0;
        
        return new OverallPerformanceStats(
            totalOps,
            avgExecutionTime,
            peakMemoryUsage.get(),
            currentMemoryUsage.get(),
            metrics.size()
        );
    }
    
    /**
     * Получить детальную статистику всех операций.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Performance Monitor Statistics ===\n");
        
        OverallPerformanceStats overall = getOverallStats();
        stats.append(String.format("Total Operations: %d\n", overall.getTotalOperations()));
        stats.append(String.format("Average Execution Time: %d ns (%.2f ms)\n", 
            overall.getAverageExecutionTime(), overall.getAverageExecutionTime() / 1_000_000.0));
        stats.append(String.format("Peak Memory Usage: %d bytes (%.2f MB)\n", 
            overall.getPeakMemoryUsage(), overall.getPeakMemoryUsage() / 1_000_000.0));
        stats.append(String.format("Current Memory Usage: %d bytes (%.2f MB)\n", 
            overall.getCurrentMemoryUsage(), overall.getCurrentMemoryUsage() / 1_000_000.0));
        stats.append(String.format("Monitored Operations: %d\n", overall.getMonitoredOperations()));
        
        stats.append("\n=== Per-Operation Statistics ===\n");
        for (PerformanceMetrics metric : metrics.values()) {
            stats.append(metric.getDetailedStats()).append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Сбросить все метрики.
     */
    public void reset() {
        metrics.clear();
        totalOperations.set(0);
        totalExecutionTime.set(0);
        peakMemoryUsage.set(0);
        currentMemoryUsage.set(0);
        logger.info("Performance metrics reset");
    }
    
    /**
     * Включить/выключить мониторинг.
     * 
     * @param enabled включить мониторинг
     */
    public void setMonitoringEnabled(boolean enabled) {
        this.monitoringEnabled = enabled;
        logger.info("Performance monitoring " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Проверить включен ли мониторинг.
     * 
     * @return true если мониторинг включен
     */
    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }
    
    /**
     * Обновить использование памяти.
     */
    private void updateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        recordMemoryUsage(usedMemory);
    }
    
    /**
     * Контекст измерения производительности.
     */
    public static class PerformanceContext {
        private final String operationName;
        private final long startTime;
        
        public PerformanceContext(String operationName, long startTime) {
            this.operationName = operationName;
            this.startTime = startTime;
        }
        
        public String getOperationName() {
            return operationName;
        }
        
        public long getStartTime() {
            return startTime;
        }
    }
}
