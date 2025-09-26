package net.sf.l2j.botmanager.actions;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Менеджер действий ботов
 * 
 * Управляет регистрацией, выполнением и очередью действий ботов.
 * Реализует паттерн Singleton для глобального доступа.
 */
public class ActionManager implements IActionManager {
    
    private static final Logger _log = Logger.getLogger(ActionManager.class);
    
    private static ActionManager instance;
    
    // Регистрированные действия по типам
    private final Map<ActionType, IAction> actions = new ConcurrentHashMap<>();
    
    // Текущие действия ботов
    private final Map<Integer, IAction> botCurrentActions = new ConcurrentHashMap<>();
    
    // Очереди действий ботов
    private final Map<Integer, Queue<IAction>> botActionQueues = new ConcurrentHashMap<>();
    
    // Статистика использования действий
    private final Map<ActionType, AtomicLong> actionUsageStats = new ConcurrentHashMap<>();
    
    // Статистика выполнения действий
    private final Map<Integer, AtomicLong> botActionStats = new ConcurrentHashMap<>();
    
    private ActionManager() {
        initializeDefaultActions();
        _log.info("ActionManager initialized");
    }
    
    public static ActionManager getInstance() {
        if (instance == null) {
            synchronized (ActionManager.class) {
                if (instance == null) {
                    instance = new ActionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Инициализирует действия по умолчанию
     */
    private void initializeDefaultActions() {
        // Регистрируем стандартные действия
        registerAction(new MoveAction());
        registerAction(new AttackAction());
        registerAction(new CastAction());
        registerAction(new LootAction());
        
        _log.info("Default actions registered");
    }
    
    @Override
    public boolean executeAction(EnhancedFakePlayer bot, ActionType actionType, Object... parameters) {
        if (bot == null || actionType == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        if (context == null) {
            return false;
        }
        
        IAction actionTemplate = actions.get(actionType);
        if (actionTemplate == null) {
            _log.warn("Action type " + actionType + " not registered");
            return false;
        }
        
        // Останавливаем текущее действие, если оно может быть прервано
        IAction currentAction = botCurrentActions.get(context.getBotId());
        if (currentAction != null && currentAction.canInterrupt()) {
            currentAction.interrupt();
            botCurrentActions.remove(context.getBotId());
        }
        
        // Создаем новый экземпляр действия
        IAction actionInstance = createActionInstance(actionTemplate);
        actionInstance.init(context);
        
        // Устанавливаем параметры действия
        setActionParameters(actionInstance, parameters);
        
        // Выполняем действие
        boolean result = actionInstance.execute(bot);
        
        if (result) {
            // Действие завершено
            actionInstance.onEnd(context);
        } else {
            // Действие продолжается
            botCurrentActions.put(context.getBotId(), actionInstance);
        }
        
        // Обновляем статистику
        actionUsageStats.computeIfAbsent(actionType, k -> new AtomicLong(0)).incrementAndGet();
        botActionStats.computeIfAbsent(context.getBotId(), k -> new AtomicLong(0)).incrementAndGet();
        
        _log.debug("Executed action " + actionType.getName() + " for bot " + context.getBotId());
        
        return result;
    }
    
    @Override
    public boolean queueAction(EnhancedFakePlayer bot, ActionType actionType, Object... parameters) {
        if (bot == null || actionType == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        if (context == null) {
            return false;
        }
        
        IAction actionTemplate = actions.get(actionType);
        if (actionTemplate == null) {
            _log.warn("Action type " + actionType + " not registered");
            return false;
        }
        
        // Создаем новый экземпляр действия
        IAction actionInstance = createActionInstance(actionTemplate);
        actionInstance.init(context);
        
        // Устанавливаем параметры действия
        setActionParameters(actionInstance, parameters);
        
        // Добавляем в очередь
        Queue<IAction> queue = botActionQueues.computeIfAbsent(context.getBotId(), k -> new LinkedList<>());
        queue.offer(actionInstance);
        
        _log.debug("Queued action " + actionType.getName() + " for bot " + context.getBotId());
        
        return true;
    }
    
    @Override
    public boolean executeNextAction(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        Queue<IAction> queue = botActionQueues.get(context.getBotId());
        
        if (queue == null || queue.isEmpty()) {
            return false;
        }
        
        // Получаем следующее действие из очереди
        IAction action = queue.poll();
        
        // Останавливаем текущее действие, если оно может быть прервано
        IAction currentAction = botCurrentActions.get(context.getBotId());
        if (currentAction != null && currentAction.canInterrupt()) {
            currentAction.interrupt();
        }
        
        // Выполняем действие
        boolean result = action.execute(bot);
        
        if (result) {
            // Действие завершено
            action.onEnd(context);
        } else {
            // Действие продолжается
            botCurrentActions.put(context.getBotId(), action);
        }
        
        _log.debug("Executed next action for bot " + context.getBotId());
        
        return result;
    }
    
    @Override
    public void clearActionQueue(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return;
        }
        
        BotContext context = bot.getContext();
        Queue<IAction> queue = botActionQueues.get(context.getBotId());
        
        if (queue != null) {
            queue.clear();
            _log.debug("Cleared action queue for bot " + context.getBotId());
        }
    }
    
    @Override
    public IAction getCurrentAction(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return null;
        }
        
        return botCurrentActions.get(bot.getContext().getBotId());
    }
    
    @Override
    public List<IAction> getActionQueue(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return new ArrayList<>();
        }
        
        BotContext context = bot.getContext();
        Queue<IAction> queue = botActionQueues.get(context.getBotId());
        
        if (queue == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(queue);
    }
    
    @Override
    public boolean hasActionsInQueue(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        Queue<IAction> queue = botActionQueues.get(context.getBotId());
        
        return queue != null && !queue.isEmpty();
    }
    
    @Override
    public int getActionQueueSize(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return 0;
        }
        
        BotContext context = bot.getContext();
        Queue<IAction> queue = botActionQueues.get(context.getBotId());
        
        return queue != null ? queue.size() : 0;
    }
    
    @Override
    public void registerAction(IAction action) {
        if (action == null) {
            return;
        }
        
        actions.put(action.getType(), action);
        _log.info("Registered action: " + action.getType().getName());
    }
    
    @Override
    public void unregisterAction(ActionType actionType) {
        if (actionType == null) {
            return;
        }
        
        IAction removed = actions.remove(actionType);
        if (removed != null) {
            _log.info("Unregistered action: " + actionType.getName());
        }
    }
    
    @Override
    public IAction getAction(ActionType actionType) {
        return actions.get(actionType);
    }
    
    @Override
    public Collection<IAction> getAllActions() {
        return new ArrayList<>(actions.values());
    }
    
    @Override
    public boolean isActionRegistered(ActionType actionType) {
        return actions.containsKey(actionType);
    }
    
    @Override
    public void stopCurrentAction(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return;
        }
        
        BotContext context = bot.getContext();
        IAction action = botCurrentActions.remove(context.getBotId());
        
        if (action != null) {
            action.interrupt();
            action.onEnd(context);
            _log.info("Stopped current action for bot " + context.getBotId());
        }
    }
    
    @Override
    public void interruptAllActions(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return;
        }
        
        BotContext context = bot.getContext();
        
        // Останавливаем текущее действие
        stopCurrentAction(bot);
        
        // Очищаем очередь действий
        clearActionQueue(bot);
        
        _log.info("Interrupted all actions for bot " + context.getBotId());
    }
    
    @Override
    public boolean canExecuteAction(EnhancedFakePlayer bot, ActionType actionType) {
        if (bot == null || actionType == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        if (context == null) {
            return false;
        }
        
        IAction actionTemplate = actions.get(actionType);
        if (actionTemplate == null) {
            return false;
        }
        
        return actionTemplate.canExecute(context);
    }
    
    @Override
    public String getActionStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Action Manager Statistics ===\n");
        
        // Статистика зарегистрированных действий
        stats.append("Registered actions: ").append(actions.size()).append("\n");
        
        // Статистика использования действий
        stats.append("Action usage statistics:\n");
        for (Map.Entry<ActionType, AtomicLong> entry : actionUsageStats.entrySet()) {
            stats.append("  ").append(entry.getKey().getName())
                 .append(": ").append(entry.getValue().get()).append(" times\n");
        }
        
        // Статистика активных ботов
        stats.append("Active bots with actions: ").append(botCurrentActions.size()).append("\n");
        
        // Статистика очередей
        int totalQueuedActions = botActionQueues.values().stream()
                .mapToInt(Queue::size)
                .sum();
        stats.append("Total queued actions: ").append(totalQueuedActions).append("\n");
        
        return stats.toString();
    }
    
    @Override
    public String getBotActionStatistics(int botId) {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Bot ").append(botId).append(" Action Statistics ===\n");
        
        IAction currentAction = botCurrentActions.get(botId);
        if (currentAction != null) {
            stats.append("Current action: ").append(currentAction.getType().getName()).append("\n");
            stats.append("Action active: ").append(currentAction.isActive()).append("\n");
            stats.append("Action completed: ").append(currentAction.isCompleted()).append("\n");
            stats.append("Action stats: ").append(currentAction.getStatistics()).append("\n");
        } else {
            stats.append("No current action\n");
        }
        
        Queue<IAction> queue = botActionQueues.get(botId);
        if (queue != null && !queue.isEmpty()) {
            stats.append("Queued actions: ").append(queue.size()).append("\n");
            stats.append("Next action: ").append(queue.peek().getType().getName()).append("\n");
        } else {
            stats.append("No queued actions\n");
        }
        
        AtomicLong actionCount = botActionStats.get(botId);
        if (actionCount != null) {
            stats.append("Total actions executed: ").append(actionCount.get()).append("\n");
        }
        
        return stats.toString();
    }
    
    @Override
    public ActionType getRecommendedAction(EnhancedFakePlayer bot, BotContext context) {
        // Простая логика рекомендации действий
        // В реальной реализации здесь должна быть более сложная логика
        
        if (bot.isInCombat()) {
            return ActionType.ATTACK;
        } else if (bot.getHpPercent() < 50) {
            return ActionType.REST;
        } else if (bot.getMpPercent() < 30) {
            return ActionType.MEDITATE;
        } else {
            return ActionType.SEARCH;
        }
    }
    
    /**
     * Создает новый экземпляр действия
     */
    private IAction createActionInstance(IAction template) {
        try {
            // Создаем новый экземпляр через рефлексию
            Class<?> actionClass = template.getClass();
            return (IAction) actionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            _log.error("Error creating action instance for " + template.getType(), e);
            return template;
        }
    }
    
    /**
     * Устанавливает параметры действия
     */
    private void setActionParameters(IAction action, Object... parameters) {
        if (parameters == null || parameters.length == 0) {
            return;
        }
        
        // Устанавливаем параметры в зависимости от типа действия
        ActionType actionType = action.getType();
        
        switch (actionType) {
            case MOVE:
                if (parameters.length >= 3) {
                    ((MoveAction) action).setTarget((Integer) parameters[0], (Integer) parameters[1], (Integer) parameters[2]);
                }
                break;
            case ATTACK:
                if (parameters.length >= 1) {
                    ((AttackAction) action).setTarget((Integer) parameters[0]);
                }
                break;
            case CAST_SKILL:
                if (parameters.length >= 1) {
                    Integer targetId = parameters.length >= 2 ? (Integer) parameters[1] : null;
                    Integer level = parameters.length >= 3 ? (Integer) parameters[2] : 1;
                    ((CastAction) action).setSkill((Integer) parameters[0], targetId, level);
                }
                break;
            case PICKUP:
                if (parameters.length >= 1) {
                    if (parameters[0] instanceof Integer) {
                        ((LootAction) action).setItem((Integer) parameters[0]);
                    } else if (parameters.length >= 3) {
                        ((LootAction) action).setPosition((Integer) parameters[0], (Integer) parameters[1], (Integer) parameters[2]);
                    }
                }
                break;
            default:
                // Для других типов действий параметры не обрабатываются
                break;
        }
    }
    
    /**
     * Получает количество активных ботов с действиями
     */
    public int getActiveBotCount() {
        return botCurrentActions.size();
    }
    
    /**
     * Получает список активных ботов
     */
    public Collection<Integer> getActiveBotIds() {
        return new ArrayList<>(botCurrentActions.keySet());
    }
    
    /**
     * Очищает статистику
     */
    public void clearStatistics() {
        actionUsageStats.clear();
        botActionStats.clear();
        _log.info("Statistics cleared");
    }
}
