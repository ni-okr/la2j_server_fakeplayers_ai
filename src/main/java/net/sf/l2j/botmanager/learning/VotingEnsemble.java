package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

/**
 * Voting Ensemble - ансамбль моделей с голосованием
 * 
 * Voting Ensemble комбинирует предсказания нескольких моделей
 * через простое голосование (для классификации) или усреднение
 * (для регрессии). Это простой, но эффективный способ
 * повышения точности предсказаний.
 * 
 * @author ni-okr
 * @version 3.4
 */
public class VotingEnsemble extends EnsembleModel {
    
    private static final Logger _log = Logger.getLogger(VotingEnsemble.class.getName());
    
    // ==================== ПАРАМЕТРЫ ====================
    
    /** Тип голосования */
    private final VotingType votingType;
    
    /** Количество выходных классов */
    private int outputClasses;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальное количество выходных классов */
    private static final int MIN_OUTPUT_CLASSES = 2;
    
    /** Максимальное количество выходных классов */
    private static final int MAX_OUTPUT_CLASSES = 1000;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый Voting Ensemble
     * 
     * @param botId ID бота
     * @param votingType тип голосования
     */
    public VotingEnsemble(int botId, VotingType votingType) {
        super(botId, EnsembleType.VOTING);
        this.votingType = votingType;
        this.outputClasses = 2; // По умолчанию бинарная классификация
        
        _log.info("VotingEnsemble created for bot " + botId + ", voting type: " + votingType);
    }
    
    /**
     * Создает новый Voting Ensemble с указанным количеством классов
     * 
     * @param botId ID бота
     * @param votingType тип голосования
     * @param outputClasses количество выходных классов
     */
    public VotingEnsemble(int botId, VotingType votingType, int outputClasses) {
        super(botId, EnsembleType.VOTING);
        this.votingType = votingType;
        this.outputClasses = Math.max(MIN_OUTPUT_CLASSES, Math.min(MAX_OUTPUT_CLASSES, outputClasses));
        
        _log.info("VotingEnsemble created for bot " + botId + 
                 ", voting type: " + votingType + 
                 ", output classes: " + this.outputClasses);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Выполняет предсказание с помощью голосования
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
            
            // Комбинируем предсказания в зависимости от типа голосования
            double[] result = combinePredictions(predictions);
            
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
            // Обучаем каждую модель отдельно
            for (int i = 0; i < modelCount; i++) {
                Object model = models.get(i);
                trainModel(model, inputs, targets, epochs);
            }
            
            _log.info("VotingEnsemble training completed for bot " + botId);
            return true;
            
        } catch (Exception e) {
            _log.error("Error in training for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Комбинирует предсказания от всех моделей
     * 
     * @param predictions массив предсказаний
     * @return комбинированное предсказание
     */
    private double[] combinePredictions(double[][] predictions) {
        if (votingType == VotingType.HARD) {
            return hardVoting(predictions);
        } else {
            return softVoting(predictions);
        }
    }
    
    /**
     * Жесткое голосование (для классификации)
     * 
     * @param predictions массив предсказаний
     * @return результат голосования
     */
    private double[] hardVoting(double[][] predictions) {
        double[] result = new double[outputClasses];
        
        // Подсчитываем голоса за каждый класс
        for (double[] prediction : predictions) {
            if (prediction != null && prediction.length > 0) {
                int predictedClass = getPredictedClass(prediction);
                if (predictedClass >= 0 && predictedClass < outputClasses) {
                    result[predictedClass]++;
                }
            }
        }
        
        // Нормализуем результат
        double totalVotes = 0;
        for (double vote : result) {
            totalVotes += vote;
        }
        
        if (totalVotes > 0) {
            for (int i = 0; i < result.length; i++) {
                result[i] /= totalVotes;
            }
        }
        
        return result;
    }
    
    /**
     * Мягкое голосование (усреднение вероятностей)
     * 
     * @param predictions массив предсказаний
     * @return результат усреднения
     */
    private double[] softVoting(double[][] predictions) {
        double[] result = new double[outputClasses];
        int validPredictions = 0;
        
        // Суммируем предсказания от всех моделей
        for (double[] prediction : predictions) {
            if (prediction != null && prediction.length == outputClasses) {
                for (int i = 0; i < outputClasses; i++) {
                    result[i] += prediction[i] * modelWeights[validPredictions];
                }
                validPredictions++;
            }
        }
        
        // Нормализуем результат
        if (validPredictions > 0) {
            for (int i = 0; i < result.length; i++) {
                result[i] /= validPredictions;
            }
            
            // Если все значения равны нулю, устанавливаем равномерное распределение
            double sum = 0;
            for (double value : result) {
                sum += value;
            }
            
            if (sum == 0) {
                double uniformValue = 1.0 / outputClasses;
                for (int i = 0; i < result.length; i++) {
                    result[i] = uniformValue;
                }
            } else {
                // Нормализуем к сумме 1
                for (int i = 0; i < result.length; i++) {
                    result[i] /= sum;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Определяет предсказанный класс
     * 
     * @param prediction предсказание модели
     * @return индекс класса
     */
    private int getPredictedClass(double[] prediction) {
        int maxIndex = 0;
        double maxValue = prediction[0];
        
        for (int i = 1; i < prediction.length; i++) {
            if (prediction[i] > maxValue) {
                maxValue = prediction[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
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
     * Возвращает тип голосования
     * 
     * @return тип голосования
     */
    public VotingType getVotingType() {
        return votingType;
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
     * Типы голосования
     */
    public enum VotingType {
        HARD("Hard Voting"),
        SOFT("Soft Voting");
        
        private final String name;
        
        VotingType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
