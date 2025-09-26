package net.sf.l2j.botmanager.learning;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Анализатор производительности ботов.
 * 
 * <p>Компонент для анализа эффективности поведения ботов, выявления паттернов
 * и предоставления рекомендаций по улучшению. Анализатор работает с данными
 * обратной связи и предоставляет детальную аналитику производительности.</p>
 * 
 * <p>Основные возможности:</p>
 * <ul>
 *   <li>Анализ эффективности действий и поведений</li>
 *   <li>Выявление паттернов успеха и неудач</li>
 *   <li>Рекомендации по оптимизации</li>
 *   <li>Сравнительный анализ между ботами</li>
 *   <li>Трендовый анализ производительности</li>
 * </ul>
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
public class PerformanceAnalyzer {
    
    /** Единственный экземпляр анализатора (Singleton) */
    private static volatile PerformanceAnalyzer instance;
    
    /** Коллектор обратной связи для получения данных */
    private final FeedbackCollector feedbackCollector;
    
    /** Кэш результатов анализа для оптимизации производительности */
    private final Map<String, AnalysisResult> analysisCache = new ConcurrentHashMap<>();
    
    /** Время последнего обновления кэша */
    private volatile long lastCacheUpdate = 0;
    
    /** Время жизни кэша в миллисекундах (5 минут) */
    private static final long CACHE_TTL = 5 * 60 * 1000;
    
    /** Минимальное количество данных для анализа */
    private static final int MIN_DATA_POINTS = 10;
    
    /** Порог для определения значимых изменений (в процентах) */
    private static final double SIGNIFICANCE_THRESHOLD = 5.0;
    
    /**
     * Приватный конструктор для Singleton паттерна.
     * 
     * @param feedbackCollector коллектор обратной связи
     */
    private PerformanceAnalyzer(FeedbackCollector feedbackCollector) {
        this.feedbackCollector = feedbackCollector;
    }
    
    /**
     * Получить единственный экземпляр анализатора.
     * 
     * @param feedbackCollector коллектор обратной связи
     * @return экземпляр анализатора
     */
    public static PerformanceAnalyzer getInstance(FeedbackCollector feedbackCollector) {
        if (instance == null) {
            synchronized (PerformanceAnalyzer.class) {
                if (instance == null) {
                    instance = new PerformanceAnalyzer(feedbackCollector);
                }
            }
        }
        return instance;
    }
    
    /**
     * Получить единственный экземпляр анализатора (если уже создан).
     * 
     * @return экземпляр анализатора или null, если не создан
     */
    public static PerformanceAnalyzer getInstance() {
        return instance;
    }
    
    /**
     * Анализировать производительность конкретного бота.
     * 
     * @param botId идентификатор бота
     * @return результат анализа производительности
     */
    public AnalysisResult analyzeBotPerformance(int botId) {
        String cacheKey = "bot_" + botId;
        
        // Проверяем кэш
        if (isCacheValid(cacheKey)) {
            return analysisCache.get(cacheKey);
        }
        
        // Получаем данные обратной связи
        ActionFeedback actionFeedback = feedbackCollector.getActionFeedback(botId);
        BehaviorFeedback behaviorFeedback = feedbackCollector.getBehaviorFeedback(botId);
        
        if (actionFeedback == null && behaviorFeedback == null) {
            return new AnalysisResult(botId, "Недостаточно данных для анализа");
        }
        
        // Выполняем анализ
        AnalysisResult result = performAnalysis(botId, actionFeedback, behaviorFeedback);
        
        // Сохраняем в кэш
        analysisCache.put(cacheKey, result);
        lastCacheUpdate = System.currentTimeMillis();
        
        return result;
    }
    
    /**
     * Анализировать производительность всех ботов.
     * 
     * @return карта результатов анализа по идентификаторам ботов
     */
    public Map<Integer, AnalysisResult> analyzeAllBotsPerformance() {
        Map<Integer, AnalysisResult> results = new HashMap<>();
        
        // Получаем список всех ботов
        Set<Integer> botIds = feedbackCollector.getAllBotIds();
        
        for (Integer botId : botIds) {
            results.put(botId, analyzeBotPerformance(botId));
        }
        
        return results;
    }
    
    /**
     * Сравнить производительность двух ботов.
     * 
     * @param botId1 идентификатор первого бота
     * @param botId2 идентификатор второго бота
     * @return результат сравнения
     */
    public ComparisonResult compareBotPerformance(int botId1, int botId2) {
        AnalysisResult result1 = analyzeBotPerformance(botId1);
        AnalysisResult result2 = analyzeBotPerformance(botId2);
        
        return new ComparisonResult(botId1, botId2, result1, result2);
    }
    
    /**
     * Найти лучших ботов по производительности.
     * 
     * @param limit максимальное количество ботов
     * @return список лучших ботов
     */
    public List<BotPerformanceRanking> getTopPerformers(int limit) {
        Map<Integer, AnalysisResult> allResults = analyzeAllBotsPerformance();
        
        return allResults.values().stream()
                .filter(result -> result.getOverallScore() > 0)
                .sorted((r1, r2) -> Double.compare(r2.getOverallScore(), r1.getOverallScore()))
                .limit(limit)
                .map(result -> new BotPerformanceRanking(result.getBotId(), result.getOverallScore(), result.getSummary()))
                .collect(Collectors.toList());
    }
    
    /**
     * Найти ботов, требующих улучшения.
     * 
     * @param threshold порог производительности (0.0 - 1.0)
     * @return список ботов, требующих улучшения
     */
    public List<BotPerformanceRanking> getBotsNeedingImprovement(double threshold) {
        Map<Integer, AnalysisResult> allResults = analyzeAllBotsPerformance();
        
        return allResults.values().stream()
                .filter(result -> result.getOverallScore() < threshold)
                .sorted((r1, r2) -> Double.compare(r1.getOverallScore(), r2.getOverallScore()))
                .map(result -> new BotPerformanceRanking(result.getBotId(), result.getOverallScore(), result.getSummary()))
                .collect(Collectors.toList());
    }
    
    /**
     * Анализировать тренды производительности.
     * 
     * @param botId идентификатор бота
     * @param days количество дней для анализа
     * @return результат трендового анализа
     */
    public TrendAnalysis analyzePerformanceTrends(int botId, int days) {
        // TODO: Реализовать трендовый анализ на основе исторических данных
        return new TrendAnalysis(botId, days, "Трендовый анализ не реализован");
    }
    
    /**
     * Получить рекомендации по улучшению производительности.
     * 
     * @param botId идентификатор бота
     * @return список рекомендаций
     */
    public List<String> getImprovementRecommendations(int botId) {
        AnalysisResult result = analyzeBotPerformance(botId);
        List<String> recommendations = new ArrayList<>();
        
        if (result.getActionSuccessRate() < 70.0) {
            recommendations.add("Улучшить точность выполнения действий");
        }
        
        if (result.getBehaviorSuccessRate() < 70.0) {
            recommendations.add("Оптимизировать выбор поведений");
        }
        
        if (result.getOverallScore() < 0.5) {
            recommendations.add("Провести комплексный анализ и оптимизацию");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Производительность в норме, продолжайте текущую стратегию");
        }
        
        return recommendations;
    }
    
    /**
     * Очистить кэш анализа.
     */
    public void clearCache() {
        analysisCache.clear();
        lastCacheUpdate = 0;
    }
    
    /**
     * Получить статистику анализатора.
     * 
     * @return статистика анализатора
     */
    public String getAnalyzerStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Performance Analyzer Statistics ===\n");
        stats.append("Cached Results: ").append(analysisCache.size()).append("\n");
        stats.append("Last Cache Update: ").append(new Date(lastCacheUpdate)).append("\n");
        stats.append("Cache TTL: ").append(CACHE_TTL / 1000).append(" seconds\n");
        stats.append("Min Data Points: ").append(MIN_DATA_POINTS).append("\n");
        stats.append("Significance Threshold: ").append(SIGNIFICANCE_THRESHOLD).append("%\n");
        
        return stats.toString();
    }
    
    /**
     * Выполнить детальный анализ производительности.
     * 
     * @param botId идентификатор бота
     * @param actionFeedback данные по действиям
     * @param behaviorFeedback данные по поведениям
     * @return результат анализа
     */
    private AnalysisResult performAnalysis(int botId, ActionFeedback actionFeedback, BehaviorFeedback behaviorFeedback) {
        double actionScore = 0.0;
        double behaviorScore = 0.0;
        double overallScore = 0.0;
        String summary = "";
        
        // Анализ действий
        if (actionFeedback != null) {
            actionScore = actionFeedback.getSuccessRate() / 100.0;
            summary += String.format("Действия: %.1f%% успешности", actionFeedback.getSuccessRate());
        }
        
        // Анализ поведений
        if (behaviorFeedback != null) {
            behaviorScore = behaviorFeedback.getSuccessRate() / 100.0;
            if (!summary.isEmpty()) summary += ", ";
            summary += String.format("Поведения: %.1f%% успешности", behaviorFeedback.getSuccessRate());
        }
        
        // Общий балл
        if (actionFeedback != null && behaviorFeedback != null) {
            overallScore = (actionScore + behaviorScore) / 2.0;
        } else if (actionFeedback != null) {
            overallScore = actionScore;
        } else if (behaviorFeedback != null) {
            overallScore = behaviorScore;
        }
        
        return new AnalysisResult(botId, actionScore, behaviorScore, overallScore, summary);
    }
    
    /**
     * Проверить валидность кэша.
     * 
     * @param cacheKey ключ кэша
     * @return true, если кэш валиден
     */
    private boolean isCacheValid(String cacheKey) {
        if (!analysisCache.containsKey(cacheKey)) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCacheUpdate) < CACHE_TTL;
    }
    
    /**
     * Результат анализа производительности бота.
     */
    public static class AnalysisResult {
        private final int botId;
        private final double actionScore;
        private final double behaviorScore;
        private final double overallScore;
        private final String summary;
        
        public AnalysisResult(int botId, double actionScore, double behaviorScore, double overallScore, String summary) {
            this.botId = botId;
            this.actionScore = actionScore;
            this.behaviorScore = behaviorScore;
            this.overallScore = overallScore;
            this.summary = summary;
        }
        
        public AnalysisResult(int botId, String errorMessage) {
            this.botId = botId;
            this.actionScore = 0.0;
            this.behaviorScore = 0.0;
            this.overallScore = 0.0;
            this.summary = errorMessage;
        }
        
        public int getBotId() { return botId; }
        public double getActionScore() { return actionScore; }
        public double getBehaviorScore() { return behaviorScore; }
        public double getOverallScore() { return overallScore; }
        public String getSummary() { return summary; }
        public double getActionSuccessRate() { return actionScore * 100.0; }
        public double getBehaviorSuccessRate() { return behaviorScore * 100.0; }
    }
    
    /**
     * Результат сравнения производительности двух ботов.
     */
    public static class ComparisonResult {
        private final int botId1;
        private final int botId2;
        private final AnalysisResult result1;
        private final AnalysisResult result2;
        
        public ComparisonResult(int botId1, int botId2, AnalysisResult result1, AnalysisResult result2) {
            this.botId1 = botId1;
            this.botId2 = botId2;
            this.result1 = result1;
            this.result2 = result2;
        }
        
        public int getBotId1() { return botId1; }
        public int getBotId2() { return botId2; }
        public AnalysisResult getResult1() { return result1; }
        public AnalysisResult getResult2() { return result2; }
        
        public boolean isBot1Better() {
            return result1.getOverallScore() > result2.getOverallScore();
        }
        
        public double getPerformanceDifference() {
            return Math.abs(result1.getOverallScore() - result2.getOverallScore());
        }
    }
    
    /**
     * Рейтинг производительности бота.
     */
    public static class BotPerformanceRanking {
        private final int botId;
        private final double score;
        private final String summary;
        
        public BotPerformanceRanking(int botId, double score, String summary) {
            this.botId = botId;
            this.score = score;
            this.summary = summary;
        }
        
        public int getBotId() { return botId; }
        public double getScore() { return score; }
        public String getSummary() { return summary; }
    }
    
    /**
     * Результат трендового анализа.
     */
    public static class TrendAnalysis {
        private final int botId;
        private final int days;
        private final String analysis;
        
        public TrendAnalysis(int botId, int days, String analysis) {
            this.botId = botId;
            this.days = days;
            this.analysis = analysis;
        }
        
        public int getBotId() { return botId; }
        public int getDays() { return days; }
        public String getAnalysis() { return analysis; }
    }
}
