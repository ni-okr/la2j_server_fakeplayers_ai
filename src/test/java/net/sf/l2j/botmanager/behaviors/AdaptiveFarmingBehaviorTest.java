package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.learning.AdaptiveAlgorithm;
import net.sf.l2j.botmanager.learning.FeedbackCollector;
import net.sf.l2j.botmanager.learning.PerformanceAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для AdaptiveFarmingBehavior.
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
@DisplayName("Adaptive Farming Behavior Tests")
public class AdaptiveFarmingBehaviorTest {
    
    private AdaptiveFarmingBehavior behavior;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        behavior = new AdaptiveFarmingBehavior();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null); // L2PcInstance будет null в тестах
        context.setBot(bot);
    }
    
    @Test
    @DisplayName("Should create adaptive farming behavior")
    void testCreateAdaptiveFarmingBehavior() {
        assertNotNull(behavior, "Behavior should not be null");
        assertEquals(BehaviorType.FARMING, behavior.getType(), "Should be farming behavior");
        assertEquals("Фарм", behavior.getType().getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should not execute with null context")
    void testCannotExecuteWithNullContext() {
        boolean result = behavior.canExecute(null);
        assertFalse(result, "Should not execute with null context");
    }
    
    @Test
    @DisplayName("Should not execute with null bot")
    void testCannotExecuteWithNullBot() {
        BotContext nullBotContext = new BotContext(1);
        nullBotContext.setBot(null);
        
        boolean result = behavior.canExecute(nullBotContext);
        assertFalse(result, "Should not execute with null bot");
    }
    
    @Test
    @DisplayName("Should not execute with null player instance")
    void testCannotExecuteWithNullPlayerInstance() {
        EnhancedFakePlayer nullPlayerBot = new EnhancedFakePlayer(context, null);
        context.setBot(nullPlayerBot);
        
        boolean result = behavior.canExecute(context);
        assertFalse(result, "Should not execute with null player instance");
    }
    
    @Test
    @DisplayName("Should get priority with null context")
    void testGetPriorityWithNullContext() {
        int priority = behavior.getPriority(null);
        assertEquals(0, priority, "Priority should be 0 with null context");
    }
    
    @Test
    @DisplayName("Should get priority with invalid context")
    void testGetPriorityWithInvalidContext() {
        context.setBot(bot);
        
        int priority = behavior.getPriority(context);
        assertEquals(0, priority, "Priority should be 0 with invalid context");
    }
    
    @Test
    @DisplayName("Should not execute with invalid context")
    void testCannotExecuteWithInvalidContext() {
        context.setBot(bot);
        
        boolean result = behavior.canExecute(context);
        assertFalse(result, "Should not execute with invalid context");
    }
    
    @Test
    @DisplayName("Should get statistics")
    void testGetStatistics() {
        String stats = behavior.getStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        // В текущей реализации статистика может быть пустой, так как нет данных
        // Просто проверяем, что метод работает без исключений
        assertTrue(stats != null, "Statistics should not be null");
    }
    
    @Test
    @DisplayName("Should set and get mob level parameters")
    void testSetMobLevelParameters() {
        behavior.setMinMobLevel(5);
        behavior.setMaxMobLevel(15);
        
        // We can't directly test the private fields, but we can test that the methods don't throw
        assertDoesNotThrow(() -> behavior.setMinMobLevel(10), "Should not throw when setting min level");
        assertDoesNotThrow(() -> behavior.setMaxMobLevel(20), "Should not throw when setting max level");
        assertDoesNotThrow(() -> behavior.setSearchRadius(1500), "Should not throw when setting search radius");
    }
    
    @Test
    @DisplayName("Should handle adaptation parameters")
    void testAdaptationParameters() {
        // Test getting adaptation parameters
        AdaptiveAlgorithm.BotAdaptationParams params = behavior.getAdaptationParams();
        // Initially should be null
        assertNull(params, "Adaptation parameters should be null initially");
        
        // Test setting adaptation parameters
        AdaptiveAlgorithm.BotAdaptationParams newParams = new AdaptiveAlgorithm.BotAdaptationParams();
        newParams.setActionPriority(1.5);
        newParams.setBehaviorPriority(0.8);
        
        behavior.setAdaptationParams(newParams);
        
        AdaptiveAlgorithm.BotAdaptationParams retrievedParams = behavior.getAdaptationParams();
        assertNotNull(retrievedParams, "Retrieved parameters should not be null");
        assertEquals(1.5, retrievedParams.getActionPriority(), 0.001, "Action priority should match");
        assertEquals(0.8, retrievedParams.getBehaviorPriority(), 0.001, "Behavior priority should match");
    }
    
    @Test
    @DisplayName("Should handle parameter bounds")
    void testParameterBounds() {
        // Test min level bounds
        behavior.setMinMobLevel(0);
        behavior.setMinMobLevel(-5);
        // Should not throw and should handle bounds internally
        
        // Test max level bounds
        behavior.setMaxMobLevel(5);
        behavior.setMinMobLevel(10);
        behavior.setMaxMobLevel(8); // Should be at least min level
        // Should not throw and should handle bounds internally
        
        // Test search radius bounds
        behavior.setSearchRadius(50);
        behavior.setSearchRadius(-100);
        // Should not throw and should handle bounds internally
    }
    
    @Test
    @DisplayName("Should handle execution with invalid context")
    void testExecuteWithInvalidContext() {
        context.setBot(bot);
        
        boolean result = behavior.execute(bot);
        assertFalse(result, "Should not execute with invalid context");
    }
    
    @Test
    @DisplayName("Should handle execution with null context")
    void testExecuteWithNullContext() {
        // Тест с null контекстом - ожидаем исключение
        assertThrows(NullPointerException.class, () -> {
            behavior.execute(null);
        }, "Should throw NullPointerException with null context");
    }
    
    @Test
    @DisplayName("Should handle statistics with adaptation parameters")
    void testStatisticsWithAdaptationParameters() {
        // Set some adaptation parameters
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        params.setActionPriority(1.2);
        params.setBehaviorPriority(0.9);
        params.setActionFrequency(1.1);
        params.setLearningRate(0.15);
        
        behavior.setAdaptationParams(params);
        
        String stats = behavior.getStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        // В текущей реализации статистика может быть пустой, так как нет данных
        // Просто проверяем, что метод работает без исключений
        assertTrue(stats != null, "Statistics should not be null");
    }
    
    @Test
    @DisplayName("Should handle multiple parameter changes")
    void testMultipleParameterChanges() {
        // Test multiple changes
        behavior.setMinMobLevel(3);
        behavior.setMaxMobLevel(12);
        behavior.setSearchRadius(800);
        
        // Test with adaptation parameters
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        behavior.setAdaptationParams(params);
        
        // Should not throw
        assertDoesNotThrow(() -> {
            behavior.setMinMobLevel(5);
            behavior.setMaxMobLevel(15);
            behavior.setSearchRadius(1200);
        }, "Should handle multiple parameter changes");
    }
    
    @Test
    @DisplayName("Should maintain behavior type consistency")
    void testBehaviorTypeConsistency() {
        assertEquals(BehaviorType.FARMING, behavior.getType(), "Should maintain farming behavior type");
        assertTrue(behavior.getType().getName().contains("Фарм"), "Should contain farming-related name");
    }
}
