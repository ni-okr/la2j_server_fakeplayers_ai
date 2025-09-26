package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Действие использования навыка
 */
public class CastAction extends AbstractAction {
    
    private static final Logger _log = Logger.getLogger(CastAction.class);
    
    // Константы для использования навыков
    private static final int CAST_RANGE = 500; // Диапазон использования навыка
    private static final long CAST_TIMEOUT = 10000; // Таймаут использования навыка (10 секунд)
    
    // Состояния использования навыка
    private enum CastState {
        PREPARING,    // Подготовка к использованию
        CASTING,      // Использование навыка
        COMPLETED,    // Навык использован
        FAILED        // Не удалось использовать навык
    }
    
    public CastAction() {
        super(ActionType.CAST_SKILL);
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
        
        // Получаем параметры навыка
        Integer skillId = getActionData("skillId", null);
        Integer targetId = getActionData("targetId", null);
        Integer level = getActionData("level", 1);
        
        if (skillId == null) {
            _log.warn("CastAction: Skill ID not set for bot " + bot.getBotId());
            setCompleted();
            return true;
        }
        
        // Получаем текущее состояние использования навыка
        CastState currentState = getActionData("castState", CastState.PREPARING);
        
        switch (currentState) {
            case PREPARING:
                return handlePreparing(bot, player, skillId, targetId, level);
            case CASTING:
                return handleCasting(bot, player, skillId, targetId, level);
            case COMPLETED:
                return handleCompleted(bot);
            case FAILED:
                return handleFailed(bot);
            default:
                setActionData("castState", CastState.PREPARING);
                return false;
        }
    }
    
    /**
     * Обработка подготовки к использованию навыка
     */
    private boolean handlePreparing(EnhancedFakePlayer bot, L2PcInstance player, int skillId, Integer targetId, int level) {
        // Проверяем, есть ли у бота достаточно MP
        if (player.getCurrentMp() < 10) { // Минимальная проверка MP
            _log.warn("Bot " + bot.getBotId() + " not enough MP for skill " + skillId);
            setActionData("castState", CastState.FAILED);
            return false;
        }
        
        // Проверяем, не в бою ли бот (для некоторых навыков)
        if (player.isInCombat() && !canCastInCombat(skillId)) {
            _log.warn("Bot " + bot.getBotId() + " cannot cast skill " + skillId + " in combat");
            setActionData("castState", CastState.FAILED);
            return false;
        }
        
        // Устанавливаем состояние использования навыка
        setActionData("castState", CastState.CASTING);
        setActionData("startTime", System.currentTimeMillis());
        
        _log.debug("Bot " + bot.getBotId() + " preparing to cast skill " + skillId + " (level " + level + ")");
        
        return false;
    }
    
    /**
     * Обработка использования навыка
     */
    private boolean handleCasting(EnhancedFakePlayer bot, L2PcInstance player, int skillId, Integer targetId, int level) {
        // Проверяем таймаут
        Long startTime = getActionData("startTime", 0L);
        if (System.currentTimeMillis() - startTime > CAST_TIMEOUT) {
            _log.warn("Bot " + bot.getBotId() + " cast timeout for skill " + skillId);
            setActionData("castState", CastState.FAILED);
            return false;
        }
        
        // Устанавливаем состояние боя
        bot.getContext().setState(BotState.FIGHTING);
        
        // Здесь должна быть логика использования навыка
        // В реальной реализации здесь будет вызов методов L2J для использования навыка
        
        // Имитируем использование навыка
        try {
            Thread.sleep(500); // Время каста навыка
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Проверяем, успешно ли использован навык
        boolean castSuccess = getActionData("castSuccess", true);
        
        if (castSuccess) {
            setActionData("castState", CastState.COMPLETED);
            _log.debug("Bot " + bot.getBotId() + " successfully cast skill " + skillId);
        } else {
            setActionData("castState", CastState.FAILED);
            _log.warn("Bot " + bot.getBotId() + " failed to cast skill " + skillId);
        }
        
        return false;
    }
    
    /**
     * Обработка завершения использования навыка
     */
    private boolean handleCompleted(EnhancedFakePlayer bot) {
        bot.getContext().setState(BotState.IDLE);
        _log.debug("Bot " + bot.getBotId() + " completed skill cast");
        setCompleted();
        return true;
    }
    
    /**
     * Обработка провала использования навыка
     */
    private boolean handleFailed(EnhancedFakePlayer bot) {
        bot.getContext().setState(BotState.IDLE);
        _log.warn("Bot " + bot.getBotId() + " skill cast failed");
        setCompleted();
        return true;
    }
    
    /**
     * Проверяет, можно ли использовать навык в бою
     * 
     * @param skillId ID навыка
     * @return true если можно
     */
    private boolean canCastInCombat(int skillId) {
        // Здесь должна быть логика проверки, можно ли использовать навык в бою
        // Пока что возвращаем true для всех навыков
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
        
        // Проверяем, не в бою ли бот (для некоторых навыков)
        Integer skillId = getActionData("skillId", null);
        if (skillId != null && player.isInCombat() && !canCastInCombat(skillId)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void init(BotContext context) {
        super.init(context);
        setActionData("castState", CastState.PREPARING);
        _log.debug("Initialized cast action for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setActionData("castState", CastState.PREPARING);
        setActionData("skillId", null);
        setActionData("targetId", null);
        setActionData("level", 1);
        setActionData("castSuccess", true);
        _log.debug("Ended cast action for bot " + context.getBotId());
    }
    
    /**
     * Устанавливает навык для использования
     * 
     * @param skillId ID навыка
     * @param targetId ID цели (может быть null)
     * @param level уровень навыка
     */
    public void setSkill(int skillId, Integer targetId, int level) {
        setActionData("skillId", skillId);
        setActionData("targetId", targetId);
        setActionData("level", level);
    }
    
    /**
     * Получает ID навыка
     * 
     * @return ID навыка или -1
     */
    public int getSkillId() {
        return getActionData("skillId", -1);
    }
    
    /**
     * Получает ID цели
     * 
     * @return ID цели или -1
     */
    public int getTargetId() {
        return getActionData("targetId", -1);
    }
    
    /**
     * Получает уровень навыка
     * 
     * @return уровень навыка
     */
    public int getLevel() {
        return getActionData("level", 1);
    }
    
    /**
     * Проверяет, завершено ли использование навыка
     * 
     * @return true если завершено
     */
    public boolean isCastCompleted() {
        CastState state = getActionData("castState", CastState.PREPARING);
        return state == CastState.COMPLETED;
    }
    
    /**
     * Проверяет, провалилось ли использование навыка
     * 
     * @return true если провалилось
     */
    public boolean isCastFailed() {
        CastState state = getActionData("castState", CastState.PREPARING);
        return state == CastState.FAILED;
    }
    
    /**
     * Устанавливает результат использования навыка
     * 
     * @param success true если успешно
     */
    public void setCastSuccess(boolean success) {
        setActionData("castSuccess", success);
    }
}
