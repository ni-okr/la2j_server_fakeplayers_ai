package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

/**
 * Слой сглаживания (Flatten) для преобразования многомерных данных в одномерные
 * 
 * Flatten слой преобразует многомерные данные (например, выходы сверточных слоев)
 * в одномерный массив, который может быть использован в полносвязных слоях.
 * Это необходимо для соединения сверточных и полносвязных частей нейронной сети.
 * 
 * @author ni-okr
 * @version 3.2
 */
public class FlattenLayer extends NetworkLayer {
    
    private static final Logger _log = Logger.getLogger(FlattenLayer.class.getName());
    
    // ==================== ПАРАМЕТРЫ СЛОЯ ====================
    
    /** Размеры входа: [высота, ширина, каналы] */
    private int[] inputShape;
    
    /** Размер выхода (общее количество элементов) */
    private int outputSize;
    
    /** Кэш для входных данных (для обратного распространения) */
    private double[][][] inputCache;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый слой сглаживания
     */
    public FlattenLayer() {
        super(NetworkLayer.LayerType.FLATTEN);
        _log.info("FlattenLayer created");
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
        // Для Flatten слоя используем размеры по умолчанию
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
        this.outputSize = inputHeight * inputWidth * inputChannels;
        
        _log.info("FlattenLayer initialized: input=" + java.util.Arrays.toString(inputShape) + 
                 ", outputSize=" + outputSize);
    }
    
    /**
     * Выполняет прямое распространение (forward pass) - 1D версия
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    @Override
    public double[] forward(double[] inputs) {
        // Для FlattenLayer 1D версия просто возвращает входные данные
        return inputs.clone();
    }
    
    /**
     * Выполняет прямое распространение (forward pass)
     * 
     * @param input входные данные [height, width, channels]
     * @return выходные данные [outputSize]
     */
    public double[] forward(double[][][] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input shape for Flatten layer");
        }
        
        // Сохраняем входные данные для обратного распространения
        this.inputCache = copy3DArray(input);
        
        // Выполняем сглаживание
        double[] output = performFlatten(input);
        
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
        // Для FlattenLayer 1D версия просто возвращает градиенты
        return outputGradients.clone();
    }
    
    /**
     * Выполняет обратное распространение (backward pass)
     * 
     * @param gradient градиент потерь относительно выхода
     * @return градиент относительно входа
     */
    public double[][][] backward(double[] gradient) {
        if (gradient == null) {
            throw new IllegalArgumentException("Gradient cannot be null");
        }
        
        if (inputCache == null) {
            throw new IllegalStateException("Forward pass must be called before backward pass");
        }
        
        if (gradient.length != outputSize) {
            throw new IllegalArgumentException("Gradient size mismatch");
        }
        
        // Вычисляем градиент относительно входа
        double[][][] inputGradient = computeInputGradient(gradient);
        
        return inputGradient;
    }
    
    /**
     * Обновляет веса (для Flatten не требуется)
     * 
     * @param learningRate скорость обучения
     */
    @Override
    public void updateWeights(double learningRate) {
        // Flatten не имеет обучаемых параметров
        _log.debug("FlattenLayer has no trainable parameters");
    }
    
    /**
     * Возвращает размер выхода слоя
     * 
     * @return количество выходных элементов
     */
    @Override
    public int getOutputSize() {
        return outputSize;
    }
    
    /**
     * Возвращает размеры входа
     * 
     * @return массив [высота, ширина, каналы]
     */
    public int[] getInputShape() {
        return inputShape != null ? inputShape.clone() : null;
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
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
     * Выполняет операцию сглаживания
     * 
     * @param input входные данные
     * @return сглаженный одномерный массив
     */
    private double[] performFlatten(double[][][] input) {
        double[] output = new double[outputSize];
        int index = 0;
        
        // Преобразуем 3D массив в 1D
        for (int h = 0; h < inputShape[0]; h++) {
            for (int w = 0; w < inputShape[1]; w++) {
                for (int c = 0; c < inputShape[2]; c++) {
                    output[index++] = input[h][w][c];
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
    private double[][][] computeInputGradient(double[] gradient) {
        double[][][] inputGradient = new double[inputShape[0]][inputShape[1]][inputShape[2]];
        int index = 0;
        
        // Преобразуем 1D градиент обратно в 3D
        for (int h = 0; h < inputShape[0]; h++) {
            for (int w = 0; w < inputShape[1]; w++) {
                for (int c = 0; c < inputShape[2]; c++) {
                    inputGradient[h][w][c] = gradient[index++];
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
    
    /**
     * Возвращает статистику слоя
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== FlattenLayer Statistics ===\n");
        if (inputShape != null) {
            stats.append("Input Shape: ").append(java.util.Arrays.toString(inputShape)).append("\n");
        }
        stats.append("Output Size: ").append(outputSize).append("\n");
        stats.append("Parameters: 0 (no trainable parameters)\n");
        return stats.toString();
    }
}
