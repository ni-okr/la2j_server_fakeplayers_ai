package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

/**
 * Слой максимального пулинга (MaxPooling) для уменьшения размерности
 * 
 * MaxPooling слой выполняет операцию максимального пулинга, которая
 * уменьшает размерность данных, сохраняя наиболее важные особенности.
 * Это помогает снизить вычислительную сложность и предотвратить
 * переобучение в Convolutional Neural Networks.
 * 
 * @author ni-okr
 * @version 3.2
 */
public class MaxPoolingLayer extends NetworkLayer {
    
    private static final Logger _log = Logger.getLogger(MaxPoolingLayer.class.getName());
    
    // ==================== ПАРАМЕТРЫ СЛОЯ ====================
    
    /** Размер окна пулинга (обычно 2x2) */
    private final int poolSize;
    
    /** Шаг пулинга (обычно равен размеру окна) */
    private final int stride;
    
    /** Размеры входа: [высота, ширина, каналы] */
    private int[] inputShape;
    
    /** Размеры выхода: [высота, ширина, каналы] */
    private int[] outputShape;
    
    /** Индексы максимальных элементов для обратного распространения */
    private int[][][][] maxIndices;
    
    /** Кэш для входных данных */
    private double[][][] inputCache;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальный размер окна */
    private static final int MIN_POOL_SIZE = 2;
    
    /** Максимальный размер окна */
    private static final int MAX_POOL_SIZE = 8;
    
    /** Минимальный шаг */
    private static final int MIN_STRIDE = 1;
    
    /** Максимальный шаг */
    private static final int MAX_STRIDE = 4;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый слой максимального пулинга
     * 
     * @param poolSize размер окна пулинга (квадратный)
     * @param stride шаг пулинга
     */
    public MaxPoolingLayer(int poolSize, int stride) {
        super(NetworkLayer.LayerType.MAX_POOLING);
        this.poolSize = poolSize;
        this.stride = stride;
        
        // Валидация параметров
        if (!isValidParameters()) {
            throw new IllegalArgumentException("Invalid MaxPooling parameters");
        }
        
        _log.info("MaxPoolingLayer created: poolSize=" + poolSize + 
                 ", stride=" + stride);
    }
    
    /**
     * Создает слой максимального пулинга с параметрами по умолчанию
     * 
     * @param poolSize размер окна пулинга
     */
    public MaxPoolingLayer(int poolSize) {
        this(poolSize, poolSize);
    }
    
    /**
     * Создает слой максимального пулинга 2x2 с шагом 2
     */
    public MaxPoolingLayer() {
        this(2, 2);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Инициализирует слой с заданными размерами (2D)
     * 
     * @param inputSize размер входа
     * @param outputSize размер выхода
     */
    @Override
    public void initialize(int inputSize, int outputSize) {
        // Для MaxPooling слоя используем размеры по умолчанию
        initialize(32, 32, 3);
    }
    
    /**
     * Инициализирует слой с заданными размерами входа
     * 
     * @param inputHeight высота входного изображения
     * @param inputWidth ширина входного изображения
     * @param inputChannels количество входных каналов
     */
    @Override
    public void initialize(int inputHeight, int inputWidth, int inputChannels) {
        this.inputShape = new int[]{inputHeight, inputWidth, inputChannels};
        
        // Вычисляем размеры выхода
        int outputHeight = calculateOutputSize(inputHeight, poolSize, stride);
        int outputWidth = calculateOutputSize(inputWidth, poolSize, stride);
        this.outputShape = new int[]{outputHeight, outputWidth, inputChannels};
        
        // Инициализируем массив для индексов максимальных элементов
        this.maxIndices = new int[outputHeight][outputWidth][inputChannels][2];
        
        _log.info("MaxPoolingLayer initialized: input=" + java.util.Arrays.toString(inputShape) + 
                 ", output=" + java.util.Arrays.toString(outputShape));
    }
    
    /**
     * Выполняет прямое распространение (forward pass) - 1D версия
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    @Override
    public double[] forward(double[] inputs) {
        // Преобразуем 1D в 3D и вызываем 3D версию
        double[][][] input3D = reshapeTo3D(inputs);
        double[][][] output3D = forward(input3D);
        return flatten3D(output3D);
    }
    
    /**
     * Выполняет прямое распространение (forward pass)
     * 
     * @param input входные данные [height, width, channels]
     * @return выходные данные [height, width, channels]
     */
    public double[][][] forward(double[][][] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input shape for MaxPooling layer");
        }
        
        // Сохраняем входные данные
        this.inputCache = copy3DArray(input);
        
        // Выполняем максимальный пулинг
        double[][][] output = performMaxPooling(input);
        
        return output;
    }
    
    /**
     * Выполняет обратное распространение (backward pass) - 1D версия
     * 
     * @param inputs входные данные
     * @param outputGradients градиенты выходного слоя
     * @return градиенты входного слоя
     */
    @Override
    public double[] backward(double[] inputs, double[] outputGradients) {
        // Преобразуем 1D в 3D и вызываем 3D версию
        double[][][] gradient3D = reshapeTo3D(outputGradients);
        double[][][] inputGradient3D = backward(gradient3D);
        return flatten3D(inputGradient3D);
    }
    
    /**
     * Выполняет обратное распространение (backward pass)
     * 
     * @param gradient градиент потерь относительно выхода
     * @return градиент относительно входа
     */
    public double[][][] backward(double[][][] gradient) {
        if (gradient == null) {
            throw new IllegalArgumentException("Gradient cannot be null");
        }
        
        if (inputCache == null || maxIndices == null) {
            throw new IllegalStateException("Forward pass must be called before backward pass");
        }
        
        // Вычисляем градиент относительно входа
        double[][][] inputGradient = computeInputGradient(gradient);
        
        return inputGradient;
    }
    
    /**
     * Обновляет веса (для MaxPooling не требуется)
     * 
     * @param learningRate скорость обучения
     */
    @Override
    public void updateWeights(double learningRate) {
        // MaxPooling не имеет обучаемых параметров
        _log.debug("MaxPoolingLayer has no trainable parameters");
    }
    
    /**
     * Возвращает размер выхода слоя
     * 
     * @return количество выходных элементов
     */
    @Override
    public int getOutputSize() {
        if (outputShape == null) {
            return 0;
        }
        return outputShape[0] * outputShape[1] * outputShape[2];
    }
    
    /**
     * Возвращает размеры выхода
     * 
     * @return массив [высота, ширина, каналы]
     */
    public int[] getOutputShape() {
        return outputShape != null ? outputShape.clone() : null;
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Проверяет валидность параметров слоя
     * 
     * @return true если параметры корректны
     */
    private boolean isValidParameters() {
        return poolSize >= MIN_POOL_SIZE && poolSize <= MAX_POOL_SIZE &&
               stride >= MIN_STRIDE && stride <= MAX_STRIDE;
    }
    
    /**
     * Проверяет валидность входных данных
     * 
     * @param input входные данные
     * @return true если данные корректны
     */
    private boolean isValidInput(double[][][] input) {
        if (input.length != inputShape[0] || 
            input[0].length != inputShape[1] || 
            input[0][0].length != inputShape[2]) {
            return false;
        }
        return true;
    }
    
    /**
     * Вычисляет размер выхода после пулинга
     * 
     * @param inputSize размер входа
     * @param poolSize размер окна пулинга
     * @param stride шаг пулинга
     * @return размер выхода
     */
    private int calculateOutputSize(int inputSize, int poolSize, int stride) {
        return (inputSize - poolSize) / stride + 1;
    }
    
    /**
     * Выполняет операцию максимального пулинга
     * 
     * @param input входные данные
     * @return результат пулинга
     */
    private double[][][] performMaxPooling(double[][][] input) {
        int outputHeight = outputShape[0];
        int outputWidth = outputShape[1];
        int channels = inputShape[2];
        
        double[][][] output = new double[outputHeight][outputWidth][channels];
        
        // Применяем пулинг для каждого канала
        for (int c = 0; c < channels; c++) {
            for (int oh = 0; oh < outputHeight; oh++) {
                for (int ow = 0; ow < outputWidth; ow++) {
                    double maxValue = Double.NEGATIVE_INFINITY;
                    int maxH = -1, maxW = -1;
                    
                    // Ищем максимальное значение в окне
                    for (int ph = 0; ph < poolSize; ph++) {
                        for (int pw = 0; pw < poolSize; pw++) {
                            int ih = oh * stride + ph;
                            int iw = ow * stride + pw;
                            
                            if (ih < inputShape[0] && iw < inputShape[1]) {
                                double value = input[ih][iw][c];
                                if (value > maxValue) {
                                    maxValue = value;
                                    maxH = ih;
                                    maxW = iw;
                                }
                            }
                        }
                    }
                    
                    output[oh][ow][c] = maxValue;
                    
                    // Сохраняем индексы максимального элемента
                    maxIndices[oh][ow][c][0] = maxH;
                    maxIndices[oh][ow][c][1] = maxW;
                }
            }
        }
        
        return output;
    }
    
    /**
     * Вычисляет градиент относительно входа
     * 
     * @param gradient градиент потерь
     * @return градиент относительно входа
     */
    private double[][][] computeInputGradient(double[][][] gradient) {
        double[][][] inputGradient = new double[inputShape[0]][inputShape[1]][inputShape[2]];
        
        // Инициализируем нулями
        for (int h = 0; h < inputShape[0]; h++) {
            for (int w = 0; w < inputShape[1]; w++) {
                for (int c = 0; c < inputShape[2]; c++) {
                    inputGradient[h][w][c] = 0.0;
                }
            }
        }
        
        // Распространяем градиент только к максимальным элементам
        for (int oh = 0; oh < outputShape[0]; oh++) {
            for (int ow = 0; ow < outputShape[1]; ow++) {
                for (int c = 0; c < inputShape[2]; c++) {
                    int maxH = maxIndices[oh][ow][c][0];
                    int maxW = maxIndices[oh][ow][c][1];
                    
                    if (maxH >= 0 && maxW >= 0) {
                        inputGradient[maxH][maxW][c] += gradient[oh][ow][c];
                    }
                }
            }
        }
        
        return inputGradient;
    }
    
    /**
     * Копирует 3D массив
     * 
     * @param array исходный массив
     * @return копия массива
     */
    private double[][][] copy3DArray(double[][][] array) {
        double[][][] copy = new double[array.length][array[0].length][array[0][0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.arraycopy(array[i][j], 0, copy[i][j], 0, array[i][j].length);
            }
        }
        return copy;
    }
    
    // ==================== ГЕТТЕРЫ ====================
    
    /**
     * Возвращает размер окна пулинга
     * 
     * @return размер окна пулинга
     */
    public int getPoolSize() {
        return poolSize;
    }
    
    /**
     * Возвращает шаг пулинга
     * 
     * @return шаг пулинга
     */
    public int getStride() {
        return stride;
    }
    
    /**
     * Возвращает размеры входа
     * 
     * @return массив [высота, ширина, каналы]
     */
    public int[] getInputShape() {
        return inputShape != null ? inputShape.clone() : null;
    }
    
    /**
     * Возвращает статистику слоя
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== MaxPoolingLayer Statistics ===\n");
        stats.append("Pool Size: ").append(poolSize).append("x").append(poolSize).append("\n");
        stats.append("Stride: ").append(stride).append("\n");
        if (inputShape != null) {
            stats.append("Input Shape: ").append(java.util.Arrays.toString(inputShape)).append("\n");
        }
        if (outputShape != null) {
            stats.append("Output Shape: ").append(java.util.Arrays.toString(outputShape)).append("\n");
        }
        stats.append("Parameters: 0 (no trainable parameters)\n");
        return stats.toString();
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ 1D/3D ПРЕОБРАЗОВАНИЙ ====================
    
    /**
     * Преобразует 1D массив в 3D
     * 
     * @param array 1D массив
     * @return 3D массив
     */
    private double[][][] reshapeTo3D(double[] array) {
        if (inputShape == null) {
            throw new IllegalStateException("Layer not initialized");
        }
        
        double[][][] reshaped = new double[inputShape[0]][inputShape[1]][inputShape[2]];
        int index = 0;
        
        for (int h = 0; h < inputShape[0] && index < array.length; h++) {
            for (int w = 0; w < inputShape[1] && index < array.length; w++) {
                for (int c = 0; c < inputShape[2] && index < array.length; c++) {
                    reshaped[h][w][c] = array[index++];
                }
            }
        }
        
        return reshaped;
    }
    
    /**
     * Преобразует 3D массив в 1D
     * 
     * @param array 3D массив
     * @return 1D массив
     */
    private double[] flatten3D(double[][][] array) {
        int totalSize = array.length * array[0].length * array[0][0].length;
        double[] flattened = new double[totalSize];
        int index = 0;
        
        for (int h = 0; h < array.length; h++) {
            for (int w = 0; w < array[h].length; w++) {
                for (int c = 0; c < array[h][w].length; c++) {
                    flattened[index++] = array[h][w][c];
                }
            }
        }
        
        return flattened;
    }
}
