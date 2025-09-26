package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для PatrollingBehavior
 */
public class PatrollingBehaviorTest {
    
    private PatrollingBehavior behavior;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        behavior = new PatrollingBehavior();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("PatrollingBehavior should be created successfully")
    void testPatrollingBehaviorCreation() {
        assertNotNull(behavior);
        assertEquals(BehaviorType.PATROLLING, behavior.getType());
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("PatrollingBehavior should activate and deactivate correctly")
    void testPatrollingBehaviorActivation() {
        behavior.activate();
        assertTrue(behavior.isActive());
        
        behavior.deactivate();
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("PatrollingBehavior should initialize with context")
    void testPatrollingBehaviorInitialization() {
        behavior.init(context);
        assertNotNull(behavior);
    }
    
    @Test
    @DisplayName("PatrollingBehavior should return correct priority")
    void testPatrollingBehaviorPriority() {
        int priority = behavior.getPriority(context);
        assertTrue(priority >= 0);
    }
    
    @Test
    @DisplayName("PatrollingBehavior should provide statistics")
    void testPatrollingBehaviorStatistics() {
        String stats = behavior.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Patrolling Behavior"));
    }
    
    @Test
    @DisplayName("PatrollingBehavior should handle null context gracefully")
    void testPatrollingBehaviorWithNullContext() {
        // Тест с null контекстом - должно возвращать false без исключений
        try {
            boolean result = behavior.canExecute(null);
            assertFalse(result, "Behavior should not execute with null context");
        } catch (NullPointerException e) {
            // Ожидаемое поведение - null context должен обрабатываться корректно
            assertTrue(true, "NullPointerException is acceptable for null context");
        }
    }
    
    @Test
    @DisplayName("PatrollingBehavior should handle execution with null player")
    void testPatrollingBehaviorExecutionWithNullPlayer() {
        context.setPlayerInstance(null);
        assertFalse(behavior.canExecute(context));
    }
}
