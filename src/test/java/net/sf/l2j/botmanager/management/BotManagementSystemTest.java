package net.sf.l2j.botmanager.management;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.management.statistics.BotStatistics;
import net.sf.l2j.botmanager.management.monitoring.BotStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для системы управления ботами
 */
public class BotManagementSystemTest {
    
    private BotManagementSystem botManagement;
    
    @BeforeEach
    void setUp() {
        botManagement = BotManagementSystem.getInstance();
        botManagement.initialize();
    }
    
    @Test
    @DisplayName("BotManagementSystem should be created successfully")
    void testBotManagementSystemCreation() {
        assertNotNull(botManagement);
        assertTrue(botManagement.isInitialized());
    }
    
    @Test
    @DisplayName("BotManagementSystem should initialize and shutdown correctly")
    void testInitializationAndShutdown() {
        assertTrue(botManagement.isInitialized());
        
        botManagement.shutdown();
        assertFalse(botManagement.isInitialized());
        
        botManagement.initialize();
        assertTrue(botManagement.isInitialized());
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle bot creation")
    void testBotCreation() {
        // Test bot creation (mock implementation)
        // EnhancedFakePlayer bot = botManagement.createBot(BotType.FARMER, "TestBot", 50);
        // assertNull(bot); // Mock implementation returns null
        
        // Test with invalid parameters
        assertNull(botManagement.createBot(null, "TestBot", 50));
        assertNull(botManagement.createBot(BotType.FARMER, null, 50));
        assertNull(botManagement.createBot(BotType.FARMER, "TestBot", 0));
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle bot removal")
    void testBotRemoval() {
        // Test removing non-existent bot
        assertFalse(botManagement.removeBot(999));
        
        // Test with invalid ID
        assertFalse(botManagement.removeBot(0));
        assertFalse(botManagement.removeBot(-1));
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle bot retrieval")
    void testBotRetrieval() {
        // Test getting non-existent bot
        assertNull(botManagement.getBot(999));
        
        // Test getting all bots
        List<net.sf.l2j.botmanager.core.EnhancedFakePlayer> allBots = botManagement.getAllBots();
        assertNotNull(allBots);
        assertTrue(allBots.isEmpty());
        
        // Test getting bots by type
        List<net.sf.l2j.botmanager.core.EnhancedFakePlayer> farmerBots = botManagement.getBotsByType(BotType.FARMER);
        assertNotNull(farmerBots);
        assertTrue(farmerBots.isEmpty());
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle behavior management")
    void testBehaviorManagement() {
        // Test setting behavior for non-existent bot
        assertFalse(botManagement.setBotBehavior(999, BehaviorType.FARMING));
        
        // Test getting behavior for non-existent bot
        assertNull(botManagement.getBotBehavior(999));
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle bot activation")
    void testBotActivation() {
        // Test activating non-existent bot
        assertFalse(botManagement.activateBot(999));
        
        // Test deactivating non-existent bot
        assertFalse(botManagement.deactivateBot(999));
        
        // Test checking activity of non-existent bot
        assertFalse(botManagement.isBotActive(999));
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle bot status")
    void testBotStatus() {
        // Test getting status of non-existent bot
        assertNull(botManagement.getBotStatus(999));
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle bot statistics")
    void testBotStatistics() {
        // Test getting statistics of non-existent bot
        assertNull(botManagement.getBotStatistics(999));
    }
    
    @Test
    @DisplayName("BotManagementSystem should provide overall statistics")
    void testOverallStatistics() {
        Map<String, Object> stats = botManagement.getOverallStatistics();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalBots"));
        assertTrue(stats.containsKey("activeBots"));
        assertTrue(stats.containsKey("inactiveBots"));
        assertTrue(stats.containsKey("systemUptime"));
        assertTrue(stats.containsKey("initialized"));
        
        assertEquals(0, stats.get("totalBots"));
        assertEquals(0, stats.get("activeBots"));
        assertEquals(0, stats.get("inactiveBots"));
        assertTrue((Boolean) stats.get("initialized"));
    }
    
    @Test
    @DisplayName("BotManagementSystem should provide bot type statistics")
    void testBotTypeStatistics() {
        Map<BotType, Integer> stats = botManagement.getBotTypeStatistics();
        assertNotNull(stats);
        
        // Check that all bot types are present
        for (BotType type : BotType.values()) {
            assertTrue(stats.containsKey(type));
            assertEquals(0, stats.get(type));
        }
    }
    
    @Test
    @DisplayName("BotManagementSystem should provide behavior statistics")
    void testBehaviorStatistics() {
        Map<BehaviorType, Integer> stats = botManagement.getBehaviorStatistics();
        assertNotNull(stats);
        
        // Check that all behavior types are present
        for (BehaviorType type : BehaviorType.values()) {
            assertTrue(stats.containsKey(type));
            assertEquals(0, stats.get(type));
        }
    }
    
    @Test
    @DisplayName("BotManagementSystem should handle shutdown when not initialized")
    void testShutdownWhenNotInitialized() {
        botManagement.shutdown();
        assertFalse(botManagement.isInitialized());
        
        // Shutdown again should not cause errors
        botManagement.shutdown();
        assertFalse(botManagement.isInitialized());
    }
}
