package net.sf.l2j.botmanager.ai;

import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.actions.ActionType;
import java.util.List;
import java.util.ArrayList;

/**
 * Решение, принятое движком принятия решений.
 * Содержит информацию о выбранном поведении и последовательности действий.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class Decision {
    
    /** Выбранный тип поведения */
    private BehaviorType selectedBehavior;
    
    /** Последовательность действий */
    private List<ActionType> actionSequence;
    
    /** Приоритет решения (0.0 - 1.0) */
    private double priority;
    
    /** Уверенность в решении (0.0 - 1.0) */
    private double confidence;
    
    /** Время принятия решения */
    private long decisionTime;
    
    /** Дополнительные параметры */
    private String parameters;
    
    /**
     * Конструктор по умолчанию.
     */
    public Decision() {
        this.selectedBehavior = null;
        this.actionSequence = new ArrayList<>();
        this.priority = 0.5;
        this.confidence = 0.5;
        this.decisionTime = System.currentTimeMillis();
        this.parameters = "";
    }
    
    /**
     * Конструктор с параметрами.
     * 
     * @param selectedBehavior выбранное поведение
     * @param actionSequence последовательность действий
     * @param priority приоритет решения
     * @param confidence уверенность в решении
     */
    public Decision(BehaviorType selectedBehavior, List<ActionType> actionSequence, 
                   double priority, double confidence) {
        this.selectedBehavior = selectedBehavior;
        this.actionSequence = actionSequence != null ? new ArrayList<>(actionSequence) : new ArrayList<>();
        this.priority = Math.max(0.0, Math.min(1.0, priority));
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
        this.decisionTime = System.currentTimeMillis();
        this.parameters = "";
    }
    
    // Геттеры и сеттеры
    
    public BehaviorType getSelectedBehavior() {
        return selectedBehavior;
    }
    
    public void setSelectedBehavior(BehaviorType selectedBehavior) {
        this.selectedBehavior = selectedBehavior;
    }
    
    public List<ActionType> getActionSequence() {
        return actionSequence;
    }
    
    public void setActionSequence(List<ActionType> actionSequence) {
        this.actionSequence = actionSequence != null ? new ArrayList<>(actionSequence) : new ArrayList<>();
    }
    
    public double getPriority() {
        return priority;
    }
    
    public void setPriority(double priority) {
        this.priority = Math.max(0.0, Math.min(1.0, priority));
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
    }
    
    public long getDecisionTime() {
        return decisionTime;
    }
    
    public void setDecisionTime(long decisionTime) {
        this.decisionTime = decisionTime;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters != null ? parameters : "";
    }
    
    /**
     * Добавление действия в последовательность.
     * 
     * @param action действие для добавления
     */
    public void addAction(ActionType action) {
        if (action != null) {
            this.actionSequence.add(action);
        }
    }
    
    /**
     * Добавление действия в начало последовательности.
     * 
     * @param action действие для добавления
     */
    public void addActionFirst(ActionType action) {
        if (action != null) {
            this.actionSequence.add(0, action);
        }
    }
    
    /**
     * Очистка последовательности действий.
     */
    public void clearActions() {
        this.actionSequence.clear();
    }
    
    /**
     * Получение количества действий в последовательности.
     * 
     * @return количество действий
     */
    public int getActionCount() {
        return this.actionSequence.size();
    }
    
    /**
     * Проверка наличия действий в последовательности.
     * 
     * @return true если есть действия
     */
    public boolean hasActions() {
        return !this.actionSequence.isEmpty();
    }
    
    /**
     * Получение первого действия в последовательности.
     * 
     * @return первое действие или null если последовательность пуста
     */
    public ActionType getFirstAction() {
        return this.actionSequence.isEmpty() ? null : this.actionSequence.get(0);
    }
    
    /**
     * Получение последнего действия в последовательности.
     * 
     * @return последнее действие или null если последовательность пуста
     */
    public ActionType getLastAction() {
        return this.actionSequence.isEmpty() ? null : this.actionSequence.get(this.actionSequence.size() - 1);
    }
    
    /**
     * Проверка валидности решения.
     * 
     * @return true если решение валидно
     */
    public boolean isValid() {
        return selectedBehavior != null && !actionSequence.isEmpty() && confidence > 0.0;
    }
    
    /**
     * Обновление времени принятия решения.
     */
    public void updateTimestamp() {
        this.decisionTime = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return String.format("Decision{behavior=%s, actions=%d, priority=%.2f, confidence=%.2f}",
                selectedBehavior, actionSequence.size(), priority, confidence);
    }
}
