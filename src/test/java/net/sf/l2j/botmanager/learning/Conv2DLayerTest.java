package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для Conv2DLayer
 * 
 * @author ni-okr
 * @version 3.2
 */
class Conv2DLayerTest {
    
    private Conv2DLayer layer;
    
    @BeforeEach
    void setUp() {
        layer = new Conv2DLayer(32, 3, 1, 1);
    }
    
    @Test
    @DisplayName("Should create Conv2D layer with correct parameters")
    void testCreateConv2DLayer() {
        assertNotNull(layer, "Layer should not be null");
        assertEquals(32, layer.getFilters(), "Should have correct number of filters");
        assertEquals(3, layer.getKernelSize(), "Should have correct kernel size");
        assertEquals(1, layer.getStride(), "Should have correct stride");
        assertEquals(1, layer.getPadding(), "Should have correct padding");
    }
    
    @Test
    @DisplayName("Should create Conv2D layer with default parameters")
    void testCreateConv2DLayerWithDefaults() {
        Conv2DLayer defaultLayer = new Conv2DLayer(16, 5);
        
        assertNotNull(defaultLayer, "Layer should not be null");
        assertEquals(16, defaultLayer.getFilters(), "Should have correct number of filters");
        assertEquals(5, defaultLayer.getKernelSize(), "Should have correct kernel size");
        assertEquals(1, defaultLayer.getStride(), "Should have default stride");
        assertEquals(0, defaultLayer.getPadding(), "Should have default padding");
    }
    
    @Test
    @DisplayName("Should initialize layer with correct input shape")
    void testInitializeLayer() {
        layer.initialize(32, 32, 3);
        
        int[] inputShape = layer.getInputShape();
        assertNotNull(inputShape, "Input shape should not be null");
        assertEquals(3, inputShape.length, "Input shape should have 3 dimensions");
        assertEquals(32, inputShape[0], "Should have correct height");
        assertEquals(32, inputShape[1], "Should have correct width");
        assertEquals(3, inputShape[2], "Should have correct channels");
        
        int[] outputShape = layer.getOutputShape();
        assertNotNull(outputShape, "Output shape should not be null");
        assertEquals(3, outputShape.length, "Output shape should have 3 dimensions");
        assertEquals(32, outputShape[0], "Should have correct output height");
        assertEquals(32, outputShape[1], "Should have correct output width");
        assertEquals(32, outputShape[2], "Should have correct output channels");
    }
    
    @Test
    @DisplayName("Should perform forward propagation with valid input")
    void testForwardPropagationWithValidInput() {
        layer.initialize(32, 32, 3);
        
        double[][][] input = createTestImage(32, 32, 3);
        double[][][] result = layer.forward((double[][][]) input);
        
        assertNotNull(result, "Result should not be null");
        assertEquals(32, result.length, "Should have correct output height");
        assertEquals(32, result.length, "Should have correct output width");
        assertEquals(32, result[0][0].length, "Should have correct output channels");
    }
    
    @Test
    @DisplayName("Should not perform forward propagation with null input")
    void testForwardPropagationWithNullInput() {
        layer.initialize(32, 32, 3);
        
        assertThrows(IllegalArgumentException.class, () -> {
            layer.forward((double[][][]) null);
        }, "Should throw exception for null input");
    }
    
    @Test
    @DisplayName("Should not perform forward propagation with invalid input shape")
    void testForwardPropagationWithInvalidInputShape() {
        layer.initialize(32, 32, 3);
        
        double[][][] invalidInput = createTestImage(16, 16, 3); // Неправильный размер
        
        assertThrows(IllegalArgumentException.class, () -> {
            layer.forward((double[][][]) invalidInput);
        }, "Should throw exception for invalid input shape");
    }
    
    @Test
    @DisplayName("Should perform backward propagation after forward pass")
    void testBackwardPropagation() {
        layer.initialize(32, 32, 3);
        
        // Сначала выполняем прямое распространение
        double[][][] input = createTestImage(32, 32, 3);
        layer.forward((double[][][]) input);
        
        // Затем обратное распространение
        double[][][] gradient = createTestImage(32, 32, 32);
        double[][][] result = layer.backward(gradient);
        
        assertNotNull(result, "Result should not be null");
        assertEquals(32, result.length, "Should have correct input height");
        assertEquals(32, result[0].length, "Should have correct input width");
        assertEquals(3, result[0][0].length, "Should have correct input channels");
    }
    
    @Test
    @DisplayName("Should not perform backward propagation without forward pass")
    void testBackwardPropagationWithoutForwardPass() {
        layer.initialize(32, 32, 3);
        
        double[][][] gradient = createTestImage(32, 32, 32);
        
        assertThrows(IllegalStateException.class, () -> {
            layer.backward(gradient);
        }, "Should throw exception without forward pass");
    }
    
    @Test
    @DisplayName("Should not perform backward propagation with null gradient")
    void testBackwardPropagationWithNullGradient() {
        layer.initialize(32, 32, 3);
        layer.forward((double[][][]) createTestImage(32, 32, 3));
        
        assertThrows(IllegalArgumentException.class, () -> {
            layer.backward(null);
        }, "Should throw exception for null gradient");
    }
    
    @Test
    @DisplayName("Should update weights with learning rate")
    void testUpdateWeights() {
        layer.initialize(32, 32, 3);
        
        // Выполняем прямое и обратное распространение
        double[][][] input = createTestImage(32, 32, 3);
        layer.forward((double[][][]) input);
        layer.backward(createTestImage(32, 32, 32));
        
        // Обновляем веса
        assertDoesNotThrow(() -> {
            layer.updateWeights(0.01);
        }, "Should update weights without exception");
    }
    
    @Test
    @DisplayName("Should get correct output size")
    void testGetOutputSize() {
        layer.initialize(32, 32, 3);
        
        int outputSize = layer.getOutputSize();
        assertEquals(32 * 32 * 32, outputSize, "Should have correct output size");
    }
    
    @Test
    @DisplayName("Should handle different kernel sizes")
    void testDifferentKernelSizes() {
        Conv2DLayer layer3x3 = new Conv2DLayer(16, 3, 1, 1);
        Conv2DLayer layer5x5 = new Conv2DLayer(16, 5, 1, 1);
        
        layer3x3.initialize(32, 32, 3);
        layer5x5.initialize(32, 32, 3);
        
        int[] outputShape3x3 = layer3x3.getOutputShape();
        int[] outputShape5x5 = layer5x5.getOutputShape();
        
        // 5x5 ядро должно дать меньший выход
        assertTrue(outputShape5x5[0] < outputShape3x3[0], "5x5 kernel should produce smaller output");
        assertTrue(outputShape5x5[1] < outputShape3x3[1], "5x5 kernel should produce smaller output");
    }
    
    @Test
    @DisplayName("Should handle different strides")
    void testDifferentStrides() {
        Conv2DLayer layerStride1 = new Conv2DLayer(16, 3, 1, 0);
        Conv2DLayer layerStride2 = new Conv2DLayer(16, 3, 2, 0);
        
        layerStride1.initialize(32, 32, 3);
        layerStride2.initialize(32, 32, 3);
        
        int[] outputShape1 = layerStride1.getOutputShape();
        int[] outputShape2 = layerStride2.getOutputShape();
        
        // Stride 2 должен дать меньший выход
        assertTrue(outputShape2[0] < outputShape1[0], "Stride 2 should produce smaller output");
        assertTrue(outputShape2[1] < outputShape1[1], "Stride 2 should produce smaller output");
    }
    
    @Test
    @DisplayName("Should handle different padding")
    void testDifferentPadding() {
        Conv2DLayer layerNoPadding = new Conv2DLayer(16, 3, 1, 0);
        Conv2DLayer layerWithPadding = new Conv2DLayer(16, 3, 1, 1);
        
        layerNoPadding.initialize(32, 32, 3);
        layerWithPadding.initialize(32, 32, 3);
        
        int[] outputShapeNoPadding = layerNoPadding.getOutputShape();
        int[] outputShapeWithPadding = layerWithPadding.getOutputShape();
        
        // Padding должен сохранить размер
        assertTrue(outputShapeWithPadding[0] >= outputShapeNoPadding[0], "Padding should preserve or increase size");
        assertTrue(outputShapeWithPadding[1] >= outputShapeNoPadding[1], "Padding should preserve or increase size");
    }
    
    @Test
    @DisplayName("Should handle multiple forward propagations")
    void testMultipleForwardPropagations() {
        layer.initialize(32, 32, 3);
        double[][][] input = createTestImage(32, 32, 3);
        
        // Выполняем несколько прямых распространений
        for (int i = 0; i < 10; i++) {
            double[][][] result = layer.forward((double[][][]) input);
            assertNotNull(result, "Result should not be null for iteration " + i);
            assertEquals(32, result.length, "Result should have correct height for iteration " + i);
            assertEquals(32, result[0].length, "Result should have correct width for iteration " + i);
            assertEquals(32, result[0][0].length, "Result should have correct channels for iteration " + i);
        }
    }
    
    @Test
    @DisplayName("Should handle different input sizes")
    void testDifferentInputSizes() {
        Conv2DLayer layer = new Conv2DLayer(16, 3, 1, 1);
        
        // Тестируем разные размеры
        int[] sizes = {16, 32, 64};
        
        for (int size : sizes) {
            layer.initialize(size, size, 3);
            double[][][] input = createTestImage(size, size, 3);
            double[][][] result = layer.forward((double[][][]) input);
            
            assertNotNull(result, "Result should not be null for size " + size);
            assertEquals(size, result.length, "Should have correct height for size " + size);
            assertEquals(size, result[0].length, "Should have correct width for size " + size);
        }
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
}

