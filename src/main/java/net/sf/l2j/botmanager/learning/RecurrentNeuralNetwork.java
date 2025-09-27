package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurrent Neural Network (RNN) для обработки временных последовательностей
 * 
 * RNN предназначены для анализа данных с временной зависимостью,
 * таких как последовательности действий ботов, паттерны поведения
 * и другие временные данные в игре Lineage 2.
 * 
 * @author ni-okr
 * @version 3.3
 */
public class RecurrentNeuralNetwork {
    
    private static final Logger _log = Logger.getLogger(RecurrentNeuralNetwork.class.getName());
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** ID бота */
    private final int botId;
    
    /** Список слоев сети */
    private final List<NetworkLayer> layers;
    
    /** Активна ли сеть */
    private boolean isActive;
    
    /** Оптимизатор */
    private AdvancedOptimizer optimizer;
    
    /** Количество эпох обучения */
    private int trainingEpochs;
    
    /** Размер батча */
    private int batchSize;
    
    /** Скорость обучения */
    private double learningRate;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальное количество слоев */
    private static final int MIN_LAYERS = 1;
    
    /** Максимальное количество слоев */
    private static final int MAX_LAYERS = 20;
    
    /** Минимальный размер батча */
    private static final int MIN_BATCH_SIZE = 1;
    
    /** Максимальный размер батча */
    private static final int MAX_BATCH_SIZE = 1000;
    
    /** Минимальная скорость обучения */
    private static final double MIN_LEARNING_RATE = 0.0001;
    
    /** Максимальная скорость обучения */
    private static final double MAX_LEARNING_RATE = 1.0;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новую RNN для бота
     * 
     * @param botId ID бота
     */
    public RecurrentNeuralNetwork(int botId) {
        this.botId = botId;
        this.layers = new ArrayList<>();
        this.isActive = false;
        this.optimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.ADAM, 0.001);
        this.trainingEpochs = 100;
        this.batchSize = 32;
        this.learningRate = 0.001;
        
        _log.info("RecurrentNeuralNetwork created for bot " + botId);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Добавляет LSTM слой
     * 
     * @param hiddenUnits количество скрытых единиц
     * @return true если слой добавлен успешно
     */
    public boolean addLSTMLayer(int hiddenUnits) {
        if (isActive) {
            _log.warn("Cannot add layers to active network for bot " + botId);
            return false;
        }
        
        if (layers.size() >= MAX_LAYERS) {
            _log.warn("Maximum number of layers reached for bot " + botId);
            return false;
        }
        
        try {
            LSTMLayer lstmLayer = new LSTMLayer(hiddenUnits);
            layers.add(lstmLayer);
            
            _log.info("LSTM layer added to RecurrentNeuralNetwork for bot " + botId + 
                     ": hiddenUnits=" + hiddenUnits);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding LSTM layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Добавляет GRU слой
     * 
     * @param hiddenUnits количество скрытых единиц
     * @return true если слой добавлен успешно
     */
    public boolean addGRULayer(int hiddenUnits) {
        if (isActive) {
            _log.warn("Cannot add layers to active network for bot " + botId);
            return false;
        }
        
        if (layers.size() >= MAX_LAYERS) {
            _log.warn("Maximum number of layers reached for bot " + botId);
            return false;
        }
        
        try {
            GRULayer gruLayer = new GRULayer(hiddenUnits);
            layers.add(gruLayer);
            
            _log.info("GRU layer added to RecurrentNeuralNetwork for bot " + botId + 
                     ": hiddenUnits=" + hiddenUnits);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding GRU layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Добавляет Dense слой
     * 
     * @param neurons количество нейронов
     * @param activation функция активации
     * @return true если слой добавлен успешно
     */
    public boolean addDenseLayer(int neurons, ActivationFunction activation) {
        if (isActive) {
            _log.warn("Cannot add layers to active network for bot " + botId);
            return false;
        }
        
        if (layers.size() >= MAX_LAYERS) {
            _log.warn("Maximum number of layers reached for bot " + botId);
            return false;
        }
        
        try {
            DenseLayer denseLayer = new DenseLayer(neurons, activation);
            layers.add(denseLayer);
            
            _log.info("Dense layer added to RecurrentNeuralNetwork for bot " + botId + 
                     ": neurons=" + neurons);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding Dense layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Активирует сеть
     * 
     * @return true если сеть активирована успешно
     */
    public boolean activate() {
        if (layers.isEmpty()) {
            _log.warn("Cannot activate network: no layers added for bot " + botId);
            return false;
        }
        
        try {
            // Инициализируем слои
            initializeLayers();
            
            // Активируем сеть
            isActive = true;
            
            _log.info("RecurrentNeuralNetwork activated for bot " + botId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error activating network for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Деактивирует сеть
     */
    public void deactivate() {
        isActive = false;
        _log.info("RecurrentNeuralNetwork deactivated for bot " + botId);
    }
    
    /**
     * Выполняет прямое распространение
     * 
     * @param inputSequence входная последовательность
     * @return выходная последовательность
     */
    public double[][] forward(double[][] inputSequence) {
        if (!isActive) {
            _log.warn("Cannot perform forward pass: network not active for bot " + botId);
            return null;
        }
        
        if (layers.isEmpty()) {
            _log.warn("Cannot perform forward pass: no layers in network for bot " + botId);
            return null;
        }
        
        if (inputSequence == null || inputSequence.length == 0) {
            _log.warn("Cannot perform forward pass: empty input sequence for bot " + botId);
            return null;
        }
        
        try {
            // Инициализируем слои с правильным размером входа при первом вызове
            if (layers.get(0).getInputSize() == 1) {
                initializeLayersWithInputSize(inputSequence[0].length);
            }
            
            double[][] currentOutput = inputSequence;
            
            // Проходим через все слои
            for (NetworkLayer layer : layers) {
                if (layer instanceof LSTMLayer) {
                    // LSTM слой - обрабатываем последовательность
                    currentOutput = processSequenceWithLSTM((LSTMLayer) layer, currentOutput);
                } else if (layer instanceof GRULayer) {
                    // GRU слой - обрабатываем последовательность
                    currentOutput = processSequenceWithGRU((GRULayer) layer, currentOutput);
                } else if (layer instanceof DenseLayer) {
                    // Dense слой - обрабатываем каждый временной шаг
                    currentOutput = processSequenceWithDense((DenseLayer) layer, currentOutput);
                }
            }
            
            return currentOutput;
            
        } catch (Exception e) {
            _log.error("Error in forward propagation for bot " + botId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Обучает сеть на временных последовательностях
     * 
     * @param inputSequences входные последовательности
     * @param targetSequences целевые последовательности
     * @param epochs количество эпох
     * @return true если обучение прошло успешно
     */
    public boolean train(double[][][] inputSequences, double[][][] targetSequences, int epochs) {
        if (!isActive) {
            _log.warn("Cannot train: network not active for bot " + botId);
            return false;
        }
        
        if (inputSequences == null || targetSequences == null) {
            _log.warn("Invalid training data for bot " + botId);
            return false;
        }
        
        if (inputSequences.length != targetSequences.length) {
            _log.warn("Input and target sequence count mismatch for bot " + botId);
            return false;
        }
        
        try {
            for (int epoch = 0; epoch < epochs; epoch++) {
                double totalError = 0.0;
                
                // Обрабатываем каждую последовательность
                for (int i = 0; i < inputSequences.length; i++) {
                    // Прямое распространение
                    double[][] output = forward(inputSequences[i]);
                    
                    if (output != null) {
                        // Вычисляем ошибку
                        double error = calculateError(output, targetSequences[i]);
                        totalError += error;
                        
                        // Обратное распространение (упрощенное)
                        performBackwardPass(inputSequences[i], targetSequences[i]);
                    }
                }
                
                // Обновляем веса
                updateWeights();
                
                // Логируем прогресс
                if (epoch % 10 == 0) {
                    _log.info("RecurrentNeuralNetwork training epoch " + epoch + 
                             " for bot " + botId + " - Error: " + String.format("%.6f", totalError / inputSequences.length));
                }
            }
            
            return true;
            
        } catch (Exception e) {
            _log.error("Error in training for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Сбрасывает состояние всех рекуррентных слоев
     */
    public void resetStates() {
        for (NetworkLayer layer : layers) {
            if (layer instanceof LSTMLayer) {
                ((LSTMLayer) layer).resetState();
            } else if (layer instanceof GRULayer) {
                ((GRULayer) layer).resetState();
            }
        }
    }
    
    /**
     * Очищает все слои
     */
    public void clearLayers() {
        layers.clear();
        isActive = false;
        _log.info("All layers cleared for RecurrentNeuralNetwork bot " + botId);
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Инициализирует все слои
     */
    private void initializeLayers() {
        int currentInputSize = 1; // Начальный размер входа - будет обновлен при первом forward pass
        
        for (int i = 0; i < layers.size(); i++) {
            NetworkLayer layer = layers.get(i);
            
            if (layer instanceof LSTMLayer) {
                LSTMLayer lstmLayer = (LSTMLayer) layer;
                lstmLayer.initialize(currentInputSize, lstmLayer.getHiddenUnits());
                currentInputSize = lstmLayer.getHiddenUnits();
                
            } else if (layer instanceof GRULayer) {
                GRULayer gruLayer = (GRULayer) layer;
                gruLayer.initialize(currentInputSize, gruLayer.getHiddenUnits());
                currentInputSize = gruLayer.getHiddenUnits();
                
            } else if (layer instanceof DenseLayer) {
                DenseLayer denseLayer = (DenseLayer) layer;
                denseLayer.initialize(currentInputSize, denseLayer.getOutputSize());
                currentInputSize = denseLayer.getOutputSize();
            }
        }
    }
    
    /**
     * Инициализирует все слои с заданным размером входа
     * 
     * @param inputSize размер входа
     */
    private void initializeLayersWithInputSize(int inputSize) {
        int currentInputSize = inputSize;
        
        for (int i = 0; i < layers.size(); i++) {
            NetworkLayer layer = layers.get(i);
            
            if (layer instanceof LSTMLayer) {
                LSTMLayer lstmLayer = (LSTMLayer) layer;
                lstmLayer.initialize(currentInputSize, lstmLayer.getHiddenUnits());
                currentInputSize = lstmLayer.getHiddenUnits();
                
            } else if (layer instanceof GRULayer) {
                GRULayer gruLayer = (GRULayer) layer;
                gruLayer.initialize(currentInputSize, gruLayer.getHiddenUnits());
                currentInputSize = gruLayer.getHiddenUnits();
                
            } else if (layer instanceof DenseLayer) {
                DenseLayer denseLayer = (DenseLayer) layer;
                denseLayer.initialize(currentInputSize, denseLayer.getOutputSize());
                currentInputSize = denseLayer.getOutputSize();
            }
        }
    }
    
    /**
     * Обрабатывает последовательность с LSTM слоем
     * 
     * @param lstmLayer LSTM слой
     * @param inputSequence входная последовательность
     * @return выходная последовательность
     */
    private double[][] processSequenceWithLSTM(LSTMLayer lstmLayer, double[][] inputSequence) {
        double[][] output = new double[inputSequence.length][lstmLayer.getHiddenUnits()];
        
        for (int t = 0; t < inputSequence.length; t++) {
            double[] stepOutput = lstmLayer.forward(inputSequence[t]);
            System.arraycopy(stepOutput, 0, output[t], 0, stepOutput.length);
        }
        
        return output;
    }
    
    /**
     * Обрабатывает последовательность с GRU слоем
     * 
     * @param gruLayer GRU слой
     * @param inputSequence входная последовательность
     * @return выходная последовательность
     */
    private double[][] processSequenceWithGRU(GRULayer gruLayer, double[][] inputSequence) {
        double[][] output = new double[inputSequence.length][gruLayer.getHiddenUnits()];
        
        for (int t = 0; t < inputSequence.length; t++) {
            double[] stepOutput = gruLayer.forward(inputSequence[t]);
            System.arraycopy(stepOutput, 0, output[t], 0, stepOutput.length);
        }
        
        return output;
    }
    
    /**
     * Обрабатывает последовательность с Dense слоем
     * 
     * @param denseLayer Dense слой
     * @param inputSequence входная последовательность
     * @return выходная последовательность
     */
    private double[][] processSequenceWithDense(DenseLayer denseLayer, double[][] inputSequence) {
        double[][] output = new double[inputSequence.length][denseLayer.getOutputSize()];
        
        for (int t = 0; t < inputSequence.length; t++) {
            double[] stepOutput = denseLayer.forward(inputSequence[t]);
            System.arraycopy(stepOutput, 0, output[t], 0, stepOutput.length);
        }
        
        return output;
    }
    
    /**
     * Вычисляет ошибку между выходом и целевыми значениями
     * 
     * @param output выходные данные
     * @param targets целевые данные
     * @return средняя квадратичная ошибка
     */
    private double calculateError(double[][] output, double[][] targets) {
        double totalError = 0.0;
        int count = 0;
        
        for (int t = 0; t < output.length; t++) {
            for (int i = 0; i < output[t].length; i++) {
                double error = output[t][i] - targets[t][i];
                totalError += error * error;
                count++;
            }
        }
        
        return totalError / count;
    }
    
    /**
     * Выполняет обратное распространение (упрощенное)
     * 
     * @param inputSequence входная последовательность
     * @param targetSequence целевая последовательность
     */
    private void performBackwardPass(double[][] inputSequence, double[][] targetSequence) {
        // Упрощенная реализация обратного распространения
        // В реальной реализации здесь должны быть сложные вычисления градиентов
        
        for (int i = layers.size() - 1; i >= 0; i--) {
            NetworkLayer layer = layers.get(i);
            
            if (layer instanceof LSTMLayer) {
                // Обработка LSTM слоя
                LSTMLayer lstmLayer = (LSTMLayer) layer;
                for (int t = inputSequence.length - 1; t >= 0; t--) {
                    double[] gradients = new double[lstmLayer.getHiddenUnits()];
                    // Простые градиенты для демонстрации
                    for (int j = 0; j < gradients.length; j++) {
                        gradients[j] = 0.1;
                    }
                    lstmLayer.backward(inputSequence[t], gradients);
                }
                
            } else if (layer instanceof GRULayer) {
                // Обработка GRU слоя
                GRULayer gruLayer = (GRULayer) layer;
                for (int t = inputSequence.length - 1; t >= 0; t--) {
                    double[] gradients = new double[gruLayer.getHiddenUnits()];
                    // Простые градиенты для демонстрации
                    for (int j = 0; j < gradients.length; j++) {
                        gradients[j] = 0.1;
                    }
                    gruLayer.backward(inputSequence[t], gradients);
                }
                
                } else if (layer instanceof DenseLayer) {
                    // Обработка Dense слоя
                    DenseLayer denseLayer = (DenseLayer) layer;
                    for (int t = inputSequence.length - 1; t >= 0; t--) {
                        double[] gradients = new double[denseLayer.getOutputSize()];
                    // Простые градиенты для демонстрации
                    for (int j = 0; j < gradients.length; j++) {
                        gradients[j] = 0.1;
                    }
                    denseLayer.backward(inputSequence[t], gradients);
                }
            }
        }
    }
    
    /**
     * Обновляет веса всех слоев
     */
    private void updateWeights() {
        for (NetworkLayer layer : layers) {
            layer.updateWeights(learningRate);
        }
    }
    
    // ==================== ГЕТТЕРЫ И СЕТТЕРЫ ====================
    
    /**
     * Возвращает ID бота
     * 
     * @return ID бота
     */
    public int getBotId() {
        return botId;
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
     * Возвращает активна ли сеть
     * 
     * @return true если сеть активна
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Устанавливает скорость обучения
     * 
     * @param learningRate скорость обучения
     */
    public void setLearningRate(double learningRate) {
        if (learningRate >= MIN_LEARNING_RATE && learningRate <= MAX_LEARNING_RATE) {
            this.learningRate = learningRate;
        }
    }
    
    /**
     * Возвращает скорость обучения
     * 
     * @return скорость обучения
     */
    public double getLearningRate() {
        return learningRate;
    }
    
    /**
     * Устанавливает размер батча
     * 
     * @param batchSize размер батча
     */
    public void setBatchSize(int batchSize) {
        if (batchSize >= MIN_BATCH_SIZE && batchSize <= MAX_BATCH_SIZE) {
            this.batchSize = batchSize;
        }
    }
    
    /**
     * Возвращает размер батча
     * 
     * @return размер батча
     */
    public int getBatchSize() {
        return batchSize;
    }
    
    /**
     * Возвращает статистику сети
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== RecurrentNeuralNetwork Statistics ===\n");
        stats.append("Bot ID: ").append(botId).append("\n");
        stats.append("Layers: ").append(layers.size()).append("\n");
        stats.append("Active: ").append(isActive).append("\n");
        stats.append("Learning Rate: ").append(learningRate).append("\n");
        stats.append("Batch Size: ").append(batchSize).append("\n");
        
        for (int i = 0; i < layers.size(); i++) {
            stats.append("Layer ").append(i).append(": ").append(layers.get(i).getType()).append("\n");
        }
        
        return stats.toString();
    }
}
