package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.core.BotContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для MachineLearningEngine
 * 
 * Проверяет корректность работы движка машинного обучения,
 * включая регистрацию ботов, обучение и предсказания.
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
@DisplayName("MachineLearningEngine Tests")
class MachineLearningEngineTest {
    
    private MachineLearningEngine engine;
    private EnhancedFakePlayer bot;
    private BotContext context;
    
    @BeforeEach
    void setUp() {
        engine = MachineLearningEngine.getInstance();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
        context.setBot(bot);
    }
    
    @Test
    @DisplayName("Should create MachineLearningEngine singleton")
    void testCreateEngine() {
        assertNotNull(engine, "Engine should be created");
        assertSame(engine, MachineLearningEngine.getInstance(), "Should return same instance");
    }
    
    @Test
    @DisplayName("Should start and stop engine")
    void testStartStopEngine() {
        assertFalse(engine.isActive(), "Engine should be inactive initially");
        
        engine.start();
        assertTrue(engine.isActive(), "Engine should be active after start");
        
        engine.stop();
        assertFalse(engine.isActive(), "Engine should be inactive after stop");
    }
    
    @Test
    @DisplayName("Should register and unregister bot")
    void testRegisterUnregisterBot() {
        int botId = bot.getBotId();
        
        // Unregister bot first to ensure clean state
        engine.unregisterBot(botId);
        assertFalse(engine.isBotRegistered(botId), "Bot should not be registered initially");
        
        engine.registerBot(bot);
        assertTrue(engine.isBotRegistered(botId), "Bot should be registered after registration");
        
        engine.unregisterBot(botId);
        assertFalse(engine.isBotRegistered(botId), "Bot should not be registered after unregistration");
    }
    
    @Test
    @DisplayName("Should handle null bot registration")
    void testRegisterNullBot() {
        engine.registerBot(null);
        // Should not throw exception
        assertTrue(true, "Should handle null bot gracefully");
    }
    
    @Test
    @DisplayName("Should get engine statistics")
    void testGetEngineStats() {
        MachineLearningEngine.EngineStats stats = engine.getEngineStats();
        
        assertNotNull(stats, "Stats should not be null");
        assertTrue(stats.getTotalDataProcessed() >= 0, "Total data processed should be non-negative");
        assertTrue(stats.getTotalPredictions() >= 0, "Total predictions should be non-negative");
        assertTrue(stats.getSuccessfulPredictions() >= 0, "Successful predictions should be non-negative");
        assertTrue(stats.getRegisteredBots() >= 0, "Registered bots should be non-negative");
    }
    
    @Test
    @DisplayName("Should get bot learning statistics")
    void testGetBotLearningStats() {
        int botId = bot.getBotId();
        
        // Unregister bot first to ensure clean state
        engine.unregisterBot(botId);
        
        // Before registration
        MachineLearningEngine.LearningStats stats = engine.getBotLearningStats(botId);
        assertNull(stats, "Stats should be null for unregistered bot");
        
        // After registration
        engine.registerBot(bot);
        stats = engine.getBotLearningStats(botId);
        assertNotNull(stats, "Stats should not be null for registered bot");
        assertEquals(botId, stats.getBotId(), "Stats should have correct bot ID");
    }
    
    @Test
    @DisplayName("Should handle training with null data")
    void testTrainWithNullData() {
        int botId = bot.getBotId();
        engine.registerBot(bot);
        
        boolean result = engine.trainBot(botId, null);
        assertFalse(result, "Training should fail with null data");
    }
    
    @Test
    @DisplayName("Should handle training with unregistered bot")
    void testTrainWithUnregisteredBot() {
        TrainingData data = new TrainingData();
        boolean result = engine.trainBot(999, data);
        assertFalse(result, "Training should fail with unregistered bot");
    }
    
    @Test
    @DisplayName("Should handle prediction with null context")
    void testPredictWithNullContext() {
        int botId = bot.getBotId();
        engine.registerBot(bot);
        
        ActionPrediction prediction = engine.predictAction(botId, null);
        assertNull(prediction, "Prediction should be null with null context");
    }
    
    @Test
    @DisplayName("Should handle prediction with unregistered bot")
    void testPredictWithUnregisteredBot() {
        PredictionContext context = new PredictionContext();
        ActionPrediction prediction = engine.predictAction(999, context);
        assertNull(prediction, "Prediction should be null with unregistered bot");
    }
    
    @Test
    @DisplayName("Should handle adaptation with null feedback")
    void testAdaptWithNullFeedback() {
        int botId = bot.getBotId();
        engine.registerBot(bot);
        
        boolean result = engine.adaptModel(botId, null);
        assertFalse(result, "Adaptation should fail with null feedback");
    }
    
    @Test
    @DisplayName("Should handle adaptation with unregistered bot")
    void testAdaptWithUnregisteredBot() {
        LearningFeedback feedback = new LearningFeedback();
        boolean result = engine.adaptModel(999, feedback);
        assertFalse(result, "Adaptation should fail with unregistered bot");
    }
    
    @Test
    @DisplayName("Should get all bot statistics")
    void testGetAllBotStats() {
        int botId = bot.getBotId();
        
        // Unregister bot first to ensure clean state
        engine.unregisterBot(botId);
        
        // Before registration
        Map<Integer, MachineLearningEngine.LearningStats> allStats = engine.getAllBotStats();
        assertNotNull(allStats, "All stats should not be null");
        assertTrue(allStats.isEmpty(), "All stats should be empty initially");
        
        // After registration
        engine.registerBot(bot);
        allStats = engine.getAllBotStats();
        assertNotNull(allStats, "All stats should not be null");
        assertFalse(allStats.isEmpty(), "All stats should not be empty after registration");
    }
    
    @Test
    @DisplayName("Should handle multiple bot registrations")
    void testMultipleBotRegistrations() {
        // Register first bot
        engine.registerBot(bot);
        assertTrue(engine.isBotRegistered(bot.getBotId()), "First bot should be registered");
        
        // Register second bot
        BotContext context2 = new BotContext(2);
        EnhancedFakePlayer bot2 = new EnhancedFakePlayer(context2, null);
        context2.setBot(bot2);
        engine.registerBot(bot2);
        
        assertTrue(engine.isBotRegistered(bot.getBotId()), "First bot should still be registered");
        assertTrue(engine.isBotRegistered(bot2.getBotId()), "Second bot should be registered");
        
        // Check engine stats
        MachineLearningEngine.EngineStats stats = engine.getEngineStats();
        assertEquals(2, stats.getRegisteredBots(), "Should have 2 registered bots");
    }
    
    @Test
    @DisplayName("Should handle inactive engine operations")
    void testInactiveEngineOperations() {
        int botId = bot.getBotId();
        engine.registerBot(bot);
        engine.stop(); // Stop engine
        
        // Training should fail
        TrainingData data = new TrainingData();
        boolean trainResult = engine.trainBot(botId, data);
        assertFalse(trainResult, "Training should fail when engine is inactive");
        
        // Prediction should fail
        PredictionContext context = new PredictionContext();
        ActionPrediction prediction = engine.predictAction(botId, context);
        assertNull(prediction, "Prediction should be null when engine is inactive");
        
        // Adaptation should fail
        LearningFeedback feedback = new LearningFeedback();
        boolean adaptResult = engine.adaptModel(botId, feedback);
        assertFalse(adaptResult, "Adaptation should fail when engine is inactive");
    }
}
