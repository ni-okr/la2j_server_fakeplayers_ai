package net.sf.l2j.botmanager.marketplace;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotException;
import net.sf.l2j.botmanager.managers.BotManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import java.util.*;
import java.util.logging.Logger;

/**
 * Рынок ботов для обычных игроков
 */
public class BotMarketplace {
    private static final Logger _log = Logger.getLogger(BotMarketplace.class.getName());

    private static BotMarketplace instance;

    private final BotManager botManager;
    private final Map<BotType, Integer> botPrices = new HashMap<>();
    private final Map<Integer, PersonalBotGroup> playerBots = new HashMap<>();

    private BotMarketplace() {
        this.botManager = BotManager.getInstance();
        initializePrices();
        _log.info("BotMarketplace initialized");
    }

    public static BotMarketplace getInstance() {
        if (instance == null) {
            synchronized (BotMarketplace.class) {
                if (instance == null) {
                    instance = new BotMarketplace();
                }
            }
        }
        return instance;
    }

    /**
     * Инициализирует цены на ботов
     */
    private void initializePrices() {
        // Цены в адене
        botPrices.put(BotType.SOLDIER, 500000);           // 500k адены
        botPrices.put(BotType.OFFICER, 2000000);          // 2M адены
        botPrices.put(BotType.HIGH_OFFICER, 10000000);    // 10M адены
        botPrices.put(BotType.VICE_GUILDMASTER, 50000000); // 50M адены
        botPrices.put(BotType.FARMER, 300000);            // 300k адены
        botPrices.put(BotType.MERCHANT, 800000);          // 800k адены
        botPrices.put(BotType.GUARD, 400000);             // 400k адены
    }

    /**
     * Получает цену бота
     */
    public int getBotPrice(BotType botType) {
        return botPrices.getOrDefault(botType, 0);
    }

    /**
     * Покупает бота для игрока
     */
    public BotContext purchaseBot(L2PcInstance player, BotType botType, String botName) throws BotException {
        int price = getBotPrice(botType);
        if (price <= 0) {
            throw new BotException("Бот этого типа недоступен для покупки");
        }

        // Проверяем, что у игрока достаточно адены
        if (player.getAdena() < price) {
            throw new BotException("Недостаточно адены для покупки бота");
        }

        // Снимаем деньги
        player.reduceAdena("BotPurchase", price, player, true);

        // Создаем бота
        BotContext bot = botManager.createBot(botType, botName);
        bot.setData("owner", player);
        bot.setData("isPersonalBot", true);
        bot.setState(net.sf.l2j.botmanager.core.BotState.IDLE);

        // Добавляем бота в группу игрока
        PersonalBotGroup group = getOrCreatePlayerGroup(player);
        group.addBot(bot);

        _log.info("Player " + player.getName() + " purchased bot " + botName + " for " + price + " adena");

        return bot;
    }

    /**
     * Получает или создает группу ботов игрока
     */
    private PersonalBotGroup getOrCreatePlayerGroup(L2PcInstance player) {
        PersonalBotGroup group = playerBots.get(player.getObjectId());
        if (group == null) {
            group = new PersonalBotGroup(player);
            playerBots.put(player.getObjectId(), group);
        }
        return group;
    }

    /**
     * Получает группу ботов игрока
     */
    public PersonalBotGroup getPlayerBotGroup(L2PcInstance player) {
        return playerBots.get(player.getObjectId());
    }

    /**
     * Получает всех ботов игрока
     */
    public Collection<BotContext> getPlayerBots(L2PcInstance player) {
        PersonalBotGroup group = getPlayerBotGroup(player);
        if (group != null) {
            return group.getAllBots();
        }
        return Collections.emptyList();
    }

    /**
     * Получает ботов игрока по типу
     */
    public Collection<BotContext> getPlayerBotsByType(L2PcInstance player, BotType type) {
        return getPlayerBots(player).stream()
                .filter(bot -> type.equals(bot.getData("botType")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Удаляет бота игрока
     */
    public void removePlayerBot(L2PcInstance player, int botId) throws BotException {
        PersonalBotGroup group = getPlayerBotGroup(player);
        if (group == null) {
            throw new BotException("У игрока нет группы ботов");
        }

        group.removeBot(botId);
        botManager.removeBot(botId);

        _log.info("Player " + player.getName() + " removed bot " + botId);
    }

    /**
     * Отправляет бота в путешествие с игроком
     */
    public void sendBotWithPlayer(L2PcInstance player, int botId) throws BotException {
        BotContext bot = botManager.getBot(botId);
        if (bot == null) {
            throw new BotException("Бот не найден");
        }

        if (!player.equals(bot.getData("owner"))) {
            throw new BotException("Этот бот не принадлежит вам");
        }

        bot.setState(net.sf.l2j.botmanager.core.BotState.MOVING);
        bot.setData("mode", "following_player");
        bot.setData("targetPlayer", player);

        _log.info("Bot " + botId + " is now following player " + player.getName());
    }

    /**
     * Отправляет бота на выполнение задачи
     */
    public void sendBotOnTask(L2PcInstance player, int botId, String task) throws BotException {
        BotContext bot = botManager.getBot(botId);
        if (bot == null) {
            throw new BotException("Бот не найден");
        }

        if (!player.equals(bot.getData("owner"))) {
            throw new BotException("Этот бот не принадлежит вам");
        }

        bot.setState(net.sf.l2j.botmanager.core.BotState.MOVING);
        bot.setData("mode", "task_execution");
        bot.setData("currentTask", task);

        _log.info("Bot " + botId + " is executing task: " + task);
    }

    /**
     * Возвращает бота в режим ожидания
     */
    public void returnBotToIdle(L2PcInstance player, int botId) throws BotException {
        BotContext bot = botManager.getBot(botId);
        if (bot == null) {
            throw new BotException("Бот не найден");
        }

        if (!player.equals(bot.getData("owner"))) {
            throw new BotException("Этот бот не принадлежит вам");
        }

        bot.setState(net.sf.l2j.botmanager.core.BotState.IDLE);
        bot.setData("mode", "idle");
        bot.removeData("targetPlayer");
        bot.removeData("currentTask");

        _log.info("Bot " + botId + " returned to idle mode");
    }

    /**
     * Получает статистику рынка ботов
     */
    public String getMarketStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("Bot Marketplace Statistics:\n");
        stats.append("Total players with bots: ").append(playerBots.size()).append("\n");

        int totalBots = 0;
        for (PersonalBotGroup group : playerBots.values()) {
            totalBots += group.getBotCount();
        }
        stats.append("Total bots sold: ").append(totalBots).append("\n");

        // Статистика по типам
        Map<BotType, Integer> typeCount = new HashMap<>();
        for (PersonalBotGroup group : playerBots.values()) {
            for (BotContext bot : group.getAllBots()) {
                BotType type = bot.getData("botType");
                if (type != null) {
                    typeCount.merge(type, 1, Integer::sum);
                }
            }
        }

        for (Map.Entry<BotType, Integer> entry : typeCount.entrySet()) {
            stats.append(entry.getKey().getName()).append(": ").append(entry.getValue()).append("\n");
        }

        return stats.toString();
    }

    /**
     * Получает статистику ботов игрока
     */
    public String getPlayerBotStatistics(L2PcInstance player) {
        PersonalBotGroup group = getPlayerBotGroup(player);
        if (group == null) {
            return "У вас нет ботов";
        }

        StringBuilder stats = new StringBuilder();
        stats.append("Ваши боты:\n");
        stats.append("Всего ботов: ").append(group.getBotCount()).append("\n");

        // Статистика по типам
        for (BotType type : BotType.values()) {
            int count = group.getBotsByType(type).size();
            if (count > 0) {
                stats.append(type.getName()).append(": ").append(count).append("\n");
            }
        }

        // Статистика по состояниям
        Map<net.sf.l2j.botmanager.core.BotState, Integer> stateCount = new HashMap<>();
        for (BotContext bot : group.getAllBots()) {
            stateCount.merge(bot.getState(), 1, Integer::sum);
        }

        for (Map.Entry<net.sf.l2j.botmanager.core.BotState, Integer> entry : stateCount.entrySet()) {
            stats.append(entry.getKey().getName()).append(": ").append(entry.getValue()).append("\n");
        }

        return stats.toString();
    }
}
