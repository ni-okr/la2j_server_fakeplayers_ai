package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Поведение социального взаимодействия
 * 
 * Бот взаимодействует с другими игроками, NPC, участвует в групповых активностях,
 * общается в чате и выполняет социальные функции.
 */
public class SocialBehavior extends AbstractBehavior {
    
    private static final Logger _log = Logger.getLogger(SocialBehavior.class);
    
    // Константы для социального поведения
    private static final int INTERACTION_RADIUS = 1000; // Радиус взаимодействия
    private static final int CHAT_RADIUS = 500; // Радиус чата
    private static final int GROUP_RADIUS = 2000; // Радиус группы
    private static final int MIN_LEVEL_DIFF = 5; // Минимальная разница в уровне для взаимодействия
    private static final int MAX_LEVEL_DIFF = 20; // Максимальная разница в уровне для взаимодействия
    
    // Состояния социального поведения
    private enum SocialState {
        IDLE,               // Простой
        CHATTING,           // Общение в чате
        GROUPING,           // Поиск группы
        TRADING,            // Торговля
        HELPING,            // Помощь другим
        GREETING,           // Приветствие
        INTERACTING_NPC,    // Взаимодействие с NPC
        PARTICIPATING_EVENT // Участие в событии
    }
    
    // Типы социального взаимодействия
    private enum InteractionType {
        FRIENDLY,           // Дружелюбное
        NEUTRAL,            // Нейтральное
        HELPFUL,            // Помогающее
        TRADING,            // Торговое
        GROUP_ACTIVITY      // Групповая активность
    }
    
    public SocialBehavior() {
        super(BehaviorType.SOCIAL);
        setMinExecutionInterval(2000); // Минимум 2 секунды между действиями
        setMaxExecutionInterval(8000); // Максимум 8 секунд
    }
    
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        L2PcInstance player = context.getPlayerInstance();
        
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Получаем текущее состояние социального поведения
        SocialState currentState = getSocialState(context);
        
        _log.debug("Social state: " + currentState + " for bot " + bot.getContext().getBotId());
        
        switch (currentState) {
            case IDLE:
                return handleIdle(bot, context, player);
                
            case CHATTING:
                return handleChatting(bot, context, player);
                
            case GROUPING:
                return handleGrouping(bot, context, player);
                
            case TRADING:
                return handleTrading(bot, context, player);
                
            case HELPING:
                return handleHelping(bot, context, player);
                
            case GREETING:
                return handleGreeting(bot, context, player);
                
            case INTERACTING_NPC:
                return handleNpcInteraction(bot, context, player);
                
            case PARTICIPATING_EVENT:
                return handleEventParticipation(bot, context, player);
                
            default:
                setSocialState(context, SocialState.IDLE);
                return true;
        }
    }
    
    /**
     * Обработка простого состояния
     */
    private boolean handleIdle(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling idle state...");
        
        // Ищем возможности для социального взаимодействия
        L2PcInstance nearbyPlayer = findNearbyPlayer(player);
        if (nearbyPlayer != null) {
            // Найден игрок поблизости
            if (shouldGreet(nearbyPlayer, player)) {
                setSocialState(context, SocialState.GREETING);
                context.setData("targetPlayerId", nearbyPlayer.getObjectId());
                return true;
            }
        }
        
        // Ищем NPC для взаимодействия
        L2NpcInstance nearbyNpc = findNearbyNpc(player);
        if (nearbyNpc != null) {
            setSocialState(context, SocialState.INTERACTING_NPC);
            context.setData("targetNpcId", nearbyNpc.getObjectId());
            return true;
        }
        
        // Ищем возможности для групповой активности
        if (shouldLookForGroup(player)) {
            setSocialState(context, SocialState.GROUPING);
            return true;
        }
        
        // Случайное общение в чате
        if (ThreadLocalRandom.current().nextInt(100) < 10) { // 10% шанс
            setSocialState(context, SocialState.CHATTING);
            return true;
        }
        
        return true;
    }
    
    /**
     * Обработка общения в чате
     */
    private boolean handleChatting(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling chatting...");
        
        // Получаем случайное сообщение для чата
        String message = getRandomChatMessage();
        if (message != null) {
            // TODO: Implement say method in EnhancedFakePlayer
            _log.info("Bot says: " + message);
            _log.info("Bot said: " + message);
        }
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Обработка поиска группы
     */
    private boolean handleGrouping(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling grouping...");
        
        // Ищем игроков для группы
        List<L2PcInstance> potentialGroupMembers = findPotentialGroupMembers(player);
        
        if (!potentialGroupMembers.isEmpty()) {
            L2PcInstance targetPlayer = potentialGroupMembers.get(0);
            
            // Предлагаем группу
            // TODO: Implement say method in EnhancedFakePlayer
            _log.info("Bot says: Hey! Want to group up?");
            context.setData("groupTargetId", targetPlayer.getObjectId());
            
            _log.info("Offered group to player " + targetPlayer.getName());
        }
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Обработка торговли
     */
    private boolean handleTrading(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling trading...");
        
        Integer targetPlayerId = (Integer) context.getData("tradeTargetId");
        if (targetPlayerId == null) {
            // Ищем игрока для торговли
            L2PcInstance tradeTarget = findTradeTarget(player);
            if (tradeTarget != null) {
                context.setData("tradeTargetId", tradeTarget.getObjectId());
                // TODO: Implement say method in EnhancedFakePlayer
                _log.info("Bot says: Want to trade?");
                _log.info("Offered trade to player " + tradeTarget.getName());
            }
        }
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Обработка помощи другим
     */
    private boolean handleHelping(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling helping...");
        
        // Ищем игроков, которым нужна помощь
        L2PcInstance playerInNeed = findPlayerInNeed(player);
        if (playerInNeed != null) {
            // Предлагаем помощь
            // TODO: Implement say method in EnhancedFakePlayer
            _log.info("Bot says: Need help?");
            context.setData("helpTargetId", playerInNeed.getObjectId());
            
            _log.info("Offered help to player " + playerInNeed.getName());
        }
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Обработка приветствия
     */
    private boolean handleGreeting(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling greeting...");
        
        Integer targetPlayerId = (Integer) context.getData("targetPlayerId");
        if (targetPlayerId != null) {
            // Получаем случайное приветствие
            String greeting = getRandomGreeting();
            // TODO: Implement say method in EnhancedFakePlayer
            _log.info("Bot says: " + greeting);
            
            _log.info("Greeted player " + targetPlayerId);
        }
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Обработка взаимодействия с NPC
     */
    private boolean handleNpcInteraction(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling NPC interaction...");
        
        Integer targetNpcId = (Integer) context.getData("targetNpcId");
        if (targetNpcId != null) {
            // Взаимодействуем с NPC
            // TODO: Implement interactWithNpc method in EnhancedFakePlayer
            _log.info("Bot interacts with NPC " + targetNpcId);
            
            _log.info("Interacted with NPC " + targetNpcId);
        }
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Обработка участия в событии
     */
    private boolean handleEventParticipation(EnhancedFakePlayer bot, BotContext context, L2PcInstance player) {
        _log.debug("Handling event participation...");
        
        // В реальной реализации здесь будет участие в игровых событиях
        // TODO: Implement say method in EnhancedFakePlayer
        _log.info("Bot says: Let's participate in this event!");
        
        // Возвращаемся к простому состоянию
        setSocialState(context, SocialState.IDLE);
        return true;
    }
    
    /**
     * Поиск игрока поблизости
     */
    private L2PcInstance findNearbyPlayer(L2PcInstance player) {
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, INTERACTION_RADIUS)) {
            if (obj instanceof L2PcInstance) {
                L2PcInstance otherPlayer = (L2PcInstance) obj;
                if (otherPlayer != player && !otherPlayer.isDead() && otherPlayer.isOnline() != 0) {
                    return otherPlayer;
                }
            }
        }
        return null;
    }
    
    /**
     * Поиск NPC поблизости
     */
    private L2NpcInstance findNearbyNpc(L2PcInstance player) {
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, INTERACTION_RADIUS)) {
            if (obj instanceof L2NpcInstance) {
                L2NpcInstance npc = (L2NpcInstance) obj;
                if (isInteractableNpc(npc)) {
                    return npc;
                }
            }
        }
        return null;
    }
    
    /**
     * Проверка, является ли NPC интерактивным
     */
    private boolean isInteractableNpc(L2NpcInstance npc) {
        // В реальной реализации здесь будет проверка типа NPC
        return npc.getLevel() <= 10; // Простые NPC
    }
    
    /**
     * Проверка, стоит ли приветствовать игрока
     */
    private boolean shouldGreet(L2PcInstance otherPlayer, L2PcInstance player) {
        int levelDiff = Math.abs(otherPlayer.getLevel() - player.getLevel());
        return levelDiff <= MAX_LEVEL_DIFF && levelDiff >= MIN_LEVEL_DIFF;
    }
    
    /**
     * Проверка, стоит ли искать группу
     */
    private boolean shouldLookForGroup(L2PcInstance player) {
        // В реальной реализации здесь будет проверка условий для группировки
        return ThreadLocalRandom.current().nextInt(100) < 15; // 15% шанс
    }
    
    /**
     * Поиск потенциальных членов группы
     */
    private List<L2PcInstance> findPotentialGroupMembers(L2PcInstance player) {
        List<L2PcInstance> members = new java.util.ArrayList<>();
        
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, GROUP_RADIUS)) {
            if (obj instanceof L2PcInstance) {
                L2PcInstance otherPlayer = (L2PcInstance) obj;
                if (otherPlayer != player && !otherPlayer.isDead() && otherPlayer.isOnline() != 0) {
                    int levelDiff = Math.abs(otherPlayer.getLevel() - player.getLevel());
                    if (levelDiff <= 10) { // Разница в уровне не более 10
                        members.add(otherPlayer);
                    }
                }
            }
        }
        
        return members;
    }
    
    /**
     * Поиск цели для торговли
     */
    private L2PcInstance findTradeTarget(L2PcInstance player) {
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, INTERACTION_RADIUS)) {
            if (obj instanceof L2PcInstance) {
                L2PcInstance otherPlayer = (L2PcInstance) obj;
                if (otherPlayer != player && !otherPlayer.isDead() && otherPlayer.isOnline() != 0) {
                    // В реальной реализации здесь будет проверка на готовность к торговле
                    return otherPlayer;
                }
            }
        }
        return null;
    }
    
    /**
     * Поиск игрока, которому нужна помощь
     */
    private L2PcInstance findPlayerInNeed(L2PcInstance player) {
        for (net.sf.l2j.gameserver.model.L2Object obj : L2World.getInstance().getVisibleObjects(player, INTERACTION_RADIUS)) {
            if (obj instanceof L2PcInstance) {
                L2PcInstance otherPlayer = (L2PcInstance) obj;
                if (otherPlayer != player && !otherPlayer.isDead() && otherPlayer.isOnline() != 0) {
                    // Проверяем, нужна ли помощь (низкий HP, бой и т.д.)
                    double hpPercent = (otherPlayer.getCurrentHp() / otherPlayer.getMaxHp()) * 100;
                    if (hpPercent < 50) { // HP меньше 50%
                        return otherPlayer;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Получение случайного сообщения для чата
     */
    private String getRandomChatMessage() {
        String[] messages = {
            "Hello everyone!",
            "How's everyone doing?",
            "Anyone want to group up?",
            "Nice weather today!",
            "Good luck with your quests!",
            "Anyone selling items?",
            "Looking for a party!",
            "Thanks for the help!",
            "See you later!",
            "Have a great day!"
        };
        
        return messages[ThreadLocalRandom.current().nextInt(messages.length)];
    }
    
    /**
     * Получение случайного приветствия
     */
    private String getRandomGreeting() {
        String[] greetings = {
            "Hello!",
            "Hi there!",
            "Hey!",
            "Good day!",
            "Nice to meet you!",
            "How are you?",
            "What's up?",
            "Greetings!",
            "Hello there!",
            "Hi!"
        };
        
        return greetings[ThreadLocalRandom.current().nextInt(greetings.length)];
    }
    
    /**
     * Получение текущего состояния социального поведения
     */
    private SocialState getSocialState(BotContext context) {
        Object state = context.getData("socialState");
        if (state instanceof SocialState) {
            return (SocialState) state;
        }
        return SocialState.IDLE;
    }
    
    /**
     * Установка состояния социального поведения
     */
    private void setSocialState(BotContext context, SocialState state) {
        context.setData("socialState", state);
    }
    
    public boolean canExecute(BotContext context) {
        L2PcInstance player = context.getPlayerInstance();
        if (player == null || player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Социальное поведение доступно всегда, если игрок жив и онлайн
        return true;
    }
    
    public int getPriority(BotContext context) {
        // Приоритет социального поведения зависит от уровня и настроения
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return 0;
        }
        
        int level = player.getLevel();
        int basePriority = 20; // Базовый приоритет
        
        // Увеличиваем приоритет с уровнем
        if (level >= 20) basePriority += 5;
        if (level >= 40) basePriority += 5;
        if (level >= 60) basePriority += 5;
        
        // Проверяем, есть ли игроки поблизости
        if (findNearbyPlayer(player) != null) {
            basePriority += 10; // Увеличиваем приоритет при наличии игроков
        }
        
        return basePriority;
    }
    
    @Override
    public String getStatistics() {
        return "Social Behavior - Active: " + isActive() + 
               ", Min Interval: " + minExecutionInterval + 
               ", Max Interval: " + maxExecutionInterval;
    }
}
