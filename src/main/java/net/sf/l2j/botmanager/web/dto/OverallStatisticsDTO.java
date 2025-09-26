package net.sf.l2j.botmanager.web.dto;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;

import java.util.Map;

/**
 * DTO для общей статистики системы через REST API
 */
public class OverallStatisticsDTO {
    
    private int totalBots;
    private int activeBots;
    private int inactiveBots;
    private long systemUptime;
    private boolean systemInitialized;
    private Map<BotType, Integer> botTypeStatistics;
    private Map<BehaviorType, Integer> behaviorStatistics;
    private Map<String, Object> additionalStats;
    private long timestamp;
    
    /**
     * Конструктор по умолчанию
     */
    public OverallStatisticsDTO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Конструктор с параметрами
     */
    public OverallStatisticsDTO(int totalBots, int activeBots, int inactiveBots, long systemUptime,
                               boolean systemInitialized, Map<BotType, Integer> botTypeStatistics,
                               Map<BehaviorType, Integer> behaviorStatistics, Map<String, Object> additionalStats) {
        this.totalBots = totalBots;
        this.activeBots = activeBots;
        this.inactiveBots = inactiveBots;
        this.systemUptime = systemUptime;
        this.systemInitialized = systemInitialized;
        this.botTypeStatistics = botTypeStatistics;
        this.behaviorStatistics = behaviorStatistics;
        this.additionalStats = additionalStats;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getTotalBots() {
        return totalBots;
    }
    
    public void setTotalBots(int totalBots) {
        this.totalBots = totalBots;
    }
    
    public int getActiveBots() {
        return activeBots;
    }
    
    public void setActiveBots(int activeBots) {
        this.activeBots = activeBots;
    }
    
    public int getInactiveBots() {
        return inactiveBots;
    }
    
    public void setInactiveBots(int inactiveBots) {
        this.inactiveBots = inactiveBots;
    }
    
    public long getSystemUptime() {
        return systemUptime;
    }
    
    public void setSystemUptime(long systemUptime) {
        this.systemUptime = systemUptime;
    }
    
    public boolean isSystemInitialized() {
        return systemInitialized;
    }
    
    public void setSystemInitialized(boolean systemInitialized) {
        this.systemInitialized = systemInitialized;
    }
    
    public Map<BotType, Integer> getBotTypeStatistics() {
        return botTypeStatistics;
    }
    
    public void setBotTypeStatistics(Map<BotType, Integer> botTypeStatistics) {
        this.botTypeStatistics = botTypeStatistics;
    }
    
    public Map<BehaviorType, Integer> getBehaviorStatistics() {
        return behaviorStatistics;
    }
    
    public void setBehaviorStatistics(Map<BehaviorType, Integer> behaviorStatistics) {
        this.behaviorStatistics = behaviorStatistics;
    }
    
    public Map<String, Object> getAdditionalStats() {
        return additionalStats;
    }
    
    public void setAdditionalStats(Map<String, Object> additionalStats) {
        this.additionalStats = additionalStats;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Получить процент активных ботов
     * @return процент активных ботов
     */
    public double getActiveBotsPercentage() {
        if (totalBots == 0) {
            return 0.0;
        }
        return (double) activeBots / totalBots * 100.0;
    }
    
    /**
     * Получить краткую статистику
     * @return краткая строка статистики
     */
    public String getShortStats() {
        return String.format("Total: %d | Active: %d (%.1f%%) | Inactive: %d | Uptime: %dms",
                totalBots, activeBots, getActiveBotsPercentage(), inactiveBots, systemUptime);
    }
    
    /**
     * Получить детальную статистику
     * @return детальная строка статистики
     */
    public String getDetailedStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Overall System Statistics ===\n");
        sb.append(String.format("Total Bots: %d | Active: %d (%.1f%%) | Inactive: %d\n", 
                totalBots, activeBots, getActiveBotsPercentage(), inactiveBots));
        sb.append(String.format("System Uptime: %d ms | Initialized: %s\n", 
                systemUptime, systemInitialized ? "Yes" : "No"));
        
        if (botTypeStatistics != null && !botTypeStatistics.isEmpty()) {
            sb.append("Bot Types:\n");
            botTypeStatistics.forEach((type, count) -> 
                sb.append(String.format("  %s: %d\n", type, count)));
        }
        
        if (behaviorStatistics != null && !behaviorStatistics.isEmpty()) {
            sb.append("Behaviors:\n");
            behaviorStatistics.forEach((behavior, count) -> 
                sb.append(String.format("  %s: %d\n", behavior, count)));
        }
        
        return sb.toString();
    }
}
