package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.actions.IAction;

/**
 * Движок принятия решений для ботов.
 * Анализирует текущую ситуацию и принимает решения о дальнейших действиях.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public interface DecisionEngine {
    
    /**
     * Анализ текущей ситуации бота.
     * 
     * @param bot контекст бота
     * @return результат анализа ситуации
     */
    SituationAnalysis analyzeSituation(EnhancedFakePlayer bot);
    
    /**
     * Принятие решения о следующем действии.
     * 
     * @param bot контекст бота
     * @param analysis результат анализа ситуации
     * @return принятое решение
     */
    Decision makeDecision(EnhancedFakePlayer bot, SituationAnalysis analysis);
    
    /**
     * Оценка приоритета поведения.
     * 
     * @param bot контекст бота
     * @param behavior поведение для оценки
     * @return приоритет поведения (0.0 - 1.0)
     */
    double evaluateBehaviorPriority(EnhancedFakePlayer bot, IBehavior behavior);
    
    /**
     * Оценка приоритета действия.
     * 
     * @param bot контекст бота
     * @param action действие для оценки
     * @return приоритет действия (0.0 - 1.0)
     */
    double evaluateActionPriority(EnhancedFakePlayer bot, IAction action);
    
    /**
     * Проверка возможности выполнения действия.
     * 
     * @param bot контекст бота
     * @param action действие для проверки
     * @return true если действие может быть выполнено
     */
    boolean canExecuteAction(EnhancedFakePlayer bot, IAction action);
    
    /**
     * Обновление контекста принятия решений.
     * 
     * @param bot контекст бота
     * @param context новый контекст
     */
    void updateContext(EnhancedFakePlayer bot, BotContext context);
    
    /**
     * Получение статистики движка решений.
     * 
     * @param bot контекст бота
     * @return статистика в виде строки
     */
    String getStatistics(EnhancedFakePlayer bot);
}
