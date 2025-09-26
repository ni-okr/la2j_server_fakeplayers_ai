package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Абстрактный базовый класс для всех действий ботов
 * 
 * Предоставляет общую функциональность и структуру для всех действий.
 * Каждое конкретное действие должно наследоваться от этого класса.
 */
public abstract class AbstractAction implements IAction {
    
    protected static final Logger _log = Logger.getLogger(AbstractAction.class);
    
    protected final ActionType actionType;
    protected BotContext context;
    protected final AtomicBoolean active = new AtomicBoolean(false);
    protected final AtomicBoolean completed = new AtomicBoolean(false);
    protected final AtomicBoolean interrupted = new AtomicBoolean(false);
    
    // Время выполнения
    protected long startTime = 0;
    protected long executionTime = 0;
    protected long lastExecutionTime = 0;
    
    // Статистика
    protected final AtomicLong executionCount = new AtomicLong(0);
    protected final AtomicLong totalExecutionTime = new AtomicLong(0);
    
    // Интервалы выполнения (в миллисекундах)
    protected long minExecutionInterval = 100; // Минимальный интервал между выполнениями
    protected long maxExecutionInterval = 5000; // Максимальный интервал между выполнениями
    
    public AbstractAction(ActionType actionType) {
        this.actionType = actionType;
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
        
        // Если действие еще не начато, инициализируем его
        if (startTime == 0) {
            startTime = currentTime;
            init(bot.getContext());
            activate();
            _log.debug("Started action " + actionType.getName() + " for bot " + bot.getBotId());
        }
        
        // Обновляем статистику
        lastExecutionTime = currentTime;
        executionCount.incrementAndGet();
        
        try {
            // Выполняем конкретную логику действия
            boolean result = doExecute(bot);
            
            // Обновляем общее время выполнения
            long currentExecutionTime = System.currentTimeMillis() - startTime;
            totalExecutionTime.addAndGet(currentExecutionTime);
            
            if (result) {
                completed.set(true);
                onEnd(bot.getContext());
                deactivate();
                _log.debug("Completed action " + actionType.getName() + " for bot " + bot.getBotId());
            }
            
            return result;
            
        } catch (Exception e) {
            _log.error("Error executing action " + actionType.getName() + " for bot " + bot.getBotId(), e);
            interrupted.set(true);
            onEnd(bot.getContext());
            deactivate();
            return false;
        }
    }
    
    /**
     * Конкретная реализация действия
     * 
     * @param bot бот
     * @return true если действие завершено
     */
    protected abstract boolean doExecute(EnhancedFakePlayer bot);
    
    @Override
    public ActionType getType() {
        return actionType;
    }
    
    @Override
    public void init(BotContext context) {
        this.context = context;
        _log.debug("Initialized action " + actionType.getName() + " for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        _log.debug("Ended action " + actionType.getName() + " for bot " + context.getBotId());
        // Очищаем данные действия
        context.removeData("action_" + actionType.name() + "_data");
    }
    
    @Override
    public boolean canExecute(BotContext context) {
        if (!isActive() && !completed.get()) {
            return true;
        }
        
        if (context == null) {
            return false;
        }
        
        // Проверяем, не мертв ли бот
        if (context.getState().name().equals("DEAD") || context.getState().name().equals("DISCONNECTED")) {
            return false;
        }
        
        return !completed.get() && !interrupted.get();
    }
    
    @Override
    public int getPriority() {
        return actionType.getPriority();
    }
    
    @Override
    public boolean isActive() {
        return active.get();
    }
    
    @Override
    public void activate() {
        active.set(true);
        _log.debug("Activated action " + actionType.getName());
    }
    
    @Override
    public void deactivate() {
        active.set(false);
        _log.debug("Deactivated action " + actionType.getName());
    }
    
    @Override
    public boolean canInterrupt() {
        // Базовые действия можно прерывать всегда
        return actionType.getPriority() < 10;
    }
    
    @Override
    public void interrupt() {
        if (canInterrupt()) {
            interrupted.set(true);
            completed.set(true);
            deactivate();
            _log.debug("Interrupted action " + actionType.getName());
        }
    }
    
    @Override
    public long getExecutionTime() {
        if (startTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    @Override
    public long getRemainingTime() {
        if (executionTime == 0) {
            return 0;
        }
        long elapsed = getExecutionTime();
        return Math.max(0, executionTime - elapsed);
    }
    
    @Override
    public boolean isCompleted() {
        return completed.get();
    }
    
    @Override
    public String getStatistics() {
        long avgExecutionTime = executionCount.get() > 0 ? totalExecutionTime.get() / executionCount.get() : 0;
        return String.format("Action: %s, Executions: %d, Avg Time: %dms, Total Time: %dms, Completed: %s", 
                actionType.getName(), executionCount.get(), avgExecutionTime, totalExecutionTime.get(), completed.get());
    }
    
    /**
     * Устанавливает время выполнения действия
     * 
     * @param time время в миллисекундах
     */
    public void setExecutionTime(long time) {
        this.executionTime = time;
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
     * Сбрасывает статистику выполнения
     */
    public void resetStatistics() {
        executionCount.set(0);
        totalExecutionTime.set(0);
        lastExecutionTime = 0;
        startTime = 0;
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
     * Устанавливает данные действия в контексте
     * 
     * @param key ключ
     * @param value значение
     */
    protected void setActionData(String key, Object value) {
        if (context != null) {
            context.setData("action_" + actionType.name() + "_" + key, value);
        }
    }
    
    /**
     * Получает данные действия из контекста
     * 
     * @param key ключ
     * @param defaultValue значение по умолчанию
     * @return значение
     */
    @SuppressWarnings("unchecked")
    protected <T> T getActionData(String key, T defaultValue) {
        if (context != null) {
            return context.getData("action_" + actionType.name() + "_" + key, defaultValue);
        }
        return defaultValue;
    }
    
    /**
     * Проверяет, прервано ли действие
     * 
     * @return true если прервано
     */
    protected boolean isInterrupted() {
        return interrupted.get();
    }
    
    /**
     * Устанавливает действие как завершенное
     */
    protected void setCompleted() {
        completed.set(true);
    }
}
