package net.sf.l2j.botmanager.integration;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.core.BotState;
import net.sf.l2j.botmanager.behaviors.IBehavior;
import net.sf.l2j.botmanager.actions.IAction;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Менеджер состояния ботов.
 * 
 * Управляет состояниями ботов, переходами между состояниями
 * и синхронизацией с системами поведений и действий.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class BotStateManager {
    
    private static final Logger logger = Logger.getLogger(BotStateManager.class);
    
    /** Состояния ботов */
    private final Map<Integer, BotStateInfo> botStates;
    
    /** История переходов состояний */
    private final Map<Integer, List<StateTransition>> stateHistory;
    
    /** Счетчик переходов состояний */
    private final AtomicLong stateTransitions;
    
    /** Счетчик ошибок состояний */
    private final AtomicLong stateErrors;
    
    /**
     * Конструктор.
     */
    public BotStateManager() {
        this.botStates = new ConcurrentHashMap<>();
        this.stateHistory = new ConcurrentHashMap<>();
        this.stateTransitions = new AtomicLong(0);
        this.stateErrors = new AtomicLong(0);
    }
    
    /**
     * Инициализация состояния бота.
     * 
     * @param bot бот
     * @return true если инициализация успешна
     */
    public boolean initializeBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot initialize null bot");
            return false;
        }
        
        int botId = bot.getBotId();
        
        if (botStates.containsKey(botId)) {
            logger.warn("Bot " + botId + " already initialized");
            return false;
        }
        
        try {
            BotStateInfo stateInfo = new BotStateInfo(bot);
            botStates.put(botId, stateInfo);
            
            // Инициализируем историю состояний
            stateHistory.put(botId, new ArrayList<>());
            
            logger.info("Bot " + botId + " state initialized");
            return true;
            
        } catch (Exception e) {
            logger.error("Error initializing bot " + botId + " state", e);
            stateErrors.incrementAndGet();
            return false;
        }
    }
    
    /**
     * Очистка состояния бота.
     * 
     * @param bot бот
     * @return true если очистка успешна
     */
    public boolean cleanupBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            logger.warn("Cannot cleanup null bot");
            return false;
        }
        
        int botId = bot.getBotId();
        
        try {
            botStates.remove(botId);
            stateHistory.remove(botId);
            
            logger.info("Bot " + botId + " state cleaned up");
            return true;
            
        } catch (Exception e) {
            logger.error("Error cleaning up bot " + botId + " state", e);
            stateErrors.incrementAndGet();
            return false;
        }
    }
    
    /**
     * Обновление состояния бота.
     * 
     * @param bot бот
     * @param newState новое состояние
     * @param reason причина изменения состояния
     * @return true если обновление успешно
     */
    public boolean updateBotState(EnhancedFakePlayer bot, BotState newState, String reason) {
        if (bot == null || newState == null) {
            logger.warn("Cannot update state: bot or newState is null");
            return false;
        }
        
        int botId = bot.getBotId();
        BotStateInfo stateInfo = botStates.get(botId);
        
        if (stateInfo == null) {
            logger.warn("Bot " + botId + " not initialized");
            return false;
        }
        
        try {
            BotState oldState = stateInfo.getCurrentState();
            
            // Проверяем, можно ли перейти в новое состояние
            if (!canTransitionTo(oldState, newState)) {
                logger.warn("Invalid state transition for bot " + botId + ": " + oldState + " -> " + newState);
                return false;
            }
            
            // Обновляем состояние
            stateInfo.setCurrentState(newState);
            stateInfo.setLastStateChangeTime(System.currentTimeMillis());
            stateInfo.setStateReason(reason);
            
            // Записываем переход в историю
            StateTransition transition = new StateTransition(oldState, newState, reason, System.currentTimeMillis());
            stateHistory.get(botId).add(transition);
            
            // Ограничиваем размер истории
            List<StateTransition> history = stateHistory.get(botId);
            if (history.size() > 100) {
                history.remove(0); // Удаляем самый старый переход
            }
            
            stateTransitions.incrementAndGet();
            
            logger.debug("Bot " + botId + " state updated: " + oldState + " -> " + newState + " (" + reason + ")");
            return true;
            
        } catch (Exception e) {
            logger.error("Error updating bot " + botId + " state", e);
            stateErrors.incrementAndGet();
            return false;
        }
    }
    
    /**
     * Получение текущего состояния бота.
     * 
     * @param bot бот
     * @return текущее состояние или null если бот не найден
     */
    public BotState getBotState(EnhancedFakePlayer bot) {
        if (bot == null) {
            return null;
        }
        
        BotStateInfo stateInfo = botStates.get(bot.getBotId());
        return stateInfo != null ? stateInfo.getCurrentState() : null;
    }
    
    /**
     * Получение информации о состоянии бота.
     * 
     * @param bot бот
     * @return информация о состоянии или null если бот не найден
     */
    public BotStateInfo getBotStateInfo(EnhancedFakePlayer bot) {
        if (bot == null) {
            return null;
        }
        
        return botStates.get(bot.getBotId());
    }
    
    /**
     * Получение истории переходов состояний бота.
     * 
     * @param bot бот
     * @return история переходов или null если бот не найден
     */
    public List<StateTransition> getBotStateHistory(EnhancedFakePlayer bot) {
        if (bot == null) {
            return null;
        }
        
        List<StateTransition> history = stateHistory.get(bot.getBotId());
        return history != null ? new ArrayList<>(history) : null;
    }
    
    /**
     * Проверка, можно ли перейти в новое состояние.
     * 
     * @param fromState текущее состояние
     * @param toState новое состояние
     * @return true если переход возможен
     */
    private boolean canTransitionTo(BotState fromState, BotState toState) {
        if (fromState == null || toState == null) {
            return false;
        }
        
        // Некоторые переходы запрещены
        switch (fromState) {
            case DEAD:
                return toState == BotState.DISCONNECTED;
            case DISCONNECTED:
                return toState == BotState.IDLE;
            default:
                return true; // Остальные переходы разрешены
        }
    }
    
    /**
     * Обновление состояния на основе поведения.
     * 
     * @param bot бот
     * @param behavior поведение
     * @return true если обновление успешно
     */
    public boolean updateStateFromBehavior(EnhancedFakePlayer bot, IBehavior behavior) {
        if (bot == null || behavior == null) {
            return false;
        }
        
        BotState newState = mapBehaviorToState(behavior);
        if (newState != null) {
            return updateBotState(bot, newState, "Behavior: " + behavior.getType());
        }
        
        return false;
    }
    
    /**
     * Обновление состояния на основе действия.
     * 
     * @param bot бот
     * @param action действие
     * @return true если обновление успешно
     */
    public boolean updateStateFromAction(EnhancedFakePlayer bot, IAction action) {
        if (bot == null || action == null) {
            return false;
        }
        
        BotState newState = mapActionToState(action);
        if (newState != null) {
            return updateBotState(bot, newState, "Action: " + action.getType());
        }
        
        return false;
    }
    
    /**
     * Маппинг поведения на состояние.
     * 
     * @param behavior поведение
     * @return соответствующее состояние
     */
    private BotState mapBehaviorToState(IBehavior behavior) {
        if (behavior == null) {
            return BotState.IDLE;
        }
        
        switch (behavior.getType()) {
            case FARMING:
                return BotState.FARMING;
            case QUESTING:
                return BotState.FARMING; // Используем FARMING для квестов
            case PVP:
                return BotState.FIGHTING;
            case IDLE:
            default:
                return BotState.IDLE;
        }
    }
    
    /**
     * Маппинг действия на состояние.
     * 
     * @param action действие
     * @return соответствующее состояние
     */
    private BotState mapActionToState(IAction action) {
        if (action == null) {
            return BotState.IDLE;
        }
        
        switch (action.getType()) {
            case MOVE:
            case TURN:
                return BotState.MOVING;
            case ATTACK:
            case CAST_SKILL:
                return BotState.FIGHTING;
            case PICKUP:
            case USE_ITEM:
                return BotState.FARMING;
            case REST:
            case HEAL:
            case MEDITATE:
                return BotState.IDLE;
            default:
                return BotState.IDLE;
        }
    }
    
    /**
     * Получение статистики менеджера состояний.
     * 
     * @return статистика в виде строки
     */
    public String getStatistics() {
        long transitions = stateTransitions.get();
        long errors = stateErrors.get();
        int activeBots = botStates.size();
        
        return String.format("BotStateManager Stats: Active Bots=%d, Transitions=%d, Errors=%d",
                activeBots, transitions, errors);
    }
    
    /**
     * Получение статистики бота.
     * 
     * @param botId ID бота
     * @return статистика бота или null если бот не найден
     */
    public String getBotStatistics(int botId) {
        BotStateInfo stateInfo = botStates.get(botId);
        if (stateInfo == null) {
            return null;
        }
        
        List<StateTransition> history = stateHistory.get(botId);
        int historySize = history != null ? history.size() : 0;
        
        return String.format("Bot %d State: %s, Last Change=%d, History Size=%d",
                botId, stateInfo.getCurrentState(), stateInfo.getLastStateChangeTime(), historySize);
    }
    
    /**
     * Получение списка активных ботов.
     * 
     * @return список ID активных ботов
     */
    public List<Integer> getActiveBotIds() {
        return new ArrayList<>(botStates.keySet());
    }
    
    /**
     * Внутренний класс для информации о состоянии бота.
     */
    public static class BotStateInfo {
        private final EnhancedFakePlayer bot;
        private volatile BotState currentState;
        private volatile long lastStateChangeTime;
        private volatile String stateReason;
        
        public BotStateInfo(EnhancedFakePlayer bot) {
            this.bot = bot;
            this.currentState = BotState.IDLE;
            this.lastStateChangeTime = System.currentTimeMillis();
            this.stateReason = "Initialized";
        }
        
        public EnhancedFakePlayer getBot() {
            return bot;
        }
        
        public BotState getCurrentState() {
            return currentState;
        }
        
        public void setCurrentState(BotState currentState) {
            this.currentState = currentState;
        }
        
        public long getLastStateChangeTime() {
            return lastStateChangeTime;
        }
        
        public void setLastStateChangeTime(long lastStateChangeTime) {
            this.lastStateChangeTime = lastStateChangeTime;
        }
        
        public String getStateReason() {
            return stateReason;
        }
        
        public void setStateReason(String stateReason) {
            this.stateReason = stateReason;
        }
    }
    
    /**
     * Внутренний класс для перехода состояния.
     */
    public static class StateTransition {
        private final BotState fromState;
        private final BotState toState;
        private final String reason;
        private final long timestamp;
        
        public StateTransition(BotState fromState, BotState toState, String reason, long timestamp) {
            this.fromState = fromState;
            this.toState = toState;
            this.reason = reason;
            this.timestamp = timestamp;
        }
        
        public BotState getFromState() {
            return fromState;
        }
        
        public BotState getToState() {
            return toState;
        }
        
        public String getReason() {
            return reason;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("%s -> %s (%s) at %d", fromState, toState, reason, timestamp);
        }
    }
}
