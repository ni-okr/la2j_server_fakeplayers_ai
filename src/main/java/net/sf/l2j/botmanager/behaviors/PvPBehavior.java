package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Поведение PvP сражений
 * 
 * Бот ищет противников, атакует их, использует навыки и тактики.
 */
public class PvPBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(PvPBehavior.class);
    
    // Константы для PvP
    private static final int SEARCH_RADIUS = 1500; // Радиус поиска противников
    private static final int ATTACK_RADIUS = 200; // Радиус атаки
    private static final int ESCAPE_RADIUS = 500; // Радиус для отступления
    private static final int MIN_HP_PERCENT = 20; // Минимальный процент HP для боя
    private static final int MIN_MP_PERCENT = 30; // Минимальный процент MP для боя
    private static final int MAX_LEVEL_DIFF = 10; // Максимальная разница в уровнях
    
    // Состояния PvP
    private enum PvPState {
        SEARCHING,      // Поиск противников
        APPROACHING,    // Приближение к противнику
        ATTACKING,      // Атака противника
        DEFENDING,      // Защита от атаки
        ESCAPING,       // Отступление
        HEALING,        // Восстановление
        WAITING         // Ожидание
    }
    
    public PvPBehavior() {
        super(BehaviorType.PVP);
        setMinExecutionInterval(200); // Минимум 200мс между действиями
        setMaxExecutionInterval(1000); // Максимум 1 секунда
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null) {
            return false;
        }
        
        // Получаем текущее состояние PvP
        PvPState currentState = getBehaviorData("pvp_state", PvPState.SEARCHING);
        
        switch (currentState) {
            case SEARCHING:
                return handleSearching(bot, player);
            case APPROACHING:
                return handleApproaching(bot, player);
            case ATTACKING:
                return handleAttacking(bot, player);
            case DEFENDING:
                return handleDefending(bot, player);
            case ESCAPING:
                return handleEscaping(bot, player);
            case HEALING:
                return handleHealing(bot, player);
            case WAITING:
                return handleWaiting(bot, player);
            default:
                setBehaviorData("pvp_state", PvPState.SEARCHING);
                return false;
        }
    }
    
    /**
     * Обработка поиска противников
     */
    private boolean handleSearching(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, нужно ли восстановиться
        if (needsHealing(player)) {
            setBehaviorData("pvp_state", PvPState.HEALING);
            context.setState(BotState.RESTING);
            return false;
        }
        
        // Ищем противника
        L2PcInstance target = findEnemy(player);
        
        if (target != null) {
            setBehaviorData("pvp_target", target);
            setBehaviorData("pvp_state", PvPState.APPROACHING);
            context.setState(BotState.MOVING);
            _log.debug("Bot " + context.getBotId() + " found enemy: " + target.getName());
        } else {
            // Противников нет, ждем
            setBehaviorData("pvp_state", PvPState.WAITING);
            context.setState(BotState.IDLE);
        }
        
        return false;
    }
    
    /**
     * Обработка приближения к противнику
     */
    private boolean handleApproaching(EnhancedFakePlayer bot, L2PcInstance player) {
        L2PcInstance target = getBehaviorData("pvp_target", null);
        
        if (target == null || target.isDead()) {
            setBehaviorData("pvp_state", PvPState.SEARCHING);
            return false;
        }
        
        double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
        
        if (distance <= ATTACK_RADIUS) {
            // Достаточно близко для атаки
            setBehaviorData("pvp_state", PvPState.ATTACKING);
            context.setState(BotState.FIGHTING);
            return false;
        } else if (distance > SEARCH_RADIUS) {
            // Слишком далеко, ищем другого противника
            setBehaviorData("pvp_target", null);
            setBehaviorData("pvp_state", PvPState.SEARCHING);
            return false;
        } else {
            // Двигаемся к цели
            context.setState(BotState.MOVING);
            // Здесь должна быть логика движения к цели
        }
        
        return false;
    }
    
    /**
     * Обработка атаки противника
     */
    private boolean handleAttacking(EnhancedFakePlayer bot, L2PcInstance player) {
        L2PcInstance target = getBehaviorData("pvp_target", null);
        
        if (target == null || target.isDead()) {
            setBehaviorData("pvp_state", PvPState.SEARCHING);
            return false;
        }
        
        // Проверяем, нужно ли отступить
        if (shouldEscape(player, target)) {
            setBehaviorData("pvp_state", PvPState.ESCAPING);
            context.setState(BotState.MOVING);
            return false;
        }
        
        double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
        
        if (distance > ATTACK_RADIUS) {
            // Слишком далеко, приближаемся
            setBehaviorData("pvp_state", PvPState.APPROACHING);
            context.setState(BotState.MOVING);
            return false;
        }
        
        // Атакуем противника
        context.setState(BotState.FIGHTING);
        // Здесь должна быть логика атаки и использования навыков
        
        // Имитируем атаку
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка защиты от атаки
     */
    private boolean handleDefending(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, атакует ли кто-то бота
        L2PcInstance attacker = getAttacker(player);
        
        if (attacker == null) {
            setBehaviorData("pvp_state", PvPState.SEARCHING);
            return false;
        }
        
        // Проверяем, нужно ли отступить
        if (shouldEscape(player, attacker)) {
            setBehaviorData("pvp_state", PvPState.ESCAPING);
            context.setState(BotState.MOVING);
            return false;
        }
        
        // Защищаемся
        context.setState(BotState.FIGHTING);
        // Здесь должна быть логика защиты и контратаки
        
        return false;
    }
    
    /**
     * Обработка отступления
     */
    private boolean handleEscaping(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, можно ли прекратить отступление
        if (!shouldEscape(player, null)) {
            setBehaviorData("pvp_state", PvPState.SEARCHING);
            return false;
        }
        
        // Отступаем
        context.setState(BotState.MOVING);
        // Здесь должна быть логика отступления
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка восстановления
     */
    private boolean handleHealing(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, восстановился ли бот
        if (!needsHealing(player)) {
            setBehaviorData("pvp_state", PvPState.SEARCHING);
            context.setState(BotState.IDLE);
            return false;
        }
        
        // Восстанавливаемся
        context.setState(BotState.RESTING);
        // Здесь должна быть логика восстановления HP/MP
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка ожидания
     */
    private boolean handleWaiting(EnhancedFakePlayer bot, L2PcInstance player) {
        // Ждем некоторое время, затем ищем противников
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        setBehaviorData("pvp_state", PvPState.SEARCHING);
        return false;
    }
    
    /**
     * Ищет противника для PvP
     */
    private L2PcInstance findEnemy(L2PcInstance player) {
        javolution.util.FastList<net.sf.l2j.gameserver.model.L2Object> objects = L2World.getInstance().getVisibleObjects(player, SEARCH_RADIUS);
        
        L2PcInstance bestTarget = null;
        double minDistance = Double.MAX_VALUE;
        
        for (net.sf.l2j.gameserver.model.L2Object obj : objects) {
            if (obj instanceof L2PcInstance) {
                L2PcInstance target = (L2PcInstance) obj;
                
                if (target.isDead() || target == player) {
                    continue;
                }
                
                // Проверяем разницу в уровнях
                int levelDiff = Math.abs(player.getLevel() - target.getLevel());
                if (levelDiff > MAX_LEVEL_DIFF) {
                    continue;
                }
                
                // Проверяем, не союзник ли это
                if (isAlly(player, target)) {
                    continue;
                }
                
                double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
                if (distance < minDistance) {
                    minDistance = distance;
                    bestTarget = target;
                }
            }
        }
        
        return bestTarget;
    }
    
    /**
     * Получает атакующего бота
     */
    private L2PcInstance getAttacker(L2PcInstance player) {
        // Здесь должна быть логика определения атакующего
        // Пока что возвращаем null
        return null;
    }
    
    /**
     * Проверяет, нужно ли отступить
     */
    private boolean shouldEscape(L2PcInstance player, L2PcInstance target) {
        double hpPercent = (double) player.getCurrentHp() / player.getMaxHp() * 100;
        double mpPercent = (double) player.getCurrentMp() / player.getMaxMp() * 100;
        
        // Отступаем, если HP или MP слишком низкие
        if (hpPercent < MIN_HP_PERCENT || mpPercent < MIN_MP_PERCENT) {
            return true;
        }
        
        // Отступаем, если противник значительно сильнее
        if (target != null) {
            int levelDiff = target.getLevel() - player.getLevel();
            if (levelDiff > 5) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Проверяет, нужно ли восстановиться
     */
    private boolean needsHealing(L2PcInstance player) {
        double hpPercent = (double) player.getCurrentHp() / player.getMaxHp() * 100;
        double mpPercent = (double) player.getCurrentMp() / player.getMaxMp() * 100;
        
        return hpPercent < MIN_HP_PERCENT || mpPercent < MIN_MP_PERCENT;
    }
    
    /**
     * Проверяет, является ли игрок союзником
     */
    private boolean isAlly(L2PcInstance player, L2PcInstance target) {
        // Здесь должна быть логика проверки союзников
        // Пока что возвращаем случайное значение
        return ThreadLocalRandom.current().nextBoolean();
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
        
        // PvP доступен только с определенного уровня
        if (player.getLevel() < 20) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void init(BotContext context) {
        super.init(context);
        setBehaviorData("pvp_state", PvPState.SEARCHING);
        setBehaviorData("pvp_target", null);
        _log.info("Initialized PvP behavior for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setBehaviorData("pvp_target", null);
        setBehaviorData("pvp_state", PvPState.SEARCHING);
        _log.info("Ended PvP behavior for bot " + context.getBotId());
    }
}
