package net.sf.l2j.botmanager.castle;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import java.util.*;
import java.util.logging.Logger;

/**
 * Группа ботов замка
 */
public class CastleBotGroup {
    private static final Logger _log = Logger.getLogger(CastleBotGroup.class.getName());

    private final int castleId;
    private final L2PcInstance owner;
    private final int groupId;
    private final Map<Integer, BotContext> bots = new HashMap<>();
    private final Map<BotType, Integer> maxBotsByType = new HashMap<>();

    // Иерархия командиров
    private BotContext viceGuildmaster;
    private final List<BotContext> highOfficers = new ArrayList<>();
    private final List<BotContext> officers = new ArrayList<>();

    public CastleBotGroup(int castleId, L2PcInstance owner) {
        this.castleId = castleId;
        this.owner = owner;
        this.groupId = Math.abs(Objects.hash(castleId, owner.getObjectId(), System.currentTimeMillis()));

        // Устанавливаем лимиты по типам ботов
        initializeBotLimits();
    }

    private void initializeBotLimits() {
        // Солдаты - без ограничений
        maxBotsByType.put(BotType.SOLDIER, Integer.MAX_VALUE);

        // Офицеры - максимум 10 на офицера
        maxBotsByType.put(BotType.OFFICER, 10);

        // Высшие офицеры - максимум 3
        maxBotsByType.put(BotType.HIGH_OFFICER, 3);

        // Заместитель гильдии - только 1
        maxBotsByType.put(BotType.VICE_GUILDMASTER, 1);

        // Другие типы - по 5 каждого
        maxBotsByType.put(BotType.FARMER, 5);
        maxBotsByType.put(BotType.MERCHANT, 5);
        maxBotsByType.put(BotType.GUARD, 5);
    }

    /**
     * Добавляет бота в группу
     */
    public void addBot(BotContext bot) {
        if (bot == null) return;

        bots.put(bot.getBotId(), bot);

        BotType botType = bot.getData("botType");
        if (botType != null) {
            organizeHierarchy(bot);
            _log.info("Added bot " + bot.getBotId() + " (" + botType.getName() + ") to castle group " + castleId);
        }
    }

    /**
     * Удаляет бота из группы
     */
    public void removeBot(int botId) {
        BotContext bot = bots.remove(botId);
        if (bot != null) {
            BotType botType = bot.getData("botType");
            if (botType != null) {
                reorganizeHierarchy();
                _log.info("Removed bot " + botId + " (" + botType.getName() + ") from castle group " + castleId);
            }
        }
    }

    /**
     * Организует иерархию командиров
     */
    private void organizeHierarchy(BotContext newBot) {
        BotType botType = newBot.getData("botType");

        switch (botType) {
            case VICE_GUILDMASTER:
                if (viceGuildmaster == null) {
                    viceGuildmaster = newBot;
                }
                break;

            case HIGH_OFFICER:
                if (highOfficers.size() < maxBotsByType.get(BotType.HIGH_OFFICER)) {
                    highOfficers.add(newBot);
                }
                break;

            case OFFICER:
                if (officers.size() < maxBotsByType.get(BotType.OFFICER)) {
                    officers.add(newBot);
                }
                break;
        }
    }

    /**
     * Реорганизует иерархию после удаления бота
     */
    private void reorganizeHierarchy() {
        // Простая реорганизация - можно улучшить
        viceGuildmaster = null;
        highOfficers.clear();
        officers.clear();

        // Перестраиваем иерархию для оставшихся ботов
        for (BotContext bot : bots.values()) {
            organizeHierarchy(bot);
        }
    }

    /**
     * Подготавливает ботов к осаде
     */
    public void prepareForSiege() {
        for (BotContext bot : bots.values()) {
            bot.setState(BotState.GUARDING);
            bot.setData("mode", "siege_defense");

            // Устанавливаем цели для защиты замка
            bot.setData("castle_defense_point", getRandomDefensePoint());
        }

        _log.info("Prepared " + bots.size() + " bots for siege defense at castle " + castleId);
    }

    /**
     * Переводит ботов в режим мирного времени
     */
    public void enterPeaceMode() {
        for (BotContext bot : bots.values()) {
            BotType botType = bot.getData("botType");
            if (botType == BotType.SOLDIER || botType == BotType.OFFICER || botType == BotType.HIGH_OFFICER) {
                bot.setState(BotState.PATROLLING);
                bot.setData("mode", "peace_patrol");
            } else {
                bot.setState(BotState.IDLE);
                bot.setData("mode", "peace_idle");
            }
        }

        _log.info("Entered peace mode for " + bots.size() + " bots at castle " + castleId);
    }

    /**
     * Получает случайную точку обороны
     */
    private String getRandomDefensePoint() {
        String[] defensePoints = {
            "main_gate", "left_tower", "right_tower", "inner_court", "throne_room"
        };
        return defensePoints[new Random().nextInt(defensePoints.length)];
    }

    /**
     * Проверяет, может ли игрок управлять ботами
     */
    public boolean canPlayerManage(L2PcInstance player) {
        // Только владелец замка может управлять ботами
        return player.equals(owner) || player.getClanId() == owner.getClanId();
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
     * Получает солдат группы
     */
    public Collection<BotContext> getSoldiers() {
        return getBotsByType(BotType.SOLDIER);
    }

    /**
     * Получает офицеров группы
     */
    public Collection<BotContext> getOfficers() {
        return getBotsByType(BotType.OFFICER);
    }

    /**
     * Получает высших офицеров
     */
    public Collection<BotContext> getHighOfficers() {
        return getBotsByType(BotType.HIGH_OFFICER);
    }

    /**
     * Получает заместителя гильдии
     */
    public BotContext getViceGuildmaster() {
        return viceGuildmaster;
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
        return maxCount == Integer.MAX_VALUE || currentCount < maxCount;
    }

    /**
     * Распускает группу ботов
     */
    public void disband() {
        for (BotContext bot : bots.values()) {
            bot.setState(BotState.DISCONNECTED);
        }
        bots.clear();
        viceGuildmaster = null;
        highOfficers.clear();
        officers.clear();

        _log.info("Disbanded bot group for castle " + castleId);
    }

    // Геттеры
    public int getCastleId() { return castleId; }
    public L2PcInstance getOwner() { return owner; }
    public int getGroupId() { return groupId; }
}
