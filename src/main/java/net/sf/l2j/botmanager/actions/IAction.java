package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;

/**
 * Интерфейс для всех действий бота
 * 
 * Действие представляет собой конкретную операцию, которую может выполнить бот.
 * Действия являются атомарными и выполняются в рамках поведений.
 */
public interface IAction {
    
    /**
     * Выполняет действие
     * 
     * @param bot бот, выполняющий действие
     * @return true если действие завершено успешно, false если нужно продолжить выполнение
     */
    boolean execute(EnhancedFakePlayer bot);
    
    /**
     * Возвращает тип действия
     * 
     * @return тип действия
     */
    ActionType getType();
    
    /**
     * Инициализирует действие
     * 
     * @param context контекст бота
     */
    void init(BotContext context);
    
    /**
     * Вызывается при завершении действия
     * 
     * @param context контекст бота
     */
    void onEnd(BotContext context);
    
    /**
     * Проверяет, может ли действие быть выполнено
     * 
     * @param context контекст бота
     * @return true если действие может быть выполнено
     */
    boolean canExecute(BotContext context);
    
    /**
     * Возвращает приоритет действия
     * 
     * @return приоритет (чем выше, тем важнее)
     */
    int getPriority();
    
    /**
     * Проверяет, является ли действие активным
     * 
     * @return true если действие активно
     */
    boolean isActive();
    
    /**
     * Активирует действие
     */
    void activate();
    
    /**
     * Деактивирует действие
     */
    void deactivate();
    
    /**
     * Проверяет, может ли действие быть прервано
     * 
     * @return true если действие может быть прервано
     */
    boolean canInterrupt();
    
    /**
     * Прерывает выполнение действия
     */
    void interrupt();
    
    /**
     * Возвращает время выполнения действия в миллисекундах
     * 
     * @return время выполнения
     */
    long getExecutionTime();
    
    /**
     * Возвращает оставшееся время выполнения
     * 
     * @return оставшееся время в миллисекундах
     */
    long getRemainingTime();
    
    /**
     * Проверяет, завершено ли действие
     * 
     * @return true если действие завершено
     */
    boolean isCompleted();
    
    /**
     * Получает статистику выполнения действия
     * 
     * @return статистика в виде строки
     */
    String getStatistics();
}
