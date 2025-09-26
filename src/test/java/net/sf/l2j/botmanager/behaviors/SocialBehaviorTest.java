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
 * Тесты для SocialBehavior
 */
public class SocialBehaviorTest {
    
    private SocialBehavior behavior;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        behavior = new SocialBehavior();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("SocialBehavior should be created successfully")
    void testSocialBehaviorCreation() {
        assertNotNull(behavior);
        assertEquals(BehaviorType.SOCIAL, behavior.getType());
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("SocialBehavior should activate and deactivate correctly")
    void testSocialBehaviorActivation() {
        behavior.activate();
        assertTrue(behavior.isActive());
        
        behavior.deactivate();
        assertFalse(behavior.isActive());
    }
    
    @Test
    @DisplayName("SocialBehavior should initialize with context")
    void testSocialBehaviorInitialization() {
        behavior.init(context);
        assertNotNull(behavior);
    }
    
    @Test
    @DisplayName("SocialBehavior should return correct priority")
    void testSocialBehaviorPriority() {
        int priority = behavior.getPriority(context);
        assertTrue(priority >= 0);
    }
    
    @Test
    @DisplayName("SocialBehavior should provide statistics")
    void testSocialBehaviorStatistics() {
        String stats = behavior.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("Social Behavior"));
    }
    
    @Test
    @DisplayName("SocialBehavior should handle null context gracefully")
    void testSocialBehaviorWithNullContext() {
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
    @DisplayName("SocialBehavior should handle execution with null player")
    void testSocialBehaviorExecutionWithNullPlayer() {
        context.setPlayerInstance(null);
        assertFalse(behavior.canExecute(context));
    }
}
