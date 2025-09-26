package net.sf.l2j.botmanager.ai.impl;

import net.sf.l2j.botmanager.ai.*;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorManager;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.actions.ActionManager;
import net.sf.l2j.botmanager.performance.PerformanceMonitor;
import net.sf.l2j.botmanager.performance.CacheManager;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Оптимизированная реализация ядра ИИ.
 * 
 * Включает кэширование, мониторинг производительности,
 * оптимизацию алгоритмов и улучшенное управление памятью.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class OptimizedAICoreImpl implements AICore {
    
    private static final Logger logger = Logger.getLogger(OptimizedAICoreImpl.class);
    
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
    
    /** Монитор производительности */
    private final PerformanceMonitor performanceMonitor;
    
    /** Менеджер кэширования */
    private final CacheManager cacheManager;
    
    /** Активные ядра ИИ для ботов */
    private final Map<Integer, AICoreState> activeCores;
    
    /** Кэш решений */
    private final Map<String, CachedDecision> decisionCache;
    
    /** Счетчик решений */
    private final AtomicLong decisionCount;
    
    /** Счетчик успешных решений */
    private final AtomicLong successfulDecisionCount;
    
    /** Счетчик кэшированных решений */
    private final AtomicLong cachedDecisionCount;
    
    /** Максимальный размер кэша решений */
    private static final int MAX_DECISION_CACHE_SIZE = 1000;
    
    /** Время жизни кэшированных решений (миллисекунды) */
    private static final long DECISION_CACHE_TTL = 30_000; // 30 секунд
    
    /**
     * Конструктор.
     * 
     * @param decisionEngine движок принятия решений
     * @param behaviorSelector селектор поведений
     * @param actionPlanner планировщик действий
     * @param behaviorManager менеджер поведений
     * @param actionManager менеджер действий
     */
    public OptimizedAICoreImpl(DecisionEngine decisionEngine, BehaviorSelector behaviorSelector,
                              ActionPlanner actionPlanner, BehaviorManager behaviorManager,
                              ActionManager actionManager) {
        this.decisionEngine = decisionEngine;
        this.behaviorSelector = behaviorSelector;
        this.actionPlanner = actionPlanner;
        this.behaviorManager = behaviorManager;
        this.actionManager = actionManager;
        this.performanceMonitor = PerformanceMonitor.getInstance();
        this.cacheManager = CacheManager.getInstance();
        this.activeCores = new ConcurrentHashMap<>();
        this.decisionCache = new ConcurrentHashMap<>();
        this.decisionCount = new AtomicLong(0);
        this.successfulDecisionCount = new AtomicLong(0);
        this.cachedDecisionCount = new AtomicLong(0);
    }
    
    @Override
    public void initialize(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot initialize AI Core: bot is null");
            return;
        }
        
        PerformanceMonitor.PerformanceContext context = 
            performanceMonitor.startOperation("AICore.initialize");
        
        try {
            int botId = bot.getBotId();
            AICoreState state = new AICoreState();
            state.setActive(true);
            state.setPriority(0.5);
            state.setLastDecisionTime(System.currentTimeMillis());
            
            activeCores.put(botId, state);
            
            logger.info("Optimized AI Core initialized for bot " + botId);
        } finally {
            performanceMonitor.endOperation(context);
        }
    }
    
    @Override
    public boolean processDecision(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot process decision: bot is null");
            return false;
        }
        
        PerformanceMonitor.PerformanceContext context = 
            performanceMonitor.startOperation("AICore.processDecision");
        
        try {
            int botId = bot.getBotId();
            AICoreState state = activeCores.get(botId);
            
            if (state == null || !state.isActive()) {
                logger.warn("AI Core not active for bot " + botId);
                return false;
            }
            
            // Проверяем кэш решений
            String cacheKey = generateCacheKey(bot);
            CachedDecision cachedDecision = decisionCache.get(cacheKey);
            
            if (cachedDecision != null && !cachedDecision.isExpired()) {
                // Используем кэшированное решение
                cachedDecisionCount.incrementAndGet();
                logger.debug("Using cached decision for bot " + botId);
                return true;
            }
            
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
            
            // Кэшируем решение
            cacheDecision(cacheKey, decision, selectedBehavior, actions);
            
            // Обновление статистики
            decisionCount.incrementAndGet();
            successfulDecisionCount.incrementAndGet();
            state.setLastDecisionTime(System.currentTimeMillis());
            
            logger.debug("Decision processed for bot " + botId + ": " + decision);
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing decision for bot " + bot.getBotId(), e);
            return false;
        } finally {
            performanceMonitor.endOperation(context);
        }
    }
    
    @Override
    public IBehavior selectBehavior(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot select behavior: bot is null");
            return null;
        }
        
        PerformanceMonitor.PerformanceContext context = 
            performanceMonitor.startOperation("AICore.selectBehavior");
        
        try {
            // Проверяем кэш
            String cacheKey = "behavior_" + bot.getBotId() + "_" + bot.getContext().getState();
            IBehavior cachedBehavior = cacheManager.get(cacheKey, IBehavior.class);
            
            if (cachedBehavior != null) {
                logger.debug("Using cached behavior for bot " + bot.getBotId());
                return cachedBehavior;
            }
            
            SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
            IBehavior behavior = behaviorSelector.selectBehavior(bot, analysis.getAvailableBehaviors());
            
            // Кэшируем поведение
            if (behavior != null) {
                cacheManager.put(cacheKey, behavior, 10_000); // 10 секунд
            }
            
            return behavior;
        } catch (Exception e) {
            logger.error("Error selecting behavior for bot " + bot.getBotId(), e);
            return null;
        } finally {
            performanceMonitor.endOperation(context);
        }
    }
    
    @Override
    public IAction[] planActions(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            logger.warn("Cannot plan actions: bot or behavior is null");
            return new IAction[0];
        }
        
        PerformanceMonitor.PerformanceContext context = 
            performanceMonitor.startOperation("AICore.planActions");
        
        try {
            // Проверяем кэш
            String cacheKey = "actions_" + bot.getBotId() + "_" + behavior.getType();
            @SuppressWarnings("unchecked")
            List<IAction> cachedActions = cacheManager.get(cacheKey, List.class);
            
            if (cachedActions != null) {
                logger.debug("Using cached actions for bot " + bot.getBotId());
                return cachedActions.toArray(new IAction[0]);
            }
            
            List<IAction> actions = actionPlanner.planActions(bot, behavior);
            
            // Кэшируем действия
            if (actions != null && !actions.isEmpty()) {
                cacheManager.put(cacheKey, actions, 5_000); // 5 секунд
            }
            
            return actions.toArray(new IAction[0]);
        } catch (Exception e) {
            logger.error("Error planning actions for bot " + bot.getBotId(), e);
            return new IAction[0];
        } finally {
            performanceMonitor.endOperation(context);
        }
    }
    
    @Override
    public double evaluateSituation(EnhancedFakePlayer bot) {
        if (bot == null) {
            return 0.0;
        }
        
        PerformanceMonitor.PerformanceContext context = 
            performanceMonitor.startOperation("AICore.evaluateSituation");
        
        try {
            // Проверяем кэш
            String cacheKey = "evaluation_" + bot.getBotId() + "_" + bot.getContext().getState();
            Double cachedEvaluation = cacheManager.get(cacheKey, Double.class);
            
            if (cachedEvaluation != null) {
                return cachedEvaluation;
            }
            
            SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
            double evaluation = analysis.getRecommendedPriority();
            
            // Кэшируем оценку
            cacheManager.put(cacheKey, evaluation, 2_000); // 2 секунды
            
            return evaluation;
        } catch (Exception e) {
            logger.error("Error evaluating situation for bot " + bot.getBotId(), e);
            return 0.0;
        } finally {
            performanceMonitor.endOperation(context);
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
            state.setPriority(priority);
        }
    }
    
    @Override
    public boolean isActive(EnhancedFakePlayer bot) {
        if (bot == null) {
            return false;
        }
        
        AICoreState state = activeCores.get(bot.getBotId());
        return state != null && state.isActive();
    }
    
    
    @Override
    public void shutdown(EnhancedFakePlayer bot) {
        if (bot == null) {
            return;
        }
        
        AICoreState state = activeCores.remove(bot.getBotId());
        if (state != null) {
            state.setActive(false);
            logger.info("AI Core shutdown for bot " + bot.getBotId());
        }
    }
    
    @Override
    public String getStatistics(EnhancedFakePlayer bot) {
        if (bot == null) {
            return "Bot is null";
        }
        
        AICoreState state = activeCores.get(bot.getBotId());
        if (state == null) {
            return "AI Core not initialized for bot " + bot.getBotId();
        }
        
        long total = decisionCount.get();
        long successful = successfulDecisionCount.get();
        long cached = cachedDecisionCount.get();
        double successRate = total > 0 ? (double) successful / total : 0.0;
        double cacheHitRate = total > 0 ? (double) cached / total : 0.0;
        
        return String.format(
            "Bot %d AI Core: Active=%s, Priority=%.2f, Total=%d, Successful=%d (%.1f%%), Cached=%d (%.1f%%)",
            bot.getBotId(), state.isActive(), state.getPriority(),
            total, successful, successRate * 100, cached, cacheHitRate * 100
        );
    }
    
    /**
     * Получить общую статистику ядра ИИ.
     * 
     * @return общая статистика
     */
    public String getOverallStatistics() {
        long total = decisionCount.get();
        long successful = successfulDecisionCount.get();
        long cached = cachedDecisionCount.get();
        double successRate = total > 0 ? (double) successful / total : 0.0;
        double cacheHitRate = total > 0 ? (double) cached / total : 0.0;
        
        return String.format(
            "Optimized AI Core Stats: Total=%d, Successful=%d (%.1f%%), Cached=%d (%.1f%%), Active=%d",
            total, successful, successRate * 100, cached, cacheHitRate * 100, activeCores.size()
        );
    }
    
    /**
     * Генерировать ключ кэша для бота.
     * 
     * @param bot бот
     * @return ключ кэша
     */
    private String generateCacheKey(EnhancedFakePlayer bot) {
        return "decision_" + bot.getBotId() + "_" + 
               bot.getContext().getState() + "_" + 
               (System.currentTimeMillis() / 1000); // Обновляем каждую секунду
    }
    
    /**
     * Кэшировать решение.
     * 
     * @param cacheKey ключ кэша
     * @param decision решение
     * @param behavior поведение
     * @param actions действия
     */
    private void cacheDecision(String cacheKey, Decision decision, IBehavior behavior, IAction[] actions) {
        if (decisionCache.size() >= MAX_DECISION_CACHE_SIZE) {
            // Удаляем самые старые записи
            cleanupOldCacheEntries();
        }
        
        CachedDecision cachedDecision = new CachedDecision(decision, behavior, actions);
        decisionCache.put(cacheKey, cachedDecision);
    }
    
    /**
     * Очистить старые записи кэша.
     */
    private void cleanupOldCacheEntries() {
        long currentTime = System.currentTimeMillis();
        decisionCache.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(currentTime));
    }
    
    /**
     * Кэшированное решение.
     */
    private static class CachedDecision {
        private final Decision decision;
        private final IBehavior behavior;
        private final IAction[] actions;
        private final long timestamp;
        
        public CachedDecision(Decision decision, IBehavior behavior, IAction[] actions) {
            this.decision = decision;
            this.behavior = behavior;
            this.actions = actions;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        public boolean isExpired(long currentTime) {
            return currentTime - timestamp > DECISION_CACHE_TTL;
        }
        
        public Decision getDecision() {
            return decision;
        }
        
        public IBehavior getBehavior() {
            return behavior;
        }
        
        public IAction[] getActions() {
            return actions;
        }
    }
}
