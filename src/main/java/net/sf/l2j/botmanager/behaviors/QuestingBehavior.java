package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Поведение выполнения квестов
 * 
 * Бот ищет NPC для квестов, принимает квесты, выполняет задачи и сдает квесты.
 */
public class QuestingBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(QuestingBehavior.class);
    
    // Константы для квестов
    private static final int SEARCH_RADIUS = 2000; // Радиус поиска NPC
    private static final int INTERACTION_RADIUS = 100; // Радиус взаимодействия с NPC
    private static final int MAX_QUESTS = 5; // Максимальное количество активных квестов
    
    // Состояния квестов
    private enum QuestingState {
        SEARCHING_NPC,    // Поиск NPC для квестов
        INTERACTING,      // Взаимодействие с NPC
        MOVING_TO_TARGET, // Движение к цели квеста
        KILLING_MOBS,     // Убийство мобов для квеста
        COLLECTING_ITEMS, // Сбор предметов для квеста
        RETURNING_NPC,    // Возвращение к NPC
        COMPLETING_QUEST  // Завершение квеста
    }
    
    public QuestingBehavior() {
        super(BehaviorType.QUESTING);
        setMinExecutionInterval(1000); // Минимум 1 секунда между действиями
        setMaxExecutionInterval(5000); // Максимум 5 секунд
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null) {
            return false;
        }
        
        // Получаем текущее состояние квестов
        QuestingState currentState = getBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
        
        switch (currentState) {
            case SEARCHING_NPC:
                return handleSearchingNpc(bot, player);
            case INTERACTING:
                return handleInteracting(bot, player);
            case MOVING_TO_TARGET:
                return handleMovingToTarget(bot, player);
            case KILLING_MOBS:
                return handleKillingMobs(bot, player);
            case COLLECTING_ITEMS:
                return handleCollectingItems(bot, player);
            case RETURNING_NPC:
                return handleReturningNpc(bot, player);
            case COMPLETING_QUEST:
                return handleCompletingQuest(bot, player);
            default:
                setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
                return false;
        }
    }
    
    /**
     * Обработка поиска NPC для квестов
     */
    private boolean handleSearchingNpc(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, есть ли активные квесты
        if (hasActiveQuests(player)) {
            setBehaviorData("questing_state", QuestingState.MOVING_TO_TARGET);
            return false;
        }
        
        // Ищем NPC для квестов
        L2NpcInstance questNpc = findQuestNpc(player);
        
        if (questNpc != null) {
            setBehaviorData("questing_npc", questNpc);
            setBehaviorData("questing_state", QuestingState.INTERACTING);
            context.setState(BotState.MOVING);
            _log.debug("Bot " + context.getBotId() + " found quest NPC: " + questNpc.getName());
        } else {
            // Если NPC нет, ждем
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return false;
    }
    
    /**
     * Обработка взаимодействия с NPC
     */
    private boolean handleInteracting(EnhancedFakePlayer bot, L2PcInstance player) {
        L2NpcInstance npc = getBehaviorData("questing_npc", null);
        
        if (npc == null || npc.isDead()) {
            setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
            return false;
        }
        
        double distance = player.getDistance(npc.getX(), npc.getY(), npc.getZ());
        
        if (distance > INTERACTION_RADIUS) {
            // Слишком далеко, двигаемся к NPC
            setBehaviorData("questing_state", QuestingState.MOVING_TO_TARGET);
            return false;
        }
        
        // Взаимодействуем с NPC
        // Здесь должна быть логика принятия квестов
        context.setState(BotState.IDLE);
        
        // Имитируем принятие квеста
        if (ThreadLocalRandom.current().nextBoolean()) {
            setBehaviorData("questing_state", QuestingState.MOVING_TO_TARGET);
            _log.debug("Bot " + context.getBotId() + " accepted quest from " + npc.getName());
        } else {
            setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
            _log.debug("Bot " + context.getBotId() + " no quests available from " + npc.getName());
        }
        
        return false;
    }
    
    /**
     * Обработка движения к цели квеста
     */
    private boolean handleMovingToTarget(EnhancedFakePlayer bot, L2PcInstance player) {
        // Получаем цель квеста
        Object questTarget = getBehaviorData("questing_target", null);
        
        if (questTarget == null) {
            // Нет цели, ищем новую
            setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
            return false;
        }
        
        // Определяем тип цели и переходим к соответствующему состоянию
        if (questTarget instanceof L2Character) {
            setBehaviorData("questing_state", QuestingState.KILLING_MOBS);
        } else {
            setBehaviorData("questing_state", QuestingState.COLLECTING_ITEMS);
        }
        
        context.setState(BotState.MOVING);
        return false;
    }
    
    /**
     * Обработка убийства мобов для квеста
     */
    private boolean handleKillingMobs(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, завершен ли квест
        if (isQuestCompleted(player)) {
            setBehaviorData("questing_state", QuestingState.RETURNING_NPC);
            return false;
        }
        
        // Ищем мобов для квеста
        L2Character target = findQuestMob(player);
        
        if (target != null) {
            // Атакуем моба
            context.setState(BotState.FIGHTING);
            // Здесь должна быть логика атаки
        } else {
            // Мобов нет, ждем
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return false;
    }
    
    /**
     * Обработка сбора предметов для квеста
     */
    private boolean handleCollectingItems(EnhancedFakePlayer bot, L2PcInstance player) {
        // Проверяем, завершен ли квест
        if (isQuestCompleted(player)) {
            setBehaviorData("questing_state", QuestingState.RETURNING_NPC);
            return false;
        }
        
        // Ищем предметы для квеста
        // Здесь должна быть логика поиска и сбора предметов
        context.setState(BotState.FARMING);
        
        // Имитируем сбор предметов
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
    
    /**
     * Обработка возвращения к NPC
     */
    private boolean handleReturningNpc(EnhancedFakePlayer bot, L2PcInstance player) {
        L2NpcInstance npc = getBehaviorData("questing_npc", null);
        
        if (npc == null || npc.isDead()) {
            setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
            return false;
        }
        
        double distance = player.getDistance(npc.getX(), npc.getY(), npc.getZ());
        
        if (distance > INTERACTION_RADIUS) {
            // Двигаемся к NPC
            context.setState(BotState.MOVING);
            // Здесь должна быть логика движения
        } else {
            // Достаточно близко, завершаем квест
            setBehaviorData("questing_state", QuestingState.COMPLETING_QUEST);
        }
        
        return false;
    }
    
    /**
     * Обработка завершения квеста
     */
    private boolean handleCompletingQuest(EnhancedFakePlayer bot, L2PcInstance player) {
        L2NpcInstance npc = getBehaviorData("questing_npc", null);
        
        if (npc == null) {
            setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
            return false;
        }
        
        // Завершаем квест
        // Здесь должна быть логика завершения квеста
        context.setState(BotState.IDLE);
        
        // Очищаем данные квеста
        setBehaviorData("questing_npc", null);
        setBehaviorData("questing_target", null);
        setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
        
        _log.debug("Bot " + context.getBotId() + " completed quest");
        
        return false;
    }
    
    /**
     * Ищет NPC для квестов
     */
    private L2NpcInstance findQuestNpc(L2PcInstance player) {
        javolution.util.FastList<net.sf.l2j.gameserver.model.L2Object> objects = L2World.getInstance().getVisibleObjects(player, SEARCH_RADIUS);
        
        for (net.sf.l2j.gameserver.model.L2Object obj : objects) {
            if (obj instanceof L2NpcInstance) {
                L2NpcInstance npc = (L2NpcInstance) obj;
                
                if (npc.isDead()) {
                    continue;
                }
                
                // Проверяем, есть ли у NPC квесты
                if (hasQuestsForPlayer(npc, player)) {
                    return npc;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Ищет моба для квеста
     */
    private L2Character findQuestMob(L2PcInstance player) {
        // Здесь должна быть логика поиска мобов для квеста
        // Пока что возвращаем null
        return null;
    }
    
    /**
     * Проверяет, есть ли у NPC квесты для игрока
     */
    private boolean hasQuestsForPlayer(L2NpcInstance npc, L2PcInstance player) {
        // Здесь должна быть логика проверки квестов
        // Пока что возвращаем случайное значение
        return ThreadLocalRandom.current().nextBoolean();
    }
    
    /**
     * Проверяет, есть ли активные квесты
     */
    private boolean hasActiveQuests(L2PcInstance player) {
        // Здесь должна быть логика проверки активных квестов
        // Пока что возвращаем случайное значение
        return ThreadLocalRandom.current().nextBoolean();
    }
    
    /**
     * Проверяет, завершен ли квест
     */
    private boolean isQuestCompleted(L2PcInstance player) {
        // Здесь должна быть логика проверки завершения квеста
        // Пока что возвращаем случайное значение
        return ThreadLocalRandom.current().nextBoolean();
    }
    
    @Override
    public boolean canExecute(BotContext context) {
        if (!super.canExecute(context)) {
            return false;
        }
        
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return false;
        }
        
        // Проверяем уровень игрока (квесты доступны с определенного уровня)
        if (player.getLevel() < 10) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void init(BotContext context) {
        super.init(context);
        setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
        setBehaviorData("questing_npc", null);
        setBehaviorData("questing_target", null);
        _log.info("Initialized questing behavior for bot " + context.getBotId());
    }
    
    @Override
    public void onEnd(BotContext context) {
        super.onEnd(context);
        setBehaviorData("questing_npc", null);
        setBehaviorData("questing_target", null);
        setBehaviorData("questing_state", QuestingState.SEARCHING_NPC);
        _log.info("Ended questing behavior for bot " + context.getBotId());
    }
}
