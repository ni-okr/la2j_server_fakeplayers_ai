package net.sf.l2j.botmanager.ai.impl;

import net.sf.l2j.botmanager.ai.BehaviorSelector;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.behaviors.BehaviorManager;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация селектора поведений для ботов.
 * Выбирает наиболее подходящее поведение на основе анализа ситуации.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class BehaviorSelectorImpl implements BehaviorSelector {
    
    private static final Logger logger = Logger.getLogger(BehaviorSelectorImpl.class);
    
    /** Менеджер поведений */
    private final BehaviorManager behaviorManager;
    
    /** Поведение по умолчанию */
    private BehaviorType defaultBehaviorType;
    
    /** Статистика выбора поведений */
    private final Map<Integer, BehaviorSelectionStats> botStats;
    
    /** Счетчик выборов */
    private final AtomicLong selectionCount;
    
    /**
     * Конструктор.
     * 
     * @param behaviorManager менеджер поведений
     */
    public BehaviorSelectorImpl(BehaviorManager behaviorManager) {
        this.behaviorManager = behaviorManager;
        this.defaultBehaviorType = BehaviorType.IDLE;
        this.botStats = new HashMap<>();
        this.selectionCount = new AtomicLong(0);
    }
    
    @Override
    public IBehavior selectBehavior(EnhancedFakePlayer bot, Map<BehaviorType, Double> availableBehaviors) {
        if (bot == null || availableBehaviors == null || availableBehaviors.isEmpty()) {
            logger.warn("Cannot select behavior: bot, behaviors or availableBehaviors is null/empty");
            return getDefaultBehavior(bot);
        }
        
        try {
            int botId = bot.getBotId();
            
            // Находим поведение с наивысшим приоритетом
            BehaviorType bestBehaviorType = null;
            double bestPriority = 0.0;
            
            for (Map.Entry<BehaviorType, Double> entry : availableBehaviors.entrySet()) {
                if (entry.getValue() > bestPriority) {
                    bestPriority = entry.getValue();
                    bestBehaviorType = entry.getKey();
                }
            }
            
            if (bestBehaviorType == null) {
                logger.warn("No behavior selected for bot " + botId + ", using default");
                return getDefaultBehavior(bot);
            }
            
            // Получаем поведение из менеджера
            IBehavior selectedBehavior = behaviorManager.getBehavior(bestBehaviorType);
            
            if (selectedBehavior == null) {
                logger.warn("Behavior not found for type " + bestBehaviorType + ", using default");
                return getDefaultBehavior(bot);
            }
            
            // Проверяем доступность поведения
            if (!isBehaviorAvailable(bot, selectedBehavior)) {
                logger.warn("Selected behavior not available for bot " + botId + ", using default");
                return getDefaultBehavior(bot);
            }
            
            // Обновляем статистику
            updateSelectionStats(botId, bestBehaviorType);
            selectionCount.incrementAndGet();
            
            logger.debug("Behavior selected for bot " + botId + ": " + bestBehaviorType + " (priority: " + bestPriority + ")");
            
            return selectedBehavior;
            
        } catch (Exception e) {
            logger.error("Error selecting behavior for bot " + bot.getBotId(), e);
            return getDefaultBehavior(bot);
        }
    }
    
    @Override
    public IBehavior selectBehaviorByType(EnhancedFakePlayer bot, BehaviorType behaviorType) {
        if (bot == null || behaviorType == null) {
            logger.warn("Cannot select behavior by type: bot or behaviorType is null");
            return getDefaultBehavior(bot);
        }
        
        try {
            IBehavior behavior = behaviorManager.getBehavior(behaviorType);
            
            if (behavior == null) {
                logger.warn("Behavior not found for type " + behaviorType + ", using default");
                return getDefaultBehavior(bot);
            }
            
            if (!isBehaviorAvailable(bot, behavior)) {
                logger.warn("Behavior not available for bot " + bot.getBotId() + ", using default");
                return getDefaultBehavior(bot);
            }
            
            updateSelectionStats(bot.getBotId(), behaviorType);
            selectionCount.incrementAndGet();
            
            logger.debug("Behavior selected by type for bot " + bot.getBotId() + ": " + behaviorType);
            
            return behavior;
            
        } catch (Exception e) {
            logger.error("Error selecting behavior by type for bot " + bot.getBotId(), e);
            return getDefaultBehavior(bot);
        }
    }
    
    @Override
    public List<IBehavior> getAvailableBehaviors(EnhancedFakePlayer bot) {
        List<IBehavior> availableBehaviors = new ArrayList<>();
        
        if (bot == null) {
            logger.warn("Cannot get available behaviors: bot is null");
            return availableBehaviors;
        }
        
        try {
            for (BehaviorType behaviorType : BehaviorType.values()) {
                IBehavior behavior = behaviorManager.getBehavior(behaviorType);
                if (behavior != null && isBehaviorAvailable(bot, behavior)) {
                    availableBehaviors.add(behavior);
                }
            }
            
            logger.debug("Found " + availableBehaviors.size() + " available behaviors for bot " + bot.getBotId());
            
        } catch (Exception e) {
            logger.error("Error getting available behaviors for bot " + bot.getBotId(), e);
        }
        
        return availableBehaviors;
    }
    
    @Override
    public double evaluateBehaviorPriority(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            return 0.0;
        }
        
        try {
            // Базовая оценка приоритета
            double priority = 0.5;
            
            // Учитываем тип поведения
            BehaviorType behaviorType = behavior.getType();
            switch (behaviorType) {
                case IDLE:
                    priority = 0.3; // Низкий приоритет для бездействия
                    break;
                case FARMING:
                    priority = 0.7; // Высокий приоритет для фарминга
                    break;
                case QUESTING:
                    priority = 0.6; // Средний приоритет для квестов
                    break;
                case PVP:
                    priority = 0.8; // Высокий приоритет для PvP
                    break;
            }
            
            // Учитываем состояние бота
            if (bot.getPlayerInstance() != null && bot.getPlayerInstance().isDead()) {
                priority = 0.0; // Мертвые боты не могут действовать
            } else if (bot.getCurrentHp() < bot.getMaxHp() * 0.3) {
                priority *= 0.5; // Снижаем приоритет при низком здоровье
            }
            
            return Math.max(0.0, Math.min(1.0, priority));
            
        } catch (Exception e) {
            logger.error("Error evaluating behavior priority for bot " + bot.getBotId(), e);
            return 0.0;
        }
    }
    
    @Override
    public boolean isBehaviorAvailable(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            return false;
        }
        
        try {
            // Проверяем базовые условия
            if (bot.getPlayerInstance() != null && bot.getPlayerInstance().isDead()) {
                return false;
            }
            
            // Проверяем специфичные для поведения условия
            return behavior.canExecute(bot.getContext());
            
        } catch (Exception e) {
            logger.error("Error checking behavior availability for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    @Override
    public IBehavior getDefaultBehavior(EnhancedFakePlayer bot) {
        if (bot == null) {
            return null;
        }
        
        try {
            IBehavior defaultBehavior = behaviorManager.getBehavior(defaultBehaviorType);
            
            if (defaultBehavior == null) {
                logger.warn("Default behavior not found, using first available");
                List<IBehavior> availableBehaviors = getAvailableBehaviors(bot);
                return availableBehaviors.isEmpty() ? null : availableBehaviors.get(0);
            }
            
            return defaultBehavior;
            
        } catch (Exception e) {
            logger.error("Error getting default behavior for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    @Override
    public void setDefaultBehavior(BehaviorType behaviorType) {
        if (behaviorType != null) {
            this.defaultBehaviorType = behaviorType;
            logger.info("Default behavior set to: " + behaviorType);
        }
    }
    
    @Override
    public String getStatistics(EnhancedFakePlayer bot) {
        if (bot == null) {
            return "Bot is null";
        }
        
        int botId = bot.getBotId();
        BehaviorSelectionStats stats = botStats.get(botId);
        
        if (stats == null) {
            return "No statistics available for bot " + botId;
        }
        
        return String.format("Behavior Selector Stats for bot %d: Total Selections=%d, " +
                           "Idle=%d, Farming=%d, Questing=%d, PvP=%d",
                botId, stats.getTotalSelections(), stats.getIdleCount(),
                stats.getFarmingCount(), stats.getQuestingCount(), stats.getPvpCount());
    }
    
    @Override
    public void resetStatistics(EnhancedFakePlayer bot) {
        if (bot != null) {
            botStats.remove(bot.getBotId());
            logger.debug("Statistics reset for bot " + bot.getBotId());
        }
    }
    
    // Приватные методы
    
    private void updateSelectionStats(int botId, BehaviorType behaviorType) {
        BehaviorSelectionStats stats = botStats.computeIfAbsent(botId, k -> new BehaviorSelectionStats());
        stats.incrementSelection(behaviorType);
    }
    
    /**
     * Внутренний класс для статистики выбора поведений.
     */
    private static class BehaviorSelectionStats {
        private int totalSelections;
        private int idleCount;
        private int farmingCount;
        private int questingCount;
        private int pvpCount;
        
        public void incrementSelection(BehaviorType behaviorType) {
            totalSelections++;
            
            switch (behaviorType) {
                case IDLE:
                    idleCount++;
                    break;
                case FARMING:
                    farmingCount++;
                    break;
                case QUESTING:
                    questingCount++;
                    break;
                case PVP:
                    pvpCount++;
                    break;
            }
        }
        
        public int getTotalSelections() {
            return totalSelections;
        }
        
        public int getIdleCount() {
            return idleCount;
        }
        
        public int getFarmingCount() {
            return farmingCount;
        }
        
        public int getQuestingCount() {
            return questingCount;
        }
        
        public int getPvpCount() {
            return pvpCount;
        }
    }
}
