package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Менеджер поведений ботов
 * 
 * Управляет регистрацией, переключением и выполнением поведений ботов.
 * Реализует паттерн Singleton для глобального доступа.
 */
public class BehaviorManager implements IBehaviorManager {
    
    private static final Logger _log = Logger.getLogger(BehaviorManager.class);
    
    private static BehaviorManager instance;
    
    // Регистрированные поведения по типам
    private final Map<BehaviorType, IBehavior> behaviors = new ConcurrentHashMap<>();
    
    // Текущие поведения ботов
    private final Map<Integer, IBehavior> botBehaviors = new ConcurrentHashMap<>();
    
    // Статистика использования поведений
    private final Map<BehaviorType, AtomicLong> behaviorUsageStats = new ConcurrentHashMap<>();
    
    // Статистика переключений поведений
    private final Map<Integer, AtomicLong> botSwitchStats = new ConcurrentHashMap<>();
    
    private BehaviorManager() {
        initializeDefaultBehaviors();
        _log.info("BehaviorManager initialized");
    }
    
    public static BehaviorManager getInstance() {
        if (instance == null) {
            synchronized (BehaviorManager.class) {
                if (instance == null) {
                    instance = new BehaviorManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Инициализирует поведения по умолчанию
     */
    private void initializeDefaultBehaviors() {
        // Регистрируем стандартные поведения
        registerBehavior(new FarmingBehavior());
        registerBehavior(new QuestingBehavior());
        registerBehavior(new PvPBehavior());
        
        // Регистрируем дополнительные поведения
        registerBehavior(new CraftingBehavior());
        registerBehavior(new TradingBehavior());
        registerBehavior(new PatrollingBehavior());
        registerBehavior(new SocialBehavior());
        
        // Создаем поведение бездействия
        registerBehavior(new IdleBehavior());
        
        _log.info("Default behaviors registered");
    }
    
    @Override
    public boolean setBehavior(EnhancedFakePlayer bot, BehaviorType behaviorType) {
        if (bot == null || behaviorType == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        if (context == null) {
            return false;
        }
        
        IBehavior newBehavior = behaviors.get(behaviorType);
        if (newBehavior == null) {
            _log.warn("Behavior type " + behaviorType + " not registered");
            return false;
        }
        
        // Останавливаем текущее поведение
        IBehavior currentBehavior = botBehaviors.get(context.getBotId());
        if (currentBehavior != null) {
            currentBehavior.deactivate();
            currentBehavior.onEnd(context);
        }
        
        // Устанавливаем новое поведение
        IBehavior behaviorInstance = createBehaviorInstance(newBehavior);
        behaviorInstance.init(context);
        behaviorInstance.activate();
        
        botBehaviors.put(context.getBotId(), behaviorInstance);
        
        // Обновляем статистику
        behaviorUsageStats.computeIfAbsent(behaviorType, k -> new AtomicLong(0)).incrementAndGet();
        botSwitchStats.computeIfAbsent(context.getBotId(), k -> new AtomicLong(0)).incrementAndGet();
        
        _log.info("Set behavior " + behaviorType.getName() + " for bot " + context.getBotId());
        
        return true;
    }
    
    @Override
    public IBehavior getCurrentBehavior(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return null;
        }
        
        return botBehaviors.get(bot.getContext().getBotId());
    }
    
    @Override
    public boolean executeCurrentBehavior(EnhancedFakePlayer bot) {
        if (bot == null) {
            return false;
        }
        
        IBehavior behavior = getCurrentBehavior(bot);
        if (behavior == null) {
            return false;
        }
        
        try {
            return behavior.execute(bot);
        } catch (Exception e) {
            _log.error("Error executing behavior for bot " + bot.getContext().getBotId(), e);
            return false;
        }
    }
    
    @Override
    public void registerBehavior(IBehavior behavior) {
        if (behavior == null) {
            return;
        }
        
        behaviors.put(behavior.getType(), behavior);
        _log.info("Registered behavior: " + behavior.getType().getName());
    }
    
    @Override
    public void unregisterBehavior(BehaviorType behaviorType) {
        if (behaviorType == null) {
            return;
        }
        
        IBehavior removed = behaviors.remove(behaviorType);
        if (removed != null) {
            _log.info("Unregistered behavior: " + behaviorType.getName());
        }
    }
    
    @Override
    public IBehavior getBehavior(BehaviorType behaviorType) {
        return behaviors.get(behaviorType);
    }
    
    @Override
    public Collection<IBehavior> getAllBehaviors() {
        return new ArrayList<>(behaviors.values());
    }
    
    @Override
    public boolean isBehaviorRegistered(BehaviorType behaviorType) {
        return behaviors.containsKey(behaviorType);
    }
    
    @Override
    public void stopCurrentBehavior(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return;
        }
        
        IBehavior behavior = botBehaviors.remove(bot.getContext().getBotId());
        if (behavior != null) {
            behavior.deactivate();
            behavior.onEnd(bot.getContext());
            _log.info("Stopped behavior for bot " + bot.getContext().getBotId());
        }
    }
    
    @Override
    public boolean switchToNextBehavior(EnhancedFakePlayer bot) {
        if (bot == null || bot.getContext() == null) {
            return false;
        }
        
        BotContext context = bot.getContext();
        IBehavior currentBehavior = getCurrentBehavior(bot);
        
        if (currentBehavior == null) {
            // Если нет текущего поведения, устанавливаем IDLE
            return setBehavior(bot, BehaviorType.IDLE);
        }
        
        // Определяем следующее подходящее поведение
        BehaviorType nextBehaviorType = determineNextBehavior(context, currentBehavior.getType());
        
        if (nextBehaviorType != null && nextBehaviorType != currentBehavior.getType()) {
            return setBehavior(bot, nextBehaviorType);
        }
        
        return false;
    }
    
    @Override
    public String getBehaviorStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Behavior Manager Statistics ===\n");
        
        // Статистика зарегистрированных поведений
        stats.append("Registered behaviors: ").append(behaviors.size()).append("\n");
        
        // Статистика использования поведений
        stats.append("Behavior usage statistics:\n");
        for (Map.Entry<BehaviorType, AtomicLong> entry : behaviorUsageStats.entrySet()) {
            stats.append("  ").append(entry.getKey().getName())
                 .append(": ").append(entry.getValue().get()).append(" times\n");
        }
        
        // Статистика активных ботов
        stats.append("Active bots with behaviors: ").append(botBehaviors.size()).append("\n");
        
        // Статистика переключений
        long totalSwitches = botSwitchStats.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        stats.append("Total behavior switches: ").append(totalSwitches).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Создает новый экземпляр поведения
     */
    private IBehavior createBehaviorInstance(IBehavior template) {
        try {
            // Создаем новый экземпляр через рефлексию
            Class<?> behaviorClass = template.getClass();
            return (IBehavior) behaviorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            _log.error("Error creating behavior instance for " + template.getType(), e);
            return template;
        }
    }
    
    /**
     * Определяет следующее подходящее поведение
     */
    private BehaviorType determineNextBehavior(BotContext context, BehaviorType currentType) {
        // Простая логика определения следующего поведения
        // В реальной реализации здесь должна быть более сложная логика
        
        if (currentType == BehaviorType.IDLE) {
            // Из бездействия переходим к различным активностям
            if (context.getPlayerInstance() != null) {
                int level = context.getPlayerInstance().getLevel();
                if (level >= 10) {
                    BehaviorType[] options = {BehaviorType.FARMING, BehaviorType.QUESTING, BehaviorType.SOCIAL};
                    return options[ThreadLocalRandom.current().nextInt(options.length)];
                }
            }
        } else if (currentType == BehaviorType.FARMING) {
            // После фарма переходим к квестам, крафту или торговле
            if (context.getPlayerInstance() != null) {
                int level = context.getPlayerInstance().getLevel();
                if (level >= 20) {
                    BehaviorType[] options = {BehaviorType.QUESTING, BehaviorType.CRAFTING, BehaviorType.TRADING};
                    return options[ThreadLocalRandom.current().nextInt(options.length)];
                }
            }
        } else if (currentType == BehaviorType.QUESTING) {
            // После квестов переходим к фарму, крафту или социальному взаимодействию
            if (context.getPlayerInstance() != null) {
                int level = context.getPlayerInstance().getLevel();
                if (level >= 20) {
                    BehaviorType[] options = {BehaviorType.FARMING, BehaviorType.CRAFTING, BehaviorType.SOCIAL};
                    return options[ThreadLocalRandom.current().nextInt(options.length)];
                }
            }
        } else if (currentType == BehaviorType.PVP) {
            // После PvP переходим к фарму, отдыху или патрулированию
            BehaviorType[] options = {BehaviorType.FARMING, BehaviorType.IDLE, BehaviorType.PATROLLING};
            return options[ThreadLocalRandom.current().nextInt(options.length)];
        } else if (currentType == BehaviorType.CRAFTING) {
            // После крафта переходим к торговле или фарму
            BehaviorType[] options = {BehaviorType.TRADING, BehaviorType.FARMING, BehaviorType.SOCIAL};
            return options[ThreadLocalRandom.current().nextInt(options.length)];
        } else if (currentType == BehaviorType.TRADING) {
            // После торговли переходим к крафту или социальному взаимодействию
            BehaviorType[] options = {BehaviorType.CRAFTING, BehaviorType.SOCIAL, BehaviorType.FARMING};
            return options[ThreadLocalRandom.current().nextInt(options.length)];
        } else if (currentType == BehaviorType.PATROLLING) {
            // После патрулирования переходим к фарму или отдыху
            BehaviorType[] options = {BehaviorType.FARMING, BehaviorType.IDLE, BehaviorType.SOCIAL};
            return options[ThreadLocalRandom.current().nextInt(options.length)];
        } else if (currentType == BehaviorType.SOCIAL) {
            // После социального взаимодействия переходим к различным активностям
            BehaviorType[] options = {BehaviorType.FARMING, BehaviorType.QUESTING, BehaviorType.CRAFTING};
            return options[ThreadLocalRandom.current().nextInt(options.length)];
        }
        
        return BehaviorType.IDLE;
    }
    
    /**
     * Получает статистику для конкретного бота
     */
    public String getBotStatistics(int botId) {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Bot ").append(botId).append(" Statistics ===\n");
        
        IBehavior currentBehavior = botBehaviors.get(botId);
        if (currentBehavior != null) {
            stats.append("Current behavior: ").append(currentBehavior.getType().getName()).append("\n");
            stats.append("Behavior active: ").append(currentBehavior.isActive()).append("\n");
            stats.append("Behavior stats: ").append(currentBehavior.getStatistics()).append("\n");
        } else {
            stats.append("No active behavior\n");
        }
        
        AtomicLong switches = botSwitchStats.get(botId);
        if (switches != null) {
            stats.append("Behavior switches: ").append(switches.get()).append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Очищает статистику
     */
    public void clearStatistics() {
        behaviorUsageStats.clear();
        botSwitchStats.clear();
        _log.info("Statistics cleared");
    }
    
    /**
     * Получает количество активных ботов с поведениями
     */
    public int getActiveBotCount() {
        return botBehaviors.size();
    }
    
    /**
     * Получает список активных ботов
     */
    public Collection<Integer> getActiveBotIds() {
        return new ArrayList<>(botBehaviors.keySet());
    }
}
