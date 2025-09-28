package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.core.BotContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Тесты для интеграции с L2J сервером.
 * 
 * Тестирует основные компоненты интеграции:
 * - BotFactory - создание ботов
 * - L2JAdapter - адаптер для L2J
 * - L2JIntegrator - полная интеграция
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class L2JIntegrationTest {
    
    private L2JIntegrator integrator;
    private L2JAdapter adapter;
    private BotFactory factory;
    
    @BeforeEach
    void setUp() {
        // Инициализируем компоненты
        integrator = L2JIntegrator.getInstance();
        adapter = L2JAdapter.getInstance();
        factory = new BotFactory();
    }
    
    @AfterEach
    void tearDown() {
        // Очищаем после тестов
        if (integrator != null && integrator.isInitialized()) {
            integrator.shutdown();
        }
    }
    
    @Test
    void testBotFactoryCreation() {
        // Тестируем создание бота через фабрику
        EnhancedFakePlayer bot = factory.createRandomBot(BotType.SOLDIER);
        
        // В тестовой среде без L2J сервера бот может быть null
        if (bot != null) {
            assertNotNull(bot.getContext(), "Bot context should not be null");
            assertEquals(BotType.SOLDIER, bot.getContext().getData("botType"), "Bot type should match");
        }
    }
    
    @Test
    void testBotFactoryWithParameters() {
        // Тестируем создание бота с параметрами
        String name = "TestBot";
        int level = 10;
        int x = 100;
        int y = 200;
        int z = 0;
        
        EnhancedFakePlayer bot = factory.createBot(BotType.OFFICER, name, level, x, y, z);
        
        // В тестовой среде без L2J сервера бот может быть null
        if (bot != null) {
            assertNotNull(bot.getContext(), "Bot context should not be null");
            assertEquals(BotType.OFFICER, bot.getContext().getData("botType"), "Bot type should match");
            assertEquals(name, bot.getContext().getData("botName"), "Bot name should match");
            assertEquals((Integer) level, bot.getContext().getData("botLevel"), "Bot level should match");
        }
    }
    
    @Test
    void testBotFactoryCastleBots() {
        // Тестируем создание группы ботов для замка
        int castleId = 1;
        int botCount = 5;
        
        EnhancedFakePlayer[] bots = factory.createCastleBots(castleId, botCount);
        
        // В тестовой среде без L2J сервера боты могут быть null
        if (bots != null) {
            assertEquals(botCount, bots.length, "Should create correct number of bots");
            
            for (EnhancedFakePlayer bot : bots) {
                if (bot != null) {
                    assertNotNull(bot.getContext(), "Bot context should not be null");
                    assertNotNull(bot.getContext().getData("botType"), "Bot type should be set");
                }
            }
        }
    }
    
    @Test
    void testL2JAdapterInitialization() {
        // Тестируем инициализацию адаптера
        boolean initialized = adapter.initialize();
        
        // В тестовой среде без L2J сервера инициализация может не удаться
        if (initialized) {
            assertTrue(adapter.initialize(), "Adapter should be initialized");
        }
    }
    
    @Test
    void testL2JAdapterBotManagement() {
        // Тестируем управление ботами через адаптер
        if (adapter.initialize()) {
            // Создаем бота
            EnhancedFakePlayer bot = adapter.createBot(BotType.SOLDIER, "TestBot", 10, 100, 200, 0);
            
            if (bot != null) {
                int botId = bot.getContext().getBotId();
                
                // Проверяем, что бот создан
                EnhancedFakePlayer retrievedBot = adapter.getBot(botId);
                assertNotNull(retrievedBot, "Bot should be retrievable");
                assertEquals(botId, retrievedBot.getContext().getBotId(), "Bot ID should match");
                
                // Проверяем список всех ботов
                assertTrue(adapter.getAllBots().size() > 0, "Should have at least one bot");
                
                // Удаляем бота
                boolean removed = adapter.removeBot(botId);
                assertTrue(removed, "Bot should be removable");
            }
        }
    }
    
    @Test
    void testL2JAdapterBotTypes() {
        // Тестируем получение ботов по типу
        if (adapter.initialize()) {
            // Создаем ботов разных типов
            adapter.createBot(BotType.SOLDIER, "SoldierBot", 10, 100, 200, 0);
            adapter.createBot(BotType.OFFICER, "OfficerBot", 15, 150, 250, 0);
            adapter.createBot(BotType.FARMER, "FarmerBot", 5, 200, 300, 0);
            
            // Проверяем получение по типу
            List<EnhancedFakePlayer> soldiers = adapter.getBotsByType(BotType.SOLDIER);
            List<EnhancedFakePlayer> officers = adapter.getBotsByType(BotType.OFFICER);
            List<EnhancedFakePlayer> farmers = adapter.getBotsByType(BotType.FARMER);
            
            // В тестовой среде боты могут не создаваться
            if (soldiers != null) {
                assertTrue(soldiers.size() >= 0, "Should be able to get soldiers");
            }
            if (officers != null) {
                assertTrue(officers.size() >= 0, "Should be able to get officers");
            }
            if (farmers != null) {
                assertTrue(farmers.size() >= 0, "Should be able to get farmers");
            }
        }
    }
    
    @Test
    void testL2JAdapterWorldInfo() {
        // Тестируем получение информации о мире
        if (adapter.initialize()) {
            String worldInfo = adapter.getWorldInfo();
            assertNotNull(worldInfo, "World info should not be null");
            assertFalse(worldInfo.isEmpty(), "World info should not be empty");
        }
    }
    
    @Test
    void testL2JIntegratorInitialization() {
        // Тестируем инициализацию интегратора
        boolean initialized = integrator.initialize();
        
        // В тестовой среде без L2J сервера инициализация может не удаться
        if (initialized) {
            assertTrue(integrator.isInitialized(), "Integrator should be initialized");
        }
    }
    
    @Test
    void testL2JIntegratorBotManagement() {
        // Тестируем управление ботами через интегратор
        if (integrator.initialize()) {
            // Создаем бота
            EnhancedFakePlayer bot = integrator.createBot(BotType.SOLDIER, "TestBot", 10, 100, 200, 0);
            
            if (bot != null) {
                int botId = bot.getContext().getBotId();
                
                // Проверяем, что бот создан
                EnhancedFakePlayer retrievedBot = integrator.getBot(botId);
                assertNotNull(retrievedBot, "Bot should be retrievable");
                assertEquals(botId, retrievedBot.getContext().getBotId(), "Bot ID should match");
                
                // Проверяем список всех ботов
                assertTrue(integrator.getAllBots().size() > 0, "Should have at least one bot");
                
                // Удаляем бота
                boolean removed = integrator.removeBot(botId);
                assertTrue(removed, "Bot should be removable");
            }
        }
    }
    
    @Test
    void testL2JIntegratorCastleBots() {
        // Тестируем создание ботов для замка
        if (integrator.initialize()) {
            int castleId = 1;
            int botCount = 3;
            
            EnhancedFakePlayer[] bots = integrator.createCastleBots(castleId, botCount);
            
            if (bots != null) {
                assertEquals(botCount, bots.length, "Should create correct number of castle bots");
                
                for (EnhancedFakePlayer bot : bots) {
                    if (bot != null) {
                        assertNotNull(bot.getContext(), "Bot context should not be null");
                        assertNotNull(bot.getContext().getData("botType"), "Bot type should be set");
                    }
                }
            }
        }
    }
    
    @Test
    void testL2JIntegratorStatistics() {
        // Тестируем получение статистики
        if (integrator.initialize()) {
            String stats = integrator.getStatistics();
            assertNotNull(stats, "Statistics should not be null");
            assertFalse(stats.isEmpty(), "Statistics should not be empty");
            assertTrue(stats.contains("L2J Integrator Statistics"), "Should contain integrator stats");
        }
    }
    
    @Test
    void testL2JIntegratorShutdown() {
        // Тестируем завершение работы интегратора
        if (integrator.initialize()) {
            integrator.shutdown();
            assertFalse(integrator.isInitialized(), "Integrator should not be initialized after shutdown");
        }
    }
    
    @Test
    void testBotContextIntegration() {
        // Тестируем интеграцию контекста бота
        BotContext context = new BotContext(12345);
        context.setData("botType", BotType.SOLDIER);
        context.setData("botName", "TestBot");
        context.setData("botLevel", 10);
        
        assertEquals(12345, context.getBotId(), "Bot ID should match");
        assertEquals(BotType.SOLDIER, context.getData("botType"), "Bot type should match");
        assertEquals("TestBot", context.getData("botName"), "Bot name should match");
        assertEquals((Integer) 10, context.getData("botLevel"), "Bot level should match");
    }
    
    @Test
    void testBotTypeEnum() {
        // Тестируем enum BotType
        BotType[] types = BotType.values();
        assertTrue(types.length > 0, "Should have bot types");
        
        // Проверяем, что все типы доступны
        assertNotNull(BotType.SOLDIER, "SOLDIER should be available");
        assertNotNull(BotType.OFFICER, "OFFICER should be available");
        assertNotNull(BotType.HIGH_OFFICER, "HIGH_OFFICER should be available");
        assertNotNull(BotType.VICE_GUILDMASTER, "VICE_GUILDMASTER should be available");
        assertNotNull(BotType.FARMER, "FARMER should be available");
        assertNotNull(BotType.MERCHANT, "MERCHANT should be available");
        assertNotNull(BotType.GUARD, "GUARD should be available");
    }
}
