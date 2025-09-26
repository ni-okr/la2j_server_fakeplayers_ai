package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Обработчик данных для машинного обучения ботов
 * 
 * Этот класс отвечает за обработку, нормализацию и подготовку
 * данных для обучения нейронных сетей ботов. Он преобразует
 * сырые данные игры в формат, пригодный для машинного обучения.
 * 
 * Ключевые возможности:
 * - Обработка обучающих данных
 * - Нормализация и стандартизация данных
 * - Обработка обратной связи
 * - Фильтрация и очистка данных
 * - Статистика обработки
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class LearningDataProcessor {
    
    private static final Logger _log = Logger.getLogger(LearningDataProcessor.class);
    
    // ==================== ПОЛЯ ====================
    
    /**
     * ID бота, для которого обрабатываются данные
     */
    private final int botId;
    
    /**
     * Количество обработанных наборов данных
     */
    private final AtomicLong processedDataSets = new AtomicLong(0);
    
    /**
     * Количество обработанных примеров
     */
    private final AtomicLong processedExamples = new AtomicLong(0);
    
    /**
     * Количество ошибок обработки
     */
    private final AtomicLong processingErrors = new AtomicLong(0);
    
    /**
     * Время последней обработки
     */
    private volatile long lastProcessingTime = 0;
    
    /**
     * Статистика нормализации
     */
    private final Map<String, NormalizationStats> normalizationStats = new HashMap<>();
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор обработчика данных
     * 
     * @param botId ID бота
     */
    public LearningDataProcessor(int botId) {
        this.botId = botId;
        _log.info("LearningDataProcessor created for bot " + botId);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Обрабатывает данные для обучения
     * 
     * @param trainingData данные для обучения
     * @return обработанные данные
     */
    public ProcessedData processTrainingData(TrainingData trainingData) {
        if (trainingData == null) {
            return null;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Извлекаем входные данные
            double[][] inputs = extractInputs(trainingData);
            if (inputs == null || inputs.length == 0) {
                processingErrors.incrementAndGet();
                return null;
            }
            
            // Извлекаем целевые данные
            double[][] targets = extractTargets(trainingData);
            if (targets == null || targets.length == 0) {
                processingErrors.incrementAndGet();
                return null;
            }
            
            // Нормализуем данные
            inputs = normalizeInputs(inputs);
            targets = normalizeTargets(targets);
            
            // Создаем обработанные данные
            ProcessedData processedData = new ProcessedData(inputs, targets);
            
            // Обновляем статистику
            processedDataSets.incrementAndGet();
            processedExamples.addAndGet(inputs.length);
            lastProcessingTime = System.currentTimeMillis();
            
            _log.debug("Processed training data for bot " + botId + 
                      " in " + (lastProcessingTime - startTime) + "ms");
            
            return processedData;
            
        } catch (Exception e) {
            processingErrors.incrementAndGet();
            _log.error("Error processing training data for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Обрабатывает обратную связь
     * 
     * @param feedback обратная связь
     * @return обработанные данные
     */
    public ProcessedData processFeedback(LearningFeedback feedback) {
        if (feedback == null) {
            return null;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Извлекаем данные из обратной связи
            double[][] inputs = extractFeedbackInputs(feedback);
            if (inputs == null || inputs.length == 0) {
                processingErrors.incrementAndGet();
                return null;
            }
            
            double[][] targets = extractFeedbackTargets(feedback);
            if (targets == null || targets.length == 0) {
                processingErrors.incrementAndGet();
                return null;
            }
            
            // Нормализуем данные
            inputs = normalizeInputs(inputs);
            targets = normalizeTargets(targets);
            
            // Создаем обработанные данные
            ProcessedData processedData = new ProcessedData(inputs, targets);
            
            // Обновляем статистику
            processedDataSets.incrementAndGet();
            processedExamples.addAndGet(inputs.length);
            lastProcessingTime = System.currentTimeMillis();
            
            _log.debug("Processed feedback data for bot " + botId + 
                      " in " + (lastProcessingTime - startTime) + "ms");
            
            return processedData;
            
        } catch (Exception e) {
            processingErrors.incrementAndGet();
            _log.error("Error processing feedback data for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== ИЗВЛЕЧЕНИЕ ДАННЫХ ====================
    
    /**
     * Извлекает входные данные из обучающих данных
     * 
     * @param trainingData обучающие данные
     * @return входные данные
     */
    private double[][] extractInputs(TrainingData trainingData) {
        List<double[]> inputList = new ArrayList<>();
        
        // Извлекаем данные из различных источников
        if (trainingData.getBotState() != null) {
            inputList.add(extractBotStateInputs(trainingData.getBotState()));
        }
        
        if (trainingData.getEnvironmentData() != null) {
            inputList.add(extractEnvironmentInputs(trainingData.getEnvironmentData()));
        }
        
        if (trainingData.getActionHistory() != null) {
            inputList.add(extractActionHistoryInputs(trainingData.getActionHistory()));
        }
        
        if (trainingData.getPerformanceMetrics() != null) {
            inputList.add(extractPerformanceInputs(trainingData.getPerformanceMetrics()));
        }
        
        return inputList.toArray(new double[0][]);
    }
    
    /**
     * Извлекает целевые данные из обучающих данных
     * 
     * @param trainingData обучающие данные
     * @return целевые данные
     */
    private double[][] extractTargets(TrainingData trainingData) {
        List<double[]> targetList = new ArrayList<>();
        
        // Извлекаем целевые значения
        if (trainingData.getExpectedActions() != null) {
            targetList.add(extractActionTargets(trainingData.getExpectedActions()));
        }
        
        if (trainingData.getExpectedBehaviors() != null) {
            targetList.add(extractBehaviorTargets(trainingData.getExpectedBehaviors()));
        }
        
        if (trainingData.getExpectedOutcomes() != null) {
            targetList.add(extractOutcomeTargets(trainingData.getExpectedOutcomes()));
        }
        
        return targetList.toArray(new double[0][]);
    }
    
    /**
     * Извлекает входные данные из обратной связи
     * 
     * @param feedback обратная связь
     * @return входные данные
     */
    private double[][] extractFeedbackInputs(LearningFeedback feedback) {
        List<double[]> inputList = new ArrayList<>();
        
        if (feedback.getActionFeedback() != null) {
            inputList.add(extractActionFeedbackInputs(feedback.getActionFeedback()));
        }
        
        if (feedback.getBehaviorFeedback() != null) {
            inputList.add(extractBehaviorFeedbackInputs(feedback.getBehaviorFeedback()));
        }
        
        if (feedback.getPerformanceData() != null) {
            inputList.add(extractPerformanceDataInputs(feedback.getPerformanceData()));
        }
        
        return inputList.toArray(new double[0][]);
    }
    
    /**
     * Извлекает целевые данные из обратной связи
     * 
     * @param feedback обратная связь
     * @return целевые данные
     */
    private double[][] extractFeedbackTargets(LearningFeedback feedback) {
        List<double[]> targetList = new ArrayList<>();
        
        // Создаем целевые значения на основе обратной связи
        double[] successTarget = {feedback.isSuccess() ? 1.0 : 0.0};
        targetList.add(successTarget);
        
        double[] performanceTarget = {feedback.getPerformanceScore()};
        targetList.add(performanceTarget);
        
        return targetList.toArray(new double[0][]);
    }
    
    // ==================== ИЗВЛЕЧЕНИЕ КОНКРЕТНЫХ ДАННЫХ ====================
    
    /**
     * Извлекает данные состояния бота
     * 
     * @param botState состояние бота
     * @return данные состояния
     */
    private double[] extractBotStateInputs(BotStateData botState) {
        return new double[]{
            botState.getHealth(),
            botState.getMana(),
            botState.getLevel(),
            botState.getExperience(),
            botState.getX(),
            botState.getY(),
            botState.getZ()
        };
    }
    
    /**
     * Извлекает данные окружения
     * 
     * @param environmentData данные окружения
     * @return данные окружения
     */
    private double[] extractEnvironmentInputs(EnvironmentData environmentData) {
        return new double[]{
            environmentData.getNearbyEnemies(),
            environmentData.getNearbyAllies(),
            environmentData.getNearbyNpcs(),
            environmentData.getTimeOfDay(),
            environmentData.getWeather(),
            environmentData.getDangerLevel()
        };
    }
    
    /**
     * Извлекает данные истории действий
     * 
     * @param actionHistory история действий
     * @return данные истории
     */
    private double[] extractActionHistoryInputs(ActionHistoryData actionHistory) {
        return new double[]{
            actionHistory.getTotalActions(),
            actionHistory.getSuccessfulActions(),
            actionHistory.getFailedActions(),
            actionHistory.getAverageExecutionTime(),
            actionHistory.getLastActionTime()
        };
    }
    
    /**
     * Извлекает данные производительности
     * 
     * @param performanceMetrics метрики производительности
     * @return данные производительности
     */
    private double[] extractPerformanceInputs(PerformanceMetrics performanceMetrics) {
        return new double[]{
            performanceMetrics.getSuccessRate(),
            performanceMetrics.getEfficiency(),
            performanceMetrics.getResponseTime(),
            performanceMetrics.getResourceUsage()
        };
    }
    
    /**
     * Извлекает целевые значения действий
     * 
     * @param expectedActions ожидаемые действия
     * @return целевые значения
     */
    private double[] extractActionTargets(ExpectedActions expectedActions) {
        return new double[]{
            expectedActions.getActionType(),
            expectedActions.getPriority(),
            expectedActions.getDuration(),
            expectedActions.getSuccessProbability()
        };
    }
    
    /**
     * Извлекает целевые значения поведений
     * 
     * @param expectedBehaviors ожидаемые поведения
     * @return целевые значения
     */
    private double[] extractBehaviorTargets(ExpectedBehaviors expectedBehaviors) {
        return new double[]{
            expectedBehaviors.getBehaviorType(),
            expectedBehaviors.getIntensity(),
            expectedBehaviors.getDuration(),
            expectedBehaviors.getSuccessProbability()
        };
    }
    
    /**
     * Извлекает целевые значения результатов
     * 
     * @param expectedOutcomes ожидаемые результаты
     * @return целевые значения
     */
    private double[] extractOutcomeTargets(ExpectedOutcomes expectedOutcomes) {
        return new double[]{
            expectedOutcomes.getSuccessRate(),
            expectedOutcomes.getEfficiency(),
            expectedOutcomes.getReward(),
            expectedOutcomes.getRisk()
        };
    }
    
    /**
     * Извлекает данные обратной связи по действиям
     * 
     * @param actionFeedback обратная связь по действиям
     * @return данные обратной связи
     */
    private double[] extractActionFeedbackInputs(ActionFeedback actionFeedback) {
        return new double[]{
            actionFeedback.getSuccessfulExecutions(),
            actionFeedback.getFailedExecutions(),
            actionFeedback.getAverageExecutionTime(),
            actionFeedback.getTotalExecutionTime()
        };
    }
    
    /**
     * Извлекает данные обратной связи по поведениям
     * 
     * @param behaviorFeedback обратная связь по поведениям
     * @return данные обратной связи
     */
    private double[] extractBehaviorFeedbackInputs(BehaviorFeedback behaviorFeedback) {
        return new double[]{
            behaviorFeedback.getSuccessfulExecutions(),
            behaviorFeedback.getFailedExecutions(),
            behaviorFeedback.getAverageExecutionTime(),
            behaviorFeedback.getAverageActionsPerformed()
        };
    }
    
    /**
     * Извлекает данные производительности
     * 
     * @param performanceData данные производительности
     * @return данные производительности
     */
    private double[] extractPerformanceDataInputs(PerformanceData performanceData) {
        return new double[]{
            performanceData.getCpuUsage(),
            performanceData.getMemoryUsage(),
            performanceData.getNetworkLatency(),
            performanceData.getResponseTime()
        };
    }
    
    // ==================== НОРМАЛИЗАЦИЯ ====================
    
    /**
     * Нормализует входные данные
     * 
     * @param inputs входные данные
     * @return нормализованные данные
     */
    private double[][] normalizeInputs(double[][] inputs) {
        if (inputs == null || inputs.length == 0) {
            return inputs;
        }
        
        double[][] normalized = new double[inputs.length][];
        for (int i = 0; i < inputs.length; i++) {
            normalized[i] = normalizeArray(inputs[i], "input");
        }
        
        return normalized;
    }
    
    /**
     * Нормализует целевые данные
     * 
     * @param targets целевые данные
     * @return нормализованные данные
     */
    private double[][] normalizeTargets(double[][] targets) {
        if (targets == null || targets.length == 0) {
            return targets;
        }
        
        double[][] normalized = new double[targets.length][];
        for (int i = 0; i < targets.length; i++) {
            normalized[i] = normalizeArray(targets[i], "target");
        }
        
        return normalized;
    }
    
    /**
     * Нормализует массив данных
     * 
     * @param data данные для нормализации
     * @param type тип данных
     * @return нормализованные данные
     */
    private double[] normalizeArray(double[] data, String type) {
        if (data == null || data.length == 0) {
            return data;
        }
        
        // Вычисляем статистики
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0.0;
        
        for (double value : data) {
            if (Double.isFinite(value)) {
                min = Math.min(min, value);
                max = Math.max(max, value);
                sum += value;
            }
        }
        
        double mean = sum / data.length;
        double range = max - min;
        
        // Обновляем статистики нормализации
        updateNormalizationStats(type, min, max, mean, range);
        
        // Нормализуем данные
        double[] normalized = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            if (Double.isFinite(data[i]) && range > 0) {
                normalized[i] = (data[i] - min) / range;
            } else {
                normalized[i] = 0.0;
            }
        }
        
        return normalized;
    }
    
    /**
     * Обновляет статистики нормализации
     * 
     * @param type тип данных
     * @param min минимальное значение
     * @param max максимальное значение
     * @param mean среднее значение
     * @param range диапазон значений
     */
    private void updateNormalizationStats(String type, double min, double max, double mean, double range) {
        NormalizationStats stats = normalizationStats.get(type);
        if (stats == null) {
            stats = new NormalizationStats();
            normalizationStats.put(type, stats);
        }
        
        stats.update(min, max, mean, range);
    }
    
    // ==================== СТАТИСТИКА ====================
    
    /**
     * Возвращает статистику обработки
     * 
     * @return статистика обработки
     */
    public ProcessingStats getProcessingStats() {
        return new ProcessingStats(
            processedDataSets.get(),
            processedExamples.get(),
            processingErrors.get(),
            lastProcessingTime
        );
    }
    
    /**
     * Возвращает статистику нормализации
     * 
     * @return статистика нормализации
     */
    public Map<String, NormalizationStats> getNormalizationStats() {
        return new HashMap<>(normalizationStats);
    }
    
    // ==================== ВНУТРЕННИЕ КЛАССЫ ====================
    
    /**
     * Статистика обработки
     */
    public static class ProcessingStats {
        private final long processedDataSets;
        private final long processedExamples;
        private final long processingErrors;
        private final long lastProcessingTime;
        
        public ProcessingStats(long processedDataSets, long processedExamples, 
                              long processingErrors, long lastProcessingTime) {
            this.processedDataSets = processedDataSets;
            this.processedExamples = processedExamples;
            this.processingErrors = processingErrors;
            this.lastProcessingTime = lastProcessingTime;
        }
        
        public long getProcessedDataSets() { return processedDataSets; }
        public long getProcessedExamples() { return processedExamples; }
        public long getProcessingErrors() { return processingErrors; }
        public long getLastProcessingTime() { return lastProcessingTime; }
        
        public double getErrorRate() {
            long total = processedDataSets + processingErrors;
            return total > 0 ? (double) processingErrors / total : 0.0;
        }
    }
    
    /**
     * Статистика нормализации
     */
    public static class NormalizationStats {
        private double minValue = Double.MAX_VALUE;
        private double maxValue = Double.MIN_VALUE;
        private double meanValue = 0.0;
        private double rangeValue = 0.0;
        private int updateCount = 0;
        
        public void update(double min, double max, double mean, double range) {
            minValue = Math.min(minValue, min);
            maxValue = Math.max(maxValue, max);
            meanValue = (meanValue * updateCount + mean) / (updateCount + 1);
            rangeValue = Math.max(rangeValue, range);
            updateCount++;
        }
        
        public double getMinValue() { return minValue; }
        public double getMaxValue() { return maxValue; }
        public double getMeanValue() { return meanValue; }
        public double getRangeValue() { return rangeValue; }
        public int getUpdateCount() { return updateCount; }
    }
}
