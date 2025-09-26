package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс для менеджера действий ботов
 */
public interface IActionManager {
    
    /**
     * Выполняет действие для бота
     * 
     * @param bot бот
     * @param actionType тип действия
     * @param parameters параметры действия
     * @return true если действие выполнено успешно
     */
    boolean executeAction(EnhancedFakePlayer bot, ActionType actionType, Object... parameters);
    
    /**
     * Добавляет действие в очередь бота
     * 
     * @param bot бот
     * @param actionType тип действия
     * @param parameters параметры действия
     * @return true если действие добавлено в очередь
     */
    boolean queueAction(EnhancedFakePlayer bot, ActionType actionType, Object... parameters);
    
    /**
     * Выполняет следующее действие из очереди
     * 
     * @param bot бот
     * @return true если действие выполнено
     */
    boolean executeNextAction(EnhancedFakePlayer bot);
    
    /**
     * Очищает очередь действий бота
     * 
     * @param bot бот
     */
    void clearActionQueue(EnhancedFakePlayer bot);
    
    /**
     * Получает текущее выполняемое действие
     * 
     * @param bot бот
     * @return текущее действие или null
     */
    IAction getCurrentAction(EnhancedFakePlayer bot);
    
    /**
     * Получает очередь действий бота
     * 
     * @param bot бот
     * @return список действий в очереди
     */
    List<IAction> getActionQueue(EnhancedFakePlayer bot);
    
    /**
     * Проверяет, есть ли действия в очереди
     * 
     * @param bot бот
     * @return true если есть действия в очереди
     */
    boolean hasActionsInQueue(EnhancedFakePlayer bot);
    
    /**
     * Получает количество действий в очереди
     * 
     * @param bot бот
     * @return количество действий
     */
    int getActionQueueSize(EnhancedFakePlayer bot);
    
    /**
     * Регистрирует новое действие
     * 
     * @param action действие для регистрации
     */
    void registerAction(IAction action);
    
    /**
     * Отменяет регистрацию действия
     * 
     * @param actionType тип действия
     */
    void unregisterAction(ActionType actionType);
    
    /**
     * Получает зарегистрированное действие по типу
     * 
     * @param actionType тип действия
     * @return действие или null
     */
    IAction getAction(ActionType actionType);
    
    /**
     * Получает все зарегистрированные действия
     * 
     * @return коллекция действий
     */
    Collection<IAction> getAllActions();
    
    /**
     * Проверяет, зарегистрировано ли действие
     * 
     * @param actionType тип действия
     * @return true если зарегистрировано
     */
    boolean isActionRegistered(ActionType actionType);
    
    /**
     * Останавливает текущее действие бота
     * 
     * @param bot бот
     */
    void stopCurrentAction(EnhancedFakePlayer bot);
    
    /**
     * Прерывает все действия бота
     * 
     * @param bot бот
     */
    void interruptAllActions(EnhancedFakePlayer bot);
    
    /**
     * Проверяет, может ли бот выполнить действие
     * 
     * @param bot бот
     * @param actionType тип действия
     * @return true если может выполнить
     */
    boolean canExecuteAction(EnhancedFakePlayer bot, ActionType actionType);
    
    /**
     * Получает статистику действий
     * 
     * @return статистика в виде строки
     */
    String getActionStatistics();
    
    /**
     * Получает статистику действий для конкретного бота
     * 
     * @param botId ID бота
     * @return статистика в виде строки
     */
    String getBotActionStatistics(int botId);
    
    /**
     * Получает рекомендуемое действие для бота
     * 
     * @param bot бот
     * @param context контекст
     * @return рекомендуемое действие или null
     */
    ActionType getRecommendedAction(EnhancedFakePlayer bot, BotContext context);
}
