package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Сверточная нейронная сеть (CNN) для обработки изображений
 * 
 * ConvolutionalNeuralNetwork предоставляет специализированную архитектуру
 * для обработки 2D изображений и извлечения пространственных особенностей.
 * CNN особенно эффективны для задач компьютерного зрения, таких как
 * классификация изображений, обнаружение объектов и сегментация.
 * 
 * @author ni-okr
 * @version 3.2
 */
public class ConvolutionalNeuralNetwork {
    
    private static final Logger _log = Logger.getLogger(ConvolutionalNeuralNetwork.class.getName());
    
    // ==================== ПАРАМЕТРЫ СЕТИ ====================
    
    /** ID бота, для которого создана сеть */
    private final int botId;
    
    /** Список слоев сети */
    private final List<NetworkLayer> layers;
    
    /** Состояние сети (активна/неактивна) */
    private boolean isActive;
    
    /** Количество эпох обучения */
    private int trainingEpochs;
    
    /** Общая ошибка обучения */
    private double totalError;
    
    /** Оптимизатор для обучения */
    private AdvancedOptimizer optimizer;
    
    /** Размеры входного изображения */
    private int[] inputShape;
    
    /** Размеры выходного изображения */
    private int[] outputShape;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальный размер изображения */
    private static final int MIN_IMAGE_SIZE = 1;
    
    /** Максимальный размер изображения */
    private static final int MAX_IMAGE_SIZE = 1024;
    
    /** Минимальное количество каналов */
    private static final int MIN_CHANNELS = 1;
    
    /** Максимальное количество каналов */
    private static final int MAX_CHANNELS = 64;
    
    /** Минимальное количество слоев */
    private static final int MIN_LAYERS = 1;
    
    /** Максимальное количество слоев */
    private static final int MAX_LAYERS = 50;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новую сверточную нейронную сеть
     * 
     * @param botId ID бота
     */
    public ConvolutionalNeuralNetwork(int botId) {
        this.botId = botId;
        this.layers = new ArrayList<>();
        this.isActive = false;
        this.trainingEpochs = 0;
        this.totalError = 0.0;
        
        _log.info("ConvolutionalNeuralNetwork created for bot " + botId);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Активирует сеть для использования
     */
    public void activate() {
        if (isActive) {
            _log.warn("Network is already active for bot " + botId);
            return;
        }
        
        if (layers.isEmpty()) {
            _log.warn("Cannot activate network: no layers added for bot " + botId);
            return;
        }
        
        isActive = true;
        _log.info("ConvolutionalNeuralNetwork activated for bot " + botId);
    }
    
    /**
     * Деактивирует сеть
     */
    public void deactivate() {
        isActive = false;
        _log.info("ConvolutionalNeuralNetwork deactivated for bot " + botId);
    }
    
    /**
     * Добавляет сверточный слой
     * 
     * @param filters количество фильтров
     * @param kernelSize размер ядра
     * @param stride шаг
     * @param padding отступ
     * @return true если слой добавлен успешно
     */
    public boolean addConv2DLayer(int filters, int kernelSize, int stride, int padding) {
        if (!isValidLayerCount()) {
            _log.warn("Cannot add layer: maximum layer count reached for bot " + botId);
            return false;
        }
        
        try {
            Conv2DLayer layer = new Conv2DLayer(filters, kernelSize, stride, padding);
            
            // Определяем размеры входа для слоя
            if (layers.isEmpty()) {
                // Первый слой - используем размеры по умолчанию
                layer.initialize(32, 32, 3); // 32x32 RGB изображение
                inputShape = new int[]{32, 32, 3};
            } else {
                // Последующий слой - используем выход предыдущего
                NetworkLayer lastLayer = layers.get(layers.size() - 1);
                if (lastLayer instanceof Conv2DLayer) {
                    Conv2DLayer convLayer = (Conv2DLayer) lastLayer;
                    int[] lastOutputShape = convLayer.getOutputShape();
                    layer.initialize(lastOutputShape[0], lastOutputShape[1], lastOutputShape[2]);
                } else if (lastLayer instanceof MaxPoolingLayer) {
                    MaxPoolingLayer poolLayer = (MaxPoolingLayer) lastLayer;
                    int[] lastOutputShape = poolLayer.getOutputShape();
                    layer.initialize(lastOutputShape[0], lastOutputShape[1], lastOutputShape[2]);
                } else {
                    _log.warn("Cannot determine input shape for Conv2D layer for bot " + botId);
                    return false;
                }
            }
            
            layers.add(layer);
            _log.info("Conv2D layer added to ConvolutionalNeuralNetwork for bot " + botId + 
                     ": filters=" + filters + ", kernelSize=" + kernelSize);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding Conv2D layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Добавляет слой максимального пулинга
     * 
     * @param poolSize размер окна пулинга
     * @param stride шаг пулинга
     * @return true если слой добавлен успешно
     */
    public boolean addMaxPoolingLayer(int poolSize, int stride) {
        if (!isValidLayerCount()) {
            _log.warn("Cannot add layer: maximum layer count reached for bot " + botId);
            return false;
        }
        
        try {
            MaxPoolingLayer layer = new MaxPoolingLayer(poolSize, stride);
            
            // Определяем размеры входа для слоя
            if (layers.isEmpty()) {
                _log.warn("Cannot add MaxPooling layer as first layer for bot " + botId);
                return false;
            }
            
            NetworkLayer lastLayer = layers.get(layers.size() - 1);
            if (lastLayer instanceof Conv2DLayer) {
                Conv2DLayer convLayer = (Conv2DLayer) lastLayer;
                int[] lastOutputShape = convLayer.getOutputShape();
                layer.initialize(lastOutputShape[0], lastOutputShape[1], lastOutputShape[2]);
            } else if (lastLayer instanceof MaxPoolingLayer) {
                MaxPoolingLayer poolLayer = (MaxPoolingLayer) lastLayer;
                int[] lastOutputShape = poolLayer.getOutputShape();
                layer.initialize(lastOutputShape[0], lastOutputShape[1], lastOutputShape[2]);
            } else {
                _log.warn("Cannot determine input shape for MaxPooling layer for bot " + botId);
                return false;
            }
            
            layers.add(layer);
            _log.info("MaxPooling layer added to ConvolutionalNeuralNetwork for bot " + botId + 
                     ": poolSize=" + poolSize + ", stride=" + stride);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding MaxPooling layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Добавляет слой сглаживания
     * 
     * @return true если слой добавлен успешно
     */
    public boolean addFlattenLayer() {
        if (!isValidLayerCount()) {
            _log.warn("Cannot add layer: maximum layer count reached for bot " + botId);
            return false;
        }
        
        try {
            FlattenLayer layer = new FlattenLayer();
            
            // Определяем размеры входа для слоя
            if (layers.isEmpty()) {
                _log.warn("Cannot add Flatten layer as first layer for bot " + botId);
                return false;
            }
            
            NetworkLayer lastLayer = layers.get(layers.size() - 1);
            if (lastLayer instanceof Conv2DLayer) {
                Conv2DLayer convLayer = (Conv2DLayer) lastLayer;
                int[] lastOutputShape = convLayer.getOutputShape();
                layer.initialize(lastOutputShape[0], lastOutputShape[1], lastOutputShape[2]);
            } else if (lastLayer instanceof MaxPoolingLayer) {
                MaxPoolingLayer poolLayer = (MaxPoolingLayer) lastLayer;
                int[] lastOutputShape = poolLayer.getOutputShape();
                layer.initialize(lastOutputShape[0], lastOutputShape[1], lastOutputShape[2]);
            } else {
                _log.warn("Cannot determine input shape for Flatten layer for bot " + botId);
                return false;
            }
            
            layers.add(layer);
            _log.info("Flatten layer added to ConvolutionalNeuralNetwork for bot " + botId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding Flatten layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Добавляет полносвязный слой
     * 
     * @param neurons количество нейронов
     * @param activation функция активации
     * @return true если слой добавлен успешно
     */
    public boolean addDenseLayer(int neurons, ActivationFunction activation) {
        if (!isValidLayerCount()) {
            _log.warn("Cannot add layer: maximum layer count reached for bot " + botId);
            return false;
        }
        
        try {
            DenseLayer layer = new DenseLayer(neurons, activation);
            
            // Определяем размер входа для слоя
            int inputSize;
            if (layers.isEmpty()) {
                inputSize = 10; // Размер по умолчанию
            } else {
                NetworkLayer lastLayer = layers.get(layers.size() - 1);
                if (lastLayer instanceof FlattenLayer) {
                    inputSize = lastLayer.getOutputSize();
                } else if (lastLayer instanceof DenseLayer) {
                    inputSize = lastLayer.getOutputSize();
                } else {
                    _log.warn("Cannot determine input size for Dense layer for bot " + botId);
                    return false;
                }
            }
            
            layer.initialize(inputSize, neurons);
            layers.add(layer);
            _log.info("Dense layer added to ConvolutionalNeuralNetwork for bot " + botId + 
                     ": neurons=" + neurons);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding Dense layer for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Выполняет прямое распространение
     * 
     * @param input входное изображение [height, width, channels]
     * @return результат обработки
     */
    public double[] forward(double[][][] input) {
        if (!isActive) {
            _log.warn("Cannot perform forward pass: network not active for bot " + botId);
            return null;
        }
        
        if (layers.isEmpty()) {
            _log.warn("Cannot perform forward pass: no layers in network for bot " + botId);
            return null;
        }
        
        try {
            Object currentInput = input;
            
            // Проходим через все слои
            for (NetworkLayer layer : layers) {
                if (layer instanceof Conv2DLayer) {
                    // Conv2D слой
                    currentInput = ((Conv2DLayer) layer).forward((double[][][]) currentInput);
                } else if (layer instanceof MaxPoolingLayer) {
                    // MaxPooling слой
                    currentInput = ((MaxPoolingLayer) layer).forward((double[][][]) currentInput);
                } else if (layer instanceof FlattenLayer) {
                    // Flatten слой
                    currentInput = ((FlattenLayer) layer).forward((double[][][]) currentInput);
                } else if (layer instanceof DenseLayer) {
                    // Dense слой
                    currentInput = ((DenseLayer) layer).forward((double[]) currentInput);
                }
            }
            
            return (double[]) currentInput;
            
        } catch (Exception e) {
            _log.error("Error in forward propagation for bot " + botId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Обучает сеть на предоставленных данных
     * 
     * @param inputs входные изображения
     * @param targets целевые значения
     * @param epochs количество эпох
     * @return true если обучение прошло успешно
     */
    public boolean train(double[][][][] inputs, double[][] targets, int epochs) {
        if (!isActive) {
            _log.warn("Cannot train: network not active for bot " + botId);
            return false;
        }
        
        if (inputs == null || targets == null || inputs.length != targets.length) {
            _log.warn("Invalid training data for bot " + botId);
            return false;
        }
        
        if (optimizer == null) {
            optimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.ADAM, 0.001);
        }
        
        try {
            totalError = 0.0;
            trainingEpochs = epochs;
            
            for (int epoch = 0; epoch < epochs; epoch++) {
                double epochError = 0.0;
                
                for (int i = 0; i < inputs.length; i++) {
                    // Прямое распространение
                    double[] prediction = forward(inputs[i]);
                    if (prediction == null) {
                        continue;
                    }
                    
                    // Вычисляем ошибку
                    double error = calculateError(prediction, targets[i]);
                    epochError += error;
                    
                    // Обратное распространение
                    backward(targets[i], prediction);
                }
                
                epochError /= inputs.length;
                totalError = epochError;
                
                if (epoch % 10 == 0) {
                    _log.info("ConvolutionalNeuralNetwork training epoch " + epoch + 
                             " for bot " + botId + " - Error: " + String.format("%.6f", epochError));
                }
            }
            
            _log.info("ConvolutionalNeuralNetwork training completed for bot " + botId + 
                     " in " + epochs + " epochs with error " + String.format("%.6f", totalError));
            return true;
            
        } catch (Exception e) {
            _log.error("Error in training for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Очищает все слои сети
     */
    public void clearLayers() {
        layers.clear();
        isActive = false;
        trainingEpochs = 0;
        totalError = 0.0;
        _log.info("All layers cleared for ConvolutionalNeuralNetwork bot " + botId);
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Проверяет валидность количества слоев
     * 
     * @return true если можно добавить еще слои
     */
    private boolean isValidLayerCount() {
        return layers.size() < MAX_LAYERS;
    }
    
    /**
     * Вычисляет ошибку между предсказанием и целевым значением
     * 
     * @param prediction предсказание
     * @param target целевое значение
     * @return ошибка
     */
    private double calculateError(double[] prediction, double[] target) {
        if (prediction.length != target.length) {
            return Double.MAX_VALUE;
        }
        
        double error = 0.0;
        for (int i = 0; i < prediction.length; i++) {
            double diff = prediction[i] - target[i];
            error += diff * diff;
        }
        
        return error / prediction.length;
    }
    
    /**
     * Выполняет обратное распространение
     * 
     * @param target целевое значение
     * @param prediction предсказание
     */
    private void backward(double[] target, double[] prediction) {
        // Вычисляем градиент потерь
        double[] gradient = new double[prediction.length];
        for (int i = 0; i < prediction.length; i++) {
            gradient[i] = 2.0 * (prediction[i] - target[i]) / prediction.length;
        }
        
        // Обратное распространение через слои
        for (int i = layers.size() - 1; i >= 0; i--) {
            NetworkLayer layer = layers.get(i);
            
            if (layer instanceof DenseLayer) {
                gradient = ((DenseLayer) layer).backward(new double[0], gradient);
            } else if (layer instanceof FlattenLayer) {
                double[][][] grad3D = ((FlattenLayer) layer).backward(gradient);
                // Преобразуем 3D градиент в 1D для следующего слоя
                gradient = flatten3D(grad3D);
            } else if (layer instanceof Conv2DLayer) {
                double[][][] grad3D = ((Conv2DLayer) layer).backward(reshapeTo3D(gradient, layer.getOutputSize()));
                gradient = flatten3D(grad3D);
            } else if (layer instanceof MaxPoolingLayer) {
                double[][][] grad3D = ((MaxPoolingLayer) layer).backward(reshapeTo3D(gradient, layer.getOutputSize()));
                gradient = flatten3D(grad3D);
            }
        }
        
        // Обновляем веса
        optimizer.updateWeights(layers);
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
    
    /**
     * Преобразует 1D массив в 3D
     * 
     * @param array 1D массив
     * @param totalSize общий размер
     * @return 3D массив
     */
    private double[][][] reshapeTo3D(double[] array, int totalSize) {
        // Упрощенная реализация - в реальности нужно знать размеры
        int size = (int) Math.cbrt(totalSize);
        double[][][] reshaped = new double[size][size][size];
        
        int index = 0;
        for (int h = 0; h < size && index < array.length; h++) {
            for (int w = 0; w < size && index < array.length; w++) {
                for (int c = 0; c < size && index < array.length; c++) {
                    reshaped[h][w][c] = array[index++];
                }
            }
        }
        
        return reshaped;
    }
    
    // ==================== ГЕТТЕРЫ ====================
    
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
     * Возвращает состояние сети
     * 
     * @return true если сеть активна
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Возвращает общую ошибку обучения
     * 
     * @return ошибка
     */
    public double getTotalError() {
        return totalError;
    }
    
    /**
     * Возвращает количество эпох обучения
     * 
     * @return количество эпох
     */
    public int getTrainingEpochs() {
        return trainingEpochs;
    }
    
    /**
     * Возвращает статистику сети
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ConvolutionalNeuralNetwork Statistics ===\n");
        stats.append("Bot ID: ").append(botId).append("\n");
        stats.append("Active: ").append(isActive).append("\n");
        stats.append("Layers: ").append(layers.size()).append("\n");
        stats.append("Training Epochs: ").append(trainingEpochs).append("\n");
        stats.append("Total Error: ").append(String.format("%.6f", totalError)).append("\n");
        
        if (inputShape != null) {
            stats.append("Input Shape: ").append(java.util.Arrays.toString(inputShape)).append("\n");
        }
        
        return stats.toString();
    }
}

