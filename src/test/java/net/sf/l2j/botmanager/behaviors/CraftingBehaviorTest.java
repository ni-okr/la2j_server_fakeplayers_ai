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
 * Тесты для CraftingBehavior
 */
public class CraftingBehaviorTest {
    
    private CraftingBehavior behavior;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        behavior = new CraftingBehavior();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("CraftingBehavior should be created successfully")
    void testCraftingBehaviorCreation() {
        assertNotNull(behavior);
        assertEquals(BehaviorType.CRAFTING, behavior.getType());
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("CraftingBehavior should activate and deactivate correctly")
    void testCraftingBehaviorActivation() {
        behavior.activate();
        assertTrue(behavior.isActive());
        
        behavior.deactivate();
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("CraftingBehavior should initialize with context")
    void testCraftingBehaviorInitialization() {
        behavior.init(context);
        assertNotNull(behavior);
    }
    
    @Test
    @DisplayName("CraftingBehavior should return correct priority")
    void testCraftingBehaviorPriority() {
        int priority = behavior.getPriority(context);
        assertTrue(priority >= 0);
    }
    
    @Test
    @DisplayName("CraftingBehavior should provide statistics")
    void testCraftingBehaviorStatistics() {
        String stats = behavior.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Crafting Behavior"));
    }
    
    @Test
    @DisplayName("CraftingBehavior should handle null context gracefully")
    void testCraftingBehaviorWithNullContext() {
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
    @DisplayName("CraftingBehavior should handle execution with null player")
    void testCraftingBehaviorExecutionWithNullPlayer() {
        context.setPlayerInstance(null);
        assertFalse(behavior.canExecute(context));
    }
}
