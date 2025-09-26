package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.List;

/**
 * Тесты для AdaptiveAlgorithm.
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
@DisplayName("Adaptive Algorithm Tests")
public class AdaptiveAlgorithmTest {
    
    private FeedbackCollector feedbackCollector;
    private PerformanceAnalyzer performanceAnalyzer;
    private AdaptiveAlgorithm adaptiveAlgorithm;
    
    @BeforeEach
    void setUp() {
        feedbackCollector = FeedbackCollector.getInstance();
        performanceAnalyzer = PerformanceAnalyzer.getInstance(feedbackCollector);
        adaptiveAlgorithm = AdaptiveAlgorithm.getInstance(feedbackCollector, performanceAnalyzer);
    }
    
    @Test
    @DisplayName("Should create singleton instance")
    void testSingletonInstance() {
        assertNotNull(adaptiveAlgorithm, "AdaptiveAlgorithm instance should not be null");
        
        AdaptiveAlgorithm anotherInstance = AdaptiveAlgorithm.getInstance(feedbackCollector, performanceAnalyzer);
        assertSame(adaptiveAlgorithm, anotherInstance, "Should return same instance");
    }
    
    @Test
    @DisplayName("Should adapt bot with insufficient data")
    void testAdaptBotWithInsufficientData() {
        // Arrange
        int botId = 999;
        
        // Act
        AdaptiveAlgorithm.AdaptationResult result = adaptiveAlgorithm.adaptBot(botId);
        
        // Assert
        assertNotNull(result, "Adaptation result should not be null");
        assertEquals(botId, result.getBotId(), "Bot ID should match");
        assertFalse(result.isSuccess(), "Adaptation should fail with insufficient data");
        assertTrue(result.getMessage().contains("Недостаточно данных"), "Should indicate insufficient data");
    }
    
    @Test
    @DisplayName("Should adapt all bots")
    void testAdaptAllBots() {
        // Arrange
        // No data setup
        
        // Act
        Map<Integer, AdaptiveAlgorithm.AdaptationResult> results = adaptiveAlgorithm.adaptAllBots();
        
        // Assert
        assertNotNull(results, "Results should not be null");
        assertTrue(results.isEmpty(), "Should be empty with no data");
    }
    
    @Test
    @DisplayName("Should get and set bot parameters")
    void testGetAndSetBotParameters() {
        // Arrange
        int botId = 1;
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        params.setActionPriority(1.5);
        params.setBehaviorPriority(0.8);
        
        // Act
        adaptiveAlgorithm.setBotParams(botId, params);
        AdaptiveAlgorithm.BotAdaptationParams retrievedParams = adaptiveAlgorithm.getBotParams(botId);
        
        // Assert
        assertNotNull(retrievedParams, "Retrieved parameters should not be null");
        assertEquals(1.5, retrievedParams.getActionPriority(), 0.001, "Action priority should match");
        assertEquals(0.8, retrievedParams.getBehaviorPriority(), 0.001, "Behavior priority should match");
    }
    
    @Test
    @DisplayName("Should get adaptation history")
    void testGetAdaptationHistory() {
        // Arrange
        int botId = 1;
        
        // Act
        List<AdaptiveAlgorithm.AdaptationRecord> history = adaptiveAlgorithm.getAdaptationHistory(botId);
        
        // Assert
        assertNotNull(history, "History should not be null");
        assertTrue(history.isEmpty(), "Should be empty with no adaptations");
    }
    
    @Test
    @DisplayName("Should get adaptation stats")
    void testGetAdaptationStats() {
        // Act
        String stats = adaptiveAlgorithm.getAdaptationStats();
        
        // Assert
        assertNotNull(stats, "Stats should not be null");
        assertFalse(stats.isEmpty(), "Stats should not be empty");
        assertTrue(stats.contains("Adaptive Algorithm Statistics"), "Should contain header");
    }
    
    @Test
    @DisplayName("Should reset bot parameters")
    void testResetBotParameters() {
        // Arrange
        int botId = 1;
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        params.setActionPriority(1.5);
        adaptiveAlgorithm.setBotParams(botId, params);
        
        // Act
        adaptiveAlgorithm.resetBotParams(botId);
        
        // Assert
        AdaptiveAlgorithm.BotAdaptationParams resetParams = adaptiveAlgorithm.getBotParams(botId);
        assertNotNull(resetParams, "Should return default parameters");
        assertEquals(1.0, resetParams.getActionPriority(), 0.001, "Should have default action priority");
        assertEquals(1.0, resetParams.getBehaviorPriority(), 0.001, "Should have default behavior priority");
    }
    
    @Test
    @DisplayName("Should reset all parameters")
    void testResetAllParameters() {
        // Arrange
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        adaptiveAlgorithm.setBotParams(1, params);
        adaptiveAlgorithm.setBotParams(2, params);
        
        // Act
        adaptiveAlgorithm.resetAllParams();
        
        // Assert
        // Should not throw and parameters should be reset
        assertDoesNotThrow(() -> adaptiveAlgorithm.resetAllParams(), "Reset all should not throw");
    }
    
    @Test
    @DisplayName("Should set learning rate")
    void testSetLearningRate() {
        // Act & Assert
        assertDoesNotThrow(() -> adaptiveAlgorithm.setLearningRate(0.5), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setLearningRate(0.3), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setLearningRate(1.0), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setLearningRate(0.0), "Should not throw");
    }
    
    @Test
    @DisplayName("Should set min data points")
    void testSetMinDataPoints() {
        // Act & Assert
        assertDoesNotThrow(() -> adaptiveAlgorithm.setMinDataPoints(10), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setMinDataPoints(1), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setMinDataPoints(100), "Should not throw");
    }
    
    @Test
    @DisplayName("Should set significance threshold")
    void testSetSignificanceThreshold() {
        // Act & Assert
        assertDoesNotThrow(() -> adaptiveAlgorithm.setSignificanceThreshold(0.1), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setSignificanceThreshold(0.0), "Should not throw");
        assertDoesNotThrow(() -> adaptiveAlgorithm.setSignificanceThreshold(1.0), "Should not throw");
    }
    
    @Test
    @DisplayName("Should handle bot adaptation params")
    void testBotAdaptationParams() {
        // Arrange
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        
        // Act & Assert
        params.setActionPriority(1.5);
        assertEquals(1.5, params.getActionPriority(), 0.001, "Action priority should be set");
        
        params.setBehaviorPriority(0.8);
        assertEquals(0.8, params.getBehaviorPriority(), 0.001, "Behavior priority should be set");
        
        params.setActionFrequency(1.2);
        assertEquals(1.2, params.getActionFrequency(), 0.001, "Action frequency should be set");
        
        params.setBehaviorDuration(0.9);
        assertEquals(0.9, params.getBehaviorDuration(), 0.001, "Behavior duration should be set");
        
        params.setLearningRate(0.15);
        assertEquals(0.15, params.getLearningRate(), 0.001, "Learning rate should be set");
    }
    
    @Test
    @DisplayName("Should handle parameter bounds")
    void testParameterBounds() {
        // Arrange
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        
        // Act & Assert - Test upper bounds
        params.setActionPriority(5.0);
        assertTrue(params.getActionPriority() <= 2.0, "Action priority should be capped at 2.0");
        
        params.setBehaviorPriority(5.0);
        assertTrue(params.getBehaviorPriority() <= 2.0, "Behavior priority should be capped at 2.0");
        
        // Test lower bounds
        params.setActionPriority(-1.0);
        assertTrue(params.getActionPriority() >= 0.1, "Action priority should be floored at 0.1");
        
        params.setBehaviorPriority(-1.0);
        assertTrue(params.getBehaviorPriority() >= 0.1, "Behavior priority should be floored at 0.1");
    }
    
    @Test
    @DisplayName("Should handle increase/decrease methods")
    void testIncreaseDecreaseMethods() {
        // Arrange
        AdaptiveAlgorithm.BotAdaptationParams params = new AdaptiveAlgorithm.BotAdaptationParams();
        
        // Act
        params.increaseActionPriority(0.5);
        params.decreaseBehaviorPriority(0.3);
        params.increaseActionFrequency(0.2);
        params.decreaseBehaviorDuration(0.1);
        
        // Assert
        assertTrue(params.getActionPriority() > 1.0, "Action priority should be increased");
        assertTrue(params.getBehaviorPriority() < 1.0, "Behavior priority should be decreased");
        assertTrue(params.getActionFrequency() > 1.0, "Action frequency should be increased");
        assertTrue(params.getBehaviorDuration() < 1.0, "Behavior duration should be decreased");
    }
}