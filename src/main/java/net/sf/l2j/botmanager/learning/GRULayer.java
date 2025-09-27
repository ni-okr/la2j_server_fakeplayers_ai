package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.Random;

/**
 * GRU (Gated Recurrent Unit) слой для рекуррентных нейронных сетей
 * 
 * GRU слой является упрощенной версией LSTM, которая сохраняет
 * способность запоминать долгосрочные зависимости, но имеет меньше
 * параметров и более простую архитектуру. Это делает GRU более
 * эффективным для некоторых задач.
 * 
 * @author ni-okr
 * @version 3.3
 */
public class GRULayer extends NetworkLayer {
    
    private static final Logger _log = Logger.getLogger(GRULayer.class.getName());
    
    // ==================== ПАРАМЕТРЫ СЛОЯ ====================
    
    /** Количество скрытых единиц */
    private final int hiddenUnits;
    
    /** Размер входа */
    private int inputSize;
    
    /** Веса для сброса гейта */
    private double[][] resetWeights;
    private double[] resetBias;
    
    /** Веса для обновления гейта */
    private double[][] updateWeights;
    private double[] updateBias;
    
    /** Веса для кандидатов */
    private double[][] candidateWeights;
    private double[] candidateBias;
    
    /** Скрытое состояние */
    private double[] hiddenState;
    
    /** Предыдущее скрытое состояние */
    private double[] prevHiddenState;
    
    /** Градиенты весов */
    private double[][] resetWeightGradients;
    private double[] resetBiasGradients;
    private double[][] updateWeightGradients;
    private double[] updateBiasGradients;
    private double[][] candidateWeightGradients;
    private double[] candidateBiasGradients;
    
    /** Кэш для входных данных */
    private double[] inputCache;
    
    /** Генератор случайных чисел */
    private final Random random;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальное количество скрытых единиц */
    private static final int MIN_HIDDEN_UNITS = 1;
    
    /** Максимальное количество скрытых единиц */
    private static final int MAX_HIDDEN_UNITS = 1024;
    
    /** Минимальный размер входа */
    private static final int MIN_INPUT_SIZE = 1;
    
    /** Максимальный размер входа */
    private static final int MAX_INPUT_SIZE = 1000;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый GRU слой
     * 
     * @param hiddenUnits количество скрытых единиц
     */
    public GRULayer(int hiddenUnits) {
        super(NetworkLayer.LayerType.GRU);
        this.hiddenUnits = hiddenUnits;
        this.random = new Random();
        
        // Валидация параметров
        if (!isValidParameters()) {
            throw new IllegalArgumentException("Invalid GRU parameters");
        }
        
        _log.info("GRULayer created: hiddenUnits=" + hiddenUnits);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Инициализирует слой с заданными размерами
     * 
     * @param inputSize размер входа
     * @param outputSize размер выхода (игнорируется для GRU)
     */
    @Override
    public void initialize(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        
        // Инициализируем веса
        initializeWeights();
        
        // Инициализируем состояния
        initializeStates();
        
        // Инициализируем градиенты
        initializeGradients();
        
        _log.info("GRULayer initialized: inputSize=" + inputSize + 
                 ", hiddenUnits=" + hiddenUnits);
    }
    
    /**
     * Выполняет прямое распространение
     * 
     * @param inputs входные данные
     * @return выходные данные (скрытое состояние)
     */
    @Override
    public double[] forward(double[] inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (inputs.length != inputSize) {
            throw new IllegalArgumentException("Input size mismatch");
        }
        
        // Сохраняем входные данные
        this.inputCache = inputs.clone();
        
        // Выполняем GRU forward pass
        double[] output = performGRUForward(inputs);
        
        return output;
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
        if (outputGradients == null) {
            throw new IllegalArgumentException("Output gradients cannot be null");
        }
        
        if (outputGradients.length != hiddenUnits) {
            throw new IllegalArgumentException("Output gradients size mismatch");
        }
        
        if (inputCache == null) {
            throw new IllegalStateException("Forward pass must be called before backward pass");
        }
        
        // Выполняем GRU backward pass
        double[] inputGradients = performGRUBackward(inputCache, outputGradients);
        
        return inputGradients;
    }
    
    /**
     * Обновляет веса с помощью оптимизатора
     * 
     * @param learningRate скорость обучения
     */
    @Override
    public void updateWeights(double learningRate) {
        // Обновляем веса сброса гейта
        updateWeightMatrix(resetWeights, resetWeightGradients, learningRate);
        updateBiasVector(resetBias, resetBiasGradients, learningRate);
        
        // Обновляем веса обновления гейта
        updateWeightMatrix(updateWeights, updateWeightGradients, learningRate);
        updateBiasVector(updateBias, updateBiasGradients, learningRate);
        
        // Обновляем веса кандидатов
        updateWeightMatrix(candidateWeights, candidateWeightGradients, learningRate);
        updateBiasVector(candidateBias, candidateBiasGradients, learningRate);
        
        // Сбрасываем градиенты
        resetGradients();
    }
    
    /**
     * Возвращает размер выхода слоя
     * 
     * @return количество выходных элементов
     */
    @Override
    public int getOutputSize() {
        return hiddenUnits;
    }
    
    /**
     * Сбрасывает состояние слоя
     */
    public void resetState() {
        if (hiddenState != null) {
            System.arraycopy(prevHiddenState, 0, hiddenState, 0, hiddenUnits);
        }
    }
    
    /**
     * Устанавливает начальное состояние
     * 
     * @param initialHiddenState начальное скрытое состояние
     */
    public void setInitialState(double[] initialHiddenState) {
        if (initialHiddenState != null && initialHiddenState.length == hiddenUnits) {
            System.arraycopy(initialHiddenState, 0, hiddenState, 0, hiddenUnits);
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Проверяет валидность параметров
     * 
     * @return true если параметры корректны
     */
    private boolean isValidParameters() {
        return hiddenUnits >= MIN_HIDDEN_UNITS && hiddenUnits <= MAX_HIDDEN_UNITS;
    }
    
    /**
     * Инициализирует веса
     */
    private void initializeWeights() {
        // Инициализация весов сброса гейта
        resetWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        resetBias = new double[hiddenUnits];
        initializeWeightMatrix(resetWeights);
        initializeBiasVector(resetBias);
        
        // Инициализация весов обновления гейта
        updateWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        updateBias = new double[hiddenUnits];
        initializeWeightMatrix(updateWeights);
        initializeBiasVector(updateBias);
        
        // Инициализация весов кандидатов
        candidateWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        candidateBias = new double[hiddenUnits];
        initializeWeightMatrix(candidateWeights);
        initializeBiasVector(candidateBias);
    }
    
    /**
     * Инициализирует состояния
     */
    private void initializeStates() {
        hiddenState = new double[hiddenUnits];
        prevHiddenState = new double[hiddenUnits];
        
        // Инициализируем нулями
        for (int i = 0; i < hiddenUnits; i++) {
            hiddenState[i] = 0.0;
            prevHiddenState[i] = 0.0;
        }
    }
    
    /**
     * Инициализирует градиенты
     */
    private void initializeGradients() {
        resetWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        resetBiasGradients = new double[hiddenUnits];
        updateWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        updateBiasGradients = new double[hiddenUnits];
        candidateWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        candidateBiasGradients = new double[hiddenUnits];
    }
    
    /**
     * Инициализирует матрицу весов
     * 
     * @param weights матрица весов
     */
    private void initializeWeightMatrix(double[][] weights) {
        double limit = Math.sqrt(6.0 / (weights[0].length + hiddenUnits));
        
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = (random.nextDouble() * 2 - 1) * limit;
            }
        }
    }
    
    /**
     * Инициализирует вектор смещений
     * 
     * @param bias вектор смещений
     */
    private void initializeBiasVector(double[] bias) {
        for (int i = 0; i < bias.length; i++) {
            bias[i] = 0.0;
        }
    }
    
    /**
     * Выполняет GRU forward pass
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    private double[] performGRUForward(double[] inputs) {
        // Объединяем входные данные и предыдущее скрытое состояние
        double[] combined = new double[inputSize + hiddenUnits];
        System.arraycopy(inputs, 0, combined, 0, inputSize);
        System.arraycopy(prevHiddenState, 0, combined, inputSize, hiddenUnits);
        
        // Сохраняем предыдущее состояние
        System.arraycopy(hiddenState, 0, prevHiddenState, 0, hiddenUnits);
        
        // Вычисляем сброс гейт
        double[] resetGate = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = resetBias[i];
            for (int j = 0; j < combined.length; j++) {
                sum += combined[j] * resetWeights[i][j];
            }
            resetGate[i] = ActivationFunction.sigmoid(sum);
        }
        
        // Вычисляем обновление гейт
        double[] updateGate = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = updateBias[i];
            for (int j = 0; j < combined.length; j++) {
                sum += combined[j] * updateWeights[i][j];
            }
            updateGate[i] = ActivationFunction.sigmoid(sum);
        }
        
        // Вычисляем кандидатов с учетом сброса гейта
        double[] resetCombined = new double[inputSize + hiddenUnits];
        System.arraycopy(inputs, 0, resetCombined, 0, inputSize);
        for (int i = 0; i < hiddenUnits; i++) {
            resetCombined[inputSize + i] = resetGate[i] * prevHiddenState[i];
        }
        
        double[] candidates = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = candidateBias[i];
            for (int j = 0; j < resetCombined.length; j++) {
                sum += resetCombined[j] * candidateWeights[i][j];
            }
            candidates[i] = ActivationFunction.tanh(sum);
        }
        
        // Обновляем скрытое состояние
        for (int i = 0; i < hiddenUnits; i++) {
            hiddenState[i] = (1 - updateGate[i]) * prevHiddenState[i] + updateGate[i] * candidates[i];
        }
        
        return hiddenState.clone();
    }
    
    /**
     * Выполняет GRU backward pass
     * 
     * @param inputs входные данные
     * @param outputGradients градиенты выходного слоя
     * @return градиенты входного слоя
     */
    private double[] performGRUBackward(double[] inputs, double[] outputGradients) {
        // Упрощенная реализация обратного распространения
        // В реальной реализации здесь должны быть сложные вычисления градиентов
        
        double[] inputGradients = new double[inputSize];
        
        // Простое распространение градиентов назад
        for (int i = 0; i < inputSize; i++) {
            inputGradients[i] = outputGradients[i % hiddenUnits] * 0.1;
        }
        
        return inputGradients;
    }
    
    /**
     * Обновляет матрицу весов
     * 
     * @param weights матрица весов
     * @param gradients градиенты
     * @param learningRate скорость обучения
     */
    private void updateWeightMatrix(double[][] weights, double[][] gradients, double learningRate) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] -= learningRate * gradients[i][j];
            }
        }
    }
    
    /**
     * Обновляет вектор смещений
     * 
     * @param bias вектор смещений
     * @param gradients градиенты
     * @param learningRate скорость обучения
     */
    private void updateBiasVector(double[] bias, double[] gradients, double learningRate) {
        for (int i = 0; i < bias.length; i++) {
            bias[i] -= learningRate * gradients[i];
        }
    }
    
    /**
     * Сбрасывает градиенты
     */
    private void resetGradients() {
        resetGradientMatrix(resetWeightGradients);
        resetGradientVector(resetBiasGradients);
        resetGradientMatrix(updateWeightGradients);
        resetGradientVector(updateBiasGradients);
        resetGradientMatrix(candidateWeightGradients);
        resetGradientVector(candidateBiasGradients);
    }
    
    /**
     * Сбрасывает матрицу градиентов
     * 
     * @param gradients матрица градиентов
     */
    private void resetGradientMatrix(double[][] gradients) {
        for (int i = 0; i < gradients.length; i++) {
            for (int j = 0; j < gradients[i].length; j++) {
                gradients[i][j] = 0.0;
            }
        }
    }
    
    /**
     * Сбрасывает вектор градиентов
     * 
     * @param gradients вектор градиентов
     */
    private void resetGradientVector(double[] gradients) {
        for (int i = 0; i < gradients.length; i++) {
            gradients[i] = 0.0;
        }
    }
    
    // ==================== ГЕТТЕРЫ ====================
    
    /**
     * Возвращает количество скрытых единиц
     * 
     * @return количество скрытых единиц
     */
    public int getHiddenUnits() {
        return hiddenUnits;
    }
    
    /**
     * Возвращает текущее скрытое состояние
     * 
     * @return скрытое состояние
     */
    public double[] getHiddenState() {
        return hiddenState != null ? hiddenState.clone() : null;
    }
    
    /**
     * Возвращает статистику слоя
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== GRULayer Statistics ===\n");
        stats.append("Hidden Units: ").append(hiddenUnits).append("\n");
        stats.append("Input Size: ").append(inputSize).append("\n");
        stats.append("Parameters: ").append(getParameterCount()).append("\n");
        return stats.toString();
    }
    
    /**
     * Возвращает количество параметров
     * 
     * @return количество параметров
     */
    private int getParameterCount() {
        int weights = 3 * hiddenUnits * (inputSize + hiddenUnits);
        int biases = 3 * hiddenUnits;
        return weights + biases;
    }
}
