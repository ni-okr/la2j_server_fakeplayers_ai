package net.sf.l2j.botmanager.learning;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Базовый адаптивный алгоритм для обучения ботов.
 * 
 * <p>Адаптивный алгоритм использует данные обратной связи для автоматического
 * улучшения поведения ботов. Алгоритм анализирует успешность действий и
 * поведений, корректирует параметры и предлагает оптимизации.</p>
 * 
 * <p>Основные принципы работы:</p>
 * <ul>
 *   <li>Анализ обратной связи в реальном времени</li>
 *   <li>Корректировка параметров на основе результатов</li>
 *   <li>Обучение на исторических данных</li>
 *   <li>Предотвращение деградации производительности</li>
 *   <li>Адаптация к изменяющимся условиям</li>
 * </ul>
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
public class AdaptiveAlgorithm {
    
    /** Единственный экземпляр алгоритма (Singleton) */
    private static volatile AdaptiveAlgorithm instance;
    
    /** Коллектор обратной связи */
    private final FeedbackCollector feedbackCollector;
    
    /** Анализатор производительности */
    private final PerformanceAnalyzer performanceAnalyzer;
    
    /** Параметры адаптации для каждого бота */
    private final Map<Integer, BotAdaptationParams> botParams = new ConcurrentHashMap<>();
    
    /** История адаптаций для анализа эффективности */
    private final Map<Integer, List<AdaptationRecord>> adaptationHistory = new ConcurrentHashMap<>();
    
    /** Коэффициент обучения (скорость адаптации) */
    private double learningRate = 0.1;
    
    /** Минимальное количество данных для адаптации */
    private int minDataPoints = 20;
    
    /** Порог для определения значимых изменений */
    private double significanceThreshold = 0.05;
    
    /** Максимальное количество адаптаций в час */
    private int maxAdaptationsPerHour = 10;
    
    /** Счетчик адаптаций по часам */
    private final Map<String, AtomicLong> adaptationCounts = new ConcurrentHashMap<>();
    
    /**
     * Приватный конструктор для Singleton паттерна.
     * 
     * @param feedbackCollector коллектор обратной связи
     * @param performanceAnalyzer анализатор производительности
     */
    private AdaptiveAlgorithm(FeedbackCollector feedbackCollector, PerformanceAnalyzer performanceAnalyzer) {
        this.feedbackCollector = feedbackCollector;
        this.performanceAnalyzer = performanceAnalyzer;
    }
    
    /**
     * Получить единственный экземпляр алгоритма.
     * 
     * @param feedbackCollector коллектор обратной связи
     * @param performanceAnalyzer анализатор производительности
     * @return экземпляр алгоритма
     */
    public static AdaptiveAlgorithm getInstance(FeedbackCollector feedbackCollector, PerformanceAnalyzer performanceAnalyzer) {
        if (instance == null) {
            synchronized (AdaptiveAlgorithm.class) {
                if (instance == null) {
                    instance = new AdaptiveAlgorithm(feedbackCollector, performanceAnalyzer);
                }
            }
        }
        return instance;
    }
    
    /**
     * Получить единственный экземпляр алгоритма (если уже создан).
     * 
     * @return экземпляр алгоритма или null, если не создан
     */
    public static AdaptiveAlgorithm getInstance() {
        return instance;
    }
    
    /**
     * Адаптировать параметры бота на основе обратной связи.
     * 
     * @param botId идентификатор бота
     * @return результат адаптации
     */
    public AdaptationResult adaptBot(int botId) {
        // Проверяем лимит адаптаций
        if (!canAdapt(botId)) {
            return new AdaptationResult(botId, false, "Превышен лимит адаптаций в час");
        }
        
        // Получаем текущие параметры
        BotAdaptationParams currentParams = getBotParams(botId);
        
        // Получаем данные обратной связи
        ActionFeedback actionFeedback = feedbackCollector.getActionFeedback(botId);
        BehaviorFeedback behaviorFeedback = feedbackCollector.getBehaviorFeedback(botId);
        
        if (actionFeedback == null && behaviorFeedback == null) {
            return new AdaptationResult(botId, false, "Недостаточно данных для адаптации");
        }
        
        // Выполняем адаптацию
        BotAdaptationParams newParams = performAdaptation(botId, currentParams, actionFeedback, behaviorFeedback);
        
        // Сохраняем новые параметры
        botParams.put(botId, newParams);
        
        // Записываем адаптацию в историю
        recordAdaptation(botId, currentParams, newParams, actionFeedback, behaviorFeedback);
        
        // Увеличиваем счетчик адаптаций
        incrementAdaptationCount(botId);
        
        return new AdaptationResult(botId, true, "Адаптация выполнена успешно");
    }
    
    /**
     * Адаптировать всех ботов.
     * 
     * @return карта результатов адаптации
     */
    public Map<Integer, AdaptationResult> adaptAllBots() {
        Map<Integer, AdaptationResult> results = new HashMap<>();
        
        Set<Integer> botIds = feedbackCollector.getAllBotIds();
        for (Integer botId : botIds) {
            results.put(botId, adaptBot(botId));
        }
        
        return results;
    }
    
    /**
     * Получить параметры адаптации для бота.
     * 
     * @param botId идентификатор бота
     * @return параметры адаптации
     */
    public BotAdaptationParams getBotParams(int botId) {
        return botParams.computeIfAbsent(botId, k -> new BotAdaptationParams());
    }
    
    /**
     * Установить параметры адаптации для бота.
     * 
     * @param botId идентификатор бота
     * @param params параметры адаптации
     */
    public void setBotParams(int botId, BotAdaptationParams params) {
        botParams.put(botId, params);
    }
    
    /**
     * Получить историю адаптаций для бота.
     * 
     * @param botId идентификатор бота
     * @return список записей адаптаций
     */
    public List<AdaptationRecord> getAdaptationHistory(int botId) {
        return adaptationHistory.getOrDefault(botId, new ArrayList<>());
    }
    
    /**
     * Получить статистику адаптаций.
     * 
     * @return статистика адаптаций
     */
    public String getAdaptationStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Adaptive Algorithm Statistics ===\n");
        stats.append("Learning Rate: ").append(learningRate).append("\n");
        stats.append("Min Data Points: ").append(minDataPoints).append("\n");
        stats.append("Significance Threshold: ").append(significanceThreshold).append("\n");
        stats.append("Max Adaptations Per Hour: ").append(maxAdaptationsPerHour).append("\n");
        stats.append("Bots with Parameters: ").append(botParams.size()).append("\n");
        stats.append("Total Adaptations: ").append(getTotalAdaptations()).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Сбросить параметры адаптации для бота.
     * 
     * @param botId идентификатор бота
     */
    public void resetBotParams(int botId) {
        botParams.remove(botId);
        adaptationHistory.remove(botId);
    }
    
    /**
     * Сбросить все параметры адаптации.
     */
    public void resetAllParams() {
        botParams.clear();
        adaptationHistory.clear();
        adaptationCounts.clear();
    }
    
    /**
     * Установить коэффициент обучения.
     * 
     * @param learningRate коэффициент обучения (0.0 - 1.0)
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = Math.max(0.0, Math.min(1.0, learningRate));
    }
    
    /**
     * Установить минимальное количество данных для адаптации.
     * 
     * @param minDataPoints минимальное количество данных
     */
    public void setMinDataPoints(int minDataPoints) {
        this.minDataPoints = Math.max(1, minDataPoints);
    }
    
    /**
     * Установить порог значимости.
     * 
     * @param significanceThreshold порог значимости (0.0 - 1.0)
     */
    public void setSignificanceThreshold(double significanceThreshold) {
        this.significanceThreshold = Math.max(0.0, Math.min(1.0, significanceThreshold));
    }
    
    /**
     * Выполнить адаптацию параметров.
     * 
     * @param botId идентификатор бота
     * @param currentParams текущие параметры
     * @param actionFeedback данные по действиям
     * @param behaviorFeedback данные по поведениям
     * @return новые параметры
     */
    private BotAdaptationParams performAdaptation(int botId, BotAdaptationParams currentParams, 
                                                 ActionFeedback actionFeedback, BehaviorFeedback behaviorFeedback) {
        BotAdaptationParams newParams = new BotAdaptationParams(currentParams);
        
        // Адаптация на основе действий
        if (actionFeedback != null && actionFeedback.getTotalExecutions() >= minDataPoints) {
            adaptActionParameters(newParams, actionFeedback);
        }
        
        // Адаптация на основе поведений
        if (behaviorFeedback != null && behaviorFeedback.getTotalExecutions() >= minDataPoints) {
            adaptBehaviorParameters(newParams, behaviorFeedback);
        }
        
        return newParams;
    }
    
    /**
     * Адаптировать параметры действий.
     * 
     * @param params параметры для адаптации
     * @param actionFeedback данные по действиям
     */
    private void adaptActionParameters(BotAdaptationParams params, ActionFeedback actionFeedback) {
        double successRate = actionFeedback.getSuccessRate() / 100.0;
        
        // Корректируем приоритет действий на основе успешности
        if (successRate > 0.8) {
            params.increaseActionPriority(learningRate * 0.1);
        } else if (successRate < 0.5) {
            params.decreaseActionPriority(learningRate * 0.2);
        }
        
        // Корректируем частоту выполнения действий
        if (successRate > 0.9) {
            params.increaseActionFrequency(learningRate * 0.05);
        } else if (successRate < 0.3) {
            params.decreaseActionFrequency(learningRate * 0.1);
        }
    }
    
    /**
     * Адаптировать параметры поведений.
     * 
     * @param params параметры для адаптации
     * @param behaviorFeedback данные по поведениям
     */
    private void adaptBehaviorParameters(BotAdaptationParams params, BehaviorFeedback behaviorFeedback) {
        double successRate = behaviorFeedback.getSuccessRate() / 100.0;
        
        // Корректируем приоритет поведений
        if (successRate > 0.8) {
            params.increaseBehaviorPriority(learningRate * 0.1);
        } else if (successRate < 0.5) {
            params.decreaseBehaviorPriority(learningRate * 0.2);
        }
        
        // Корректируем длительность поведений
        if (successRate > 0.9) {
            params.increaseBehaviorDuration(learningRate * 0.1);
        } else if (successRate < 0.3) {
            params.decreaseBehaviorDuration(learningRate * 0.15);
        }
    }
    
    /**
     * Записать адаптацию в историю.
     * 
     * @param botId идентификатор бота
     * @param oldParams старые параметры
     * @param newParams новые параметры
     * @param actionFeedback данные по действиям
     * @param behaviorFeedback данные по поведениям
     */
    private void recordAdaptation(int botId, BotAdaptationParams oldParams, BotAdaptationParams newParams,
                                 ActionFeedback actionFeedback, BehaviorFeedback behaviorFeedback) {
        AdaptationRecord record = new AdaptationRecord(
            botId,
            System.currentTimeMillis(),
            oldParams,
            newParams,
            actionFeedback,
            behaviorFeedback
        );
        
        adaptationHistory.computeIfAbsent(botId, k -> new ArrayList<>()).add(record);
        
        // Ограничиваем размер истории (последние 100 записей)
        List<AdaptationRecord> history = adaptationHistory.get(botId);
        if (history.size() > 100) {
            history.remove(0);
        }
    }
    
    /**
     * Проверить, можно ли выполнить адаптацию.
     * 
     * @param botId идентификатор бота
     * @return true, если можно адаптировать
     */
    private boolean canAdapt(int botId) {
        String hourKey = getCurrentHourKey();
        AtomicLong count = adaptationCounts.computeIfAbsent(hourKey, k -> new AtomicLong(0));
        
        return count.get() < maxAdaptationsPerHour;
    }
    
    /**
     * Увеличить счетчик адаптаций.
     * 
     * @param botId идентификатор бота
     */
    private void incrementAdaptationCount(int botId) {
        String hourKey = getCurrentHourKey();
        adaptationCounts.computeIfAbsent(hourKey, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Получить ключ текущего часа.
     * 
     * @return ключ текущего часа
     */
    private String getCurrentHourKey() {
        return String.valueOf(System.currentTimeMillis() / (60 * 60 * 1000));
    }
    
    /**
     * Получить общее количество адаптаций.
     * 
     * @return общее количество адаптаций
     */
    private long getTotalAdaptations() {
        return adaptationCounts.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
    }
    
    /**
     * Параметры адаптации бота.
     */
    public static class BotAdaptationParams {
        private final AtomicReference<Double> actionPriority = new AtomicReference<>(1.0);
        private final AtomicReference<Double> behaviorPriority = new AtomicReference<>(1.0);
        private final AtomicReference<Double> actionFrequency = new AtomicReference<>(1.0);
        private final AtomicReference<Double> behaviorDuration = new AtomicReference<>(1.0);
        private final AtomicReference<Double> learningRate = new AtomicReference<>(0.1);
        
        public BotAdaptationParams() {}
        
        public BotAdaptationParams(BotAdaptationParams other) {
            this.actionPriority.set(other.actionPriority.get());
            this.behaviorPriority.set(other.behaviorPriority.get());
            this.actionFrequency.set(other.actionFrequency.get());
            this.behaviorDuration.set(other.behaviorDuration.get());
            this.learningRate.set(other.learningRate.get());
        }
        
        public double getActionPriority() { return actionPriority.get(); }
        public double getBehaviorPriority() { return behaviorPriority.get(); }
        public double getActionFrequency() { return actionFrequency.get(); }
        public double getBehaviorDuration() { return behaviorDuration.get(); }
        public double getLearningRate() { return learningRate.get(); }
        
        public void setActionPriority(double value) { actionPriority.set(Math.max(0.1, Math.min(2.0, value))); }
        public void setBehaviorPriority(double value) { behaviorPriority.set(Math.max(0.1, Math.min(2.0, value))); }
        public void setActionFrequency(double value) { actionFrequency.set(Math.max(0.1, Math.min(2.0, value))); }
        public void setBehaviorDuration(double value) { behaviorDuration.set(Math.max(0.1, Math.min(2.0, value))); }
        public void setLearningRate(double value) { learningRate.set(Math.max(0.01, Math.min(0.5, value))); }
        
        public void increaseActionPriority(double delta) { 
            actionPriority.set(Math.min(2.0, actionPriority.get() + delta)); 
        }
        public void decreaseActionPriority(double delta) { 
            actionPriority.set(Math.max(0.1, actionPriority.get() - delta)); 
        }
        
        public void increaseBehaviorPriority(double delta) { 
            behaviorPriority.set(Math.min(2.0, behaviorPriority.get() + delta)); 
        }
        public void decreaseBehaviorPriority(double delta) { 
            behaviorPriority.set(Math.max(0.1, behaviorPriority.get() - delta)); 
        }
        
        public void increaseActionFrequency(double delta) { 
            actionFrequency.set(Math.min(2.0, actionFrequency.get() + delta)); 
        }
        public void decreaseActionFrequency(double delta) { 
            actionFrequency.set(Math.max(0.1, actionFrequency.get() - delta)); 
        }
        
        public void increaseBehaviorDuration(double delta) { 
            behaviorDuration.set(Math.min(2.0, behaviorDuration.get() + delta)); 
        }
        public void decreaseBehaviorDuration(double delta) { 
            behaviorDuration.set(Math.max(0.1, behaviorDuration.get() - delta)); 
        }
    }
    
    /**
     * Результат адаптации.
     */
    public static class AdaptationResult {
        private final int botId;
        private final boolean success;
        private final String message;
        
        public AdaptationResult(int botId, boolean success, String message) {
            this.botId = botId;
            this.success = success;
            this.message = message;
        }
        
        public int getBotId() { return botId; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    /**
     * Запись об адаптации.
     */
    public static class AdaptationRecord {
        private final int botId;
        private final long timestamp;
        private final BotAdaptationParams oldParams;
        private final BotAdaptationParams newParams;
        private final ActionFeedback actionFeedback;
        private final BehaviorFeedback behaviorFeedback;
        
        public AdaptationRecord(int botId, long timestamp, BotAdaptationParams oldParams, 
                              BotAdaptationParams newParams, ActionFeedback actionFeedback, 
                              BehaviorFeedback behaviorFeedback) {
            this.botId = botId;
            this.timestamp = timestamp;
            this.oldParams = oldParams;
            this.newParams = newParams;
            this.actionFeedback = actionFeedback;
            this.behaviorFeedback = behaviorFeedback;
        }
        
        public int getBotId() { return botId; }
        public long getTimestamp() { return timestamp; }
        public BotAdaptationParams getOldParams() { return oldParams; }
        public BotAdaptationParams getNewParams() { return newParams; }
        public ActionFeedback getActionFeedback() { return actionFeedback; }
        public BehaviorFeedback getBehaviorFeedback() { return behaviorFeedback; }
    }
}
