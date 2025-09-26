package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.ai.impl.DecisionEngineImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для движка принятия решений (Decision Engine)
 */
public class DecisionEngineTest {
    
    private DecisionEngine decisionEngine;
    private EnhancedFakePlayer bot;
    private BotContext context;
    
    @BeforeEach
    void setUp() {
        decisionEngine = new DecisionEngineImpl();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("Анализ ситуации")
    void testAnalyzeSituation() {
        // Анализируем ситуацию
        SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
        
        // Проверяем, что анализ не null
        assertNotNull(analysis, "Situation analysis should not be null");
        
        // Проверяем основные поля
        assertTrue(analysis.getHealthLevel() >= 0.0 && analysis.getHealthLevel() <= 1.0, 
                  "Health level should be between 0.0 and 1.0");
        assertTrue(analysis.getManaLevel() >= 0.0 && analysis.getManaLevel() <= 1.0, 
                  "Mana level should be between 0.0 and 1.0");
        assertTrue(analysis.getDangerLevel() >= 0.0 && analysis.getDangerLevel() <= 1.0, 
                  "Danger level should be between 0.0 and 1.0");
        assertTrue(analysis.getNearbyEnemies() >= 0, "Nearby enemies should be non-negative");
        assertTrue(analysis.getNearbyAllies() >= 0, "Nearby allies should be non-negative");
        assertTrue(analysis.getRecommendedPriority() >= 0.0 && analysis.getRecommendedPriority() <= 1.0, 
                  "Recommended priority should be between 0.0 and 1.0");
    }
    
    @Test
    @DisplayName("Принятие решения")
    void testMakeDecision() {
        // Анализируем ситуацию
        SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
        
        // Принимаем решение
        Decision decision = decisionEngine.makeDecision(bot, analysis);
        
        // Проверяем, что решение не null
        assertNotNull(decision, "Decision should not be null");
        
        // Проверяем основные поля решения
        assertNotNull(decision.getSelectedBehavior(), "Selected behavior should not be null");
        assertNotNull(decision.getActionSequence(), "Action sequence should not be null");
        assertTrue(decision.getPriority() >= 0.0 && decision.getPriority() <= 1.0, 
                  "Decision priority should be between 0.0 and 1.0");
        assertTrue(decision.getConfidence() >= 0.0 && decision.getConfidence() <= 1.0, 
                  "Decision confidence should be between 0.0 and 1.0");
        assertTrue(decision.getDecisionTime() > 0, "Decision time should be positive");
    }
    
    @Test
    @DisplayName("Оценка приоритета поведения")
    void testEvaluateBehaviorPriority() {
        // Создаем мок поведения
        MockBehavior behavior = new MockBehavior(BehaviorType.FARMING);
        
        // Оцениваем приоритет
        double priority = decisionEngine.evaluateBehaviorPriority(bot, behavior);
        
        // Проверяем, что приоритет в допустимом диапазоне
        assertTrue(priority >= 0.0 && priority <= 1.0, "Behavior priority should be between 0.0 and 1.0");
    }
    
    @Test
    @DisplayName("Оценка приоритета действия")
    void testEvaluateActionPriority() {
        // Создаем мок действия
        MockAction action = new MockAction(ActionType.MOVE);
        
        // Оцениваем приоритет
        double priority = decisionEngine.evaluateActionPriority(bot, action);
        
        // Проверяем, что приоритет в допустимом диапазоне
        assertTrue(priority >= 0.0 && priority <= 1.0, "Action priority should be between 0.0 and 1.0");
    }
    
    @Test
    @DisplayName("Проверка возможности выполнения действия")
    void testCanExecuteAction() {
        // Создаем мок действия
        MockAction action = new MockAction(ActionType.MOVE);
        
        // Проверяем возможность выполнения
        boolean canExecute = decisionEngine.canExecuteAction(bot, action);
        
        // Проверяем результат
        assertTrue(canExecute, "Action should be executable");
    }
    
    @Test
    @DisplayName("Обновление контекста")
    void testUpdateContext() {
        // Создаем новый контекст
        BotContext newContext = new BotContext(2);
        
        // Обновляем контекст
        assertDoesNotThrow(() -> decisionEngine.updateContext(bot, newContext), 
                          "Context update should not throw exception");
    }
    
    @Test
    @DisplayName("Получение статистики")
    void testGetStatistics() {
        // Получаем статистику
        String stats = decisionEngine.getStatistics(bot);
        
        // Проверяем, что статистика не пустая
        assertNotNull(stats, "Statistics should not be null");
        assertFalse(stats.isEmpty(), "Statistics should not be empty");
        assertTrue(stats.contains("Decision Engine Stats"), "Statistics should contain Decision Engine info");
    }
    
    @Test
    @DisplayName("Обработка null входных данных")
    void testNullInputs() {
        // Тестируем с null ботом
        SituationAnalysis analysis1 = decisionEngine.analyzeSituation(null);
        assertNotNull(analysis1, "Analysis should not be null for null bot");
        
        Decision decision1 = decisionEngine.makeDecision(null, null);
        assertNull(decision1, "Decision should be null for null inputs");
        
        double priority1 = decisionEngine.evaluateBehaviorPriority(null, null);
        assertEquals(0.0, priority1, 0.01, "Priority should be 0.0 for null inputs");
        
        double priority2 = decisionEngine.evaluateActionPriority(null, null);
        assertEquals(0.0, priority2, 0.01, "Priority should be 0.0 for null inputs");
        
        boolean canExecute = decisionEngine.canExecuteAction(null, null);
        assertFalse(canExecute, "Action should not be executable for null inputs");
        
        String stats = decisionEngine.getStatistics(null);
        assertNotNull(stats, "Statistics should not be null for null bot");
    }
    
    @Test
    @DisplayName("Множественные вызовы")
    void testMultipleCalls() {
        // Выполняем несколько операций
        for (int i = 0; i < 5; i++) {
            SituationAnalysis analysis = decisionEngine.analyzeSituation(bot);
            assertNotNull(analysis, "Analysis should not be null on iteration " + i);
            
            Decision decision = decisionEngine.makeDecision(bot, analysis);
            assertNotNull(decision, "Decision should not be null on iteration " + i);
        }
        
        // Получаем статистику
        String stats = decisionEngine.getStatistics(bot);
        assertNotNull(stats, "Statistics should be available after multiple calls");
    }
    
    // Вспомогательные классы для тестирования
    
    private static class MockBehavior implements net.sf.l2j.botmanager.behaviors.IBehavior {
        private final BehaviorType type;
        
        public MockBehavior(BehaviorType type) {
            this.type = type;
        }
        
        @Override
        public boolean execute(net.sf.l2j.botmanager.core.EnhancedFakePlayer bot) {
            return true;
        }
        
        @Override
        public BehaviorType getType() {
            return type;
        }
        
        @Override
        public void init(net.sf.l2j.botmanager.core.BotContext context) {}
        
        @Override
        public void onEnd(net.sf.l2j.botmanager.core.BotContext context) {}
        
        @Override
        public boolean canExecute(net.sf.l2j.botmanager.core.BotContext context) {
            return true;
        }
        
        @Override
        public int getPriority() {
            return 50;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public void activate() {}
        
        @Override
        public void deactivate() {}
        
        @Override
        public String getStatistics() {
            return "Mock Behavior";
        }
    }
    
    private static class MockAction implements net.sf.l2j.botmanager.actions.IAction {
        private final ActionType type;
        
        public MockAction(ActionType type) {
            this.type = type;
        }
        
        @Override
        public boolean execute(net.sf.l2j.botmanager.core.EnhancedFakePlayer bot) {
            return true;
        }
        
        @Override
        public ActionType getType() {
            return type;
        }
        
        @Override
        public void init(net.sf.l2j.botmanager.core.BotContext context) {}
        
        @Override
        public void onEnd(net.sf.l2j.botmanager.core.BotContext context) {}
        
        @Override
        public boolean canExecute(net.sf.l2j.botmanager.core.BotContext context) {
            return true;
        }
        
        @Override
        public int getPriority() {
            return 50;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public void activate() {}
        
        @Override
        public void deactivate() {}
        
        @Override
        public boolean canInterrupt() {
            return true;
        }
        
        @Override
        public void interrupt() {}
        
        @Override
        public long getExecutionTime() {
            return 1000;
        }
        
        @Override
        public long getRemainingTime() {
            return 500;
        }
        
        @Override
        public boolean isCompleted() {
            return false;
        }
        
        @Override
        public String getStatistics() {
            return "Mock Action";
        }
    }
}
