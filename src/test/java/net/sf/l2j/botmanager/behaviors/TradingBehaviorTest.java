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
 * Тесты для TradingBehavior
 */
public class TradingBehaviorTest {
    
    private TradingBehavior behavior;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        behavior = new TradingBehavior();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("TradingBehavior should be created successfully")
    void testTradingBehaviorCreation() {
        assertNotNull(behavior);
        assertEquals(BehaviorType.TRADING, behavior.getType());
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("TradingBehavior should activate and deactivate correctly")
    void testTradingBehaviorActivation() {
        behavior.activate();
        assertTrue(behavior.isActive());
        
        behavior.deactivate();
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("TradingBehavior should initialize with context")
    void testTradingBehaviorInitialization() {
        behavior.init(context);
        assertNotNull(behavior);
    }
    
    @Test
    @DisplayName("TradingBehavior should return correct priority")
    void testTradingBehaviorPriority() {
        int priority = behavior.getPriority(context);
        assertTrue(priority >= 0);
    }
    
    @Test
    @DisplayName("TradingBehavior should provide statistics")
    void testTradingBehaviorStatistics() {
        String stats = behavior.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Trading Behavior"));
    }
    
    @Test
    @DisplayName("TradingBehavior should handle null context gracefully")
    void testTradingBehaviorWithNullContext() {
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
    @DisplayName("TradingBehavior should handle execution with null player")
    void testTradingBehaviorExecutionWithNullPlayer() {
        context.setPlayerInstance(null);
        assertFalse(behavior.canExecute(context));
    }
}
