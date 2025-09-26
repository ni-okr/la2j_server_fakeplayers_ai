package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Дополнительные тесты для BehaviorManager с новыми поведениями
 */
public class BehaviorManagerAdditionalTest {
    
    private BehaviorManager behaviorManager;
    private BotContext context;
    private EnhancedFakePlayer bot;
    
    @BeforeEach
    void setUp() {
        behaviorManager = BehaviorManager.getInstance();
        context = new BotContext(1);
        bot = new EnhancedFakePlayer(context, null);
    }
    
    @Test
    @DisplayName("BehaviorManager should register new behaviors")
    void testNewBehaviorsRegistration() {
        // Проверяем, что новые поведения зарегистрированы
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.CRAFTING));
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.TRADING));
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.PATROLLING));
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.SOCIAL));
    }
    
    @Test
    @DisplayName("BehaviorManager should set CraftingBehavior")
    void testSetCraftingBehavior() {
        boolean result = behaviorManager.setBehavior(bot, BehaviorType.CRAFTING);
        assertTrue(result);
        
        IBehavior currentBehavior = behaviorManager.getCurrentBehavior(bot);
        assertNotNull(currentBehavior);
        assertEquals(BehaviorType.CRAFTING, currentBehavior.getType());
    }
    
    @Test
    @DisplayName("BehaviorManager should set TradingBehavior")
    void testSetTradingBehavior() {
        boolean result = behaviorManager.setBehavior(bot, BehaviorType.TRADING);
        assertTrue(result);
        
        IBehavior currentBehavior = behaviorManager.getCurrentBehavior(bot);
        assertNotNull(currentBehavior);
        assertEquals(BehaviorType.TRADING, currentBehavior.getType());
    }
    
    @Test
    @DisplayName("BehaviorManager should set PatrollingBehavior")
    void testSetPatrollingBehavior() {
        boolean result = behaviorManager.setBehavior(bot, BehaviorType.PATROLLING);
        assertTrue(result);
        
        IBehavior currentBehavior = behaviorManager.getCurrentBehavior(bot);
        assertNotNull(currentBehavior);
        assertEquals(BehaviorType.PATROLLING, currentBehavior.getType());
    }
    
    @Test
    @DisplayName("BehaviorManager should set SocialBehavior")
    void testSetSocialBehavior() {
        boolean result = behaviorManager.setBehavior(bot, BehaviorType.SOCIAL);
        assertTrue(result);
        
        IBehavior currentBehavior = behaviorManager.getCurrentBehavior(bot);
        assertNotNull(currentBehavior);
        assertEquals(BehaviorType.SOCIAL, currentBehavior.getType());
    }
    
    @Test
    @DisplayName("BehaviorManager should switch between new behaviors")
    void testSwitchBetweenNewBehaviors() {
        // Устанавливаем начальное поведение
        behaviorManager.setBehavior(bot, BehaviorType.CRAFTING);
        
        // Переключаем на следующее поведение
        boolean switched = behaviorManager.switchToNextBehavior(bot);
        assertTrue(switched);
        
        IBehavior currentBehavior = behaviorManager.getCurrentBehavior(bot);
        assertNotNull(currentBehavior);
        assertNotEquals(BehaviorType.CRAFTING, currentBehavior.getType());
    }
    
    @Test
    @DisplayName("BehaviorManager should provide statistics for new behaviors")
    void testBehaviorManagerStatistics() {
        // Устанавливаем несколько поведений
        boolean craftingSet = behaviorManager.setBehavior(bot, BehaviorType.CRAFTING);
        boolean tradingSet = behaviorManager.setBehavior(bot, BehaviorType.TRADING);
        boolean patrollingSet = behaviorManager.setBehavior(bot, BehaviorType.PATROLLING);
        boolean socialSet = behaviorManager.setBehavior(bot, BehaviorType.SOCIAL);
        
        // Проверяем, что хотя бы некоторые поведения были установлены
        assertTrue(craftingSet || tradingSet || patrollingSet || socialSet, 
                  "At least one behavior should be set successfully");
        
        String stats = behaviorManager.getBehaviorStatistics();
        assertNotNull(stats);
        // Проверяем, что статистика содержит информацию о поведениях
        assertTrue(stats.length() > 0, "Statistics should not be empty");
    }
    
    @Test
    @DisplayName("BehaviorManager should handle invalid behavior types")
    void testInvalidBehaviorTypes() {
        // Попытка установить несуществующий тип поведения
        boolean result = behaviorManager.setBehavior(bot, null);
        assertFalse(result);
    }
    
    @Test
    @DisplayName("BehaviorManager should stop current behavior")
    void testStopCurrentBehavior() {
        // Устанавливаем поведение
        behaviorManager.setBehavior(bot, BehaviorType.CRAFTING);
        assertNotNull(behaviorManager.getCurrentBehavior(bot));
        
        // Останавливаем поведение
        behaviorManager.stopCurrentBehavior(bot);
        assertNull(behaviorManager.getCurrentBehavior(bot));
    }
}
