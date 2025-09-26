package net.sf.l2j.botmanager.ai.impl;

import net.sf.l2j.botmanager.ai.*;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorManager;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.actions.ActionManager;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация ядра ИИ для ботов.
 * Управляет принятием решений, выбором поведений и планированием действий.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class AICoreImpl implements AICore {
    
    private static final Logger logger = Logger.getLogger(AICoreImpl.class);
    
    /** Движок принятия решений */
    private final DecisionEngine decisionEngine;
    
    /** Селектор поведений */
    private final BehaviorSelector behaviorSelector;
    
    /** Планировщик действий */
    private final ActionPlanner actionPlanner;
    
    /** Менеджер поведений */
    private final BehaviorManager behaviorManager;
    
    /** Менеджер действий */
    private final ActionManager actionManager;
    
    /** Активные ядра ИИ для ботов */
    private final Map<Integer, AICoreState> activeCores;
    
    /** Счетчик решений */
    private final AtomicLong decisionCount;
    
    /** Счетчик успешных решений */
    private final AtomicLong successfulDecisionCount;
    
    /**
     * Конструктор.
     * 
     * @param decisionEngine движок принятия решений
     * @param behaviorSelector селектор поведений
     * @param actionPlanner планировщик действий
     * @param behaviorManager менеджер поведений
     * @param actionManager менеджер действий
     */
    public AICoreImpl(DecisionEngine decisionEngine, BehaviorSelector behaviorSelector,
                     ActionPlanner actionPlanner, BehaviorManager behaviorManager,
                     ActionManager actionManager) {
        this.decisionEngine = decisionEngine;
        this.behaviorSelector = behaviorSelector;
        this.actionPlanner = actionPlanner;
        this.behaviorManager = behaviorManager;
        this.actionManager = actionManager;
        this.activeCores = new ConcurrentHashMap<>();
        this.decisionCount = new AtomicLong(0);
        this.successfulDecisionCount = new AtomicLong(0);
    }
    
    @Override
    public void initialize(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot initialize AI Core: bot is null");
            return;
        }
        
        int botId = bot.getBotId();
        AICoreState state = new AICoreState();
        state.setActive(true);
        state.setPriority(0.5);
        state.setLastDecisionTime(System.currentTimeMillis());
        
        activeCores.put(botId, state);
        
        logger.info("AI Core initialized for bot " + botId);
    }
    
    @Override
    public boolean processDecision(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot process decision: bot is null");
            return false;
        }
        
        int botId = bot.getBotId();
        AICoreState state = activeCores.get(botId);
        
        if (state == null || !state.isActive()) {
            logger.warn("AI Core not active for bot " + botId);
            return false;
        }
        
        try {
            // Анализ текущей ситуации
            SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
            
            // Принятие решения
            Decision decision = decisionEngine.makeDecision(bot, analysis);
            
            if (decision == null || !decision.isValid()) {
                logger.debug("No valid decision made for bot " + botId);
                return false;
            }
            
            // Выбор поведения
            IBehavior selectedBehavior = behaviorSelector.selectBehavior(bot, analysis.getAvailableBehaviors());
            
            if (selectedBehavior == null) {
                logger.debug("No suitable behavior found for bot " + botId);
                return false;
            }
            
            // Планирование действий
            IAction[] actions = planActions(bot, selectedBehavior);
            
            if (actions == null || actions.length == 0) {
                logger.debug("No actions planned for bot " + botId);
                return false;
            }
            
            // Обновление статистики
            decisionCount.incrementAndGet();
            successfulDecisionCount.incrementAndGet();
            state.setLastDecisionTime(System.currentTimeMillis());
            
            logger.debug("Decision processed for bot " + botId + ": " + decision);
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing decision for bot " + botId, e);
            return false;
        }
    }
    
    @Override
    public IBehavior selectBehavior(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot select behavior: bot is null");
            return null;
        }
        
        try {
            SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
            return behaviorSelector.selectBehavior(bot, analysis.getAvailableBehaviors());
        } catch (Exception e) {
            logger.error("Error selecting behavior for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    @Override
    public IAction[] planActions(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            logger.warn("Cannot plan actions: bot or behavior is null");
            return new IAction[0];
        }
        
        try {
            List<IAction> actions = actionPlanner.planActions(bot, behavior);
            return actions.toArray(new IAction[0]);
        } catch (Exception e) {
            logger.error("Error planning actions for bot " + bot.getBotId(), e);
            return new IAction[0];
        }
    }
    
    @Override
    public double evaluateSituation(EnhancedFakePlayer bot) {
        if (bot == null) {
            return 0.0;
        }
        
        try {
            SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
            return analysis.getRecommendedPriority();
        } catch (Exception e) {
            logger.error("Error evaluating situation for bot " + bot.getBotId(), e);
            return 0.0;
        }
    }
    
    @Override
    public double getPriority(EnhancedFakePlayer bot) {
        if (bot == null) {
            return 0.0;
        }
        
        AICoreState state = activeCores.get(bot.getBotId());
        return state != null ? state.getPriority() : 0.0;
    }
    
    @Override
    public void setPriority(EnhancedFakePlayer bot, double priority) {
        if (bot == null) {
            return;
        }
        
        AICoreState state = activeCores.get(bot.getBotId());
        if (state != null) {
            state.setPriority(Math.max(0.0, Math.min(1.0, priority)));
        }
    }
    
    @Override
    public void shutdown(EnhancedFakePlayer bot) {
        if (bot == null) {
            return;
        }
        
        int botId = bot.getBotId();
        AICoreState state = activeCores.get(botId);
        
        if (state != null) {
            state.setActive(false);
            logger.info("AI Core shutdown for bot " + botId);
        }
    }
    
    @Override
    public String getStatistics(EnhancedFakePlayer bot) {
        if (bot == null) {
            return "Bot is null";
        }
        
        int botId = bot.getBotId();
        AICoreState state = activeCores.get(botId);
        
        if (state == null) {
            return "AI Core not initialized for bot " + botId;
        }
        
        long totalDecisions = decisionCount.get();
        long successfulDecisions = successfulDecisionCount.get();
        double successRate = totalDecisions > 0 ? (double) successfulDecisions / totalDecisions : 0.0;
        
        return String.format("AI Core Stats for bot %d: Active=%s, Priority=%.2f, " +
                           "Total Decisions=%d, Success Rate=%.2f%%, Last Decision=%d",
                botId, state.isActive(), state.getPriority(), totalDecisions,
                successRate * 100, state.getLastDecisionTime());
    }
    
    @Override
    public boolean isActive(EnhancedFakePlayer bot) {
        if (bot == null) {
            return false;
        }
        
        AICoreState state = activeCores.get(bot.getBotId());
        return state != null && state.isActive();
    }
    
    /**
     * Внутренний класс для хранения состояния ядра ИИ бота.
     */
    private static class AICoreState {
        private boolean active;
        private double priority;
        private long lastDecisionTime;
        
        public boolean isActive() {
            return active;
        }
        
        public void setActive(boolean active) {
            this.active = active;
        }
        
        public double getPriority() {
            return priority;
        }
        
        public void setPriority(double priority) {
            this.priority = priority;
        }
        
        public long getLastDecisionTime() {
            return lastDecisionTime;
        }
        
        public void setLastDecisionTime(long lastDecisionTime) {
            this.lastDecisionTime = lastDecisionTime;
        }
    }
}
