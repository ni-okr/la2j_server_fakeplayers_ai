package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.serverpackets.CharInfo;
import net.sf.l2j.gameserver.serverpackets.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Адаптер для интеграции системы ботов с L2J сервером.
 * 
 * Обеспечивает взаимодействие между системой ботов и L2J,
 * включая управление персонажами, событиями и состоянием мира.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class L2JAdapter {
    
    private static final Logger logger = Logger.getLogger(L2JAdapter.class);
    
    /** Карта активных ботов */
    private final ConcurrentMap<Integer, EnhancedFakePlayer> activeBots = new ConcurrentHashMap<>();
    
    /** Синглтон */
    private static L2JAdapter instance;
    
    /** Флаг инициализации */
    private boolean initialized = false;
    
    /**
     * Приватный конструктор для синглтона.
     */
    private L2JAdapter() {
        // Приватный конструктор
    }
    
    /**
     * Получает экземпляр адаптера.
     * 
     * @return экземпляр адаптера
     */
    public static synchronized L2JAdapter getInstance() {
        if (instance == null) {
            instance = new L2JAdapter();
        }
        return instance;
    }
    
    /**
     * Инициализирует адаптер.
     * 
     * @return true если инициализация успешна
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            // Проверяем, что L2J сервер запущен
            // TODO: Добавить проверку GameServer.getInstance()
            
            // Инициализируем компоненты
            initializeComponents();
            
            initialized = true;
            logger.info("L2J Adapter initialized successfully");
            
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to initialize L2J Adapter", e);
            return false;
        }
    }
    
    /**
     * Инициализирует компоненты адаптера.
     */
    private void initializeComponents() {
        // Здесь можно добавить инициализацию дополнительных компонентов
        logger.debug("Initializing L2J Adapter components");
    }
    
    /**
     * Создает бота в L2J сервере.
     * 
     * @param botType тип бота
     * @param name имя бота
     * @param level уровень бота
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return созданный бот или null при ошибке
     */
    public EnhancedFakePlayer createBot(BotType botType, String name, int level, int x, int y, int z) {
        if (!initialized) {
            logger.error("L2J Adapter not initialized");
            return null;
        }
        
        try {
            // Создаем бота через фабрику
            EnhancedFakePlayer bot = BotFactory.createBot(botType, name, level, x, y, z);
            
            if (bot != null) {
                // Добавляем в карту активных ботов
                activeBots.put(bot.getContext().getBotId(), bot);
                
                // Уведомляем о создании бота
                notifyBotCreated(bot);
                
                logger.info("Bot created successfully: " + name + " (ID: " + bot.getContext().getBotId() + ")");
            }
            
            return bot;
            
        } catch (Exception e) {
            logger.error("Error creating bot: " + name, e);
            return null;
        }
    }
    
    /**
     * Удаляет бота из L2J сервера.
     * 
     * @param botId ID бота
     * @return true если удаление успешно
     */
    public boolean removeBot(int botId) {
        if (!initialized) {
            logger.error("L2J Adapter not initialized");
            return false;
        }
        
        try {
            EnhancedFakePlayer bot = activeBots.remove(botId);
            if (bot != null) {
                // Удаляем бота через фабрику
                BotFactory.removeBot(bot);
                
                // Уведомляем об удалении бота
                notifyBotRemoved(bot);
                
                logger.info("Bot removed successfully: " + bot.getContext().getBotId());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("Error removing bot: " + botId, e);
            return false;
        }
    }
    
    /**
     * Получает бота по ID.
     * 
     * @param botId ID бота
     * @return бот или null если не найден
     */
    public EnhancedFakePlayer getBot(int botId) {
        return activeBots.get(botId);
    }
    
    /**
     * Получает всех активных ботов.
     * 
     * @return список активных ботов
     */
    public List<EnhancedFakePlayer> getAllBots() {
        return new ArrayList<>(activeBots.values());
    }
    
    /**
     * Получает ботов по типу.
     * 
     * @param botType тип бота
     * @return список ботов указанного типа
     */
    public List<EnhancedFakePlayer> getBotsByType(BotType botType) {
        List<EnhancedFakePlayer> result = new ArrayList<>();
        
        for (EnhancedFakePlayer bot : activeBots.values()) {
            if (bot.getContext().getData("botType") == botType) {
                result.add(bot);
            }
        }
        
        return result;
    }
    
    /**
     * Перемещает бота в указанную позицию.
     * 
     * @param botId ID бота
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return true если перемещение успешно
     */
    public boolean moveBot(int botId, int x, int y, int z) {
        EnhancedFakePlayer bot = getBot(botId);
        if (bot == null) {
            return false;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return false;
        }
        
        try {
            // Создаем позицию
            L2CharPosition pos = new L2CharPosition(x, y, z, 0);
            
            // Перемещаем персонажа
            player.setXYZ(x, y, z);
            player.setHeading(0);
            
            // Обновляем информацию о персонаже
            player.broadcastPacket(new CharInfo(player));
            player.broadcastPacket(new UserInfo(player));
            
            logger.debug("Bot " + botId + " moved to position (" + x + ", " + y + ", " + z + ")");
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error moving bot " + botId, e);
            return false;
        }
    }
    
    /**
     * Получает информацию о мире L2J.
     * 
     * @return информация о мире
     */
    public String getWorldInfo() {
        if (!initialized) {
            return "L2J Adapter not initialized";
        }
        
        try {
            StringBuilder info = new StringBuilder();
            info.append("L2J World Info:\n");
            info.append("- Active Players: ").append("N/A (L2World not initialized)").append("\n");
            info.append("- Active Bots: ").append(activeBots.size()).append("\n");
            info.append("- Server Status: ").append("Running").append("\n");
            
            return info.toString();
            
        } catch (Exception e) {
            logger.error("Error getting world info", e);
            return "Error getting world info: " + e.getMessage();
        }
    }
    
    /**
     * Получает ближайших монстров к боту.
     * 
     * @param botId ID бота
     * @param radius радиус поиска
     * @return список ближайших монстров
     */
    public List<L2MonsterInstance> getNearbyMonsters(int botId, int radius) {
        List<L2MonsterInstance> monsters = new ArrayList<>();
        
        EnhancedFakePlayer bot = getBot(botId);
        if (bot == null) {
            return monsters;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return monsters;
        }
        
        try {
            // Получаем все объекты в радиусе
            for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, radius)) {
                if (obj instanceof L2MonsterInstance) {
                    monsters.add((L2MonsterInstance) obj);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error getting nearby monsters for bot " + botId, e);
        }
        
        return monsters;
    }
    
    /**
     * Получает ближайших NPC к боту.
     * 
     * @param botId ID бота
     * @param radius радиус поиска
     * @return список ближайших NPC
     */
    public List<L2NpcInstance> getNearbyNpcs(int botId, int radius) {
        List<L2NpcInstance> npcs = new ArrayList<>();
        
        EnhancedFakePlayer bot = getBot(botId);
        if (bot == null) {
            return npcs;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return npcs;
        }
        
        try {
            // Получаем все объекты в радиусе
            for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, radius)) {
                if (obj instanceof L2NpcInstance) {
                    npcs.add((L2NpcInstance) obj);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error getting nearby NPCs for bot " + botId, e);
        }
        
        return npcs;
    }
    
    /**
     * Проверяет, находится ли бот в зоне.
     * 
     * @param botId ID бота
     * @param zoneId ID зоны
     * @return true если бот в зоне
     */
    public boolean isBotInZone(int botId, int zoneId) {
        EnhancedFakePlayer bot = getBot(botId);
        if (bot == null) {
            return false;
        }
        
        L2PcInstance player = bot.getPlayerInstance();
        if (player == null) {
            return false;
        }
        
        try {
            // Проверяем зоны
            // TODO: Добавить проверку зон
            return false;
            
        } catch (Exception e) {
            logger.error("Error checking zone for bot " + botId, e);
        }
        
        return false;
    }
    
    /**
     * Уведомляет о создании бота.
     * 
     * @param bot созданный бот
     */
    private void notifyBotCreated(EnhancedFakePlayer bot) {
        // Здесь можно добавить уведомления о создании бота
        logger.debug("Bot created notification: " + bot.getContext().getBotId());
    }
    
    /**
     * Уведомляет об удалении бота.
     * 
     * @param bot удаленный бот
     */
    private void notifyBotRemoved(EnhancedFakePlayer bot) {
        // Здесь можно добавить уведомления об удалении бота
        logger.debug("Bot removed notification: " + bot.getContext().getBotId());
    }
    
    /**
     * Завершает работу адаптера.
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        try {
            // Удаляем всех ботов
            for (EnhancedFakePlayer bot : new ArrayList<>(activeBots.values())) {
                removeBot(bot.getContext().getBotId());
            }
            
            // Очищаем карту
            activeBots.clear();
            
            initialized = false;
            logger.info("L2J Adapter shutdown completed");
            
        } catch (Exception e) {
            logger.error("Error during L2J Adapter shutdown", e);
        }
    }
    
    /**
     * Получает статистику адаптера.
     * 
     * @return статистика
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("L2J Adapter Statistics:\n");
        stats.append("- Initialized: ").append(initialized).append("\n");
        stats.append("- Active Bots: ").append(activeBots.size()).append("\n");
        stats.append("- Bot Types: ");
        
        for (BotType type : BotType.values()) {
            int count = getBotsByType(type).size();
            if (count > 0) {
                stats.append(type.name()).append("(").append(count).append(") ");
            }
        }
        
        return stats.toString();
    }
}
