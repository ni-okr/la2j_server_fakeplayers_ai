package net.sf.l2j.botmanager.castle;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import java.util.logging.Logger;

/**
 * Менеджер меню для управления ботами в замках
 */
public class CastleMenuManager {
    private static final Logger _log = Logger.getLogger(CastleMenuManager.class.getName());

    private static CastleMenuManager instance;

    private CastleMenuManager() {
        _log.info("CastleMenuManager initialized");
    }

    public static CastleMenuManager getInstance() {
        if (instance == null) {
            synchronized (CastleMenuManager.class) {
                if (instance == null) {
                    instance = new CastleMenuManager();
                }
            }
        }
        return instance;
    }

    /**
     * Показывает главное меню управления замком
     */
    public void showMainMenu(L2PcInstance player, int castleId) {
        Castle castle = getCastleById(castleId);
        if (castle == null) {
            player.sendMessage("Замок не найден.");
            return;
        }

        if (!canManageCastle(player, castle)) {
            player.sendMessage("У вас нет прав на управление этим замком.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_main.htm");
        html.replace("%castleName%", castle.getName());
        html.replace("%castleId%", String.valueOf(castleId));

        StringBuilder menu = new StringBuilder();
        menu.append("<table width=300>");

        // Меню покупки ботов
        menu.append("<tr><td><button value=\"Купить ботов\" action=\"bypass -h castle_bot_purchase ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Меню управления осадой
        menu.append("<tr><td><button value=\"Управление осадой\" action=\"bypass -h castle_bot_siege ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Меню мирного времени
        menu.append("<tr><td><button value=\"Мирное время\" action=\"bypass -h castle_bot_peace ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Меню семи печатей
        menu.append("<tr><td><button value=\"Семь Печатей\" action=\"bypass -h castle_bot_seven_seals ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Меню олимпиады
        menu.append("<tr><td><button value=\"Олимпиада\" action=\"bypass -h castle_bot_olympiad ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Статистика
        menu.append("<tr><td><button value=\"Статистика\" action=\"bypass -h castle_bot_stats ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        menu.append("</table>");

        html.replace("%menuButtons%", menu.toString());

        // Добавляем статистику
        CastleBotManager botManager = CastleBotManager.getInstance();
        String stats = botManager.getCastleBotStatistics(castleId);
        html.replace("%castleStats%", stats.replace("\n", "<br>"));

        player.sendPacket(html);
    }

    /**
     * Показывает меню покупки ботов
     */
    public void showPurchaseMenu(L2PcInstance player, int castleId) {
        Castle castle = getCastleById(castleId);
        if (castle == null || !canManageCastle(player, castle)) {
            player.sendMessage("У вас нет прав на покупку ботов для этого замка.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_purchase.htm");
        html.replace("%castleName%", castle.getName());

        StringBuilder purchaseOptions = new StringBuilder();
        purchaseOptions.append("<table width=300>");

        // Солдаты
        purchaseOptions.append("<tr><td>Солдат</td><td>Рядовой боец для защиты замка</td>");
        purchaseOptions.append("<td><button value=\"Купить\" action=\"bypass -h castle_bot_purchase_soldier ")
             .append(castleId).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Офицеры
        purchaseOptions.append("<tr><td>Офицер</td><td>Командир группы солдат</td>");
        purchaseOptions.append("<td><button value=\"Купить\" action=\"bypass -h castle_bot_purchase_officer ")
             .append(castleId).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Высший офицер
        purchaseOptions.append("<tr><td>Высший офицер</td><td>Стратегический командир</td>");
        purchaseOptions.append("<td><button value=\"Купить\" action=\"bypass -h castle_bot_purchase_high_officer ")
             .append(castleId).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Заместитель гильдии
        purchaseOptions.append("<tr><td>Заместитель гильдии</td><td>Главный стратег</td>");
        purchaseOptions.append("<td><button value=\"Купить\" action=\"bypass -h castle_bot_purchase_vice_guildmaster ")
             .append(castleId).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        purchaseOptions.append("</table>");

        html.replace("%purchaseOptions%", purchaseOptions.toString());
        html.replace("%castleId%", String.valueOf(castleId));

        player.sendPacket(html);
    }

    /**
     * Показывает меню управления осадой
     */
    public void showSiegeMenu(L2PcInstance player, int castleId) {
        Castle castle = getCastleById(castleId);
        if (castle == null || !canManageCastle(player, castle)) {
            player.sendMessage("У вас нет прав на управление осадой.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_siege.htm");
        html.replace("%castleName%", castle.getName());

        StringBuilder siegeControls = new StringBuilder();
        siegeControls.append("<table width=300>");

        // Подготовить к осаде
        siegeControls.append("<tr><td><button value=\"Подготовить к осаде\" action=\"bypass -h castle_bot_prepare_siege ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Стратегические команды
        siegeControls.append("<tr><td><button value=\"Защитить главные ворота\" action=\"bypass -h castle_bot_defend_gates ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        siegeControls.append("<tr><td><button value=\"Защитить стены\" action=\"bypass -h castle_bot_defend_walls ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        siegeControls.append("<tr><td><button value=\"Контратака\" action=\"bypass -h castle_bot_counter_attack ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        siegeControls.append("</table>");

        html.replace("%siegeControls%", siegeControls.toString());
        html.replace("%castleId%", String.valueOf(castleId));

        player.sendPacket(html);
    }

    /**
     * Показывает меню мирного времени
     */
    public void showPeaceMenu(L2PcInstance player, int castleId) {
        Castle castle = getCastleById(castleId);
        if (castle == null || !canManageCastle(player, castle)) {
            player.sendMessage("У вас нет прав на управление в мирное время.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_peace.htm");
        html.replace("%castleName%", castle.getName());

        StringBuilder peaceControls = new StringBuilder();
        peaceControls.append("<table width=300>");

        // Режимы мирного времени
        peaceControls.append("<tr><td><button value=\"Режим патрулирования\" action=\"bypass -h castle_bot_patrol_mode ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        peaceControls.append("<tr><td><button value=\"Режим фарма\" action=\"bypass -h castle_bot_farm_mode ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        peaceControls.append("<tr><td><button value=\"Режим торговли\" action=\"bypass -h castle_bot_trade_mode ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Управление ресурсами
        peaceControls.append("<tr><td><br></td></tr>");
        peaceControls.append("<tr><td><font color=\"LEVEL\">Управление ресурсами:</font></td></tr>");
        peaceControls.append("<tr><td><button value=\"Собрать ресурсы\" action=\"bypass -h castle_bot_gather_resources ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        peaceControls.append("</table>");

        html.replace("%peaceControls%", peaceControls.toString());
        html.replace("%castleId%", String.valueOf(castleId));

        player.sendPacket(html);
    }

    /**
     * Показывает меню семи печатей
     */
    public void showSevenSealsMenu(L2PcInstance player, int castleId) {
        Castle castle = getCastleById(castleId);
        if (castle == null || !canManageCastle(player, castle)) {
            player.sendMessage("У вас нет прав на управление в период семи печатей.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_seven_seals.htm");
        html.replace("%castleName%", castle.getName());

        StringBuilder sevenSealsControls = new StringBuilder();
        sevenSealsControls.append("<table width=300>");

        // Управление подземельями
        sevenSealsControls.append("<tr><td><button value=\"Фарм Necropolis\" action=\"bypass -h castle_bot_farm_necropolis ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        sevenSealsControls.append("<tr><td><button value=\"Фарм Catacombs\" action=\"bypass -h castle_bot_farm_catacombs ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Защита печатей
        sevenSealsControls.append("<tr><td><br></td></tr>");
        sevenSealsControls.append("<tr><td><font color=\"LEVEL\">Защита печатей:</font></td></tr>");
        sevenSealsControls.append("<tr><td><button value=\"Защитить печати\" action=\"bypass -h castle_bot_defend_seals ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        sevenSealsControls.append("</table>");

        html.replace("%sevenSealsControls%", sevenSealsControls.toString());
        html.replace("%castleId%", String.valueOf(castleId));

        player.sendPacket(html);
    }

    /**
     * Показывает меню олимпиады
     */
    public void showOlympiadMenu(L2PcInstance player, int castleId) {
        Castle castle = getCastleById(castleId);
        if (castle == null || !canManageCastle(player, castle)) {
            player.sendMessage("У вас нет прав на управление олимпиадой.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_olympiad.htm");
        html.replace("%castleName%", castle.getName());

        StringBuilder olympiadControls = new StringBuilder();
        olympiadControls.append("<table width=300>");

        // Подготовка к олимпиаде
        olympiadControls.append("<tr><td><button value=\"Подготовить к олимпиаде\" action=\"bypass -h castle_bot_prepare_olympiad ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        // Управление участниками
        olympiadControls.append("<tr><td><button value=\"Управление участниками\" action=\"bypass -h castle_bot_manage_participants ")
             .append(castleId).append("\" width=200 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        olympiadControls.append("</table>");

        html.replace("%olympiadControls%", olympiadControls.toString());
        html.replace("%castleId%", String.valueOf(castleId));

        player.sendPacket(html);
    }

    /**
     * Показывает статистику ботов замка
     */
    public void showStatistics(L2PcInstance player, int castleId) {
        CastleBotManager botManager = CastleBotManager.getInstance();
        String stats = botManager.getCastleBotStatistics(castleId);

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/castle_bot_stats.htm");

        html.replace("%castleStats%", stats.replace("\n", "<br>"));
        html.replace("%castleId%", String.valueOf(castleId));

        player.sendPacket(html);
    }

    /**
     * Получает замок по ID
     */
    private Castle getCastleById(int castleId) {
        // Здесь должна быть логика получения замка из системы замков L2J
        // Пока заглушка
        return null;
    }

    /**
     * Проверяет, может ли игрок управлять замком
     */
    private boolean canManageCastle(L2PcInstance player, Castle castle) {
        if (castle == null) return false;

        // Владелец замка
        if (castle.getOwnerId() == player.getClanId()) {
            return true;
        }

        // Лидер клана
        return player.isClanLeader();
    }
}
