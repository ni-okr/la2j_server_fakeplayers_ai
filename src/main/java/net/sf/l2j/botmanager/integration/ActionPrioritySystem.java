package net.sf.l2j.botmanager.integration;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Система приоритетов действий для ботов.
 * 
 * Управляет приоритетами действий в зависимости от контекста,
 * поведения и текущей ситуации бота.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class ActionPrioritySystem {
    
    private static final Logger logger = Logger.getLogger(ActionPrioritySystem.class);
    
    /** Базовые приоритеты действий */
    private final Map<ActionType, Double> basePriorities;
    
    /** Модификаторы приоритетов по поведениям */
    private final Map<BehaviorType, Map<ActionType, Double>> behaviorModifiers;
    
    /** Модификаторы приоритетов по ситуациям */
    private final Map<String, Map<ActionType, Double>> situationModifiers;
    
    /** Статистика использования приоритетов */
    private final Map<Integer, PriorityStats> botStats;
    
    /** Счетчик обновлений приоритетов */
    private final AtomicLong priorityUpdates;
    
    /**
     * Конструктор.
     */
    public ActionPrioritySystem() {
        this.basePriorities = new ConcurrentHashMap<>();
        this.behaviorModifiers = new ConcurrentHashMap<>();
        this.situationModifiers = new ConcurrentHashMap<>();
        this.botStats = new ConcurrentHashMap<>();
        this.priorityUpdates = new AtomicLong(0);
        
        initializeBasePriorities();
        initializeBehaviorModifiers();
        initializeSituationModifiers();
    }
    
    /**
     * Инициализация базовых приоритетов.
     */
    private void initializeBasePriorities() {
        // Базовые приоритеты действий (0.0 - 1.0)
        basePriorities.put(ActionType.IDLE, 0.1);
        basePriorities.put(ActionType.MOVE, 0.7);
        basePriorities.put(ActionType.TURN, 0.5);
        basePriorities.put(ActionType.STOP, 0.2);
        
        basePriorities.put(ActionType.ATTACK, 0.9);
        basePriorities.put(ActionType.CAST_SKILL, 0.8);
        basePriorities.put(ActionType.USE_ITEM, 0.6);
        basePriorities.put(ActionType.DEFEND, 0.7);
        basePriorities.put(ActionType.ESCAPE, 0.8);
        
        basePriorities.put(ActionType.PICKUP, 0.5);
        basePriorities.put(ActionType.DROP, 0.3);
        basePriorities.put(ActionType.USE, 0.4);
        basePriorities.put(ActionType.EQUIP, 0.6);
        basePriorities.put(ActionType.UNEQUIP, 0.4);
        
        basePriorities.put(ActionType.TALK, 0.3);
        basePriorities.put(ActionType.TRADE, 0.4);
        basePriorities.put(ActionType.JOIN_PARTY, 0.5);
        basePriorities.put(ActionType.LEAVE_PARTY, 0.3);
        
        basePriorities.put(ActionType.REST, 0.2);
        basePriorities.put(ActionType.HEAL, 0.8);
        basePriorities.put(ActionType.MEDITATE, 0.3);
        
        basePriorities.put(ActionType.SEARCH, 0.4);
        basePriorities.put(ActionType.SCAN, 0.3);
        basePriorities.put(ActionType.INVESTIGATE, 0.2);
        
        basePriorities.put(ActionType.FOLLOW, 0.6);
        basePriorities.put(ActionType.GUARD, 0.7);
        basePriorities.put(ActionType.PATROL, 0.5);
        basePriorities.put(ActionType.WAIT, 0.1);
        
        logger.info("Base priorities initialized for " + basePriorities.size() + " action types");
    }
    
    /**
     * Инициализация модификаторов по поведениям.
     */
    private void initializeBehaviorModifiers() {
        // Модификаторы для поведения FARMING
        Map<ActionType, Double> farmingModifiers = new HashMap<>();
        farmingModifiers.put(ActionType.ATTACK, 1.2);
        farmingModifiers.put(ActionType.PICKUP, 1.1);
        farmingModifiers.put(ActionType.MOVE, 1.0);
        farmingModifiers.put(ActionType.CAST_SKILL, 0.9);
        farmingModifiers.put(ActionType.REST, 0.8);
        behaviorModifiers.put(BehaviorType.FARMING, farmingModifiers);
        
        // Модификаторы для поведения QUESTING
        Map<ActionType, Double> questingModifiers = new HashMap<>();
        questingModifiers.put(ActionType.MOVE, 1.1);
        questingModifiers.put(ActionType.TALK, 1.2);
        questingModifiers.put(ActionType.CAST_SKILL, 1.0);
        questingModifiers.put(ActionType.ATTACK, 0.8);
        questingModifiers.put(ActionType.INVESTIGATE, 1.1);
        behaviorModifiers.put(BehaviorType.QUESTING, questingModifiers);
        
        // Модификаторы для поведения PVP
        Map<ActionType, Double> pvpModifiers = new HashMap<>();
        pvpModifiers.put(ActionType.ATTACK, 1.3);
        pvpModifiers.put(ActionType.CAST_SKILL, 1.2);
        pvpModifiers.put(ActionType.DEFEND, 1.1);
        pvpModifiers.put(ActionType.ESCAPE, 1.0);
        pvpModifiers.put(ActionType.HEAL, 1.2);
        pvpModifiers.put(ActionType.MOVE, 1.1);
        behaviorModifiers.put(BehaviorType.PVP, pvpModifiers);
        
        // Модификаторы для поведения IDLE
        Map<ActionType, Double> idleModifiers = new HashMap<>();
        idleModifiers.put(ActionType.IDLE, 1.0);
        idleModifiers.put(ActionType.WAIT, 1.0);
        idleModifiers.put(ActionType.REST, 1.0);
        behaviorModifiers.put(BehaviorType.IDLE, idleModifiers);
        
        logger.info("Behavior modifiers initialized for " + behaviorModifiers.size() + " behavior types");
    }
    
    /**
     * Инициализация модификаторов по ситуациям.
     */
    private void initializeSituationModifiers() {
        // Модификаторы для опасной ситуации
        Map<ActionType, Double> dangerModifiers = new HashMap<>();
        dangerModifiers.put(ActionType.ATTACK, 1.2);
        dangerModifiers.put(ActionType.DEFEND, 1.3);
        dangerModifiers.put(ActionType.ESCAPE, 1.4);
        dangerModifiers.put(ActionType.HEAL, 1.2);
        dangerModifiers.put(ActionType.CAST_SKILL, 1.1);
        situationModifiers.put("DANGER", dangerModifiers);
        
        // Модификаторы для безопасной ситуации
        Map<ActionType, Double> safeModifiers = new HashMap<>();
        safeModifiers.put(ActionType.REST, 1.2);
        safeModifiers.put(ActionType.MEDITATE, 1.1);
        safeModifiers.put(ActionType.PICKUP, 1.1);
        safeModifiers.put(ActionType.TALK, 1.0);
        situationModifiers.put("SAFE", safeModifiers);
        
        // Модификаторы для низкого здоровья
        Map<ActionType, Double> lowHealthModifiers = new HashMap<>();
        lowHealthModifiers.put(ActionType.HEAL, 1.5);
        lowHealthModifiers.put(ActionType.USE_ITEM, 1.3);
        lowHealthModifiers.put(ActionType.ESCAPE, 1.2);
        lowHealthModifiers.put(ActionType.REST, 1.1);
        situationModifiers.put("LOW_HEALTH", lowHealthModifiers);
        
        // Модификаторы для низкой маны
        Map<ActionType, Double> lowManaModifiers = new HashMap<>();
        lowManaModifiers.put(ActionType.MEDITATE, 1.4);
        lowManaModifiers.put(ActionType.REST, 1.2);
        lowManaModifiers.put(ActionType.USE_ITEM, 1.1);
        situationModifiers.put("LOW_MANA", lowManaModifiers);
        
        logger.info("Situation modifiers initialized for " + situationModifiers.size() + " situation types");
    }
    
    /**
     * Вычисление приоритета действия для бота.
     * 
     * @param bot бот
     * @param action действие
     * @param behavior текущее поведение
     * @param situation текущая ситуация
     * @return приоритет действия (0.0 - 1.0)
     */
    public double calculateActionPriority(EnhancedFakePlayer bot, IAction action, 
                                        IBehavior behavior, String situation) {
        if (bot == null || action == null) {
            return 0.0;
        }
        
        try {
            ActionType actionType = action.getType();
            double priority = basePriorities.getOrDefault(actionType, 0.5);
            
            // Применяем модификаторы поведения
            if (behavior != null) {
                BehaviorType behaviorType = behavior.getType();
                Map<ActionType, Double> behaviorMods = behaviorModifiers.get(behaviorType);
                if (behaviorMods != null) {
                    Double modifier = behaviorMods.get(actionType);
                    if (modifier != null) {
                        priority *= modifier;
                    }
                }
            }
            
            // Применяем модификаторы ситуации
            if (situation != null && !situation.isEmpty()) {
                Map<ActionType, Double> situationMods = situationModifiers.get(situation);
                if (situationMods != null) {
                    Double modifier = situationMods.get(actionType);
                    if (modifier != null) {
                        priority *= modifier;
                    }
                }
            }
            
            // Нормализуем приоритет
            priority = Math.max(0.0, Math.min(1.0, priority));
            
            // Обновляем статистику
            updateBotStats(bot.getBotId(), actionType, priority);
            priorityUpdates.incrementAndGet();
            
            return priority;
            
        } catch (Exception e) {
            logger.error("Error calculating action priority for bot " + bot.getBotId(), e);
            return 0.5; // Возвращаем средний приоритет при ошибке
        }
    }
    
    /**
     * Получение приоритета действия по типу.
     * 
     * @param actionType тип действия
     * @return базовый приоритет
     */
    public double getBasePriority(ActionType actionType) {
        return basePriorities.getOrDefault(actionType, 0.5);
    }
    
    /**
     * Установка базового приоритета действия.
     * 
     * @param actionType тип действия
     * @param priority новый приоритет
     */
    public void setBasePriority(ActionType actionType, double priority) {
        basePriorities.put(actionType, Math.max(0.0, Math.min(1.0, priority)));
        logger.info("Base priority updated for " + actionType + ": " + priority);
    }
    
    /**
     * Получение модификатора поведения.
     * 
     * @param behaviorType тип поведения
     * @param actionType тип действия
     * @return модификатор
     */
    public double getBehaviorModifier(BehaviorType behaviorType, ActionType actionType) {
        Map<ActionType, Double> modifiers = behaviorModifiers.get(behaviorType);
        if (modifiers == null) {
            return 1.0;
        }
        return modifiers.getOrDefault(actionType, 1.0);
    }
    
    /**
     * Установка модификатора поведения.
     * 
     * @param behaviorType тип поведения
     * @param actionType тип действия
     * @param modifier модификатор
     */
    public void setBehaviorModifier(BehaviorType behaviorType, ActionType actionType, double modifier) {
        behaviorModifiers.computeIfAbsent(behaviorType, k -> new HashMap<>())
                         .put(actionType, Math.max(0.1, Math.min(2.0, modifier)));
        logger.info("Behavior modifier updated for " + behaviorType + " -> " + actionType + ": " + modifier);
    }
    
    /**
     * Получение модификатора ситуации.
     * 
     * @param situation ситуация
     * @param actionType тип действия
     * @return модификатор
     */
    public double getSituationModifier(String situation, ActionType actionType) {
        Map<ActionType, Double> modifiers = situationModifiers.get(situation);
        if (modifiers == null) {
            return 1.0;
        }
        return modifiers.getOrDefault(actionType, 1.0);
    }
    
    /**
     * Установка модификатора ситуации.
     * 
     * @param situation ситуация
     * @param actionType тип действия
     * @param modifier модификатор
     */
    public void setSituationModifier(String situation, ActionType actionType, double modifier) {
        situationModifiers.computeIfAbsent(situation, k -> new HashMap<>())
                          .put(actionType, Math.max(0.1, Math.min(2.0, modifier)));
        logger.info("Situation modifier updated for " + situation + " -> " + actionType + ": " + modifier);
    }
    
    /**
     * Обновление статистики бота.
     * 
     * @param botId ID бота
     * @param actionType тип действия
     * @param priority приоритет
     */
    private void updateBotStats(int botId, ActionType actionType, double priority) {
        PriorityStats stats = botStats.computeIfAbsent(botId, k -> new PriorityStats());
        stats.updateActionPriority(actionType, priority);
    }
    
    /**
     * Получение статистики приоритетов.
     * 
     * @return статистика в виде строки
     */
    public String getStatistics() {
        long updates = priorityUpdates.get();
        int botCount = botStats.size();
        
        return String.format("ActionPrioritySystem Stats: Updates=%d, Bots=%d", updates, botCount);
    }
    
    /**
     * Получение статистики бота.
     * 
     * @param botId ID бота
     * @return статистика бота или null если бот не найден
     */
    public String getBotStatistics(int botId) {
        PriorityStats stats = botStats.get(botId);
        if (stats == null) {
            return null;
        }
        
        return stats.getStatistics();
    }
    
    /**
     * Внутренний класс для статистики приоритетов.
     */
    private static class PriorityStats {
        private final Map<ActionType, Double> averagePriorities = new HashMap<>();
        private final Map<ActionType, Integer> actionCounts = new HashMap<>();
        private long totalUpdates = 0;
        
        public void updateActionPriority(ActionType actionType, double priority) {
            totalUpdates++;
            
            // Обновляем средний приоритет
            double currentAvg = averagePriorities.getOrDefault(actionType, 0.0);
            int count = actionCounts.getOrDefault(actionType, 0);
            
            double newAvg = (currentAvg * count + priority) / (count + 1);
            averagePriorities.put(actionType, newAvg);
            actionCounts.put(actionType, count + 1);
        }
        
        public String getStatistics() {
            StringBuilder sb = new StringBuilder();
            sb.append("PriorityStats: Total Updates=").append(totalUpdates);
            
            for (Map.Entry<ActionType, Double> entry : averagePriorities.entrySet()) {
                ActionType actionType = entry.getKey();
                double avgPriority = entry.getValue();
                int count = actionCounts.getOrDefault(actionType, 0);
                sb.append(", ").append(actionType).append("=").append(String.format("%.2f", avgPriority))
                  .append("(").append(count).append(")");
            }
            
            return sb.toString();
        }
    }
}
