package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.Random;

/**
 * Bagging Ensemble - ансамбль моделей с бэггингом
 * 
 * Bagging (Bootstrap Aggregating) Ensemble создает множество
 * моделей на разных подвыборках данных, что помогает уменьшить
 * переобучение и повысить обобщающую способность.
 * 
 * @author ni-okr
 * @version 3.4
 */
public class BaggingEnsemble extends EnsembleModel {
    
    private static final Logger _log = Logger.getLogger(BaggingEnsemble.class.getName());
    
    // ==================== ПАРАМЕТРЫ ====================
    
    /** Размер подвыборки (в процентах от исходных данных) */
    private double sampleSize;
    
    /** Количество выходных классов */
    private int outputClasses;
    
    /** Генератор случайных чисел */
    private final Random random;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальный размер подвыборки */
    private static final double MIN_SAMPLE_SIZE = 0.1;
    
    /** Максимальный размер подвыборки */
    private static final double MAX_SAMPLE_SIZE = 1.0;
    
    /** Минимальное количество выходных классов */
    private static final int MIN_OUTPUT_CLASSES = 2;
    
    /** Максимальное количество выходных классов */
    private static final int MAX_OUTPUT_CLASSES = 1000;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый Bagging Ensemble
     * 
     * @param botId ID бота
     * @param sampleSize размер подвыборки (0.0 - 1.0)
     */
    public BaggingEnsemble(int botId, double sampleSize) {
        super(botId, EnsembleType.BAGGING);
        this.sampleSize = Math.max(MIN_SAMPLE_SIZE, Math.min(MAX_SAMPLE_SIZE, sampleSize));
        this.outputClasses = 2; // По умолчанию бинарная классификация
        this.random = new Random();
        
        _log.info("BaggingEnsemble created for bot " + botId + 
                 ", sample size: " + this.sampleSize);
    }
    
    /**
     * Создает новый Bagging Ensemble с указанным количеством классов
     * 
     * @param botId ID бота
     * @param sampleSize размер подвыборки (0.0 - 1.0)
     * @param outputClasses количество выходных классов
     */
    public BaggingEnsemble(int botId, double sampleSize, int outputClasses) {
        super(botId, EnsembleType.BAGGING);
        this.sampleSize = Math.max(MIN_SAMPLE_SIZE, Math.min(MAX_SAMPLE_SIZE, sampleSize));
        this.outputClasses = Math.max(MIN_OUTPUT_CLASSES, Math.min(MAX_OUTPUT_CLASSES, outputClasses));
        this.random = new Random();
        
        _log.info("BaggingEnsemble created for bot " + botId + 
                 ", sample size: " + this.sampleSize +
                 ", output classes: " + this.outputClasses);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Выполняет предсказание с помощью бэггинга
     * 
     * @param input входные данные
     * @return результат предсказания
     */
    @Override
    public double[] predict(double[] input) {
        if (!isActive) {
            _log.warn("Cannot predict: ensemble not active for bot " + botId);
            return null;
        }
        
        if (input == null) {
            _log.warn("Cannot predict: input is null for bot " + botId);
            return null;
        }
        
        try {
            // Получаем предсказания от всех моделей
            double[][] predictions = getAllPredictions(input);
            
            // Усредняем предсказания
            double[] result = averagePredictions(predictions);
            
            return result;
            
        } catch (Exception e) {
            _log.error("Error in prediction for bot " + botId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Обучает ансамбль
     * 
     * @param inputs входные данные
     * @param targets целевые значения
     * @param epochs количество эпох
     * @return true если обучение прошло успешно
     */
    @Override
    public boolean train(double[][] inputs, double[][] targets, int epochs) {
        if (!isActive) {
            _log.warn("Cannot train: ensemble not active for bot " + botId);
            return false;
        }
        
        if (inputs == null || targets == null) {
            _log.warn("Invalid training data for bot " + botId);
            return false;
        }
        
        if (inputs.length != targets.length) {
            _log.warn("Input and target count mismatch for bot " + botId);
            return false;
        }
        
        try {
            // Обучаем каждую модель на своей подвыборке
            for (int i = 0; i < modelCount; i++) {
                Object model = models.get(i);
                
                // Создаем подвыборку для текущей модели
                BootstrapSample sample = createBootstrapSample(inputs, targets);
                
                // Обучаем модель на подвыборке
                trainModelOnSample(model, sample.inputs, sample.targets, epochs);
            }
            
            _log.info("BaggingEnsemble training completed for bot " + botId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error in training for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Создает бутстрап-подвыборку
     * 
     * @param inputs входные данные
     * @param targets целевые значения
     * @return бутстрап-подвыборка
     */
    private BootstrapSample createBootstrapSample(double[][] inputs, double[][] targets) {
        int sampleSizeInt = (int) (inputs.length * sampleSize);
        double[][] sampleInputs = new double[sampleSizeInt][];
        double[][] sampleTargets = new double[sampleSizeInt][];
        
        // Создаем подвыборку с возвращением
        for (int i = 0; i < sampleSizeInt; i++) {
            int randomIndex = random.nextInt(inputs.length);
            sampleInputs[i] = inputs[randomIndex].clone();
            sampleTargets[i] = targets[randomIndex].clone();
        }
        
        return new BootstrapSample(sampleInputs, sampleTargets);
    }
    
    /**
     * Обучает модель на подвыборке
     * 
     * @param model модель для обучения
     * @param inputs входные данные подвыборки
     * @param targets целевые значения подвыборки
     * @param epochs количество эпох
     */
    private void trainModelOnSample(Object model, double[][] inputs, double[][] targets, int epochs) {
        try {
            if (model instanceof DeepNeuralNetwork) {
                DeepNeuralNetwork dnn = (DeepNeuralNetwork) model;
                ProcessedData data = new ProcessedData(inputs, targets);
                dnn.train(data);
            } else if (model instanceof ConvolutionalNeuralNetwork) {
                ConvolutionalNeuralNetwork cnn = (ConvolutionalNeuralNetwork) model;
                // Преобразуем данные для CNN (упрощенная версия)
                double[][][][] cnnInputs = convertToCNNInput(inputs);
                double[][] cnnTargets = targets; // CNN использует те же targets
                cnn.train(cnnInputs, cnnTargets, epochs);
            } else if (model instanceof RecurrentNeuralNetwork) {
                RecurrentNeuralNetwork rnn = (RecurrentNeuralNetwork) model;
                // Преобразуем данные для RNN (упрощенная версия)
                double[][][] rnnInputs = convertToRNNInput(inputs);
                double[][][] rnnTargets = convertToRNNTarget(targets);
                rnn.train(rnnInputs, rnnTargets, epochs);
            } else {
                _log.warn("Unknown model type for training: " + model.getClass().getSimpleName());
            }
        } catch (Exception e) {
            _log.error("Error training model on sample: " + e.getMessage());
        }
    }
    
    /**
     * Усредняет предсказания от всех моделей
     * 
     * @param predictions массив предсказаний
     * @return усредненное предсказание
     */
    private double[] averagePredictions(double[][] predictions) {
        double[] result = new double[outputClasses];
        int validPredictions = 0;
        
        // Суммируем предсказания от всех моделей
        for (double[] prediction : predictions) {
            if (prediction != null && prediction.length == outputClasses) {
                for (int i = 0; i < outputClasses; i++) {
                    result[i] += prediction[i];
                }
                validPredictions++;
            }
        }
        
        // Усредняем результат
        if (validPredictions > 0) {
            for (int i = 0; i < result.length; i++) {
                result[i] /= validPredictions;
            }
        }
        
        return result;
    }
    
    /**
     * Получает предсказание от конкретной модели
     * 
     * @param model модель
     * @param input входные данные
     * @return предсказание
     */
    @Override
    protected double[] getModelPrediction(Object model, double[] input) {
        try {
            if (model instanceof DeepNeuralNetwork) {
                DeepNeuralNetwork dnn = (DeepNeuralNetwork) model;
                return dnn.forward(input);
            } else if (model instanceof ConvolutionalNeuralNetwork) {
                ConvolutionalNeuralNetwork cnn = (ConvolutionalNeuralNetwork) model;
                // Преобразуем входные данные для CNN (упрощенная версия)
                double[][][] cnnInput = convertToCNNInput(new double[][]{input})[0];
                return cnn.forward(cnnInput);
            } else if (model instanceof RecurrentNeuralNetwork) {
                RecurrentNeuralNetwork rnn = (RecurrentNeuralNetwork) model;
                // Преобразуем входные данные для RNN (упрощенная версия)
                double[][] rnnInput = convertToRNNInput(new double[][]{input})[0];
                double[][] rnnOutput = rnn.forward(rnnInput);
                return rnnOutput != null && rnnOutput.length > 0 ? rnnOutput[0] : new double[outputClasses];
            } else {
                _log.warn("Unknown model type for prediction: " + model.getClass().getSimpleName());
                return new double[outputClasses];
            }
        } catch (Exception e) {
            _log.error("Error getting prediction from model: " + e.getMessage());
            return new double[outputClasses];
        }
    }
    
    // ==================== МЕТОДЫ ПРЕОБРАЗОВАНИЯ ДАННЫХ ====================
    
    /**
     * Преобразует данные для CNN (упрощенная версия)
     * 
     * @param inputs входные данные
     * @return данные для CNN
     */
    private double[][][][] convertToCNNInput(double[][] inputs) {
        // Упрощенное преобразование - в реальной реализации должно быть более сложным
        double[][][][] cnnInputs = new double[inputs.length][][][];
        for (int i = 0; i < inputs.length; i++) {
            cnnInputs[i] = new double[1][1][inputs[i].length];
            System.arraycopy(inputs[i], 0, cnnInputs[i][0][0], 0, inputs[i].length);
        }
        return cnnInputs;
    }
    
    
    /**
     * Преобразует данные для RNN (упрощенная версия)
     * 
     * @param inputs входные данные
     * @return данные для RNN
     */
    private double[][][] convertToRNNInput(double[][] inputs) {
        // Упрощенное преобразование - в реальной реализации должно быть более сложным
        double[][][] rnnInputs = new double[inputs.length][][];
        for (int i = 0; i < inputs.length; i++) {
            rnnInputs[i] = new double[1][inputs[i].length];
            System.arraycopy(inputs[i], 0, rnnInputs[i][0], 0, inputs[i].length);
        }
        return rnnInputs;
    }
    
    /**
     * Преобразует целевые данные для RNN (упрощенная версия)
     * 
     * @param targets целевые данные
     * @return данные для RNN
     */
    private double[][][] convertToRNNTarget(double[][] targets) {
        // Упрощенное преобразование - в реальной реализации должно быть более сложным
        double[][][] rnnTargets = new double[targets.length][][];
        for (int i = 0; i < targets.length; i++) {
            rnnTargets[i] = new double[1][targets[i].length];
            System.arraycopy(targets[i], 0, rnnTargets[i][0], 0, targets[i].length);
        }
        return rnnTargets;
    }
    
    // ==================== ГЕТТЕРЫ И СЕТТЕРЫ ====================
    
    /**
     * Возвращает размер подвыборки
     * 
     * @return размер подвыборки
     */
    public double getSampleSize() {
        return sampleSize;
    }
    
    /**
     * Устанавливает размер подвыборки
     * 
     * @param sampleSize размер подвыборки
     */
    public void setSampleSize(double sampleSize) {
        if (sampleSize >= MIN_SAMPLE_SIZE && sampleSize <= MAX_SAMPLE_SIZE) {
            this.sampleSize = sampleSize;
        }
    }
    
    /**
     * Возвращает количество выходных классов
     * 
     * @return количество выходных классов
     */
    public int getOutputClasses() {
        return outputClasses;
    }
    
    /**
     * Устанавливает количество выходных классов
     * 
     * @param outputClasses количество выходных классов
     */
    public void setOutputClasses(int outputClasses) {
        if (outputClasses >= MIN_OUTPUT_CLASSES && outputClasses <= MAX_OUTPUT_CLASSES) {
            this.outputClasses = outputClasses;
        }
    }
    
    // ==================== ВЛОЖЕННЫЕ КЛАССЫ ====================
    
    /**
     * Класс для хранения бутстрап-подвыборки
     */
    private static class BootstrapSample {
        final double[][] inputs;
        final double[][] targets;
        
        BootstrapSample(double[][] inputs, double[][] targets) {
            this.inputs = inputs;
            this.targets = targets;
        }
    }
}
