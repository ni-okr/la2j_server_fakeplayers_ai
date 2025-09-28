package net.sf.l2j.botmanager.l2j;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для BotConnectionManager
 */
public class BotConnectionManagerTest {
    
    private BotConnectionManager connectionManager;
    
    @BeforeEach
    void setUp() {
        connectionManager = BotConnectionManager.getInstance();
        connectionManager.activate();
    }
    
    @Test
    @DisplayName("Тест создания менеджера подключений")
    void testConnectionManagerCreation() {
        assertNotNull(connectionManager);
        assertTrue(connectionManager.isActive());
    }
    
    @Test
    @DisplayName("Тест активации менеджера")
    void testActivation() {
        connectionManager.deactivate();
        assertFalse(connectionManager.isActive());
        
        connectionManager.activate();
        assertTrue(connectionManager.isActive());
    }
    
    @Test
    @DisplayName("Тест подключения бота")
    void testConnectBot() {
        String botName = "TestBot";
        BotType botType = BotType.FARMER;
        
        BotContext context = connectionManager.connectBot(botType, botName);
        
        assertNotNull(context);
        assertEquals(botType, context.getBotType());
        assertTrue(connectionManager.getConnectedBotCount() > 0);
    }
    
    @Test
    @DisplayName("Тест отключения бота")
    void testDisconnectBot() {
        String botName = "TestBot";
        BotType botType = BotType.FARMER;
        
        BotContext context = connectionManager.connectBot(botType, botName);
        assertNotNull(context);
        
        int botId = context.getBotId();
        boolean disconnected = connectionManager.disconnectBot(botId);
        
        assertTrue(disconnected);
        assertNull(connectionManager.getBot(botId));
    }
    
    @Test
    @DisplayName("Тест получения бота по ID")
    void testGetBot() {
        String botName = "TestBot";
        BotType botType = BotType.PVP;
        
        BotContext context = connectionManager.connectBot(botType, botName);
        assertNotNull(context);
        
        int botId = context.getBotId();
        EnhancedFakePlayer bot = connectionManager.getBot(botId);
        
        assertNotNull(bot);
        assertEquals(botId, bot.getBotId());
    }
    
    @Test
    @DisplayName("Тест получения всех ботов")
    void testGetAllBots() {
        connectionManager.connectBot(BotType.FARMER, "Bot1");
        connectionManager.connectBot(BotType.PVP, "Bot2");
        connectionManager.connectBot(BotType.SUPPORT, "Bot3");
        
        var allBots = connectionManager.getAllBots();
        assertEquals(3, allBots.size());
    }
    
    @Test
    @DisplayName("Тест подключения неактивного менеджера")
    void testConnectInactiveManager() {
        connectionManager.deactivate();
        
        BotContext context = connectionManager.connectBot(BotType.FARMER, "TestBot");
        assertNull(context);
    }
    
    @Test
    @DisplayName("Тест статистики подключений")
    void testConnectionStatistics() {
        connectionManager.connectBot(BotType.FARMER, "Bot1");
        connectionManager.connectBot(BotType.PVP, "Bot2");
        
        String stats = connectionManager.getConnectionStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Connected Bots: 2"));
        assertTrue(stats.contains("Active: true"));
    }
    
    @Test
    @DisplayName("Тест максимального количества ботов")
    void testMaxBotCount() {
        assertEquals(1000, connectionManager.getMaxBotCount());
    }
    
    @Test
    @DisplayName("Тест деактивации с отключением всех ботов")
    void testDeactivationWithDisconnect() {
        connectionManager.connectBot(BotType.FARMER, "Bot1");
        connectionManager.connectBot(BotType.PVP, "Bot2");
        
        assertEquals(2, connectionManager.getConnectedBotCount());
        
        connectionManager.deactivate();
        
        assertEquals(0, connectionManager.getConnectedBotCount());
        assertFalse(connectionManager.isActive());
    }
}
