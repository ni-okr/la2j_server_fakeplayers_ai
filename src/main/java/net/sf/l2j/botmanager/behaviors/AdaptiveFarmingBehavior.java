package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.learning.FeedbackCollector;
import net.sf.l2j.botmanager.learning.PerformanceAnalyzer;
import net.sf.l2j.botmanager.learning.AdaptiveAlgorithm;
import net.sf.l2j.botmanager.learning.AdaptiveAlgorithm.BotAdaptationParams;
import net.sf.l2j.botmanager.utils.Logger;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.L2World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Адаптивное поведение фарма мобов.
 * 
 * <p>Этот класс расширяет базовое поведение фарма, добавляя способность к обучению
 * и адаптации на основе обратной связи. Бот анализирует свою эффективность,
 * корректирует параметры поведения и улучшает стратегию фарма.</p>
 * 
 * <p>Основные возможности адаптации:</p>
 * <ul>
 *   <li>Динамическое изменение приоритета целей</li>
 *   <li>Адаптация времени ожидания между действиями</li>
 *   <li>Корректировка стратегии выбора мобов</li>
 *   <li>Обучение на основе успешности фарма</li>
 *   <li>Адаптация к изменяющимся условиям</li>
 * </ul>
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
public class AdaptiveFarmingBehavior extends AbstractBehavior {
    
    private static final Logger logger = Logger.getLogger(AdaptiveFarmingBehavior.class);
    
    /** Коллектор обратной связи для обучения */
    private final FeedbackCollector feedbackCollector;
    
    /** Анализатор производительности */
    private final PerformanceAnalyzer performanceAnalyzer;
    
    /** Адаптивный алгоритм */
    private final AdaptiveAlgorithm adaptiveAlgorithm;
    
    /** Параметры адаптации для данного бота */
    private BotAdaptationParams adaptationParams;
    
    /** Время последней адаптации */
    private long lastAdaptationTime = 0;
    
    /** Интервал адаптации в миллисекундах (5 минут) */
    private static final long ADAPTATION_INTERVAL = 5 * 60 * 1000;
    
    /** Минимальный уровень моба для атаки */
    private int minMobLevel = 1;
    
    /** Максимальный уровень моба для атаки */
    private int maxMobLevel = 10;
    
    /** Радиус поиска мобов */
    private int searchRadius = 1000;
    
    /** Время ожидания между действиями */
    private long actionDelay = 2000;
    
    /** Коэффициент агрессивности (0.0 - 1.0) */
    private double aggressiveness = 0.5;
    
    /** Коэффициент осторожности (0.0 - 1.0) */
    private double caution = 0.3;
    
    /**
     * Конструктор адаптивного поведения фарма.
     */
    public AdaptiveFarmingBehavior() {
        super(BehaviorType.FARMING);
        
        this.feedbackCollector = FeedbackCollector.getInstance();
        this.performanceAnalyzer = PerformanceAnalyzer.getInstance(feedbackCollector);
        this.adaptiveAlgorithm = AdaptiveAlgorithm.getInstance(feedbackCollector, performanceAnalyzer);
        
        // Устанавливаем интервалы выполнения
        this.minExecutionInterval = 1000;
        this.maxExecutionInterval = 5000;
        
        logger.info("AdaptiveFarmingBehavior initialized");
    }
    
    /**
     * Проверить, может ли поведение быть выполнено.
     * 
     * @param context контекст бота
     * @return true, если поведение может быть выполнено
     */
    @Override
    public boolean canExecute(BotContext context) {
        if (context == null) {
            return false;
        }
        
        L2PcInstance player = context.getPlayerInstance();
        if (player == null) {
            return false;
        }
        
        // Проверяем базовые условия
        if (player.isDead() || player.isOnline() == 0) {
            return false;
        }
        
        // Проверяем HP и MP
        if (player.getCurrentHp() < player.getMaxHp() * 0.3) {
            return false; // Слишком мало HP для фарма
        }
        
        if (player.getCurrentMp() < player.getMaxMp() * 0.1) {
            return false; // Слишком мало MP для фарма
        }
        
        // Проверяем наличие мобов поблизости
        return hasNearbyMobs(player);
    }
    
    /**
     * Выполнить поведение фарма с адаптацией.
     * 
     * @param bot бот
     * @return true, если поведение выполнено успешно
     */
    @Override
    protected boolean doExecute(EnhancedFakePlayer bot) {
        BotContext context = bot.getContext();
        if (!canExecute(context)) {
            return false;
        }
        
        L2PcInstance player = context.getPlayerInstance();
        
        try {
            // Выполняем адаптацию, если прошло достаточно времени
            if (shouldAdapt()) {
                adaptBehavior(bot);
            }
            
            // Получаем текущие параметры адаптации
            updateAdaptationParams(bot);
            
            // Выполняем фарм с адаптированными параметрами
            boolean success = performAdaptiveFarming(player);
            
            // Записываем обратную связь
            recordFeedback(bot, success);
            
            return success;
            
        } catch (Exception e) {
            logger.error("Error in AdaptiveFarmingBehavior.doExecute: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Получить приоритет поведения с учетом адаптации.
     * 
     * @param context контекст бота
     * @return приоритет поведения
     */
    public int getPriority(BotContext context) {
        if (!canExecute(context)) {
            return 0;
        }
        
        // Базовый приоритет
        int basePriority = 100; // Базовый приоритет для фарма
        
        // Применяем адаптивный коэффициент
        if (adaptationParams != null) {
            double adaptiveMultiplier = adaptationParams.getBehaviorPriority();
            return (int) (basePriority * adaptiveMultiplier);
        }
        
        return basePriority;
    }
    
    /**
     * Получить статистику поведения с адаптивными параметрами.
     * 
     * @return статистика поведения
     */
    @Override
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append(super.getStatistics());
        
        if (adaptationParams != null) {
            stats.append("\n=== Adaptive Parameters ===\n");
            stats.append("Action Priority: ").append(String.format("%.2f", adaptationParams.getActionPriority())).append("\n");
            stats.append("Behavior Priority: ").append(String.format("%.2f", adaptationParams.getBehaviorPriority())).append("\n");
            stats.append("Action Frequency: ").append(String.format("%.2f", adaptationParams.getActionFrequency())).append("\n");
            stats.append("Learning Rate: ").append(String.format("%.2f", adaptationParams.getLearningRate())).append("\n");
        }
        
        stats.append("\n=== Farming Parameters ===\n");
        stats.append("Min Mob Level: ").append(minMobLevel).append("\n");
        stats.append("Max Mob Level: ").append(maxMobLevel).append("\n");
        stats.append("Search Radius: ").append(searchRadius).append("\n");
        stats.append("Action Delay: ").append(actionDelay).append("ms\n");
        stats.append("Aggressiveness: ").append(String.format("%.2f", aggressiveness)).append("\n");
        stats.append("Caution: ").append(String.format("%.2f", caution)).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Проверить, нужно ли выполнить адаптацию.
     * 
     * @return true, если нужно адаптироваться
     */
    private boolean shouldAdapt() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAdaptationTime) > ADAPTATION_INTERVAL;
    }
    
    /**
     * Выполнить адаптацию поведения.
     * 
     * @param bot бот для адаптации
     */
    private void adaptBehavior(EnhancedFakePlayer bot) {
        try {
            int botId = bot.getContext().getBotId();
            
            // Выполняем адаптацию через адаптивный алгоритм
            AdaptiveAlgorithm.AdaptationResult result = adaptiveAlgorithm.adaptBot(botId);
            
            if (result.isSuccess()) {
                logger.info("Bot " + botId + " farming behavior adapted successfully");
                lastAdaptationTime = System.currentTimeMillis();
            } else {
                logger.warn("Bot " + botId + " farming behavior adaptation failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error during behavior adaptation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Обновить параметры адаптации для бота.
     * 
     * @param bot бот
     */
    private void updateAdaptationParams(EnhancedFakePlayer bot) {
        try {
            int botId = bot.getContext().getBotId();
            adaptationParams = adaptiveAlgorithm.getBotParams(botId);
            
            // Применяем адаптивные параметры к поведению
            if (adaptationParams != null) {
                // Корректируем задержку между действиями
                long baseDelay = 2000;
                double frequencyMultiplier = adaptationParams.getActionFrequency();
                actionDelay = (long) (baseDelay / frequencyMultiplier);
                
                // Корректируем агрессивность
                double baseAggressiveness = 0.5;
                double priorityMultiplier = adaptationParams.getActionPriority();
                aggressiveness = Math.min(1.0, baseAggressiveness * priorityMultiplier);
                
                // Корректируем осторожность (обратно пропорционально агрессивности)
                caution = Math.max(0.0, 1.0 - aggressiveness);
            }
            
        } catch (Exception e) {
            logger.error("Error updating adaptation parameters: " + e.getMessage(), e);
        }
    }
    
    /**
     * Выполнить адаптивный фарм мобов.
     * 
     * @param player игрок
     * @return true, если фарм выполнен успешно
     */
    private boolean performAdaptiveFarming(L2PcInstance player) {
        try {
            // Находим подходящего моба
            L2MonsterInstance target = findBestTarget(player);
            if (target == null) {
                return false;
            }
            
            // Проверяем расстояние до цели
            double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
            if (distance > searchRadius) {
                return false;
            }
            
            // Применяем стратегию атаки на основе адаптивных параметров
            boolean success = executeAdaptiveAttack(player, target);
            
            // Применяем задержку между действиями
            if (actionDelay > 0) {
                Thread.sleep(actionDelay);
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("Error in performAdaptiveFarming: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Найти лучшую цель для атаки.
     * 
     * @param player игрок
     * @return лучший моб для атаки или null
     */
    private L2MonsterInstance findBestTarget(L2PcInstance player) {
        try {
            List<net.sf.l2j.gameserver.model.L2Object> visibleObjects = 
                L2World.getInstance().getVisibleObjects(player, searchRadius);
            
            L2MonsterInstance bestTarget = null;
            double bestScore = Double.NEGATIVE_INFINITY;
            
            for (net.sf.l2j.gameserver.model.L2Object obj : visibleObjects) {
                if (obj instanceof L2MonsterInstance) {
                    L2MonsterInstance monster = (L2MonsterInstance) obj;
                    
                    // Проверяем уровень моба
                    if (monster.getLevel() < minMobLevel || monster.getLevel() > maxMobLevel) {
                        continue;
                    }
                    
                    // Проверяем, что моб не мертв
                    if (monster.isDead()) {
                        continue;
                    }
                    
                    // Вычисляем оценку моба
                    double score = calculateTargetScore(player, monster);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestTarget = monster;
                    }
                }
            }
            
            return bestTarget;
            
        } catch (Exception e) {
            logger.error("Error finding target: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Вычислить оценку цели для атаки.
     * 
     * @param player игрок
     * @param monster моб
     * @return оценка цели
     */
    private double calculateTargetScore(L2PcInstance player, L2MonsterInstance monster) {
        double score = 0.0;
        
        // Базовый счет на основе уровня
        int levelDiff = monster.getLevel() - player.getLevel();
        score += 100 - Math.abs(levelDiff) * 10;
        
        // Бонус за близость
        double distance = player.getDistance(monster.getX(), monster.getY(), monster.getZ());
        score += (searchRadius - distance) / searchRadius * 50;
        
        // Применяем адаптивные коэффициенты
        if (adaptationParams != null) {
            // Агрессивность влияет на предпочтение более сильных мобов
            score += levelDiff * aggressiveness * 20;
            
            // Осторожность влияет на предпочтение более слабых мобов
            score -= levelDiff * caution * 15;
        }
        
        // Добавляем случайность для разнообразия
        score += ThreadLocalRandom.current().nextDouble(-10, 10);
        
        return score;
    }
    
    /**
     * Выполнить адаптивную атаку.
     * 
     * @param player игрок
     * @param target цель
     * @return true, если атака выполнена успешно
     */
    private boolean executeAdaptiveAttack(L2PcInstance player, L2MonsterInstance target) {
        try {
            // TODO: Реализовать реальную атаку через L2J API
            // Пока что симулируем атаку
            
            double distance = player.getDistance(target.getX(), target.getY(), target.getZ());
            
            // Проверяем, нужно ли подойти ближе
            if (distance > 100) {
                // TODO: Реализовать движение к цели
                logger.debug("Moving to target at distance: " + distance);
            }
            
            // Выполняем атаку
            if (distance <= 100) {
                // TODO: Реализовать атаку
                logger.debug("Attacking target: " + target.getName() + " (Level: " + target.getLevel() + ")");
                
                // Симулируем успешность атаки на основе адаптивных параметров
                double successChance = 0.7 + (aggressiveness * 0.2) - (caution * 0.1);
                return ThreadLocalRandom.current().nextDouble() < successChance;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("Error in executeAdaptiveAttack: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Записать обратную связь о выполнении поведения.
     * 
     * @param bot бот
     * @param success успешность выполнения
     */
    private void recordFeedback(EnhancedFakePlayer bot, boolean success) {
        try {
            // TODO: Реализовать запись обратной связи через FeedbackCollector
            // feedbackCollector.recordBehavior(bot, BehaviorType.FARMING, success, executionTime, details);
            
            if (success) {
                logger.debug("Farming behavior executed successfully for bot " + bot.getContext().getBotId());
            } else {
                logger.debug("Farming behavior failed for bot " + bot.getContext().getBotId());
            }
            
        } catch (Exception e) {
            logger.error("Error recording feedback: " + e.getMessage(), e);
        }
    }
    
    /**
     * Проверить наличие мобов поблизости.
     * 
     * @param player игрок
     * @return true, если есть мобы поблизости
     */
    private boolean hasNearbyMobs(L2PcInstance player) {
        try {
            List<net.sf.l2j.gameserver.model.L2Object> visibleObjects = 
                L2World.getInstance().getVisibleObjects(player, searchRadius);
            
            for (net.sf.l2j.gameserver.model.L2Object obj : visibleObjects) {
                if (obj instanceof L2MonsterInstance) {
                    L2MonsterInstance monster = (L2MonsterInstance) obj;
                    if (!monster.isDead() && 
                        monster.getLevel() >= minMobLevel && 
                        monster.getLevel() <= maxMobLevel) {
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("Error checking for nearby mobs: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Установить минимальный уровень моба для атаки.
     * 
     * @param minLevel минимальный уровень
     */
    public void setMinMobLevel(int minLevel) {
        this.minMobLevel = Math.max(1, minLevel);
    }
    
    /**
     * Установить максимальный уровень моба для атаки.
     * 
     * @param maxLevel максимальный уровень
     */
    public void setMaxMobLevel(int maxLevel) {
        this.maxMobLevel = Math.max(minMobLevel, maxLevel);
    }
    
    /**
     * Установить радиус поиска мобов.
     * 
     * @param radius радиус поиска
     */
    public void setSearchRadius(int radius) {
        this.searchRadius = Math.max(100, radius);
    }
    
    /**
     * Получить текущие параметры адаптации.
     * 
     * @return параметры адаптации
     */
    public BotAdaptationParams getAdaptationParams() {
        return adaptationParams;
    }
    
    /**
     * Установить параметры адаптации.
     * 
     * @param params параметры адаптации
     */
    public void setAdaptationParams(BotAdaptationParams params) {
        this.adaptationParams = params;
    }
}
