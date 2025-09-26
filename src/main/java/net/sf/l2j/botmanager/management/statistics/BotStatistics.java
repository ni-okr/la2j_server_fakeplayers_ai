package net.sf.l2j.botmanager.management.statistics;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Класс для хранения статистики бота
 * Содержит информацию о производительности и активности бота
 */
public class BotStatistics {
    
    private final int botId;
    private final String botName;
    private final BotType botType;
    private final LocalDateTime createdTime;
    private final long totalUptime;
    private final long totalActions;
    private final long totalKills;
    private final long totalDeaths;
    private final long totalExperience;
    private final long totalGold;
    private final Map<BehaviorType, Long> behaviorUsage;
    private final Map<String, Long> actionCounts;
    private final double averagePerformance;
    private final int currentLevel;
    private final long lastActivity;
    
    /**
     * Конструктор
     */
    public BotStatistics(int botId, String botName, BotType botType, LocalDateTime createdTime,
                        long totalUptime, long totalActions, long totalKills, long totalDeaths,
                        long totalExperience, long totalGold, Map<BehaviorType, Long> behaviorUsage,
                        Map<String, Long> actionCounts, double averagePerformance, int currentLevel,
                        long lastActivity) {
        this.botId = botId;
        this.botName = botName;
        this.botType = botType;
        this.createdTime = createdTime;
        this.totalUptime = totalUptime;
        this.totalActions = totalActions;
        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.totalExperience = totalExperience;
        this.totalGold = totalGold;
        this.behaviorUsage = behaviorUsage;
        this.actionCounts = actionCounts;
        this.averagePerformance = averagePerformance;
        this.currentLevel = currentLevel;
        this.lastActivity = lastActivity;
    }
    
    // Getters
    public int getBotId() {
        return botId;
    }
    
    public String getBotName() {
        return botName;
    }
    
    public BotType getBotType() {
        return botType;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public long getTotalUptime() {
        return totalUptime;
    }
    
    public long getTotalActions() {
        return totalActions;
    }
    
    public long getTotalKills() {
        return totalKills;
    }
    
    public long getTotalDeaths() {
        return totalDeaths;
    }
    
    public long getTotalExperience() {
        return totalExperience;
    }
    
    public long getTotalGold() {
        return totalGold;
    }
    
    public Map<BehaviorType, Long> getBehaviorUsage() {
        return behaviorUsage;
    }
    
    public Map<String, Long> getActionCounts() {
        return actionCounts;
    }
    
    public double getAveragePerformance() {
        return averagePerformance;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public long getLastActivity() {
        return lastActivity;
    }
    
    /**
     * Получить K/D соотношение
     * @return K/D соотношение
     */
    public double getKillDeathRatio() {
        if (totalDeaths == 0) {
            return totalKills > 0 ? Double.MAX_VALUE : 0.0;
        }
        return (double) totalKills / totalDeaths;
    }
    
    /**
     * Получить среднее количество действий в час
     * @return среднее количество действий в час
     */
    public double getActionsPerHour() {
        if (totalUptime == 0) {
            return 0.0;
        }
        return (double) totalActions / (totalUptime / 3600000.0);
    }
    
    /**
     * Получить строковое представление статистики
     * @return строка статистики
     */
    @Override
    public String toString() {
        return String.format("Bot[%d] %s (%s) - Level: %d | Actions: %d | Kills: %d | Deaths: %d | K/D: %.2f | Performance: %.2f%%",
                botId, botName, botType, currentLevel, totalActions, totalKills, totalDeaths, 
                getKillDeathRatio(), averagePerformance);
    }
    
    /**
     * Получить детальную статистику
     * @return детальная строка статистики
     */
    public String getDetailedStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Bot Statistics [%d] %s ===\n", botId, botName));
        sb.append(String.format("Type: %s | Level: %d | Created: %s\n", botType, currentLevel, createdTime));
        sb.append(String.format("Uptime: %d ms | Actions: %d | Actions/Hour: %.2f\n", 
                totalUptime, totalActions, getActionsPerHour()));
        sb.append(String.format("Kills: %d | Deaths: %d | K/D: %.2f\n", 
                totalKills, totalDeaths, getKillDeathRatio()));
        sb.append(String.format("Experience: %d | Gold: %d\n", totalExperience, totalGold));
        sb.append(String.format("Performance: %.2f%% | Last Activity: %d\n", averagePerformance, lastActivity));
        
        if (behaviorUsage != null && !behaviorUsage.isEmpty()) {
            sb.append("Behavior Usage:\n");
            behaviorUsage.forEach((behavior, count) -> 
                sb.append(String.format("  %s: %d\n", behavior, count)));
        }
        
        if (actionCounts != null && !actionCounts.isEmpty()) {
            sb.append("Action Counts:\n");
            actionCounts.forEach((action, count) -> 
                sb.append(String.format("  %s: %d\n", action, count)));
        }
        
        return sb.toString();
    }
}
