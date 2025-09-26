package net.sf.l2j.botmanager.learning;

/**
 * Данные для обучения нейронной сети
 * 
 * Этот класс содержит все необходимые данные для обучения
 * нейронной сети бота, включая состояние бота, окружение,
 * историю действий и ожидаемые результаты.
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class TrainingData {
    
    // ==================== ПОЛЯ ====================
    
    /**
     * Состояние бота
     */
    private BotStateData botState;
    
    /**
     * Данные окружения
     */
    private EnvironmentData environmentData;
    
    /**
     * История действий
     */
    private ActionHistoryData actionHistory;
    
    /**
     * Метрики производительности
     */
    private PerformanceMetrics performanceMetrics;
    
    /**
     * Ожидаемые действия
     */
    private ExpectedActions expectedActions;
    
    /**
     * Ожидаемые поведения
     */
    private ExpectedBehaviors expectedBehaviors;
    
    /**
     * Ожидаемые результаты
     */
    private ExpectedOutcomes expectedOutcomes;
    
    /**
     * Временная метка
     */
    private long timestamp;
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор по умолчанию
     */
    public TrainingData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Конструктор с параметрами
     * 
     * @param botState состояние бота
     * @param environmentData данные окружения
     * @param actionHistory история действий
     */
    public TrainingData(BotStateData botState, EnvironmentData environmentData, 
                       ActionHistoryData actionHistory) {
        this.botState = botState;
        this.environmentData = environmentData;
        this.actionHistory = actionHistory;
        this.timestamp = System.currentTimeMillis();
    }
    
    // ==================== GETTERS И SETTERS ====================
    
    public BotStateData getBotState() { return botState; }
    public void setBotState(BotStateData botState) { this.botState = botState; }
    
    public EnvironmentData getEnvironmentData() { return environmentData; }
    public void setEnvironmentData(EnvironmentData environmentData) { this.environmentData = environmentData; }
    
    public ActionHistoryData getActionHistory() { return actionHistory; }
    public void setActionHistory(ActionHistoryData actionHistory) { this.actionHistory = actionHistory; }
    
    public PerformanceMetrics getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    
    public ExpectedActions getExpectedActions() { return expectedActions; }
    public void setExpectedActions(ExpectedActions expectedActions) { this.expectedActions = expectedActions; }
    
    public ExpectedBehaviors getExpectedBehaviors() { return expectedBehaviors; }
    public void setExpectedBehaviors(ExpectedBehaviors expectedBehaviors) { this.expectedBehaviors = expectedBehaviors; }
    
    public ExpectedOutcomes getExpectedOutcomes() { return expectedOutcomes; }
    public void setExpectedOutcomes(ExpectedOutcomes expectedOutcomes) { this.expectedOutcomes = expectedOutcomes; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
