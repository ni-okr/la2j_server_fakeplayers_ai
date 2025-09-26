package net.sf.l2j.botmanager.performance;

/**
 * Статистика пула потоков.
 * 
 * Содержит метрики производительности пула потоков:
 * размеры пула, количество задач, использование и производительность.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class ThreadPoolStats {
    
    private final int corePoolSize;
    private final int maxPoolSize;
    private final int currentPoolSize;
    private final int activeThreads;
    private final long totalTasks;
    private final long completedTasks;
    private final int queueSize;
    private final long submittedTasks;
    private final long rejectedTasks;
    
    /**
     * Конструктор.
     * 
     * @param corePoolSize размер основного пула
     * @param maxPoolSize максимальный размер пула
     * @param currentPoolSize текущий размер пула
     * @param activeThreads количество активных потоков
     * @param totalTasks общее количество задач
     * @param completedTasks количество завершенных задач
     * @param queueSize размер очереди
     * @param submittedTasks количество отправленных задач
     * @param rejectedTasks количество отклоненных задач
     */
    public ThreadPoolStats(int corePoolSize, int maxPoolSize, int currentPoolSize,
                          int activeThreads, long totalTasks, long completedTasks,
                          int queueSize, long submittedTasks, long rejectedTasks) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.currentPoolSize = currentPoolSize;
        this.activeThreads = activeThreads;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.queueSize = queueSize;
        this.submittedTasks = submittedTasks;
        this.rejectedTasks = rejectedTasks;
    }
    
    /**
     * Получить размер основного пула.
     * 
     * @return размер основного пула
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }
    
    /**
     * Получить максимальный размер пула.
     * 
     * @return максимальный размер пула
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    /**
     * Получить текущий размер пула.
     * 
     * @return текущий размер пула
     */
    public int getCurrentPoolSize() {
        return currentPoolSize;
    }
    
    /**
     * Получить количество активных потоков.
     * 
     * @return количество активных потоков
     */
    public int getActiveThreads() {
        return activeThreads;
    }
    
    /**
     * Получить общее количество задач.
     * 
     * @return общее количество задач
     */
    public long getTotalTasks() {
        return totalTasks;
    }
    
    /**
     * Получить количество завершенных задач.
     * 
     * @return количество завершенных задач
     */
    public long getCompletedTasks() {
        return completedTasks;
    }
    
    /**
     * Получить размер очереди.
     * 
     * @return размер очереди
     */
    public int getQueueSize() {
        return queueSize;
    }
    
    /**
     * Получить количество отправленных задач.
     * 
     * @return количество отправленных задач
     */
    public long getSubmittedTasks() {
        return submittedTasks;
    }
    
    /**
     * Получить количество отклоненных задач.
     * 
     * @return количество отклоненных задач
     */
    public long getRejectedTasks() {
        return rejectedTasks;
    }
    
    /**
     * Получить коэффициент использования пула.
     * 
     * @return коэффициент использования (0.0 - 1.0)
     */
    public double getUtilization() {
        if (maxPoolSize == 0) {
            return 0.0;
        }
        return (double) activeThreads / maxPoolSize;
    }
    
    /**
     * Получить коэффициент использования в процентах.
     * 
     * @return коэффициент использования в процентах
     */
    public double getUtilizationPercent() {
        return getUtilization() * 100.0;
    }
    
    /**
     * Получить коэффициент завершения задач.
     * 
     * @return коэффициент завершения (0.0 - 1.0)
     */
    public double getCompletionRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks;
    }
    
    /**
     * Получить коэффициент завершения в процентах.
     * 
     * @return коэффициент завершения в процентах
     */
    public double getCompletionRatePercent() {
        return getCompletionRate() * 100.0;
    }
    
    /**
     * Получить коэффициент отклонения задач.
     * 
     * @return коэффициент отклонения (0.0 - 1.0)
     */
    public double getRejectionRate() {
        if (submittedTasks == 0) {
            return 0.0;
        }
        return (double) rejectedTasks / submittedTasks;
    }
    
    /**
     * Получить коэффициент отклонения в процентах.
     * 
     * @return коэффициент отклонения в процентах
     */
    public double getRejectionRatePercent() {
        return getRejectionRate() * 100.0;
    }
    
    /**
     * Получить краткую статистику.
     * 
     * @return краткая статистика
     */
    public String getShortStats() {
        return String.format("Pool: %d/%d threads (%d active), %d/%d tasks (%.1f%% util, %.1f%% complete)",
            currentPoolSize, maxPoolSize, activeThreads,
            completedTasks, totalTasks,
            getUtilizationPercent(), getCompletionRatePercent());
    }
    
    /**
     * Получить детальную статистику.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        return String.format(
            "Thread Pool Statistics:\n" +
            "  Core Pool Size: %d\n" +
            "  Max Pool Size: %d\n" +
            "  Current Pool Size: %d\n" +
            "  Active Threads: %d\n" +
            "  Total Tasks: %d\n" +
            "  Completed Tasks: %d\n" +
            "  Queue Size: %d\n" +
            "  Submitted Tasks: %d\n" +
            "  Rejected Tasks: %d\n" +
            "  Utilization: %.2f%%\n" +
            "  Completion Rate: %.2f%%\n" +
            "  Rejection Rate: %.2f%%",
            corePoolSize, maxPoolSize, currentPoolSize, activeThreads,
            totalTasks, completedTasks, queueSize, submittedTasks, rejectedTasks,
            getUtilizationPercent(), getCompletionRatePercent(), getRejectionRatePercent()
        );
    }
    
    @Override
    public String toString() {
        return getShortStats();
    }
}
