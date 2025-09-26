package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.learning.FeedbackCollector;
import net.sf.l2j.botmanager.learning.PerformanceAnalyzer;
import net.sf.l2j.botmanager.learning.AdaptiveAlgorithm;
import net.sf.l2j.botmanager.utils.Logger;

/**
 * Адаптивное PvP поведение для ботов
 * 
 * Это поведение обеспечивает интеллектуальное PvP взаимодействие с другими игроками,
 * включая адаптивные стратегии боя, анализ противников и автоматическую настройку
 * параметров на основе обратной связи от системы обучения.
 * 
 * Ключевые возможности:
 * - Адаптивные стратегии PvP боя
 * - Анализ противников и выбор тактики
 * - Интеграция с системой обучения
 * - Автоматическая адаптация боевых параметров
 * - Статистика и мониторинг производительности
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class AdaptivePvPBehavior extends AbstractBehavior {
    
    // ==================== АДАПТИВНЫЕ ПАРАМЕТРЫ PVP ====================
    
    /**
     * Минимальный уровень противника для атаки
     * Адаптируется на основе успешности боев
     */
    private int minTargetLevel = 1;
    
    /**
     * Максимальный уровень противника для атаки
     * Адаптируется на основе успешности боев
     */
    private int maxTargetLevel = 10;
    
    /**
     * Приоритет PvP действий
     * Влияет на частоту выбора PvP действий
     */
    private double actionPriority = 1.0;
    
    /**
     * Приоритет PvP поведения
     * Влияет на общий приоритет поведения
     */
    private double behaviorPriority = 1.0;
    
    /**
     * Частота PvP действий
     * Влияет на интервал между PvP действиями
     */
    private double actionFrequency = 1.0;
    
    /**
     * Агрессивность поведения
     * Определяет склонность к инициации PvP
     */
    private double aggressiveness = 0.5;
    
    /**
     * Осторожность поведения
     * Определяет склонность к избеганию опасных ситуаций
     */
    private double caution = 0.5;
    
    /**
     * Стратегия боя
     * 0.0 - оборонительная, 1.0 - наступательная
     */
    private double combatStrategy = 0.5;
    
    // ==================== СИСТЕМА ОБУЧЕНИЯ ====================
    
    /**
     * Сборщик обратной связи для анализа эффективности PvP действий
     */
    private final FeedbackCollector feedbackCollector;
    
    /**
     * Анализатор производительности для оценки эффективности PvP стратегий
     */
    private final PerformanceAnalyzer performanceAnalyzer;
    
    /**
     * Адаптивный алгоритм для автоматической настройки PvP параметров
     */
    private final AdaptiveAlgorithm adaptiveAlgorithm;
    
    /**
     * Адаптивные параметры, полученные от системы обучения
     */
    private AdaptiveAlgorithm.BotAdaptationParams adaptationParams;
    
    // ==================== СТАТИСТИКА И МОНИТОРИНГ ====================
    
    /**
     * Количество успешных PvP боев
     */
    private int successfulBattles = 0;
    
    /**
     * Количество неудачных PvP боев
     */
    private int failedBattles = 0;
    
    /**
     * Общее время, проведенное в PvP
     */
    private long totalPvPTime = 0;
    
    /**
     * Время последнего PvP действия
     */
    private long lastPvPActionTime = 0;
    
    /**
     * Минимальный интервал между PvP действиями (в миллисекундах)
     */
    private long minExecutionInterval = 5000; // 5 секунд
    
    /**
     * Максимальный интервал между PvP действиями (в миллисекундах)
     */
    private long maxExecutionInterval = 30000; // 30 секунд
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор адаптивного PvP поведения
     * 
     * Инициализирует поведение с базовыми параметрами и подключает
     * систему обучения для адаптации стратегий.
     */
    public AdaptivePvPBehavior() {
        super(BehaviorType.PVP);
        
        // Инициализация системы обучения
        this.feedbackCollector = FeedbackCollector.getInstance();
        this.performanceAnalyzer = PerformanceAnalyzer.getInstance();
        this.adaptiveAlgorithm = AdaptiveAlgorithm.getInstance();
        
        // Инициализация адаптивных параметров
        this.adaptationParams = new AdaptiveAlgorithm.BotAdaptationParams();
        
        Logger.getLogger(AdaptivePvPBehavior.class).info("AdaptivePvPBehavior initialized");
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Проверяет, может ли поведение быть выполнено в текущем контексте
     * 
     * @param context контекст бота с информацией о текущем состоянии
     * @return true, если поведение может быть выполнено, false в противном случае
     */
    @Override
    public boolean canExecute(BotContext context) {
        if (context == null) {
            return false;
        }
        
        // Проверяем, что бот жив и не в процессе другого действия
        if (context.getBot() == null || !context.getBot().isAlive()) {
            return false;
        }
        
        // Проверяем интервал между PvP действиями
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPvPActionTime < minExecutionInterval) {
            return false;
        }
        
        // Проверяем, есть ли подходящие цели для PvP
        return hasSuitableTargets(context);
    }
    
    /**
     * Выполняет адаптивное PvP поведение
     * 
     * @param bot бот, для которого выполняется поведение
     * @return true, если поведение было успешно выполнено, false в противном случае
     */
    @Override
    public boolean doExecute(EnhancedFakePlayer bot) {
        if (bot == null || !bot.isAlive()) {
            return false;
        }
        
        try {
            // Обновляем время последнего PvP действия
            lastPvPActionTime = System.currentTimeMillis();
            
            // Анализируем текущую ситуацию
            PvPSituation situation = analyzePvPSituation(bot);
            
            // Выбираем стратегию на основе анализа
            PvPStrategy strategy = selectStrategy(situation);
            
            // Выполняем выбранную стратегию
            boolean success = executeStrategy(bot, strategy);
            
            // Записываем обратную связь
            recordFeedback(bot, strategy, success);
            
            // Адаптируем параметры на основе результатов
            adaptParameters(bot, strategy, success);
            
            return success;
            
        } catch (Exception e) {
            Logger.getLogger(AdaptivePvPBehavior.class).error("Error executing AdaptivePvPBehavior: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Рассчитывает приоритет поведения для данного контекста
     * 
     * @param context контекст бота
     * @return приоритет поведения (0.0 - 1.0)
     */
    public double getPriority(BotContext context) {
        if (context == null || context.getBot() == null) {
            return 0.0;
        }
        
        // Базовый приоритет PvP поведения
        double basePriority = 0.7;
        
        // Учитываем агрессивность
        basePriority *= aggressiveness;
        
        // Учитываем наличие подходящих целей
        if (hasSuitableTargets(context)) {
            basePriority *= 1.2;
        }
        
        // Учитываем адаптивные параметры
        if (adaptationParams != null) {
            basePriority *= adaptationParams.getBehaviorPriority();
        }
        
        return Math.min(1.0, basePriority);
    }
    
    // ==================== АНАЛИЗ И СТРАТЕГИИ ====================
    
    /**
     * Анализирует текущую PvP ситуацию
     * 
     * @param bot бот для анализа
     * @return анализ PvP ситуации
     */
    private PvPSituation analyzePvPSituation(EnhancedFakePlayer bot) {
        PvPSituation situation = new PvPSituation();
        
        // Анализируем здоровье бота
        situation.botHealth = bot.getCurrentHp() / (double) bot.getMaxHp();
        
        // Анализируем мана бота
        situation.botMana = bot.getCurrentMp() / (double) bot.getMaxMp();
        
        // Анализируем уровень бота
        situation.botLevel = bot.getLevel();
        
        // Анализируем ближайших противников
        situation.nearbyEnemies = findNearbyEnemies(bot);
        
        // Анализируем союзников
        situation.nearbyAllies = findNearbyAllies(bot);
        
        // Определяем уровень опасности
        situation.dangerLevel = calculateDangerLevel(situation);
        
        return situation;
    }
    
    /**
     * Выбирает стратегию PvP на основе анализа ситуации
     * 
     * @param situation анализ PvP ситуации
     * @return выбранная стратегия
     */
    private PvPStrategy selectStrategy(PvPSituation situation) {
        PvPStrategy strategy = new PvPStrategy();
        
        // Выбираем тип стратегии на основе ситуации
        if (situation.dangerLevel > 0.8) {
            strategy.type = PvPStrategyType.DEFENSIVE;
        } else if (situation.dangerLevel < 0.3) {
            strategy.type = PvPStrategyType.AGGRESSIVE;
        } else {
            strategy.type = PvPStrategyType.BALANCED;
        }
        
        // Настраиваем параметры стратегии
        strategy.aggressiveness = this.aggressiveness;
        strategy.caution = this.caution;
        strategy.combatStrategy = this.combatStrategy;
        
        // Учитываем адаптивные параметры
        if (adaptationParams != null) {
            strategy.aggressiveness *= adaptationParams.getActionPriority();
            strategy.caution *= adaptationParams.getBehaviorPriority();
        }
        
        return strategy;
    }
    
    /**
     * Выполняет выбранную PvP стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy стратегия для выполнения
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeStrategy(EnhancedFakePlayer bot, PvPStrategy strategy) {
        try {
            switch (strategy.type) {
                case AGGRESSIVE:
                    return executeAggressiveStrategy(bot, strategy);
                case DEFENSIVE:
                    return executeDefensiveStrategy(bot, strategy);
                case BALANCED:
                    return executeBalancedStrategy(bot, strategy);
                default:
                    return false;
            }
        } catch (Exception e) {
            Logger.getLogger(AdaptivePvPBehavior.class).error("Error executing PvP strategy: " + e.getMessage(), e);
            return false;
        }
    }
    
    // ==================== СТРАТЕГИИ ВЫПОЛНЕНИЯ ====================
    
    /**
     * Выполняет агрессивную PvP стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy параметры стратегии
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeAggressiveStrategy(EnhancedFakePlayer bot, PvPStrategy strategy) {
        // Ищем ближайшую цель для атаки
        EnhancedFakePlayer target = findBestTarget(bot);
        if (target == null) {
            return false;
        }
        
        // Атакуем цель
        return attackTarget(bot, target);
    }
    
    /**
     * Выполняет оборонительную PvP стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy параметры стратегии
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeDefensiveStrategy(EnhancedFakePlayer bot, PvPStrategy strategy) {
        // Проверяем, нужна ли защита
        if (bot.getCurrentHp() < bot.getMaxHp() * 0.5) {
            // Используем навык лечения
            return useHealingSkill(bot);
        }
        
        // Ищем безопасное место для отступления
        return retreatToSafety(bot);
    }
    
    /**
     * Выполняет сбалансированную PvP стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy параметры стратегии
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeBalancedStrategy(EnhancedFakePlayer bot, PvPStrategy strategy) {
        // Чередуем между атакой и защитой
        if (Math.random() < strategy.aggressiveness) {
            return executeAggressiveStrategy(bot, strategy);
        } else {
            return executeDefensiveStrategy(bot, strategy);
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Проверяет наличие подходящих целей для PvP
     * 
     * @param context контекст бота
     * @return true, если есть подходящие цели
     */
    private boolean hasSuitableTargets(BotContext context) {
        // TODO: Реализовать поиск подходящих целей
        // В реальной реализации здесь будет поиск игроков в радиусе
        return true; // Заглушка для тестирования
    }
    
    /**
     * Находит ближайших противников
     * 
     * @param bot бот для поиска
     * @return количество ближайших противников
     */
    private int findNearbyEnemies(EnhancedFakePlayer bot) {
        // TODO: Реализовать поиск противников
        return 0; // Заглушка для тестирования
    }
    
    /**
     * Находит ближайших союзников
     * 
     * @param bot бот для поиска
     * @return количество ближайших союзников
     */
    private int findNearbyAllies(EnhancedFakePlayer bot) {
        // TODO: Реализовать поиск союзников
        return 0; // Заглушка для тестирования
    }
    
    /**
     * Рассчитывает уровень опасности ситуации
     * 
     * @param situation анализ PvP ситуации
     * @return уровень опасности (0.0 - 1.0)
     */
    private double calculateDangerLevel(PvPSituation situation) {
        double danger = 0.0;
        
        // Учитываем здоровье бота
        danger += (1.0 - situation.botHealth) * 0.4;
        
        // Учитываем количество противников
        danger += Math.min(situation.nearbyEnemies * 0.2, 0.4);
        
        // Учитываем отсутствие союзников
        if (situation.nearbyAllies == 0) {
            danger += 0.2;
        }
        
        return Math.min(1.0, danger);
    }
    
    /**
     * Находит лучшую цель для атаки
     * 
     * @param bot бот для поиска цели
     * @return лучшая цель для атаки или null
     */
    private EnhancedFakePlayer findBestTarget(EnhancedFakePlayer bot) {
        // TODO: Реализовать поиск лучшей цели
        return null; // Заглушка для тестирования
    }
    
    /**
     * Атакует выбранную цель
     * 
     * @param bot бот-атакующий
     * @param target цель для атаки
     * @return true, если атака была успешной
     */
    private boolean attackTarget(EnhancedFakePlayer bot, EnhancedFakePlayer target) {
        // TODO: Реализовать атаку цели
        return true; // Заглушка для тестирования
    }
    
    /**
     * Использует навык лечения
     * 
     * @param bot бот для лечения
     * @return true, если лечение было успешным
     */
    private boolean useHealingSkill(EnhancedFakePlayer bot) {
        // TODO: Реализовать использование навыка лечения
        return true; // Заглушка для тестирования
    }
    
    /**
     * Отступает в безопасное место
     * 
     * @param bot бот для отступления
     * @return true, если отступление было успешным
     */
    private boolean retreatToSafety(EnhancedFakePlayer bot) {
        // TODO: Реализовать отступление
        return true; // Заглушка для тестирования
    }
    
    // ==================== ОБРАТНАЯ СВЯЗЬ И АДАПТАЦИЯ ====================
    
    /**
     * Записывает обратную связь о выполнении PvP стратегии
     * 
     * @param bot бот, для которого записывается обратная связь
     * @param strategy выполненная стратегия
     * @param success успешность выполнения
     */
    private void recordFeedback(EnhancedFakePlayer bot, PvPStrategy strategy, boolean success) {
        try {
            // Записываем обратную связь о действии
            feedbackCollector.recordAction(
                bot,
                net.sf.l2j.botmanager.actions.ActionType.ATTACK,
                success,
                System.currentTimeMillis() - lastPvPActionTime,
                "PvP_" + strategy.type.name()
            );
            
            // Записываем обратную связь о поведении
            feedbackCollector.recordBehavior(
                bot,
                BehaviorType.PVP,
                success,
                System.currentTimeMillis() - lastPvPActionTime,
                1, // количество действий
                "AdaptivePvPBehavior"
            );
            
            // Обновляем статистику
            if (success) {
                successfulBattles++;
            } else {
                failedBattles++;
            }
            
        } catch (Exception e) {
            Logger.getLogger(AdaptivePvPBehavior.class).error("Error recording PvP feedback: " + e.getMessage(), e);
        }
    }
    
    /**
     * Адаптирует параметры PvP поведения на основе результатов
     * 
     * @param bot бот для адаптации
     * @param strategy выполненная стратегия
     * @param success успешность выполнения
     */
    private void adaptParameters(EnhancedFakePlayer bot, PvPStrategy strategy, boolean success) {
        try {
            // Получаем рекомендации от адаптивного алгоритма
            AdaptiveAlgorithm.BotAdaptationParams recommendations = 
                adaptiveAlgorithm.getBotParams(bot.getBotId());
            
            if (recommendations != null) {
                // Применяем рекомендации
                this.actionPriority = recommendations.getActionPriority();
                this.behaviorPriority = recommendations.getBehaviorPriority();
                this.actionFrequency = recommendations.getActionFrequency();
                
                // Адаптируем специфичные для PvP параметры
                if (success) {
                    // Увеличиваем агрессивность при успехе
                    this.aggressiveness = Math.min(1.0, this.aggressiveness + 0.05);
                } else {
                    // Увеличиваем осторожность при неудаче
                    this.caution = Math.min(1.0, this.caution + 0.05);
                }
                
                // Обновляем адаптивные параметры
                this.adaptationParams = recommendations;
            }
            
        } catch (Exception e) {
            Logger.getLogger(AdaptivePvPBehavior.class).error("Error adapting PvP parameters: " + e.getMessage(), e);
        }
    }
    
    // ==================== СТАТИСТИКА И МОНИТОРИНГ ====================
    
    /**
     * Возвращает статистику PvP поведения
     * 
     * @return строка со статистикой поведения
     */
    @Override
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        
        stats.append("=== AdaptivePvPBehavior Statistics ===\n");
        stats.append("Successful Battles: ").append(successfulBattles).append("\n");
        stats.append("Failed Battles: ").append(failedBattles).append("\n");
        stats.append("Success Rate: ").append(calculateSuccessRate()).append("%\n");
        stats.append("Total PvP Time: ").append(totalPvPTime).append("ms\n");
        stats.append("\n");
        
        stats.append("=== PvP Parameters ===\n");
        stats.append("Min Target Level: ").append(minTargetLevel).append("\n");
        stats.append("Max Target Level: ").append(maxTargetLevel).append("\n");
        stats.append("Action Priority: ").append(String.format("%.2f", actionPriority)).append("\n");
        stats.append("Behavior Priority: ").append(String.format("%.2f", behaviorPriority)).append("\n");
        stats.append("Action Frequency: ").append(String.format("%.2f", actionFrequency)).append("\n");
        stats.append("Aggressiveness: ").append(String.format("%.2f", aggressiveness)).append("\n");
        stats.append("Caution: ").append(String.format("%.2f", caution)).append("\n");
        stats.append("Combat Strategy: ").append(String.format("%.2f", combatStrategy)).append("\n");
        stats.append("\n");
        
        if (adaptationParams != null) {
            stats.append("=== Adaptive Parameters ===\n");
            stats.append("Action Priority: ").append(String.format("%.2f", adaptationParams.getActionPriority())).append("\n");
            stats.append("Behavior Priority: ").append(String.format("%.2f", adaptationParams.getBehaviorPriority())).append("\n");
            stats.append("Action Frequency: ").append(String.format("%.2f", adaptationParams.getActionFrequency())).append("\n");
            stats.append("Learning Rate: ").append(String.format("%.2f", adaptationParams.getLearningRate())).append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Рассчитывает процент успешности PvP боев
     * 
     * @return процент успешности (0.0 - 100.0)
     */
    private double calculateSuccessRate() {
        int totalBattles = successfulBattles + failedBattles;
        if (totalBattles == 0) {
            return 0.0;
        }
        return (successfulBattles * 100.0) / totalBattles;
    }
    
    // ==================== GETTERS И SETTERS ====================
    
    /**
     * Устанавливает минимальный уровень цели для атаки
     * 
     * @param minTargetLevel минимальный уровень цели
     */
    public void setMinTargetLevel(int minTargetLevel) {
        this.minTargetLevel = Math.max(1, minTargetLevel);
    }
    
    /**
     * Возвращает минимальный уровень цели для атаки
     * 
     * @return минимальный уровень цели
     */
    public int getMinTargetLevel() {
        return minTargetLevel;
    }
    
    /**
     * Устанавливает максимальный уровень цели для атаки
     * 
     * @param maxTargetLevel максимальный уровень цели
     */
    public void setMaxTargetLevel(int maxTargetLevel) {
        this.maxTargetLevel = Math.max(1, maxTargetLevel);
    }
    
    /**
     * Возвращает максимальный уровень цели для атаки
     * 
     * @return максимальный уровень цели
     */
    public int getMaxTargetLevel() {
        return maxTargetLevel;
    }
    
    /**
     * Устанавливает агрессивность поведения
     * 
     * @param aggressiveness агрессивность (0.0 - 1.0)
     */
    public void setAggressiveness(double aggressiveness) {
        this.aggressiveness = Math.max(0.0, Math.min(1.0, aggressiveness));
    }
    
    /**
     * Возвращает агрессивность поведения
     * 
     * @return агрессивность (0.0 - 1.0)
     */
    public double getAggressiveness() {
        return aggressiveness;
    }
    
    /**
     * Устанавливает осторожность поведения
     * 
     * @param caution осторожность (0.0 - 1.0)
     */
    public void setCaution(double caution) {
        this.caution = Math.max(0.0, Math.min(1.0, caution));
    }
    
    /**
     * Возвращает осторожность поведения
     * 
     * @return осторожность (0.0 - 1.0)
     */
    public double getCaution() {
        return caution;
    }
    
    /**
     * Устанавливает стратегию боя
     * 
     * @param combatStrategy стратегия боя (0.0 - 1.0)
     */
    public void setCombatStrategy(double combatStrategy) {
        this.combatStrategy = Math.max(0.0, Math.min(1.0, combatStrategy));
    }
    
    /**
     * Возвращает стратегию боя
     * 
     * @return стратегия боя (0.0 - 1.0)
     */
    public double getCombatStrategy() {
        return combatStrategy;
    }
    
    /**
     * Устанавливает адаптивные параметры
     * 
     * @param adaptationParams адаптивные параметры
     */
    public void setAdaptationParams(AdaptiveAlgorithm.BotAdaptationParams adaptationParams) {
        this.adaptationParams = adaptationParams;
    }
    
    /**
     * Возвращает адаптивные параметры
     * 
     * @return адаптивные параметры
     */
    public AdaptiveAlgorithm.BotAdaptationParams getAdaptationParams() {
        return adaptationParams;
    }
    
    // ==================== ВНУТРЕННИЕ КЛАССЫ ====================
    
    /**
     * Анализ PvP ситуации
     */
    private static class PvPSituation {
        double botHealth;
        double botMana;
        int botLevel;
        int nearbyEnemies;
        int nearbyAllies;
        double dangerLevel;
    }
    
    /**
     * PvP стратегия
     */
    private static class PvPStrategy {
        PvPStrategyType type;
        double aggressiveness;
        double caution;
        double combatStrategy;
    }
    
    /**
     * Типы PvP стратегий
     */
    private enum PvPStrategyType {
        AGGRESSIVE,  // Агрессивная стратегия
        DEFENSIVE,   // Оборонительная стратегия
        BALANCED     // Сбалансированная стратегия
    }
}
