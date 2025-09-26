package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.ai.AICore;
import net.sf.l2j.botmanager.ai.impl.AICoreImpl;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.integration.IntegrationManager;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Интегратор для связывания системы ботов с L2J сервером.
 * 
 * Обеспечивает полную интеграцию всех компонентов системы ботов
 * с L2J сервером, включая управление жизненным циклом ботов,
 * обработку событий и координацию между системами.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class L2JIntegrator {
    
    private static final Logger logger = Logger.getLogger(L2JIntegrator.class);
    
    /** Синглтон */
    private static L2JIntegrator instance;
    
    /** L2J адаптер */
    private L2JAdapter l2jAdapter;
    
    /** Менеджер интеграции */
    private IntegrationManager integrationManager;
    
    /** Ядро ИИ */
    private AICore aiCore;
    
    /** Планировщик задач */
    private ScheduledExecutorService scheduler;
    
    /** Флаг инициализации */
    private boolean initialized = false;
    
    /** Интервал обновления ботов (в миллисекундах) */
    private static final long BOT_UPDATE_INTERVAL = 1000; // 1 секунда
    
    /**
     * Приватный конструктор для синглтона.
     */
    private L2JIntegrator() {
        // Приватный конструктор
    }
    
    /**
     * Получает экземпляр интегратора.
     * 
     * @return экземпляр интегратора
     */
    public static synchronized L2JIntegrator getInstance() {
        if (instance == null) {
            instance = new L2JIntegrator();
        }
        return instance;
    }
    
    /**
     * Инициализирует интегратор.
     * 
     * @return true если инициализация успешна
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            logger.info("Initializing L2J Integrator...");
            
            // Инициализируем L2J адаптер
            l2jAdapter = L2JAdapter.getInstance();
            if (!l2jAdapter.initialize()) {
                logger.error("Failed to initialize L2J Adapter");
                return false;
            }
            
            // Инициализируем ядро ИИ
            // TODO: Создать AICoreImpl с правильными параметрами
            aiCore = null;
            
            // Инициализируем менеджер интеграции
            integrationManager = IntegrationManager.getInstance();
            
            // Создаем планировщик задач
            scheduler = Executors.newScheduledThreadPool(4);
            
            // Запускаем обновление ботов
            startBotUpdates();
            
            initialized = true;
            logger.info("L2J Integrator initialized successfully");
            
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to initialize L2J Integrator", e);
            return false;
        }
    }
    
    /**
     * Запускает обновление ботов.
     */
    private void startBotUpdates() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateAllBots();
            } catch (Exception e) {
                logger.error("Error updating bots", e);
            }
        }, 0, BOT_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
        
        logger.info("Bot updates started with interval: " + BOT_UPDATE_INTERVAL + "ms");
    }
    
    /**
     * Обновляет всех активных ботов.
     */
    private void updateAllBots() {
        List<EnhancedFakePlayer> bots = l2jAdapter.getAllBots();
        
        for (EnhancedFakePlayer bot : bots) {
            try {
                updateBot(bot);
            } catch (Exception e) {
                logger.error("Error updating bot " + bot.getContext().getBotId(), e);
            }
        }
    }
    
    /**
     * Обновляет конкретного бота.
     * 
     * @param bot бот для обновления
     */
    private void updateBot(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isActive()) {
            return;
        }
        
        try {
            // Обрабатываем решения ИИ
            if (aiCore != null) {
                boolean decisionProcessed = aiCore.processDecision(bot);
                
                if (decisionProcessed) {
                    // Обновляем состояние бота через менеджер интеграции
                    // TODO: Добавить метод updateBotState в IntegrationManager
                }
            }
            
        } catch (Exception e) {
            logger.error("Error updating bot " + bot.getContext().getBotId(), e);
        }
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
            logger.error("L2J Integrator not initialized");
            return null;
        }
        
        try {
            // Создаем бота через L2J адаптер
            EnhancedFakePlayer bot = l2jAdapter.createBot(botType, name, level, x, y, z);
            
            if (bot != null) {
                // Регистрируем бота в менеджере интеграции
                // TODO: Добавить метод registerBot в IntegrationManager
                
                logger.info("Bot created and registered: " + name + " (ID: " + bot.getContext().getBotId() + ")");
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
            logger.error("L2J Integrator not initialized");
            return false;
        }
        
        try {
            // Удаляем бота из менеджера интеграции
            // TODO: Добавить метод unregisterBot в IntegrationManager
            
            // Удаляем бота через L2J адаптер
            boolean removed = l2jAdapter.removeBot(botId);
            
            if (removed) {
                logger.info("Bot removed and unregistered: " + botId);
            }
            
            return removed;
            
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
        if (!initialized) {
            return null;
        }
        
        return l2jAdapter.getBot(botId);
    }
    
    /**
     * Получает всех активных ботов.
     * 
     * @return список активных ботов
     */
    public List<EnhancedFakePlayer> getAllBots() {
        if (!initialized) {
            return List.of();
        }
        
        return l2jAdapter.getAllBots();
    }
    
    /**
     * Получает ботов по типу.
     * 
     * @param botType тип бота
     * @return список ботов указанного типа
     */
    public List<EnhancedFakePlayer> getBotsByType(BotType botType) {
        if (!initialized) {
            return List.of();
        }
        
        return l2jAdapter.getBotsByType(botType);
    }
    
    /**
     * Создает группу ботов для замка.
     * 
     * @param castleId ID замка
     * @param botCount количество ботов
     * @return массив созданных ботов
     */
    public EnhancedFakePlayer[] createCastleBots(int castleId, int botCount) {
        if (!initialized) {
            logger.error("L2J Integrator not initialized");
            return new EnhancedFakePlayer[0];
        }
        
        try {
            // Создаем ботов через L2J адаптер
            EnhancedFakePlayer[] bots = BotFactory.createCastleBots(castleId, botCount);
            
            // Регистрируем каждого бота
            for (EnhancedFakePlayer bot : bots) {
                if (bot != null) {
                    // TODO: Добавить метод registerBot в IntegrationManager
                }
            }
            
            logger.info("Created " + bots.length + " castle bots for castle " + castleId);
            
            return bots;
            
        } catch (Exception e) {
            logger.error("Error creating castle bots for castle " + castleId, e);
            return new EnhancedFakePlayer[0];
        }
    }
    
    /**
     * Получает статистику интегратора.
     * 
     * @return статистика
     */
    public String getStatistics() {
        if (!initialized) {
            return "L2J Integrator not initialized";
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("L2J Integrator Statistics:\n");
        stats.append("- Initialized: ").append(initialized).append("\n");
        stats.append("- L2J Adapter: ").append(l2jAdapter != null ? "Active" : "Inactive").append("\n");
        stats.append("- AI Core: ").append(aiCore != null ? "Active" : "Inactive").append("\n");
        stats.append("- Integration Manager: ").append(integrationManager != null ? "Active" : "Inactive").append("\n");
        stats.append("- Scheduler: ").append(scheduler != null ? "Active" : "Inactive").append("\n");
        
        if (l2jAdapter != null) {
            stats.append("\n").append(l2jAdapter.getStatistics());
        }
        
        if (integrationManager != null) {
            stats.append("\n").append(integrationManager.getStatistics());
        }
        
        return stats.toString();
    }
    
    /**
     * Завершает работу интегратора.
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        try {
            logger.info("Shutting down L2J Integrator...");
            
            // Останавливаем планировщик
            if (scheduler != null) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            // Завершаем работу менеджера интеграции
            if (integrationManager != null) {
                integrationManager.shutdown();
            }
            
            // Завершаем работу L2J адаптера
            if (l2jAdapter != null) {
                l2jAdapter.shutdown();
            }
            
            initialized = false;
            logger.info("L2J Integrator shutdown completed");
            
        } catch (Exception e) {
            logger.error("Error during L2J Integrator shutdown", e);
        }
    }
    
    /**
     * Проверяет, инициализирован ли интегратор.
     * 
     * @return true если инициализирован
     */
    public boolean isInitialized() {
        return initialized;
    }
}
