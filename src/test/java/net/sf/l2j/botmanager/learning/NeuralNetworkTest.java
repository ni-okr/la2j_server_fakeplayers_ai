package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для NeuralNetwork
 * 
 * Проверяет корректность работы нейронной сети,
 * включая создание, обучение и предсказания.
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
@DisplayName("NeuralNetwork Tests")
class NeuralNetworkTest {
    
    private NeuralNetwork network;
    private final int botId = 1;
    
    @BeforeEach
    void setUp() {
        network = new NeuralNetwork(botId);
    }
    
    @Test
    @DisplayName("Should create NeuralNetwork with default architecture")
    void testCreateNetworkWithDefaultArchitecture() {
        assertNotNull(network, "Network should be created");
        assertEquals("10-2x8-5", network.getArchitecture(), "Should have default architecture");
        assertEquals(0.1, network.getLearningRate(), "Should have default learning rate");
        assertFalse(network.isActive(), "Network should be inactive initially");
    }
    
    @Test
    @DisplayName("Should create NeuralNetwork with custom architecture")
    void testCreateNetworkWithCustomArchitecture() {
        NeuralNetwork customNetwork = new NeuralNetwork(2, 5, 1, 3, 2);
        
        assertNotNull(customNetwork, "Custom network should be created");
        assertEquals("5-1x3-2", customNetwork.getArchitecture(), "Should have custom architecture");
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
    @DisplayName("Should set and get learning rate")
    void testSetGetLearningRate() {
        double newRate = 0.5;
        network.setLearningRate(newRate);
        assertEquals(newRate, network.getLearningRate(), "Should set learning rate");
        
        // Test clamping
        network.setLearningRate(2.0);
        assertEquals(1.0, network.getLearningRate(), "Should clamp to maximum");
        
        network.setLearningRate(-1.0);
        assertEquals(0.001, network.getLearningRate(), "Should clamp to minimum");
    }
    
    @Test
    @DisplayName("Should handle forward propagation with null inputs")
    void testForwardPropagationWithNullInputs() {
        network.activate();
        double[] result = network.forward(null);
        assertNull(result, "Should return null for null inputs");
    }
    
    @Test
    @DisplayName("Should handle forward propagation with wrong input size")
    void testForwardPropagationWithWrongInputSize() {
        network.activate();
        double[] inputs = {1.0, 2.0}; // Wrong size
        double[] result = network.forward(inputs);
        assertNull(result, "Should return null for wrong input size");
    }
    
    @Test
    @DisplayName("Should handle forward propagation with inactive network")
    void testForwardPropagationWithInactiveNetwork() {
        double[] inputs = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        double[] result = network.forward(inputs);
        assertNull(result, "Should return null for inactive network");
    }
    
    @Test
    @DisplayName("Should perform forward propagation with valid inputs")
    void testForwardPropagationWithValidInputs() {
        network.activate();
        double[] inputs = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        double[] result = network.forward(inputs);
        
        assertNotNull(result, "Should return valid result");
        assertEquals(5, result.length, "Should return correct output size");
        
        // Check that all outputs are in valid range [0, 1] (sigmoid output)
        for (double output : result) {
            assertTrue(output >= 0.0 && output <= 1.0, "Output should be in range [0, 1]");
        }
    }
    
    @Test
    @DisplayName("Should handle training with null data")
    void testTrainWithNullData() {
        network.activate();
        boolean result = network.train(null);
        assertFalse(result, "Training should fail with null data");
    }
    
    @Test
    @DisplayName("Should handle training with inactive network")
    void testTrainWithInactiveNetwork() {
        ProcessedData data = createTestData();
        boolean result = network.train(data);
        assertFalse(result, "Training should fail with inactive network");
    }
    
    @Test
    @DisplayName("Should handle training with invalid data")
    void testTrainWithInvalidData() {
        network.activate();
        
        // Create invalid data (null inputs or targets)
        ProcessedData invalidData = new ProcessedData(null, null);
        boolean result = network.train(invalidData);
        assertFalse(result, "Training should fail with invalid data");
    }
    
    @Test
    @DisplayName("Should perform training with valid data")
    void testTrainWithValidData() {
        network.activate();
        ProcessedData data = createTestData();
        
        boolean result = network.train(data);
        assertTrue(result, "Training should succeed with valid data");
        
        // Check that training epochs increased
        assertTrue(network.getTrainingEpochs() > 0, "Training epochs should increase");
        assertTrue(network.getLastTrainingTime() > 0, "Last training time should be set");
    }
    
    @Test
    @DisplayName("Should handle adaptation with null data")
    void testAdaptWithNullData() {
        network.activate();
        boolean result = network.adapt(null);
        assertFalse(result, "Adaptation should fail with null data");
    }
    
    @Test
    @DisplayName("Should handle adaptation with inactive network")
    void testAdaptWithInactiveNetwork() {
        ProcessedData data = createTestData();
        boolean result = network.adapt(data);
        assertFalse(result, "Adaptation should fail with inactive network");
    }
    
    @Test
    @DisplayName("Should perform adaptation with valid data")
    void testAdaptWithValidData() {
        network.activate();
        ProcessedData data = createTestData();
        
        boolean result = network.adapt(data);
        assertTrue(result, "Adaptation should succeed with valid data");
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
    @DisplayName("Should handle multiple forward propagations")
    void testMultipleForwardPropagations() {
        network.activate();
        double[] inputs = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        
        // Perform multiple forward propagations
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
        ProcessedData data = createTestData();
        
        // Perform multiple training sessions
        for (int i = 0; i < 5; i++) {
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
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0},
            {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0},
            {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5}
        };
        
        double[][] targets = {
            {1.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 1.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 1.0, 0.0, 0.0}
        };
        
        return new ProcessedData(inputs, targets);
    }
}
