package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.Random;

/**
 * LSTM (Long Short-Term Memory) слой для рекуррентных нейронных сетей
 * 
 * LSTM слой решает проблему исчезающих градиентов в обычных RNN,
 * позволяя сети запоминать информацию на длительные периоды времени.
 * Это особенно важно для анализа временных последовательностей
 * и паттернов поведения ботов в игре.
 * 
 * @author ni-okr
 * @version 3.3
 */
public class LSTMLayer extends NetworkLayer {
    
    private static final Logger _log = Logger.getLogger(LSTMLayer.class.getName());
    
    // ==================== ПАРАМЕТРЫ СЛОЯ ====================
    
    /** Количество скрытых единиц */
    private final int hiddenUnits;
    
    /** Размер входа */
    private int inputSize;
    
    /** Веса для забывающего гейта */
    private double[][] forgetWeights;
    private double[] forgetBias;
    
    /** Веса для входного гейта */
    private double[][] inputWeights;
    private double[] inputBias;
    
    /** Веса для кандидатов */
    private double[][] candidateWeights;
    private double[] candidateBias;
    
    /** Веса для выходного гейта */
    private double[][] outputWeights;
    private double[] outputBias;
    
    /** Состояние ячейки */
    private double[] cellState;
    
    /** Скрытое состояние */
    private double[] hiddenState;
    
    /** Предыдущее скрытое состояние */
    private double[] prevHiddenState;
    
    /** Предыдущее состояние ячейки */
    private double[] prevCellState;
    
    /** Градиенты весов */
    private double[][] forgetWeightGradients;
    private double[] forgetBiasGradients;
    private double[][] inputWeightGradients;
    private double[] inputBiasGradients;
    private double[][] candidateWeightGradients;
    private double[] candidateBiasGradients;
    private double[][] outputWeightGradients;
    private double[] outputBiasGradients;
    
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
     * Создает новый LSTM слой
     * 
     * @param hiddenUnits количество скрытых единиц
     */
    public LSTMLayer(int hiddenUnits) {
        super(NetworkLayer.LayerType.LSTM);
        this.hiddenUnits = hiddenUnits;
        this.random = new Random();
        
        // Валидация параметров
        if (!isValidParameters()) {
            throw new IllegalArgumentException("Invalid LSTM parameters");
        }
        
        _log.info("LSTMLayer created: hiddenUnits=" + hiddenUnits);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Инициализирует слой с заданными размерами
     * 
     * @param inputSize размер входа
     * @param outputSize размер выхода (игнорируется для LSTM)
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
        
        _log.info("LSTMLayer initialized: inputSize=" + inputSize + 
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
        
        // Выполняем LSTM forward pass
        double[] output = performLSTMForward(inputs);
        
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
        
        // Выполняем LSTM backward pass
        double[] inputGradients = performLSTMBackward(inputCache, outputGradients);
        
        return inputGradients;
    }
    
    /**
     * Обновляет веса с помощью оптимизатора
     * 
     * @param learningRate скорость обучения
     */
    @Override
    public void updateWeights(double learningRate) {
        // Обновляем веса забывающего гейта
        updateWeightMatrix(forgetWeights, forgetWeightGradients, learningRate);
        updateBiasVector(forgetBias, forgetBiasGradients, learningRate);
        
        // Обновляем веса входного гейта
        updateWeightMatrix(inputWeights, inputWeightGradients, learningRate);
        updateBiasVector(inputBias, inputBiasGradients, learningRate);
        
        // Обновляем веса кандидатов
        updateWeightMatrix(candidateWeights, candidateWeightGradients, learningRate);
        updateBiasVector(candidateBias, candidateBiasGradients, learningRate);
        
        // Обновляем веса выходного гейта
        updateWeightMatrix(outputWeights, outputWeightGradients, learningRate);
        updateBiasVector(outputBias, outputBiasGradients, learningRate);
        
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
        if (cellState != null) {
            System.arraycopy(prevCellState, 0, cellState, 0, hiddenUnits);
        }
    }
    
    /**
     * Устанавливает начальное состояние
     * 
     * @param initialHiddenState начальное скрытое состояние
     * @param initialCellState начальное состояние ячейки
     */
    public void setInitialState(double[] initialHiddenState, double[] initialCellState) {
        if (initialHiddenState != null && initialHiddenState.length == hiddenUnits) {
            System.arraycopy(initialHiddenState, 0, hiddenState, 0, hiddenUnits);
        }
        if (initialCellState != null && initialCellState.length == hiddenUnits) {
            System.arraycopy(initialCellState, 0, cellState, 0, hiddenUnits);
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
        // Инициализация весов забывающего гейта
        forgetWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        forgetBias = new double[hiddenUnits];
        initializeWeightMatrix(forgetWeights);
        initializeBiasVector(forgetBias);
        
        // Инициализация весов входного гейта
        inputWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        inputBias = new double[hiddenUnits];
        initializeWeightMatrix(inputWeights);
        initializeBiasVector(inputBias);
        
        // Инициализация весов кандидатов
        candidateWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        candidateBias = new double[hiddenUnits];
        initializeWeightMatrix(candidateWeights);
        initializeBiasVector(candidateBias);
        
        // Инициализация весов выходного гейта
        outputWeights = new double[hiddenUnits][inputSize + hiddenUnits];
        outputBias = new double[hiddenUnits];
        initializeWeightMatrix(outputWeights);
        initializeBiasVector(outputBias);
    }
    
    /**
     * Инициализирует состояния
     */
    private void initializeStates() {
        hiddenState = new double[hiddenUnits];
        cellState = new double[hiddenUnits];
        prevHiddenState = new double[hiddenUnits];
        prevCellState = new double[hiddenUnits];
        
        // Инициализируем нулями
        for (int i = 0; i < hiddenUnits; i++) {
            hiddenState[i] = 0.0;
            cellState[i] = 0.0;
            prevHiddenState[i] = 0.0;
            prevCellState[i] = 0.0;
        }
    }
    
    /**
     * Инициализирует градиенты
     */
    private void initializeGradients() {
        forgetWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        forgetBiasGradients = new double[hiddenUnits];
        inputWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        inputBiasGradients = new double[hiddenUnits];
        candidateWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        candidateBiasGradients = new double[hiddenUnits];
        outputWeightGradients = new double[hiddenUnits][inputSize + hiddenUnits];
        outputBiasGradients = new double[hiddenUnits];
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
     * Выполняет LSTM forward pass
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    private double[] performLSTMForward(double[] inputs) {
        // Объединяем входные данные и предыдущее скрытое состояние
        double[] combined = new double[inputSize + hiddenUnits];
        System.arraycopy(inputs, 0, combined, 0, inputSize);
        System.arraycopy(prevHiddenState, 0, combined, inputSize, hiddenUnits);
        
        // Сохраняем предыдущие состояния
        System.arraycopy(hiddenState, 0, prevHiddenState, 0, hiddenUnits);
        System.arraycopy(cellState, 0, prevCellState, 0, hiddenUnits);
        
        // Вычисляем забывающий гейт
        double[] forgetGate = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = forgetBias[i];
            for (int j = 0; j < combined.length; j++) {
                sum += combined[j] * forgetWeights[i][j];
            }
            forgetGate[i] = ActivationFunction.sigmoid(sum);
        }
        
        // Вычисляем входной гейт
        double[] inputGate = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = inputBias[i];
            for (int j = 0; j < combined.length; j++) {
                sum += combined[j] * inputWeights[i][j];
            }
            inputGate[i] = ActivationFunction.sigmoid(sum);
        }
        
        // Вычисляем кандидатов
        double[] candidates = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = candidateBias[i];
            for (int j = 0; j < combined.length; j++) {
                sum += combined[j] * candidateWeights[i][j];
            }
            candidates[i] = ActivationFunction.tanh(sum);
        }
        
        // Вычисляем выходной гейт
        double[] outputGate = new double[hiddenUnits];
        for (int i = 0; i < hiddenUnits; i++) {
            double sum = outputBias[i];
            for (int j = 0; j < combined.length; j++) {
                sum += combined[j] * outputWeights[i][j];
            }
            outputGate[i] = ActivationFunction.sigmoid(sum);
        }
        
        // Обновляем состояние ячейки
        for (int i = 0; i < hiddenUnits; i++) {
            cellState[i] = forgetGate[i] * prevCellState[i] + inputGate[i] * candidates[i];
        }
        
        // Вычисляем скрытое состояние
        for (int i = 0; i < hiddenUnits; i++) {
            hiddenState[i] = outputGate[i] * ActivationFunction.tanh(cellState[i]);
        }
        
        return hiddenState.clone();
    }
    
    /**
     * Выполняет LSTM backward pass
     * 
     * @param inputs входные данные
     * @param outputGradients градиенты выходного слоя
     * @return градиенты входного слоя
     */
    private double[] performLSTMBackward(double[] inputs, double[] outputGradients) {
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
        resetGradientMatrix(forgetWeightGradients);
        resetGradientVector(forgetBiasGradients);
        resetGradientMatrix(inputWeightGradients);
        resetGradientVector(inputBiasGradients);
        resetGradientMatrix(candidateWeightGradients);
        resetGradientVector(candidateBiasGradients);
        resetGradientMatrix(outputWeightGradients);
        resetGradientVector(outputBiasGradients);
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
     * Возвращает текущее состояние ячейки
     * 
     * @return состояние ячейки
     */
    public double[] getCellState() {
        return cellState != null ? cellState.clone() : null;
    }
    
    /**
     * Возвращает статистику слоя
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== LSTMLayer Statistics ===\n");
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
        int weights = 4 * hiddenUnits * (inputSize + hiddenUnits);
        int biases = 4 * hiddenUnits;
        return weights + biases;
    }
}
