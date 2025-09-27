package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ConvolutionalNeuralNetwork
 * 
 * @author ni-okr
 * @version 3.2
 */
class ConvolutionalNeuralNetworkTest {
    
    private ConvolutionalNeuralNetwork network;
    private final int botId = 1;
    
    @BeforeEach
    void setUp() {
        network = new ConvolutionalNeuralNetwork(botId);
    }
    
    @Test
    @DisplayName("Should create CNN with correct bot ID")
    void testCreateCNN() {
        assertNotNull(network, "Network should not be null");
        assertEquals(botId, network.getBotId(), "Bot ID should match");
        assertFalse(network.isActive(), "Network should not be active initially");
        assertEquals(0, network.getLayerCount(), "Should have no layers initially");
    }
    
    @Test
    @DisplayName("Should activate and deactivate network")
    void testActivateDeactivate() {
        // Добавляем слой для активации
        network.addConv2DLayer(32, 3, 1, 1);
        network.activate();
        
        assertTrue(network.isActive(), "Network should be active after activation");
        
        network.deactivate();
        assertFalse(network.isActive(), "Network should not be active after deactivation");
    }
    
    @Test
    @DisplayName("Should add Conv2D layer")
    void testAddConv2DLayer() {
        boolean result = network.addConv2DLayer(32, 3, 1, 1);
        
        assertTrue(result, "Should successfully add Conv2D layer");
        assertEquals(1, network.getLayerCount(), "Should have 1 layer");
    }
    
    @Test
    @DisplayName("Should add MaxPooling layer")
    void testAddMaxPoolingLayer() {
        // Добавляем Conv2D слой сначала
        network.addConv2DLayer(32, 3, 1, 1);
        
        boolean result = network.addMaxPoolingLayer(2, 2);
        
        assertTrue(result, "Should successfully add MaxPooling layer");
        assertEquals(2, network.getLayerCount(), "Should have 2 layers");
    }
    
    @Test
    @DisplayName("Should add Flatten layer")
    void testAddFlattenLayer() {
        // Добавляем Conv2D слой сначала
        network.addConv2DLayer(32, 3, 1, 1);
        
        boolean result = network.addFlattenLayer();
        
        assertTrue(result, "Should successfully add Flatten layer");
        assertEquals(2, network.getLayerCount(), "Should have 2 layers");
    }
    
    @Test
    @DisplayName("Should add Dense layer")
    void testAddDenseLayer() {
        // Добавляем Flatten слой сначала
        network.addConv2DLayer(32, 3, 1, 1);
        network.addFlattenLayer();
        
        boolean result = network.addDenseLayer(64, new ActivationFunction.ReLU());
        
        assertTrue(result, "Should successfully add Dense layer");
        assertEquals(3, network.getLayerCount(), "Should have 3 layers");
    }
    
    @Test
    @DisplayName("Should not add MaxPooling as first layer")
    void testAddMaxPoolingAsFirstLayer() {
        boolean result = network.addMaxPoolingLayer(2, 2);
        
        assertFalse(result, "Should not add MaxPooling as first layer");
        assertEquals(0, network.getLayerCount(), "Should have no layers");
    }
    
    @Test
    @DisplayName("Should not add Flatten as first layer")
    void testAddFlattenAsFirstLayer() {
        boolean result = network.addFlattenLayer();
        
        assertFalse(result, "Should not add Flatten as first layer");
        assertEquals(0, network.getLayerCount(), "Should have no layers");
    }
    
    @Test
    @DisplayName("Should perform forward propagation with valid network")
    void testForwardPropagationWithValidNetwork() {
        network.addConv2DLayer(32, 3, 1, 1);
        network.addMaxPoolingLayer(2, 2);
        network.addFlattenLayer();
        network.addDenseLayer(10, new ActivationFunction.Softmax());
        network.activate();
        
        // Создаем тестовое изображение 32x32x3
        double[][][] input = createTestImage(32, 32, 3);
        double[] result = network.forward(input);
        
        assertNotNull(result, "Should return valid result");
        assertEquals(10, result.length, "Should return correct output size");
    }
    
    @Test
    @DisplayName("Should not perform forward propagation when not active")
    void testForwardPropagationWhenNotActive() {
        network.addConv2DLayer(32, 3, 1, 1);
        
        double[][][] input = createTestImage(32, 32, 3);
        double[] result = network.forward(input);
        
        assertNull(result, "Should return null when network is not active");
    }
    
    @Test
    @DisplayName("Should not perform forward propagation with no layers")
    void testForwardPropagationWithNoLayers() {
        network.activate();
        
        double[][][] input = createTestImage(32, 32, 3);
        double[] result = network.forward(input);
        
        assertNull(result, "Should return null when no layers");
    }
    
    @Test
    @DisplayName("Should train network with valid data")
    void testTrainWithValidData() {
        network.addConv2DLayer(32, 3, 1, 1);
        network.addMaxPoolingLayer(2, 2);
        network.addFlattenLayer();
        network.addDenseLayer(2, new ActivationFunction.Softmax());
        network.activate();
        
        // Создаем тестовые данные
        double[][][][] inputs = createTestImages(10, 32, 32, 3);
        double[][] targets = createTestTargets(10, 2);
        
        boolean result = network.train(inputs, targets, 5);
        
        assertTrue(result, "Training should succeed");
        assertTrue(network.getTotalError() >= 0, "Error should be non-negative");
        assertEquals(5, network.getTrainingEpochs(), "Should record training epochs");
    }
    
    @Test
    @DisplayName("Should not train when not active")
    void testTrainWhenNotActive() {
        network.addConv2DLayer(32, 3, 1, 1);
        
        double[][][][] inputs = createTestImages(5, 32, 32, 3);
        double[][] targets = createTestTargets(5, 2);
        
        boolean result = network.train(inputs, targets, 5);
        
        assertFalse(result, "Should not train when not active");
    }
    
    @Test
    @DisplayName("Should not train with invalid data")
    void testTrainWithInvalidData() {
        network.addConv2DLayer(32, 3, 1, 1);
        network.activate();
        
        // Неправильные размеры данных
        double[][][][] inputs = createTestImages(5, 32, 32, 3);
        double[][] targets = createTestTargets(3, 2); // Неправильный размер
        
        boolean result = network.train(inputs, targets, 5);
        
        assertFalse(result, "Should not train with invalid data");
    }
    
    @Test
    @DisplayName("Should clear all layers")
    void testClearLayers() {
        network.addConv2DLayer(32, 3, 1, 1);
        network.addMaxPoolingLayer(2, 2);
        network.activate();
        
        assertEquals(2, network.getLayerCount(), "Should have 2 layers before clear");
        assertTrue(network.isActive(), "Should be active before clear");
        
        network.clearLayers();
        
        assertEquals(0, network.getLayerCount(), "Should have no layers after clear");
        assertFalse(network.isActive(), "Should not be active after clear");
    }
    
    @Test
    @DisplayName("Should get statistics")
    void testGetStatistics() {
        network.addConv2DLayer(32, 3, 1, 1);
        network.activate();
        
        String stats = network.getStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("ConvolutionalNeuralNetwork Statistics"), "Should contain header");
        assertTrue(stats.contains("Bot ID: " + botId), "Should contain bot ID");
        assertTrue(stats.contains("Active: true"), "Should contain active status");
        assertTrue(stats.contains("Layers: 1"), "Should contain layer count");
    }
    
    @Test
    @DisplayName("Should handle multiple forward propagations")
    void testMultipleForwardPropagations() {
        network.addConv2DLayer(32, 3, 1, 1);
        network.addMaxPoolingLayer(2, 2);
        network.addFlattenLayer();
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        network.activate();
        
        double[][][] input = createTestImage(32, 32, 3);
        
        // Выполняем несколько прямых распространений
        for (int i = 0; i < 10; i++) {
            double[] result = network.forward(input);
            assertNotNull(result, "Result should not be null for iteration " + i);
            assertEquals(5, result.length, "Result should have correct size for iteration " + i);
        }
    }
    
    @Test
    @DisplayName("Should handle complex CNN architecture")
    void testComplexCNNArchitecture() {
        // Создаем сложную архитектуру CNN
        network.addConv2DLayer(32, 3, 1, 1);
        network.addMaxPoolingLayer(2, 2);
        network.addConv2DLayer(64, 3, 1, 1);
        network.addMaxPoolingLayer(2, 2);
        network.addFlattenLayer();
        network.addDenseLayer(128, new ActivationFunction.ReLU());
        network.addDenseLayer(10, new ActivationFunction.Softmax());
        network.activate();
        
        assertEquals(7, network.getLayerCount(), "Should have 7 layers");
        assertTrue(network.isActive(), "Should be active");
        
        double[][][] input = createTestImage(32, 32, 3);
        double[] result = network.forward(input);
        
        assertNotNull(result, "Should return valid result");
        assertEquals(10, result.length, "Should return correct output size");
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает тестовое изображение
     * 
     * @param height высота
     * @param width ширина
     * @param channels каналы
     * @return тестовое изображение
     */
    private double[][][] createTestImage(int height, int width, int channels) {
        double[][][] image = new double[height][width][channels];
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                for (int c = 0; c < channels; c++) {
                    image[h][w][c] = Math.random();
                }
            }
        }
        
        return image;
    }
    
    /**
     * Создает массив тестовых изображений
     * 
     * @param count количество изображений
     * @param height высота
     * @param width ширина
     * @param channels каналы
     * @return массив изображений
     */
    private double[][][][] createTestImages(int count, int height, int width, int channels) {
        double[][][][] images = new double[count][height][width][channels];
        
        for (int i = 0; i < count; i++) {
            images[i] = createTestImage(height, width, channels);
        }
        
        return images;
    }
    
    /**
     * Создает тестовые целевые значения
     * 
     * @param count количество образцов
     * @param classes количество классов
     * @return целевые значения
     */
    private double[][] createTestTargets(int count, int classes) {
        double[][] targets = new double[count][classes];
        
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < classes; j++) {
                targets[i][j] = Math.random();
            }
        }
        
        return targets;
    }
}

