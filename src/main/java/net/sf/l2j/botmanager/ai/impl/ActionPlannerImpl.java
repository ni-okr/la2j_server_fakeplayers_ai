package net.sf.l2j.botmanager.ai.impl;

import net.sf.l2j.botmanager.ai.ActionPlanner;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.actions.ActionManager;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация планировщика действий для ботов.
 * Создает последовательности действий для выполнения выбранного поведения.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class ActionPlannerImpl implements ActionPlanner {
    
    private static final Logger logger = Logger.getLogger(ActionPlannerImpl.class);
    
    /** Менеджер действий */
    private final ActionManager actionManager;
    
    /** Статистика планирования */
    private final Map<Integer, ActionPlanningStats> botStats;
    
    /** Счетчик планов */
    private final AtomicLong planningCount;
    
    /**
     * Конструктор.
     * 
     * @param actionManager менеджер действий
     */
    public ActionPlannerImpl(ActionManager actionManager) {
        this.actionManager = actionManager;
        this.botStats = new HashMap<>();
        this.planningCount = new AtomicLong(0);
    }
    
    @Override
    public List<IAction> planActions(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            logger.warn("Cannot plan actions: bot or behavior is null");
            return new ArrayList<>();
        }
        
        try {
            int botId = bot.getBotId();
            BehaviorType behaviorType = behavior.getType();
            
            List<IAction> actions = planActionsByType(bot, behaviorType);
            
            // Обновляем статистику
            updatePlanningStats(botId, behaviorType, actions.size());
            planningCount.incrementAndGet();
            
            logger.debug("Actions planned for bot " + botId + ": " + behaviorType + " (" + actions.size() + " actions)");
            
            return actions;
            
        } catch (Exception e) {
            logger.error("Error planning actions for bot " + bot.getBotId(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<IAction> planActionsByType(EnhancedFakePlayer bot, BehaviorType behaviorType) {
        List<IAction> actions = new ArrayList<>();
        
        if (bot == null || behaviorType == null) {
            logger.warn("Cannot plan actions by type: bot or behaviorType is null");
            return actions;
        }
        
        try {
            // Планирование на основе типа поведения
            switch (behaviorType) {
                case IDLE:
                    actions = planIdleActions(bot);
                    break;
                case FARMING:
                    actions = planFarmingActions(bot);
                    break;
                case QUESTING:
                    actions = planQuestingActions(bot);
                    break;
                case PVP:
                    actions = planPvpActions(bot);
                    break;
                default:
                    actions = planDefaultActions(bot);
                    break;
            }
            
            // Оптимизируем последовательность
            actions = optimizeActionSequence(bot, actions);
            
            logger.debug("Actions planned by type for bot " + bot.getBotId() + ": " + behaviorType + " (" + actions.size() + " actions)");
            
        } catch (Exception e) {
            logger.error("Error planning actions by type for bot " + bot.getBotId(), e);
        }
        
        return actions;
    }
    
    @Override
    public IAction createAction(EnhancedFakePlayer bot, ActionType actionType, Map<String, Object> parameters) {
        if (bot == null || actionType == null) {
            logger.warn("Cannot create action: bot or actionType is null");
            return null;
        }
        
        try {
            IAction action = actionManager.getAction(actionType);
            
            if (action == null) {
                logger.warn("Action not found for type " + actionType);
                return null;
            }
            
            // Инициализируем действие с параметрами
            action.init(bot.getContext());
            
            // Устанавливаем параметры если они предоставлены
            // TODO: Добавить публичный метод для установки параметров действий
            if (parameters != null && !parameters.isEmpty()) {
                logger.debug("Parameters provided but not set: " + parameters.keySet());
            }
            
            logger.debug("Action created for bot " + bot.getBotId() + ": " + actionType);
            
            return action;
            
        } catch (Exception e) {
            logger.error("Error creating action for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    @Override
    public List<IAction> optimizeActionSequence(EnhancedFakePlayer bot, List<IAction> actions) {
        if (bot == null || actions == null || actions.isEmpty()) {
            return actions;
        }
        
        try {
            List<IAction> optimizedActions = new ArrayList<>();
            
            // Простая оптимизация: убираем дублирующиеся действия подряд
            IAction lastAction = null;
            for (IAction action : actions) {
                if (lastAction == null || !action.getType().equals(lastAction.getType())) {
                    optimizedActions.add(action);
                    lastAction = action;
                }
            }
            
            logger.debug("Action sequence optimized for bot " + bot.getBotId() + 
                        ": " + actions.size() + " -> " + optimizedActions.size() + " actions");
            
            return optimizedActions;
            
        } catch (Exception e) {
            logger.error("Error optimizing action sequence for bot " + bot.getBotId(), e);
            return actions;
        }
    }
    
    @Override
    public boolean canExecuteActionSequence(EnhancedFakePlayer bot, List<IAction> actions) {
        if (bot == null || actions == null || actions.isEmpty()) {
            return false;
        }
        
        try {
            // Проверяем базовые условия
            if (bot.getPlayerInstance() != null && bot.getPlayerInstance().isDead()) {
                return false;
            }
            
            // Проверяем каждое действие
            for (IAction action : actions) {
                if (!action.canExecute(bot.getContext())) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error checking action sequence execution for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    @Override
    public long estimateExecutionTime(EnhancedFakePlayer bot, List<IAction> actions) {
        if (bot == null || actions == null || actions.isEmpty()) {
            return 0;
        }
        
        try {
            long totalTime = 0;
            
            // Простая оценка времени выполнения
            for (IAction action : actions) {
                switch (action.getType()) {
                    case MOVE:
                        totalTime += 2000; // 2 секунды на перемещение
                        break;
                    case ATTACK:
                        totalTime += 1500; // 1.5 секунды на атаку
                        break;
                    case CAST_SKILL:
                        totalTime += 3000; // 3 секунды на каст
                        break;
                    case PICKUP:
                        totalTime += 1000; // 1 секунда на лут
                        break;
                }
            }
            
            return totalTime;
            
        } catch (Exception e) {
            logger.error("Error estimating execution time for bot " + bot.getBotId(), e);
            return 0;
        }
    }
    
    @Override
    public double evaluateActionPriority(EnhancedFakePlayer bot, IAction action, IBehavior behavior) {
        if (bot == null || action == null || behavior == null) {
            return 0.0;
        }
        
        try {
            // Базовая оценка приоритета
            double priority = 0.5;
            
            // Учитываем тип действия
            ActionType actionType = action.getType();
            switch (actionType) {
                case MOVE:
                    priority = 0.8; // Высокий приоритет для перемещения
                    break;
                case ATTACK:
                    priority = 0.9; // Очень высокий приоритет для атаки
                    break;
                case CAST_SKILL:
                    priority = 0.7; // Высокий приоритет для кастов
                    break;
                case PICKUP:
                    priority = 0.4; // Низкий приоритет для лута
                    break;
            }
            
            // Учитываем контекст поведения
            BehaviorType behaviorType = behavior.getType();
            if (behaviorType == BehaviorType.FARMING && actionType == ActionType.ATTACK) {
                priority = 1.0; // Максимальный приоритет для атаки при фарминге
            } else if (behaviorType == BehaviorType.PVP && actionType == ActionType.CAST_SKILL) {
                priority = 0.9; // Высокий приоритет для кастов в PvP
            }
            
            return Math.max(0.0, Math.min(1.0, priority));
            
        } catch (Exception e) {
            logger.error("Error evaluating action priority for bot " + bot.getBotId(), e);
            return 0.0;
        }
    }
    
    @Override
    public String getStatistics(EnhancedFakePlayer bot) {
        if (bot == null) {
            return "Bot is null";
        }
        
        int botId = bot.getBotId();
        ActionPlanningStats stats = botStats.get(botId);
        
        if (stats == null) {
            return "No statistics available for bot " + botId;
        }
        
        return String.format("Action Planner Stats for bot %d: Total Plans=%d, " +
                           "Idle=%d, Farming=%d, Questing=%d, PvP=%d, Avg Actions=%.1f",
                botId, stats.getTotalPlans(), stats.getIdlePlans(),
                stats.getFarmingPlans(), stats.getQuestingPlans(), stats.getPvpPlans(),
                stats.getAverageActionsPerPlan());
    }
    
    @Override
    public void resetStatistics(EnhancedFakePlayer bot) {
        if (bot != null) {
            botStats.remove(bot.getBotId());
            logger.debug("Statistics reset for bot " + bot.getBotId());
        }
    }
    
    // Приватные методы для планирования действий
    
    private List<IAction> planIdleActions(EnhancedFakePlayer bot) {
        List<IAction> actions = new ArrayList<>();
        
        // При бездействии просто ждем
        // В реальной реализации можно добавить случайные движения
        
        return actions;
    }
    
    private List<IAction> planFarmingActions(EnhancedFakePlayer bot) {
        List<IAction> actions = new ArrayList<>();
        
        // План фарминга: перемещение -> атака -> лут
        IAction moveAction = createAction(bot, ActionType.MOVE, null);
        if (moveAction != null) {
            actions.add(moveAction);
        }
        
        IAction attackAction = createAction(bot, ActionType.ATTACK, null);
        if (attackAction != null) {
            actions.add(attackAction);
        }
        
        IAction lootAction = createAction(bot, ActionType.PICKUP, null);
        if (lootAction != null) {
            actions.add(lootAction);
        }
        
        return actions;
    }
    
    private List<IAction> planQuestingActions(EnhancedFakePlayer bot) {
        List<IAction> actions = new ArrayList<>();
        
        // План квестов: перемещение -> каст
        IAction moveAction = createAction(bot, ActionType.MOVE, null);
        if (moveAction != null) {
            actions.add(moveAction);
        }
        
        IAction castAction = createAction(bot, ActionType.CAST_SKILL, null);
        if (castAction != null) {
            actions.add(castAction);
        }
        
        return actions;
    }
    
    private List<IAction> planPvpActions(EnhancedFakePlayer bot) {
        List<IAction> actions = new ArrayList<>();
        
        // План PvP: перемещение -> атака -> каст
        IAction moveAction = createAction(bot, ActionType.MOVE, null);
        if (moveAction != null) {
            actions.add(moveAction);
        }
        
        IAction attackAction = createAction(bot, ActionType.ATTACK, null);
        if (attackAction != null) {
            actions.add(attackAction);
        }
        
        IAction castAction = createAction(bot, ActionType.CAST_SKILL, null);
        if (castAction != null) {
            actions.add(castAction);
        }
        
        return actions;
    }
    
    private List<IAction> planDefaultActions(EnhancedFakePlayer bot) {
        List<IAction> actions = new ArrayList<>();
        
        // План по умолчанию: простое перемещение
        IAction moveAction = createAction(bot, ActionType.MOVE, null);
        if (moveAction != null) {
            actions.add(moveAction);
        }
        
        return actions;
    }
    
    private void updatePlanningStats(int botId, BehaviorType behaviorType, int actionCount) {
        ActionPlanningStats stats = botStats.computeIfAbsent(botId, k -> new ActionPlanningStats());
        stats.addPlan(behaviorType, actionCount);
    }
    
    /**
     * Внутренний класс для статистики планирования действий.
     */
    private static class ActionPlanningStats {
        private int totalPlans;
        private int idlePlans;
        private int farmingPlans;
        private int questingPlans;
        private int pvpPlans;
        private int totalActions;
        
        public void addPlan(BehaviorType behaviorType, int actionCount) {
            totalPlans++;
            totalActions += actionCount;
            
            switch (behaviorType) {
                case IDLE:
                    idlePlans++;
                    break;
                case FARMING:
                    farmingPlans++;
                    break;
                case QUESTING:
                    questingPlans++;
                    break;
                case PVP:
                    pvpPlans++;
                    break;
            }
        }
        
        public int getTotalPlans() {
            return totalPlans;
        }
        
        public int getIdlePlans() {
            return idlePlans;
        }
        
        public int getFarmingPlans() {
            return farmingPlans;
        }
        
        public int getQuestingPlans() {
            return questingPlans;
        }
        
        public int getPvpPlans() {
            return pvpPlans;
        }
        
        public double getAverageActionsPerPlan() {
            return totalPlans > 0 ? (double) totalActions / totalPlans : 0.0;
        }
    }
}
