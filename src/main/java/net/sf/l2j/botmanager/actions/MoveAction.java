package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Действие движения бота к указанной позиции
 */
public class MoveAction extends AbstractAction {
    
    private static final Logger _log = Logger.getLogger(MoveAction.class);
    
    // Константы для движения
    private static final int MOVE_TOLERANCE = 50; // Допустимое расстояние до цели
    private static final long MOVE_TIMEOUT = 30000; // Таймаут движения (30 секунд)
    
    // Состояния движения
    private enum MoveState {
        STARTING,    // Начало движения
        MOVING,      // Движение к цели
        ARRIVED,     // Достиг цели
        FAILED       // Не удалось добраться
    }
    
    public MoveAction() {
        super(ActionType.MOVE);
        setMinExecutionInterval(100); // Минимум 100мс между проверками
        setMaxExecutionInterval(1000); // Максимум 1 секунда
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null) {
            return false;
        }
        
        // Получаем параметры движения
        Integer targetX = getActionData("targetX", null);
        Integer targetY = getActionData("targetY", null);
        Integer targetZ = getActionData("targetZ", null);
        
        if (targetX == null || targetY == null || targetZ == null) {
            _log.warn("MoveAction: Target coordinates not set for bot " + bot.getBotId());
            setCompleted();
            return true;
        }
        
        // Получаем текущее состояние движения
        MoveState currentState = getActionData("moveState", MoveState.STARTING);
        
        switch (currentState) {
            case STARTING:
                return handleStarting(bot, player, targetX, targetY, targetZ);
            case MOVING:
                return handleMoving(bot, player, targetX, targetY, targetZ);
            case ARRIVED:
                return handleArrived(bot);
            case FAILED:
                return handleFailed(bot);
            default:
                setActionData("moveState", MoveState.STARTING);
                return false;
        }
    }
    
    /**
     * Обработка начала движения
     */
    private boolean handleStarting(EnhancedFakePlayer bot, L2PcInstance player, int targetX, int targetY, int targetZ) {
        // Проверяем, не находимся ли мы уже у цели
        double distance = player.getDistance(targetX, targetY, targetZ);
        if (distance <= MOVE_TOLERANCE) {
            setActionData("moveState", MoveState.ARRIVED);
            return false;
        }
        
        // Устанавливаем состояние движения
        bot.getContext().setState(BotState.MOVING);
        setActionData("moveState", MoveState.MOVING);
        setActionData("startTime", System.currentTimeMillis());
        
        _log.debug("Bot " + bot.getBotId() + " started moving to (" + targetX + ", " + targetY + ", " + targetZ + ")");
        
        return false;
    }
    
    /**
     * Обработка движения к цели
     */
    private boolean handleMoving(EnhancedFakePlayer bot, L2PcInstance player, int targetX, int targetY, int targetZ) {
        // Проверяем таймаут
        Long startTime = getActionData("startTime", 0L);
        if (System.currentTimeMillis() - startTime > MOVE_TIMEOUT) {
            _log.warn("Bot " + bot.getBotId() + " move timeout");
            setActionData("moveState", MoveState.FAILED);
            return false;
        }
        
        // Проверяем расстояние до цели
        double distance = player.getDistance(targetX, targetY, targetZ);
        
        if (distance <= MOVE_TOLERANCE) {
            // Достигли цели
            setActionData("moveState", MoveState.ARRIVED);
            return false;
        }
        
        // Здесь должна быть логика движения к цели
        // В реальной реализации здесь будет вызов методов L2J для движения
        // Пока что просто ждем
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка достижения цели
     */
    private boolean handleArrived(EnhancedFakePlayer bot) {
        bot.getContext().setState(BotState.IDLE);
        _log.debug("Bot " + bot.getBotId() + " arrived at destination");
        setCompleted();
        return true;
    }
    
    /**
     * Обработка неудачного движения
     */
    private boolean handleFailed(EnhancedFakePlayer bot) {
        bot.getContext().setState(BotState.IDLE);
        _log.warn("Bot " + bot.getBotId() + " failed to reach destination");
        setCompleted();
        return true;
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
        
        // Проверяем, не в бою ли бот
        if (player.isInCombat()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void init(BotContext context) {
        super.init(context);
        setActionData("moveState", MoveState.STARTING);
        _log.debug("Initialized move action for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setActionData("moveState", MoveState.STARTING);
        setActionData("targetX", null);
        setActionData("targetY", null);
        setActionData("targetZ", null);
        _log.debug("Ended move action for bot " + context.getBotId());
    }
    
    /**
     * Устанавливает целевую позицию
     * 
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     */
    public void setTarget(int x, int y, int z) {
        setActionData("targetX", x);
        setActionData("targetY", y);
        setActionData("targetZ", z);
    }
    
    /**
     * Получает целевую позицию
     * 
     * @return массив [x, y, z] или null
     */
    public int[] getTarget() {
        Integer x = getActionData("targetX", null);
        Integer y = getActionData("targetY", null);
        Integer z = getActionData("targetZ", null);
        
        if (x != null && y != null && z != null) {
            return new int[]{x, y, z};
        }
        
        return null;
    }
    
    /**
     * Проверяет, достиг ли бот цели
     * 
     * @return true если достиг
     */
    public boolean hasArrived() {
        MoveState state = getActionData("moveState", MoveState.STARTING);
        return state == MoveState.ARRIVED;
    }
    
    /**
     * Проверяет, не провалилось ли движение
     * 
     * @return true если провалилось
     */
    public boolean hasFailed() {
        MoveState state = getActionData("moveState", MoveState.STARTING);
        return state == MoveState.FAILED;
    }
}
