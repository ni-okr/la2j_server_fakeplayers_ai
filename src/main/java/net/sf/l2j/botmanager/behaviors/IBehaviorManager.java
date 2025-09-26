package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;

import java.util.Collection;

/**
 * Интерфейс для менеджера поведений ботов
 */
public interface IBehaviorManager {
    
    /**
     * Устанавливает поведение для бота
     * 
     * @param bot бот
     * @param behaviorType тип поведения
     * @return true если поведение установлено успешно
     */
    boolean setBehavior(EnhancedFakePlayer bot, BehaviorType behaviorType);
    
    /**
     * Получает текущее поведение бота
     * 
     * @param bot бот
     * @return текущее поведение или null
     */
    IBehavior getCurrentBehavior(EnhancedFakePlayer bot);
    
    /**
     * Выполняет текущее поведение бота
     * 
     * @param bot бот
     * @return true если поведение выполнено успешно
     */
    boolean executeCurrentBehavior(EnhancedFakePlayer bot);
    
    /**
     * Регистрирует новое поведение
     * 
     * @param behavior поведение для регистрации
     */
    void registerBehavior(IBehavior behavior);
    
    /**
     * Отменяет регистрацию поведения
     * 
     * @param behaviorType тип поведения
     */
    void unregisterBehavior(BehaviorType behaviorType);
    
    /**
     * Получает зарегистрированное поведение по типу
     * 
     * @param behaviorType тип поведения
     * @return поведение или null
     */
    IBehavior getBehavior(BehaviorType behaviorType);
    
    /**
     * Получает все зарегистрированные поведения
     * 
     * @return коллекция поведений
     */
    Collection<IBehavior> getAllBehaviors();
    
    /**
     * Проверяет, зарегистрировано ли поведение
     * 
     * @param behaviorType тип поведения
     * @return true если зарегистрировано
     */
    boolean isBehaviorRegistered(BehaviorType behaviorType);
    
    /**
     * Останавливает текущее поведение бота
     * 
     * @param bot бот
     */
    void stopCurrentBehavior(EnhancedFakePlayer bot);
    
    /**
     * Переключает поведение бота на следующее подходящее
     * 
     * @param bot бот
     * @return true если поведение переключено
     */
    boolean switchToNextBehavior(EnhancedFakePlayer bot);
    
    /**
     * Получает статистику поведений
     * 
     * @return статистика в виде строки
     */
    String getBehaviorStatistics();
}
