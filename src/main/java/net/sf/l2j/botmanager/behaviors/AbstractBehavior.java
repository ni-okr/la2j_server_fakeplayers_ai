package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Абстрактный базовый класс для всех поведений ботов
 * 
 * Предоставляет общую функциональность и структуру для всех поведений.
 * Каждое конкретное поведение должно наследоваться от этого класса.
 */
public abstract class AbstractBehavior implements IBehavior {
    
    protected static final Logger _log = Logger.getLogger(AbstractBehavior.class);
    
    protected final BehaviorType behaviorType;
    protected BotContext context;
    protected final AtomicBoolean active = new AtomicBoolean(false);
    protected long lastExecutionTime = 0;
    protected long executionCount = 0;
    protected long totalExecutionTime = 0;
    
    // Интервалы выполнения (в миллисекундах)
    protected long minExecutionInterval = 100; // Минимальный интервал между выполнениями
    protected long maxExecutionInterval = 5000; // Максимальный интервал между выполнениями
    
    public AbstractBehavior(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }
    
    @Override
    public final boolean execute(EnhancedFakePlayer bot) {
        if (!canExecute(bot.getContext())) {
            return false;
        }
        
        // Проверяем интервал выполнения
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastExecutionTime < minExecutionInterval) {
            return false;
        }
        
        // Обновляем статистику
        long startTime = System.currentTimeMillis();
        lastExecutionTime = currentTime;
        executionCount++;
        
        try {
            // Выполняем конкретную логику поведения
            boolean result = doExecute(bot);
            
            // Обновляем общее время выполнения
            totalExecutionTime += (System.currentTimeMillis() - startTime);
            
            if (result) {
                _log.debug("Behavior " + behaviorType.getName() + " completed for bot " + bot.getContext().getBotId());
            }
            
            return result;
            
        } catch (Exception e) {
            _log.error("Error executing behavior " + behaviorType.getName() + " for bot " + bot.getContext().getBotId(), e);
            return false;
        }
    }
    
    /**
     * Конкретная реализация поведения
     * 
     * @param bot бот
     * @return true если поведение завершено
     */
    protected abstract boolean doExecute(EnhancedFakePlayer bot);
    
    @Override
    public BehaviorType getType() {
        return behaviorType;
    }
    
    @Override
    public void init(BotContext context) {
        this.context = context;
        _log.debug("Initialized behavior " + behaviorType.getName() + " for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        _log.debug("Ended behavior " + behaviorType.getName() + " for bot " + context.getBotId());
        // Очищаем данные поведения
        context.removeData("behavior_" + behaviorType.name() + "_data");
    }
    
    @Override
    public boolean canExecute(BotContext context) {
        if (!isActive()) {
            return false;
        }
        
        if (context == null) {
            return false;
        }
        
        // Проверяем, не мертв ли бот
        if (context.getState().name().equals("DEAD") || context.getState().name().equals("DISCONNECTED")) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int getPriority() {
        return behaviorType.getPriority();
    }
    
    @Override
    public boolean isActive() {
        return active.get();
    }
    
    @Override
    public void activate() {
        active.set(true);
        _log.debug("Activated behavior " + behaviorType.getName());
    }
    
    @Override
    public void deactivate() {
        active.set(false);
        _log.debug("Deactivated behavior " + behaviorType.getName());
    }
    
    /**
     * Устанавливает минимальный интервал выполнения
     * 
     * @param interval интервал в миллисекундах
     */
    public void setMinExecutionInterval(long interval) {
        this.minExecutionInterval = Math.max(0, interval);
    }
    
    /**
     * Устанавливает максимальный интервал выполнения
     * 
     * @param interval интервал в миллисекундах
     */
    public void setMaxExecutionInterval(long interval) {
        this.maxExecutionInterval = Math.max(minExecutionInterval, interval);
    }
    
    /**
     * Получает статистику выполнения поведения
     * 
     * @return статистика в виде строки
     */
    public String getStatistics() {
        long avgExecutionTime = executionCount > 0 ? totalExecutionTime / executionCount : 0;
        return String.format("Behavior: %s, Executions: %d, Avg Time: %dms, Total Time: %dms", 
                behaviorType.getName(), executionCount, avgExecutionTime, totalExecutionTime);
    }
    
    /**
     * Сбрасывает статистику выполнения
     */
    public void resetStatistics() {
        executionCount = 0;
        totalExecutionTime = 0;
        lastExecutionTime = 0;
    }
    
    /**
     * Проверяет, прошло ли достаточно времени с последнего выполнения
     * 
     * @return true если можно выполнять
     */
    protected boolean canExecuteNow() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastExecutionTime >= minExecutionInterval;
    }
    
    /**
     * Получает контекст бота
     * 
     * @return контекст бота
     */
    protected BotContext getContext() {
        return context;
    }
    
    /**
     * Устанавливает данные поведения в контексте
     * 
     * @param key ключ
     * @param value значение
     */
    protected void setBehaviorData(String key, Object value) {
        if (context != null) {
            context.setData("behavior_" + behaviorType.name() + "_" + key, value);
        }
    }
    
    /**
     * Получает данные поведения из контекста
     * 
     * @param key ключ
     * @param defaultValue значение по умолчанию
     * @return значение
     */
    @SuppressWarnings("unchecked")
    protected <T> T getBehaviorData(String key, T defaultValue) {
        if (context != null) {
            return context.getData("behavior_" + behaviorType.name() + "_" + key, defaultValue);
        }
        return defaultValue;
    }
}
