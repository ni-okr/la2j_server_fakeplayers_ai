package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;

/**
 * Интерфейс для всех поведений бота
 * 
 * Поведение определяет логику действий бота в определенной ситуации.
 * Каждое поведение должно быть модульным и переиспользуемым.
 */
public interface IBehavior {
    
    /**
     * Выполняет поведение
     * 
     * @param bot контекст бота
     * @return true если поведение завершено, false если нужно продолжить выполнение
     */
    boolean execute(EnhancedFakePlayer bot);
    
    /**
     * Возвращает тип поведения
     * 
     * @return тип поведения
     */
    BehaviorType getType();
    
    /**
     * Инициализирует поведение
     * 
     * @param context контекст бота
     */
    void init(BotContext context);
    
    /**
     * Вызывается при завершении поведения
     * 
     * @param context контекст бота
     */
    void onEnd(BotContext context);
    
    /**
     * Проверяет, может ли поведение быть выполнено
     * 
     * @param context контекст бота
     * @return true если поведение может быть выполнено
     */
    boolean canExecute(BotContext context);
    
    /**
     * Возвращает приоритет поведения
     * 
     * @return приоритет (чем выше, тем важнее)
     */
    int getPriority();
    
    /**
     * Проверяет, является ли поведение активным
     * 
     * @return true если поведение активно
     */
    boolean isActive();
    
    /**
     * Активирует поведение
     */
    void activate();
    
    /**
     * Деактивирует поведение
     */
    void deactivate();
    
    /**
     * Получает статистику выполнения поведения
     * 
     * @return статистика в виде строки
     */
    String getStatistics();
}
