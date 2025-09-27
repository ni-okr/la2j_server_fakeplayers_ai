package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для VotingEnsemble
 * 
 * @author ni-okr
 * @version 3.4
 */
@DisplayName("VotingEnsemble Tests")
class VotingEnsembleTest {
    
    private VotingEnsemble ensemble;
    private static final int TEST_BOT_ID = 1;
    
    @BeforeEach
    void setUp() {
        ensemble = new VotingEnsemble(TEST_BOT_ID, VotingEnsemble.VotingType.SOFT, 3);
    }
    
    @Nested
    @DisplayName("Ensemble Creation Tests")
    class EnsembleCreationTests {
        
        @Test
        @DisplayName("Should create voting ensemble with correct parameters")
        void testVotingEnsembleCreation() {
            assertNotNull(ensemble, "Ensemble should not be null");
            assertEquals(TEST_BOT_ID, ensemble.getBotId(), "Should have correct bot ID");
            assertEquals(EnsembleModel.EnsembleType.VOTING, ensemble.getType(), "Should have VOTING type");
            assertEquals(VotingEnsemble.VotingType.SOFT, ensemble.getVotingType(), "Should have SOFT voting type");
            assertEquals(3, ensemble.getOutputClasses(), "Should have 3 output classes");
        }
        
        @Test
        @DisplayName("Should create ensemble with different voting types")
        void testDifferentVotingTypes() {
            VotingEnsemble hardVoting = new VotingEnsemble(1, VotingEnsemble.VotingType.HARD, 2);
            VotingEnsemble softVoting = new VotingEnsemble(2, VotingEnsemble.VotingType.SOFT, 2);
            
            assertEquals(VotingEnsemble.VotingType.HARD, hardVoting.getVotingType(), "Should have HARD voting type");
            assertEquals(VotingEnsemble.VotingType.SOFT, softVoting.getVotingType(), "Should have SOFT voting type");
        }
        
        @Test
        @DisplayName("Should create ensemble with different output classes")
        void testDifferentOutputClasses() {
            VotingEnsemble binaryEnsemble = new VotingEnsemble(1, VotingEnsemble.VotingType.SOFT, 2);
            VotingEnsemble multiClassEnsemble = new VotingEnsemble(2, VotingEnsemble.VotingType.SOFT, 10);
            
            assertEquals(2, binaryEnsemble.getOutputClasses(), "Should have 2 output classes");
            assertEquals(10, multiClassEnsemble.getOutputClasses(), "Should have 10 output classes");
        }
    }
    
    @Nested
    @DisplayName("Model Addition Tests")
    class ModelAdditionTests {
        
        @Test
        @DisplayName("Should add different types of models")
        void testAddDifferentModelTypes() {
            DeepNeuralNetwork dnn = new DeepNeuralNetwork(1);
            ConvolutionalNeuralNetwork cnn = new ConvolutionalNeuralNetwork(2);
            RecurrentNeuralNetwork rnn = new RecurrentNeuralNetwork(3);
            
            boolean result1 = ensemble.addModel(dnn);
            boolean result2 = ensemble.addModel(cnn);
            boolean result3 = ensemble.addModel(rnn);
            
            assertTrue(result1, "Should add DNN successfully");
            assertTrue(result2, "Should add CNN successfully");
            assertTrue(result3, "Should add RNN successfully");
            assertEquals(3, ensemble.getModelCount(), "Should have 3 models");
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
            assertEquals(2, ensemble.getModelCount(), "Should still have 2 models");
        }
    }
    
    @Nested
    @DisplayName("Prediction Tests")
    class PredictionTests {
        
        @BeforeEach
        void setUp() {
            // Добавляем модели и активируем ансамбль
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
            
            // Обучаем модели простыми данными
            double[][] inputs = {
                {1.0, 2.0, 3.0, 4.0, 5.0},
                {2.0, 3.0, 4.0, 5.0, 6.0},
                {3.0, 4.0, 5.0, 6.0, 7.0}
            };
            double[][] targets = {
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0}
            };
            
            ensemble.train(inputs, targets, 1);
        }
        
        @Test
        @DisplayName("Should predict with valid input")
        void testPredictWithValidInput() {
            double[] input = createTestInput(5);
            
            double[] result = ensemble.predict(input);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(3, result.length, "Should have correct output size");
            
            // Проверяем, что сумма вероятностей близка к 1
            double sum = 0;
            for (double value : result) {
                sum += value;
                assertTrue(value >= 0 && value <= 1, "Values should be in range [0,1]");
            }
            assertEquals(1.0, sum, 0.1, "Sum should be close to 1");
        }
        
        @Test
        @DisplayName("Should not predict with inactive ensemble")
        void testPredictWithInactiveEnsemble() {
            ensemble.deactivate();
            double[] input = createTestInput(5);
            
            double[] result = ensemble.predict(input);
            
            assertNull(result, "Result should be null for inactive ensemble");
        }
        
        @Test
        @DisplayName("Should not predict with null input")
        void testPredictWithNullInput() {
            double[] result = ensemble.predict(null);
            
            assertNull(result, "Result should be null for null input");
        }
    }
    
    @Nested
    @DisplayName("Training Tests")
    class TrainingTests {
        
        @BeforeEach
        void setUp() {
            // Добавляем модели и активируем ансамбль
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
        }
        
        @Test
        @DisplayName("Should train with valid data")
        void testTrainWithValidData() {
            double[][] inputs = createTestData(10, 5, 3);
            double[][] targets = createTestData(10, 3, 3);
            
            boolean result = ensemble.train(inputs, targets, 10);
            
            assertTrue(result, "Training should succeed");
        }
        
        @Test
        @DisplayName("Should not train with inactive ensemble")
        void testTrainWithInactiveEnsemble() {
            ensemble.deactivate();
            double[][] inputs = createTestData(10, 5, 3);
            double[][] targets = createTestData(10, 3, 3);
            
            boolean result = ensemble.train(inputs, targets, 10);
            
            assertFalse(result, "Training should fail with inactive ensemble");
        }
        
        @Test
        @DisplayName("Should not train with null data")
        void testTrainWithNullData() {
            boolean result = ensemble.train(null, null, 10);
            
            assertFalse(result, "Training should fail with null data");
        }
        
        @Test
        @DisplayName("Should not train with mismatched data")
        void testTrainWithMismatchedData() {
            double[][] inputs = createTestData(10, 5, 3);
            double[][] targets = createTestData(5, 3, 3); // Different count
            
            boolean result = ensemble.train(inputs, targets, 10);
            
            assertFalse(result, "Training should fail with mismatched data");
        }
    }
    
    @Nested
    @DisplayName("Voting Type Tests")
    class VotingTypeTests {
        
        @BeforeEach
        void setUp() {
            // Добавляем модели и активируем ансамбль
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
        }
        
        @Test
        @DisplayName("Should use soft voting for predictions")
        void testSoftVoting() {
            VotingEnsemble softEnsemble = new VotingEnsemble(1, VotingEnsemble.VotingType.SOFT, 3);
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            softEnsemble.addModel(model1);
            softEnsemble.addModel(model2);
            softEnsemble.activate();
            
            double[] input = createTestInput(5);
            double[] result = softEnsemble.predict(input);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(3, result.length, "Should have correct output size");
        }
        
        @Test
        @DisplayName("Should use hard voting for predictions")
        void testHardVoting() {
            VotingEnsemble hardEnsemble = new VotingEnsemble(1, VotingEnsemble.VotingType.HARD, 3);
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            hardEnsemble.addModel(model1);
            hardEnsemble.addModel(model2);
            hardEnsemble.activate();
            
            double[] input = createTestInput(5);
            double[] result = hardEnsemble.predict(input);
            
            assertNotNull(result, "Result should not be null");
            assertEquals(3, result.length, "Should have correct output size");
        }
    }
    
    @Nested
    @DisplayName("Output Classes Tests")
    class OutputClassesTests {
        
        @Test
        @DisplayName("Should set valid output classes")
        void testSetValidOutputClasses() {
            ensemble.setOutputClasses(5);
            
            assertEquals(5, ensemble.getOutputClasses(), "Should have 5 output classes");
        }
        
        @Test
        @DisplayName("Should not set invalid output classes")
        void testSetInvalidOutputClasses() {
            int originalClasses = ensemble.getOutputClasses();
            
            ensemble.setOutputClasses(0); // Too low
            assertEquals(originalClasses, ensemble.getOutputClasses(), "Should not change with invalid classes");
            
            ensemble.setOutputClasses(2000); // Too high
            assertEquals(originalClasses, ensemble.getOutputClasses(), "Should not change with invalid classes");
        }
    }
    
    @Nested
    @DisplayName("Complex Ensemble Tests")
    class ComplexEnsembleTests {
        
        @Test
        @DisplayName("Should handle complex ensemble with multiple model types")
        void testComplexEnsemble() {
            // Создаем сложный ансамбль с разными типами моделей
            VotingEnsemble complexEnsemble = new VotingEnsemble(1, VotingEnsemble.VotingType.SOFT, 5);
            
            DeepNeuralNetwork dnn1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork dnn2 = new DeepNeuralNetwork(2);
            ConvolutionalNeuralNetwork cnn = new ConvolutionalNeuralNetwork(3);
            RecurrentNeuralNetwork rnn = new RecurrentNeuralNetwork(4);
            
            complexEnsemble.addModel(dnn1);
            complexEnsemble.addModel(dnn2);
            complexEnsemble.addModel(cnn);
            complexEnsemble.addModel(rnn);
            
            boolean activated = complexEnsemble.activate();
            assertTrue(activated, "Should activate complex ensemble");
            
            assertEquals(4, complexEnsemble.getModelCount(), "Should have 4 models");
            assertTrue(complexEnsemble.isActive(), "Should be active");
        }
        
        @Test
        @DisplayName("Should handle multiple predictions")
        void testMultiplePredictions() {
            DeepNeuralNetwork model1 = new DeepNeuralNetwork(1);
            DeepNeuralNetwork model2 = new DeepNeuralNetwork(2);
            
            ensemble.addModel(model1);
            ensemble.addModel(model2);
            ensemble.activate();
            
            for (int i = 0; i < 5; i++) {
                double[] input = createTestInput(5);
                double[] result = ensemble.predict(input);
                
                assertNotNull(result, "Result should not be null for iteration " + i);
                assertEquals(3, result.length, "Should have correct output size for iteration " + i);
            }
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
