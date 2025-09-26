package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Поведение бездействия
 * 
 * Бот не выполняет никаких активных действий, просто ждет.
 * Используется как поведение по умолчанию или когда нет других активных задач.
 */
public class IdleBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(IdleBehavior.class);
    
    // Константы для бездействия
    private static final long IDLE_DURATION = 5000; // Длительность бездействия в миллисекундах
    private static final long MIN_IDLE_TIME = 1000; // Минимальное время бездействия
    
    public IdleBehavior() {
        super(BehaviorType.IDLE);
        setMinExecutionInterval(1000); // Минимум 1 секунда между проверками
        setMaxExecutionInterval(10000); // Максимум 10 секунд
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null) {
            return false;
        }
        
        // Устанавливаем состояние бездействия
        context.setState(BotState.IDLE);
        
        // Получаем время начала бездействия
        Long idleStartTime = getBehaviorData("idle_start_time", null);
        if (idleStartTime == null) {
            idleStartTime = System.currentTimeMillis();
            setBehaviorData("idle_start_time", idleStartTime);
            _log.debug("Bot " + context.getBotId() + " started idling");
        }
        
        // Проверяем, прошло ли достаточно времени
        long currentTime = System.currentTimeMillis();
        long idleDuration = currentTime - idleStartTime;
        
        if (idleDuration >= IDLE_DURATION) {
            // Время бездействия истекло, сбрасываем таймер
            setBehaviorData("idle_start_time", null);
            _log.debug("Bot " + context.getBotId() + " finished idling after " + idleDuration + "ms");
            return true; // Поведение завершено
        }
        
        // Продолжаем бездействие
        try {
            Thread.sleep(Math.min(1000, IDLE_DURATION - idleDuration));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    @Override
    public boolean canExecute(BotContext context) {
        if (!super.canExecute(context)) {
            return false;
        }
        
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return false;
        }
        
        // Бездействие возможно всегда, если бот жив
        return !player.isDead();
    }
    
    @Override
    public void init(BotContext context) {
        super.init(context);
        setBehaviorData("idle_start_time", null);
        _log.debug("Initialized idle behavior for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setBehaviorData("idle_start_time", null);
        _log.debug("Ended idle behavior for bot " + context.getBotId());
    }
    
    /**
     * Проверяет, можно ли прервать бездействие
     * 
     * @return true если можно прервать
     */
    public boolean canInterrupt() {
        Long idleStartTime = getBehaviorData("idle_start_time", null);
        if (idleStartTime == null) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        long idleDuration = currentTime - idleStartTime;
        
        return idleDuration >= MIN_IDLE_TIME;
    }
    
    /**
     * Получает оставшееся время бездействия
     * 
     * @return оставшееся время в миллисекундах
     */
    public long getRemainingIdleTime() {
        Long idleStartTime = getBehaviorData("idle_start_time", null);
        if (idleStartTime == null) {
            return IDLE_DURATION;
        }
        
        long currentTime = System.currentTimeMillis();
        long idleDuration = currentTime - idleStartTime;
        
        return Math.max(0, IDLE_DURATION - idleDuration);
    }
}
