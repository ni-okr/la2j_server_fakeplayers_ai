package net.sf.l2j.botmanager.performance;

/**
 * Общая статистика производительности системы.
 * 
 * Содержит агрегированные метрики по всей системе:
 * общее количество операций, среднее время выполнения,
 * использование памяти и количество отслеживаемых операций.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class OverallPerformanceStats {
    
    private final long totalOperations;
    private final long averageExecutionTime;
    private final long peakMemoryUsage;
    private final long currentMemoryUsage;
    private final int monitoredOperations;
    
    /**
     * Конструктор.
     * 
     * @param totalOperations общее количество операций
     * @param averageExecutionTime среднее время выполнения в наносекундах
     * @param peakMemoryUsage пиковое использование памяти в байтах
     * @param currentMemoryUsage текущее использование памяти в байтах
     * @param monitoredOperations количество отслеживаемых операций
     */
    public OverallPerformanceStats(long totalOperations, long averageExecutionTime,
                                 long peakMemoryUsage, long currentMemoryUsage,
                                 int monitoredOperations) {
        this.totalOperations = totalOperations;
        this.averageExecutionTime = averageExecutionTime;
        this.peakMemoryUsage = peakMemoryUsage;
        this.currentMemoryUsage = currentMemoryUsage;
        this.monitoredOperations = monitoredOperations;
    }
    
    /**
     * Получить общее количество операций.
     * 
     * @return общее количество операций
     */
    public long getTotalOperations() {
        return totalOperations;
    }
    
    /**
     * Получить среднее время выполнения.
     * 
     * @return среднее время выполнения в наносекундах
     */
    public long getAverageExecutionTime() {
        return averageExecutionTime;
    }
    
    /**
     * Получить среднее время выполнения в миллисекундах.
     * 
     * @return среднее время выполнения в миллисекундах
     */
    public double getAverageExecutionTimeMs() {
        return averageExecutionTime / 1_000_000.0;
    }
    
    /**
     * Получить пиковое использование памяти.
     * 
     * @return пиковое использование памяти в байтах
     */
    public long getPeakMemoryUsage() {
        return peakMemoryUsage;
    }
    
    /**
     * Получить пиковое использование памяти в мегабайтах.
     * 
     * @return пиковое использование памяти в мегабайтах
     */
    public double getPeakMemoryUsageMb() {
        return peakMemoryUsage / 1_000_000.0;
    }
    
    /**
     * Получить текущее использование памяти.
     * 
     * @return текущее использование памяти в байтах
     */
    public long getCurrentMemoryUsage() {
        return currentMemoryUsage;
    }
    
    /**
     * Получить текущее использование памяти в мегабайтах.
     * 
     * @return текущее использование памяти в мегабайтах
     */
    public double getCurrentMemoryUsageMb() {
        return currentMemoryUsage / 1_000_000.0;
    }
    
    /**
     * Получить количество отслеживаемых операций.
     * 
     * @return количество отслеживаемых операций
     */
    public int getMonitoredOperations() {
        return monitoredOperations;
    }
    
    /**
     * Получить краткую статистику.
     * 
     * @return краткая статистика
     */
    public String getShortStats() {
        return String.format(
            "Ops: %d, Avg: %.2f ms, Memory: %.2f/%.2f MB (current/peak), Monitored: %d",
            totalOperations,
            getAverageExecutionTimeMs(),
            getCurrentMemoryUsageMb(),
            getPeakMemoryUsageMb(),
            monitoredOperations
        );
    }
    
    /**
     * Получить детальную статистику.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        return String.format(
            "Total Operations: %d\n" +
            "Average Execution Time: %d ns (%.2f ms)\n" +
            "Peak Memory Usage: %d bytes (%.2f MB)\n" +
            "Current Memory Usage: %d bytes (%.2f MB)\n" +
            "Monitored Operations: %d",
            totalOperations,
            averageExecutionTime, getAverageExecutionTimeMs(),
            peakMemoryUsage, getPeakMemoryUsageMb(),
            currentMemoryUsage, getCurrentMemoryUsageMb(),
            monitoredOperations
        );
    }
    
    @Override
    public String toString() {
        return getShortStats();
    }
}
