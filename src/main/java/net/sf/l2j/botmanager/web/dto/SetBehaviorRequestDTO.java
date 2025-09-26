package net.sf.l2j.botmanager.web.dto;

import net.sf.l2j.botmanager.behaviors.BehaviorType;

/**
 * DTO для запроса установки поведения через REST API
 */
public class SetBehaviorRequestDTO {
    
    private BehaviorType behaviorType;
    
    /**
     * Конструктор по умолчанию
     */
    public SetBehaviorRequestDTO() {
    }
    
    /**
     * Конструктор с параметрами
     */
    public SetBehaviorRequestDTO(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }
    
    // Getters and Setters
    public BehaviorType getBehaviorType() {
        return behaviorType;
    }
    
    public void setBehaviorType(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }
    
    /**
     * Проверить валидность запроса
     * @return true если запрос валиден
     */
    public boolean isValid() {
        return behaviorType != null;
    }
    
    /**
     * Получить строковое представление запроса
     * @return строка запроса
     */
    @Override
    public String toString() {
        return String.format("SetBehaviorRequest{behaviorType=%s}", behaviorType);
    }
}
