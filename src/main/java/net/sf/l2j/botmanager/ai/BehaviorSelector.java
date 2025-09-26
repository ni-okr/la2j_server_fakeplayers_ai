package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import java.util.List;
import java.util.Map;

/**
 * Селектор поведений для ботов.
 * Выбирает наиболее подходящее поведение на основе анализа ситуации.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public interface BehaviorSelector {
    
    /**
     * Выбор подходящего поведения из доступных.
     * 
     * @param bot контекст бота
     * @param availableBehaviors доступные поведения с приоритетами
     * @return выбранное поведение или null если нет подходящего
     */
    IBehavior selectBehavior(EnhancedFakePlayer bot, Map<BehaviorType, Double> availableBehaviors);
    
    /**
     * Выбор поведения по типу.
     * 
     * @param bot контекст бота
     * @param behaviorType тип поведения
     * @return поведение или null если недоступно
     */
    IBehavior selectBehaviorByType(EnhancedFakePlayer bot, BehaviorType behaviorType);
    
    /**
     * Получение списка доступных поведений.
     * 
     * @param bot контекст бота
     * @return список доступных поведений
     */
    List<IBehavior> getAvailableBehaviors(EnhancedFakePlayer bot);
    
    /**
     * Оценка приоритета поведения для текущей ситуации.
     * 
     * @param bot контекст бота
     * @param behavior поведение для оценки
     * @return приоритет (0.0 - 1.0)
     */
    double evaluateBehaviorPriority(EnhancedFakePlayer bot, IBehavior behavior);
    
    /**
     * Проверка доступности поведения.
     * 
     * @param bot контекст бота
     * @param behavior поведение для проверки
     * @return true если поведение доступно
     */
    boolean isBehaviorAvailable(EnhancedFakePlayer bot, IBehavior behavior);
    
    /**
     * Получение поведения по умолчанию.
     * 
     * @param bot контекст бота
     * @return поведение по умолчанию
     */
    IBehavior getDefaultBehavior(EnhancedFakePlayer bot);
    
    /**
     * Установка поведения по умолчанию.
     * 
     * @param behaviorType тип поведения по умолчанию
     */
    void setDefaultBehavior(BehaviorType behaviorType);
    
    /**
     * Получение статистики селектора.
     * 
     * @param bot контекст бота
     * @return статистика в виде строки
     */
    String getStatistics(EnhancedFakePlayer bot);
    
    /**
     * Сброс статистики селектора.
     * 
     * @param bot контекст бота
     */
    void resetStatistics(EnhancedFakePlayer bot);
}
