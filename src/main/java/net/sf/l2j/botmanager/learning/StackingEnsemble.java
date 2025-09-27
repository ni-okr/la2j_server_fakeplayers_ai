package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

/**
 * Stacking Ensemble - ансамбль моделей со стэкингом
 * 
 * Stacking Ensemble использует мета-обучатель для комбинирования
 * предсказаний базовых моделей. Это более сложный, но часто
 * более эффективный подход по сравнению с простым голосованием.
 * 
 * @author ni-okr
 * @version 3.4
 */
public class StackingEnsemble extends EnsembleModel {
    
    private static final Logger _log = Logger.getLogger(StackingEnsemble.class.getName());
    
    // ==================== ПАРАМЕТРЫ ====================
    
    /** Мета-обучатель */
    private Object metaLearner;
    
    /** Обучен ли мета-обучатель */
    private boolean metaLearnerTrained;
    
    /** Количество выходных классов */
    private int outputClasses;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальное количество выходных классов */
    private static final int MIN_OUTPUT_CLASSES = 2;
    
    /** Максимальное количество выходных классов */
    private static final int MAX_OUTPUT_CLASSES = 1000;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый Stacking Ensemble
     * 
     * @param botId ID бота
     * @param metaLearner мета-обучатель
     */
    public StackingEnsemble(int botId, Object metaLearner) {
        super(botId, EnsembleType.STACKING);
        this.metaLearner = metaLearner;
        this.metaLearnerTrained = false;
        this.outputClasses = 2; // По умолчанию бинарная классификация
        
        _log.info("StackingEnsemble created for bot " + botId + 
                 ", meta-learner: " + (metaLearner != null ? metaLearner.getClass().getSimpleName() : "null"));
    }
    
    /**
     * Создает новый Stacking Ensemble с указанным количеством классов
     * 
     * @param botId ID бота
     * @param metaLearner мета-обучатель
     * @param outputClasses количество выходных классов
     */
    public StackingEnsemble(int botId, Object metaLearner, int outputClasses) {
        super(botId, EnsembleType.STACKING);
        this.metaLearner = metaLearner;
        this.metaLearnerTrained = false;
        this.outputClasses = Math.max(MIN_OUTPUT_CLASSES, Math.min(MAX_OUTPUT_CLASSES, outputClasses));
        
        _log.info("StackingEnsemble created for bot " + botId + 
                 ", meta-learner: " + (metaLearner != null ? metaLearner.getClass().getSimpleName() : "null") +
                 ", output classes: " + this.outputClasses);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Выполняет предсказание с помощью стэкинга
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
        
        if (!metaLearnerTrained) {
            _log.warn("Cannot predict: meta-learner not trained for bot " + botId);
            return null;
        }
        
        if (input == null) {
            _log.warn("Cannot predict: input is null for bot " + botId);
            return null;
        }
        
        try {
            // Получаем предсказания от всех базовых моделей
            double[][] basePredictions = getAllPredictions(input);
            
            // Объединяем предсказания в один вектор
            double[] metaInput = combineBasePredictions(basePredictions);
            
            // Получаем финальное предсказание от мета-обучателя
            double[] result = getMetaLearnerPrediction(metaInput);
            
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
            // Шаг 1: Обучаем базовые модели
            trainBaseModels(inputs, targets, epochs);
            
            // Шаг 2: Создаем мета-данные для обучения мета-обучателя
            double[][] metaInputs = createMetaInputs(inputs);
            double[][] metaTargets = targets; // Используем те же целевые значения
            
            // Шаг 3: Обучаем мета-обучатель
            trainMetaLearner(metaInputs, metaTargets, epochs);
            
            metaLearnerTrained = true;
            
            _log.info("StackingEnsemble training completed for bot " + botId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error in training for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Обучает базовые модели
     * 
     * @param inputs входные данные
     * @param targets целевые значения
     * @param epochs количество эпох
     */
    private void trainBaseModels(double[][] inputs, double[][] targets, int epochs) {
        for (int i = 0; i < modelCount; i++) {
            Object model = models.get(i);
            trainModel(model, inputs, targets, epochs);
        }
    }
    
    /**
     * Создает мета-данные для обучения мета-обучателя
     * 
     * @param inputs входные данные
     * @return мета-данные
     */
    private double[][] createMetaInputs(double[][] inputs) {
        double[][] metaInputs = new double[inputs.length][];
        
        for (int i = 0; i < inputs.length; i++) {
            // Получаем предсказания от всех базовых моделей
            double[][] basePredictions = getAllPredictions(inputs[i]);
            
            // Объединяем предсказания в один вектор
            metaInputs[i] = combineBasePredictions(basePredictions);
        }
        
        return metaInputs;
    }
    
    /**
     * Объединяет предсказания базовых моделей в один вектор
     * 
     * @param basePredictions предсказания базовых моделей
     * @return объединенный вектор
     */
    private double[] combineBasePredictions(double[][] basePredictions) {
        int totalLength = 0;
        
        // Вычисляем общую длину
        for (double[] prediction : basePredictions) {
            if (prediction != null) {
                totalLength += prediction.length;
            }
        }
        
        double[] combined = new double[totalLength];
        int index = 0;
        
        // Объединяем предсказания
        for (double[] prediction : basePredictions) {
            if (prediction != null) {
                System.arraycopy(prediction, 0, combined, index, prediction.length);
                index += prediction.length;
            }
        }
        
        return combined;
    }
    
    /**
     * Обучает мета-обучатель
     * 
     * @param metaInputs мета-входные данные
     * @param metaTargets мета-целевые значения
     * @param epochs количество эпох
     */
    private void trainMetaLearner(double[][] metaInputs, double[][] metaTargets, int epochs) {
        if (metaLearner == null) {
            _log.warn("Meta-learner is null, cannot train");
            return;
        }
        
        try {
            if (metaLearner instanceof DeepNeuralNetwork) {
                DeepNeuralNetwork dnn = (DeepNeuralNetwork) metaLearner;
                ProcessedData data = new ProcessedData(metaInputs, metaTargets);
                dnn.train(data);
            } else if (metaLearner instanceof ConvolutionalNeuralNetwork) {
                ConvolutionalNeuralNetwork cnn = (ConvolutionalNeuralNetwork) metaLearner;
                // Преобразуем данные для CNN (упрощенная версия)
                double[][][][] cnnInputs = convertToCNNInput(metaInputs);
                double[][] cnnTargets = metaTargets; // CNN использует те же targets
                cnn.train(cnnInputs, cnnTargets, epochs);
            } else if (metaLearner instanceof RecurrentNeuralNetwork) {
                RecurrentNeuralNetwork rnn = (RecurrentNeuralNetwork) metaLearner;
                // Преобразуем данные для RNN (упрощенная версия)
                double[][][] rnnInputs = convertToRNNInput(metaInputs);
                double[][][] rnnTargets = convertToRNNTarget(metaTargets);
                rnn.train(rnnInputs, rnnTargets, epochs);
            } else {
                _log.warn("Unknown meta-learner type: " + metaLearner.getClass().getSimpleName());
            }
        } catch (Exception e) {
            _log.error("Error training meta-learner: " + e.getMessage());
        }
    }
    
    /**
     * Получает предсказание от мета-обучателя
     * 
     * @param metaInput мета-входные данные
     * @return предсказание
     */
    private double[] getMetaLearnerPrediction(double[] metaInput) {
        if (metaLearner == null) {
            _log.warn("Meta-learner is null, cannot predict");
            return new double[outputClasses];
        }
        
        try {
            if (metaLearner instanceof DeepNeuralNetwork) {
                DeepNeuralNetwork dnn = (DeepNeuralNetwork) metaLearner;
                return dnn.forward(metaInput);
            } else if (metaLearner instanceof ConvolutionalNeuralNetwork) {
                ConvolutionalNeuralNetwork cnn = (ConvolutionalNeuralNetwork) metaLearner;
                // Преобразуем входные данные для CNN (упрощенная версия)
                double[][][] cnnInput = convertToCNNInput(new double[][]{metaInput})[0];
                return cnn.forward(cnnInput);
            } else if (metaLearner instanceof RecurrentNeuralNetwork) {
                RecurrentNeuralNetwork rnn = (RecurrentNeuralNetwork) metaLearner;
                // Преобразуем входные данные для RNN (упрощенная версия)
                double[][] rnnInput = convertToRNNInput(new double[][]{metaInput})[0];
                double[][] rnnOutput = rnn.forward(rnnInput);
                return rnnOutput != null && rnnOutput.length > 0 ? rnnOutput[0] : new double[outputClasses];
            } else {
                _log.warn("Unknown meta-learner type: " + metaLearner.getClass().getSimpleName());
                return new double[outputClasses];
            }
        } catch (Exception e) {
            _log.error("Error getting prediction from meta-learner: " + e.getMessage());
            return new double[outputClasses];
        }
    }
    
    /**
     * Обучает конкретную модель
     * 
     * @param model модель для обучения
     * @param inputs входные данные
     * @param targets целевые значения
     * @param epochs количество эпох
     */
    private void trainModel(Object model, double[][] inputs, double[][] targets, int epochs) {
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
            _log.error("Error training model: " + e.getMessage());
        }
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
     * Возвращает мета-обучатель
     * 
     * @return мета-обучатель
     */
    public Object getMetaLearner() {
        return metaLearner;
    }
    
    /**
     * Устанавливает мета-обучатель
     * 
     * @param metaLearner мета-обучатель
     */
    public void setMetaLearner(Object metaLearner) {
        this.metaLearner = metaLearner;
        this.metaLearnerTrained = false; // Сбрасываем флаг обучения
    }
    
    /**
     * Возвращает обучен ли мета-обучатель
     * 
     * @return true если мета-обучатель обучен
     */
    public boolean isMetaLearnerTrained() {
        return metaLearnerTrained;
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
}
