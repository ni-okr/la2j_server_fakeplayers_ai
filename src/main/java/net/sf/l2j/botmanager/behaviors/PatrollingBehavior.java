package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Поведение патрулирования
 * 
 * Бот патрулирует определенную область, ищет врагов, защищает территорию
 * и реагирует на угрозы.
 */
public class PatrollingBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(PatrollingBehavior.class);
    
    // Константы для патрулирования
    private static final int PATROL_RADIUS = 2000; // Радиус патрулирования
    private static final int DETECTION_RADIUS = 1000; // Радиус обнаружения врагов
    private static final int ATTACK_RADIUS = 300; // Радиус атаки
    private static final int MIN_HP_PERCENT = 60; // Минимальный процент HP для патрулирования
    private static final int MIN_MP_PERCENT = 30; // Минимальный процент MP для патрулирования
    
    // Состояния патрулирования
    private enum PatrolState {
        MOVING_TO_PATROL,   // Движение к точке патрулирования
        PATROLLING,         // Патрулирование
        INVESTIGATING,      // Исследование подозрительной активности
        ENGAGING_ENEMY,     // Бой с врагом
        RETURNING_TO_POST,  // Возвращение на пост
        RESTING             // Отдых
    }
    
    // Типы патрулирования
    private enum PatrolType {
        CIRCULAR,           // Круговое патрулирование
        LINEAR,             // Линейное патрулирование
        RANDOM,             // Случайное патрулирование
        GUARD_POST          // Охрана поста
    }
    
    public PatrollingBehavior() {
        super(BehaviorType.PATROLLING);
        setMinExecutionInterval(1000); // Минимум 1 секунда между действиями
        setMaxExecutionInterval(4000); // Максимум 4 секунды
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Получаем текущее состояние патрулирования
        PatrolState currentState = getPatrolState(context);
        
        _log.debug("Patrol state: " + currentState + " for bot " + bot.getContext().getBotId());
        
        switch (currentState) {
            case MOVING_TO_PATROL:
                return moveToPatrolPoint(bot, context, player);
                
            case PATROLLING:
                return performPatrol(bot, context, player);
                
            case INVESTIGATING:
                return investigateActivity(bot, context, player);
                
            case ENGAGING_ENEMY:
                return engageEnemy(bot, context, player);
                
            case RETURNING_TO_POST:
                return returnToPost(bot, context, player);
                
            case RESTING:
                return rest(bot, context, player);
                
            default:
                setPatrolState(context, PatrolState.MOVING_TO_PATROL);
                return true;
        }
    }
    
    /**
     * Движение к точке патрулирования
     */
    private boolean moveToPatrolPoint(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Moving to patrol point...");
        
        // Получаем или создаем точку патрулирования
        Integer patrolX = (Integer) context.getData("patrolX");
        Integer patrolY = (Integer) context.getData("patrolY");
        Integer patrolZ = (Integer) context.getData("patrolZ");
        
        if (patrolX == null || patrolY == null || patrolZ == null) {
            // Создаем новую точку патрулирования
            createPatrolPoint(context, player);
            patrolX = (Integer) context.getData("patrolX");
            patrolY = (Integer) context.getData("patrolY");
            patrolZ = (Integer) context.getData("patrolZ");
        }
        
        double distance = player.getDistance(patrolX, patrolY, patrolZ);
        
        if (distance <= 200) {
            // Достигли точки патрулирования
            setPatrolState(context, PatrolState.PATROLLING);
            _log.info("Reached patrol point, starting patrol...");
            return true;
        }
        
        // Движемся к точке патрулирования
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to patrol point at (" + patrolX + ", " + patrolY + ", " + patrolZ + ")");
        return true;
    }
    
    /**
     * Выполнение патрулирования
     */
    private boolean performPatrol(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Performing patrol...");
        
        // Проверяем на наличие врагов
        L2MonsterInstance enemy = findNearestEnemy(player);
        if (enemy != null) {
            // Обнаружен враг
            context.setData("enemyId", enemy.getObjectId());
            context.setData("enemyX", enemy.getX());
            context.setData("enemyY", enemy.getY());
            context.setData("enemyZ", enemy.getZ());
            
            setPatrolState(context, PatrolState.ENGAGING_ENEMY);
            _log.info("Enemy detected: " + enemy.getName() + " at (" + 
                     enemy.getX() + ", " + enemy.getY() + ", " + enemy.getZ() + ")");
            return true;
        }
        
        // Проверяем на подозрительную активность
        if (detectSuspiciousActivity(player)) {
            setPatrolState(context, PatrolState.INVESTIGATING);
            _log.info("Suspicious activity detected, investigating...");
            return true;
        }
        
        // Продолжаем патрулирование
        PatrolType patrolType = getPatrolType(context);
        return continuePatrol(bot, context, player, patrolType);
    }
    
    /**
     * Исследование подозрительной активности
     */
    private boolean investigateActivity(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Investigating suspicious activity...");
        
        // Получаем точку для исследования
        Integer investigateX = (Integer) context.getData("investigateX");
        Integer investigateY = (Integer) context.getData("investigateY");
        Integer investigateZ = (Integer) context.getData("investigateZ");
        
        if (investigateX == null || investigateY == null || investigateZ == null) {
            // Создаем случайную точку для исследования
            investigateX = player.getX() + ThreadLocalRandom.current().nextInt(-500, 501);
            investigateY = player.getY() + ThreadLocalRandom.current().nextInt(-500, 501);
            investigateZ = player.getZ();
            
            context.setData("investigateX", investigateX);
            context.setData("investigateY", investigateY);
            context.setData("investigateZ", investigateZ);
        }
        
        double distance = player.getDistance(investigateX, investigateY, investigateZ);
        
        if (distance <= 100) {
            // Достигли точки исследования
            _log.info("Investigation complete, returning to patrol...");
            context.removeData("investigateX");
            context.removeData("investigateY");
            context.removeData("investigateZ");
            setPatrolState(context, PatrolState.PATROLLING);
            return true;
        }
        
        // Движемся к точке исследования
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to investigate at (" + investigateX + ", " + investigateY + ", " + investigateZ + ")");
        return true;
    }
    
    /**
     * Бой с врагом
     */
    private boolean engageEnemy(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Engaging enemy...");
        
        Integer enemyX = (Integer) context.getData("enemyX");
        Integer enemyY = (Integer) context.getData("enemyY");
        Integer enemyZ = (Integer) context.getData("enemyZ");
        
        if (enemyX == null || enemyY == null || enemyZ == null) {
            // Враг исчез
            setPatrolState(context, PatrolState.PATROLLING);
            return true;
        }
        
        double distance = player.getDistance(enemyX, enemyY, enemyZ);
        
        if (distance <= ATTACK_RADIUS) {
            // Атакуем врага
            // TODO: Implement attack method in EnhancedFakePlayer
            _log.info("Attacking enemy at (" + enemyX + ", " + enemyY + ", " + enemyZ + ")");
        } else {
            // Движемся к врагу
            // TODO: Implement moveTo method in EnhancedFakePlayer
            _log.debug("Moving to enemy at (" + enemyX + ", " + enemyY + ", " + enemyZ + ")");
        }
        
        // Проверяем, жив ли враг
        if (ThreadLocalRandom.current().nextInt(100) < 20) { // 20% шанс, что враг побежден
            _log.info("Enemy defeated, returning to patrol...");
            context.removeData("enemyId");
            context.removeData("enemyX");
            context.removeData("enemyY");
            context.removeData("enemyZ");
            setPatrolState(context, PatrolState.PATROLLING);
        }
        
        return true;
    }
    
    /**
     * Возвращение на пост
     */
    private boolean returnToPost(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Returning to post...");
        
        Integer postX = (Integer) context.getData("postX");
        Integer postY = (Integer) context.getData("postY");
        Integer postZ = (Integer) context.getData("postZ");
        
        if (postX == null || postY == null || postZ == null) {
            // Создаем пост
            createPost(context, player);
            postX = (Integer) context.getData("postX");
            postY = (Integer) context.getData("postY");
            postZ = (Integer) context.getData("postZ");
        }
        
        double distance = player.getDistance(postX, postY, postZ);
        
        if (distance <= 200) {
            // Достигли поста
            setPatrolState(context, PatrolState.PATROLLING);
            _log.info("Returned to post, resuming patrol...");
            return true;
        }
        
        // Движемся к посту
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to post at (" + postX + ", " + postY + ", " + postZ + ")");
        return true;
    }
    
    /**
     * Отдых
     */
    private boolean rest(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Resting...");
        
        // Проверяем HP и MP
        double hpPercent = (player.getCurrentHp() / player.getMaxHp()) * 100;
        double mpPercent = (player.getCurrentMp() / player.getMaxMp()) * 100;
        
        if (hpPercent >= MIN_HP_PERCENT && mpPercent >= MIN_MP_PERCENT) {
            // Достаточно HP и MP, возвращаемся к патрулированию
            setPatrolState(context, PatrolState.PATROLLING);
        } else {
            // Восстанавливаемся
            if (hpPercent < MIN_HP_PERCENT) {
                useHealingPotion(bot, context, player);
            }
            
            if (mpPercent < MIN_MP_PERCENT) {
                useManaPotion(bot, context, player);
            }
        }
        
        return true;
    }
    
    /**
     * Создание точки патрулирования
     */
    private void createPatrolPoint(BotContext context, L2PcInstance player) {
        int currentX = player.getX();
        int currentY = player.getY();
        int currentZ = player.getZ();
        
        // Создаем точку патрулирования в радиусе
        int offsetX = ThreadLocalRandom.current().nextInt(-PATROL_RADIUS, PATROL_RADIUS + 1);
        int offsetY = ThreadLocalRandom.current().nextInt(-PATROL_RADIUS, PATROL_RADIUS + 1);
        
        int patrolX = currentX + offsetX;
        int patrolY = currentY + offsetY;
        int patrolZ = currentZ;
        
        context.setData("patrolX", patrolX);
        context.setData("patrolY", patrolY);
        context.setData("patrolZ", patrolZ);
        
        _log.info("Created patrol point at (" + patrolX + ", " + patrolY + ", " + patrolZ + ")");
    }
    
    /**
     * Создание поста
     */
    private void createPost(BotContext context, L2PcInstance player) {
        int currentX = player.getX();
        int currentY = player.getY();
        int currentZ = player.getZ();
        
        context.setData("postX", currentX);
        context.setData("postY", currentY);
        context.setData("postZ", currentZ);
        
        _log.info("Created post at (" + currentX + ", " + currentY + ", " + currentZ + ")");
    }
    
    /**
     * Продолжение патрулирования
     */
    private boolean continuePatrol(EnhancedFakePlayer bot, BotContext context, L2PcInstance player, PatrolType patrolType) {
        switch (patrolType) {
            case CIRCULAR:
                return performCircularPatrol(bot, context, player);
            case LINEAR:
                return performLinearPatrol(bot, context, player);
            case RANDOM:
                return performRandomPatrol(bot, context, player);
            case GUARD_POST:
                return performGuardPost(bot, context, player);
            default:
                return performRandomPatrol(bot, context, player);
        }
    }
    
    /**
     * Круговое патрулирование
     */
    private boolean performCircularPatrol(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        // В реальной реализации здесь будет круговое движение
        moveRandomly(bot, context, player);
        return true;
    }
    
    /**
     * Линейное патрулирование
     */
    private boolean performLinearPatrol(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        // В реальной реализации здесь будет линейное движение
        moveRandomly(bot, context, player);
        return true;
    }
    
    /**
     * Случайное патрулирование
     */
    private boolean performRandomPatrol(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        moveRandomly(bot, context, player);
        return true;
    }
    
    /**
     * Охрана поста
     */
    private boolean performGuardPost(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        // Стоим на месте и наблюдаем
        return true;
    }
    
    /**
     * Поиск ближайшего врага
     */
    private L2MonsterInstance findNearestEnemy(L2PcInstance player) {
        L2MonsterInstance nearestEnemy = null;
        double minDistance = Double.MAX_VALUE;
        
        // Получаем всех монстров в радиусе
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, DETECTION_RADIUS)) {
            if (obj instanceof L2MonsterInstance) {
                L2MonsterInstance monster = (L2MonsterInstance) obj;
                
                // Проверяем, является ли монстр врагом
                if (isEnemy(monster)) {
                    double distance = player.getDistance(monster.getX(), monster.getY(), monster.getZ());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestEnemy = monster;
                    }
                }
            }
        }
        
        return nearestEnemy;
    }
    
    /**
     * Проверка, является ли монстр врагом
     */
    private boolean isEnemy(L2MonsterInstance monster) {
        // В реальной реализации здесь будет проверка по уровню, типу и т.д.
        return monster.getLevel() <= 20; // Простые монстры
    }
    
    /**
     * Обнаружение подозрительной активности
     */
    private boolean detectSuspiciousActivity(L2PcInstance player) {
        // В реальной реализации здесь будет анализ окружения
        return ThreadLocalRandom.current().nextInt(100) < 5; // 5% шанс
    }
    
    /**
     * Получение типа патрулирования
     */
    private PatrolType getPatrolType(BotContext context) {
        Object type = context.getData("patrolType");
        if (type instanceof PatrolType) {
            return (PatrolType) type;
        }
        
        // Устанавливаем случайный тип
        PatrolType[] types = PatrolType.values();
        PatrolType selectedType = types[ThreadLocalRandom.current().nextInt(types.length)];
        context.setData("patrolType", selectedType);
        return selectedType;
    }
    
    /**
     * Случайное движение
     */
    private void moveRandomly(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        int currentX = player.getX();
        int currentY = player.getY();
        int currentZ = player.getZ();
        
        // Случайное смещение в радиусе патрулирования
        int offsetX = ThreadLocalRandom.current().nextInt(-PATROL_RADIUS/2, PATROL_RADIUS/2 + 1);
        int offsetY = ThreadLocalRandom.current().nextInt(-PATROL_RADIUS/2, PATROL_RADIUS/2 + 1);
        
        int newX = currentX + offsetX;
        int newY = currentY + offsetY;
        
        // TODO: Implement moveTo method in EnhancedFakePlayer
        _log.debug("Moving to new location at (" + newX + ", " + newY + ", " + currentZ + ")");
    }
    
    /**
     * Использование зелья лечения
     */
    private void useHealingPotion(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Using healing potion...");
    }
    
    /**
     * Использование зелья маны
     */
    private void useManaPotion(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Using mana potion...");
    }
    
    /**
     * Получение текущего состояния патрулирования
     */
    private PatrolState getPatrolState(BotContext context) {
        Object state = context.getData("patrolState");
        if (state instanceof PatrolState) {
            return (PatrolState) state;
        }
        return PatrolState.MOVING_TO_PATROL;
    }
    
    /**
     * Установка состояния патрулирования
     */
    private void setPatrolState(BotContext context, PatrolState state) {
        context.setData("patrolState", state);
    }
    
    public boolean canExecute(BotContext context) {
        L2PcInstance player = context.getPlayerInstance();
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Проверяем минимальные требования для патрулирования
        double hpPercent = (player.getCurrentHp() / player.getMaxHp()) * 100;
        double mpPercent = (player.getCurrentMp() / player.getMaxMp()) * 100;
        
        return hpPercent >= MIN_HP_PERCENT && mpPercent >= MIN_MP_PERCENT;
    }
    
    public int getPriority(BotContext context) {
        // Приоритет патрулирования зависит от уровня и роли
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return 0;
        }
        
        int level = player.getLevel();
        int basePriority = 40; // Базовый приоритет
        
        // Увеличиваем приоритет с уровнем
        if (level >= 25) basePriority += 10;
        if (level >= 45) basePriority += 10;
        if (level >= 65) basePriority += 10;
        
        return basePriority;
    }
    
    @Override
    public String getStatistics() {
        return "Patrolling Behavior - Active: " + isActive() + 
               ", Min Interval: " + minExecutionInterval + 
               ", Max Interval: " + maxExecutionInterval;
    }
}
