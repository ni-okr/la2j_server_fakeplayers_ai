package net.sf.l2j.botmanager.learning;

import java.util.Random;

/**
 * Дополнительные слои для глубокой нейронной сети
 * 
 * Этот файл содержит реализации дополнительных слоев,
 * таких как Dropout, BatchNormalization и других.
 * 
 * @author AI Assistant
 * @version 3.2
 * @since 2025-09-27
 */

/**
 * Слой Dropout для предотвращения переобучения
 */
class DropoutLayer extends NetworkLayer {
    
    private final double dropoutRate;
    private boolean[] mask;
    private boolean training;
    
    /**
     * Конструктор слоя Dropout
     * 
     * @param dropoutRate коэффициент dropout (0.0 - 1.0)
     */
    public DropoutLayer(double dropoutRate) {
        super(LayerType.DROPOUT);
        this.dropoutRate = Math.max(0.0, Math.min(1.0, dropoutRate));
        this.training = true;
    }
    
    @Override
    public void initialize(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = inputSize; // Dropout не изменяет размер
        this.mask = new boolean[inputSize];
    }
    
    @Override
    public double[] forward(double[] inputs) {
        if (inputs.length != inputSize) {
            throw new IllegalArgumentException("Input size mismatch");
        }
        
        if (training) {
            // Создаем маску для dropout
            Random random = new Random();
            for (int i = 0; i < inputSize; i++) {
                mask[i] = random.nextDouble() > dropoutRate;
            }
            
            // Применяем dropout
            double[] output = new double[inputSize];
            for (int i = 0; i < inputSize; i++) {
                output[i] = mask[i] ? inputs[i] / (1.0 - dropoutRate) : 0.0;
            }
            return output;
        } else {
            // В режиме инференса просто возвращаем входы
            return inputs.clone();
        }
    }
    
    @Override
    public double[] backward(double[] inputs, double[] outputGradients) {
        if (training) {
            // Применяем маску к градиентам
            double[] inputGradients = new double[inputSize];
            for (int i = 0; i < inputSize; i++) {
                inputGradients[i] = mask[i] ? outputGradients[i] / (1.0 - dropoutRate) : 0.0;
            }
            return inputGradients;
        } else {
            return outputGradients.clone();
        }
    }
    
    @Override
    public void updateWeights(double learningRate) {
        // Dropout не имеет весов для обновления
    }
    
    /**
     * Устанавливает режим обучения
     * 
     * @param training true для обучения, false для инференса
     */
    public void setTraining(boolean training) {
        this.training = training;
    }
    
    /**
     * Возвращает коэффициент dropout
     * 
     * @return коэффициент dropout
     */
    public double getDropoutRate() {
        return dropoutRate;
    }
}

/**
 * Слой Batch Normalization для стабилизации обучения
 */
class BatchNormalizationLayer extends NetworkLayer {
    
    private double[] gamma; // Масштабирующий параметр
    private double[] beta;  // Сдвигающий параметр
    private double[] runningMean; // Бегущее среднее
    private double[] runningVar;  // Бегущая дисперсия
    private double[] normalized;  // Нормализованные значения
    private double[] xHat;        // Нормализованные значения для обратного распространения
    private boolean training;
    private double momentum;
    private double epsilon;
    
    /**
     * Конструктор слоя Batch Normalization
     */
    public BatchNormalizationLayer() {
        super(LayerType.BATCH_NORMALIZATION);
        this.training = true;
        this.momentum = 0.9;
        this.epsilon = 1e-8;
    }
    
    @Override
    public void initialize(int inputSize, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = inputSize; // BatchNorm не изменяет размер
        this.gamma = new double[inputSize];
        this.beta = new double[inputSize];
        this.runningMean = new double[inputSize];
        this.runningVar = new double[inputSize];
        this.normalized = new double[inputSize];
        this.xHat = new double[inputSize];
        
        // Инициализируем параметры
        for (int i = 0; i < inputSize; i++) {
            gamma[i] = 1.0; // Начальное значение gamma = 1
            beta[i] = 0.0;  // Начальное значение beta = 0
            runningMean[i] = 0.0;
            runningVar[i] = 1.0;
        }
    }
    
    @Override
    public double[] forward(double[] inputs) {
        if (inputs.length != inputSize) {
            throw new IllegalArgumentException("Input size mismatch");
        }
        
        if (training) {
            // Вычисляем среднее и дисперсию для текущего батча
            double mean = 0.0;
            for (double input : inputs) {
                mean += input;
            }
            mean /= inputSize;
            
            double var = 0.0;
            for (double input : inputs) {
                var += (input - mean) * (input - mean);
            }
            var /= inputSize;
            
            // Обновляем бегущие статистики
            for (int i = 0; i < inputSize; i++) {
                runningMean[i] = momentum * runningMean[i] + (1.0 - momentum) * mean;
                runningVar[i] = momentum * runningVar[i] + (1.0 - momentum) * var;
            }
            
            // Нормализуем
            for (int i = 0; i < inputSize; i++) {
                xHat[i] = (inputs[i] - mean) / Math.sqrt(var + epsilon);
                normalized[i] = gamma[i] * xHat[i] + beta[i];
            }
        } else {
            // В режиме инференса используем бегущие статистики
            for (int i = 0; i < inputSize; i++) {
                normalized[i] = gamma[i] * (inputs[i] - runningMean[i]) / Math.sqrt(runningVar[i] + epsilon) + beta[i];
            }
        }
        
        return normalized.clone();
    }
    
    @Override
    public double[] backward(double[] inputs, double[] outputGradients) {
        if (training) {
            // Вычисляем градиенты для batch normalization
            double[] inputGradients = new double[inputSize];
            double[] gammaGradients = new double[inputSize];
            double[] betaGradients = new double[inputSize];
            
            // Градиенты по beta
            for (int i = 0; i < inputSize; i++) {
                betaGradients[i] = outputGradients[i];
            }
            
            // Градиенты по gamma
            for (int i = 0; i < inputSize; i++) {
                gammaGradients[i] = outputGradients[i] * xHat[i];
            }
            
            // Градиенты по входам (упрощенная версия)
            for (int i = 0; i < inputSize; i++) {
                inputGradients[i] = outputGradients[i] * gamma[i];
            }
            
            // Обновляем параметры (упрощенная версия)
            for (int i = 0; i < inputSize; i++) {
                gamma[i] += 0.01 * gammaGradients[i]; // Простое обновление
                beta[i] += 0.01 * betaGradients[i];
            }
            
            return inputGradients;
        } else {
            return outputGradients.clone();
        }
    }
    
    @Override
    public void updateWeights(double learningRate) {
        // Параметры gamma и beta обновляются в методе backward
    }
    
    /**
     * Устанавливает режим обучения
     * 
     * @param training true для обучения, false для инференса
     */
    public void setTraining(boolean training) {
        this.training = training;
    }
    
    /**
     * Возвращает параметр gamma
     * 
     * @return параметр gamma
     */
    public double[] getGamma() {
        return gamma.clone();
    }
    
    /**
     * Возвращает параметр beta
     * 
     * @return параметр beta
     */
    public double[] getBeta() {
        return beta.clone();
    }
    
    /**
     * Возвращает бегущее среднее
     * 
     * @return бегущее среднее
     */
    public double[] getRunningMean() {
        return runningMean.clone();
    }
    
    /**
     * Возвращает бегущую дисперсию
     * 
     * @return бегущая дисперсия
     */
    public double[] getRunningVar() {
        return runningVar.clone();
    }
}
