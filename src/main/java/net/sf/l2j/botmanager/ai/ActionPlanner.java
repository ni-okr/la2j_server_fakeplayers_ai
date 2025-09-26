package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.actions.ActionType;
import java.util.List;
import java.util.Map;

/**
 * Планировщик действий для ботов.
 * Создает последовательности действий для выполнения выбранного поведения.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public interface ActionPlanner {
    
    /**
     * Планирование последовательности действий для поведения.
     * 
     * @param bot контекст бота
     * @param behavior выбранное поведение
     * @return последовательность действий
     */
    List<IAction> planActions(EnhancedFakePlayer bot, IBehavior behavior);
    
    /**
     * Планирование действий по типу поведения.
     * 
     * @param bot контекст бота
     * @param behaviorType тип поведения
     * @return последовательность действий
     */
    List<IAction> planActionsByType(EnhancedFakePlayer bot, net.sf.l2j.botmanager.behaviors.BehaviorType behaviorType);
    
    /**
     * Создание действия по типу.
     * 
     * @param bot контекст бота
     * @param actionType тип действия
     * @param parameters параметры действия
     * @return созданное действие или null если не удалось создать
     */
    IAction createAction(EnhancedFakePlayer bot, ActionType actionType, Map<String, Object> parameters);
    
    /**
     * Оптимизация последовательности действий.
     * 
     * @param bot контекст бота
     * @param actions исходная последовательность действий
     * @return оптимизированная последовательность действий
     */
    List<IAction> optimizeActionSequence(EnhancedFakePlayer bot, List<IAction> actions);
    
    /**
     * Проверка возможности выполнения последовательности действий.
     * 
     * @param bot контекст бота
     * @param actions последовательность действий
     * @return true если все действия могут быть выполнены
     */
    boolean canExecuteActionSequence(EnhancedFakePlayer bot, List<IAction> actions);
    
    /**
     * Получение времени выполнения последовательности действий.
     * 
     * @param bot контекст бота
     * @param actions последовательность действий
     * @return время выполнения в миллисекундах
     */
    long estimateExecutionTime(EnhancedFakePlayer bot, List<IAction> actions);
    
    /**
     * Получение приоритета действия в контексте поведения.
     * 
     * @param bot контекст бота
     * @param action действие для оценки
     * @param behavior контекстное поведение
     * @return приоритет (0.0 - 1.0)
     */
    double evaluateActionPriority(EnhancedFakePlayer bot, IAction action, IBehavior behavior);
    
    /**
     * Получение статистики планировщика.
     * 
     * @param bot контекст бота
     * @return статистика в виде строки
     */
    String getStatistics(EnhancedFakePlayer bot);
    
    /**
     * Сброс статистики планировщика.
     * 
     * @param bot контекст бота
     */
    void resetStatistics(EnhancedFakePlayer bot);
}
