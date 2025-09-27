package net.sf.l2j.botmanager.learning;

/**
 * Базовый класс для слоев нейронной сети
 * 
 * Этот класс определяет интерфейс для всех типов слоев
 * в глубокой нейронной сети, включая полносвязные, dropout,
 * batch normalization и другие типы слоев.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
public abstract class NetworkLayer {
    
    /**
     * Тип слоя
     */
    protected final LayerType type;
    
    /**
     * Количество входных нейронов
     */
    protected int inputSize;
    
    /**
     * Количество выходных нейронов
     */
    protected int outputSize;
    
    /**
     * Конструктор базового слоя
     * 
     * @param type тип слоя
     */
    protected NetworkLayer(LayerType type) {
        this.type = type;
    }
    
    /**
     * Выполняет прямое распространение через слой
     * 
     * @param inputs входные данные
     * @return выходные данные
     */
    public abstract double[] forward(double[] inputs);
    
    /**
     * Выполняет обратное распространение через слой
     * 
     * @param inputs входные данные
     * @param outputGradients градиенты выходного слоя
     * @return градиенты входного слоя
     */
    public abstract double[] backward(double[] inputs, double[] outputGradients);
    
    /**
     * Инициализирует слой
     * 
     * @param inputSize размер входа
     * @param outputSize размер выхода
     */
    public abstract void initialize(int inputSize, int outputSize);
    
    /**
     * Инициализирует слой с заданными размерами (для 3D слоев)
     * 
     * @param inputHeight высота входа
     * @param inputWidth ширина входа
     * @param inputChannels каналы входа
     */
    public void initialize(int inputHeight, int inputWidth, int inputChannels) {
        // Реализация по умолчанию - переопределяется в подклассах
        throw new UnsupportedOperationException("3D initialization not supported by this layer type");
    }
    
    /**
     * Обновляет веса слоя
     * 
     * @param learningRate скорость обучения
     */
    public abstract void updateWeights(double learningRate);
    
    /**
     * Возвращает тип слоя
     * 
     * @return тип слоя
     */
    public LayerType getType() {
        return type;
    }
    
    /**
     * Возвращает размер входа
     * 
     * @return размер входа
     */
    public int getInputSize() {
        return inputSize;
    }
    
    /**
     * Возвращает размер выхода
     * 
     * @return размер выхода
     */
    public int getOutputSize() {
        return outputSize;
    }
    
    /**
     * Перечисление типов слоев
     */
    public enum LayerType {
        DENSE("Dense"),
        DROPOUT("Dropout"),
        BATCH_NORMALIZATION("BatchNormalization"),
        CONVOLUTIONAL("Convolutional"),
        CONV2D("Conv2D"),
        MAX_POOLING("MaxPooling"),
        FLATTEN("Flatten"),
        RECURRENT("Recurrent"),
        LSTM("LSTM"),
        GRU("GRU");
        
        private final String name;
        
        LayerType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
