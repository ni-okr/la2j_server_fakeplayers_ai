package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для RecurrentNeuralNetwork
 * 
 * @author ni-okr
 * @version 3.3
 */
@DisplayName("RecurrentNeuralNetwork Tests")
class RecurrentNeuralNetworkTest {
    
    private RecurrentNeuralNetwork network;
    private static final int TEST_BOT_ID = 1;
    
    @BeforeEach
    void setUp() {
        network = new RecurrentNeuralNetwork(TEST_BOT_ID);
    }
    
    @Nested
    @DisplayName("Network Creation Tests")
    class NetworkCreationTests {
        
        @Test
        @DisplayName("Should create network with correct bot ID")
        void testNetworkCreation() {
            assertNotNull(network, "Network should not be null");
            assertEquals(TEST_BOT_ID, network.getBotId(), "Should have correct bot ID");
            assertFalse(network.isActive(), "Should not be active initially");
            assertEquals(0, network.getLayerCount(), "Should have no layers initially");
        }
        
        @Test
        @DisplayName("Should have correct initial parameters")
        void testInitialParameters() {
            assertEquals(0.001, network.getLearningRate(), "Should have default learning rate");
            assertEquals(32, network.getBatchSize(), "Should have default batch size");
        }
    }
    
    @Nested
    @DisplayName("Layer Addition Tests")
    class LayerAdditionTests {
        
        @Test
        @DisplayName("Should add LSTM layer successfully")
        void testAddLSTMLayer() {
            boolean result = network.addLSTMLayer(64);
            
            assertTrue(result, "Should add LSTM layer successfully");
            assertEquals(1, network.getLayerCount(), "Should have one layer");
        }
        
        @Test
        @DisplayName("Should add GRU layer successfully")
        void testAddGRULayer() {
            boolean result = network.addGRULayer(32);
            
            assertTrue(result, "Should add GRU layer successfully");
            assertEquals(1, network.getLayerCount(), "Should have one layer");
        }
        
        @Test
        @DisplayName("Should add Dense layer successfully")
        void testAddDenseLayer() {
            boolean result = network.addDenseLayer(10, new ActivationFunction.ReLU());
            
            assertTrue(result, "Should add Dense layer successfully");
            assertEquals(1, network.getLayerCount(), "Should have one layer");
        }
        
        @Test
        @DisplayName("Should not add layers to active network")
        void testAddLayerToActiveNetwork() {
            network.addLSTMLayer(32);
            network.activate();
            
            boolean result = network.addGRULayer(16);
            
            assertFalse(result, "Should not add layer to active network");
            assertEquals(1, network.getLayerCount(), "Should still have one layer");
        }
        
        @Test
        @DisplayName("Should add multiple layers in sequence")
        void testAddMultipleLayers() {
            network.addLSTMLayer(32);
            network.addGRULayer(16);
            network.addDenseLayer(8, new ActivationFunction.Sigmoid());
            
            assertEquals(3, network.getLayerCount(), "Should have three layers");
        }
    }
    
    @Nested
    @DisplayName("Network Activation Tests")
    class NetworkActivationTests {
        
        @Test
        @DisplayName("Should activate network with layers")
        void testActivateNetworkWithLayers() {
            network.addLSTMLayer(32);
            network.addDenseLayer(10, new ActivationFunction.Softmax());
            
            boolean result = network.activate();
            
            assertTrue(result, "Should activate network successfully");
            assertTrue(network.isActive(), "Should be active");
        }
        
        @Test
        @DisplayName("Should not activate network without layers")
        void testActivateNetworkWithoutLayers() {
            boolean result = network.activate();
            
            assertFalse(result, "Should not activate network without layers");
            assertFalse(network.isActive(), "Should not be active");
        }
        
        @Test
        @DisplayName("Should deactivate network")
        void testDeactivateNetwork() {
            network.addLSTMLayer(32);
            network.activate();
            
            network.deactivate();
            
            assertFalse(network.isActive(), "Should not be active after deactivation");
        }
    }
    
    @Nested
    @DisplayName("Forward Propagation Tests")
    class ForwardPropagationTests {
        
        @BeforeEach
        void setUp() {
            network.addLSTMLayer(32);
            network.addDenseLayer(10, new ActivationFunction.Softmax());
            network.activate();
        }
        
        @Test
        @DisplayName("Should perform forward propagation with valid input")
        void testForwardPropagationWithValidInput() {
            double[][] inputSequence = createTestSequence(10, 5);
            
            double[][] result = network.forward(inputSequence);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(inputSequence.length, result.length, "Should have same sequence length");
            assertEquals(10, result[0].length, "Should have correct output size");
        }
        
        @Test
        @DisplayName("Should not perform forward propagation with inactive network")
        void testForwardPropagationWithInactiveNetwork() {
            network.deactivate();
            double[][] inputSequence = createTestSequence(10, 5);
            
            double[][] result = network.forward(inputSequence);
            
            assertNull(result, "Result should be null for inactive network");
        }
        
        @Test
        @DisplayName("Should handle empty sequence")
        void testForwardPropagationWithEmptySequence() {
            double[][] emptySequence = new double[0][5];
            
            double[][] result = network.forward(emptySequence);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(0, result.length, "Should return empty sequence");
        }
    }
    
    @Nested
    @DisplayName("Training Tests")
    class TrainingTests {
        
        @BeforeEach
        void setUp() {
            network.addLSTMLayer(16);
            network.addDenseLayer(5, new ActivationFunction.Softmax());
            network.activate();
        }
        
        @Test
        @DisplayName("Should train with valid data")
        void testTrainWithValidData() {
            double[][][] inputSequences = createTestSequences(5, 10, 3);
            double[][][] targetSequences = createTestSequences(5, 10, 5);
            
            boolean result = network.train(inputSequences, targetSequences, 10);
            
            assertTrue(result, "Training should succeed");
        }
        
        @Test
        @DisplayName("Should not train with inactive network")
        void testTrainWithInactiveNetwork() {
            network.deactivate();
            double[][][] inputSequences = createTestSequences(5, 10, 3);
            double[][][] targetSequences = createTestSequences(5, 10, 5);
            
            boolean result = network.train(inputSequences, targetSequences, 10);
            
            assertFalse(result, "Training should fail with inactive network");
        }
        
        @Test
        @DisplayName("Should not train with null data")
        void testTrainWithNullData() {
            boolean result = network.train(null, null, 10);
            
            assertFalse(result, "Training should fail with null data");
        }
        
        @Test
        @DisplayName("Should not train with mismatched data")
        void testTrainWithMismatchedData() {
            double[][][] inputSequences = createTestSequences(5, 10, 3);
            double[][][] targetSequences = createTestSequences(3, 10, 5); // Different count
            
            boolean result = network.train(inputSequences, targetSequences, 10);
            
            assertFalse(result, "Training should fail with mismatched data");
        }
    }
    
    @Nested
    @DisplayName("State Management Tests")
    class StateManagementTests {
        
        @BeforeEach
        void setUp() {
            network.addLSTMLayer(32);
            network.addGRULayer(16);
            network.activate();
        }
        
        @Test
        @DisplayName("Should reset states")
        void testResetStates() {
            // Выполняем forward pass для установки состояния
            double[][] inputSequence = createTestSequence(10, 5);
            network.forward(inputSequence);
            
            // Сбрасываем состояние
            network.resetStates();
            
            // Проверяем, что состояние сброшено (нет исключений)
            assertDoesNotThrow(() -> network.resetStates(), 
                "Should reset states without errors");
        }
        
        @Test
        @DisplayName("Should clear all layers")
        void testClearLayers() {
            assertEquals(2, network.getLayerCount(), "Should have two layers initially");
            
            network.clearLayers();
            
            assertEquals(0, network.getLayerCount(), "Should have no layers after clearing");
            assertFalse(network.isActive(), "Should not be active after clearing");
        }
    }
    
    @Nested
    @DisplayName("Parameter Tests")
    class ParameterTests {
        
        @Test
        @DisplayName("Should set valid learning rate")
        void testSetValidLearningRate() {
            network.setLearningRate(0.01);
            
            assertEquals(0.01, network.getLearningRate(), "Should set learning rate correctly");
        }
        
        @Test
        @DisplayName("Should not set invalid learning rate")
        void testSetInvalidLearningRate() {
            double originalRate = network.getLearningRate();
            
            network.setLearningRate(-0.1); // Too low
            assertEquals(originalRate, network.getLearningRate(), "Should not change with invalid rate");
            
            network.setLearningRate(2.0); // Too high
            assertEquals(originalRate, network.getLearningRate(), "Should not change with invalid rate");
        }
        
        @Test
        @DisplayName("Should set valid batch size")
        void testSetValidBatchSize() {
            network.setBatchSize(64);
            
            assertEquals(64, network.getBatchSize(), "Should set batch size correctly");
        }
        
        @Test
        @DisplayName("Should not set invalid batch size")
        void testSetInvalidBatchSize() {
            int originalSize = network.getBatchSize();
            
            network.setBatchSize(0); // Too low
            assertEquals(originalSize, network.getBatchSize(), "Should not change with invalid size");
            
            network.setBatchSize(2000); // Too high
            assertEquals(originalSize, network.getBatchSize(), "Should not change with invalid size");
        }
    }
    
    @Nested
    @DisplayName("Complex Architecture Tests")
    class ComplexArchitectureTests {
        
        @Test
        @DisplayName("Should handle complex RNN architecture")
        void testComplexRNNArchitecture() {
            // Создаем сложную архитектуру
            network.addLSTMLayer(64);
            network.addGRULayer(32);
            network.addLSTMLayer(16);
            network.addDenseLayer(8, new ActivationFunction.ReLU());
            network.addDenseLayer(3, new ActivationFunction.Softmax());
            network.activate();
            
            assertEquals(5, network.getLayerCount(), "Should have 5 layers");
            assertTrue(network.isActive(), "Should be active");
            
            // Тестируем forward pass
            double[][] inputSequence = createTestSequence(20, 10);
            double[][] result = network.forward(inputSequence);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(inputSequence.length, result.length, "Should have same sequence length");
            assertEquals(3, result[0].length, "Should have correct output size");
        }
        
        @Test
        @DisplayName("Should handle multiple forward propagations")
        void testMultipleForwardPropagations() {
            network.addLSTMLayer(32);
            network.addDenseLayer(5, new ActivationFunction.Sigmoid());
            network.activate();
            
            for (int i = 0; i < 5; i++) {
                double[][] inputSequence = createTestSequence(10, 8);
                double[][] result = network.forward(inputSequence);
                
                assertNotNull(result, "Result should not be null for iteration " + i);
                assertEquals(inputSequence.length, result.length, "Should have same sequence length for iteration " + i);
                assertEquals(5, result[0].length, "Should have correct output size for iteration " + i);
            }
        }
    }
    
    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {
        
        @Test
        @DisplayName("Should provide correct statistics")
        void testStatistics() {
            network.addLSTMLayer(32);
            network.addGRULayer(16);
            network.addDenseLayer(10, new ActivationFunction.ReLU());
            network.activate();
            
            String stats = network.getStatistics();
            
            assertNotNull(stats, "Statistics should not be null");
            assertTrue(stats.contains("Bot ID: " + TEST_BOT_ID), "Should contain bot ID");
            assertTrue(stats.contains("Layers: 3"), "Should contain layer count");
            assertTrue(stats.contains("Active: true"), "Should contain active status");
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает тестовую последовательность
     * 
     * @param sequenceLength длина последовательности
     * @param inputSize размер входа
     * @return тестовая последовательность
     */
    private double[][] createTestSequence(int sequenceLength, int inputSize) {
        double[][] sequence = new double[sequenceLength][inputSize];
        
        for (int t = 0; t < sequenceLength; t++) {
            for (int i = 0; i < inputSize; i++) {
                sequence[t][i] = Math.random() * 2 - 1; // Случайные значения от -1 до 1
            }
        }
        
        return sequence;
    }
    
    /**
     * Создает набор тестовых последовательностей
     * 
     * @param sequenceCount количество последовательностей
     * @param sequenceLength длина каждой последовательности
     * @param inputSize размер входа
     * @return набор тестовых последовательностей
     */
    private double[][][] createTestSequences(int sequenceCount, int sequenceLength, int inputSize) {
        double[][][] sequences = new double[sequenceCount][sequenceLength][inputSize];
        
        for (int s = 0; s < sequenceCount; s++) {
            sequences[s] = createTestSequence(sequenceLength, inputSize);
        }
        
        return sequences;
    }
}
