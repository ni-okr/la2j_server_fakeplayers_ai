package net.sf.l2j.botmanager.web.dto;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о боте через REST API
 */
public class BotDTO {
    
    private int id;
    private String name;
    private BotType type;
    private int level;
    private BehaviorType currentBehavior;
    private boolean active;
    private String status;
    private String location;
    private double healthPercentage;
    private double manaPercentage;
    private String lastAction;
    private long uptime;
    private LocalDateTime createdTime;
    private LocalDateTime lastUpdate;
    
    /**
     * Конструктор по умолчанию
     */
    public BotDTO() {
    }
    
    /**
     * Конструктор с параметрами
     */
    public BotDTO(int id, String name, BotType type, int level, BehaviorType currentBehavior, 
                  boolean active, String status, String location, double healthPercentage, 
                  double manaPercentage, String lastAction, long uptime, 
                  LocalDateTime createdTime, LocalDateTime lastUpdate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.currentBehavior = currentBehavior;
        this.active = active;
        this.status = status;
        this.location = location;
        this.healthPercentage = healthPercentage;
        this.manaPercentage = manaPercentage;
        this.lastAction = lastAction;
        this.uptime = uptime;
        this.createdTime = createdTime;
        this.lastUpdate = lastUpdate;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BotType getType() {
        return type;
    }
    
    public void setType(BotType type) {
        this.type = type;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public BehaviorType getCurrentBehavior() {
        return currentBehavior;
    }
    
    public void setCurrentBehavior(BehaviorType currentBehavior) {
        this.currentBehavior = currentBehavior;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public double getHealthPercentage() {
        return healthPercentage;
    }
    
    public void setHealthPercentage(double healthPercentage) {
        this.healthPercentage = healthPercentage;
    }
    
    public double getManaPercentage() {
        return manaPercentage;
    }
    
    public void setManaPercentage(double manaPercentage) {
        this.manaPercentage = manaPercentage;
    }
    
    public String getLastAction() {
        return lastAction;
    }
    
    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }
    
    public long getUptime() {
        return uptime;
    }
    
    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    /**
     * Получить краткую информацию о боте
     * @return краткая строка информации
     */
    public String getShortInfo() {
        return String.format("[%d] %s (%s) - Level %d | %s | %s", 
                id, name, type, level, currentBehavior, active ? "Active" : "Inactive");
    }
    
    /**
     * Получить детальную информацию о боте
     * @return детальная строка информации
     */
    public String getDetailedInfo() {
        return String.format("Bot[%d] %s (%s) - Level %d | %s | %s | HP: %.1f%% | MP: %.1f%% | Uptime: %dms",
                id, name, type, level, currentBehavior, active ? "Active" : "Inactive", 
                healthPercentage, manaPercentage, uptime);
    }
}
