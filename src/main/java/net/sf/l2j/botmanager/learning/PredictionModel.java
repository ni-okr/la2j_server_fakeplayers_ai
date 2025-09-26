package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Модель предсказаний для машинного обучения ботов
 * 
 * Этот класс представляет собой модель для предсказания оптимальных
 * действий ботов на основе обученной нейронной сети. Модель использует
 * различные алгоритмы для интерпретации выходов сети и выбора
 * наилучших действий.
 * 
 * Ключевые возможности:
 * - Предсказание действий на основе нейронной сети
 * - Интерпретация выходов сети
 * - Выбор оптимальных действий
 * - Оценка уверенности в предсказаниях
 * - Статистика предсказаний
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class PredictionModel {
    
    private static final Logger _log = Logger.getLogger(PredictionModel.class);
    
    // ==================== КОНСТАНТЫ ====================
    
    /**
     * Минимальная уверенность для принятия предсказания
     */
    private static final double MIN_CONFIDENCE = 0.3;
    
    /**
     * Высокая уверенность в предсказании
     */
    private static final double HIGH_CONFIDENCE = 0.8;
    
    /**
     * Максимальное количество действий для предсказания
     */
    private static final int MAX_ACTIONS = 10;
    
    // ==================== ПОЛЯ ====================
    
    /**
     * ID бота, для которого создана модель
     */
    private final int botId;
    
    /**
     * Нейронная сеть для предсказаний
     */
    private NeuralNetwork network;
    
    /**
     * Количество предсказаний
     */
    private final AtomicLong predictionCount = new AtomicLong(0);
    
    /**
     * Количество успешных предсказаний
     */
    private final AtomicLong successfulPredictions = new AtomicLong(0);
    
    /**
     * Количество предсказаний с высокой уверенностью
     */
    private final AtomicLong highConfidencePredictions = new AtomicLong(0);
    
    /**
     * Время последнего предсказания
     */
    private volatile long lastPredictionTime = 0;
    
    /**
     * Средняя уверенность в предсказаниях
     */
    private volatile double averageConfidence = 0.0;
    
    /**
     * История предсказаний
     */
    private final List<ActionPrediction> predictionHistory = new ArrayList<>();
    
    /**
     * Максимальный размер истории
     */
    private static final int MAX_HISTORY_SIZE = 1000;
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор модели предсказаний
     * 
     * @param botId ID бота
     */
    public PredictionModel(int botId) {
        this.botId = botId;
        _log.info("PredictionModel created for bot " + botId);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Устанавливает нейронную сеть для предсказаний
     * 
     * @param network нейронная сеть
     */
    public void setNetwork(NeuralNetwork network) {
        this.network = network;
        _log.info("Neural network set for PredictionModel of bot " + botId);
    }
    
    /**
     * Предсказывает оптимальное действие
     * 
     * @param network нейронная сеть
     * @param context контекст для предсказания
     * @return предсказание действия
     */
    public ActionPrediction predict(NeuralNetwork network, PredictionContext context) {
        if (network == null || context == null) {
            return null;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Подготавливаем входные данные
            double[] inputs = prepareInputs(context);
            if (inputs == null) {
                return null;
            }
            
            // Получаем выходы нейронной сети
            double[] outputs = network.forward(inputs);
            if (outputs == null) {
                return null;
            }
            
            // Интерпретируем выходы сети
            ActionPrediction prediction = interpretOutputs(outputs, context);
            
            // Обновляем статистику
            if (prediction != null) {
                updateStatistics(prediction);
                addToHistory(prediction);
                lastPredictionTime = System.currentTimeMillis();
                
                _log.debug("Prediction made for bot " + botId + 
                          " in " + (lastPredictionTime - startTime) + "ms");
            }
            
            return prediction;
            
        } catch (Exception e) {
            _log.error("Error making prediction for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== ПОДГОТОВКА ДАННЫХ ====================
    
    /**
     * Подготавливает входные данные для предсказания
     * 
     * @param context контекст предсказания
     * @return входные данные
     */
    private double[] prepareInputs(PredictionContext context) {
        List<Double> inputList = new ArrayList<>();
        
        // Добавляем данные состояния бота
        if (context.getBotState() != null) {
            addBotStateInputs(inputList, context.getBotState());
        }
        
        // Добавляем данные окружения
        if (context.getEnvironmentData() != null) {
            addEnvironmentInputs(inputList, context.getEnvironmentData());
        }
        
        // Добавляем данные о целях
        if (context.getTargetData() != null) {
            addTargetInputs(inputList, context.getTargetData());
        }
        
        // Добавляем исторические данные
        if (context.getHistoricalData() != null) {
            addHistoricalInputs(inputList, context.getHistoricalData());
        }
        
        // Преобразуем в массив
        double[] inputs = new double[inputList.size()];
        for (int i = 0; i < inputList.size(); i++) {
            inputs[i] = inputList.get(i);
        }
        
        return inputs;
    }
    
    /**
     * Добавляет данные состояния бота
     * 
     * @param inputList список входных данных
     * @param botState состояние бота
     */
    private void addBotStateInputs(List<Double> inputList, BotStateData botState) {
        inputList.add(botState.getHealth());
        inputList.add(botState.getMana());
        inputList.add((double) botState.getLevel());
        inputList.add(botState.getExperience());
        inputList.add(botState.getX());
        inputList.add(botState.getY());
        inputList.add(botState.getZ());
    }
    
    /**
     * Добавляет данные окружения
     * 
     * @param inputList список входных данных
     * @param environmentData данные окружения
     */
    private void addEnvironmentInputs(List<Double> inputList, EnvironmentData environmentData) {
        inputList.add((double) environmentData.getNearbyEnemies());
        inputList.add((double) environmentData.getNearbyAllies());
        inputList.add((double) environmentData.getNearbyNpcs());
        inputList.add(environmentData.getTimeOfDay());
        inputList.add(environmentData.getWeather());
        inputList.add(environmentData.getDangerLevel());
    }
    
    /**
     * Добавляет данные о целях
     * 
     * @param inputList список входных данных
     * @param targetData данные о целях
     */
    private void addTargetInputs(List<Double> inputList, TargetData targetData) {
        inputList.add((double) targetData.getTargetCount());
        inputList.add(targetData.getAverageDistance());
        inputList.add(targetData.getAverageLevel());
        inputList.add(targetData.getThreatLevel());
    }
    
    /**
     * Добавляет исторические данные
     * 
     * @param inputList список входных данных
     * @param historicalData исторические данные
     */
    private void addHistoricalInputs(List<Double> inputList, HistoricalData historicalData) {
        inputList.add(historicalData.getRecentSuccessRate());
        inputList.add(historicalData.getAveragePerformance());
        inputList.add(historicalData.getTrendDirection());
        inputList.add(historicalData.getVolatility());
    }
    
    // ==================== ИНТЕРПРЕТАЦИЯ ВЫХОДОВ ====================
    
    /**
     * Интерпретирует выходы нейронной сети
     * 
     * @param outputs выходы сети
     * @param context контекст предсказания
     * @return предсказание действия
     */
    private ActionPrediction interpretOutputs(double[] outputs, PredictionContext context) {
        if (outputs.length < 3) {
            return null;
        }
        
        // Извлекаем основные параметры
        double actionType = outputs[0];
        double priority = outputs[1];
        double confidence = outputs[2];
        
        // Дополнительные параметры (если доступны)
        double duration = outputs.length > 3 ? outputs[3] : 1.0;
        double intensity = outputs.length > 4 ? outputs[4] : 0.5;
        
        // Проверяем минимальную уверенность
        if (confidence < MIN_CONFIDENCE) {
            return null;
        }
        
        // Создаем предсказание
        ActionPrediction prediction = new ActionPrediction();
        prediction.setBotId(botId);
        prediction.setActionType((int) (actionType * MAX_ACTIONS));
        prediction.setPriority(Math.max(0.0, Math.min(1.0, priority)));
        prediction.setConfidence(Math.max(0.0, Math.min(1.0, confidence)));
        prediction.setDuration(Math.max(0.1, Math.min(10.0, duration)));
        prediction.setIntensity(Math.max(0.0, Math.min(1.0, intensity)));
        prediction.setTimestamp(System.currentTimeMillis());
        
        // Добавляем дополнительные параметры
        if (outputs.length > 5) {
            Map<String, Double> additionalParams = new HashMap<>();
            for (int i = 5; i < outputs.length; i++) {
                additionalParams.put("param_" + (i - 5), outputs[i]);
            }
            prediction.setAdditionalParameters(additionalParams);
        }
        
        return prediction;
    }
    
    // ==================== СТАТИСТИКА ====================
    
    /**
     * Обновляет статистику предсказаний
     * 
     * @param prediction предсказание
     */
    private void updateStatistics(ActionPrediction prediction) {
        predictionCount.incrementAndGet();
        
        if (prediction.getConfidence() > HIGH_CONFIDENCE) {
            highConfidencePredictions.incrementAndGet();
        }
        
        // Обновляем среднюю уверенность
        long count = predictionCount.get();
        averageConfidence = (averageConfidence * (count - 1) + prediction.getConfidence()) / count;
    }
    
    /**
     * Добавляет предсказание в историю
     * 
     * @param prediction предсказание
     */
    private void addToHistory(ActionPrediction prediction) {
        synchronized (predictionHistory) {
            predictionHistory.add(prediction);
            
            // Ограничиваем размер истории
            if (predictionHistory.size() > MAX_HISTORY_SIZE) {
                predictionHistory.remove(0);
            }
        }
    }
    
    /**
     * Возвращает статистику предсказаний
     * 
     * @return статистика предсказаний
     */
    public PredictionStats getPredictionStats() {
        return new PredictionStats(
            predictionCount.get(),
            successfulPredictions.get(),
            highConfidencePredictions.get(),
            averageConfidence,
            lastPredictionTime
        );
    }
    
    /**
     * Возвращает историю предсказаний
     * 
     * @return история предсказаний
     */
    public List<ActionPrediction> getPredictionHistory() {
        synchronized (predictionHistory) {
            return new ArrayList<>(predictionHistory);
        }
    }
    
    /**
     * Очищает историю предсказаний
     */
    public void clearHistory() {
        synchronized (predictionHistory) {
            predictionHistory.clear();
        }
    }
    
    // ==================== ВНУТРЕННИЕ КЛАССЫ ====================
    
    /**
     * Статистика предсказаний
     */
    public static class PredictionStats {
        private final long predictionCount;
        private final long successfulPredictions;
        private final long highConfidencePredictions;
        private final double averageConfidence;
        private final long lastPredictionTime;
        
        public PredictionStats(long predictionCount, long successfulPredictions, 
                              long highConfidencePredictions, double averageConfidence, 
                              long lastPredictionTime) {
            this.predictionCount = predictionCount;
            this.successfulPredictions = successfulPredictions;
            this.highConfidencePredictions = highConfidencePredictions;
            this.averageConfidence = averageConfidence;
            this.lastPredictionTime = lastPredictionTime;
        }
        
        public long getPredictionCount() { return predictionCount; }
        public long getSuccessfulPredictions() { return successfulPredictions; }
        public long getHighConfidencePredictions() { return highConfidencePredictions; }
        public double getAverageConfidence() { return averageConfidence; }
        public long getLastPredictionTime() { return lastPredictionTime; }
        
        public double getSuccessRate() {
            return predictionCount > 0 ? (double) successfulPredictions / predictionCount : 0.0;
        }
        
        public double getHighConfidenceRate() {
            return predictionCount > 0 ? (double) highConfidencePredictions / predictionCount : 0.0;
        }
    }
}
