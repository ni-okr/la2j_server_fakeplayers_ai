package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер взаимодействия ботов с NPC
 */
public class NpcInteractionManager {
    
    private static final Logger _log = Logger.getLogger(NpcInteractionManager.class);
    
    // ==================== SINGLETON ====================
    
    private static volatile NpcInteractionManager instance;
    
    public static NpcInteractionManager getInstance() {
        if (instance == null) {
            synchronized (NpcInteractionManager.class) {
                if (instance == null) {
                    instance = new NpcInteractionManager();
                }
            }
        }
        return instance;
    }
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** Карта взаимодействий ботов с NPC */
    private final Map<Integer, Map<Integer, NpcInteraction>> botInteractions;
    
    /** Кэш NPC по типам */
    private final Map<Integer, List<L2NpcInstance>> npcCache;
    
    /** Максимальный радиус поиска NPC */
    private static final int MAX_SEARCH_RADIUS = 2000;
    
    /** Дистанция взаимодействия с NPC */
    private static final int INTERACTION_DISTANCE = 150;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    private NpcInteractionManager() {
        this.botInteractions = new ConcurrentHashMap<>();
        this.npcCache = new ConcurrentHashMap<>();
        _log.info("NpcInteractionManager initialized");
    }
    
    // ==================== ВЗАИМОДЕЙСТВИЕ С NPC ====================
    
    /**
     * Начинает взаимодействие с NPC
     * 
     * @param bot бот
     * @param npcId ID NPC
     * @return true если взаимодействие начато
     */
    public boolean startInteraction(EnhancedFakePlayer bot, int npcId) {
        if (bot == null || !bot.isActive()) {
            return false;
        }
        
        L2NpcInstance npc = findNpcById(bot, npcId);
        if (npc == null) {
            _log.warn("NPC not found for interaction: " + npcId);
            return false;
        }
        
        return startInteraction(bot, npc);
    }
    
    /**
     * Начинает взаимодействие с NPC
     * 
     * @param bot бот
     * @param npc NPC
     * @return true если взаимодействие начато
     */
    public boolean startInteraction(EnhancedFakePlayer bot, L2NpcInstance npc) {
        if (bot == null || npc == null || !bot.isActive()) {
            return false;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null || player.isDead()) {
                return false;
            }
            
            // Проверка дистанции
            double distance = player.getDistance(npc);
            if (distance > INTERACTION_DISTANCE) {
                _log.debug("NPC too far for interaction: " + distance);
                return false;
            }
            
            // Создание записи взаимодействия
            NpcInteraction interaction = new NpcInteraction(npc, System.currentTimeMillis());
            botInteractions.computeIfAbsent(bot.getBotId(), k -> new ConcurrentHashMap<>())
                .put(npc.getObjectId(), interaction);
            
            // Установка цели
            player.setTarget(npc);
            
            // Начало взаимодействия
            player.getAI().setIntention(net.sf.l2j.gameserver.ai.CtrlIntention.INTERACT, npc);
            
            _log.debug("Bot " + bot.getBotId() + " started interaction with NPC: " + npc.getName());
            return true;
            
        } catch (Exception e) {
            _log.error("Error starting interaction for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    /**
     * Завершает взаимодействие с NPC
     * 
     * @param bot бот
     * @param npcId ID NPC
     * @return true если взаимодействие завершено
     */
    public boolean endInteraction(EnhancedFakePlayer bot, int npcId) {
        if (bot == null) {
            return false;
        }
        
        Map<Integer, NpcInteraction> interactions = botInteractions.get(bot.getBotId());
        if (interactions == null) {
            return false;
        }
        
        NpcInteraction interaction = interactions.remove(npcId);
        if (interaction == null) {
            return false;
        }
        
        _log.debug("Bot " + bot.getBotId() + " ended interaction with NPC: " + npcId);
        return true;
    }
    
    // ==================== ПОИСК NPC ====================
    
    /**
     * Находит NPC по ID в радиусе
     * 
     * @param bot бот
     * @param npcId ID NPC
     * @param radius радиус поиска
     * @return NPC или null
     */
    public L2NpcInstance findNpcById(EnhancedFakePlayer bot, int npcId, int radius) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            List<L2Character> characters = L2World.getInstance().getVisibleObjects(player, L2Character.class);
            for (L2Character character : characters) {
                if (character instanceof L2NpcInstance) {
                    L2NpcInstance npc = (L2NpcInstance) character;
                    if (npc.getNpcId() == npcId && !npc.isDead()) {
                        double distance = player.getDistance(npc);
                        if (distance <= radius) {
                            return npc;
                        }
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            _log.error("Error finding NPC by ID for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    /**
     * Находит NPC по ID в стандартном радиусе
     * 
     * @param bot бот
     * @param npcId ID NPC
     * @return NPC или null
     */
    public L2NpcInstance findNpcById(EnhancedFakePlayer bot, int npcId) {
        return findNpcById(bot, npcId, MAX_SEARCH_RADIUS);
    }
    
    /**
     * Находит ближайшего NPC по типу
     * 
     * @param bot бот
     * @param npcType тип NPC
     * @param radius радиус поиска
     * @return ближайший NPC или null
     */
    public L2NpcInstance findNearestNpcByType(EnhancedFakePlayer bot, int npcType, int radius) {
        if (bot == null || !bot.isActive()) {
            return null;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return null;
            }
            
            L2NpcInstance nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            
            List<L2Character> characters = L2World.getInstance().getVisibleObjects(player, L2Character.class);
            for (L2Character character : characters) {
                if (character instanceof L2NpcInstance) {
                    L2NpcInstance npc = (L2NpcInstance) character;
                    if (npc.getNpcId() == npcType && !npc.isDead()) {
                        double distance = player.getDistance(npc);
                        if (distance <= radius && distance < nearestDistance) {
                            nearest = npc;
                            nearestDistance = distance;
                        }
                    }
                }
            }
            
            return nearest;
            
        } catch (Exception e) {
            _log.error("Error finding nearest NPC by type for bot " + bot.getBotId(), e);
            return null;
        }
    }
    
    /**
     * Находит всех NPC определенного типа в радиусе
     * 
     * @param bot бот
     * @param npcType тип NPC
     * @param radius радиус поиска
     * @return список NPC
     */
    public List<L2NpcInstance> findAllNpcsByType(EnhancedFakePlayer bot, int npcType, int radius) {
        List<L2NpcInstance> npcs = new ArrayList<>();
        
        if (bot == null || !bot.isActive()) {
            return npcs;
        }
        
        try {
            L2PcInstance player = bot.getPlayerInstance();
            if (player == null) {
                return npcs;
            }
            
            List<L2Character> characters = L2World.getInstance().getVisibleObjects(player, L2Character.class);
            for (L2Character character : characters) {
                if (character instanceof L2NpcInstance) {
                    L2NpcInstance npc = (L2NpcInstance) character;
                    if (npc.getNpcId() == npcType && !npc.isDead()) {
                        double distance = player.getDistance(npc);
                        if (distance <= radius) {
                            npcs.add(npc);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            _log.error("Error finding all NPCs by type for bot " + bot.getBotId(), e);
        }
        
        return npcs;
    }
    
    // ==================== ТОРГОВЛЯ ====================
    
    /**
     * Открывает магазин NPC
     * 
     * @param bot бот
     * @param npcId ID NPC
     * @return true если магазин открыт
     */
    public boolean openShop(EnhancedFakePlayer bot, int npcId) {
        L2NpcInstance npc = findNpcById(bot, npcId);
        if (npc == null) {
            return false;
        }
        
        return openShop(bot, npc);
    }
    
    /**
     * Открывает магазин NPC
     * 
     * @param bot бот
     * @param npc NPC
     * @return true если магазин открыт
     */
    public boolean openShop(EnhancedFakePlayer bot, L2NpcInstance npc) {
        if (!startInteraction(bot, npc)) {
            return false;
        }
        
        try {
            // Здесь должна быть логика открытия магазина
            // В реальной реализации это будет через пакеты
            _log.debug("Bot " + bot.getBotId() + " opened shop with NPC: " + npc.getName());
            return true;
            
        } catch (Exception e) {
            _log.error("Error opening shop for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    // ==================== КВЕСТЫ ====================
    
    /**
     * Начинает квест с NPC
     * 
     * @param bot бот
     * @param npcId ID NPC
     * @param questId ID квеста
     * @return true если квест начат
     */
    public boolean startQuest(EnhancedFakePlayer bot, int npcId, int questId) {
        L2NpcInstance npc = findNpcById(bot, npcId);
        if (npc == null) {
            return false;
        }
        
        return startQuest(bot, npc, questId);
    }
    
    /**
     * Начинает квест с NPC
     * 
     * @param bot бот
     * @param npc NPC
     * @param questId ID квеста
     * @return true если квест начат
     */
    public boolean startQuest(EnhancedFakePlayer bot, L2NpcInstance npc, int questId) {
        if (!startInteraction(bot, npc)) {
            return false;
        }
        
        try {
            // Здесь должна быть логика начала квеста
            // В реальной реализации это будет через систему квестов L2J
            _log.debug("Bot " + bot.getBotId() + " started quest " + questId + " with NPC: " + npc.getName());
            return true;
            
        } catch (Exception e) {
            _log.error("Error starting quest for bot " + bot.getBotId(), e);
            return false;
        }
    }
    
    // ==================== СТАТИСТИКА ====================
    
    /**
     * Получает статистику взаимодействий бота
     * 
     * @param botId ID бота
     * @return статистика
     */
    public String getInteractionStatistics(int botId) {
        Map<Integer, NpcInteraction> interactions = botInteractions.get(botId);
        if (interactions == null) {
            return "No interactions found for bot: " + botId;
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== NPC Interaction Statistics for Bot ").append(botId).append(" ===\n");
        stats.append("Active Interactions: ").append(interactions.size()).append("\n");
        
        for (NpcInteraction interaction : interactions.values()) {
            stats.append("NPC: ").append(interaction.getNpc().getName())
                 .append(" (ID: ").append(interaction.getNpc().getNpcId()).append(")")
                 .append(" - Started: ").append(new Date(interaction.getStartTime())).append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Очищает взаимодействия бота
     * 
     * @param botId ID бота
     */
    public void clearBotInteractions(int botId) {
        botInteractions.remove(botId);
        _log.debug("Cleared interactions for bot: " + botId);
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================
    
    /**
     * Класс для хранения информации о взаимодействии
     */
    private static class NpcInteraction {
        private final L2NpcInstance npc;
        private final long startTime;
        
        public NpcInteraction(L2NpcInstance npc, long startTime) {
            this.npc = npc;
            this.startTime = startTime;
        }
        
        public L2NpcInstance getNpc() {
            return npc;
        }
        
        public long getStartTime() {
            return startTime;
        }
    }
}
