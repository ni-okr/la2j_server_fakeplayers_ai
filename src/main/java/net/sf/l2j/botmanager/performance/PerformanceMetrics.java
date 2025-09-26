package net.sf.l2j.botmanager.performance;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Метрики производительности для конкретной операции.
 * 
 * Отслеживает статистику выполнения операции: количество вызовов,
 * время выполнения, минимальное/максимальное время, среднее время.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class PerformanceMetrics {
    
    private final String operationName;
    private final AtomicLong executionCount;
    private final AtomicLong totalExecutionTime;
    private final AtomicLong minExecutionTime;
    private final AtomicLong maxExecutionTime;
    private final AtomicReference<Long> lastExecutionTime;
    
    /**
     * Конструктор.
     * 
     * @param operationName название операции
     */
    public PerformanceMetrics(String operationName) {
        this.operationName = operationName;
        this.executionCount = new AtomicLong(0);
        this.totalExecutionTime = new AtomicLong(0);
        this.minExecutionTime = new AtomicLong(Long.MAX_VALUE);
        this.maxExecutionTime = new AtomicLong(0);
        this.lastExecutionTime = new AtomicReference<>(0L);
    }
    
    /**
     * Записать выполнение операции.
     * 
     * @param executionTime время выполнения в наносекундах
     */
    public void recordExecution(long executionTime) {
        executionCount.incrementAndGet();
        totalExecutionTime.addAndGet(executionTime);
        lastExecutionTime.set(executionTime);
        
        // Обновляем минимальное время
        long currentMin = minExecutionTime.get();
        while (executionTime < currentMin && !minExecutionTime.compareAndSet(currentMin, executionTime)) {
            currentMin = minExecutionTime.get();
        }
        
        // Обновляем максимальное время
        long currentMax = maxExecutionTime.get();
        while (executionTime > currentMax && !maxExecutionTime.compareAndSet(currentMax, executionTime)) {
            currentMax = maxExecutionTime.get();
        }
    }
    
    /**
     * Получить название операции.
     * 
     * @return название операции
     */
    public String getOperationName() {
        return operationName;
    }
    
    /**
     * Получить количество выполнений.
     * 
     * @return количество выполнений
     */
    public long getExecutionCount() {
        return executionCount.get();
    }
    
    /**
     * Получить общее время выполнения.
     * 
     * @return общее время выполнения в наносекундах
     */
    public long getTotalExecutionTime() {
        return totalExecutionTime.get();
    }
    
    /**
     * Получить среднее время выполнения.
     * 
     * @return среднее время выполнения в наносекундах
     */
    public long getAverageExecutionTime() {
        long count = executionCount.get();
        return count > 0 ? totalExecutionTime.get() / count : 0;
    }
    
    /**
     * Получить минимальное время выполнения.
     * 
     * @return минимальное время выполнения в наносекундах
     */
    public long getMinExecutionTime() {
        long min = minExecutionTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }
    
    /**
     * Получить максимальное время выполнения.
     * 
     * @return максимальное время выполнения в наносекундах
     */
    public long getMaxExecutionTime() {
        return maxExecutionTime.get();
    }
    
    /**
     * Получить время последнего выполнения.
     * 
     * @return время последнего выполнения в наносекундах
     */
    public long getLastExecutionTime() {
        return lastExecutionTime.get();
    }
    
    /**
     * Получить детальную статистику.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        long count = getExecutionCount();
        long avg = getAverageExecutionTime();
        long min = getMinExecutionTime();
        long max = getMaxExecutionTime();
        long last = getLastExecutionTime();
        
        return String.format(
            "Operation: %s | Count: %d | Avg: %d ns (%.2f ms) | Min: %d ns (%.2f ms) | Max: %d ns (%.2f ms) | Last: %d ns (%.2f ms)",
            operationName,
            count,
            avg, avg / 1_000_000.0,
            min, min / 1_000_000.0,
            max, max / 1_000_000.0,
            last, last / 1_000_000.0
        );
    }
    
    /**
     * Получить краткую статистику.
     * 
     * @return краткая статистика
     */
    public String getShortStats() {
        return String.format("%s: %d calls, avg %.2f ms", 
            operationName, getExecutionCount(), getAverageExecutionTime() / 1_000_000.0);
    }
    
    /**
     * Сбросить метрики.
     */
    public void reset() {
        executionCount.set(0);
        totalExecutionTime.set(0);
        minExecutionTime.set(Long.MAX_VALUE);
        maxExecutionTime.set(0);
        lastExecutionTime.set(0L);
    }
}
