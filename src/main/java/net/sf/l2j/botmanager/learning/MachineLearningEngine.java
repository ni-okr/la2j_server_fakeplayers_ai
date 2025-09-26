package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Движок машинного обучения для ботов
 * 
 * Этот класс представляет собой основной движок машинного обучения,
 * который координирует работу нейронных сетей, обработку данных
 * и обучение ботов на основе собранной информации.
 * 
 * Ключевые возможности:
 * - Управление нейронными сетями для каждого бота
 * - Обработка данных обучения
 * - Предсказание оптимальных действий
 * - Адаптация моделей на основе обратной связи
 * - Статистика и мониторинг обучения
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class MachineLearningEngine {
    
    private static final Logger _log = Logger.getLogger(MachineLearningEngine.class);
    
    // ==================== СИНГЛТОН ====================
    
    private static volatile MachineLearningEngine instance;
    private static final Object lock = new Object();
    
    /**
     * Получает экземпляр движка машинного обучения
     * 
     * @return экземпляр движка
     */
    public static MachineLearningEngine getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MachineLearningEngine();
                }
            }
        }
        return instance;
    }
    
    // ==================== ПОЛЯ ====================
    
    /**
     * Нейронные сети для каждого бота
     */
    private final Map<Integer, NeuralNetwork> botNetworks = new ConcurrentHashMap<>();
    
    /**
     * Обработчики данных для каждого бота
     */
    private final Map<Integer, LearningDataProcessor> botDataProcessors = new ConcurrentHashMap<>();
    
    /**
     * Модели предсказаний для каждого бота
     */
    private final Map<Integer, PredictionModel> botPredictionModels = new ConcurrentHashMap<>();
    
    /**
     * Статистика обучения для каждого бота
     */
    private final Map<Integer, LearningStats> botLearningStats = new ConcurrentHashMap<>();
    
    /**
     * Флаг активности движка
     */
    private final AtomicBoolean isActive = new AtomicBoolean(false);
    
    /**
     * Общее количество обработанных данных
     */
    private final AtomicLong totalDataProcessed = new AtomicLong(0);
    
    /**
     * Общее количество предсказаний
     */
    private final AtomicLong totalPredictions = new AtomicLong(0);
    
    /**
     * Общее количество успешных предсказаний
     */
    private final AtomicLong successfulPredictions = new AtomicLong(0);
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Приватный конструктор для синглтона
     */
    private MachineLearningEngine() {
        _log.info("MachineLearningEngine initialized");
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Запускает движок машинного обучения
     */
    public void start() {
        if (isActive.compareAndSet(false, true)) {
            _log.info("MachineLearningEngine started");
        }
    }
    
    /**
     * Останавливает движок машинного обучения
     */
    public void stop() {
        if (isActive.compareAndSet(true, false)) {
            _log.info("MachineLearningEngine stopped");
        }
    }
    
    /**
     * Проверяет, активен ли движок
     * 
     * @return true, если движок активен
     */
    public boolean isActive() {
        return isActive.get();
    }
    
    // ==================== УПРАВЛЕНИЕ БОТАМИ ====================
    
    /**
     * Регистрирует бота в системе машинного обучения
     * 
     * @param bot бот для регистрации
     */
    public void registerBot(EnhancedFakePlayer bot) {
        if (bot == null) {
            return;
        }
        
        int botId = bot.getBotId();
        
        // Создаем нейронную сеть для бота
        NeuralNetwork network = new NeuralNetwork(botId);
        botNetworks.put(botId, network);
        
        // Создаем обработчик данных для бота
        LearningDataProcessor processor = new LearningDataProcessor(botId);
        botDataProcessors.put(botId, processor);
        
        // Создаем модель предсказаний для бота
        PredictionModel model = new PredictionModel(botId);
        botPredictionModels.put(botId, model);
        
        // Создаем статистику обучения для бота
        LearningStats stats = new LearningStats(botId);
        botLearningStats.put(botId, stats);
        
        _log.info("Bot " + botId + " registered in MachineLearningEngine");
    }
    
    /**
     * Удаляет бота из системы машинного обучения
     * 
     * @param botId ID бота для удаления
     */
    public void unregisterBot(int botId) {
        botNetworks.remove(botId);
        botDataProcessors.remove(botId);
        botPredictionModels.remove(botId);
        botLearningStats.remove(botId);
        
        _log.info("Bot " + botId + " unregistered from MachineLearningEngine");
    }
    
    /**
     * Проверяет, зарегистрирован ли бот
     * 
     * @param botId ID бота
     * @return true, если бот зарегистрирован
     */
    public boolean isBotRegistered(int botId) {
        return botNetworks.containsKey(botId);
    }
    
    // ==================== ОБУЧЕНИЕ ====================
    
    /**
     * Обучает нейронную сеть бота на основе данных
     * 
     * @param botId ID бота
     * @param trainingData данные для обучения
     * @return true, если обучение прошло успешно
     */
    public boolean trainBot(int botId, TrainingData trainingData) {
        if (!isActive.get() || !isBotRegistered(botId)) {
            return false;
        }
        
        try {
            NeuralNetwork network = botNetworks.get(botId);
            LearningDataProcessor processor = botDataProcessors.get(botId);
            LearningStats stats = botLearningStats.get(botId);
            
            if (network == null || processor == null || stats == null) {
                return false;
            }
            
            // Обрабатываем данные для обучения
            ProcessedData processedData = processor.processTrainingData(trainingData);
            
            // Обучаем нейронную сеть
            boolean success = network.train(processedData);
            
            // Обновляем статистику
            if (success) {
                stats.recordTrainingSuccess();
                totalDataProcessed.incrementAndGet();
            } else {
                stats.recordTrainingFailure();
            }
            
            return success;
            
        } catch (Exception e) {
            _log.error("Error training bot " + botId + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Предсказывает оптимальное действие для бота
     * 
     * @param botId ID бота
     * @param context контекст для предсказания
     * @return предсказание действия
     */
    public ActionPrediction predictAction(int botId, PredictionContext context) {
        if (!isActive.get() || !isBotRegistered(botId)) {
            return null;
        }
        
        try {
            NeuralNetwork network = botNetworks.get(botId);
            PredictionModel model = botPredictionModels.get(botId);
            LearningStats stats = botLearningStats.get(botId);
            
            if (network == null || model == null || stats == null) {
                return null;
            }
            
            // Предсказываем действие
            ActionPrediction prediction = model.predict(network, context);
            
            // Обновляем статистику
            if (prediction != null) {
                stats.recordPrediction();
                totalPredictions.incrementAndGet();
                
                if (prediction.getConfidence() > 0.7) {
                    successfulPredictions.incrementAndGet();
                }
            }
            
            return prediction;
            
        } catch (Exception e) {
            _log.error("Error predicting action for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== АДАПТАЦИЯ ====================
    
    /**
     * Адаптирует модель бота на основе обратной связи
     * 
     * @param botId ID бота
     * @param feedback обратная связь
     * @return true, если адаптация прошла успешно
     */
    public boolean adaptModel(int botId, LearningFeedback feedback) {
        if (!isActive.get() || !isBotRegistered(botId)) {
            return false;
        }
        
        try {
            NeuralNetwork network = botNetworks.get(botId);
            LearningDataProcessor processor = botDataProcessors.get(botId);
            LearningStats stats = botLearningStats.get(botId);
            
            if (network == null || processor == null || stats == null) {
                return false;
            }
            
            // Обрабатываем обратную связь
            ProcessedData feedbackData = processor.processFeedback(feedback);
            
            // Адаптируем нейронную сеть
            boolean success = network.adapt(feedbackData);
            
            // Обновляем статистику
            if (success) {
                stats.recordAdaptationSuccess();
            } else {
                stats.recordAdaptationFailure();
            }
            
            return success;
            
        } catch (Exception e) {
            _log.error("Error adapting model for bot " + botId + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    // ==================== СТАТИСТИКА ====================
    
    /**
     * Возвращает статистику обучения для бота
     * 
     * @param botId ID бота
     * @return статистика обучения
     */
    public LearningStats getBotLearningStats(int botId) {
        return botLearningStats.get(botId);
    }
    
    /**
     * Возвращает общую статистику движка
     * 
     * @return общая статистика
     */
    public EngineStats getEngineStats() {
        return new EngineStats(
            totalDataProcessed.get(),
            totalPredictions.get(),
            successfulPredictions.get(),
            botNetworks.size()
        );
    }
    
    /**
     * Возвращает статистику всех ботов
     * 
     * @return статистика всех ботов
     */
    public Map<Integer, LearningStats> getAllBotStats() {
        return new HashMap<>(botLearningStats);
    }
    
    // ==================== ВНУТРЕННИЕ КЛАССЫ ====================
    
    /**
     * Статистика обучения бота
     */
    public static class LearningStats {
        private final int botId;
        private final AtomicLong trainingSuccesses = new AtomicLong(0);
        private final AtomicLong trainingFailures = new AtomicLong(0);
        private final AtomicLong predictions = new AtomicLong(0);
        private final AtomicLong adaptationSuccesses = new AtomicLong(0);
        private final AtomicLong adaptationFailures = new AtomicLong(0);
        private final AtomicLong lastTrainingTime = new AtomicLong(0);
        private final AtomicLong lastPredictionTime = new AtomicLong(0);
        
        public LearningStats(int botId) {
            this.botId = botId;
        }
        
        public void recordTrainingSuccess() {
            trainingSuccesses.incrementAndGet();
            lastTrainingTime.set(System.currentTimeMillis());
        }
        
        public void recordTrainingFailure() {
            trainingFailures.incrementAndGet();
        }
        
        public void recordPrediction() {
            predictions.incrementAndGet();
            lastPredictionTime.set(System.currentTimeMillis());
        }
        
        public void recordAdaptationSuccess() {
            adaptationSuccesses.incrementAndGet();
        }
        
        public void recordAdaptationFailure() {
            adaptationFailures.incrementAndGet();
        }
        
        // Getters
        public int getBotId() { return botId; }
        public long getTrainingSuccesses() { return trainingSuccesses.get(); }
        public long getTrainingFailures() { return trainingFailures.get(); }
        public long getPredictions() { return predictions.get(); }
        public long getAdaptationSuccesses() { return adaptationSuccesses.get(); }
        public long getAdaptationFailures() { return adaptationFailures.get(); }
        public long getLastTrainingTime() { return lastTrainingTime.get(); }
        public long getLastPredictionTime() { return lastPredictionTime.get(); }
        
        public double getTrainingSuccessRate() {
            long total = trainingSuccesses.get() + trainingFailures.get();
            return total > 0 ? (double) trainingSuccesses.get() / total : 0.0;
        }
        
        public double getAdaptationSuccessRate() {
            long total = adaptationSuccesses.get() + adaptationFailures.get();
            return total > 0 ? (double) adaptationSuccesses.get() / total : 0.0;
        }
    }
    
    /**
     * Общая статистика движка
     */
    public static class EngineStats {
        private final long totalDataProcessed;
        private final long totalPredictions;
        private final long successfulPredictions;
        private final int registeredBots;
        
        public EngineStats(long totalDataProcessed, long totalPredictions, 
                          long successfulPredictions, int registeredBots) {
            this.totalDataProcessed = totalDataProcessed;
            this.totalPredictions = totalPredictions;
            this.successfulPredictions = successfulPredictions;
            this.registeredBots = registeredBots;
        }
        
        public long getTotalDataProcessed() { return totalDataProcessed; }
        public long getTotalPredictions() { return totalPredictions; }
        public long getSuccessfulPredictions() { return successfulPredictions; }
        public int getRegisteredBots() { return registeredBots; }
        
        public double getPredictionSuccessRate() {
            return totalPredictions > 0 ? (double) successfulPredictions / totalPredictions : 0.0;
        }
    }
}
