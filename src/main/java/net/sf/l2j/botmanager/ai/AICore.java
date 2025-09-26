package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.actions.IAction;

/**
 * Основной интерфейс ядра ИИ для ботов.
 * Управляет принятием решений, выбором поведений и планированием действий.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public interface AICore {
    
    /**
     * Инициализация ядра ИИ для конкретного бота.
     * 
     * @param bot контекст бота
     */
    void initialize(EnhancedFakePlayer bot);
    
    /**
     * Основной цикл принятия решений.
     * Вызывается периодически для обновления состояния бота.
     * 
     * @param bot контекст бота
     * @return true если решение было принято, false если нет изменений
     */
    boolean processDecision(EnhancedFakePlayer bot);
    
    /**
     * Выбор подходящего поведения для текущей ситуации.
     * 
     * @param bot контекст бота
     * @return выбранное поведение или null если нет подходящего
     */
    IBehavior selectBehavior(EnhancedFakePlayer bot);
    
    /**
     * Планирование последовательности действий для выполнения поведения.
     * 
     * @param bot контекст бота
     * @param behavior выбранное поведение
     * @return массив действий для выполнения
     */
    IAction[] planActions(EnhancedFakePlayer bot, IBehavior behavior);
    
    /**
     * Оценка текущей ситуации и приоритетов.
     * 
     * @param bot контекст бота
     * @return оценка ситуации (0.0 - 1.0)
     */
    double evaluateSituation(EnhancedFakePlayer bot);
    
    /**
     * Получение текущего приоритета бота.
     * 
     * @param bot контекст бота
     * @return приоритет (0.0 - 1.0)
     */
    double getPriority(EnhancedFakePlayer bot);
    
    /**
     * Установка приоритета бота.
     * 
     * @param bot контекст бота
     * @param priority новый приоритет (0.0 - 1.0)
     */
    void setPriority(EnhancedFakePlayer bot, double priority);
    
    /**
     * Остановка работы ядра ИИ для бота.
     * 
     * @param bot контекст бота
     */
    void shutdown(EnhancedFakePlayer bot);
    
    /**
     * Получение статистики работы ядра ИИ.
     * 
     * @param bot контекст бота
     * @return статистика в виде строки
     */
    String getStatistics(EnhancedFakePlayer bot);
    
    /**
     * Проверка активности ядра ИИ.
     * 
     * @param bot контекст бота
     * @return true если ядро активно
     */
    boolean isActive(EnhancedFakePlayer bot);
}
