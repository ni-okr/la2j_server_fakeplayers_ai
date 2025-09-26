package net.sf.l2j.botmanager.core;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.botmanager.utils.Logger;

/**
 * Расширенный FakePlayer с поддержкой ИИ
 * 
 * Обертка над L2PcInstance, предоставляющая дополнительную функциональность
 * для работы с ботами и ИИ системой.
 */
public class EnhancedFakePlayer {
    
    private static final Logger _log = Logger.getLogger(EnhancedFakePlayer.class);
    
    private final BotContext context;
    private final L2PcInstance playerInstance;
    private volatile boolean isActive = false;
    private volatile long lastUpdateTime = 0;
    
    public EnhancedFakePlayer(BotContext context, L2PcInstance playerInstance) {
        this.context = context;
        this.playerInstance = playerInstance;
        this.lastUpdateTime = System.currentTimeMillis();
        
        if (context != null) {
            context.setPlayerInstance(playerInstance);
        }
        
        _log.debug("Created EnhancedFakePlayer for bot " + context.getBotId());
    }
    
    /**
     * Получает контекст бота
     * 
     * @return контекст бота
     */
    public BotContext getContext() {
        return context;
    }
    
    /**
     * Получает экземпляр игрока
     * 
     * @return экземпляр игрока
     */
    public L2PcInstance getPlayerInstance() {
        return playerInstance;
    }
    
    /**
     * Проверяет, активен ли бот
     * 
     * @return true если активен
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Активирует бота
     */
    public void activate() {
        isActive = true;
        lastUpdateTime = System.currentTimeMillis();
        _log.debug("Activated bot " + context.getBotId());
    }
    
    /**
     * Деактивирует бота
     */
    public void deactivate() {
        isActive = false;
        _log.debug("Deactivated bot " + context.getBotId());
    }
    
    /**
     * Обновляет время последней активности
     */
    public void updateActivity() {
        lastUpdateTime = System.currentTimeMillis();
        if (context != null) {
            context.updateLastActivity();
        }
    }
    
    /**
     * Получает время последнего обновления
     * 
     * @return время в миллисекундах
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    /**
     * Проверяет, нужно ли обновить бота
     * 
     * @param interval интервал обновления в миллисекундах
     * @return true если нужно обновить
     */
    public boolean needsUpdate(long interval) {
        return System.currentTimeMillis() - lastUpdateTime >= interval;
    }
    
    /**
     * Получает ID бота
     * 
     * @return ID бота
     */
    public int getBotId() {
        return context != null ? context.getBotId() : -1;
    }
    
    /**
     * Получает имя бота
     * 
     * @return имя бота
     */
    public String getBotName() {
        if (context != null) {
            return context.getData("botName", "Unknown");
        }
        return playerInstance != null ? playerInstance.getName() : "Unknown";
    }
    
    /**
     * Получает тип бота
     * 
     * @return тип бота
     */
    public BotType getBotType() {
        if (context != null) {
            return context.getData("botType", BotType.SOLDIER);
        }
        return BotType.SOLDIER;
    }
    
    /**
     * Получает состояние бота
     * 
     * @return состояние бота
     */
    public BotState getBotState() {
        return context != null ? context.getState() : BotState.IDLE;
    }
    
    /**
     * Устанавливает состояние бота
     * 
     * @param state новое состояние
     */
    public void setBotState(BotState state) {
        if (context != null) {
            context.setState(state);
        }
    }
    
    /**
     * Проверяет, жив ли бот
     * 
     * @return true если жив
     */
    public boolean isAlive() {
        return playerInstance != null && !playerInstance.isDead();
    }
    
    /**
     * Проверяет, в бою ли бот
     * 
     * @return true если в бою
     */
    public boolean isInCombat() {
        return playerInstance != null && playerInstance.isInCombat();
    }
    
    /**
     * Получает уровень бота
     * 
     * @return уровень бота
     */
    public int getLevel() {
        return playerInstance != null ? playerInstance.getLevel() : 1;
    }
    
    /**
     * Получает текущее HP бота
     * 
     * @return текущее HP
     */
    public int getCurrentHp() {
        return playerInstance != null ? (int) playerInstance.getCurrentHp() : 0;
    }
    
    /**
     * Получает максимальное HP бота
     * 
     * @return максимальное HP
     */
    public int getMaxHp() {
        return playerInstance != null ? playerInstance.getMaxHp() : 1;
    }
    
    /**
     * Получает текущее MP бота
     * 
     * @return текущее MP
     */
    public int getCurrentMp() {
        return playerInstance != null ? (int) playerInstance.getCurrentMp() : 0;
    }
    
    /**
     * Получает максимальное MP бота
     * 
     * @return максимальное MP
     */
    public int getMaxMp() {
        return playerInstance != null ? playerInstance.getMaxMp() : 1;
    }
    
    /**
     * Получает процент HP бота
     * 
     * @return процент HP (0-100)
     */
    public double getHpPercent() {
        if (playerInstance == null) {
            return 0.0;
        }
        return (double) playerInstance.getCurrentHp() / playerInstance.getMaxHp() * 100.0;
    }
    
    /**
     * Получает процент MP бота
     * 
     * @return процент MP (0-100)
     */
    public double getMpPercent() {
        if (playerInstance == null) {
            return 0.0;
        }
        return (double) playerInstance.getCurrentMp() / playerInstance.getMaxMp() * 100.0;
    }
    
    /**
     * Проверяет, нужно ли восстановиться
     * 
     * @param minHpPercent минимальный процент HP
     * @param minMpPercent минимальный процент MP
     * @return true если нужно восстановиться
     */
    public boolean needsRest(double minHpPercent, double minMpPercent) {
        return getHpPercent() < minHpPercent || getMpPercent() < minMpPercent;
    }
    
    /**
     * Получает координаты бота
     * 
     * @return массив [x, y, z]
     */
    public int[] getLocation() {
        if (playerInstance == null) {
            return new int[]{0, 0, 0};
        }
        return new int[]{
            (int) playerInstance.getX(),
            (int) playerInstance.getY(),
            (int) playerInstance.getZ()
        };
    }
    
    /**
     * Проверяет, находится ли бот в указанной области
     * 
     * @param x координата X
     * @param y координата Y
     * @param radius радиус
     * @return true если в области
     */
    public boolean isInArea(int x, int y, int radius) {
        if (playerInstance == null) {
            return false;
        }
        
        double dx = playerInstance.getX() - x;
        double dy = playerInstance.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance <= radius;
    }
    
    /**
     * Получает статистику бота
     * 
     * @return статистика в виде строки
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Bot ").append(getBotId()).append(" Statistics ===\n");
        stats.append("Name: ").append(getBotName()).append("\n");
        stats.append("Type: ").append(getBotType().getName()).append("\n");
        stats.append("State: ").append(getBotState().getName()).append("\n");
        stats.append("Level: ").append(getLevel()).append("\n");
        stats.append("HP: ").append(getCurrentHp()).append("/").append(getMaxHp())
              .append(" (").append(String.format("%.1f", getHpPercent())).append("%)\n");
        stats.append("MP: ").append(getCurrentMp()).append("/").append(getMaxMp())
              .append(" (").append(String.format("%.1f", getMpPercent())).append("%)\n");
        stats.append("Active: ").append(isActive()).append("\n");
        stats.append("Alive: ").append(isAlive()).append("\n");
        stats.append("In Combat: ").append(isInCombat()).append("\n");
        
        if (context != null) {
            stats.append("Creation Time: ").append(context.getCreationTime()).append("\n");
            stats.append("Last Activity: ").append(context.getLastActivityTime()).append("\n");
        }
        
        return stats.toString();
    }
    
    @Override
    public String toString() {
        return "EnhancedFakePlayer{id=" + getBotId() + 
               ", name=" + getBotName() + 
               ", type=" + getBotType() + 
               ", state=" + getBotState() + 
               ", active=" + isActive() + "}";
    }
}
