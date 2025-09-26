package net.sf.l2j.botmanager.web.dto;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO для передачи статистики бота через REST API
 */
public class BotStatisticsDTO {
    
    private int botId;
    private String botName;
    private BotType botType;
    private LocalDateTime createdTime;
    private long totalUptime;
    private long totalActions;
    private long totalKills;
    private long totalDeaths;
    private long totalExperience;
    private long totalGold;
    private Map<BehaviorType, Long> behaviorUsage;
    private Map<String, Long> actionCounts;
    private double averagePerformance;
    private int currentLevel;
    private long lastActivity;
    private double killDeathRatio;
    private double actionsPerHour;
    
    /**
     * Конструктор по умолчанию
     */
    public BotStatisticsDTO() {
    }
    
    /**
     * Конструктор с параметрами
     */
    public BotStatisticsDTO(int botId, String botName, BotType botType, LocalDateTime createdTime,
                           long totalUptime, long totalActions, long totalKills, long totalDeaths,
                           long totalExperience, long totalGold, Map<BehaviorType, Long> behaviorUsage,
                           Map<String, Long> actionCounts, double averagePerformance, int currentLevel,
                           long lastActivity, double killDeathRatio, double actionsPerHour) {
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
        this.killDeathRatio = killDeathRatio;
        this.actionsPerHour = actionsPerHour;
    }
    
    // Getters and Setters
    public int getBotId() {
        return botId;
    }
    
    public void setBotId(int botId) {
        this.botId = botId;
    }
    
    public String getBotName() {
        return botName;
    }
    
    public void setBotName(String botName) {
        this.botName = botName;
    }
    
    public BotType getBotType() {
        return botType;
    }
    
    public void setBotType(BotType botType) {
        this.botType = botType;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public long getTotalUptime() {
        return totalUptime;
    }
    
    public void setTotalUptime(long totalUptime) {
        this.totalUptime = totalUptime;
    }
    
    public long getTotalActions() {
        return totalActions;
    }
    
    public void setTotalActions(long totalActions) {
        this.totalActions = totalActions;
    }
    
    public long getTotalKills() {
        return totalKills;
    }
    
    public void setTotalKills(long totalKills) {
        this.totalKills = totalKills;
    }
    
    public long getTotalDeaths() {
        return totalDeaths;
    }
    
    public void setTotalDeaths(long totalDeaths) {
        this.totalDeaths = totalDeaths;
    }
    
    public long getTotalExperience() {
        return totalExperience;
    }
    
    public void setTotalExperience(long totalExperience) {
        this.totalExperience = totalExperience;
    }
    
    public long getTotalGold() {
        return totalGold;
    }
    
    public void setTotalGold(long totalGold) {
        this.totalGold = totalGold;
    }
    
    public Map<BehaviorType, Long> getBehaviorUsage() {
        return behaviorUsage;
    }
    
    public void setBehaviorUsage(Map<BehaviorType, Long> behaviorUsage) {
        this.behaviorUsage = behaviorUsage;
    }
    
    public Map<String, Long> getActionCounts() {
        return actionCounts;
    }
    
    public void setActionCounts(Map<String, Long> actionCounts) {
        this.actionCounts = actionCounts;
    }
    
    public double getAveragePerformance() {
        return averagePerformance;
    }
    
    public void setAveragePerformance(double averagePerformance) {
        this.averagePerformance = averagePerformance;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
    
    public long getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public double getKillDeathRatio() {
        return killDeathRatio;
    }
    
    public void setKillDeathRatio(double killDeathRatio) {
        this.killDeathRatio = killDeathRatio;
    }
    
    public double getActionsPerHour() {
        return actionsPerHour;
    }
    
    public void setActionsPerHour(double actionsPerHour) {
        this.actionsPerHour = actionsPerHour;
    }
    
    /**
     * Получить краткую статистику
     * @return краткая строка статистики
     */
    public String getShortStats() {
        return String.format("Bot[%d] %s - Actions: %d | K/D: %.2f | Performance: %.2f%%",
                botId, botName, totalActions, killDeathRatio, averagePerformance);
    }
    
    /**
     * Получить детальную статистику
     * @return детальная строка статистики
     */
    public String getDetailedStats() {
        return String.format("=== Bot Statistics [%d] %s ===\n" +
                "Type: %s | Level: %d | Created: %s\n" +
                "Uptime: %d ms | Actions: %d | Actions/Hour: %.2f\n" +
                "Kills: %d | Deaths: %d | K/D: %.2f\n" +
                "Experience: %d | Gold: %d\n" +
                "Performance: %.2f%% | Last Activity: %d",
                botId, botName, botType, currentLevel, createdTime,
                totalUptime, totalActions, actionsPerHour,
                totalKills, totalDeaths, killDeathRatio,
                totalExperience, totalGold, averagePerformance, lastActivity);
    }
}
