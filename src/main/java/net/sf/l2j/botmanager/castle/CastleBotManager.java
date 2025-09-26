package net.sf.l2j.botmanager.castle;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotException;
import net.sf.l2j.botmanager.managers.BotManager;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import java.util.*;
import java.util.logging.Logger;

/**
 * Менеджер ботов для замков
 */
public class CastleBotManager {
    private static final Logger _log = Logger.getLogger(CastleBotManager.class.getName());

    private static CastleBotManager instance;

    private final BotManager botManager;
    private final Map<Integer, CastleBotGroup> castleGroups = new HashMap<>();

    private CastleBotManager() {
        this.botManager = BotManager.getInstance();
        _log.info("CastleBotManager initialized");
    }

    public static CastleBotManager getInstance() {
        if (instance == null) {
            synchronized (CastleBotManager.class) {
                if (instance == null) {
                    instance = new CastleBotManager();
                }
            }
        }
        return instance;
    }

    /**
     * Создает группу ботов для замка
     */
    public CastleBotGroup createBotGroup(int castleId, L2PcInstance owner) throws BotException {
        CastleBotGroup group = new CastleBotGroup(castleId, owner);
        castleGroups.put(castleId, group);
        _log.info("Created bot group for castle " + castleId + " owned by " + owner.getName());
        return group;
    }

    /**
     * Получает группу ботов для замка
     */
    public CastleBotGroup getBotGroup(int castleId) {
        return castleGroups.get(castleId);
    }

    /**
     * Получает группу ботов игрока
     */
    public CastleBotGroup getBotGroupByOwner(L2PcInstance owner) {
        return castleGroups.values().stream()
                .filter(group -> group.getOwner().equals(owner))
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет группу ботов
     */
    public void removeBotGroup(int castleId) {
        CastleBotGroup group = castleGroups.remove(castleId);
        if (group != null) {
            group.disband();
            _log.info("Removed bot group for castle " + castleId);
        }
    }

    /**
     * Покупает бота для замка
     */
    public BotContext purchaseBot(int castleId, BotType botType, String botName, L2PcInstance owner) throws BotException {
        CastleBotGroup group = getBotGroup(castleId);
        if (group == null) {
            group = createBotGroup(castleId, owner);
        }

        // Проверяем, может ли игрок покупать ботов для этого замка
        if (!group.canPlayerManage(owner)) {
            throw new BotException("Player cannot manage bots for this castle");
        }

        // Создаем бота
        BotContext bot = botManager.createBot(botType, botName);
        bot.setData("owner", owner);
        bot.setData("castleId", castleId);
        bot.setData("groupId", group.getGroupId());

        // Добавляем бота в группу
        group.addBot(bot);

        _log.info("Player " + owner.getName() + " purchased bot " + botName + " for castle " + castleId);
        return bot;
    }

    /**
     * Получает всех ботов замка
     */
    public Collection<BotContext> getCastleBots(int castleId) {
        CastleBotGroup group = getBotGroup(castleId);
        if (group != null) {
            return group.getAllBots();
        }
        return Collections.emptyList();
    }

    /**
     * Получает ботов замка по типу
     */
    public Collection<BotContext> getCastleBotsByType(int castleId, BotType type) {
        return getCastleBots(castleId).stream()
                .filter(bot -> type.equals(bot.getData("botType")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Получает ботов замка по владельцу
     */
    public Collection<BotContext> getCastleBotsByOwner(int castleId, L2PcInstance owner) {
        return getCastleBots(castleId).stream()
                .filter(bot -> owner.equals(bot.getData("owner")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Подготавливает ботов к осаде
     */
    public void prepareForSiege(int castleId) {
        CastleBotGroup group = getBotGroup(castleId);
        if (group != null) {
            group.prepareForSiege();
            _log.info("Prepared bots for siege at castle " + castleId);
        }
    }

    /**
     * Переводит ботов в режим мирного времени
     */
    public void enterPeaceMode(int castleId) {
        CastleBotGroup group = getBotGroup(castleId);
        if (group != null) {
            group.enterPeaceMode();
            _log.info("Entered peace mode for castle " + castleId);
        }
    }

    /**
     * Получает статистику ботов замка
     */
    public String getCastleBotStatistics(int castleId) {
        CastleBotGroup group = getBotGroup(castleId);
        if (group == null) {
            return "No bots found for castle " + castleId;
        }

        StringBuilder stats = new StringBuilder();
        stats.append("Castle Bot Statistics for Castle ").append(castleId).append(":\n");
        stats.append("Owner: ").append(group.getOwner().getName()).append("\n");
        stats.append("Total bots: ").append(group.getBotCount()).append("\n");

        // Статистика по типам
        for (BotType type : BotType.values()) {
            Collection<BotContext> bots = getCastleBotsByType(castleId, type);
            if (!bots.isEmpty()) {
                stats.append(type.getName()).append(": ").append(bots.size()).append("\n");
            }
        }

        return stats.toString();
    }

    /**
     * Получает общую статистику всех замков
     */
    public String getAllCastlesStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("All Castles Bot Statistics:\n");

        for (Map.Entry<Integer, CastleBotGroup> entry : castleGroups.entrySet()) {
            stats.append("Castle ").append(entry.getKey()).append(": ")
                 .append(entry.getValue().getBotCount()).append(" bots\n");
        }

        return stats.toString();
    }
}
