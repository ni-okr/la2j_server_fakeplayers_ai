package net.sf.l2j.botmanager.learning;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для ансамблей моделей
 * 
 * Ансамбли моделей объединяют предсказания нескольких моделей
 * для повышения точности и надежности. Это особенно важно
 * для сложных задач, где одна модель может не справиться.
 * 
 * @author ni-okr
 * @version 3.4
 */
public abstract class EnsembleModel {
    
    private static final Logger _log = Logger.getLogger(EnsembleModel.class.getName());
    
    // ==================== ОСНОВНЫЕ ПАРАМЕТРЫ ====================
    
    /** ID бота */
    protected final int botId;
    
    /** Список моделей в ансамбле */
    protected final List<Object> models;
    
    /** Тип ансамбля */
    protected final EnsembleType type;
    
    /** Активен ли ансамбль */
    protected boolean isActive;
    
    /** Веса моделей (для взвешенного голосования) */
    protected double[] modelWeights;
    
    /** Количество моделей */
    protected int modelCount;
    
    // ==================== КОНСТАНТЫ ====================
    
    /** Минимальное количество моделей */
    private static final int MIN_MODELS = 2;
    
    /** Максимальное количество моделей */
    private static final int MAX_MODELS = 20;
    
    /** Минимальный вес модели */
    private static final double MIN_WEIGHT = 0.0;
    
    /** Максимальный вес модели */
    private static final double MAX_WEIGHT = 1.0;
    
    // ==================== КОНСТРУКТОРЫ ====================
    
    /**
     * Создает новый ансамбль моделей
     * 
     * @param botId ID бота
     * @param type тип ансамбля
     */
    public EnsembleModel(int botId, EnsembleType type) {
        this.botId = botId;
        this.type = type;
        this.models = new ArrayList<>();
        this.isActive = false;
        this.modelCount = 0;
        
        _log.info("EnsembleModel created for bot " + botId + ", type: " + type);
    }
    
    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================
    
    /**
     * Добавляет модель в ансамбль
     * 
     * @param model модель для добавления
     * @return true если модель добавлена успешно
     */
    public boolean addModel(Object model) {
        if (isActive) {
            _log.warn("Cannot add models to active ensemble for bot " + botId);
            return false;
        }
        
        if (modelCount >= MAX_MODELS) {
            _log.warn("Maximum number of models reached for bot " + botId);
            return false;
        }
        
        if (model == null) {
            _log.warn("Cannot add null model to ensemble for bot " + botId);
            return false;
        }
        
        try {
            models.add(model);
            modelCount++;
            
            _log.info("Model added to ensemble for bot " + botId + 
                     ", total models: " + modelCount);
            return true;
            
        } catch (Exception e) {
            _log.error("Error adding model to ensemble for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Удаляет модель из ансамбля
     * 
     * @param index индекс модели для удаления
     * @return true если модель удалена успешно
     */
    public boolean removeModel(int index) {
        if (isActive) {
            _log.warn("Cannot remove models from active ensemble for bot " + botId);
            return false;
        }
        
        if (index < 0 || index >= modelCount) {
            _log.warn("Invalid model index for bot " + botId + ": " + index);
            return false;
        }
        
        try {
            models.remove(index);
            modelCount--;
            
            _log.info("Model removed from ensemble for bot " + botId + 
                     ", total models: " + modelCount);
            return true;
            
        } catch (Exception e) {
            _log.error("Error removing model from ensemble for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Активирует ансамбль
     * 
     * @return true если ансамбль активирован успешно
     */
    public boolean activate() {
        if (modelCount < MIN_MODELS) {
            _log.warn("Cannot activate ensemble: insufficient models for bot " + botId + 
                     " (need at least " + MIN_MODELS + ", have " + modelCount + ")");
            return false;
        }
        
        try {
            // Инициализируем веса моделей
            initializeWeights();
            
            // Активируем ансамбль
            isActive = true;
            
            _log.info("EnsembleModel activated for bot " + botId + 
                     " with " + modelCount + " models");
            return true;
            
        } catch (Exception e) {
            _log.error("Error activating ensemble for bot " + botId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Деактивирует ансамбль
     */
    public void deactivate() {
        isActive = false;
        _log.info("EnsembleModel deactivated for bot " + botId);
    }
    
    /**
     * Выполняет предсказание с помощью ансамбля
     * 
     * @param input входные данные
     * @return результат предсказания
     */
    public abstract double[] predict(double[] input);
    
    /**
     * Обучает ансамбль
     * 
     * @param inputs входные данные
     * @param targets целевые значения
     * @param epochs количество эпох
     * @return true если обучение прошло успешно
     */
    public abstract boolean train(double[][] inputs, double[][] targets, int epochs);
    
    /**
     * Очищает все модели
     */
    public void clearModels() {
        models.clear();
        modelCount = 0;
        isActive = false;
        _log.info("All models cleared for EnsembleModel bot " + botId);
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    /**
     * Инициализирует веса моделей
     */
    protected void initializeWeights() {
        modelWeights = new double[modelCount];
        
        // Равномерное распределение весов
        double weight = 1.0 / modelCount;
        for (int i = 0; i < modelCount; i++) {
            modelWeights[i] = weight;
        }
    }
    
    /**
     * Устанавливает веса моделей
     * 
     * @param weights массив весов
     * @return true если веса установлены успешно
     */
    public boolean setModelWeights(double[] weights) {
        if (weights == null) {
            _log.warn("Cannot set null weights for bot " + botId);
            return false;
        }
        
        if (weights.length != modelCount) {
            _log.warn("Weight count mismatch for bot " + botId + 
                     " (expected " + modelCount + ", got " + weights.length + ")");
            return false;
        }
        
        // Проверяем, что веса положительные
        double totalWeight = 0.0;
        for (double weight : weights) {
            if (weight < MIN_WEIGHT) {
                _log.warn("Invalid weight value for bot " + botId + ": " + weight);
                return false;
            }
            totalWeight += weight;
        }
        
        // Нормализуем веса
        if (totalWeight > 0) {
            for (int i = 0; i < weights.length; i++) {
                modelWeights[i] = weights[i] / totalWeight;
            }
        }
        
        _log.info("Model weights updated for bot " + botId);
        return true;
    }
    
    /**
     * Получает предсказания от всех моделей
     * 
     * @param input входные данные
     * @return массив предсказаний от каждой модели
     */
    protected double[][] getAllPredictions(double[] input) {
        double[][] predictions = new double[modelCount][];
        
        for (int i = 0; i < modelCount; i++) {
            try {
                Object model = models.get(i);
                predictions[i] = getModelPrediction(model, input);
            } catch (Exception e) {
                _log.error("Error getting prediction from model " + i + " for bot " + botId + ": " + e.getMessage());
                predictions[i] = new double[0]; // Пустое предсказание
            }
        }
        
        return predictions;
    }
    
    /**
     * Получает предсказание от конкретной модели
     * 
     * @param model модель
     * @param input входные данные
     * @return предсказание
     */
    protected abstract double[] getModelPrediction(Object model, double[] input);
    
    // ==================== ГЕТТЕРЫ И СЕТТЕРЫ ====================
    
    /**
     * Возвращает ID бота
     * 
     * @return ID бота
     */
    public int getBotId() {
        return botId;
    }
    
    /**
     * Возвращает тип ансамбля
     * 
     * @return тип ансамбля
     */
    public EnsembleType getType() {
        return type;
    }
    
    /**
     * Возвращает количество моделей
     * 
     * @return количество моделей
     */
    public int getModelCount() {
        return modelCount;
    }
    
    /**
     * Возвращает активен ли ансамбль
     * 
     * @return true если ансамбль активен
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Возвращает веса моделей
     * 
     * @return массив весов
     */
    public double[] getModelWeights() {
        return modelWeights != null ? modelWeights.clone() : null;
    }
    
    /**
     * Возвращает статистику ансамбля
     * 
     * @return строка со статистикой
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== EnsembleModel Statistics ===\n");
        stats.append("Bot ID: ").append(botId).append("\n");
        stats.append("Type: ").append(type).append("\n");
        stats.append("Models: ").append(modelCount).append("\n");
        stats.append("Active: ").append(isActive).append("\n");
        
        if (modelWeights != null) {
            stats.append("Weights: ");
            for (int i = 0; i < modelWeights.length; i++) {
                stats.append(String.format("%.3f", modelWeights[i]));
                if (i < modelWeights.length - 1) {
                    stats.append(", ");
                }
            }
            stats.append("\n");
        }
        
        return stats.toString();
    }
    
    // ==================== ВЛОЖЕННЫЕ КЛАССЫ ====================
    
    /**
     * Типы ансамблей
     */
    public enum EnsembleType {
        VOTING("Voting"),
        STACKING("Stacking"),
        BAGGING("Bagging"),
        BOOSTING("Boosting");
        
        private final String name;
        
        EnsembleType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
