package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для AdvancedOptimizer
 * 
 * Проверяет корректность работы различных алгоритмов оптимизации,
 * включая SGD, Adam, RMSprop и другие.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
@DisplayName("AdvancedOptimizer Tests")
class AdvancedOptimizerTest {
    
    private AdvancedOptimizer optimizer;
    
    @BeforeEach
    void setUp() {
        optimizer = new AdvancedOptimizer();
    }
    
    @Test
    @DisplayName("Should create AdvancedOptimizer with default parameters")
    void testCreateOptimizerWithDefaults() {
        assertNotNull(optimizer, "Optimizer should be created");
        assertEquals(AdvancedOptimizer.OptimizerType.ADAM, optimizer.getType(), "Should have default Adam type");
        assertEquals(0.001, optimizer.getLearningRate(), "Should have default learning rate");
        assertEquals(0, optimizer.getIteration(), "Should start with 0 iterations");
    }
    
    @Test
    @DisplayName("Should create AdvancedOptimizer with custom parameters")
    void testCreateOptimizerWithCustomParameters() {
        AdvancedOptimizer customOptimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.SGD, 0.01);
        
        assertNotNull(customOptimizer, "Custom optimizer should be created");
        assertEquals(AdvancedOptimizer.OptimizerType.SGD, customOptimizer.getType(), "Should have SGD type");
        assertEquals(0.01, customOptimizer.getLearningRate(), "Should have custom learning rate");
    }
    
    @Test
    @DisplayName("Should set and get learning rate")
    void testSetGetLearningRate() {
        double newRate = 0.005;
        optimizer.setLearningRate(newRate);
        assertEquals(newRate, optimizer.getLearningRate(), "Should set learning rate");
        
        // Тест граничных значений
        optimizer.setLearningRate(2.0);
        assertEquals(1.0, optimizer.getLearningRate(), "Should clamp to maximum");
        
        optimizer.setLearningRate(-1.0);
        assertEquals(1e-8, optimizer.getLearningRate(), "Should clamp to minimum");
    }
    
    @Test
    @DisplayName("Should set and get optimizer type")
    void testSetGetOptimizerType() {
        optimizer.setType(AdvancedOptimizer.OptimizerType.RMSPROP);
        assertEquals(AdvancedOptimizer.OptimizerType.RMSPROP, optimizer.getType(), "Should set optimizer type");
        
        optimizer.setType(AdvancedOptimizer.OptimizerType.ADAM);
        assertEquals(AdvancedOptimizer.OptimizerType.ADAM, optimizer.getType(), "Should change optimizer type");
    }
    
    @Test
    @DisplayName("Should set and get parameters")
    void testSetGetParameters() {
        optimizer.setParameter("beta1", 0.9);
        optimizer.setParameter("beta2", 0.999);
        
        assertEquals(0.9, optimizer.getParameter("beta1"), "Should set beta1 parameter");
        assertEquals(0.999, optimizer.getParameter("beta2"), "Should set beta2 parameter");
        assertEquals(0.0, optimizer.getParameter("nonexistent"), "Should return 0 for nonexistent parameter");
    }
    
    @Test
    @DisplayName("Should reset optimizer state")
    void testResetOptimizer() {
        // Выполняем несколько итераций
        List<NetworkLayer> layers = createTestLayers();
        optimizer.updateWeights(layers);
        optimizer.updateWeights(layers);
        
        assertTrue(optimizer.getIteration() > 0, "Should have iterations before reset");
        
        optimizer.reset();
        assertEquals(0, optimizer.getIteration(), "Should reset iterations to 0");
    }
    
    @Test
    @DisplayName("Should update weights for different optimizer types")
    void testUpdateWeightsForDifferentTypes() {
        List<NetworkLayer> layers = createTestLayers();
        
        // Тест SGD
        optimizer.setType(AdvancedOptimizer.OptimizerType.SGD);
        optimizer.updateWeights(layers);
        assertEquals(1, optimizer.getIteration(), "Should increment iteration for SGD");
        
        // Тест Adam
        optimizer.setType(AdvancedOptimizer.OptimizerType.ADAM);
        optimizer.updateWeights(layers);
        assertEquals(2, optimizer.getIteration(), "Should increment iteration for Adam");
        
        // Тест RMSprop
        optimizer.setType(AdvancedOptimizer.OptimizerType.RMSPROP);
        optimizer.updateWeights(layers);
        assertEquals(3, optimizer.getIteration(), "Should increment iteration for RMSprop");
    }
    
    @Test
    @DisplayName("Should handle update weights with empty layers")
    void testUpdateWeightsWithEmptyLayers() {
        List<NetworkLayer> emptyLayers = new ArrayList<>();
        
        // Не должно выбрасывать исключение
        assertDoesNotThrow(() -> optimizer.updateWeights(emptyLayers), "Should handle empty layers");
        assertEquals(1, optimizer.getIteration(), "Should increment iteration even with empty layers");
    }
    
    @Test
    @DisplayName("Should handle update weights with null layers")
    void testUpdateWeightsWithNullLayers() {
        // Не должно выбрасывать исключение
        assertDoesNotThrow(() -> optimizer.updateWeights(null), "Should handle null layers");
    }
    
    @Test
    @DisplayName("Should get statistics")
    void testGetStatistics() {
        String stats = optimizer.getStatistics();
        
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.contains("AdvancedOptimizer Statistics"), "Should contain header");
        assertTrue(stats.contains("Type: " + optimizer.getType()), "Should contain optimizer type");
        assertTrue(stats.contains("Learning Rate:"), "Should contain learning rate");
        assertTrue(stats.contains("Iterations: " + optimizer.getIteration()), "Should contain iterations");
    }
    
    @Test
    @DisplayName("Should test all optimizer types")
    void testAllOptimizerTypes() {
        AdvancedOptimizer.OptimizerType[] types = AdvancedOptimizer.OptimizerType.values();
        
        for (AdvancedOptimizer.OptimizerType type : types) {
            AdvancedOptimizer testOptimizer = new AdvancedOptimizer(type, 0.001);
            
            assertNotNull(testOptimizer, "Optimizer should be created for type " + type);
            assertEquals(type, testOptimizer.getType(), "Should have correct type " + type);
            assertEquals(0.001, testOptimizer.getLearningRate(), "Should have correct learning rate for " + type);
        }
    }
    
    @Test
    @DisplayName("Should test optimizer type descriptions")
    void testOptimizerTypeDescriptions() {
        assertEquals("Stochastic Gradient Descent", AdvancedOptimizer.OptimizerType.SGD.toString());
        assertEquals("Momentum", AdvancedOptimizer.OptimizerType.MOMENTUM.toString());
        assertEquals("AdaGrad", AdvancedOptimizer.OptimizerType.ADAGRAD.toString());
        assertEquals("RMSprop", AdvancedOptimizer.OptimizerType.RMSPROP.toString());
        assertEquals("Adam", AdvancedOptimizer.OptimizerType.ADAM.toString());
        assertEquals("Adamax", AdvancedOptimizer.OptimizerType.ADAMAX.toString());
        assertEquals("Nadam", AdvancedOptimizer.OptimizerType.NADAM.toString());
    }
    
    @Test
    @DisplayName("Should test parameter initialization for different types")
    void testParameterInitialization() {
        // Тест Adam
        AdvancedOptimizer adamOptimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.ADAM, 0.001);
        assertEquals(0.9, adamOptimizer.getParameter("beta1"), "Adam should have beta1 parameter");
        assertEquals(0.999, adamOptimizer.getParameter("beta2"), "Adam should have beta2 parameter");
        
        // Тест RMSprop
        AdvancedOptimizer rmspropOptimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.RMSPROP, 0.001);
        assertEquals(0.9, rmspropOptimizer.getParameter("decay"), "RMSprop should have decay parameter");
        
        // Тест Momentum
        AdvancedOptimizer momentumOptimizer = new AdvancedOptimizer(AdvancedOptimizer.OptimizerType.MOMENTUM, 0.001);
        assertEquals(0.9, momentumOptimizer.getParameter("momentum"), "Momentum should have momentum parameter");
    }
    
    @Test
    @DisplayName("Should handle multiple iterations")
    void testMultipleIterations() {
        List<NetworkLayer> layers = createTestLayers();
        
        // Выполняем несколько итераций
        for (int i = 0; i < 10; i++) {
            optimizer.updateWeights(layers);
        }
        
        assertEquals(10, optimizer.getIteration(), "Should have 10 iterations");
    }
    
    @Test
    @DisplayName("Should handle learning rate changes during training")
    void testLearningRateChangesDuringTraining() {
        List<NetworkLayer> layers = createTestLayers();
        
        // Начальная скорость обучения
        double initialRate = 0.01;
        optimizer.setLearningRate(initialRate);
        assertEquals(initialRate, optimizer.getLearningRate(), "Should have initial learning rate");
        
        // Выполняем несколько итераций
        optimizer.updateWeights(layers);
        optimizer.updateWeights(layers);
        
        // Изменяем скорость обучения
        double newRate = 0.005;
        optimizer.setLearningRate(newRate);
        assertEquals(newRate, optimizer.getLearningRate(), "Should have new learning rate");
        
        // Продолжаем обучение
        optimizer.updateWeights(layers);
        assertEquals(3, optimizer.getIteration(), "Should continue incrementing iterations");
    }
    
    @Test
    @DisplayName("Should handle parameter changes")
    void testParameterChanges() {
        // Устанавливаем параметры
        optimizer.setParameter("beta1", 0.8);
        optimizer.setParameter("beta2", 0.9);
        
        assertEquals(0.8, optimizer.getParameter("beta1"), "Should set beta1");
        assertEquals(0.9, optimizer.getParameter("beta2"), "Should set beta2");
        
        // Изменяем параметры
        optimizer.setParameter("beta1", 0.85);
        assertEquals(0.85, optimizer.getParameter("beta1"), "Should update beta1");
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает тестовые слои для оптимизатора
     * 
     * @return список тестовых слоев
     */
    private List<NetworkLayer> createTestLayers() {
        List<NetworkLayer> layers = new ArrayList<>();
        
        // Создаем тестовый DenseLayer
        DenseLayer layer = new DenseLayer(5, new ActivationFunction.ReLU());
        layer.initialize(10, 5);
        layers.add(layer);
        
        return layers;
    }
}
