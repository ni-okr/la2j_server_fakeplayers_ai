package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Поведение фарма мобов
 * 
 * Бот ищет мобов в радиусе, атакует их, собирает лут и восстанавливается.
 */
public class FarmingBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(FarmingBehavior.class);
    
    // Константы для фарма
    private static final int SEARCH_RADIUS = 1000; // Радиус поиска мобов
    private static final int ATTACK_RADIUS = 200; // Радиус атаки
    private static final int LOOT_RADIUS = 100; // Радиус сбора лута
    private static final int MIN_HP_PERCENT = 30; // Минимальный процент HP для атаки
    private static final int MIN_MP_PERCENT = 20; // Минимальный процент MP для атаки
    
    // Состояния фарма
    private enum FarmingState {
        SEARCHING,    // Поиск мобов
        MOVING,       // Движение к мобу
        ATTACKING,    // Атака моба
        LOOTING,      // Сбор лута
        RESTING       // Восстановление
    }
    
    public FarmingBehavior() {
        super(BehaviorType.FARMING);
        setMinExecutionInterval(500); // Минимум 500мс между действиями
        setMaxExecutionInterval(2000); // Максимум 2 секунды
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null) {
            return false;
        }
        
        // Получаем текущее состояние фарма
        FarmingState currentState = getBehaviorData("farming_state", FarmingState.SEARCHING);
        
        switch (currentState) {
            case SEARCHING:
                return handleSearching(bot, player);
            case MOVING:
                return handleMoving(bot, player);
            case ATTACKING:
                return handleAttacking(bot, player);
            case LOOTING:
                return handleLooting(bot, player);
            case RESTING:
                return handleResting(bot, player);
            default:
                setBehaviorData("farming_state", FarmingState.SEARCHING);
                return false;
        }
    }
    
    /**
     * Обработка состояния поиска мобов
     */
    private boolean handleSearching(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, нужно ли отдохнуть
        if (needsRest(player)) {
            setBehaviorData("farming_state", FarmingState.RESTING);
            context.setState(BotState.RESTING);
            return false;
        }
        
        // Ищем ближайшего моба
        L2MonsterInstance target = findNearestMonster(player);
        
        if (target != null) {
            setBehaviorData("farming_target", target);
            setBehaviorData("farming_state", FarmingState.MOVING);
            context.setState(BotState.MOVING);
            _log.debug("Bot " + context.getBotId() + " found target: " + target.getName());
        } else {
            // Если мобов нет, ждем немного
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return false;
    }
    
    /**
     * Обработка состояния движения к мобу
     */
    private boolean handleMoving(EnhancedFakePlayer bot, L2PcInstance player) {
        L2MonsterInstance target = getBehaviorData("farming_target", null);
        
        if (target == null || target.isDead()) {
            setBehaviorData("farming_state", FarmingState.SEARCHING);
            return false;
        }
        
        double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
        
        if (distance <= ATTACK_RADIUS) {
            // Достаточно близко для атаки
            setBehaviorData("farming_state", FarmingState.ATTACKING);
            context.setState(BotState.FIGHTING);
            return false;
        } else if (distance > SEARCH_RADIUS) {
            // Слишком далеко, ищем другого моба
            setBehaviorData("farming_target", null);
            setBehaviorData("farming_state", FarmingState.SEARCHING);
            return false;
        } else {
            // Двигаемся к цели
            // Здесь должна быть логика движения к цели
            // Пока что просто ждем
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return false;
    }
    
    /**
     * Обработка состояния атаки моба
     */
    private boolean handleAttacking(EnhancedFakePlayer bot, L2PcInstance player) {
        L2MonsterInstance target = getBehaviorData("farming_target", null);
        
        if (target == null || target.isDead()) {
            setBehaviorData("farming_state", FarmingState.LOOTING);
            context.setState(BotState.FARMING);
            return false;
        }
        
        // Проверяем, нужно ли отдохнуть
        if (needsRest(player)) {
            setBehaviorData("farming_state", FarmingState.RESTING);
            context.setState(BotState.RESTING);
            return false;
        }
        
        double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
        
        if (distance > ATTACK_RADIUS) {
            // Слишком далеко, переходим к движению
            setBehaviorData("farming_state", FarmingState.MOVING);
            context.setState(BotState.MOVING);
            return false;
        }
        
        // Атакуем моба
        // Здесь должна быть логика атаки
        // Пока что просто ждем
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка состояния сбора лута
     */
    private boolean handleLooting(EnhancedFakePlayer bot, L2PcInstance player) {
        L2MonsterInstance target = getBehaviorData("farming_target", null);
        
        if (target != null && !target.isDead()) {
            // Моб еще жив, продолжаем атаку
            setBehaviorData("farming_state", FarmingState.ATTACKING);
            context.setState(BotState.FIGHTING);
            return false;
        }
        
        // Собираем лут
        // Здесь должна быть логика сбора лута
        context.setState(BotState.FARMING);
        
        // Очищаем цель и переходим к поиску
        setBehaviorData("farming_target", null);
        setBehaviorData("farming_state", FarmingState.SEARCHING);
        
        _log.debug("Bot " + context.getBotId() + " finished looting, searching for new target");
        
        return false;
    }
    
    /**
     * Обработка состояния отдыха
     */
    private boolean handleResting(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, восстановился ли бот
        if (!needsRest(player)) {
            setBehaviorData("farming_state", FarmingState.SEARCHING);
            context.setState(BotState.IDLE);
            return false;
        }
        
        // Восстанавливаемся
        // Здесь должна быть логика восстановления HP/MP
        context.setState(BotState.RESTING);
        
        try {
            Thread.sleep(2000); // Отдыхаем 2 секунды
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Ищет ближайшего моба для атаки
     */
    private L2MonsterInstance findNearestMonster(L2PcInstance player) {
        javolution.util.FastList<net.sf.l2j.gameserver.model.L2Object> objects = L2World.getInstance().getVisibleObjects(player, SEARCH_RADIUS);
        
        L2MonsterInstance nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (net.sf.l2j.gameserver.model.L2Object obj : objects) {
            if (obj instanceof L2MonsterInstance) {
                L2MonsterInstance monster = (L2MonsterInstance) obj;
                
                if (monster.isDead() || monster.isInCombat()) {
                    continue;
                }
                
                double distance = player.getDistance(monster.getX(), monster.getY(), monster.getZ());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = monster;
                }
            }
        }
        
        return nearest;
    }
    
    /**
     * Проверяет, нужно ли боту отдохнуть
     */
    private boolean needsRest(L2PcInstance player) {
        double hpPercent = (double) player.getCurrentHp() / player.getMaxHp() * 100;
        double mpPercent = (double) player.getCurrentMp() / player.getMaxMp() * 100;
        
        return hpPercent < MIN_HP_PERCENT || mpPercent < MIN_MP_PERCENT;
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
        setBehaviorData("farming_state", FarmingState.SEARCHING);
        setBehaviorData("farming_target", null);
        _log.info("Initialized farming behavior for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setBehaviorData("farming_target", null);
        setBehaviorData("farming_state", FarmingState.SEARCHING);
        _log.info("Ended farming behavior for bot " + context.getBotId());
    }
}
