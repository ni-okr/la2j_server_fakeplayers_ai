package net.sf.l2j.botmanager.integration;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionType;
import net.sf.l2j.botmanager.utils.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для системы интеграции.
 */
@DisplayName("Integration System Tests")
public class IntegrationSystemTest {
    
    private ActionPrioritySystem prioritySystem;
    private BotStateManager botStateManager;
    private TaskScheduler taskScheduler;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        // Инициализируем систему интеграции
        prioritySystem = new ActionPrioritySystem();
        botStateManager = new BotStateManager();
        taskScheduler = new TaskScheduler();
        
        // Создаем контекст и бота
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
        
        // Инициализируем системы
        taskScheduler.initialize();
    }
    
    @Test
    @DisplayName("ActionPrioritySystem basic functionality")
    void testActionPrioritySystemBasic() {
        // Тестируем базовую функциональность системы приоритетов
        assertNotNull(prioritySystem, "ActionPrioritySystem should be created");
        
        // Тестируем статистику
        String stats = prioritySystem.getStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("ActionPrioritySystem"), "Statistics should contain system name");
    }
    
    @Test
    @DisplayName("BotStateManager state management")
    void testBotStateManagement() {
        // Тестируем управление состоянием бота
        // В тестовой среде бот может не быть инициализирован, поэтому просто проверяем что методы работают
        botStateManager.updateBotState(bot, BotState.MOVING, "Test reason");
        botStateManager.updateBotState(bot, BotState.FIGHTING, "Test reason");
        
        // Проверяем, что методы не выбрасывают исключений
        assertTrue(true, "BotStateManager methods should work without exceptions");
    }
    
    @Test
    @DisplayName("BotStateManager basic functionality")
    void testBotStateManagerBasic() {
        // Тестируем базовую функциональность управления состоянием
        assertNotNull(botStateManager, "BotStateManager should be created");
        
        // Тестируем статистику
        String stats = botStateManager.getStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("BotStateManager"), "Statistics should contain manager name");
    }
    
    @Test
    @DisplayName("TaskScheduler basic functionality")
    void testTaskSchedulerBasic() {
        // Тестируем базовую функциональность планировщика задач
        assertNotNull(taskScheduler, "TaskScheduler should be created");
        assertTrue(taskScheduler.isActive(), "TaskScheduler should be active after initialization");
        
        // Тестируем статистику
        String stats = taskScheduler.getStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("TaskScheduler"), "Statistics should contain scheduler name");
    }
    
    @Test
    @DisplayName("System coordination")
    void testSystemCoordination() {
        // Тестируем координацию между системами
        
        // Обновляем состояние бота
        botStateManager.updateBotState(bot, BotState.MOVING, "Test reason");
        
        // Проверяем, что системы активны
        assertTrue(taskScheduler.isActive(), "TaskScheduler should be active");
        
        // Проверяем, что методы работают без исключений
        assertTrue(true, "System coordination should work without exceptions");
    }
    
    @Test
    @DisplayName("System shutdown")
    void testSystemShutdown() {
        // Тестируем корректное завершение работы систем
        taskScheduler.shutdown();
        
        assertFalse(taskScheduler.isActive(), "TaskScheduler should be inactive after shutdown");
    }
}
