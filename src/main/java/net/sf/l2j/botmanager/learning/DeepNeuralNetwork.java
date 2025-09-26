package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Глубокая нейронная сеть для машинного обучения ботов
 * 
 * Этот класс представляет собой глубокую нейронную сеть с множеством слоев,
 * специально разработанную для сложных задач обучения ботов. Сеть поддерживает
 * различные архитектуры, включая полносвязные, сверточные и рекуррентные слои.
 * 
 * Ключевые возможности:
 * - Глубокая архитектура с множеством слоев
 * - Различные типы слоев (Dense, Dropout, BatchNormalization)
 * - Продвинутые алгоритмы оптимизации
 * - Регуляризация и предотвращение переобучения
 * - Автоматическое определение архитектуры
 * - Статистика и мониторинг обучения
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
public class DeepNeuralNetwork {
    
    private static final Logger _log = Logger.getLogger(DeepNeuralNetwork.class);
    
    // ==================== КОНСТАНТЫ ====================
    
    /**
     * Максимальное количество слоев
     */
    private static final int MAX_LAYERS = 50;
    
    /**
     * Минимальное количество нейронов в слое
     */
    private static final int MIN_NEURONS = 1;
    
    /**
     * Максимальное количество нейронов в слое
     */
    private static final int MAX_NEURONS = 10000;
    
    /**
     * Минимальная скорость обучения
     */
    private static final double MIN_LEARNING_RATE = 0.0001;
    
    /**
     * Максимальная скорость обучения
     */
    private static final double MAX_LEARNING_RATE = 1.0;
    
    /**
     * Максимальное количество эпох обучения
     */
    private static final int MAX_EPOCHS = 10000;
    
    /**
     * Минимальная ошибка для остановки обучения
     */
    private static final double MIN_ERROR = 0.0001;
    
    // ==================== ПОЛЯ ====================
    
    /**
     * ID бота, для которого создана сеть
     */
    private final int botId;
    
    /**
     * Слои нейронной сети
     */
    private final List<NetworkLayer> layers;
    
    /**
     * Оптимизатор для обучения
     */
    private AdvancedOptimizer optimizer;
    
    /**
     * Флаг активности сети
     */
    private final AtomicBoolean isActive = new AtomicBoolean(false);
    
    /**
     * Количество эпох обучения
     */
    private final AtomicLong trainingEpochs = new AtomicLong(0);
    
    /**
     * Текущая ошибка сети
     */
    private volatile double currentError = Double.MAX_VALUE;
    
    /**
     * Время последнего обучения
     */
    private volatile long lastTrainingTime = 0;
    
    /**
     * История ошибок обучения
     */
    private final List<Double> errorHistory = new ArrayList<>();
    
    /**
     * Максимальный размер истории ошибок
     */
    private static final int MAX_ERROR_HISTORY = 1000;
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор глубокой нейронной сети
     * 
     * @param botId ID бота
     */
    public DeepNeuralNetwork(int botId) {
        this.botId = botId;
        this.layers = new ArrayList<>();
        this.optimizer = new AdvancedOptimizer();
        
        _log.info("DeepNeuralNetwork created for bot " + botId);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Активирует глубокую нейронную сеть
     */
    public void activate() {
        isActive.set(true);
        _log.info("DeepNeuralNetwork activated for bot " + botId);
    }
    
    /**
     * Деактивирует глубокую нейронную сеть
     */
    public void deactivate() {
        isActive.set(false);
        _log.info("DeepNeuralNetwork deactivated for bot " + botId);
    }
    
    /**
     * Проверяет, активна ли сеть
     * 
     * @return true, если сеть активна
     */
    public boolean isActive() {
        return isActive.get();
    }
    
    // ==================== УПРАВЛЕНИЕ СЛОЯМИ ====================
    
    /**
     * Добавляет слой в сеть
     * 
     * @param layer слой для добавления
     * @return true, если слой был добавлен
     */
    public boolean addLayer(NetworkLayer layer) {
        if (layers.size() >= MAX_LAYERS) {
            _log.warn("Maximum number of layers reached for bot " + botId);
            return false;
        }
        
        if (layer == null) {
            _log.warn("Cannot add null layer for bot " + botId);
            return false;
        }
        
        layers.add(layer);
        _log.info("Layer added to DeepNeuralNetwork for bot " + botId + ": " + layer.getType());
        return true;
    }
    
    /**
     * Создает и добавляет полносвязный слой
     * 
     * @param neurons количество нейронов
     * @param activation функция активации
     * @return true, если слой был добавлен
     */
    public boolean addDenseLayer(int neurons, ActivationFunction activation) {
        if (neurons < MIN_NEURONS || neurons > MAX_NEURONS) {
            _log.warn("Invalid number of neurons: " + neurons + " for bot " + botId);
            return false;
        }
        
        DenseLayer layer = new DenseLayer(neurons, activation);
        
        // Определяем размер входа для слоя
        int inputSize = layers.isEmpty() ? 10 : layers.get(layers.size() - 1).getOutputSize();
        layer.initialize(inputSize, neurons);
        
        return addLayer(layer);
    }
    
    /**
     * Создает и добавляет слой Dropout
     * 
     * @param dropoutRate коэффициент dropout
     * @return true, если слой был добавлен
     */
    public boolean addDropoutLayer(double dropoutRate) {
        if (dropoutRate < 0.0 || dropoutRate >= 1.0) {
            _log.warn("Invalid dropout rate: " + dropoutRate + " for bot " + botId);
            return false;
        }
        
        DropoutLayer layer = new DropoutLayer(dropoutRate);
        return addLayer(layer);
    }
    
    /**
     * Создает и добавляет слой BatchNormalization
     * 
     * @return true, если слой был добавлен
     */
    public boolean addBatchNormalizationLayer() {
        BatchNormalizationLayer layer = new BatchNormalizationLayer();
        return addLayer(layer);
    }
    
    /**
     * Удаляет последний слой
     * 
     * @return true, если слой был удален
     */
    public boolean removeLastLayer() {
        if (layers.isEmpty()) {
            return false;
        }
        
        NetworkLayer removed = layers.remove(layers.size() - 1);
        _log.info("Layer removed from DeepNeuralNetwork for bot " + botId + ": " + removed.getType());
        return true;
    }
    
    /**
     * Очищает все слои
     */
    public void clearLayers() {
        layers.clear();
        _log.info("All layers cleared for DeepNeuralNetwork bot " + botId);
    }
    
    // ==================== ПРОДВИЖЕНИЕ ====================
    
    /**
     * Выполняет прямое распространение сигнала через сеть
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    public double[] forward(double[] inputs) {
        if (!isActive.get() || inputs == null || layers.isEmpty()) {
            return null;
        }
        
        try {
            double[] currentInputs = inputs.clone();
            
            // Проходим через все слои
            for (NetworkLayer layer : layers) {
                currentInputs = layer.forward(currentInputs);
                if (currentInputs == null) {
                    _log.error("Layer forward propagation failed for bot " + botId);
                    return null;
                }
            }
            
            return currentInputs;
            
        } catch (Exception e) {
            _log.error("Error in forward propagation for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Выполняет обратное распространение ошибки
     * 
     * @param inputs входные данные
     * @param targets целевые значения
     * @return ошибка обратного распространения
     */
    public double[] backward(double[] inputs, double[] targets) {
        if (!isActive.get() || inputs == null || targets == null || layers.isEmpty()) {
            return null;
        }
        
        try {
            // Прямое распространение для получения активаций
            List<double[]> activations = new ArrayList<>();
            double[] currentInputs = inputs.clone();
            activations.add(currentInputs);
            
            for (NetworkLayer layer : layers) {
                currentInputs = layer.forward(currentInputs);
                if (currentInputs == null) {
                    return null;
                }
                activations.add(currentInputs);
            }
            
            // Вычисляем ошибку выходного слоя
            double[] output = activations.get(activations.size() - 1);
            double[] outputError = new double[output.length];
            for (int i = 0; i < output.length; i++) {
                outputError[i] = targets[i] - output[i];
            }
            
            // Обратное распространение через слои
            double[] currentError = outputError;
            for (int i = layers.size() - 1; i >= 0; i--) {
                NetworkLayer layer = layers.get(i);
                double[] layerInput = activations.get(i);
                currentError = layer.backward(layerInput, currentError);
                if (currentError == null) {
                    return null;
                }
            }
            
            return outputError;
            
        } catch (Exception e) {
            _log.error("Error in backward propagation for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== ОБУЧЕНИЕ ====================
    
    /**
     * Обучает глубокую нейронную сеть
     * 
     * @param data данные для обучения
     * @return true, если обучение прошло успешно
     */
    public boolean train(ProcessedData data) {
        if (!isActive.get() || data == null || layers.isEmpty()) {
            return false;
        }
        
        try {
            double[][] inputs = data.getInputs();
            double[][] targets = data.getTargets();
            
            if (inputs == null || targets == null || inputs.length != targets.length) {
                return false;
            }
            
            int epochs = 0;
            double error = Double.MAX_VALUE;
            double previousError = Double.MAX_VALUE;
            int patience = 0;
            int maxPatience = 100; // Early stopping patience
            
            while (epochs < MAX_EPOCHS && error > MIN_ERROR && patience < maxPatience) {
                error = 0.0;
                
                // Проходим по всем обучающим примерам
                for (int i = 0; i < inputs.length; i++) {
                    double[] input = inputs[i];
                    double[] target = targets[i];
                    
                    // Прямое распространение
                    double[] output = forward(input);
                    if (output == null) {
                        continue;
                    }
                    
                    // Вычисляем ошибку
                    double[] sampleError = new double[output.length];
                    for (int j = 0; j < output.length; j++) {
                        sampleError[j] = target[j] - output[j];
                        error += sampleError[j] * sampleError[j];
                    }
                    
                    // Обратное распространение
                    backward(input, target);
                    
                    // Обновляем веса
                    optimizer.updateWeights(layers);
                }
                
                error /= inputs.length;
                errorHistory.add(error);
                
                // Ограничиваем размер истории ошибок
                if (errorHistory.size() > MAX_ERROR_HISTORY) {
                    errorHistory.remove(0);
                }
                
                // Early stopping
                if (error >= previousError) {
                    patience++;
                } else {
                    patience = 0;
                }
                
                previousError = error;
                epochs++;
                
                // Логируем прогресс каждые 100 эпох
                if (epochs % 100 == 0) {
                    _log.debug("Training epoch " + epochs + " for bot " + botId + 
                              ", error: " + String.format("%.6f", error));
                }
            }
            
            currentError = error;
            trainingEpochs.addAndGet(epochs);
            lastTrainingTime = System.currentTimeMillis();
            
            _log.info("DeepNeuralNetwork training completed for bot " + botId + 
                     " in " + epochs + " epochs with error " + String.format("%.6f", error));
            
            return true;
            
        } catch (Exception e) {
            _log.error("Error training DeepNeuralNetwork for bot " + botId + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Адаптирует сеть на основе обратной связи
     * 
     * @param data данные обратной связи
     * @return true, если адаптация прошла успешно
     */
    public boolean adapt(ProcessedData data) {
        if (!isActive.get() || data == null) {
            return false;
        }
        
        try {
            // Используем меньшую скорость обучения для адаптации
            double originalLearningRate = optimizer.getLearningRate();
            optimizer.setLearningRate(originalLearningRate * 0.1);
            
            boolean success = train(data);
            
            // Восстанавливаем исходную скорость обучения
            optimizer.setLearningRate(originalLearningRate);
            
            return success;
            
        } catch (Exception e) {
            _log.error("Error adapting DeepNeuralNetwork for bot " + botId + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    // ==================== НАСТРОЙКА ====================
    
    /**
     * Устанавливает оптимизатор
     * 
     * @param optimizer оптимизатор
     */
    public void setOptimizer(AdvancedOptimizer optimizer) {
        this.optimizer = optimizer;
        _log.info("Optimizer set for DeepNeuralNetwork bot " + botId);
    }
    
    /**
     * Возвращает оптимизатор
     * 
     * @return оптимизатор
     */
    public AdvancedOptimizer getOptimizer() {
        return optimizer;
    }
    
    /**
     * Устанавливает скорость обучения
     * 
     * @param learningRate скорость обучения
     */
    public void setLearningRate(double learningRate) {
        optimizer.setLearningRate(Math.max(MIN_LEARNING_RATE, Math.min(MAX_LEARNING_RATE, learningRate)));
    }
    
    /**
     * Возвращает скорость обучения
     * 
     * @return скорость обучения
     */
    public double getLearningRate() {
        return optimizer.getLearningRate();
    }
    
    // ==================== СТАТИСТИКА ====================
    
    /**
     * Возвращает текущую ошибку сети
     * 
     * @return текущая ошибка
     */
    public double getCurrentError() {
        return currentError;
    }
    
    /**
     * Возвращает количество эпох обучения
     * 
     * @return количество эпох
     */
    public long getTrainingEpochs() {
        return trainingEpochs.get();
    }
    
    /**
     * Возвращает время последнего обучения
     * 
     * @return время последнего обучения
     */
    public long getLastTrainingTime() {
        return lastTrainingTime;
    }
    
    /**
     * Возвращает историю ошибок
     * 
     * @return история ошибок
     */
    public List<Double> getErrorHistory() {
        return new ArrayList<>(errorHistory);
    }
    
    /**
     * Возвращает количество слоев
     * 
     * @return количество слоев
     */
    public int getLayerCount() {
        return layers.size();
    }
    
    /**
     * Возвращает архитектуру сети
     * 
     * @return строка с архитектурой
     */
    public String getArchitecture() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeepNeuralNetwork[");
        for (int i = 0; i < layers.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(layers.get(i).getType());
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Возвращает статистику сети
     * 
     * @return статистика сети
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== DeepNeuralNetwork Statistics ===\n");
        stats.append("Bot ID: ").append(botId).append("\n");
        stats.append("Layers: ").append(layers.size()).append("\n");
        stats.append("Architecture: ").append(getArchitecture()).append("\n");
        stats.append("Current Error: ").append(String.format("%.6f", currentError)).append("\n");
        stats.append("Training Epochs: ").append(trainingEpochs.get()).append("\n");
        stats.append("Learning Rate: ").append(String.format("%.6f", getLearningRate())).append("\n");
        stats.append("Optimizer: ").append(optimizer.getType()).append("\n");
        stats.append("Active: ").append(isActive.get()).append("\n");
        
        if (!errorHistory.isEmpty()) {
            stats.append("Error History (last 10): ");
            int start = Math.max(0, errorHistory.size() - 10);
            for (int i = start; i < errorHistory.size(); i++) {
                if (i > start) stats.append(", ");
                stats.append(String.format("%.4f", errorHistory.get(i)));
            }
            stats.append("\n");
        }
        
        return stats.toString();
    }
}
