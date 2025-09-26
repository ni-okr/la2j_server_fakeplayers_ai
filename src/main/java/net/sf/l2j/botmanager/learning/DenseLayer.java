package net.sf.l2j.botmanager.learning;

import java.util.Random;

/**
 * Полносвязный слой нейронной сети
 * 
 * Этот класс реализует полносвязный (dense) слой нейронной сети,
 * где каждый нейрон связан со всеми нейронами предыдущего слоя.
 * Слой поддерживает различные функции активации и методы инициализации весов.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
public class DenseLayer extends NetworkLayer {
    
    // ==================== ПОЛЯ ====================
    
    /**
     * Веса слоя [inputSize][outputSize]
     */
    private double[][] weights;
    
    /**
     * Смещения [outputSize]
     */
    private double[] biases;
    
    /**
     * Градиенты весов [inputSize][outputSize]
     */
    private double[][] weightGradients;
    
    /**
     * Градиенты смещений [outputSize]
     */
    private double[] biasGradients;
    
    /**
     * Функция активации
     */
    private final ActivationFunction activationFunction;
    
    /**
     * Активации нейронов (для обратного распространения)
     */
    private double[] activations;
    
    /**
     * Взвешенные суммы (для обратного распространения)
     */
    private double[] weightedSums;
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор полносвязного слоя
     * 
     * @param outputSize количество выходных нейронов
     * @param activationFunction функция активации
     */
    public DenseLayer(int outputSize, ActivationFunction activationFunction) {
        super(LayerType.DENSE);
        this.outputSize = outputSize;
        this.activationFunction = activationFunction;
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Инициализирует слой
     * 
     * @param inputSize размер входа
     * @param outputSize размер выхода
     */
    @Override
    public void initialize(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        
        // Инициализируем веса и смещения
        initializeWeights();
        initializeBiases();
        
        // Инициализируем градиенты
        weightGradients = new double[inputSize][outputSize];
        biasGradients = new double[outputSize];
    }
    
    /**
     * Выполняет прямое распространение
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    @Override
    public double[] forward(double[] inputs) {
        if (inputs.length != inputSize) {
            throw new IllegalArgumentException("Input size mismatch: expected " + inputSize + ", got " + inputs.length);
        }
        
        // Вычисляем взвешенные суммы
        weightedSums = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            weightedSums[j] = biases[j];
            for (int i = 0; i < inputSize; i++) {
                weightedSums[j] += inputs[i] * weights[i][j];
            }
        }
        
        // Применяем функцию активации
        activations = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            activations[j] = activationFunction.activate(weightedSums[j]);
        }
        
        return activations.clone();
    }
    
    /**
     * Выполняет обратное распространение
     * 
     * @param inputs входные данные
     * @param outputGradients градиенты выходного слоя
     * @return градиенты входного слоя
     */
    @Override
    public double[] backward(double[] inputs, double[] outputGradients) {
        if (outputGradients.length != outputSize) {
            throw new IllegalArgumentException("Output gradient size mismatch: expected " + outputSize + ", got " + outputGradients.length);
        }
        
        // Вычисляем градиенты по взвешенным суммам
        double[] weightedSumGradients = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            weightedSumGradients[j] = outputGradients[j] * activationFunction.derivative(weightedSums[j]);
        }
        
        // Вычисляем градиенты весов
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightGradients[i][j] = inputs[i] * weightedSumGradients[j];
            }
        }
        
        // Вычисляем градиенты смещений
        for (int j = 0; j < outputSize; j++) {
            biasGradients[j] = weightedSumGradients[j];
        }
        
        // Вычисляем градиенты входного слоя
        double[] inputGradients = new double[inputSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                inputGradients[i] += weights[i][j] * weightedSumGradients[j];
            }
        }
        
        return inputGradients;
    }
    
    /**
     * Обновляет веса слоя
     * 
     * @param learningRate скорость обучения
     */
    @Override
    public void updateWeights(double learningRate) {
        // Обновляем веса
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights[i][j] += learningRate * weightGradients[i][j];
            }
        }
        
        // Обновляем смещения
        for (int j = 0; j < outputSize; j++) {
            biases[j] += learningRate * biasGradients[j];
        }
    }
    
    // ==================== ИНИЦИАЛИЗАЦИЯ ====================
    
    /**
     * Инициализирует веса слоя
     */
    private void initializeWeights() {
        weights = new double[inputSize][outputSize];
        Random random = new Random();
        
        // Используем инициализацию Xavier/Glorot
        double limit = Math.sqrt(6.0 / (inputSize + outputSize));
        
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights[i][j] = random.nextGaussian() * limit;
            }
        }
    }
    
    /**
     * Инициализирует смещения слоя
     */
    private void initializeBiases() {
        biases = new double[outputSize];
        // Инициализируем смещения нулями
        for (int j = 0; j < outputSize; j++) {
            biases[j] = 0.0;
        }
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Возвращает веса слоя
     * 
     * @return веса слоя
     */
    public double[][] getWeights() {
        return weights.clone();
    }
    
    /**
     * Возвращает смещения слоя
     * 
     * @return смещения слоя
     */
    public double[] getBiases() {
        return biases.clone();
    }
    
    /**
     * Возвращает функцию активации
     * 
     * @return функция активации
     */
    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }
    
    /**
     * Возвращает активации нейронов
     * 
     * @return активации нейронов
     */
    public double[] getActivations() {
        return activations != null ? activations.clone() : null;
    }
    
    /**
     * Возвращает взвешенные суммы
     * 
     * @return взвешенные суммы
     */
    public double[] getWeightedSums() {
        return weightedSums != null ? weightedSums.clone() : null;
    }
}
