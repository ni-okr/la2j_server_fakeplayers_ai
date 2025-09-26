package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для DeepNeuralNetwork
 * 
 * Проверяет корректность работы глубокой нейронной сети,
 * включая создание, обучение и предсказания.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
@DisplayName("DeepNeuralNetwork Tests")
class DeepNeuralNetworkTest {
    
    private DeepNeuralNetwork network;
    private final int botId = 1;
    
    @BeforeEach
    void setUp() {
        network = new DeepNeuralNetwork(botId);
    }
    
    @Test
    @DisplayName("Should create DeepNeuralNetwork successfully")
    void testCreateNetwork() {
        assertNotNull(network, "Network should be created");
        assertFalse(network.isActive(), "Network should be inactive initially");
        assertEquals(0, network.getLayerCount(), "Should have no layers initially");
    }
    
    @Test
    @DisplayName("Should activate and deactivate network")
    void testActivateDeactivateNetwork() {
        assertFalse(network.isActive(), "Network should be inactive initially");
        
        network.activate();
        assertTrue(network.isActive(), "Network should be active after activation");
        
        network.deactivate();
        assertFalse(network.isActive(), "Network should be inactive after deactivation");
    }
    
    @Test
    @DisplayName("Should add dense layers")
    void testAddDenseLayers() {
        // Добавляем первый слой
        boolean result1 = network.addDenseLayer(10, new ActivationFunction.ReLU());
        assertTrue(result1, "Should add first dense layer");
        assertEquals(1, network.getLayerCount(), "Should have 1 layer");
        
        // Добавляем второй слой
        boolean result2 = network.addDenseLayer(5, new ActivationFunction.Sigmoid());
        assertTrue(result2, "Should add second dense layer");
        assertEquals(2, network.getLayerCount(), "Should have 2 layers");
    }
    
    @Test
    @DisplayName("Should add dropout layers")
    void testAddDropoutLayers() {
        // Добавляем dropout слой
        boolean result = network.addDropoutLayer(0.5);
        assertTrue(result, "Should add dropout layer");
        assertEquals(1, network.getLayerCount(), "Should have 1 layer");
        
        // Тест с неверным коэффициентом dropout
        boolean invalidResult = network.addDropoutLayer(1.5);
        assertFalse(invalidResult, "Should not add dropout layer with invalid rate");
    }
    
    @Test
    @DisplayName("Should add batch normalization layers")
    void testAddBatchNormalizationLayers() {
        // Добавляем batch normalization слой
        boolean result = network.addBatchNormalizationLayer();
        assertTrue(result, "Should add batch normalization layer");
        assertEquals(1, network.getLayerCount(), "Should have 1 layer");
    }
    
    @Test
    @DisplayName("Should handle invalid dense layer parameters")
    void testInvalidDenseLayerParameters() {
        // Тест с неверным количеством нейронов
        boolean result1 = network.addDenseLayer(0, new ActivationFunction.ReLU());
        assertFalse(result1, "Should not add dense layer with 0 neurons");
        
        boolean result2 = network.addDenseLayer(10001, new ActivationFunction.ReLU());
        assertFalse(result2, "Should not add dense layer with too many neurons");
    }
    
    @Test
    @DisplayName("Should remove last layer")
    void testRemoveLastLayer() {
        // Добавляем несколько слоев
        network.addDenseLayer(10, new ActivationFunction.ReLU());
        network.addDenseLayer(5, new ActivationFunction.Sigmoid());
        assertEquals(2, network.getLayerCount(), "Should have 2 layers");
        
        // Удаляем последний слой
        boolean result = network.removeLastLayer();
        assertTrue(result, "Should remove last layer");
        assertEquals(1, network.getLayerCount(), "Should have 1 layer");
    }
    
    @Test
    @DisplayName("Should handle removing layer from empty network")
    void testRemoveLayerFromEmptyNetwork() {
        boolean result = network.removeLastLayer();
        assertFalse(result, "Should not remove layer from empty network");
    }
    
    @Test
    @DisplayName("Should clear all layers")
    void testClearLayers() {
        // Добавляем несколько слоев
        network.addDenseLayer(10, new ActivationFunction.ReLU());
        network.addDenseLayer(5, new ActivationFunction.Sigmoid());
        assertEquals(2, network.getLayerCount(), "Should have 2 layers");
        
        // Очищаем все слои
        network.clearLayers();
        assertEquals(0, network.getLayerCount(), "Should have 0 layers");
    }
    
    @Test
    @DisplayName("Should handle forward propagation with empty network")
    void testForwardPropagationWithEmptyNetwork() {
        network.activate();
        double[] inputs = {1.0, 2.0, 3.0};
        double[] result = network.forward(inputs);
        assertNull(result, "Should return null for empty network");
    }
    
    @Test
    @DisplayName("Should handle forward propagation with inactive network")
    void testForwardPropagationWithInactiveNetwork() {
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        double[] inputs = {1.0, 2.0, 3.0};
        double[] result = network.forward(inputs);
        assertNull(result, "Should return null for inactive network");
    }
    
    @Test
    @DisplayName("Should perform forward propagation with valid network")
    void testForwardPropagationWithValidNetwork() {
        network.activate();
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        
        double[] inputs = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        double[] result = network.forward(inputs);
        
        assertNotNull(result, "Should return valid result");
        assertEquals(5, result.length, "Should return correct output size");
        
        // Проверяем, что все выходы неотрицательны (ReLU)
        for (double output : result) {
            assertTrue(output >= 0.0, "Output should be non-negative for ReLU");
        }
    }
    
    @Test
    @DisplayName("Should handle training with null data")
    void testTrainWithNullData() {
        network.activate();
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        
        boolean result = network.train(null);
        assertFalse(result, "Training should fail with null data");
    }
    
    @Test
    @DisplayName("Should handle training with inactive network")
    void testTrainWithInactiveNetwork() {
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        ProcessedData data = createTestData();
        
        boolean result = network.train(data);
        assertFalse(result, "Training should fail with inactive network");
    }
    
    @Test
    @DisplayName("Should handle training with empty network")
    void testTrainWithEmptyNetwork() {
        network.activate();
        ProcessedData data = createTestData();
        
        boolean result = network.train(data);
        assertFalse(result, "Training should fail with empty network");
    }
    
    @Test
    @DisplayName("Should perform training with valid network")
    void testTrainWithValidNetwork() {
        network.activate();
        network.addDenseLayer(10, new ActivationFunction.ReLU());
        network.addDenseLayer(5, new ActivationFunction.Sigmoid());
        
        ProcessedData data = createTestData();
        boolean result = network.train(data);
        
        assertTrue(result, "Training should succeed with valid network");
        assertTrue(network.getTrainingEpochs() > 0, "Training epochs should increase");
        assertTrue(network.getLastTrainingTime() > 0, "Last training time should be set");
    }
    
    @Test
    @DisplayName("Should handle adaptation with null data")
    void testAdaptWithNullData() {
        network.activate();
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        
        boolean result = network.adapt(null);
        assertFalse(result, "Adaptation should fail with null data");
    }
    
    @Test
    @DisplayName("Should handle adaptation with inactive network")
    void testAdaptWithInactiveNetwork() {
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        ProcessedData data = createTestData();
        
        boolean result = network.adapt(data);
        assertFalse(result, "Adaptation should fail with inactive network");
    }
    
    @Test
    @DisplayName("Should perform adaptation with valid network")
    void testAdaptWithValidNetwork() {
        network.activate();
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        
        ProcessedData data = createTestData();
        boolean result = network.adapt(data);
        
        assertTrue(result, "Adaptation should succeed with valid network");
    }
    
    @Test
    @DisplayName("Should set and get learning rate")
    void testSetGetLearningRate() {
        double newRate = 0.01;
        network.setLearningRate(newRate);
        assertEquals(newRate, network.getLearningRate(), "Should set learning rate");
        
        // Тест граничных значений
        network.setLearningRate(2.0);
        assertEquals(1.0, network.getLearningRate(), "Should clamp to maximum");
        
        network.setLearningRate(-1.0);
        assertEquals(0.0001, network.getLearningRate(), "Should clamp to minimum");
    }
    
    @Test
    @DisplayName("Should set and get optimizer")
    void testSetGetOptimizer() {
        AdvancedOptimizer newOptimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.ADAM, 0.001);
        network.setOptimizer(newOptimizer);
        
        assertEquals(newOptimizer, network.getOptimizer(), "Should set optimizer");
        assertEquals(AdvancedOptimizer.OptimizerType.ADAM, network.getOptimizer().getType(), "Should have correct optimizer type");
    }
    
    @Test
    @DisplayName("Should get current error")
    void testGetCurrentError() {
        double error = network.getCurrentError();
        assertTrue(error >= 0.0, "Current error should be non-negative");
    }
    
    @Test
    @DisplayName("Should get training epochs")
    void testGetTrainingEpochs() {
        long epochs = network.getTrainingEpochs();
        assertTrue(epochs >= 0, "Training epochs should be non-negative");
    }
    
    @Test
    @DisplayName("Should get last training time")
    void testGetLastTrainingTime() {
        long time = network.getLastTrainingTime();
        assertTrue(time >= 0, "Last training time should be non-negative");
    }
    
    @Test
    @DisplayName("Should get error history")
    void testGetErrorHistory() {
        List<Double> history = network.getErrorHistory();
        assertNotNull(history, "Error history should not be null");
        assertTrue(history.isEmpty(), "Error history should be empty initially");
    }
    
    @Test
    @DisplayName("Should get architecture")
    void testGetArchitecture() {
        String architecture = network.getArchitecture();
        assertNotNull(architecture, "Architecture should not be null");
        assertTrue(architecture.contains("DeepNeuralNetwork"), "Architecture should contain network type");
    }
    
    @Test
    @DisplayName("Should get statistics")
    void testGetStatistics() {
        String stats = network.getStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("DeepNeuralNetwork Statistics"), "Statistics should contain header");
        assertTrue(stats.contains("Bot ID: " + botId), "Statistics should contain bot ID");
    }
    
    @Test
    @DisplayName("Should handle multiple forward propagations")
    void testMultipleForwardPropagations() {
        network.activate();
        network.addDenseLayer(5, new ActivationFunction.ReLU());
        
        double[] inputs = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        
        // Выполняем несколько прямых распространений
        for (int i = 0; i < 10; i++) {
            double[] result = network.forward(inputs);
            assertNotNull(result, "Result should not be null for iteration " + i);
            assertEquals(5, result.length, "Result should have correct size for iteration " + i);
        }
    }
    
    @Test
    @DisplayName("Should handle multiple training sessions")
    void testMultipleTrainingSessions() {
        network.activate();
        network.addDenseLayer(10, new ActivationFunction.ReLU());
        network.addDenseLayer(5, new ActivationFunction.Sigmoid());
        
        ProcessedData data = createTestData();
        
        // Выполняем несколько сессий обучения
        for (int i = 0; i < 3; i++) {
            boolean result = network.train(data);
            assertTrue(result, "Training should succeed for session " + i);
        }
        
        assertTrue(network.getTrainingEpochs() > 0, "Should have trained for some epochs");
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает тестовые данные для обучения
     * 
     * @return тестовые данные
     */
    private ProcessedData createTestData() {
        // Создаем простые тестовые данные
        double[][] inputs = {
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {0.1, 0.2, 0.3, 0.4, 0.5},
            {0.5, 0.5, 0.5, 0.5, 0.5}
        };
        
        double[][] targets = {
            {1.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 1.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 1.0, 0.0, 0.0}
        };
        
        return new ProcessedData(inputs, targets);
    }
}
