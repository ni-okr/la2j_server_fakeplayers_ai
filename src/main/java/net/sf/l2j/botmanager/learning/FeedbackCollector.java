package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Система сбора обратной связи для обучения ботов.
 * Собирает данные о результатах действий и эффективности поведения.
 */
public class FeedbackCollector {
    private static final Logger logger = Logger.getLogger(FeedbackCollector.class);
    private static FeedbackCollector instance;
    
    // Статистика по действиям
    private final Map<ActionType, ActionFeedback> actionFeedback = new ConcurrentHashMap<>();
    
    // Статистика по поведениям
    private final Map<BehaviorType, BehaviorFeedback> behaviorFeedback = new ConcurrentHashMap<>();
    
    // Слушатели обратной связи
    private final List<IFeedbackListener> listeners = new CopyOnWriteArrayList<>();
    
    // Общая статистика
    private final AtomicLong totalActions = new AtomicLong(0);
    private final AtomicLong totalBehaviors = new AtomicLong(0);
    private final AtomicLong successfulActions = new AtomicLong(0);
    private final AtomicLong successfulBehaviors = new AtomicLong(0);
    
    private boolean active = false;
    
    private FeedbackCollector() {
        initialize();
    }
    
    /**
     * Получить экземпляр коллектора обратной связи.
     */
    public static synchronized FeedbackCollector getInstance() {
        if (instance == null) {
            instance = new FeedbackCollector();
        }
        return instance;
    }
    
    /**
     * Инициализация коллектора.
     */
    private void initialize() {
        // Инициализация статистики для всех типов действий
        for (ActionType actionType : ActionType.values()) {
            actionFeedback.put(actionType, new ActionFeedback(actionType));
        }
        
        // Инициализация статистики для всех типов поведений
        for (BehaviorType behaviorType : BehaviorType.values()) {
            behaviorFeedback.put(behaviorType, new BehaviorFeedback(behaviorType));
        }
        
        active = true;
        logger.info("FeedbackCollector initialized");
    }
    
    /**
     * Записать результат действия.
     */
    public void recordAction(EnhancedFakePlayer bot, ActionType actionType, boolean success, 
                           long executionTime, String details) {
        if (!active) return;
        
        ActionFeedback feedback = actionFeedback.get(actionType);
        if (feedback != null) {
            feedback.recordExecution(success, executionTime, details);
        }
        
        totalActions.incrementAndGet();
        if (success) {
            successfulActions.incrementAndGet();
        }
        
        // Уведомить слушателей
        notifyActionFeedback(bot, actionType, success, executionTime, details);
        
        logger.debug("Recorded action: " + actionType + " - " + (success ? "SUCCESS" : "FAILURE") + 
                    " (" + executionTime + "ms)");
    }
    
    /**
     * Записать результат поведения.
     */
    public void recordBehavior(EnhancedFakePlayer bot, BehaviorType behaviorType, boolean success, 
                             long executionTime, int actionsPerformed, String details) {
        if (!active) return;
        
        BehaviorFeedback feedback = behaviorFeedback.get(behaviorType);
        if (feedback != null) {
            feedback.recordExecution(success, executionTime, actionsPerformed, details);
        }
        
        totalBehaviors.incrementAndGet();
        if (success) {
            successfulBehaviors.incrementAndGet();
        }
        
        // Уведомить слушателей
        notifyBehaviorFeedback(bot, behaviorType, success, executionTime, actionsPerformed, details);
        
        logger.debug("Recorded behavior: " + behaviorType + " - " + (success ? "SUCCESS" : "FAILURE") + 
                    " (" + executionTime + "ms, " + actionsPerformed + " actions)");
    }
    
    /**
     * Записать событие обучения.
     */
    public void recordLearningEvent(EnhancedFakePlayer bot, String eventType, 
                                  Map<String, Object> parameters, double learningValue) {
        if (!active) return;
        
        LearningEvent event = new LearningEvent(bot.getBotId(), eventType, parameters, learningValue);
        
        // Уведомить слушателей
        notifyLearningEvent(bot, event);
        
        logger.debug("Recorded learning event: " + eventType + " for bot " + bot.getBotId() + 
                    " (value: " + learningValue + ")");
    }
    
    /**
     * Добавить слушателя обратной связи.
     */
    public void addListener(IFeedbackListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            logger.debug("Added feedback listener: " + listener.getClass().getSimpleName());
        }
    }
    
    /**
     * Удалить слушателя обратной связи.
     */
    public void removeListener(IFeedbackListener listener) {
        if (listeners.remove(listener)) {
            logger.debug("Removed feedback listener: " + listener.getClass().getSimpleName());
        }
    }
    
    /**
     * Получить статистику по действиям.
     */
    public Map<ActionType, ActionFeedback> getActionFeedback() {
        return new ConcurrentHashMap<>(actionFeedback);
    }
    
    /**
     * Получить статистику по поведениям.
     */
    public Map<BehaviorType, BehaviorFeedback> getBehaviorFeedback() {
        return new ConcurrentHashMap<>(behaviorFeedback);
    }
    
    /**
     * Получить общую статистику.
     */
    public OverallFeedback getOverallFeedback() {
        return new OverallFeedback(
            totalActions.get(),
            totalBehaviors.get(),
            successfulActions.get(),
            successfulBehaviors.get()
        );
    }
    
    /**
     * Получить детальную статистику.
     */
    public String getDetailedStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Feedback Collector Statistics ===\n");
        stats.append("Total Actions: ").append(totalActions.get()).append("\n");
        stats.append("Successful Actions: ").append(successfulActions.get()).append("\n");
        stats.append("Action Success Rate: ").append(getActionSuccessRate()).append("%\n");
        stats.append("Total Behaviors: ").append(totalBehaviors.get()).append("\n");
        stats.append("Successful Behaviors: ").append(successfulBehaviors.get()).append("\n");
        stats.append("Behavior Success Rate: ").append(getBehaviorSuccessRate()).append("%\n");
        stats.append("Active Listeners: ").append(listeners.size()).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Получить коэффициент успешности действий.
     */
    public double getActionSuccessRate() {
        long total = totalActions.get();
        if (total == 0) return 0.0;
        return (double) successfulActions.get() / total * 100.0;
    }
    
    /**
     * Получить коэффициент успешности поведений.
     */
    public double getBehaviorSuccessRate() {
        long total = totalBehaviors.get();
        if (total == 0) return 0.0;
        return (double) successfulBehaviors.get() / total * 100.0;
    }
    
    /**
     * Сбросить статистику.
     */
    public void reset() {
        actionFeedback.clear();
        behaviorFeedback.clear();
        totalActions.set(0);
        totalBehaviors.set(0);
        successfulActions.set(0);
        successfulBehaviors.set(0);
        
        // Переинициализация
        initialize();
        
        logger.info("FeedbackCollector statistics reset");
    }
    
    /**
     * Остановить коллектор.
     */
    public void shutdown() {
        active = false;
        listeners.clear();
        logger.info("FeedbackCollector shutdown");
    }
    
    // Уведомления слушателей
    private void notifyActionFeedback(EnhancedFakePlayer bot, ActionType actionType, 
                                    boolean success, long executionTime, String details) {
        for (IFeedbackListener listener : listeners) {
            try {
                listener.onActionFeedback(bot, actionType, success, executionTime, details);
            } catch (Exception e) {
                logger.error("Error notifying action feedback listener", e);
            }
        }
    }
    
    private void notifyBehaviorFeedback(EnhancedFakePlayer bot, BehaviorType behaviorType, 
                                      boolean success, long executionTime, int actionsPerformed, String details) {
        for (IFeedbackListener listener : listeners) {
            try {
                listener.onBehaviorFeedback(bot, behaviorType, success, executionTime, actionsPerformed, details);
            } catch (Exception e) {
                logger.error("Error notifying behavior feedback listener", e);
            }
        }
    }
    
    private void notifyLearningEvent(EnhancedFakePlayer bot, LearningEvent event) {
        for (IFeedbackListener listener : listeners) {
            try {
                listener.onLearningEvent(bot, event);
            } catch (Exception e) {
                logger.error("Error notifying learning event listener", e);
            }
        }
    }
}
