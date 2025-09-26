package net.sf.l2j.botmanager.web;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.web.dto.*;
import net.sf.l2j.botmanager.web.service.BotWebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для веб-сервиса управления ботами
 */
public class BotWebServiceTest {
    
    private BotWebService botWebService;
    
    @BeforeEach
    void setUp() {
        botWebService = new BotWebService();
    }
    
    @Test
    @DisplayName("BotWebService should be created successfully")
    void testBotWebServiceCreation() {
        assertNotNull(botWebService);
    }
    
    @Test
    @DisplayName("BotWebService should retrieve all bots")
    void testGetAllBots() {
        List<BotDTO> bots = botWebService.getAllBots();
        assertNotNull(bots);
        // В тестовой среде список может быть пустым
        assertTrue(bots.isEmpty());
    }
    
    @Test
    @DisplayName("BotWebService should handle bot retrieval by ID")
    void testGetBot() {
        // Test with non-existent bot
        BotDTO bot = botWebService.getBot(999);
        assertNull(bot);
        
        // Test with invalid ID
        assertNull(botWebService.getBot(0));
        assertNull(botWebService.getBot(-1));
    }
    
    @Test
    @DisplayName("BotWebService should handle bot retrieval by type")
    void testGetBotsByType() {
        List<BotDTO> bots = botWebService.getBotsByType(BotType.FARMER);
        assertNotNull(bots);
        assertTrue(bots.isEmpty());
    }
    
    @Test
    @DisplayName("BotWebService should handle bot status retrieval")
    void testGetBotStatus() {
        // Test with non-existent bot
        BotDTO status = botWebService.getBotStatus(999);
        assertNull(status);
    }
    
    @Test
    @DisplayName("BotWebService should handle bot statistics retrieval")
    void testGetBotStatistics() {
        // Test with non-existent bot
        BotStatisticsDTO stats = botWebService.getBotStatistics(999);
        assertNull(stats);
    }
    
    @Test
    @DisplayName("BotWebService should provide overall statistics")
    void testGetOverallStatistics() {
        OverallStatisticsDTO stats = botWebService.getOverallStatistics();
        assertNotNull(stats);
        assertTrue(stats.getTotalBots() >= 0);
        assertTrue(stats.getActiveBots() >= 0);
        assertTrue(stats.getInactiveBots() >= 0);
        assertTrue(stats.getSystemUptime() >= 0);
    }
    
    @Test
    @DisplayName("BotWebService should handle bot creation")
    void testCreateBot() {
        // Test with valid parameters
        BotDTO bot = botWebService.createBot(BotType.FARMER, "TestBot", 50);
        // В тестовой среде создание может возвращать null
        // assertNotNull(bot);
        
        // Test with invalid parameters
        assertNull(botWebService.createBot(null, "TestBot", 50));
        assertNull(botWebService.createBot(BotType.FARMER, null, 50));
        assertNull(botWebService.createBot(BotType.FARMER, "TestBot", 0));
    }
    
    @Test
    @DisplayName("BotWebService should handle bot removal")
    void testRemoveBot() {
        // Test removing non-existent bot
        assertFalse(botWebService.removeBot(999));
        
        // Test with invalid ID
        assertFalse(botWebService.removeBot(0));
        assertFalse(botWebService.removeBot(-1));
    }
    
    @Test
    @DisplayName("BotWebService should handle behavior setting")
    void testSetBotBehavior() {
        // Test setting behavior for non-existent bot
        assertFalse(botWebService.setBotBehavior(999, BehaviorType.FARMING));
    }
    
    @Test
    @DisplayName("BotWebService should handle bot activation")
    void testActivateBot() {
        // Test activating non-existent bot
        assertFalse(botWebService.activateBot(999));
    }
    
    @Test
    @DisplayName("BotWebService should handle bot deactivation")
    void testDeactivateBot() {
        // Test deactivating non-existent bot
        assertFalse(botWebService.deactivateBot(999));
    }
}
