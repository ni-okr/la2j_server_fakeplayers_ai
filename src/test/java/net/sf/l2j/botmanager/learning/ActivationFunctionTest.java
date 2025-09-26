package net.sf.l2j.botmanager.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ActivationFunction
 * 
 * Проверяет корректность работы различных функций активации,
 * включая их значения и производные.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */
@DisplayName("ActivationFunction Tests")
class ActivationFunctionTest {
    
    @Test
    @DisplayName("Should test Sigmoid activation function")
    void testSigmoidActivation() {
        ActivationFunction.Sigmoid sigmoid = new ActivationFunction.Sigmoid();
        
        // Тест активации
        assertEquals(0.5, sigmoid.activate(0.0), 1e-6, "Sigmoid(0) should be 0.5");
        assertTrue(sigmoid.activate(10.0) > 0.9, "Sigmoid(10) should be close to 1");
        assertTrue(sigmoid.activate(-10.0) < 0.1, "Sigmoid(-10) should be close to 0");
        
        // Тест производной
        double x = 0.0;
        double activation = sigmoid.activate(x);
        double derivative = sigmoid.derivative(x);
        assertEquals(activation * (1.0 - activation), derivative, 1e-6, "Sigmoid derivative should be correct");
        
        // Проверяем название
        assertEquals("Sigmoid", sigmoid.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test Tanh activation function")
    void testTanhActivation() {
        ActivationFunction.Tanh tanh = new ActivationFunction.Tanh();
        
        // Тест активации
        assertEquals(0.0, tanh.activate(0.0), 1e-6, "Tanh(0) should be 0");
        assertTrue(tanh.activate(10.0) > 0.9, "Tanh(10) should be close to 1");
        assertTrue(tanh.activate(-10.0) < -0.9, "Tanh(-10) should be close to -1");
        
        // Тест производной
        double x = 0.0;
        double activation = tanh.activate(x);
        double derivative = tanh.derivative(x);
        assertEquals(1.0 - activation * activation, derivative, 1e-6, "Tanh derivative should be correct");
        
        // Проверяем название
        assertEquals("Tanh", tanh.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test ReLU activation function")
    void testReLUActivation() {
        ActivationFunction.ReLU relu = new ActivationFunction.ReLU();
        
        // Тест активации
        assertEquals(0.0, relu.activate(-5.0), 1e-6, "ReLU(-5) should be 0");
        assertEquals(0.0, relu.activate(0.0), 1e-6, "ReLU(0) should be 0");
        assertEquals(5.0, relu.activate(5.0), 1e-6, "ReLU(5) should be 5");
        
        // Тест производной
        assertEquals(0.0, relu.derivative(-5.0), 1e-6, "ReLU derivative for negative should be 0");
        assertEquals(1.0, relu.derivative(5.0), 1e-6, "ReLU derivative for positive should be 1");
        
        // Проверяем название
        assertEquals("ReLU", relu.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test LeakyReLU activation function")
    void testLeakyReLUActivation() {
        ActivationFunction.LeakyReLU leakyRelu = new ActivationFunction.LeakyReLU(0.1);
        
        // Тест активации
        assertEquals(-0.5, leakyRelu.activate(-5.0), 1e-6, "LeakyReLU(-5) should be -0.5");
        assertEquals(0.0, leakyRelu.activate(0.0), 1e-6, "LeakyReLU(0) should be 0");
        assertEquals(5.0, leakyRelu.activate(5.0), 1e-6, "LeakyReLU(5) should be 5");
        
        // Тест производной
        assertEquals(0.1, leakyRelu.derivative(-5.0), 1e-6, "LeakyReLU derivative for negative should be alpha");
        assertEquals(1.0, leakyRelu.derivative(5.0), 1e-6, "LeakyReLU derivative for positive should be 1");
        
        // Проверяем название
        assertEquals("LeakyReLU", leakyRelu.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test LeakyReLU with default alpha")
    void testLeakyReLUWithDefaultAlpha() {
        ActivationFunction.LeakyReLU leakyRelu = new ActivationFunction.LeakyReLU();
        
        // Тест с дефолтным alpha = 0.01
        assertEquals(-0.05, leakyRelu.activate(-5.0), 1e-6, "LeakyReLU(-5) with default alpha should be -0.05");
        assertEquals(0.01, leakyRelu.derivative(-5.0), 1e-6, "LeakyReLU derivative with default alpha should be 0.01");
    }
    
    @Test
    @DisplayName("Should test ELU activation function")
    void testELUActivation() {
        ActivationFunction.ELU elu = new ActivationFunction.ELU(1.0);
        
        // Тест активации
        assertEquals(0.0, elu.activate(0.0), 1e-6, "ELU(0) should be 0");
        assertEquals(5.0, elu.activate(5.0), 1e-6, "ELU(5) should be 5");
        assertTrue(elu.activate(-5.0) < 0, "ELU(-5) should be negative");
        
        // Тест производной
        assertEquals(1.0, elu.derivative(5.0), 1e-6, "ELU derivative for positive should be 1");
        assertTrue(elu.derivative(-5.0) > 0, "ELU derivative for negative should be positive");
        
        // Проверяем название
        assertEquals("ELU", elu.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test ELU with default alpha")
    void testELUWithDefaultAlpha() {
        ActivationFunction.ELU elu = new ActivationFunction.ELU();
        
        // Тест с дефолтным alpha = 1.0
        assertEquals(0.0, elu.activate(0.0), 1e-6, "ELU(0) with default alpha should be 0");
        assertEquals(1.0, elu.derivative(5.0), 1e-6, "ELU derivative with default alpha should be 1");
    }
    
    @Test
    @DisplayName("Should test Softmax activation function")
    void testSoftmaxActivation() {
        ActivationFunction.Softmax softmax = new ActivationFunction.Softmax();
        
        // Тест активации (для одного значения)
        assertEquals(1.0, softmax.activate(1.0), 1e-6, "Softmax(1) should be 1");
        
        // Тест применения softmax к массиву
        double[] values = {1.0, 2.0, 3.0};
        double[] result = softmax.applySoftmax(values);
        
        assertNotNull(result, "Softmax result should not be null");
        assertEquals(values.length, result.length, "Softmax result should have same length");
        
        // Проверяем, что сумма равна 1
        double sum = 0.0;
        for (double value : result) {
            sum += value;
        }
        assertEquals(1.0, sum, 1e-6, "Softmax result sum should be 1");
        
        // Проверяем, что все значения положительны
        for (double value : result) {
            assertTrue(value > 0, "Softmax result should be positive");
        }
        
        // Проверяем название
        assertEquals("Softmax", softmax.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test Swish activation function")
    void testSwishActivation() {
        ActivationFunction.Swish swish = new ActivationFunction.Swish();
        
        // Тест активации
        assertEquals(0.0, swish.activate(0.0), 1e-6, "Swish(0) should be 0");
        assertTrue(swish.activate(5.0) > 0, "Swish(5) should be positive");
        assertTrue(swish.activate(-5.0) < 0, "Swish(-5) should be negative");
        
        // Тест производной
        assertTrue(swish.derivative(0.0) > 0, "Swish derivative should be positive");
        assertTrue(swish.derivative(5.0) > 0, "Swish derivative for positive should be positive");
        
        // Проверяем название
        assertEquals("Swish", swish.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test GELU activation function")
    void testGELUActivation() {
        ActivationFunction.GELU gelu = new ActivationFunction.GELU();
        
        // Тест активации
        assertEquals(0.0, gelu.activate(0.0), 1e-6, "GELU(0) should be 0");
        assertTrue(gelu.activate(5.0) > 0, "GELU(5) should be positive");
        assertTrue(gelu.activate(-5.0) < 0, "GELU(-5) should be negative");
        
        // Тест производной
        assertTrue(gelu.derivative(0.0) > 0, "GELU derivative should be positive");
        assertTrue(gelu.derivative(5.0) > 0, "GELU derivative for positive should be positive");
        
        // Проверяем название
        assertEquals("GELU", gelu.getName(), "Should have correct name");
    }
    
    @Test
    @DisplayName("Should test activation functions with extreme values")
    void testActivationFunctionsWithExtremeValues() {
        ActivationFunction.Sigmoid sigmoid = new ActivationFunction.Sigmoid();
        ActivationFunction.Tanh tanh = new ActivationFunction.Tanh();
        
        // Тест с очень большими значениями
        double largeValue = 1000.0;
        assertTrue(sigmoid.activate(largeValue) > 0.99, "Sigmoid should handle large values");
        assertTrue(tanh.activate(largeValue) > 0.99, "Tanh should handle large values");
        
        // Тест с очень маленькими значениями
        double smallValue = -1000.0;
        assertTrue(sigmoid.activate(smallValue) < 0.01, "Sigmoid should handle small values");
        assertTrue(tanh.activate(smallValue) < -0.99, "Tanh should handle small values");
    }
    
    @Test
    @DisplayName("Should test activation functions with zero")
    void testActivationFunctionsWithZero() {
        ActivationFunction.ReLU relu = new ActivationFunction.ReLU();
        ActivationFunction.LeakyReLU leakyRelu = new ActivationFunction.LeakyReLU(0.1);
        ActivationFunction.ELU elu = new ActivationFunction.ELU(1.0);
        
        // Все функции должны возвращать 0 для входа 0
        assertEquals(0.0, relu.activate(0.0), 1e-6, "ReLU(0) should be 0");
        assertEquals(0.0, leakyRelu.activate(0.0), 1e-6, "LeakyReLU(0) should be 0");
        assertEquals(0.0, elu.activate(0.0), 1e-6, "ELU(0) should be 0");
    }
    
    @Test
    @DisplayName("Should test activation function continuity")
    void testActivationFunctionContinuity() {
        ActivationFunction.Sigmoid sigmoid = new ActivationFunction.Sigmoid();
        ActivationFunction.Tanh tanh = new ActivationFunction.Tanh();
        ActivationFunction.ReLU relu = new ActivationFunction.ReLU();
        
        // Тест непрерывности в окрестности 0
        double[] testValues = {-0.1, -0.01, -0.001, 0.0, 0.001, 0.01, 0.1};
        
        for (double x : testValues) {
            double sigmoidValue = sigmoid.activate(x);
            double tanhValue = tanh.activate(x);
            double reluValue = relu.activate(x);
            
            assertTrue(Double.isFinite(sigmoidValue), "Sigmoid should be finite for " + x);
            assertTrue(Double.isFinite(tanhValue), "Tanh should be finite for " + x);
            assertTrue(Double.isFinite(reluValue), "ReLU should be finite for " + x);
        }
    }
}
