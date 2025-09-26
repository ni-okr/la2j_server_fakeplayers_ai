package net.sf.l2j.botmanager.behaviors;

import net.sf.l2j.botmanager.core.BotContext;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.learning.FeedbackCollector;
import net.sf.l2j.botmanager.learning.PerformanceAnalyzer;
import net.sf.l2j.botmanager.learning.AdaptiveAlgorithm;
import net.sf.l2j.botmanager.utils.Logger;

/**
 * Адаптивное торговое поведение для ботов
 * 
 * Это поведение обеспечивает интеллектуальную торговлю с NPC и другими игроками,
 * включая адаптивные стратегии торговли, анализ рынка и автоматическую настройку
 * параметров на основе обратной связи от системы обучения.
 * 
 * Ключевые возможности:
 * - Адаптивные стратегии торговли
 * - Анализ рынка и цен
 * - Интеграция с системой обучения
 * - Автоматическая адаптация торговых параметров
 * - Статистика и мониторинг производительности
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class AdaptiveTradingBehavior extends AbstractBehavior {
    
    // ==================== АДАПТИВНЫЕ ПАРАМЕТРЫ ТОРГОВЛИ ====================
    
    /**
     * Минимальная сумма для торговли
     * Адаптируется на основе успешности торговых операций
     */
    private long minTradeAmount = 1000;
    
    /**
     * Максимальная сумма для торговли
     * Адаптируется на основе успешности торговых операций
     */
    private long maxTradeAmount = 100000;
    
    /**
     * Приоритет торговых действий
     * Влияет на частоту выбора торговых действий
     */
    private double actionPriority = 1.0;
    
    /**
     * Приоритет торгового поведения
     * Влияет на общий приоритет поведения
     */
    private double behaviorPriority = 1.0;
    
    /**
     * Частота торговых действий
     * Влияет на интервал между торговыми действиями
     */
    private double actionFrequency = 1.0;
    
    /**
     * Агрессивность торговли
     * Определяет склонность к рискованным сделкам
     */
    private double tradingAggressiveness = 0.5;
    
    /**
     * Осторожность в торговле
     * Определяет склонность к безопасным сделкам
     */
    private double tradingCaution = 0.5;
    
    /**
     * Стратегия торговли
     * 0.0 - покупка, 1.0 - продажа
     */
    private double tradingStrategy = 0.5;
    
    /**
     * Процент наценки при продаже
     * Адаптируется на основе рыночных условий
     */
    private double markupPercentage = 0.1; // 10%
    
    /**
     * Процент скидки при покупке
     * Адаптируется на основе рыночных условий
     */
    private double discountPercentage = 0.05; // 5%
    
    // ==================== СИСТЕМА ОБУЧЕНИЯ ====================
    
    /**
     * Сборщик обратной связи для анализа эффективности торговых операций
     */
    private final FeedbackCollector feedbackCollector;
    
    /**
     * Анализатор производительности для оценки эффективности торговых стратегий
     */
    private final PerformanceAnalyzer performanceAnalyzer;
    
    /**
     * Адаптивный алгоритм для автоматической настройки торговых параметров
     */
    private final AdaptiveAlgorithm adaptiveAlgorithm;
    
    /**
     * Адаптивные параметры, полученные от системы обучения
     */
    private AdaptiveAlgorithm.BotAdaptationParams adaptationParams;
    
    // ==================== СТАТИСТИКА И МОНИТОРИНГ ====================
    
    /**
     * Количество успешных торговых операций
     */
    private int successfulTrades = 0;
    
    /**
     * Количество неудачных торговых операций
     */
    private int failedTrades = 0;
    
    /**
     * Общая прибыль от торговли
     */
    private long totalProfit = 0;
    
    /**
     * Общая сумма торговых операций
     */
    private long totalTradeVolume = 0;
    
    /**
     * Время последней торговой операции
     */
    private long lastTradeTime = 0;
    
    /**
     * Минимальный интервал между торговыми операциями (в миллисекундах)
     */
    private long minExecutionInterval = 10000; // 10 секунд
    
    /**
     * Максимальный интервал между торговыми операциями (в миллисекундах)
     */
    private long maxExecutionInterval = 300000; // 5 минут
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор адаптивного торгового поведения
     * 
     * Инициализирует поведение с базовыми параметрами и подключает
     * систему обучения для адаптации стратегий.
     */
    public AdaptiveTradingBehavior() {
        super(BehaviorType.TRADING);
        
        // Инициализация системы обучения
        this.feedbackCollector = FeedbackCollector.getInstance();
        this.performanceAnalyzer = PerformanceAnalyzer.getInstance();
        this.adaptiveAlgorithm = AdaptiveAlgorithm.getInstance();
        
        // Инициализация адаптивных параметров
        this.adaptationParams = new AdaptiveAlgorithm.BotAdaptationParams();
        
        Logger.getLogger(AdaptiveTradingBehavior.class).info("AdaptiveTradingBehavior initialized");
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
        
        // Проверяем интервал между торговыми операциями
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTradeTime < minExecutionInterval) {
            return false;
        }
        
        // Проверяем, есть ли подходящие торговые возможности
        return hasTradingOpportunities(context);
    }
    
    /**
     * Выполняет адаптивное торговое поведение
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
            // Обновляем время последней торговой операции
            lastTradeTime = System.currentTimeMillis();
            
            // Анализируем текущую торговую ситуацию
            TradingSituation situation = analyzeTradingSituation(bot);
            
            // Выбираем стратегию на основе анализа
            TradingStrategy strategy = selectStrategy(situation);
            
            // Выполняем выбранную стратегию
            boolean success = executeStrategy(bot, strategy);
            
            // Записываем обратную связь
            recordFeedback(bot, strategy, success);
            
            // Адаптируем параметры на основе результатов
            adaptParameters(bot, strategy, success);
            
            return success;
            
        } catch (Exception e) {
            Logger.getLogger(AdaptiveTradingBehavior.class).error("Error executing AdaptiveTradingBehavior: " + e.getMessage(), e);
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
        
        // Базовый приоритет торгового поведения
        double basePriority = 0.6;
        
        // Учитываем агрессивность торговли
        basePriority *= tradingAggressiveness;
        
        // Учитываем наличие торговых возможностей
        if (hasTradingOpportunities(context)) {
            basePriority *= 1.3;
        }
        
        // Учитываем адаптивные параметры
        if (adaptationParams != null) {
            basePriority *= adaptationParams.getBehaviorPriority();
        }
        
        return Math.min(1.0, basePriority);
    }
    
    // ==================== АНАЛИЗ И СТРАТЕГИИ ====================
    
    /**
     * Анализирует текущую торговую ситуацию
     * 
     * @param bot бот для анализа
     * @return анализ торговой ситуации
     */
    private TradingSituation analyzeTradingSituation(EnhancedFakePlayer bot) {
        TradingSituation situation = new TradingSituation();
        
        // Анализируем текущие деньги бота
        situation.currentMoney = getBotMoney(bot);
        
        // Анализируем инвентарь бота
        situation.inventorySpace = getInventorySpace(bot);
        situation.valuableItems = getValuableItems(bot);
        
        // Анализируем ближайших торговцев
        situation.nearbyTraders = findNearbyTraders(bot);
        
        // Анализируем рыночные цены
        situation.marketPrices = analyzeMarketPrices(bot);
        
        // Определяем уровень торговых возможностей
        situation.tradingOpportunityLevel = calculateTradingOpportunityLevel(situation);
        
        return situation;
    }
    
    /**
     * Выбирает стратегию торговли на основе анализа ситуации
     * 
     * @param situation анализ торговой ситуации
     * @return выбранная стратегия
     */
    private TradingStrategy selectStrategy(TradingSituation situation) {
        TradingStrategy strategy = new TradingStrategy();
        
        // Выбираем тип стратегии на основе ситуации
        if (situation.tradingOpportunityLevel > 0.8) {
            strategy.type = TradingStrategyType.AGGRESSIVE;
        } else if (situation.tradingOpportunityLevel < 0.3) {
            strategy.type = TradingStrategyType.CONSERVATIVE;
        } else {
            strategy.type = TradingStrategyType.BALANCED;
        }
        
        // Настраиваем параметры стратегии
        strategy.aggressiveness = this.tradingAggressiveness;
        strategy.caution = this.tradingCaution;
        strategy.tradingStrategy = this.tradingStrategy;
        strategy.markupPercentage = this.markupPercentage;
        strategy.discountPercentage = this.discountPercentage;
        
        // Учитываем адаптивные параметры
        if (adaptationParams != null) {
            strategy.aggressiveness *= adaptationParams.getActionPriority();
            strategy.caution *= adaptationParams.getBehaviorPriority();
        }
        
        return strategy;
    }
    
    /**
     * Выполняет выбранную торговую стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy стратегия для выполнения
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeStrategy(EnhancedFakePlayer bot, TradingStrategy strategy) {
        try {
            switch (strategy.type) {
                case AGGRESSIVE:
                    return executeAggressiveStrategy(bot, strategy);
                case CONSERVATIVE:
                    return executeConservativeStrategy(bot, strategy);
                case BALANCED:
                    return executeBalancedStrategy(bot, strategy);
                default:
                    return false;
            }
        } catch (Exception e) {
            Logger.getLogger(AdaptiveTradingBehavior.class).error("Error executing trading strategy: " + e.getMessage(), e);
            return false;
        }
    }
    
    // ==================== СТРАТЕГИИ ВЫПОЛНЕНИЯ ====================
    
    /**
     * Выполняет агрессивную торговую стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy параметры стратегии
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeAggressiveStrategy(EnhancedFakePlayer bot, TradingStrategy strategy) {
        // Ищем высокоприбыльные торговые возможности
        TradingOpportunity opportunity = findHighProfitOpportunity(bot, strategy);
        if (opportunity == null) {
            return false;
        }
        
        // Выполняем сделку
        return executeTrade(bot, opportunity);
    }
    
    /**
     * Выполняет консервативную торговую стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy параметры стратегии
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeConservativeStrategy(EnhancedFakePlayer bot, TradingStrategy strategy) {
        // Ищем безопасные торговые возможности
        TradingOpportunity opportunity = findSafeOpportunity(bot, strategy);
        if (opportunity == null) {
            return false;
        }
        
        // Выполняем сделку
        return executeTrade(bot, opportunity);
    }
    
    /**
     * Выполняет сбалансированную торговую стратегию
     * 
     * @param bot бот для выполнения стратегии
     * @param strategy параметры стратегии
     * @return true, если стратегия была успешно выполнена
     */
    private boolean executeBalancedStrategy(EnhancedFakePlayer bot, TradingStrategy strategy) {
        // Чередуем между агрессивными и консервативными подходами
        if (Math.random() < strategy.aggressiveness) {
            return executeAggressiveStrategy(bot, strategy);
        } else {
            return executeConservativeStrategy(bot, strategy);
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Проверяет наличие торговых возможностей
     * 
     * @param context контекст бота
     * @return true, если есть торговые возможности
     */
    private boolean hasTradingOpportunities(BotContext context) {
        // TODO: Реализовать поиск торговых возможностей
        // В реальной реализации здесь будет поиск NPC торговцев и игроков
        return true; // Заглушка для тестирования
    }
    
    /**
     * Получает текущие деньги бота
     * 
     * @param bot бот для получения денег
     * @return количество денег
     */
    private long getBotMoney(EnhancedFakePlayer bot) {
        // TODO: Реализовать получение денег бота
        return 10000; // Заглушка для тестирования
    }
    
    /**
     * Получает свободное место в инвентаре
     * 
     * @param bot бот для проверки инвентаря
     * @return количество свободных слотов
     */
    private int getInventorySpace(EnhancedFakePlayer bot) {
        // TODO: Реализовать проверку инвентаря
        return 10; // Заглушка для тестирования
    }
    
    /**
     * Получает ценные предметы в инвентаре
     * 
     * @param bot бот для проверки предметов
     * @return количество ценных предметов
     */
    private int getValuableItems(EnhancedFakePlayer bot) {
        // TODO: Реализовать поиск ценных предметов
        return 5; // Заглушка для тестирования
    }
    
    /**
     * Находит ближайших торговцев
     * 
     * @param bot бот для поиска
     * @return количество ближайших торговцев
     */
    private int findNearbyTraders(EnhancedFakePlayer bot) {
        // TODO: Реализовать поиск торговцев
        return 2; // Заглушка для тестирования
    }
    
    /**
     * Анализирует рыночные цены
     * 
     * @param bot бот для анализа
     * @return информация о рыночных ценах
     */
    private MarketPrices analyzeMarketPrices(EnhancedFakePlayer bot) {
        // TODO: Реализовать анализ рыночных цен
        return new MarketPrices(); // Заглушка для тестирования
    }
    
    /**
     * Рассчитывает уровень торговых возможностей
     * 
     * @param situation анализ торговой ситуации
     * @return уровень возможностей (0.0 - 1.0)
     */
    private double calculateTradingOpportunityLevel(TradingSituation situation) {
        double opportunity = 0.0;
        
        // Учитываем наличие денег
        if (situation.currentMoney > minTradeAmount) {
            opportunity += 0.3;
        }
        
        // Учитываем наличие ценных предметов
        if (situation.valuableItems > 0) {
            opportunity += 0.2;
        }
        
        // Учитываем наличие торговцев
        if (situation.nearbyTraders > 0) {
            opportunity += 0.3;
        }
        
        // Учитываем свободное место в инвентаре
        if (situation.inventorySpace > 0) {
            opportunity += 0.2;
        }
        
        return Math.min(1.0, opportunity);
    }
    
    /**
     * Находит высокоприбыльную торговую возможность
     * 
     * @param bot бот для поиска
     * @param strategy стратегия торговли
     * @return торговая возможность или null
     */
    private TradingOpportunity findHighProfitOpportunity(EnhancedFakePlayer bot, TradingStrategy strategy) {
        // TODO: Реализовать поиск высокоприбыльных возможностей
        return new TradingOpportunity(); // Заглушка для тестирования
    }
    
    /**
     * Находит безопасную торговую возможность
     * 
     * @param bot бот для поиска
     * @param strategy стратегия торговли
     * @return торговая возможность или null
     */
    private TradingOpportunity findSafeOpportunity(EnhancedFakePlayer bot, TradingStrategy strategy) {
        // TODO: Реализовать поиск безопасных возможностей
        return new TradingOpportunity(); // Заглушка для тестирования
    }
    
    /**
     * Выполняет торговую сделку
     * 
     * @param bot бот для выполнения сделки
     * @param opportunity торговая возможность
     * @return true, если сделка была успешной
     */
    private boolean executeTrade(EnhancedFakePlayer bot, TradingOpportunity opportunity) {
        // TODO: Реализовать выполнение торговой сделки
        return true; // Заглушка для тестирования
    }
    
    // ==================== ОБРАТНАЯ СВЯЗЬ И АДАПТАЦИЯ ====================
    
    /**
     * Записывает обратную связь о выполнении торговой стратегии
     * 
     * @param bot бот, для которого записывается обратная связь
     * @param strategy выполненная стратегия
     * @param success успешность выполнения
     */
    private void recordFeedback(EnhancedFakePlayer bot, TradingStrategy strategy, boolean success) {
        try {
            // Записываем обратную связь о действии
            feedbackCollector.recordAction(
                bot,
                net.sf.l2j.botmanager.actions.ActionType.MOVE,
                success,
                System.currentTimeMillis() - lastTradeTime,
                "Trading_" + strategy.type.name()
            );
            
            // Записываем обратную связь о поведении
            feedbackCollector.recordBehavior(
                bot,
                BehaviorType.TRADING,
                success,
                System.currentTimeMillis() - lastTradeTime,
                1, // количество действий
                "AdaptiveTradingBehavior"
            );
            
            // Обновляем статистику
            if (success) {
                successfulTrades++;
                // TODO: Обновить прибыль и объем торговли
            } else {
                failedTrades++;
            }
            
        } catch (Exception e) {
            Logger.getLogger(AdaptiveTradingBehavior.class).error("Error recording trading feedback: " + e.getMessage(), e);
        }
    }
    
    /**
     * Адаптирует параметры торгового поведения на основе результатов
     * 
     * @param bot бот для адаптации
     * @param strategy выполненная стратегия
     * @param success успешность выполнения
     */
    private void adaptParameters(EnhancedFakePlayer bot, TradingStrategy strategy, boolean success) {
        try {
            // Получаем рекомендации от адаптивного алгоритма
            AdaptiveAlgorithm.BotAdaptationParams recommendations = 
                adaptiveAlgorithm.getBotParams(bot.getBotId());
            
            if (recommendations != null) {
                // Применяем рекомендации
                this.actionPriority = recommendations.getActionPriority();
                this.behaviorPriority = recommendations.getBehaviorPriority();
                this.actionFrequency = recommendations.getActionFrequency();
                
                // Адаптируем специфичные для торговли параметры
                if (success) {
                    // Увеличиваем агрессивность при успехе
                    this.tradingAggressiveness = Math.min(1.0, this.tradingAggressiveness + 0.05);
                } else {
                    // Увеличиваем осторожность при неудаче
                    this.tradingCaution = Math.min(1.0, this.tradingCaution + 0.05);
                }
                
                // Обновляем адаптивные параметры
                this.adaptationParams = recommendations;
            }
            
        } catch (Exception e) {
            Logger.getLogger(AdaptiveTradingBehavior.class).error("Error adapting trading parameters: " + e.getMessage(), e);
        }
    }
    
    // ==================== СТАТИСТИКА И МОНИТОРИНГ ====================
    
    /**
     * Возвращает статистику торгового поведения
     * 
     * @return строка со статистикой поведения
     */
    @Override
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        
        stats.append("=== AdaptiveTradingBehavior Statistics ===\n");
        stats.append("Successful Trades: ").append(successfulTrades).append("\n");
        stats.append("Failed Trades: ").append(failedTrades).append("\n");
        stats.append("Success Rate: ").append(calculateSuccessRate()).append("%\n");
        stats.append("Total Profit: ").append(totalProfit).append(" adena\n");
        stats.append("Total Trade Volume: ").append(totalTradeVolume).append(" adena\n");
        stats.append("\n");
        
        stats.append("=== Trading Parameters ===\n");
        stats.append("Min Trade Amount: ").append(minTradeAmount).append(" adena\n");
        stats.append("Max Trade Amount: ").append(maxTradeAmount).append(" adena\n");
        stats.append("Action Priority: ").append(String.format("%.2f", actionPriority)).append("\n");
        stats.append("Behavior Priority: ").append(String.format("%.2f", behaviorPriority)).append("\n");
        stats.append("Action Frequency: ").append(String.format("%.2f", actionFrequency)).append("\n");
        stats.append("Trading Aggressiveness: ").append(String.format("%.2f", tradingAggressiveness)).append("\n");
        stats.append("Trading Caution: ").append(String.format("%.2f", tradingCaution)).append("\n");
        stats.append("Trading Strategy: ").append(String.format("%.2f", tradingStrategy)).append("\n");
        stats.append("Markup Percentage: ").append(String.format("%.1f%%", markupPercentage * 100)).append("\n");
        stats.append("Discount Percentage: ").append(String.format("%.1f%%", discountPercentage * 100)).append("\n");
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
     * Рассчитывает процент успешности торговых операций
     * 
     * @return процент успешности (0.0 - 100.0)
     */
    private double calculateSuccessRate() {
        int totalTrades = successfulTrades + failedTrades;
        if (totalTrades == 0) {
            return 0.0;
        }
        return (successfulTrades * 100.0) / totalTrades;
    }
    
    // ==================== GETTERS И SETTERS ====================
    
    /**
     * Устанавливает минимальную сумму для торговли
     * 
     * @param minTradeAmount минимальная сумма
     */
    public void setMinTradeAmount(long minTradeAmount) {
        this.minTradeAmount = Math.max(0, minTradeAmount);
    }
    
    /**
     * Возвращает минимальную сумму для торговли
     * 
     * @return минимальная сумма
     */
    public long getMinTradeAmount() {
        return minTradeAmount;
    }
    
    /**
     * Устанавливает максимальную сумму для торговли
     * 
     * @param maxTradeAmount максимальная сумма
     */
    public void setMaxTradeAmount(long maxTradeAmount) {
        this.maxTradeAmount = Math.max(0, maxTradeAmount);
    }
    
    /**
     * Возвращает максимальную сумму для торговли
     * 
     * @return максимальная сумма
     */
    public long getMaxTradeAmount() {
        return maxTradeAmount;
    }
    
    /**
     * Устанавливает агрессивность торговли
     * 
     * @param tradingAggressiveness агрессивность (0.0 - 1.0)
     */
    public void setTradingAggressiveness(double tradingAggressiveness) {
        this.tradingAggressiveness = Math.max(0.0, Math.min(1.0, tradingAggressiveness));
    }
    
    /**
     * Возвращает агрессивность торговли
     * 
     * @return агрессивность (0.0 - 1.0)
     */
    public double getTradingAggressiveness() {
        return tradingAggressiveness;
    }
    
    /**
     * Устанавливает осторожность в торговле
     * 
     * @param tradingCaution осторожность (0.0 - 1.0)
     */
    public void setTradingCaution(double tradingCaution) {
        this.tradingCaution = Math.max(0.0, Math.min(1.0, tradingCaution));
    }
    
    /**
     * Возвращает осторожность в торговле
     * 
     * @return осторожность (0.0 - 1.0)
     */
    public double getTradingCaution() {
        return tradingCaution;
    }
    
    /**
     * Устанавливает стратегию торговли
     * 
     * @param tradingStrategy стратегия торговли (0.0 - 1.0)
     */
    public void setTradingStrategy(double tradingStrategy) {
        this.tradingStrategy = Math.max(0.0, Math.min(1.0, tradingStrategy));
    }
    
    /**
     * Возвращает стратегию торговли
     * 
     * @return стратегия торговли (0.0 - 1.0)
     */
    public double getTradingStrategy() {
        return tradingStrategy;
    }
    
    /**
     * Устанавливает процент наценки
     * 
     * @param markupPercentage процент наценки (0.0 - 1.0)
     */
    public void setMarkupPercentage(double markupPercentage) {
        this.markupPercentage = Math.max(0.0, Math.min(1.0, markupPercentage));
    }
    
    /**
     * Возвращает процент наценки
     * 
     * @return процент наценки (0.0 - 1.0)
     */
    public double getMarkupPercentage() {
        return markupPercentage;
    }
    
    /**
     * Устанавливает процент скидки
     * 
     * @param discountPercentage процент скидки (0.0 - 1.0)
     */
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = Math.max(0.0, Math.min(1.0, discountPercentage));
    }
    
    /**
     * Возвращает процент скидки
     * 
     * @return процент скидки (0.0 - 1.0)
     */
    public double getDiscountPercentage() {
        return discountPercentage;
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
     * Анализ торговой ситуации
     */
    private static class TradingSituation {
        long currentMoney;
        int inventorySpace;
        int valuableItems;
        int nearbyTraders;
        MarketPrices marketPrices;
        double tradingOpportunityLevel;
    }
    
    /**
     * Торговая стратегия
     */
    private static class TradingStrategy {
        TradingStrategyType type;
        double aggressiveness;
        double caution;
        double tradingStrategy;
        double markupPercentage;
        double discountPercentage;
    }
    
    /**
     * Типы торговых стратегий
     */
    private enum TradingStrategyType {
        AGGRESSIVE,  // Агрессивная стратегия
        CONSERVATIVE, // Консервативная стратегия
        BALANCED     // Сбалансированная стратегия
    }
    
    /**
     * Рыночные цены
     */
    private static class MarketPrices {
        // TODO: Добавить поля для рыночных цен
    }
    
    /**
     * Торговая возможность
     */
    private static class TradingOpportunity {
        // TODO: Добавить поля для торговой возможности
    }
}
