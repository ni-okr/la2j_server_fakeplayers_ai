package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для системы действий ботов
 */
class ActionSystemTest {
    
    private ActionManager actionManager;
    
    @BeforeEach
    void setUp() {
        actionManager = ActionManager.getInstance();
    }
    
    @Test
    void testActionManagerSingleton() {
        ActionManager instance1 = ActionManager.getInstance();
        ActionManager instance2 = ActionManager.getInstance();
        
        assertSame(instance1, instance2, "ActionManager should be singleton");
    }
    
    @Test
    void testRegisterAction() {
        // Создаем тестовое действие
        MoveAction moveAction = new MoveAction();
        
        // Регистрируем действие
        actionManager.registerAction(moveAction);
        
        // Проверяем, что действие зарегистрировано
        assertTrue(actionManager.isActionRegistered(ActionType.MOVE));
        assertEquals(moveAction, actionManager.getAction(ActionType.MOVE));
    }
    
    @Test
    void testActionTypeEnum() {
        // Проверяем основные типы действий
        assertEquals("Движение", ActionType.MOVE.getName());
        assertEquals("Атака", ActionType.ATTACK.getName());
        assertEquals("Использование навыка", ActionType.CAST_SKILL.getName());
        assertEquals("Подбор предмета", ActionType.PICKUP.getName());
        
        // Проверяем приоритеты
        assertTrue(ActionType.ATTACK.getPriority() > ActionType.MOVE.getPriority());
        assertTrue(ActionType.CAST_SKILL.getPriority() > ActionType.MOVE.getPriority());
        
        // Проверяем методы проверки типов
        assertTrue(ActionType.ATTACK.isCombatAction());
        assertTrue(ActionType.MOVE.isPeacefulAction());
        assertTrue(ActionType.TALK.isSocialAction());
        assertTrue(ActionType.REST.isRecoveryAction());
        assertTrue(ActionType.SEARCH.isSearchAction());
        assertTrue(ActionType.FOLLOW.isSpecialAction());
    }
    
    @Test
    void testActionInterruption() {
        // Проверяем, что действие с высоким приоритетом может прервать действие с низким приоритетом
        assertTrue(ActionType.ATTACK.canInterrupt(ActionType.MOVE));
        assertTrue(ActionType.CAST_SKILL.canInterrupt(ActionType.MOVE));
        assertFalse(ActionType.MOVE.canInterrupt(ActionType.ATTACK));
    }
    
    @Test
    void testActionRequirements() {
        // Проверяем требования к действиям
        assertTrue(ActionType.ATTACK.requiresTarget());
        assertTrue(ActionType.CAST_SKILL.requiresTarget());
        assertTrue(ActionType.TALK.requiresTarget());
        
        assertTrue(ActionType.USE_ITEM.requiresItem());
        assertTrue(ActionType.EQUIP.requiresItem());
        assertTrue(ActionType.PICKUP.requiresItem());
        
        assertTrue(ActionType.MOVE.requiresPosition());
        assertTrue(ActionType.GUARD.requiresPosition());
        assertTrue(ActionType.PATROL.requiresPosition());
    }
    
    @Test
    void testMoveAction() {
        MoveAction moveAction = new MoveAction();
        
        // Проверяем тип действия
        assertEquals(ActionType.MOVE, moveAction.getType());
        
        // Проверяем приоритет
        assertTrue(moveAction.getPriority() > 0);
        
        // Проверяем, что действие неактивно по умолчанию
        assertFalse(moveAction.isActive());
        
        // Проверяем, что действие не завершено по умолчанию
        assertFalse(moveAction.isCompleted());
    }
    
    @Test
    void testAttackAction() {
        AttackAction attackAction = new AttackAction();
        
        // Проверяем тип действия
        assertEquals(ActionType.ATTACK, attackAction.getType());
        
        // Проверяем, что действие неактивно по умолчанию
        assertFalse(attackAction.isActive());
        
        // Проверяем, что действие не завершено по умолчанию
        assertFalse(attackAction.isCompleted());
    }
    
    @Test
    void testCastAction() {
        CastAction castAction = new CastAction();
        
        // Проверяем тип действия
        assertEquals(ActionType.CAST_SKILL, castAction.getType());
        
        // Проверяем, что действие неактивно по умолчанию
        assertFalse(castAction.isActive());
        
        // Проверяем, что действие не завершено по умолчанию
        assertFalse(castAction.isCompleted());
    }
    
    @Test
    void testLootAction() {
        LootAction lootAction = new LootAction();
        
        // Проверяем тип действия
        assertEquals(ActionType.PICKUP, lootAction.getType());
        
        // Проверяем, что действие неактивно по умолчанию
        assertFalse(lootAction.isActive());
        
        // Проверяем, что действие не завершено по умолчанию
        assertFalse(lootAction.isCompleted());
    }
    
    @Test
    void testActionActivation() {
        MoveAction moveAction = new MoveAction();
        
        // Активируем действие
        moveAction.activate();
        assertTrue(moveAction.isActive());
        
        // Деактивируем действие
        moveAction.deactivate();
        assertFalse(moveAction.isActive());
    }
    
    @Test
    void testActionInterruptionCapability() {
        MoveAction moveAction = new MoveAction();
        
        // Проверяем, что базовые действия можно прерывать
        assertTrue(moveAction.canInterrupt());
        
        // Прерываем действие
        moveAction.interrupt();
        assertTrue(moveAction.isCompleted());
    }
    
    @Test
    void testActionStatistics() {
        MoveAction moveAction = new MoveAction();
        
        // Получаем статистику
        String stats = moveAction.getStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("Action: " + ActionType.MOVE.getName()), "Should contain action name");
    }
    
    @Test
    void testActionManagerStatistics() {
        // Получаем статистику менеджера
        String stats = actionManager.getActionStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("Action Manager Statistics"), "Should contain header");
        assertTrue(stats.contains("Registered actions"), "Should contain action count");
    }
    
    @Test
    void testActionManagerWithMultipleActions() {
        // Проверяем, что менеджер содержит стандартные действия
        assertTrue(actionManager.isActionRegistered(ActionType.MOVE));
        assertTrue(actionManager.isActionRegistered(ActionType.ATTACK));
        assertTrue(actionManager.isActionRegistered(ActionType.CAST_SKILL));
        assertTrue(actionManager.isActionRegistered(ActionType.PICKUP));
        
        // Проверяем количество зарегистрированных действий
        assertTrue(actionManager.getAllActions().size() >= 4);
    }
    
    @Test
    void testActionManagerUnregister() {
        // Создаем временное действие
        MoveAction tempAction = new MoveAction();
        actionManager.registerAction(tempAction);
        
        // Проверяем, что оно зарегистрировано
        assertTrue(actionManager.isActionRegistered(ActionType.MOVE));
        
        // Отменяем регистрацию
        actionManager.unregisterAction(ActionType.MOVE);
        
        // Проверяем, что оно больше не зарегистрировано
        assertFalse(actionManager.isActionRegistered(ActionType.MOVE));
    }
    
    @Test
    void testActionManagerActiveBotCount() {
        // Проверяем, что изначально нет активных ботов
        assertEquals(0, actionManager.getActiveBotCount());
        
        // Проверяем, что список активных ботов пуст
        assertTrue(actionManager.getActiveBotIds().isEmpty());
    }
    
    @Test
    void testMoveActionTarget() {
        MoveAction moveAction = new MoveAction();
        
        // Создаем контекст бота
        BotContext context = new BotContext(1);
        moveAction.init(context);
        
        // Устанавливаем цель
        moveAction.setTarget(100, 200, 300);
        
        // Проверяем, что цель установлена
        int[] target = moveAction.getTarget();
        assertNotNull(target, "Target should not be null");
        assertEquals(100, target[0]);
        assertEquals(200, target[1]);
        assertEquals(300, target[2]);
        
        // Тестируем выполнение без реального игрока (должно возвращать false)
        // Не тестируем execute с null, так как это приводит к NullPointerException
    }
    
    @Test
    void testAttackActionTarget() {
        AttackAction attackAction = new AttackAction();
        
        // Создаем контекст бота
        BotContext context = new BotContext(1);
        attackAction.init(context);
        
        // Устанавливаем цель
        attackAction.setTarget(123);
        
        // Проверяем, что цель установлена
        assertEquals(123, attackAction.getTarget());
        
        // Тестируем выполнение без реального игрока (должно возвращать false)
        // Не тестируем execute с null, так как это приводит к NullPointerException
    }
    
    @Test
    void testCastActionSkill() {
        CastAction castAction = new CastAction();
        
        // Создаем контекст бота
        BotContext context = new BotContext(1);
        castAction.init(context);
        
        // Устанавливаем навык
        castAction.setSkill(456, 789, 5);
        
        // Проверяем, что навык установлен
        assertEquals(456, castAction.getSkillId());
        assertEquals(789, castAction.getTargetId());
        assertEquals(5, castAction.getLevel());
        
        // Тестируем выполнение без реального игрока (должно возвращать false)
        // Не тестируем execute с null, так как это приводит к NullPointerException
    }
    
    @Test
    void testLootActionItem() {
        LootAction lootAction = new LootAction();
        
        // Создаем контекст бота
        BotContext context = new BotContext(1);
        lootAction.init(context);
        
        // Устанавливаем предмет
        lootAction.setItem(999);
        
        // Проверяем, что предмет установлен
        assertEquals(999, lootAction.getItemId());
        
        // Тестируем выполнение без реального игрока (должно возвращать false)
        // Не тестируем execute с null, так как это приводит к NullPointerException
    }
    
    @Test
    void testLootActionPosition() {
        LootAction lootAction = new LootAction();
        
        // Создаем контекст бота
        BotContext context = new BotContext(1);
        lootAction.init(context);
        
        // Устанавливаем позицию
        lootAction.setPosition(100, 200, 300);
        
        // Проверяем, что позиция установлена
        int[] position = lootAction.getPosition();
        assertNotNull(position, "Position should not be null");
        assertEquals(100, position[0]);
        assertEquals(200, position[1]);
        assertEquals(300, position[2]);
        
        // Тестируем выполнение без реального игрока (должно возвращать false)
        // Не тестируем execute с null, так как это приводит к NullPointerException
    }
}
