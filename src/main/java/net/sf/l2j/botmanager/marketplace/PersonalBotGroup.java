package net.sf.l2j.botmanager.marketplace;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import java.util.*;
import java.util.logging.Logger;

/**
 * Личная группа ботов игрока
 */
public class PersonalBotGroup {
    private static final Logger _log = Logger.getLogger(PersonalBotGroup.class.getName());

    private final L2PcInstance owner;
    private final int groupId;
    private final Map<Integer, BotContext> bots = new HashMap<>();
    private final Map<BotType, Integer> maxBotsByType = new HashMap<>();
    private final Map<Integer, String> activeTasks = new HashMap<>();

    public PersonalBotGroup(L2PcInstance owner) {
        this.owner = owner;
        this.groupId = Math.abs(Objects.hash(owner.getObjectId(), System.currentTimeMillis()));

        // Устанавливаем лимиты ботов для обычных игроков
        initializeBotLimits();
    }

    private void initializeBotLimits() {
        // Для обычных игроков лимиты ниже, чем для владельцев замков
        maxBotsByType.put(BotType.SOLDIER, 3);
        maxBotsByType.put(BotType.OFFICER, 1);
        maxBotsByType.put(BotType.HIGH_OFFICER, 0); // Недоступно обычным игрокам
        maxBotsByType.put(BotType.VICE_GUILDMASTER, 0); // Недоступно обычным игрокам
        maxBotsByType.put(BotType.FARMER, 5);
        maxBotsByType.put(BotType.MERCHANT, 2);
        maxBotsByType.put(BotType.GUARD, 3);
    }

    /**
     * Добавляет бота в группу
     */
    public void addBot(BotContext bot) {
        if (bot == null) return;

        BotType botType = bot.getData("botType");
        if (botType != null && canAddMoreBots(botType)) {
            bots.put(bot.getBotId(), bot);
            _log.info("Added bot " + bot.getBotId() + " (" + botType.getName() + ") to player " + owner.getName() + "'s group");
        }
    }

    /**
     * Удаляет бота из группы
     */
    public void removeBot(int botId) {
        BotContext bot = bots.remove(botId);
        if (bot != null) {
            activeTasks.remove(botId);
            _log.info("Removed bot " + botId + " from player " + owner.getName() + "'s group");
        }
    }

    /**
     * Получает всех ботов группы
     */
    public Collection<BotContext> getAllBots() {
        return bots.values();
    }

    /**
     * Получает ботов по типу
     */
    public Collection<BotContext> getBotsByType(BotType type) {
        return bots.values().stream()
                .filter(bot -> type.equals(bot.getData("botType")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Получает активных ботов
     */
    public Collection<BotContext> getActiveBots() {
        return bots.values().stream()
                .filter(bot -> bot.getState() != BotState.IDLE && bot.getState() != BotState.DISCONNECTED)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Получает ботов в режиме ожидания
     */
    public Collection<BotContext> getIdleBots() {
        return bots.values().stream()
                .filter(bot -> bot.getState() == BotState.IDLE)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Получает ботов, следующих за игроком
     */
    public Collection<BotContext> getFollowingBots() {
        return bots.values().stream()
                .filter(bot -> "following_player".equals(bot.getData("mode")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Назначает задачу боту
     */
    public void assignTask(int botId, String task) {
        BotContext bot = bots.get(botId);
        if (bot != null) {
            activeTasks.put(botId, task);
            bot.setData("currentTask", task);
            bot.setState(BotState.MOVING);
            _log.info("Assigned task '" + task + "' to bot " + botId);
        }
    }

    /**
     * Завершает задачу бота
     */
    public void completeTask(int botId) {
        BotContext bot = bots.get(botId);
        if (bot != null) {
            activeTasks.remove(botId);
            bot.removeData("currentTask");
            bot.setState(BotState.IDLE);
            _log.info("Completed task for bot " + botId);
        }
    }

    /**
     * Получает текущую задачу бота
     */
    public String getBotTask(int botId) {
        return activeTasks.get(botId);
    }

    /**
     * Отправляет ботов в путешествие с игроком
     */
    public void sendBotsWithPlayer(Collection<BotContext> bots) {
        for (BotContext bot : bots) {
            bot.setState(BotState.MOVING);
            bot.setData("mode", "following_player");
            bot.setData("targetPlayer", owner);
        }

        _log.info("Sent " + bots.size() + " bots to follow player " + owner.getName());
    }

    /**
     * Возвращает всех ботов в режим ожидания
     */
    public void returnAllBotsToIdle() {
        for (BotContext bot : bots.values()) {
            bot.setState(BotState.IDLE);
            bot.setData("mode", "idle");
            bot.removeData("targetPlayer");
            bot.removeData("currentTask");
        }
        activeTasks.clear();
        _log.info("Returned all bots to idle mode for player " + owner.getName());
    }

    /**
     * Получает количество ботов
     */
    public int getBotCount() {
        return bots.size();
    }

    /**
     * Получает количество ботов определенного типа
     */
    public int getBotCount(BotType type) {
        return getBotsByType(type).size();
    }

    /**
     * Получает максимальное количество ботов определенного типа
     */
    public int getMaxBots(BotType type) {
        return maxBotsByType.getOrDefault(type, 0);
    }

    /**
     * Проверяет, можно ли добавить еще ботов этого типа
     */
    public boolean canAddMoreBots(BotType type) {
        int currentCount = getBotCount(type);
        int maxCount = getMaxBots(type);
        return currentCount < maxCount;
    }

    /**
     * Получает статистику группы ботов
     */
    public String getGroupStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("Bot Group Statistics for ").append(owner.getName()).append(":\n");
        stats.append("Total bots: ").append(getBotCount()).append("\n");
        stats.append("Active bots: ").append(getActiveBots().size()).append("\n");
        stats.append("Idle bots: ").append(getIdleBots().size()).append("\n");
        stats.append("Following bots: ").append(getFollowingBots().size()).append("\n");

        // Статистика по типам
        for (BotType type : BotType.values()) {
            int count = getBotCount(type);
            if (count > 0) {
                stats.append(type.getName()).append(": ").append(count).append("/").append(getMaxBots(type)).append("\n");
            }
        }

        // Статистика по состояниям
        Map<BotState, Integer> stateCount = new HashMap<>();
        for (BotContext bot : bots.values()) {
            stateCount.merge(bot.getState(), 1, Integer::sum);
        }

        for (Map.Entry<BotState, Integer> entry : stateCount.entrySet()) {
            stats.append(entry.getKey().getName()).append(": ").append(entry.getValue()).append("\n");
        }

        // Активные задачи
        if (!activeTasks.isEmpty()) {
            stats.append("\nActive tasks:\n");
            for (Map.Entry<Integer, String> entry : activeTasks.entrySet()) {
                stats.append("Bot ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return stats.toString();
    }

    /**
     * Распускает группу ботов
     */
    public void disband() {
        for (BotContext bot : bots.values()) {
            bot.setState(BotState.DISCONNECTED);
        }
        bots.clear();
        activeTasks.clear();
        _log.info("Disbanded bot group for player " + owner.getName());
    }

    // Геттеры
    public L2PcInstance getOwner() { return owner; }
    public int getGroupId() { return groupId; }
}
