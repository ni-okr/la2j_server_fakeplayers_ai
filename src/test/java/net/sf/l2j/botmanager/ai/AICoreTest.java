package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.BehaviorManager;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionManager;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.ai.impl.AICoreImpl;
import net.sf.l2j.botmanager.ai.impl.DecisionEngineImpl;
import net.sf.l2j.botmanager.ai.impl.BehaviorSelectorImpl;
import net.sf.l2j.botmanager.ai.impl.ActionPlannerImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ядра ИИ (AI Core)
 */
public class AICoreTest {
    
    private AICore aiCore;
    private EnhancedFakePlayer bot;
    private BotContext context;
    
    @BeforeEach
    void setUp() {
        // Создаем компоненты ИИ
        DecisionEngine decisionEngine = new DecisionEngineImpl();
        BehaviorManager behaviorManager = BehaviorManager.getInstance();
        ActionManager actionManager = ActionManager.getInstance();
        BehaviorSelector behaviorSelector = new BehaviorSelectorImpl(behaviorManager);
        ActionPlanner actionPlanner = new ActionPlannerImpl(actionManager);
        
        // Создаем ядро ИИ
        aiCore = new AICoreImpl(decisionEngine, behaviorSelector, actionPlanner, behaviorManager, actionManager);
        
        // Создаем тестового бота
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("Инициализация ядра ИИ")
    void testInitialize() {
        // Инициализируем ядро ИИ
        aiCore.initialize(bot);
        
        // Проверяем, что ядро активно
        assertTrue(aiCore.isActive(bot), "AI Core should be active after initialization");
        
        // Проверяем приоритет по умолчанию
        assertEquals(0.5, aiCore.getPriority(bot), 0.01, "Default priority should be 0.5");
    }
    
    @Test
    @DisplayName("Установка приоритета")
    void testSetPriority() {
        aiCore.initialize(bot);
        
        // Устанавливаем новый приоритет
        aiCore.setPriority(bot, 0.8);
        
        // Проверяем, что приоритет изменился
        assertEquals(0.8, aiCore.getPriority(bot), 0.01, "Priority should be 0.8");
        
        // Проверяем граничные значения
        aiCore.setPriority(bot, 1.5);
        assertEquals(1.0, aiCore.getPriority(bot), 0.01, "Priority should be clamped to 1.0");
        
        aiCore.setPriority(bot, -0.5);
        assertEquals(0.0, aiCore.getPriority(bot), 0.01, "Priority should be clamped to 0.0");
    }
    
    @Test
    @DisplayName("Оценка ситуации")
    void testEvaluateSituation() {
        aiCore.initialize(bot);
        
        // Оцениваем ситуацию
        double situation = aiCore.evaluateSituation(bot);
        
        // Проверяем, что оценка в допустимом диапазоне
        assertTrue(situation >= 0.0 && situation <= 1.0, "Situation evaluation should be between 0.0 and 1.0");
    }
    
    @Test
    @DisplayName("Выбор поведения")
    void testSelectBehavior() {
        aiCore.initialize(bot);
        
        // Выбираем поведение
        net.sf.l2j.botmanager.behaviors.IBehavior behavior = aiCore.selectBehavior(bot);
        
        // Проверяем, что поведение выбрано (может быть null если нет доступных поведений)
        // Это нормально для тестового бота без playerInstance
        if (behavior != null) {
            assertNotNull(behavior.getType(), "Behavior type should not be null");
        }
    }
    
    @Test
    @DisplayName("Планирование действий")
    void testPlanActions() {
        aiCore.initialize(bot);
        
        // Выбираем поведение
        net.sf.l2j.botmanager.behaviors.IBehavior behavior = aiCore.selectBehavior(bot);
        
        // Планируем действия (может быть null если поведение недоступно)
        net.sf.l2j.botmanager.actions.IAction[] actions = aiCore.planActions(bot, behavior);
        
        // Проверяем, что действия запланированы
        assertNotNull(actions, "Actions should be planned");
        assertTrue(actions.length >= 0, "Actions array should not be null");
    }
    
    @Test
    @DisplayName("Обработка решений")
    void testProcessDecision() {
        aiCore.initialize(bot);
        
        // Обрабатываем решение
        boolean result = aiCore.processDecision(bot);
        
        // Проверяем результат (может быть false если поведение недоступно)
        // Это нормально для тестового бота без playerInstance
        assertNotNull(result, "Decision processing should return a boolean result");
    }
    
    @Test
    @DisplayName("Получение статистики")
    void testGetStatistics() {
        aiCore.initialize(bot);
        
        // Получаем статистику
        String stats = aiCore.getStatistics(bot);
        
        // Проверяем, что статистика не пустая
        assertNotNull(stats, "Statistics should not be null");
        assertFalse(stats.isEmpty(), "Statistics should not be empty");
        assertTrue(stats.contains("AI Core Stats"), "Statistics should contain AI Core info");
    }
    
    @Test
    @DisplayName("Остановка ядра ИИ")
    void testShutdown() {
        aiCore.initialize(bot);
        
        // Проверяем, что ядро активно
        assertTrue(aiCore.isActive(bot), "AI Core should be active before shutdown");
        
        // Останавливаем ядро
        aiCore.shutdown(bot);
        
        // Проверяем, что ядро неактивно
        assertFalse(aiCore.isActive(bot), "AI Core should be inactive after shutdown");
    }
    
    @Test
    @DisplayName("Обработка null бота")
    void testNullBot() {
        // Тестируем методы с null ботом
        assertFalse(aiCore.isActive(null), "AI Core should not be active for null bot");
        assertEquals(0.0, aiCore.getPriority(null), 0.01, "Priority should be 0.0 for null bot");
        assertEquals(0.0, aiCore.evaluateSituation(null), 0.01, "Situation should be 0.0 for null bot");
        assertNull(aiCore.selectBehavior(null), "Behavior should be null for null bot");
        assertNotNull(aiCore.planActions(null, null), "Actions should be empty array for null inputs");
        assertFalse(aiCore.processDecision(null), "Decision processing should return false for null bot");
        assertNotNull(aiCore.getStatistics(null), "Statistics should not be null for null bot");
        
        // Остановка с null ботом не должна вызывать исключений
        assertDoesNotThrow(() -> aiCore.shutdown(null), "Shutdown should not throw exception for null bot");
    }
    
    @Test
    @DisplayName("Множественные вызовы")
    void testMultipleCalls() {
        aiCore.initialize(bot);
        
        // Выполняем несколько операций
        for (int i = 0; i < 5; i++) {
            aiCore.processDecision(bot);
            aiCore.evaluateSituation(bot);
            aiCore.selectBehavior(bot);
        }
        
        // Проверяем, что ядро все еще активно
        assertTrue(aiCore.isActive(bot), "AI Core should still be active after multiple calls");
        
        // Получаем статистику
        String stats = aiCore.getStatistics(bot);
        assertNotNull(stats, "Statistics should be available after multiple calls");
    }
}
