package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Действие сбора добычи
 */
public class LootAction extends AbstractAction {
    
    private static final Logger _log = Logger.getLogger(LootAction.class);
    
    // Константы для сбора добычи
    private static final int LOOT_RANGE = 100; // Диапазон сбора добычи
    private static final long LOOT_TIMEOUT = 5000; // Таймаут сбора добычи (5 секунд)
    
    // Состояния сбора добычи
    private enum LootState {
        SEARCHING,    // Поиск добычи
        APPROACHING,  // Приближение к добыче
        LOOTING,      // Сбор добычи
        COMPLETED,    // Добыча собрана
        FAILED        // Не удалось собрать добычу
    }
    
    public LootAction() {
        super(ActionType.PICKUP);
        setMinExecutionInterval(200); // Минимум 200мс между проверками
        setMaxExecutionInterval(1000); // Максимум 1 секунда
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null) {
            return false;
        }
        
        // Получаем параметры добычи
        Integer itemId = getActionData("itemId", null);
        Integer targetX = getActionData("targetX", null);
        Integer targetY = getActionData("targetY", null);
        Integer targetZ = getActionData("targetZ", null);
        
        // Получаем текущее состояние сбора добычи
        LootState currentState = getActionData("lootState", LootState.SEARCHING);
        
        switch (currentState) {
            case SEARCHING:
                return handleSearching(bot, player, itemId, targetX, targetY, targetZ);
            case APPROACHING:
                return handleApproaching(bot, player, itemId, targetX, targetY, targetZ);
            case LOOTING:
                return handleLooting(bot, player, itemId);
            case COMPLETED:
                return handleCompleted(bot);
            case FAILED:
                return handleFailed(bot);
            default:
                setActionData("lootState", LootState.SEARCHING);
                return false;
        }
    }
    
    /**
     * Обработка поиска добычи
     */
    private boolean handleSearching(EnhancedFakePlayer bot, L2PcInstance player, Integer itemId, Integer targetX, Integer targetY, Integer targetZ) {
        // Если указаны координаты, переходим к приближению
        if (targetX != null && targetY != null && targetZ != null) {
            setActionData("lootState", LootState.APPROACHING);
            setActionData("startTime", System.currentTimeMillis());
            _log.debug("Bot " + bot.getBotId() + " searching for loot at (" + targetX + ", " + targetY + ", " + targetZ + ")");
            return false;
        }
        
        // Если указан ID предмета, ищем его
        if (itemId != null) {
            // Здесь должна быть логика поиска предмета по ID
            // Пока что просто переходим к сбору
            setActionData("lootState", LootState.LOOTING);
            setActionData("startTime", System.currentTimeMillis());
            _log.debug("Bot " + bot.getBotId() + " searching for item " + itemId);
            return false;
        }
        
        // Если ничего не указано, ищем ближайшую добычу
        // Здесь должна быть логика поиска ближайшей добычи
        setActionData("lootState", LootState.LOOTING);
        setActionData("startTime", System.currentTimeMillis());
        _log.debug("Bot " + bot.getBotId() + " searching for nearby loot");
        
        return false;
    }
    
    /**
     * Обработка приближения к добыче
     */
    private boolean handleApproaching(EnhancedFakePlayer bot, L2PcInstance player, Integer itemId, Integer targetX, Integer targetY, Integer targetZ) {
        // Проверяем таймаут
        Long startTime = getActionData("startTime", 0L);
        if (System.currentTimeMillis() - startTime > LOOT_TIMEOUT) {
            _log.warn("Bot " + bot.getBotId() + " loot timeout");
            setActionData("lootState", LootState.FAILED);
            return false;
        }
        
        // Проверяем расстояние до добычи
        double distance = player.getDistance(targetX, targetY, targetZ);
        
        if (distance <= LOOT_RANGE) {
            // Достаточно близко для сбора
            setActionData("lootState", LootState.LOOTING);
            return false;
        }
        
        // Здесь должна быть логика движения к добыче
        // Пока что просто ждем
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка сбора добычи
     */
    private boolean handleLooting(EnhancedFakePlayer bot, L2PcInstance player, Integer itemId) {
        // Проверяем таймаут
        Long startTime = getActionData("startTime", 0L);
        if (System.currentTimeMillis() - startTime > LOOT_TIMEOUT) {
            _log.warn("Bot " + bot.getBotId() + " loot timeout");
            setActionData("lootState", LootState.FAILED);
            return false;
        }
        
        // Устанавливаем состояние сбора добычи
        bot.getContext().setState(BotState.FARMING);
        
        // Здесь должна быть логика сбора добычи
        // В реальной реализации здесь будет вызов методов L2J для сбора добычи
        
        // Имитируем сбор добычи
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Проверяем, успешно ли собрана добыча
        boolean lootSuccess = getActionData("lootSuccess", true);
        
        if (lootSuccess) {
            setActionData("lootState", LootState.COMPLETED);
            _log.debug("Bot " + bot.getBotId() + " successfully looted item " + itemId);
        } else {
            setActionData("lootState", LootState.FAILED);
            _log.warn("Bot " + bot.getBotId() + " failed to loot item " + itemId);
        }
        
        return false;
    }
    
    /**
     * Обработка завершения сбора добычи
     */
    private boolean handleCompleted(EnhancedFakePlayer bot) {
        bot.getContext().setState(BotState.IDLE);
        _log.debug("Bot " + bot.getBotId() + " completed looting");
        setCompleted();
        return true;
    }
    
    /**
     * Обработка провала сбора добычи
     */
    private boolean handleFailed(EnhancedFakePlayer bot) {
        bot.getContext().setState(BotState.IDLE);
        _log.warn("Bot " + bot.getBotId() + " looting failed");
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
        
        // Проверяем, жив ли бот
        if (player.isDead()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void init(BotContext context) {
        super.init(context);
        setActionData("lootState", LootState.SEARCHING);
        _log.debug("Initialized loot action for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setActionData("lootState", LootState.SEARCHING);
        setActionData("itemId", null);
        setActionData("targetX", null);
        setActionData("targetY", null);
        setActionData("targetZ", null);
        setActionData("lootSuccess", true);
        _log.debug("Ended loot action for bot " + context.getBotId());
    }
    
    /**
     * Устанавливает предмет для сбора
     * 
     * @param itemId ID предмета
     */
    public void setItem(int itemId) {
        setActionData("itemId", itemId);
    }
    
    /**
     * Устанавливает позицию добычи
     * 
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     */
    public void setPosition(int x, int y, int z) {
        setActionData("targetX", x);
        setActionData("targetY", y);
        setActionData("targetZ", z);
    }
    
    /**
     * Получает ID предмета
     * 
     * @return ID предмета или -1
     */
    public int getItemId() {
        return getActionData("itemId", -1);
    }
    
    /**
     * Получает позицию добычи
     * 
     * @return массив [x, y, z] или null
     */
    public int[] getPosition() {
        Integer x = getActionData("targetX", null);
        Integer y = getActionData("targetY", null);
        Integer z = getActionData("targetZ", null);
        
        if (x != null && y != null && z != null) {
            return new int[]{x, y, z};
        }
        
        return null;
    }
    
    /**
     * Проверяет, завершен ли сбор добычи
     * 
     * @return true если завершен
     */
    public boolean isLootCompleted() {
        LootState state = getActionData("lootState", LootState.SEARCHING);
        return state == LootState.COMPLETED;
    }
    
    /**
     * Проверяет, провалился ли сбор добычи
     * 
     * @return true если провалился
     */
    public boolean isLootFailed() {
        LootState state = getActionData("lootState", LootState.SEARCHING);
        return state == LootState.FAILED;
    }
    
    /**
     * Устанавливает результат сбора добычи
     * 
     * @param success true если успешно
     */
    public void setLootSuccess(boolean success) {
        setActionData("lootSuccess", success);
    }
}
