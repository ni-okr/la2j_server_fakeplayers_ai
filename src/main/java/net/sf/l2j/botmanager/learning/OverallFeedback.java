package net.sf.l2j.botmanager.learning;

/**
 * Общая статистика обратной связи системы обучения.
 * Содержит агрегированные данные о всех действиях и поведениях ботов.
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
public class OverallFeedback {
    
    /** Общее количество выполненных действий */
    private final long totalActions;
    
    /** Общее количество выполненных поведений */
    private final long totalBehaviors;
    
    /** Количество успешных действий */
    private final long successfulActions;
    
    /** Количество успешных поведений */
    private final long successfulBehaviors;
    
    /**
     * Конструктор общей статистики обратной связи.
     * 
     * @param totalActions общее количество действий
     * @param totalBehaviors общее количество поведений
     * @param successfulActions количество успешных действий
     * @param successfulBehaviors количество успешных поведений
     */
    public OverallFeedback(long totalActions, long totalBehaviors, 
                          long successfulActions, long successfulBehaviors) {
        this.totalActions = Math.max(0, totalActions);
        this.totalBehaviors = Math.max(0, totalBehaviors);
        this.successfulActions = Math.max(0, Math.min(successfulActions, totalActions));
        this.successfulBehaviors = Math.max(0, Math.min(successfulBehaviors, totalBehaviors));
    }
    
    /**
     * Получить общее количество действий.
     * 
     * @return общее количество действий
     */
    public long getTotalActions() {
        return totalActions;
    }
    
    /**
     * Получить общее количество поведений.
     * 
     * @return общее количество поведений
     */
    public long getTotalBehaviors() {
        return totalBehaviors;
    }
    
    /**
     * Получить количество успешных действий.
     * 
     * @return количество успешных действий
     */
    public long getSuccessfulActions() {
        return successfulActions;
    }
    
    /**
     * Получить количество успешных поведений.
     * 
     * @return количество успешных поведений
     */
    public long getSuccessfulBehaviors() {
        return successfulBehaviors;
    }
    
    /**
     * Получить количество неудачных действий.
     * 
     * @return количество неудачных действий
     */
    public long getFailedActions() {
        return totalActions - successfulActions;
    }
    
    /**
     * Получить количество неудачных поведений.
     * 
     * @return количество неудачных поведений
     */
    public long getFailedBehaviors() {
        return totalBehaviors - successfulBehaviors;
    }
    
    /**
     * Получить коэффициент успешности действий в процентах.
     * 
     * @return коэффициент успешности действий (0.0 - 100.0)
     */
    public double getActionSuccessRate() {
        if (totalActions == 0) return 0.0;
        return (double) successfulActions / totalActions * 100.0;
    }
    
    /**
     * Получить коэффициент успешности поведений в процентах.
     * 
     * @return коэффициент успешности поведений (0.0 - 100.0)
     */
    public double getBehaviorSuccessRate() {
        if (totalBehaviors == 0) return 0.0;
        return (double) successfulBehaviors / totalBehaviors * 100.0;
    }
    
    /**
     * Получить коэффициент неудач действий в процентах.
     * 
     * @return коэффициент неудач действий (0.0 - 100.0)
     */
    public double getActionFailureRate() {
        if (totalActions == 0) return 0.0;
        return (double) getFailedActions() / totalActions * 100.0;
    }
    
    /**
     * Получить коэффициент неудач поведений в процентах.
     * 
     * @return коэффициент неудач поведений (0.0 - 100.0)
     */
    public double getBehaviorFailureRate() {
        if (totalBehaviors == 0) return 0.0;
        return (double) getFailedBehaviors() / totalBehaviors * 100.0;
    }
    
    /**
     * Получить общий коэффициент успешности системы.
     * Рассчитывается как среднее арифметическое коэффициентов успешности действий и поведений.
     * 
     * @return общий коэффициент успешности (0.0 - 100.0)
     */
    public double getOverallSuccessRate() {
        if (totalActions == 0 && totalBehaviors == 0) return 0.0;
        if (totalActions == 0) return getBehaviorSuccessRate();
        if (totalBehaviors == 0) return getActionSuccessRate();
        
        return (getActionSuccessRate() + getBehaviorSuccessRate()) / 2.0;
    }
    
    /**
     * Проверить, есть ли данные для анализа.
     * 
     * @return true, если есть хотя бы одно действие или поведение
     */
    public boolean hasData() {
        return totalActions > 0 || totalBehaviors > 0;
    }
    
    /**
     * Проверить, является ли система эффективной.
     * Система считается эффективной, если общий коэффициент успешности больше 70%.
     * 
     * @return true, если система эффективна
     */
    public boolean isEffective() {
        return getOverallSuccessRate() > 70.0;
    }
    
    /**
     * Проверить, нуждается ли система в улучшении.
     * Система нуждается в улучшении, если общий коэффициент успешности меньше 50%.
     * 
     * @return true, если система нуждается в улучшении
     */
    public boolean needsImprovement() {
        return getOverallSuccessRate() < 50.0;
    }
    
    /**
     * Получить общее количество операций (действий + поведений).
     * 
     * @return общее количество операций
     */
    public long getTotalOperations() {
        return totalActions + totalBehaviors;
    }
    
    /**
     * Получить общее количество успешных операций.
     * 
     * @return общее количество успешных операций
     */
    public long getTotalSuccessfulOperations() {
        return successfulActions + successfulBehaviors;
    }
    
    /**
     * Получить общий коэффициент успешности операций.
     * 
     * @return общий коэффициент успешности операций (0.0 - 100.0)
     */
    public double getTotalSuccessRate() {
        long total = getTotalOperations();
        if (total == 0) return 0.0;
        return (double) getTotalSuccessfulOperations() / total * 100.0;
    }
    
    /**
     * Получить детальную статистику в виде строки.
     * 
     * @return детальная статистика
     */
    public String getDetailedStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Overall Feedback Statistics ===\n");
        stats.append("Total Actions: ").append(totalActions).append("\n");
        stats.append("Successful Actions: ").append(successfulActions).append("\n");
        stats.append("Failed Actions: ").append(getFailedActions()).append("\n");
        stats.append("Action Success Rate: ").append(String.format("%.2f", getActionSuccessRate())).append("%\n");
        stats.append("Action Failure Rate: ").append(String.format("%.2f", getActionFailureRate())).append("%\n");
        stats.append("Total Behaviors: ").append(totalBehaviors).append("\n");
        stats.append("Successful Behaviors: ").append(successfulBehaviors).append("\n");
        stats.append("Failed Behaviors: ").append(getFailedBehaviors()).append("\n");
        stats.append("Behavior Success Rate: ").append(String.format("%.2f", getBehaviorSuccessRate())).append("%\n");
        stats.append("Behavior Failure Rate: ").append(String.format("%.2f", getBehaviorFailureRate())).append("%\n");
        stats.append("Overall Success Rate: ").append(String.format("%.2f", getOverallSuccessRate())).append("%\n");
        stats.append("Total Operations: ").append(getTotalOperations()).append("\n");
        stats.append("Total Success Rate: ").append(String.format("%.2f", getTotalSuccessRate())).append("%\n");
        stats.append("System Status: ").append(getSystemStatus()).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Получить краткую статистику в виде строки.
     * 
     * @return краткая статистика
     */
    public String getSummary() {
        return String.format(
            "Overall: %d actions (%.1f%% success), %d behaviors (%.1f%% success), Total: %.1f%% success",
            totalActions, getActionSuccessRate(),
            totalBehaviors, getBehaviorSuccessRate(),
            getOverallSuccessRate()
        );
    }
    
    /**
     * Получить статус системы на основе эффективности.
     * 
     * @return статус системы
     */
    public String getSystemStatus() {
        if (!hasData()) return "No Data";
        if (isEffective()) return "Effective";
        if (needsImprovement()) return "Needs Improvement";
        return "Moderate";
    }
    
    /**
     * Создать копию статистики с добавленными данными.
     * 
     * @param additionalActions дополнительные действия
     * @param additionalBehaviors дополнительные поведения
     * @param additionalSuccessfulActions дополнительные успешные действия
     * @param additionalSuccessfulBehaviors дополнительные успешные поведения
     * @return новая копия статистики
     */
    public OverallFeedback add(long additionalActions, long additionalBehaviors,
                              long additionalSuccessfulActions, long additionalSuccessfulBehaviors) {
        return new OverallFeedback(
            totalActions + additionalActions,
            totalBehaviors + additionalBehaviors,
            successfulActions + additionalSuccessfulActions,
            successfulBehaviors + additionalSuccessfulBehaviors
        );
    }
    
    /**
     * Получить строковое представление статистики.
     * 
     * @return строковое представление
     */
    @Override
    public String toString() {
        return String.format(
            "OverallFeedback{actions=%d/%d (%.1f%%), behaviors=%d/%d (%.1f%%), overall=%.1f%%}",
            successfulActions, totalActions, getActionSuccessRate(),
            successfulBehaviors, totalBehaviors, getBehaviorSuccessRate(),
            getOverallSuccessRate()
        );
    }
    
    /**
     * Проверить равенство статистик.
     * 
     * @param obj объект для сравнения
     * @return true, если статистики равны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        OverallFeedback that = (OverallFeedback) obj;
        return totalActions == that.totalActions &&
               totalBehaviors == that.totalBehaviors &&
               successfulActions == that.successfulActions &&
               successfulBehaviors == that.successfulBehaviors;
    }
    
    /**
     * Получить хеш-код статистики.
     * 
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        int result = Long.hashCode(totalActions);
        result = 31 * result + Long.hashCode(totalBehaviors);
        result = 31 * result + Long.hashCode(successfulActions);
        result = 31 * result + Long.hashCode(successfulBehaviors);
        return result;
    }
}
