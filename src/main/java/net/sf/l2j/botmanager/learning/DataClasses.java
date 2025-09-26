package net.sf.l2j.botmanager.learning;

import java.util.Map;

/**
 * Вспомогательные классы данных для машинного обучения
 * 
 * Этот файл содержит все необходимые классы данных
 * для системы машинного обучения ботов.
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */

/**
 * Данные окружения
 */
class EnvironmentData {
    private int nearbyEnemies;
    private int nearbyAllies;
    private int nearbyNpcs;
    private double timeOfDay;
    private double weather;
    private double dangerLevel;
    private long timestamp;
    
    public EnvironmentData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getNearbyEnemies() { return nearbyEnemies; }
    public void setNearbyEnemies(int nearbyEnemies) { this.nearbyEnemies = nearbyEnemies; }
    
    public int getNearbyAllies() { return nearbyAllies; }
    public void setNearbyAllies(int nearbyAllies) { this.nearbyAllies = nearbyAllies; }
    
    public int getNearbyNpcs() { return nearbyNpcs; }
    public void setNearbyNpcs(int nearbyNpcs) { this.nearbyNpcs = nearbyNpcs; }
    
    public double getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(double timeOfDay) { this.timeOfDay = timeOfDay; }
    
    public double getWeather() { return weather; }
    public void setWeather(double weather) { this.weather = weather; }
    
    public double getDangerLevel() { return dangerLevel; }
    public void setDangerLevel(double dangerLevel) { this.dangerLevel = dangerLevel; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Данные истории действий
 */
class ActionHistoryData {
    private int totalActions;
    private int successfulActions;
    private int failedActions;
    private double averageExecutionTime;
    private long lastActionTime;
    private long timestamp;
    
    public ActionHistoryData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getTotalActions() { return totalActions; }
    public void setTotalActions(int totalActions) { this.totalActions = totalActions; }
    
    public int getSuccessfulActions() { return successfulActions; }
    public void setSuccessfulActions(int successfulActions) { this.successfulActions = successfulActions; }
    
    public int getFailedActions() { return failedActions; }
    public void setFailedActions(int failedActions) { this.failedActions = failedActions; }
    
    public double getAverageExecutionTime() { return averageExecutionTime; }
    public void setAverageExecutionTime(double averageExecutionTime) { this.averageExecutionTime = averageExecutionTime; }
    
    public long getLastActionTime() { return lastActionTime; }
    public void setLastActionTime(long lastActionTime) { this.lastActionTime = lastActionTime; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Метрики производительности
 */
class PerformanceMetrics {
    private double successRate;
    private double efficiency;
    private double responseTime;
    private double resourceUsage;
    private long timestamp;
    
    public PerformanceMetrics() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public double getEfficiency() { return efficiency; }
    public void setEfficiency(double efficiency) { this.efficiency = efficiency; }
    
    public double getResponseTime() { return responseTime; }
    public void setResponseTime(double responseTime) { this.responseTime = responseTime; }
    
    public double getResourceUsage() { return resourceUsage; }
    public void setResourceUsage(double resourceUsage) { this.resourceUsage = resourceUsage; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Ожидаемые действия
 */
class ExpectedActions {
    private int actionType;
    private double priority;
    private double duration;
    private double successProbability;
    private long timestamp;
    
    public ExpectedActions() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getActionType() { return actionType; }
    public void setActionType(int actionType) { this.actionType = actionType; }
    
    public double getPriority() { return priority; }
    public void setPriority(double priority) { this.priority = priority; }
    
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    
    public double getSuccessProbability() { return successProbability; }
    public void setSuccessProbability(double successProbability) { this.successProbability = successProbability; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Ожидаемые поведения
 */
class ExpectedBehaviors {
    private int behaviorType;
    private double intensity;
    private double duration;
    private double successProbability;
    private long timestamp;
    
    public ExpectedBehaviors() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getBehaviorType() { return behaviorType; }
    public void setBehaviorType(int behaviorType) { this.behaviorType = behaviorType; }
    
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
    
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    
    public double getSuccessProbability() { return successProbability; }
    public void setSuccessProbability(double successProbability) { this.successProbability = successProbability; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Ожидаемые результаты
 */
class ExpectedOutcomes {
    private double successRate;
    private double efficiency;
    private double reward;
    private double risk;
    private long timestamp;
    
    public ExpectedOutcomes() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public double getEfficiency() { return efficiency; }
    public void setEfficiency(double efficiency) { this.efficiency = efficiency; }
    
    public double getReward() { return reward; }
    public void setReward(double reward) { this.reward = reward; }
    
    public double getRisk() { return risk; }
    public void setRisk(double risk) { this.risk = risk; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Обратная связь обучения
 */
class LearningFeedback {
    private ActionFeedback actionFeedback;
    private BehaviorFeedback behaviorFeedback;
    private PerformanceData performanceData;
    private boolean success;
    private double performanceScore;
    private long timestamp;
    
    public LearningFeedback() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public ActionFeedback getActionFeedback() { return actionFeedback; }
    public void setActionFeedback(ActionFeedback actionFeedback) { this.actionFeedback = actionFeedback; }
    
    public BehaviorFeedback getBehaviorFeedback() { return behaviorFeedback; }
    public void setBehaviorFeedback(BehaviorFeedback behaviorFeedback) { this.behaviorFeedback = behaviorFeedback; }
    
    public PerformanceData getPerformanceData() { return performanceData; }
    public void setPerformanceData(PerformanceData performanceData) { this.performanceData = performanceData; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Данные производительности
 */
class PerformanceData {
    private double cpuUsage;
    private double memoryUsage;
    private double networkLatency;
    private double responseTime;
    private long timestamp;
    
    public PerformanceData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public double getNetworkLatency() { return networkLatency; }
    public void setNetworkLatency(double networkLatency) { this.networkLatency = networkLatency; }
    
    public double getResponseTime() { return responseTime; }
    public void setResponseTime(double responseTime) { this.responseTime = responseTime; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Контекст предсказания
 */
class PredictionContext {
    private BotStateData botState;
    private EnvironmentData environmentData;
    private TargetData targetData;
    private HistoricalData historicalData;
    private long timestamp;
    
    public PredictionContext() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public BotStateData getBotState() { return botState; }
    public void setBotState(BotStateData botState) { this.botState = botState; }
    
    public EnvironmentData getEnvironmentData() { return environmentData; }
    public void setEnvironmentData(EnvironmentData environmentData) { this.environmentData = environmentData; }
    
    public TargetData getTargetData() { return targetData; }
    public void setTargetData(TargetData targetData) { this.targetData = targetData; }
    
    public HistoricalData getHistoricalData() { return historicalData; }
    public void setHistoricalData(HistoricalData historicalData) { this.historicalData = historicalData; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Данные о целях
 */
class TargetData {
    private int targetCount;
    private double averageDistance;
    private double averageLevel;
    private double threatLevel;
    private long timestamp;
    
    public TargetData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getTargetCount() { return targetCount; }
    public void setTargetCount(int targetCount) { this.targetCount = targetCount; }
    
    public double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(double averageDistance) { this.averageDistance = averageDistance; }
    
    public double getAverageLevel() { return averageLevel; }
    public void setAverageLevel(double averageLevel) { this.averageLevel = averageLevel; }
    
    public double getThreatLevel() { return threatLevel; }
    public void setThreatLevel(double threatLevel) { this.threatLevel = threatLevel; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Исторические данные
 */
class HistoricalData {
    private double recentSuccessRate;
    private double averagePerformance;
    private double trendDirection;
    private double volatility;
    private long timestamp;
    
    public HistoricalData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public double getRecentSuccessRate() { return recentSuccessRate; }
    public void setRecentSuccessRate(double recentSuccessRate) { this.recentSuccessRate = recentSuccessRate; }
    
    public double getAveragePerformance() { return averagePerformance; }
    public void setAveragePerformance(double averagePerformance) { this.averagePerformance = averagePerformance; }
    
    public double getTrendDirection() { return trendDirection; }
    public void setTrendDirection(double trendDirection) { this.trendDirection = trendDirection; }
    
    public double getVolatility() { return volatility; }
    public void setVolatility(double volatility) { this.volatility = volatility; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Предсказание действия
 */
class ActionPrediction {
    private int botId;
    private int actionType;
    private double priority;
    private double confidence;
    private double duration;
    private double intensity;
    private Map<String, Double> additionalParameters;
    private long timestamp;
    
    public ActionPrediction() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getBotId() { return botId; }
    public void setBotId(int botId) { this.botId = botId; }
    
    public int getActionType() { return actionType; }
    public void setActionType(int actionType) { this.actionType = actionType; }
    
    public double getPriority() { return priority; }
    public void setPriority(double priority) { this.priority = priority; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
    
    public Map<String, Double> getAdditionalParameters() { return additionalParameters; }
    public void setAdditionalParameters(Map<String, Double> additionalParameters) { this.additionalParameters = additionalParameters; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * Обработанные данные
 */
class ProcessedData {
    private double[][] inputs;
    private double[][] targets;
    private long timestamp;
    
    public ProcessedData(double[][] inputs, double[][] targets) {
        this.inputs = inputs;
        this.targets = targets;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public double[][] getInputs() { return inputs; }
    public void setInputs(double[][] inputs) { this.inputs = inputs; }
    
    public double[][] getTargets() { return targets; }
    public void setTargets(double[][] targets) { this.targets = targets; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
