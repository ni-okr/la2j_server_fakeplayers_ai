package net.sf.l2j.botmanager.integration;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.ai.AICore;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Менеджер интеграции всех систем ботов.
 * 
 * Связывает системы поведений, действий и ИИ в единую рабочую систему.
 * Управляет жизненным циклом ботов и координирует работу всех компонентов.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class IntegrationManager {
    
    private static final Logger logger = Logger.getLogger(IntegrationManager.class);
    
    private static IntegrationManager instance;
    
    /** Ядро ИИ */
    private final AICore aiCore;
    
    /** Активные боты */
    private final Map<Integer, BotIntegration> activeBots;
    
    /** Счетчик обработанных циклов */
    private final AtomicLong processedCycles;
    
    /** Счетчик успешных операций */
    private final AtomicLong successfulOperations;
    
    /** Флаг активности */
    private volatile boolean isActive = false;
    
    /**
     * Конструктор.
     * 
     * @param aiCore ядро ИИ
     */
    private IntegrationManager(AICore aiCore) {
        this.aiCore = aiCore;
        this.activeBots = new ConcurrentHashMap<>();
        this.processedCycles = new AtomicLong(0);
        this.successfulOperations = new AtomicLong(0);
    }
    
    /**
     * Получить экземпляр менеджера интеграции.
     * 
     * @param aiCore ядро ИИ
     * @return экземпляр менеджера
     */
    public static synchronized IntegrationManager getInstance(AICore aiCore) {
        if (instance == null) {
            instance = new IntegrationManager(aiCore);
        }
        return instance;
    }
    
    /**
     * Получить экземпляр менеджера интеграции.
     * 
     * @return экземпляр менеджера
     */
    public static synchronized IntegrationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("IntegrationManager not initialized. Call getInstance(AICore) first.");
        }
        return instance;
    }
    
    /**
     * Инициализация менеджера интеграции.
     */
    public void initialize() {
        if (isActive) {
            logger.warn("IntegrationManager already initialized");
            return;
        }
        
        isActive = true;
        logger.info("IntegrationManager initialized");
    }
    
    /**
     * Остановка менеджера интеграции.
     */
    public void shutdown() {
        if (!isActive) {
            logger.warn("IntegrationManager already shutdown");
            return;
        }
        
        // Останавливаем всех ботов
        for (BotIntegration botIntegration : activeBots.values()) {
            stopBot(botIntegration.getBot());
        }
        
        activeBots.clear();
        isActive = false;
        logger.info("IntegrationManager shutdown");
    }
    
    /**
     * Добавление бота в систему.
     * 
     * @param bot бот для добавления
     * @return true если бот успешно добавлен
     */
    public boolean addBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot add null bot");
            return false;
        }
        
        if (!isActive) {
            logger.warn("IntegrationManager not active");
            return false;
        }
        
        int botId = bot.getBotId();
        
        if (activeBots.containsKey(botId)) {
            logger.warn("Bot " + botId + " already exists");
            return false;
        }
        
        try {
            // Инициализируем ядро ИИ для бота
            aiCore.initialize(bot);
            
            // Создаем интеграцию бота
            BotIntegration botIntegration = new BotIntegration(bot);
            activeBots.put(botId, botIntegration);
            
            logger.info("Bot " + botId + " added to integration system");
            return true;
            
        } catch (Exception e) {
            logger.error("Error adding bot " + botId, e);
            return false;
        }
    }
    
    /**
     * Удаление бота из системы.
     * 
     * @param bot бот для удаления
     * @return true если бот успешно удален
     */
    public boolean removeBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot remove null bot");
            return false;
        }
        
        int botId = bot.getBotId();
        BotIntegration botIntegration = activeBots.remove(botId);
        
        if (botIntegration == null) {
            logger.warn("Bot " + botId + " not found in integration system");
            return false;
        }
        
        try {
            // Останавливаем ядро ИИ для бота
            aiCore.shutdown(bot);
            
            // Очищаем интеграцию
            botIntegration.cleanup();
            
            logger.info("Bot " + botId + " removed from integration system");
            return true;
            
        } catch (Exception e) {
            logger.error("Error removing bot " + botId, e);
            return false;
        }
    }
    
    /**
     * Остановка бота.
     * 
     * @param bot бот для остановки
     */
    private void stopBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            return;
        }
        
        try {
            aiCore.shutdown(bot);
        } catch (Exception e) {
            logger.error("Error stopping bot " + bot.getBotId(), e);
        }
    }
    
    /**
     * Обработка одного цикла для всех ботов.
     * 
     * @return количество обработанных ботов
     */
    public int processCycle() {
        if (!isActive) {
            return 0;
        }
        
        int processedBots = 0;
        
        try {
            for (BotIntegration botIntegration : activeBots.values()) {
                if (processBot(botIntegration)) {
                    processedBots++;
                }
            }
            
            processedCycles.incrementAndGet();
            
        } catch (Exception e) {
            logger.error("Error processing cycle", e);
        }
        
        return processedBots;
    }
    
    /**
     * Обработка одного бота.
     * 
     * @param botIntegration интеграция бота
     * @return true если бот обработан успешно
     */
    private boolean processBot(BotIntegration botIntegration) {
        try {
            EnhancedFakePlayer bot = botIntegration.getBot();
            
            // Проверяем, что бот активен
            if (!aiCore.isActive(bot)) {
                return false;
            }
            
            // Обрабатываем решение ИИ
            boolean decisionProcessed = aiCore.processDecision(bot);
            
            if (decisionProcessed) {
                successfulOperations.incrementAndGet();
            }
            
            // Обновляем статистику
            botIntegration.updateLastProcessedTime();
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing bot " + botIntegration.getBot().getBotId(), e);
            return false;
        }
    }
    
    /**
     * Получение статистики менеджера интеграции.
     * 
     * @return статистика в виде строки
     */
    public String getStatistics() {
        long cycles = processedCycles.get();
        long operations = successfulOperations.get();
        int activeBotsCount = activeBots.size();
        
        double successRate = cycles > 0 ? (double) operations / cycles : 0.0;
        
        return String.format("IntegrationManager Stats: Active=%s, Bots=%d, Cycles=%d, " +
                           "Operations=%d, Success Rate=%.2f%%",
                isActive, activeBotsCount, cycles, operations, successRate * 100);
    }
    
    /**
     * Получение статистики конкретного бота.
     * 
     * @param botId ID бота
     * @return статистика бота или null если бот не найден
     */
    public String getBotStatistics(int botId) {
        BotIntegration botIntegration = activeBots.get(botId);
        if (botIntegration == null) {
            return null;
        }
        
        return botIntegration.getStatistics();
    }
    
    /**
     * Получение списка активных ботов.
     * 
     * @return список ID активных ботов
     */
    public List<Integer> getActiveBotIds() {
        return List.copyOf(activeBots.keySet());
    }
    
    /**
     * Проверка активности менеджера.
     * 
     * @return true если менеджер активен
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Получение количества активных ботов.
     * 
     * @return количество активных ботов
     */
    public int getActiveBotCount() {
        return activeBots.size();
    }
    
    /**
     * Внутренний класс для интеграции бота.
     */
    private static class BotIntegration {
        private final EnhancedFakePlayer bot;
        private volatile long lastProcessedTime;
        private volatile long processedCount;
        
        public BotIntegration(EnhancedFakePlayer bot) {
            this.bot = bot;
            this.lastProcessedTime = System.currentTimeMillis();
            this.processedCount = 0;
        }
        
        public EnhancedFakePlayer getBot() {
            return bot;
        }
        
        public void updateLastProcessedTime() {
            this.lastProcessedTime = System.currentTimeMillis();
            this.processedCount++;
        }
        
        public long getLastProcessedTime() {
            return lastProcessedTime;
        }
        
        public long getProcessedCount() {
            return processedCount;
        }
        
        public void cleanup() {
            // Очистка ресурсов бота
        }
        
        public String getStatistics() {
            return String.format("Bot %d: Processed=%d, Last Processed=%d",
                    bot.getBotId(), processedCount, lastProcessedTime);
        }
    }
}
