package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.serverpackets.CharTemplates;
import net.sf.l2j.gameserver.templates.PcTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Менеджер подключения ботов к L2J GameServer
 */
public class BotConnectionManager {
    
    private static final Logger _log = Logger.getLogger(BotConnectionManager.class);
    
    // ==================== SINGLETON ====================
    
    private static volatile BotConnectionManager instance;
    
    public static BotConnectionManager getInstance() {
        if (instance == null) {
            synchronized (BotConnectionManager.class) {
                if (instance == null) {
                    instance = new BotConnectionManager();
                }
            }
        }
        return instance;
    }
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** Карта подключенных ботов */
    private final Map<Integer, EnhancedFakePlayer> connectedBots;
    
    /** Счетчик ID ботов */
    private final AtomicInteger botIdCounter;
    
    /** Максимальное количество ботов */
    private static final int MAX_BOTS = 1000;
    
    /** Статус менеджера */
    private volatile boolean isActive = false;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    private BotConnectionManager() {
        this.connectedBots = new ConcurrentHashMap<>();
        this.botIdCounter = new AtomicInteger(1);
        _log.info("BotConnectionManager initialized");
    }
    
    // ==================== УПРАВЛЕНИЕ ПОДКЛЮЧЕНИЯМИ ====================
    
    /**
     * Подключает бота к серверу
     * 
     * @param botType тип бота
     * @param botName имя бота
     * @return контекст подключенного бота или null при ошибке
     */
    public BotContext connectBot(BotType botType, String botName) {
        if (!isActive) {
            _log.warn("BotConnectionManager is not active");
            return null;
        }
        
        if (connectedBots.size() >= MAX_BOTS) {
            _log.warn("Maximum number of bots reached: " + MAX_BOTS);
            return null;
        }
        
        try {
            int botId = botIdCounter.getAndIncrement();
            BotContext context = new BotContext(botId);
            context.setBotType(botType);
            
            // Создание L2PcInstance для бота
            L2PcInstance playerInstance = createPlayerInstance(botName, botType);
            if (playerInstance == null) {
                _log.error("Failed to create player instance for bot: " + botName);
                return null;
            }
            
            // Создание EnhancedFakePlayer
            EnhancedFakePlayer fakePlayer = new EnhancedFakePlayer(playerInstance, context);
            context.setBot(fakePlayer);
            
            // Регистрация в GameServer
            if (!registerBotInGameServer(fakePlayer)) {
                _log.error("Failed to register bot in GameServer: " + botName);
                return null;
            }
            
            // Добавление в список подключенных ботов
            connectedBots.put(botId, fakePlayer);
            
            _log.info("Bot connected successfully: " + botName + " (ID: " + botId + ", Type: " + botType + ")");
            return context;
            
        } catch (Exception e) {
            _log.error("Error connecting bot: " + botName, e);
            return null;
        }
    }
    
    /**
     * Отключает бота от сервера
     * 
     * @param botId ID бота
     * @return true если бот успешно отключен
     */
    public boolean disconnectBot(int botId) {
        EnhancedFakePlayer bot = connectedBots.remove(botId);
        if (bot == null) {
            _log.warn("Bot not found for disconnection: " + botId);
            return false;
        }
        
        try {
            // Отключение от GameServer
            unregisterBotFromGameServer(bot);
            
            // Очистка ресурсов
            bot.cleanup();
            
            _log.info("Bot disconnected successfully: " + botId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error disconnecting bot: " + botId, e);
            return false;
        }
    }
    
    /**
     * Получает бота по ID
     * 
     * @param botId ID бота
     * @return бот или null если не найден
     */
    public EnhancedFakePlayer getBot(int botId) {
        return connectedBots.get(botId);
    }
    
    /**
     * Получает всех подключенных ботов
     * 
     * @return карта ботов
     */
    public Map<Integer, EnhancedFakePlayer> getAllBots() {
        return new ConcurrentHashMap<>(connectedBots);
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает L2PcInstance для бота
     */
    private L2PcInstance createPlayerInstance(String botName, BotType botType) {
        try {
            // Создание клиента для бота
            L2GameClient client = new L2GameClient(null, null, true);
            
            // Создание шаблона персонажа
            PcTemplate template = createBotTemplate(botType);
            if (template == null) {
                return null;
            }
            
            // Создание L2PcInstance
            L2PcInstance player = new L2PcInstance(template, client, botName);
            
            // Настройка базовых параметров
            setupBotPlayer(player, botType);
            
            return player;
            
        } catch (Exception e) {
            _log.error("Error creating player instance for bot: " + botName, e);
            return null;
        }
    }
    
    /**
     * Создает шаблон персонажа для бота
     */
    private PcTemplate createBotTemplate(BotType botType) {
        // Здесь должна быть логика создания шаблона в зависимости от типа бота
        // Пока возвращаем базовый шаблон
        return new PcTemplate();
    }
    
    /**
     * Настраивает параметры игрока-бота
     */
    private void setupBotPlayer(L2PcInstance player, BotType botType) {
        // Базовые настройки
        player.setLevel(1);
        player.setExp(0);
        player.setSp(0);
        
        // Настройки в зависимости от типа бота
        switch (botType) {
            case FARMER:
                setupFarmerBot(player);
                break;
            case PVP:
                setupPvpBot(player);
                break;
            case SUPPORT:
                setupSupportBot(player);
                break;
            default:
                setupDefaultBot(player);
                break;
        }
    }
    
    /**
     * Настройка бота-фермера
     */
    private void setupFarmerBot(L2PcInstance player) {
        player.setLevel(20);
        // Дополнительные настройки для фермера
    }
    
    /**
     * Настройка PvP бота
     */
    private void setupPvpBot(L2PcInstance player) {
        player.setLevel(40);
        // Дополнительные настройки для PvP
    }
    
    /**
     * Настройка бота поддержки
     */
    private void setupSupportBot(L2PcInstance player) {
        player.setLevel(30);
        // Дополнительные настройки для поддержки
    }
    
    /**
     * Настройка бота по умолчанию
     */
    private void setupDefaultBot(L2PcInstance player) {
        player.setLevel(10);
    }
    
    /**
     * Регистрирует бота в GameServer
     */
    private boolean registerBotInGameServer(EnhancedFakePlayer bot) {
        try {
            // Добавление в мир игры
            bot.getPlayerInstance().spawnMe();
            
            // Регистрация в системе ботов
            GameServer.getInstance().addBot(bot);
            
            return true;
        } catch (Exception e) {
            _log.error("Error registering bot in GameServer", e);
            return false;
        }
    }
    
    /**
     * Отменяет регистрацию бота в GameServer
     */
    private void unregisterBotFromGameServer(EnhancedFakePlayer bot) {
        try {
            // Удаление из мира игры
            bot.getPlayerInstance().deleteMe();
            
            // Удаление из системы ботов
            GameServer.getInstance().removeBot(bot);
            
        } catch (Exception e) {
            _log.error("Error unregistering bot from GameServer", e);
        }
    }
    
    // ==================== УПРАВЛЕНИЕ СИСТЕМОЙ ====================
    
    /**
     * Активирует менеджер подключений
     */
    public void activate() {
        if (isActive) {
            _log.warn("BotConnectionManager is already active");
            return;
        }
        
        isActive = true;
        _log.info("BotConnectionManager activated");
    }
    
    /**
     * Деактивирует менеджер подключений
     */
    public void deactivate() {
        if (!isActive) {
            _log.warn("BotConnectionManager is not active");
            return;
        }
        
        // Отключение всех ботов
        for (int botId : connectedBots.keySet()) {
            disconnectBot(botId);
        }
        
        isActive = false;
        _log.info("BotConnectionManager deactivated");
    }
    
    /**
     * Проверяет, активен ли менеджер
     */
    public boolean isActive() {
        return isActive;
    }
    
    // ==================== СТАТИСТИКА ====================
    
    /**
     * Возвращает количество подключенных ботов
     */
    public int getConnectedBotCount() {
        return connectedBots.size();
    }
    
    /**
     * Возвращает максимальное количество ботов
     */
    public int getMaxBotCount() {
        return MAX_BOTS;
    }
    
    /**
     * Возвращает статистику подключений
     */
    public String getConnectionStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Bot Connection Statistics ===\n");
        stats.append("Active: ").append(isActive).append("\n");
        stats.append("Connected Bots: ").append(connectedBots.size()).append("\n");
        stats.append("Max Bots: ").append(MAX_BOTS).append("\n");
        stats.append("Next Bot ID: ").append(botIdCounter.get()).append("\n");
        
        return stats.toString();
    }
}
