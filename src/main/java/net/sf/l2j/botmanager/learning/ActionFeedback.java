package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.actions.ActionType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Статистика обратной связи для конкретного типа действия.
 */
public class ActionFeedback {
    private final ActionType actionType;
    private final AtomicLong totalExecutions = new AtomicLong(0);
    private final AtomicLong successfulExecutions = new AtomicLong(0);
    private final AtomicLong failedExecutions = new AtomicLong(0);
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    private final AtomicLong minExecutionTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxExecutionTime = new AtomicLong(0);
    private final AtomicReference<Double> averageExecutionTime = new AtomicReference<>(0.0);
    
    // Детали выполнения
    private final List<String> recentDetails = new CopyOnWriteArrayList<>();
    private final int maxDetails = 100;
    
    public ActionFeedback(ActionType actionType) {
        this.actionType = actionType;
    }
    
    /**
     * Записать выполнение действия.
     */
    public void recordExecution(boolean success, long executionTime, String details) {
        totalExecutions.incrementAndGet();
        
        if (success) {
            successfulExecutions.incrementAndGet();
        } else {
            failedExecutions.incrementAndGet();
        }
        
        // Обновление времени выполнения
        totalExecutionTime.addAndGet(executionTime);
        updateMinMaxTime(executionTime);
        updateAverageTime();
        
        // Добавление деталей
        addDetails(details);
    }
    
    /**
     * Обновить минимальное и максимальное время выполнения.
     */
    private void updateMinMaxTime(long executionTime) {
        long currentMin = minExecutionTime.get();
        while (executionTime < currentMin && !minExecutionTime.compareAndSet(currentMin, executionTime)) {
            currentMin = minExecutionTime.get();
        }
        
        long currentMax = maxExecutionTime.get();
        while (executionTime > currentMax && !maxExecutionTime.compareAndSet(currentMax, executionTime)) {
            currentMax = maxExecutionTime.get();
        }
    }
    
    /**
     * Обновить среднее время выполнения.
     */
    private void updateAverageTime() {
        long total = totalExecutions.get();
        if (total > 0) {
            double average = (double) totalExecutionTime.get() / total;
            averageExecutionTime.set(average);
        }
    }
    
    /**
     * Добавить детали выполнения.
     */
    private void addDetails(String details) {
        if (details != null && !details.isEmpty()) {
            recentDetails.add(details);
            
            // Ограничение количества деталей
            while (recentDetails.size() > maxDetails) {
                recentDetails.remove(0);
            }
        }
    }
    
    /**
     * Получить коэффициент успешности.
     */
    public double getSuccessRate() {
        long total = totalExecutions.get();
        if (total == 0) return 0.0;
        return (double) successfulExecutions.get() / total * 100.0;
    }
    
    /**
     * Получить коэффициент неудач.
     */
    public double getFailureRate() {
        long total = totalExecutions.get();
        if (total == 0) return 0.0;
        return (double) failedExecutions.get() / total * 100.0;
    }
    
    /**
     * Получить среднее время выполнения.
     */
    public double getAverageExecutionTime() {
        return averageExecutionTime.get();
    }
    
    /**
     * Получить минимальное время выполнения.
     */
    public long getMinExecutionTime() {
        long min = minExecutionTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }
    
    /**
     * Получить максимальное время выполнения.
     */
    public long getMaxExecutionTime() {
        return maxExecutionTime.get();
    }
    
    /**
     * Получить общее время выполнения.
     */
    public long getTotalExecutionTime() {
        return totalExecutionTime.get();
    }
    
    /**
     * Получить количество выполнений.
     */
    public long getTotalExecutions() {
        return totalExecutions.get();
    }
    
    /**
     * Получить количество успешных выполнений.
     */
    public long getSuccessfulExecutions() {
        return successfulExecutions.get();
    }
    
    /**
     * Получить количество неудачных выполнений.
     */
    public long getFailedExecutions() {
        return failedExecutions.get();
    }
    
    /**
     * Получить недавние детали.
     */
    public List<String> getRecentDetails() {
        return new ArrayList<>(recentDetails);
    }
    
    /**
     * Получить статистику в виде строки.
     */
    public String getStats() {
        return String.format(
            "Action: %s | Executions: %d | Success: %.2f%% | Avg Time: %.2fms | Min: %dms | Max: %dms",
            actionType,
            totalExecutions.get(),
            getSuccessRate(),
            getAverageExecutionTime(),
            getMinExecutionTime(),
            getMaxExecutionTime()
        );
    }
    
    /**
     * Получить детальную статистику.
     */
    public String getDetailedStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Action Feedback: ").append(actionType).append(" ===\n");
        stats.append("Total Executions: ").append(totalExecutions.get()).append("\n");
        stats.append("Successful: ").append(successfulExecutions.get()).append("\n");
        stats.append("Failed: ").append(failedExecutions.get()).append("\n");
        stats.append("Success Rate: ").append(String.format("%.2f", getSuccessRate())).append("%\n");
        stats.append("Failure Rate: ").append(String.format("%.2f", getFailureRate())).append("%\n");
        stats.append("Average Time: ").append(String.format("%.2f", getAverageExecutionTime())).append("ms\n");
        stats.append("Min Time: ").append(getMinExecutionTime()).append("ms\n");
        stats.append("Max Time: ").append(getMaxExecutionTime()).append("ms\n");
        stats.append("Total Time: ").append(getTotalExecutionTime()).append("ms\n");
        
        if (!recentDetails.isEmpty()) {
            stats.append("Recent Details:\n");
            for (String detail : recentDetails) {
                stats.append("  - ").append(detail).append("\n");
            }
        }
        
        return stats.toString();
    }
    
    /**
     * Сбросить статистику.
     */
    public void reset() {
        totalExecutions.set(0);
        successfulExecutions.set(0);
        failedExecutions.set(0);
        totalExecutionTime.set(0);
        minExecutionTime.set(Long.MAX_VALUE);
        maxExecutionTime.set(0);
        averageExecutionTime.set(0.0);
        recentDetails.clear();
    }
}
