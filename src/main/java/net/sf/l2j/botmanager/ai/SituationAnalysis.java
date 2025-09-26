package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionType;
import java.util.Map;
import java.util.HashMap;

/**
 * Результат анализа текущей ситуации бота.
 * Содержит информацию о состоянии бота, окружении и доступных возможностях.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class SituationAnalysis {
    
    /** Уровень опасности (0.0 - 1.0) */
    private double dangerLevel;
    
    /** Уровень здоровья (0.0 - 1.0) */
    private double healthLevel;
    
    /** Уровень маны (0.0 - 1.0) */
    private double manaLevel;
    
    /** Количество врагов поблизости */
    private int nearbyEnemies;
    
    /** Количество союзников поблизости */
    private int nearbyAllies;
    
    /** Доступные поведения */
    private Map<BehaviorType, Double> availableBehaviors;
    
    /** Доступные действия */
    private Map<ActionType, Double> availableActions;
    
    /** Рекомендуемый приоритет */
    private double recommendedPriority;
    
    /** Время последнего обновления */
    private long lastUpdateTime;
    
    /**
     * Конструктор по умолчанию.
     */
    public SituationAnalysis() {
        this.dangerLevel = 0.0;
        this.healthLevel = 1.0;
        this.manaLevel = 1.0;
        this.nearbyEnemies = 0;
        this.nearbyAllies = 0;
        this.availableBehaviors = new HashMap<>();
        this.availableActions = new HashMap<>();
        this.recommendedPriority = 0.5;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    // Геттеры и сеттеры
    
    public double getDangerLevel() {
        return dangerLevel;
    }
    
    public void setDangerLevel(double dangerLevel) {
        this.dangerLevel = Math.max(0.0, Math.min(1.0, dangerLevel));
    }
    
    public double getHealthLevel() {
        return healthLevel;
    }
    
    public void setHealthLevel(double healthLevel) {
        this.healthLevel = Math.max(0.0, Math.min(1.0, healthLevel));
    }
    
    public double getManaLevel() {
        return manaLevel;
    }
    
    public void setManaLevel(double manaLevel) {
        this.manaLevel = Math.max(0.0, Math.min(1.0, manaLevel));
    }
    
    public int getNearbyEnemies() {
        return nearbyEnemies;
    }
    
    public void setNearbyEnemies(int nearbyEnemies) {
        this.nearbyEnemies = Math.max(0, nearbyEnemies);
    }
    
    public int getNearbyAllies() {
        return nearbyAllies;
    }
    
    public void setNearbyAllies(int nearbyAllies) {
        this.nearbyAllies = Math.max(0, nearbyAllies);
    }
    
    public Map<BehaviorType, Double> getAvailableBehaviors() {
        return availableBehaviors;
    }
    
    public void setAvailableBehaviors(Map<BehaviorType, Double> availableBehaviors) {
        this.availableBehaviors = availableBehaviors;
    }
    
    public Map<ActionType, Double> getAvailableActions() {
        return availableActions;
    }
    
    public void setAvailableActions(Map<ActionType, Double> availableActions) {
        this.availableActions = availableActions;
    }
    
    public double getRecommendedPriority() {
        return recommendedPriority;
    }
    
    public void setRecommendedPriority(double recommendedPriority) {
        this.recommendedPriority = Math.max(0.0, Math.min(1.0, recommendedPriority));
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    /**
     * Добавление доступного поведения с приоритетом.
     * 
     * @param behavior тип поведения
     * @param priority приоритет (0.0 - 1.0)
     */
    public void addAvailableBehavior(BehaviorType behavior, double priority) {
        this.availableBehaviors.put(behavior, Math.max(0.0, Math.min(1.0, priority)));
    }
    
    /**
     * Добавление доступного действия с приоритетом.
     * 
     * @param action тип действия
     * @param priority приоритет (0.0 - 1.0)
     */
    public void addAvailableAction(ActionType action, double priority) {
        this.availableActions.put(action, Math.max(0.0, Math.min(1.0, priority)));
    }
    
    /**
     * Получение приоритета поведения.
     * 
     * @param behavior тип поведения
     * @return приоритет или 0.0 если поведение недоступно
     */
    public double getBehaviorPriority(BehaviorType behavior) {
        return this.availableBehaviors.getOrDefault(behavior, 0.0);
    }
    
    /**
     * Получение приоритета действия.
     * 
     * @param action тип действия
     * @return приоритет или 0.0 если действие недоступно
     */
    public double getActionPriority(ActionType action) {
        return this.availableActions.getOrDefault(action, 0.0);
    }
    
    /**
     * Проверка актуальности анализа.
     * 
     * @param maxAge максимальный возраст в миллисекундах
     * @return true если анализ актуален
     */
    public boolean isUpToDate(long maxAge) {
        return (System.currentTimeMillis() - lastUpdateTime) <= maxAge;
    }
    
    /**
     * Обновление времени последнего обновления.
     */
    public void updateTimestamp() {
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return String.format("SituationAnalysis{danger=%.2f, health=%.2f, mana=%.2f, enemies=%d, allies=%d, priority=%.2f}",
                dangerLevel, healthLevel, manaLevel, nearbyEnemies, nearbyAllies, recommendedPriority);
    }
}
