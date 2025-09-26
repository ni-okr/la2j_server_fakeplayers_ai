package net.sf.l2j.botmanager.managers;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.BotException;
import net.sf.l2j.botmanager.events.BotCreatedEvent;
import net.sf.l2j.botmanager.events.BotRemovedEvent;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Менеджер для управления ботами
 */
public class BotManager {
    private static final Logger _log = Logger.getLogger(BotManager.class.getName());

    private static BotManager instance;

    private final Map<Integer, BotContext> bots = new ConcurrentHashMap<>();
    private final AtomicInteger nextBotId = new AtomicInteger(1);
    private final EventManager eventManager;
    private int maxBotCount = 1000;

    private BotManager() {
        this.eventManager = EventManager.getInstance();
        _log.info("BotManager initialized");
    }

    public static BotManager getInstance() {
        if (instance == null) {
            synchronized (BotManager.class) {
                if (instance == null) {
                    instance = new BotManager();
                }
            }
        }
        return instance;
    }

    /**
     * Создает нового бота
     */
    public BotContext createBot(BotType type, String botName) throws BotException {
        if (getBotCount() >= maxBotCount) {
            throw new BotException("Maximum bot count reached: " + maxBotCount);
        }

        int botId = nextBotId.getAndIncrement();
        BotContext context = new BotContext(botId);

        context.setData("botName", botName);
        context.setData("botType", type);

        bots.put(botId, context);

        _log.info("Created bot with ID: " + botId + ", name: " + botName + ", type: " + type);

        // Публикуем событие создания бота
        eventManager.publish(new BotCreatedEvent(context));

        return context;
    }

    /**
     * Создает бота с подключением к персонажу
     */
    public BotContext createBot(BotType type, L2PcInstance playerInstance) throws BotException {
        BotContext context = createBot(type, playerInstance.getName());
        context.setPlayerInstance(playerInstance);
        return context;
    }

    /**
     * Удаляет бота
     */
    public void removeBot(int botId) throws BotException {
        BotContext context = bots.remove(botId);
        if (context == null) {
            throw new BotException("Bot with ID " + botId + " not found");
        }

        _log.info("Removed bot with ID: " + botId);

        // Публикуем событие удаления бота
        eventManager.publish(new BotRemovedEvent(context));
    }

    /**
     * Получает бота по ID
     */
    public BotContext getBot(int botId) {
        return bots.get(botId);
    }

    /**
     * Получает всех ботов
     */
    public Collection<BotContext> getAllBots() {
        return bots.values();
    }

    /**
     * Получает количество ботов
     */
    public int getBotCount() {
        return bots.size();
    }

    /**
     * Проверяет, существует ли бот
     */
    public boolean hasBot(int botId) {
        return bots.containsKey(botId);
    }

    /**
     * Получает ботов по типу
     */
    public Collection<BotContext> getBotsByType(BotType type) {
        return bots.values().stream()
                .filter(bot -> type.equals(bot.getData("botType")))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Получает активных ботов
     */
    public Collection<BotContext> getActiveBots() {
        return bots.values().stream()
                .filter(bot -> bot.getPlayerInstance() != null)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Получает ботов по состоянию
     */
    public Collection<BotContext> getBotsByState(BotState state) {
        return bots.values().stream()
                .filter(bot -> bot.getState() == state)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Получает ботов владельца
     */
    public Collection<BotContext> getBotsByOwner(L2PcInstance owner) {
        return bots.values().stream()
                .filter(bot -> owner.equals(bot.getData("owner")))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Назначает владельца бота
     */
    public void setBotOwner(int botId, L2PcInstance owner) {
        BotContext bot = getBot(botId);
        if (bot != null) {
            bot.setData("owner", owner);
        }
    }

    /**
     * Получает максимальное количество ботов
     */
    public int getMaxBotCount() {
        return maxBotCount;
    }

    /**
     * Устанавливает максимальное количество ботов
     */
    public void setMaxBotCount(int maxCount) {
        this.maxBotCount = maxCount;
    }

    /**
     * Очищает всех ботов
     */
    public void clearAllBots() {
        _log.info("Clearing all bots...");
        bots.clear();
        _log.info("All bots cleared");
    }

    /**
     * Получает статистику ботов
     */
    public String getBotStatistics() {
        int totalBots = getBotCount();
        int activeBots = getActiveBots().size();

        StringBuilder stats = new StringBuilder();
        stats.append("Bot Statistics:\n");
        stats.append("Total bots: ").append(totalBots).append("\n");
        stats.append("Active bots: ").append(activeBots).append("\n");
        stats.append("Inactive bots: ").append(totalBots - activeBots).append("\n");

        // Статистика по типам
        for (BotType type : BotType.values()) {
            int count = getBotsByType(type).size();
            if (count > 0) {
                stats.append(type.getName()).append(": ").append(count).append("\n");
            }
        }

        // Статистика по состояниям
        for (BotState state : BotState.values()) {
            int count = getBotsByState(state).size();
            if (count > 0) {
                stats.append(state.getName()).append(": ").append(count).append("\n");
            }
        }

        return stats.toString();
    }
}
