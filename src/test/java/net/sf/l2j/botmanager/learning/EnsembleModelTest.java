package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для EnsembleModel
 * 
 * @author ni-okr
 * @version 3.4
 */
@DisplayName("EnsembleModel Tests")
class EnsembleModelTest {
    
    private VotingEnsemble ensemble;
    private static final int TEST_BOT_ID = 1;
    
    @BeforeEach
    void setUp() {
        ensemble = new VotingEnsemble(TEST_BOT_ID, VotingEnsemble.VotingType.SOFT);
    }
    
    @Nested
    @DisplayName("Ensemble Creation Tests")
    class EnsembleCreationTests {
        
        @Test
        @DisplayName("Should create ensemble with correct parameters")
        void testEnsembleCreation() {
            assertNotNull(ensemble, "Ensemble should not be null");
            assertEquals(TEST_BOT_ID, ensemble.getBotId(), "Should have correct bot ID");
            assertEquals(EnsembleModel.EnsembleType.VOTING, ensemble.getType(), "Should have correct type");
            assertFalse(ensemble.isActive(), "Should not be active initially");
            assertEquals(0, ensemble.getModelCount(), "Should have no models initially");
        }
        
        @Test
        @DisplayName("Should create different ensemble types")
        void testDifferentEnsembleTypes() {
            VotingEnsemble votingEnsemble = new VotingEnsemble(1, VotingEnsemble.VotingType.HARD);
            StackingEnsemble stackingEnsemble = new StackingEnsemble(2, null);
            BaggingEnsemble baggingEnsemble = new BaggingEnsemble(3, 0.8);
            
            assertEquals(EnsembleModel.EnsembleType.VOTING, votingEnsemble.getType(), "Should have VOTING type");
            assertEquals(EnsembleModel.EnsembleType.STACKING, stackingEnsemble.getType(), "Should have STACKING type");
            assertEquals(EnsembleModel.EnsembleType.BAGGING, baggingEnsemble.getType(), "Should have BAGGING type");
        }
    }
    
    @Nested
    @DisplayName("Model Management Tests")
    class ModelManagementTests {
        
        @Test
        @DisplayName("Should add models successfully")
        void testAddModels() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            boolean result1 = ensemble.addModel(model1);
            boolean result2 = ensemble.addModel(model2);
            
            assertTrue(result1, "Should add first model successfully");
            assertTrue(result2, "Should add second model successfully");
            assertEquals(2, ensemble.getModelCount(), "Should have two models");
        }
        
        @Test
        @DisplayName("Should not add null model")
        void testAddNullModel() {
            boolean result = ensemble.addModel(null);
            
            assertFalse(result, "Should not add null model");
            assertEquals(0, ensemble.getModelCount(), "Should still have no models");
        }
        
        @Test
        @DisplayName("Should not add models to active ensemble")
        void testAddModelToActiveEnsemble() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            DeepNeuralNetwork model3 = new DeepNeuralNetwork(3);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            boolean activated = ensemble.activate();
            assertTrue(activated, "Ensemble should be activated");
            
            boolean result = ensemble.addModel(model3);
            
            assertFalse(result, "Should not add model to active ensemble");
            assertEquals(2, ensemble.getModelCount(), "Should still have two models");
        }
        
        @Test
        @DisplayName("Should remove models successfully")
        void testRemoveModels() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            
            boolean result = ensemble.removeModel(0);
            
            assertTrue(result, "Should remove model successfully");
            assertEquals(1, ensemble.getModelCount(), "Should have one model left");
        }
        
        @Test
        @DisplayName("Should not remove models from active ensemble")
        void testRemoveModelFromActiveEnsemble() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
            
            boolean result = ensemble.removeModel(0);
            
            assertFalse(result, "Should not remove model from active ensemble");
            assertEquals(2, ensemble.getModelCount(), "Should still have two models");
        }
        
        @Test
        @DisplayName("Should not remove model with invalid index")
        void testRemoveModelWithInvalidIndex() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            ensemble.addModel(model1);
            
            boolean result = ensemble.removeModel(5);
            
            assertFalse(result, "Should not remove model with invalid index");
            assertEquals(1, ensemble.getModelCount(), "Should still have one model");
        }
        
        @Test
        @DisplayName("Should clear all models")
        void testClearModels() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            
            ensemble.clearModels();
            
            assertEquals(0, ensemble.getModelCount(), "Should have no models after clearing");
            assertFalse(ensemble.isActive(), "Should not be active after clearing");
        }
    }
    
    @Nested
    @DisplayName("Ensemble Activation Tests")
    class EnsembleActivationTests {
        
        @Test
        @DisplayName("Should activate ensemble with sufficient models")
        void testActivateEnsembleWithSufficientModels() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            
            boolean result = ensemble.activate();
            
            assertTrue(result, "Should activate ensemble successfully");
            assertTrue(ensemble.isActive(), "Should be active");
        }
        
        @Test
        @DisplayName("Should not activate ensemble with insufficient models")
        void testActivateEnsembleWithInsufficientModels() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            
            ensemble.addModel(model1);
            
            boolean result = ensemble.activate();
            
            assertFalse(result, "Should not activate ensemble with insufficient models");
            assertFalse(ensemble.isActive(), "Should not be active");
        }
        
        @Test
        @DisplayName("Should deactivate ensemble")
        void testDeactivateEnsemble() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
            
            ensemble.deactivate();
            
            assertFalse(ensemble.isActive(), "Should not be active after deactivation");
        }
    }
    
    @Nested
    @DisplayName("Weight Management Tests")
    class WeightManagementTests {
        
        @BeforeEach
        void setUp() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            DeepNeuralNetwork model3 = new DeepNeuralNetwork(3);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.addModel(model3);
            ensemble.activate();
        }
        
        @Test
        @DisplayName("Should set valid model weights")
        void testSetValidModelWeights() {
            double[] weights = {0.5, 0.3, 0.2};
            
            boolean result = ensemble.setModelWeights(weights);
            
            assertTrue(result, "Should set valid weights successfully");
            assertArrayEquals(weights, ensemble.getModelWeights(), "Should have correct weights");
        }
        
        @Test
        @DisplayName("Should not set null weights")
        void testSetNullWeights() {
            boolean result = ensemble.setModelWeights(null);
            
            assertFalse(result, "Should not set null weights");
        }
        
        @Test
        @DisplayName("Should not set weights with wrong count")
        void testSetWeightsWithWrongCount() {
            double[] weights = {0.5, 0.5}; // Wrong count
            
            boolean result = ensemble.setModelWeights(weights);
            
            assertFalse(result, "Should not set weights with wrong count");
        }
        
        @Test
        @DisplayName("Should normalize weights")
        void testNormalizeWeights() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            DeepNeuralNetwork model3 = new DeepNeuralNetwork(3);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.addModel(model3);
            ensemble.activate();
            
            double[] weights = {1.0, 2.0, 3.0}; // Will be normalized
            double[] expectedWeights = {1.0/6.0, 2.0/6.0, 3.0/6.0};
            
            boolean result = ensemble.setModelWeights(weights);
            
            assertTrue(result, "Should set weights successfully");
            assertArrayEquals(expectedWeights, ensemble.getModelWeights(), 0.001, "Should normalize weights");
        }
    }
    
    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {
        
        @Test
        @DisplayName("Should provide statistics")
        void testStatistics() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
            
            String stats = ensemble.getStatistics();
            
            assertNotNull(stats, "Statistics should not be null");
            assertTrue(stats.contains("Bot ID: " + TEST_BOT_ID), "Should contain bot ID");
            assertTrue(stats.contains("Type: Voting"), "Should contain ensemble type");
            assertTrue(stats.contains("Models: 2"), "Should contain model count");
            assertTrue(stats.contains("Active: true"), "Should contain active status");
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает тестовые входные данные
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
    
    /**
     * Создает тестовые данные обучения
     * 
     * @param samples количество образцов
     * @param features количество признаков
     * @param classes количество классов
     * @return тестовые данные
     */
    private double[][] createTestData(int samples, int features, int classes) {
        double[][] data = new double[samples][];
        for (int i = 0; i < samples; i++) {
            data[i] = new double[features];
            for (int j = 0; j < features; j++) {
                data[i][j] = Math.random() * 2 - 1;
            }
        }
        return data;
    }
}
