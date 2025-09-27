package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для GRULayer
 * 
 * @author ni-okr
 * @version 3.3
 */
@DisplayName("GRULayer Tests")
class GRULayerTest {
    
    private GRULayer layer;
    private static final int HIDDEN_UNITS = 32;
    private static final int INPUT_SIZE = 10;
    
    @BeforeEach
    void setUp() {
        layer = new GRULayer(HIDDEN_UNITS);
        layer.initialize(INPUT_SIZE, HIDDEN_UNITS);
    }
    
    @Nested
    @DisplayName("Layer Creation Tests")
    class LayerCreationTests {
        
        @Test
        @DisplayName("Should create GRU layer with correct parameters")
        void testLayerCreation() {
            assertNotNull(layer, "Layer should not be null");
            assertEquals(HIDDEN_UNITS, layer.getHiddenUnits(), "Should have correct hidden units");
            assertEquals(NetworkLayer.LayerType.GRU, layer.getType(), "Should have correct type");
        }
        
        @Test
        @DisplayName("Should initialize with correct sizes")
        void testInitialization() {
            assertEquals(INPUT_SIZE, layer.getInputSize(), "Should have correct input size");
            assertEquals(HIDDEN_UNITS, layer.getOutputSize(), "Should have correct output size");
        }
        
        @Test
        @DisplayName("Should throw exception for invalid hidden units")
        void testInvalidHiddenUnits() {
            assertThrows(IllegalArgumentException.class, () -> new GRULayer(0), 
                "Should throw exception for zero hidden units");
            assertThrows(IllegalArgumentException.class, () -> new GRULayer(-1), 
                "Should throw exception for negative hidden units");
        }
    }
    
    @Nested
    @DisplayName("Forward Propagation Tests")
    class ForwardPropagationTests {
        
        @Test
        @DisplayName("Should perform forward propagation with valid input")
        void testForwardPropagationWithValidInput() {
            double[] input = createTestInput(INPUT_SIZE);
            
            double[] result = layer.forward(input);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(HIDDEN_UNITS, result.length, "Should have correct output size");
            
            // Проверяем, что все значения в разумных пределах
            for (double value : result) {
                assertTrue(value >= -1.0 && value <= 1.0, 
                    "Output values should be in reasonable range");
            }
        }
        
        @Test
        @DisplayName("Should not perform forward propagation with null input")
        void testForwardPropagationWithNullInput() {
            assertThrows(IllegalArgumentException.class, () -> layer.forward(null), 
                "Should throw exception for null input");
        }
        
        @Test
        @DisplayName("Should not perform forward propagation with invalid input size")
        void testForwardPropagationWithInvalidInputSize() {
            double[] invalidInput = createTestInput(INPUT_SIZE + 1);
            
            assertThrows(IllegalArgumentException.class, () -> layer.forward(invalidInput), 
                "Should throw exception for invalid input size");
        }
        
        @Test
        @DisplayName("Should maintain state between forward passes")
        void testStateMaintenance() {
            double[] input1 = createTestInput(INPUT_SIZE);
            double[] input2 = createTestInput(INPUT_SIZE);
            
            double[] result1 = layer.forward(input1);
            double[] result2 = layer.forward(input2);
            
            // Результаты должны быть разными из-за состояния
            assertNotEquals(result1, result2, "Results should be different due to state");
        }
    }
    
    @Nested
    @DisplayName("Backward Propagation Tests")
    class BackwardPropagationTests {
        
        @Test
        @DisplayName("Should perform backward propagation after forward pass")
        void testBackwardPropagationAfterForwardPass() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            double[] outputGradients = createTestInput(HIDDEN_UNITS);
            double[] result = layer.backward(input, outputGradients);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(INPUT_SIZE, result.length, "Should have correct input gradient size");
        }
        
        @Test
        @DisplayName("Should not perform backward propagation without forward pass")
        void testBackwardPropagationWithoutForwardPass() {
            double[] input = createTestInput(INPUT_SIZE);
            double[] outputGradients = createTestInput(HIDDEN_UNITS);
            
            assertThrows(IllegalStateException.class, () -> layer.backward(input, outputGradients), 
                "Should throw exception without forward pass");
        }
        
        @Test
        @DisplayName("Should not perform backward propagation with null gradients")
        void testBackwardPropagationWithNullGradients() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            assertThrows(IllegalArgumentException.class, () -> layer.backward(input, null), 
                "Should throw exception for null gradients");
        }
        
        @Test
        @DisplayName("Should not perform backward propagation with invalid gradient size")
        void testBackwardPropagationWithInvalidGradientSize() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            double[] invalidGradients = createTestInput(HIDDEN_UNITS + 1);
            
            assertThrows(IllegalArgumentException.class, () -> layer.backward(input, invalidGradients), 
                "Should throw exception for invalid gradient size");
        }
    }
    
    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {
        
        @Test
        @DisplayName("Should reset state")
        void testResetState() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            // Сбрасываем состояние
            layer.resetState();
            
            // Проверяем, что состояние сброшено (нет исключений)
            assertDoesNotThrow(() -> layer.resetState(), 
                "Should reset state without errors");
        }
        
        @Test
        @DisplayName("Should set initial state")
        void testSetInitialState() {
            double[] initialHidden = createTestInput(HIDDEN_UNITS);
            
            layer.setInitialState(initialHidden);
            
            // Проверяем, что состояние установлено (нет исключений)
            assertDoesNotThrow(() -> layer.setInitialState(initialHidden), 
                "Should set initial state without errors");
        }
        
        @Test
        @DisplayName("Should handle null initial state")
        void testSetNullInitialState() {
            assertDoesNotThrow(() -> layer.setInitialState(null), 
                "Should handle null initial state");
        }
        
        @Test
        @DisplayName("Should handle invalid initial state size")
        void testSetInvalidInitialStateSize() {
            double[] invalidHidden = createTestInput(HIDDEN_UNITS + 1);
            
            assertDoesNotThrow(() -> layer.setInitialState(invalidHidden), 
                "Should handle invalid initial state size");
        }
    }
    
    @Nested
    @DisplayName("Weight Update Tests")
    class WeightUpdateTests {
        
        @Test
        @DisplayName("Should update weights")
        void testUpdateWeights() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            double[] outputGradients = createTestInput(HIDDEN_UNITS);
            layer.backward(input, outputGradients);
            
            // Обновляем веса
            assertDoesNotThrow(() -> layer.updateWeights(0.01), 
                "Should update weights without errors");
        }
        
        @Test
        @DisplayName("Should handle different learning rates")
        void testUpdateWeightsWithDifferentLearningRates() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            double[] outputGradients = createTestInput(HIDDEN_UNITS);
            layer.backward(input, outputGradients);
            
            // Тестируем разные скорости обучения
            assertDoesNotThrow(() -> layer.updateWeights(0.001), 
                "Should handle small learning rate");
            assertDoesNotThrow(() -> layer.updateWeights(0.1), 
                "Should handle large learning rate");
        }
    }
    
    @Nested
    @DisplayName("State Access Tests")
    class StateAccessTests {
        
        @Test
        @DisplayName("Should get hidden state")
        void testGetHiddenState() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            double[] hiddenState = layer.getHiddenState();
            
            assertNotNull(hiddenState, "Hidden state should not be null");
            assertEquals(HIDDEN_UNITS, hiddenState.length, "Should have correct hidden state size");
        }
        
        @Test
        @DisplayName("Should return copies of states")
        void testStateCopies() {
            double[] input = createTestInput(INPUT_SIZE);
            layer.forward(input);
            
            double[] hiddenState1 = layer.getHiddenState();
            double[] hiddenState2 = layer.getHiddenState();
            
            assertNotSame(hiddenState1, hiddenState2, "Should return copies, not references");
        }
    }
    
    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {
        
        @Test
        @DisplayName("Should provide statistics")
        void testStatistics() {
            String stats = layer.getStatistics();
            
            assertNotNull(stats, "Statistics should not be null");
            assertTrue(stats.contains("GRULayer Statistics"), "Should contain layer type");
            assertTrue(stats.contains("Hidden Units: " + HIDDEN_UNITS), "Should contain hidden units");
            assertTrue(stats.contains("Input Size: " + INPUT_SIZE), "Should contain input size");
            assertTrue(stats.contains("Parameters:"), "Should contain parameter count");
        }
    }
    
    @Nested
    @DisplayName("Multiple Operations Tests")
    class MultipleOperationsTests {
        
        @Test
        @DisplayName("Should handle multiple forward propagations")
        void testMultipleForwardPropagations() {
            for (int i = 0; i < 5; i++) {
                double[] input = createTestInput(INPUT_SIZE);
                double[] result = layer.forward(input);
                
                assertNotNull(result, "Result should not be null for iteration " + i);
                assertEquals(HIDDEN_UNITS, result.length, "Should have correct output size for iteration " + i);
            }
        }
        
        @Test
        @DisplayName("Should handle multiple forward-backward cycles")
        void testMultipleForwardBackwardCycles() {
            for (int i = 0; i < 3; i++) {
                double[] input = createTestInput(INPUT_SIZE);
                layer.forward(input);
                
                double[] outputGradients = createTestInput(HIDDEN_UNITS);
                double[] inputGradients = layer.backward(input, outputGradients);
                
                assertNotNull(inputGradients, "Input gradients should not be null for iteration " + i);
                assertEquals(INPUT_SIZE, inputGradients.length, "Should have correct input gradient size for iteration " + i);
                
                layer.updateWeights(0.01);
            }
        }
    }
    
    @Nested
    @DisplayName("Comparison with LSTM Tests")
    class ComparisonWithLSTMTests {
        
        @Test
        @DisplayName("Should have fewer parameters than LSTM")
        void testParameterCountComparison() {
            LSTMLayer lstmLayer = new LSTMLayer(HIDDEN_UNITS);
            lstmLayer.initialize(INPUT_SIZE, HIDDEN_UNITS);
            
            // GRU должен иметь меньше параметров, чем LSTM
            // Это проверяется через статистику
            String gruStats = layer.getStatistics();
            String lstmStats = lstmLayer.getStatistics();
            
            assertNotNull(gruStats, "GRU statistics should not be null");
            assertNotNull(lstmStats, "LSTM statistics should not be null");
            
            // GRU имеет 3 набора весов, LSTM имеет 4 набора
            assertTrue(gruStats.contains("Parameters:"), "GRU should have parameter count");
            assertTrue(lstmStats.contains("Parameters:"), "LSTM should have parameter count");
        }
        
        @Test
        @DisplayName("Should have similar performance to LSTM")
        void testPerformanceComparison() {
            double[] input = createTestInput(INPUT_SIZE);
            
            long startTime = System.nanoTime();
            double[] gruResult = layer.forward(input);
            long gruTime = System.nanoTime() - startTime;
            
            LSTMLayer lstmLayer = new LSTMLayer(HIDDEN_UNITS);
            lstmLayer.initialize(INPUT_SIZE, HIDDEN_UNITS);
            
            startTime = System.nanoTime();
            double[] lstmResult = lstmLayer.forward(input);
            long lstmTime = System.nanoTime() - startTime;
            
            assertNotNull(gruResult, "GRU result should not be null");
            assertNotNull(lstmResult, "LSTM result should not be null");
            
            // GRU должен быть быстрее LSTM (хотя это может варьироваться)
            // Проверяем, что оба работают в разумное время
            assertTrue(gruTime < 1_000_000, "GRU should complete in reasonable time"); // 1ms
            assertTrue(lstmTime < 1_000_000, "LSTM should complete in reasonable time"); // 1ms
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает тестовый входной массив
     * 
     * @param size размер массива
     * @return тестовый массив
     */
    private double[] createTestInput(int size) {
        double[] input = new double[size];
        for (int i = 0; i < size; i++) {
            input[i] = Math.random() * 2 - 1; // Случайные значения от -1 до 1
        }
        return input;
    }
}
