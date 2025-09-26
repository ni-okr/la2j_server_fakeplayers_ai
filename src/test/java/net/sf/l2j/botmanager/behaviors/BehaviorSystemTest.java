package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для системы поведений ботов
 */
class BehaviorSystemTest {
    
    private BehaviorManager behaviorManager;
    
    @BeforeEach
    void setUp() {
        behaviorManager = BehaviorManager.getInstance();
    }
    
    @Test
    void testBehaviorManagerSingleton() {
        BehaviorManager instance1 = BehaviorManager.getInstance();
        BehaviorManager instance2 = BehaviorManager.getInstance();
        
        assertSame(instance1, instance2, "BehaviorManager should be singleton");
    }
    
    @Test
    void testRegisterBehavior() {
        // Создаем тестовое поведение
        IdleBehavior idleBehavior = new IdleBehavior();
        
        // Регистрируем поведение
        behaviorManager.registerBehavior(idleBehavior);
        
        // Проверяем, что поведение зарегистрировано
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.IDLE));
        assertEquals(idleBehavior, behaviorManager.getBehavior(BehaviorType.IDLE));
    }
    
    @Test
    void testBehaviorTypeEnum() {
        // Проверяем основные типы поведений
        assertEquals("Бездействие", BehaviorType.IDLE.getName());
        assertEquals("Фарм", BehaviorType.FARMING.getName());
        assertEquals("Квесты", BehaviorType.QUESTING.getName());
        assertEquals("PvP", BehaviorType.PVP.getName());
        
        // Проверяем приоритеты
        assertTrue(BehaviorType.PVP.getPriority() > BehaviorType.FARMING.getPriority());
        assertTrue(BehaviorType.FARMING.getPriority() > BehaviorType.IDLE.getPriority());
        
        // Проверяем методы проверки типов
        assertTrue(BehaviorType.PVP.isCombatBehavior());
        assertTrue(BehaviorType.FARMING.isPeacefulBehavior());
        assertTrue(BehaviorType.QUESTING.isPeacefulBehavior());
    }
    
    @Test
    void testBehaviorInterruption() {
        // Проверяем, что поведение с высоким приоритетом может прервать поведение с низким приоритетом
        assertTrue(BehaviorType.PVP.canInterrupt(BehaviorType.IDLE));
        assertTrue(BehaviorType.FARMING.canInterrupt(BehaviorType.IDLE));
        assertFalse(BehaviorType.IDLE.canInterrupt(BehaviorType.PVP));
    }
    
    @Test
    void testFarmingBehavior() {
        FarmingBehavior farmingBehavior = new FarmingBehavior();
        
        // Проверяем тип поведения
        assertEquals(BehaviorType.FARMING, farmingBehavior.getType());
        
        // Проверяем приоритет
        assertTrue(farmingBehavior.getPriority() > 0);
        
        // Проверяем, что поведение неактивно по умолчанию
        assertFalse(farmingBehavior.isActive());
    }
    
    @Test
    void testQuestingBehavior() {
        QuestingBehavior questingBehavior = new QuestingBehavior();
        
        // Проверяем тип поведения
        assertEquals(BehaviorType.QUESTING, questingBehavior.getType());
        
        // Проверяем, что поведение неактивно по умолчанию
        assertFalse(questingBehavior.isActive());
    }
    
    @Test
    void testPvPBehavior() {
        PvPBehavior pvpBehavior = new PvPBehavior();
        
        // Проверяем тип поведения
        assertEquals(BehaviorType.PVP, pvpBehavior.getType());
        
        // Проверяем, что поведение неактивно по умолчанию
        assertFalse(pvpBehavior.isActive());
    }
    
    @Test
    void testIdleBehavior() {
        IdleBehavior idleBehavior = new IdleBehavior();
        
        // Проверяем тип поведения
        assertEquals(BehaviorType.IDLE, idleBehavior.getType());
        
        // Проверяем, что поведение неактивно по умолчанию
        assertFalse(idleBehavior.isActive());
    }
    
    @Test
    void testBehaviorActivation() {
        IdleBehavior idleBehavior = new IdleBehavior();
        
        // Активируем поведение
        idleBehavior.activate();
        assertTrue(idleBehavior.isActive());
        
        // Деактивируем поведение
        idleBehavior.deactivate();
        assertFalse(idleBehavior.isActive());
    }
    
    @Test
    void testBehaviorStatistics() {
        IdleBehavior idleBehavior = new IdleBehavior();
        
        // Получаем статистику
        String stats = idleBehavior.getStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("Behavior: " + BehaviorType.IDLE.getName()), "Should contain behavior name");
    }
    
    @Test
    void testBehaviorManagerStatistics() {
        // Получаем статистику менеджера
        String stats = behaviorManager.getBehaviorStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("Behavior Manager Statistics"), "Should contain header");
        assertTrue(stats.contains("Registered behaviors"), "Should contain behavior count");
    }
    
    @Test
    void testBehaviorManagerWithMultipleBehaviors() {
        // Проверяем, что менеджер содержит стандартные поведения
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.IDLE));
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.FARMING));
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.QUESTING));
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.PVP));
        
        // Проверяем количество зарегистрированных поведений
        assertTrue(behaviorManager.getAllBehaviors().size() >= 4);
    }
    
    @Test
    void testBehaviorManagerUnregister() {
        // Создаем временное поведение
        IdleBehavior tempBehavior = new IdleBehavior();
        behaviorManager.registerBehavior(tempBehavior);
        
        // Проверяем, что оно зарегистрировано
        assertTrue(behaviorManager.isBehaviorRegistered(BehaviorType.IDLE));
        
        // Отменяем регистрацию
        behaviorManager.unregisterBehavior(BehaviorType.IDLE);
        
        // Проверяем, что оно больше не зарегистрировано
        assertFalse(behaviorManager.isBehaviorRegistered(BehaviorType.IDLE));
    }
    
    @Test
    void testBehaviorManagerActiveBotCount() {
        // Проверяем, что изначально нет активных ботов
        assertEquals(0, behaviorManager.getActiveBotCount());
        
        // Проверяем, что список активных ботов пуст
        assertTrue(behaviorManager.getActiveBotIds().isEmpty());
    }
}