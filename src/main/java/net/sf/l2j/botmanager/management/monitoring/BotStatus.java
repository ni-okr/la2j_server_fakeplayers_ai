package net.sf.l2j.botmanager.management.monitoring;

import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;

import java.time.LocalDateTime;

/**
 * Класс для хранения статуса бота
 * Содержит информацию о текущем состоянии бота
 */
public class BotStatus {
    
    private final int botId;
    private final String botName;
    private final BotType botType;
    private final BotState currentState;
    private final BehaviorType currentBehavior;
    private final boolean isActive;
    private final LocalDateTime lastUpdate;
    private final String location;
    private final int level;
    private final double healthPercentage;
    private final double manaPercentage;
    private final String lastAction;
    private final long uptime;
    
    /**
     * Конструктор
     */
    public BotStatus(int botId, String botName, BotType botType, BotState currentState, 
                    BehaviorType currentBehavior, boolean isActive, LocalDateTime lastUpdate,
                    String location, int level, double healthPercentage, double manaPercentage,
                    String lastAction, long uptime) {
        this.botId = botId;
        this.botName = botName;
        this.botType = botType;
        this.currentState = currentState;
        this.currentBehavior = currentBehavior;
        this.isActive = isActive;
        this.lastUpdate = lastUpdate;
        this.location = location;
        this.level = level;
        this.healthPercentage = healthPercentage;
        this.manaPercentage = manaPercentage;
        this.lastAction = lastAction;
        this.uptime = uptime;
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
    
    public BotState getCurrentState() {
        return currentState;
    }
    
    public BehaviorType getCurrentBehavior() {
        return currentBehavior;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public int getLevel() {
        return level;
    }
    
    public double getHealthPercentage() {
        return healthPercentage;
    }
    
    public double getManaPercentage() {
        return manaPercentage;
    }
    
    public String getLastAction() {
        return lastAction;
    }
    
    public long getUptime() {
        return uptime;
    }
    
    /**
     * Получить строковое представление статуса
     * @return строка статуса
     */
    @Override
    public String toString() {
        return String.format("Bot[%d] %s (%s) - %s | %s | %s | Level: %d | HP: %.1f%% | MP: %.1f%% | Uptime: %dms",
                botId, botName, botType, currentState, currentBehavior, 
                isActive ? "Active" : "Inactive", level, healthPercentage, 
                manaPercentage, uptime);
    }
    
    /**
     * Получить краткую информацию о статусе
     * @return краткая строка статуса
     */
    public String getShortStatus() {
        return String.format("[%d] %s - %s | %s", 
                botId, botName, currentState, isActive ? "Active" : "Inactive");
    }
}
