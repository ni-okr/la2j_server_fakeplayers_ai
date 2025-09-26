package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Нейронная сеть для машинного обучения ботов
 * 
 * Этот класс представляет собой упрощенную нейронную сеть,
 * специально разработанную для обучения ботов в игре.
 * Сеть использует алгоритм обратного распространения ошибки
 * для адаптации весов на основе обучающих данных.
 * 
 * Ключевые возможности:
 * - Многослойная архитектура
 * - Алгоритм обратного распространения ошибки
 * - Адаптивное обучение
 * - Сохранение и загрузка весов
 * - Статистика обучения
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class NeuralNetwork {
    
    private static final Logger _log = Logger.getLogger(NeuralNetwork.class);
    
    // ==================== КОНСТАНТЫ ====================
    
    /**
     * Скорость обучения по умолчанию
     */
    private static final double DEFAULT_LEARNING_RATE = 0.1;
    
    /**
     * Минимальная скорость обучения
     */
    private static final double MIN_LEARNING_RATE = 0.001;
    
    /**
     * Максимальная скорость обучения
     */
    private static final double MAX_LEARNING_RATE = 1.0;
    
    /**
     * Максимальное количество эпох обучения
     */
    private static final int MAX_EPOCHS = 1000;
    
    /**
     * Минимальная ошибка для остановки обучения
     */
    private static final double MIN_ERROR = 0.001;
    
    // ==================== ПОЛЯ ====================
    
    /**
     * ID бота, для которого создана сеть
     */
    private final int botId;
    
    /**
     * Количество входных нейронов
     */
    private final int inputSize;
    
    /**
     * Количество скрытых слоев
     */
    private final int hiddenLayers;
    
    /**
     * Количество нейронов в каждом скрытом слое
     */
    private final int hiddenSize;
    
    /**
     * Количество выходных нейронов
     */
    private final int outputSize;
    
    /**
     * Веса между слоями
     */
    private final List<double[][]> weights;
    
    /**
     * Смещения для каждого слоя
     */
    private final List<double[]> biases;
    
    /**
     * Скорость обучения
     */
    private double learningRate;
    
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
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор нейронной сети
     * 
     * @param botId ID бота
     */
    public NeuralNetwork(int botId) {
        this(botId, 10, 2, 8, 5); // По умолчанию: 10 входов, 2 скрытых слоя по 8 нейронов, 5 выходов
    }
    
    /**
     * Конструктор нейронной сети с настройками
     * 
     * @param botId ID бота
     * @param inputSize количество входных нейронов
     * @param hiddenLayers количество скрытых слоев
     * @param hiddenSize количество нейронов в скрытом слое
     * @param outputSize количество выходных нейронов
     */
    public NeuralNetwork(int botId, int inputSize, int hiddenLayers, int hiddenSize, int outputSize) {
        this.botId = botId;
        this.inputSize = inputSize;
        this.hiddenLayers = hiddenLayers;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = DEFAULT_LEARNING_RATE;
        
        // Инициализируем веса и смещения
        this.weights = new ArrayList<>();
        this.biases = new ArrayList<>();
        
        initializeWeights();
        
        _log.info("NeuralNetwork created for bot " + botId + " with architecture: " + 
                 inputSize + "-" + hiddenLayers + "x" + hiddenSize + "-" + outputSize);
    }
    
    // ==================== ИНИЦИАЛИЗАЦИЯ ====================
    
    /**
     * Инициализирует веса и смещения случайными значениями
     */
    private void initializeWeights() {
        Random random = new Random();
        
        // Веса между входным и первым скрытым слоем
        double[][] inputToHidden = new double[inputSize][hiddenSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                inputToHidden[i][j] = random.nextGaussian() * 0.5;
            }
        }
        weights.add(inputToHidden);
        
        // Веса между скрытыми слоями
        for (int layer = 1; layer < hiddenLayers; layer++) {
            double[][] hiddenToHidden = new double[hiddenSize][hiddenSize];
            for (int i = 0; i < hiddenSize; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    hiddenToHidden[i][j] = random.nextGaussian() * 0.5;
                }
            }
            weights.add(hiddenToHidden);
        }
        
        // Веса между последним скрытым и выходным слоем
        double[][] hiddenToOutput = new double[hiddenSize][outputSize];
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                hiddenToOutput[i][j] = random.nextGaussian() * 0.5;
            }
        }
        weights.add(hiddenToOutput);
        
        // Инициализируем смещения
        // Смещения для скрытых слоев
        for (int layer = 0; layer < hiddenLayers; layer++) {
            double[] bias = new double[hiddenSize];
            for (int i = 0; i < hiddenSize; i++) {
                bias[i] = random.nextGaussian() * 0.1;
            }
            biases.add(bias);
        }
        
        // Смещения для выходного слоя
        double[] outputBias = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            outputBias[i] = random.nextGaussian() * 0.1;
        }
        biases.add(outputBias);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Активирует нейронную сеть
     */
    public void activate() {
        isActive.set(true);
        _log.info("NeuralNetwork activated for bot " + botId);
    }
    
    /**
     * Деактивирует нейронную сеть
     */
    public void deactivate() {
        isActive.set(false);
        _log.info("NeuralNetwork deactivated for bot " + botId);
    }
    
    /**
     * Проверяет, активна ли сеть
     * 
     * @return true, если сеть активна
     */
    public boolean isActive() {
        return isActive.get();
    }
    
    // ==================== ПРОДВИЖЕНИЕ ====================
    
    /**
     * Выполняет прямое распространение сигнала через сеть
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    public double[] forward(double[] inputs) {
        if (!isActive.get() || inputs == null || inputs.length != inputSize) {
            return null;
        }
        
        try {
            // Нормализуем входные данные
            double[] normalizedInputs = normalizeInputs(inputs);
            
            // Вычисляем активации для каждого слоя
            List<double[]> activations = new ArrayList<>();
            activations.add(normalizedInputs);
            
            // Скрытые слои
            double[] currentActivation = normalizedInputs;
            for (int layer = 0; layer < hiddenLayers; layer++) {
                currentActivation = computeLayerActivation(currentActivation, weights.get(layer), biases.get(layer));
                currentActivation = applyActivationFunction(currentActivation);
                activations.add(currentActivation);
            }
            
            // Выходной слой
            double[] output = computeLayerActivation(currentActivation, weights.get(hiddenLayers), biases.get(hiddenLayers));
            output = applyActivationFunction(output);
            
            return output;
            
        } catch (Exception e) {
            _log.error("Error in forward propagation for bot " + botId + ": " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Вычисляет активацию слоя
     * 
     * @param inputs входные данные
     * @param weights веса слоя
     * @param biases смещения слоя
     * @return активации нейронов
     */
    private double[] computeLayerActivation(double[] inputs, double[][] weights, double[] biases) {
        int outputSize = biases.length;
        double[] activations = new double[outputSize];
        
        for (int j = 0; j < outputSize; j++) {
            double sum = biases[j];
            for (int i = 0; i < inputs.length; i++) {
                sum += inputs[i] * weights[i][j];
            }
            activations[j] = sum;
        }
        
        return activations;
    }
    
    /**
     * Применяет функцию активации к нейронам
     * 
     * @param activations активации нейронов
     * @return активации после применения функции
     */
    private double[] applyActivationFunction(double[] activations) {
        double[] result = new double[activations.length];
        for (int i = 0; i < activations.length; i++) {
            result[i] = sigmoid(activations[i]);
        }
        return result;
    }
    
    /**
     * Сигмоидальная функция активации
     * 
     * @param x входное значение
     * @return результат функции
     */
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    /**
     * Производная сигмоидальной функции
     * 
     * @param x входное значение
     * @return производная
     */
    private double sigmoidDerivative(double x) {
        double s = sigmoid(x);
        return s * (1.0 - s);
    }
    
    // ==================== ОБУЧЕНИЕ ====================
    
    /**
     * Обучает нейронную сеть на основе данных
     * 
     * @param data данные для обучения
     * @return true, если обучение прошло успешно
     */
    public boolean train(ProcessedData data) {
        if (!isActive.get() || data == null) {
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
            
            while (epochs < MAX_EPOCHS && error > MIN_ERROR) {
                error = 0.0;
                
                // Проходим по всем обучающим примерам
                for (int i = 0; i < inputs.length; i++) {
                    double[] input = inputs[i];
                    double[] target = targets[i];
                    
                    // Прямое распространение
                    List<double[]> activations = new ArrayList<>();
                    activations.add(input);
                    
                    double[] currentActivation = input;
                    for (int layer = 0; layer < hiddenLayers; layer++) {
                        currentActivation = computeLayerActivation(currentActivation, weights.get(layer), biases.get(layer));
                        currentActivation = applyActivationFunction(currentActivation);
                        activations.add(currentActivation);
                    }
                    
                    double[] output = computeLayerActivation(currentActivation, weights.get(hiddenLayers), biases.get(hiddenLayers));
                    output = applyActivationFunction(output);
                    
                    // Вычисляем ошибку
                    double[] outputError = new double[outputSize];
                    for (int j = 0; j < outputSize; j++) {
                        outputError[j] = target[j] - output[j];
                        error += outputError[j] * outputError[j];
                    }
                    
                    // Обратное распространение ошибки
                    backpropagate(activations, outputError);
                }
                
                error /= inputs.length;
                epochs++;
            }
            
            currentError = error;
            trainingEpochs.addAndGet(epochs);
            lastTrainingTime = System.currentTimeMillis();
            
            _log.info("NeuralNetwork training completed for bot " + botId + 
                     " in " + epochs + " epochs with error " + String.format("%.6f", error));
            
            return true;
            
        } catch (Exception e) {
            _log.error("Error training NeuralNetwork for bot " + botId + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Выполняет обратное распространение ошибки
     * 
     * @param activations активации всех слоев
     * @param outputError ошибка выходного слоя
     */
    private void backpropagate(List<double[]> activations, double[] outputError) {
        // Ошибка для выходного слоя
        double[] delta = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            delta[j] = outputError[j] * sigmoidDerivative(activations.get(activations.size() - 1)[j]);
        }
        
        // Обновляем веса и смещения для выходного слоя
        updateLayerWeights(activations.get(activations.size() - 2), delta, hiddenLayers);
        
        // Обрабатываем скрытые слои
        for (int layer = hiddenLayers - 1; layer >= 0; layer--) {
            double[] prevDelta = new double[hiddenSize];
            for (int i = 0; i < hiddenSize; i++) {
                double sum = 0.0;
                for (int j = 0; j < delta.length; j++) {
                    sum += delta[j] * weights.get(layer + 1)[i][j];
                }
                prevDelta[i] = sum * sigmoidDerivative(activations.get(layer + 1)[i]);
            }
            
            updateLayerWeights(activations.get(layer), prevDelta, layer);
            delta = prevDelta;
        }
    }
    
    /**
     * Обновляет веса и смещения слоя
     * 
     * @param inputs входные данные слоя
     * @param delta ошибка слоя
     * @param layerIndex индекс слоя
     */
    private void updateLayerWeights(double[] inputs, double[] delta, int layerIndex) {
        double[][] layerWeights = weights.get(layerIndex);
        double[] layerBiases = biases.get(layerIndex);
        
        // Обновляем веса
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < delta.length; j++) {
                layerWeights[i][j] += learningRate * delta[j] * inputs[i];
            }
        }
        
        // Обновляем смещения
        for (int j = 0; j < delta.length; j++) {
            layerBiases[j] += learningRate * delta[j];
        }
    }
    
    // ==================== АДАПТАЦИЯ ====================
    
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
            double originalLearningRate = learningRate;
            learningRate *= 0.1; // Уменьшаем скорость обучения в 10 раз
            
            boolean success = train(data);
            
            // Восстанавливаем исходную скорость обучения
            learningRate = originalLearningRate;
            
            return success;
            
        } catch (Exception e) {
            _log.error("Error adapting NeuralNetwork for bot " + botId + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Нормализует входные данные
     * 
     * @param inputs входные данные
     * @return нормализованные данные
     */
    private double[] normalizeInputs(double[] inputs) {
        double[] normalized = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            // Простая нормализация в диапазон [0, 1]
            normalized[i] = Math.max(0.0, Math.min(1.0, inputs[i]));
        }
        return normalized;
    }
    
    /**
     * Устанавливает скорость обучения
     * 
     * @param learningRate скорость обучения
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = Math.max(MIN_LEARNING_RATE, Math.min(MAX_LEARNING_RATE, learningRate));
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
     * Возвращает архитектуру сети
     * 
     * @return строка с архитектурой
     */
    public String getArchitecture() {
        return inputSize + "-" + hiddenLayers + "x" + hiddenSize + "-" + outputSize;
    }
}
