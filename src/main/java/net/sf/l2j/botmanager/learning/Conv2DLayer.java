package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.Random;

/**
 * Сверточный слой (Conv2D) для обработки 2D изображений
 * 
 * Этот класс реализует сверточную операцию, которая является основой
 * Convolutional Neural Networks (CNN). Сверточные слои эффективно
 * извлекают локальные особенности из изображений, такие как края,
 * текстуры и более сложные паттерны.
 * 
 * @author ni-okr
 * @version 3.2
 */
public class Conv2DLayer extends NetworkLayer {
    
    private static final Logger _log = Logger.getLogger(Conv2DLayer.class.getName());
    
    // ==================== ПАРАМЕТРЫ СЛОЯ ====================
    
    /** Количество фильтров (выходных каналов) */
    private final int filters;
    
    /** Размер ядра (kernel size) - обычно 3x3 или 5x5 */
    private final int kernelSize;
    
    /** Шаг (stride) - на сколько пикселей сдвигается фильтр */
    private final int stride;
    
    /** Отступ (padding) - добавляемые нули по краям */
    private final int padding;
    
    /** Размеры входа: [высота, ширина, каналы] */
    private int[] inputShape;
    
    /** Размеры выхода: [высота, ширина, каналы] */
    private int[] outputShape;
    
    /** Веса фильтров: [filters, kernelHeight, kernelWidth, inputChannels] */
    private double[][][][] weights;
    
    /** Смещения (bias) для каждого фильтра */
    private double[] biases;
    
    /** Градиенты весов для обратного распространения */
    private double[][][][] weightGradients;
    
    /** Градиенты смещений */
    private double[] biasGradients;
    
    /** Кэш для входных данных (для обратного распространения) */
    private double[][][] inputCache;
    
    /** Кэш для выходных данных */
    private double[][][] outputCache;
    
    /** Генератор случайных чисел для инициализации весов */
    private final Random random;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальный размер ядра */
    private static final int MIN_KERNEL_SIZE = 1;
    
    /** Максимальный размер ядра */
    private static final int MAX_KERNEL_SIZE = 7;
    
    /** Минимальное количество фильтров */
    private static final int MIN_FILTERS = 1;
    
    /** Максимальное количество фильтров */
    private static final int MAX_FILTERS = 512;
    
    /** Минимальный шаг */
    private static final int MIN_STRIDE = 1;
    
    /** Максимальный шаг */
    private static final int MAX_STRIDE = 5;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый сверточный слой с заданными параметрами
     * 
     * @param filters количество фильтров (выходных каналов)
     * @param kernelSize размер ядра (квадратный)
     * @param stride шаг свертки
     * @param padding отступ
     */
    public Conv2DLayer(int filters, int kernelSize, int stride, int padding) {
        super(NetworkLayer.LayerType.CONV2D);
        this.filters = filters;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.padding = padding;
        this.random = new Random();
        
        // Валидация параметров
        if (!isValidParameters()) {
            throw new IllegalArgumentException("Invalid Conv2D parameters");
        }
        
        _log.info("Conv2DLayer created: filters=" + filters + 
                 ", kernelSize=" + kernelSize + 
                 ", stride=" + stride + 
                 ", padding=" + padding);
    }
    
    /**
     * Создает сверточный слой с параметрами по умолчанию
     * 
     * @param filters количество фильтров
     * @param kernelSize размер ядра
     */
    public Conv2DLayer(int filters, int kernelSize) {
        this(filters, kernelSize, 1, 0);
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
        // Для Conv2D слоя используем размеры по умолчанию
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
        int outputHeight = calculateOutputSize(inputHeight, kernelSize, stride, padding);
        int outputWidth = calculateOutputSize(inputWidth, kernelSize, stride, padding);
        this.outputShape = new int[]{outputHeight, outputWidth, filters};
        
        // Инициализируем веса и смещения
        initializeWeights();
        initializeBiases();
        
        // Инициализируем градиенты
        initializeGradients();
        
        _log.info("Conv2DLayer initialized: input=" + java.util.Arrays.toString(inputShape) + 
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
     * @return выходные данные [height, width, filters]
     */
    public double[][][] forward(double[][][] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input shape for Conv2D layer");
        }
        
        // Сохраняем входные данные для обратного распространения
        this.inputCache = copy3DArray(input);
        
        // Выполняем свертку
        double[][][] output = performConvolution(input);
        
        // Сохраняем выходные данные
        this.outputCache = copy3DArray(output);
        
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
        
        if (inputCache == null) {
            throw new IllegalStateException("Forward pass must be called before backward pass");
        }
        
        // Вычисляем градиенты весов и смещений
        computeWeightGradients(gradient);
        computeBiasGradients(gradient);
        
        // Вычисляем градиент относительно входа
        double[][][] inputGradient = computeInputGradient(gradient);
        
        return inputGradient;
    }
    
    /**
     * Обновляет веса и смещения с помощью оптимизатора
     * 
     * @param learningRate скорость обучения
     */
    @Override
    public void updateWeights(double learningRate) {
        if (weightGradients == null || biasGradients == null) {
            _log.warn("Cannot update weights: gradients not computed");
            return;
        }
        
        // Обновляем веса
        for (int f = 0; f < filters; f++) {
            for (int h = 0; h < kernelSize; h++) {
                for (int w = 0; w < kernelSize; w++) {
                    for (int c = 0; c < inputShape[2]; c++) {
                        weights[f][h][w][c] -= learningRate * weightGradients[f][h][w][c];
                    }
                }
            }
        }
        
        // Обновляем смещения
        for (int f = 0; f < filters; f++) {
            biases[f] -= learningRate * biasGradients[f];
        }
        
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
        return filters >= MIN_FILTERS && filters <= MAX_FILTERS &&
               kernelSize >= MIN_KERNEL_SIZE && kernelSize <= MAX_KERNEL_SIZE &&
               stride >= MIN_STRIDE && stride <= MAX_STRIDE &&
               padding >= 0;
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
     * Вычисляет размер выхода после свертки
     * 
     * @param inputSize размер входа
     * @param kernelSize размер ядра
     * @param stride шаг
     * @param padding отступ
     * @return размер выхода
     */
    private int calculateOutputSize(int inputSize, int kernelSize, int stride, int padding) {
        return (inputSize + 2 * padding - kernelSize) / stride + 1;
    }
    
    /**
     * Инициализирует веса фильтров
     */
    private void initializeWeights() {
        weights = new double[filters][kernelSize][kernelSize][inputShape[2]];
        
        // Используем инициализацию Xavier/He
        double limit = Math.sqrt(6.0 / (kernelSize * kernelSize * inputShape[2]));
        
        for (int f = 0; f < filters; f++) {
            for (int h = 0; h < kernelSize; h++) {
                for (int w = 0; w < kernelSize; w++) {
                    for (int c = 0; c < inputShape[2]; c++) {
                        weights[f][h][w][c] = (random.nextDouble() * 2 - 1) * limit;
                    }
                }
            }
        }
    }
    
    /**
     * Инициализирует смещения
     */
    private void initializeBiases() {
        biases = new double[filters];
        // Инициализируем смещения нулями
        for (int f = 0; f < filters; f++) {
            biases[f] = 0.0;
        }
    }
    
    /**
     * Инициализирует градиенты
     */
    private void initializeGradients() {
        weightGradients = new double[filters][kernelSize][kernelSize][inputShape[2]];
        biasGradients = new double[filters];
    }
    
    /**
     * Выполняет сверточную операцию
     * 
     * @param input входные данные
     * @return результат свертки
     */
    private double[][][] performConvolution(double[][][] input) {
        int outputHeight = outputShape[0];
        int outputWidth = outputShape[1];
        double[][][] output = new double[outputHeight][outputWidth][filters];
        
        // Применяем каждый фильтр
        for (int f = 0; f < filters; f++) {
            for (int oh = 0; oh < outputHeight; oh++) {
                for (int ow = 0; ow < outputWidth; ow++) {
                    double sum = 0.0;
                    
                    // Свертка с ядром
                    for (int kh = 0; kh < kernelSize; kh++) {
                        for (int kw = 0; kw < kernelSize; kw++) {
                            int ih = oh * stride + kh - padding;
                            int iw = ow * stride + kw - padding;
                            
                            // Проверяем границы
                            if (ih >= 0 && ih < inputShape[0] && 
                                iw >= 0 && iw < inputShape[1]) {
                                
                                // Суммируем по всем входным каналам
                                for (int c = 0; c < inputShape[2]; c++) {
                                    sum += input[ih][iw][c] * weights[f][kh][kw][c];
                                }
                            }
                        }
                    }
                    
                    output[oh][ow][f] = sum + biases[f];
                }
            }
        }
        
        return output;
    }
    
    /**
     * Вычисляет градиенты весов
     * 
     * @param gradient градиент потерь
     */
    private void computeWeightGradients(double[][][] gradient) {
        // Сбрасываем градиенты
        for (int f = 0; f < filters; f++) {
            for (int h = 0; h < kernelSize; h++) {
                for (int w = 0; w < kernelSize; w++) {
                    for (int c = 0; c < inputShape[2]; c++) {
                        weightGradients[f][h][w][c] = 0.0;
                    }
                }
            }
        }
        
        // Вычисляем градиенты
        for (int f = 0; f < filters; f++) {
            for (int oh = 0; oh < outputShape[0]; oh++) {
                for (int ow = 0; ow < outputShape[1]; ow++) {
                    double grad = gradient[oh][ow][f];
                    
                    for (int kh = 0; kh < kernelSize; kh++) {
                        for (int kw = 0; kw < kernelSize; kw++) {
                            int ih = oh * stride + kh - padding;
                            int iw = ow * stride + kw - padding;
                            
                            if (ih >= 0 && ih < inputShape[0] && 
                                iw >= 0 && iw < inputShape[1]) {
                                
                                for (int c = 0; c < inputShape[2]; c++) {
                                    weightGradients[f][kh][kw][c] += 
                                        grad * inputCache[ih][iw][c];
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Вычисляет градиенты смещений
     * 
     * @param gradient градиент потерь
     */
    private void computeBiasGradients(double[][][] gradient) {
        for (int f = 0; f < filters; f++) {
            biasGradients[f] = 0.0;
            
            for (int oh = 0; oh < outputShape[0]; oh++) {
                for (int ow = 0; ow < outputShape[1]; ow++) {
                    biasGradients[f] += gradient[oh][ow][f];
                }
            }
        }
    }
    
    /**
     * Вычисляет градиент относительно входа
     * 
     * @param gradient градиент потерь
     * @return градиент относительно входа
     */
    private double[][][] computeInputGradient(double[][][] gradient) {
        double[][][] inputGradient = new double[inputShape[0]][inputShape[1]][inputShape[2]];
        
        for (int f = 0; f < filters; f++) {
            for (int oh = 0; oh < outputShape[0]; oh++) {
                for (int ow = 0; ow < outputShape[1]; ow++) {
                    double grad = gradient[oh][ow][f];
                    
                    for (int kh = 0; kh < kernelSize; kh++) {
                        for (int kw = 0; kw < kernelSize; kw++) {
                            int ih = oh * stride + kh - padding;
                            int iw = ow * stride + kw - padding;
                            
                            if (ih >= 0 && ih < inputShape[0] && 
                                iw >= 0 && iw < inputShape[1]) {
                                
                                for (int c = 0; c < inputShape[2]; c++) {
                                    inputGradient[ih][iw][c] += 
                                        grad * weights[f][kh][kw][c];
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return inputGradient;
    }
    
    /**
     * Сбрасывает градиенты
     */
    private void resetGradients() {
        for (int f = 0; f < filters; f++) {
            for (int h = 0; h < kernelSize; h++) {
                for (int w = 0; w < kernelSize; w++) {
                    for (int c = 0; c < inputShape[2]; c++) {
                        weightGradients[f][h][w][c] = 0.0;
                    }
                }
            }
            biasGradients[f] = 0.0;
        }
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
     * Возвращает количество фильтров
     * 
     * @return количество фильтров
     */
    public int getFilters() {
        return filters;
    }
    
    /**
     * Возвращает размер ядра
     * 
     * @return размер ядра
     */
    public int getKernelSize() {
        return kernelSize;
    }
    
    /**
     * Возвращает шаг
     * 
     * @return шаг
     */
    public int getStride() {
        return stride;
    }
    
    /**
     * Возвращает отступ
     * 
     * @return отступ
     */
    public int getPadding() {
        return padding;
    }
    
    /**
     * Возвращает размеры входа
     * 
     * @return массив [высота, ширина, каналы]
     */
    public int[] getInputShape() {
        return inputShape != null ? inputShape.clone() : null;
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
