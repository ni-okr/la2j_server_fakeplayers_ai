package net.sf.l2j.botmanager.learning;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * Событие обучения бота.
 * Содержит информацию о событии, которое может быть использовано для обучения и адаптации поведения бота.
 * 
 * @author ni-okr
 * @version 1.0
 * @since 2025-09-26
 */
public class LearningEvent {
    
    /** Уникальный идентификатор бота, для которого произошло событие */
    private final int botId;
    
    /** Тип события обучения (например, "action_success", "behavior_completion", "learning_trigger") */
    private final String eventType;
    
    /** Параметры события в виде ключ-значение пары */
    private final Map<String, Object> parameters;
    
    /** Значение обучения (от -1.0 до 1.0, где положительные значения указывают на успех) */
    private final double learningValue;
    
    /** Временная метка создания события */
    private final long timestamp;
    
    /** Дополнительное описание события */
    private final String description;
    
    /**
     * Конструктор события обучения.
     * 
     * @param botId идентификатор бота
     * @param eventType тип события
     * @param parameters параметры события
     * @param learningValue значение обучения
     */
    public LearningEvent(int botId, String eventType, Map<String, Object> parameters, double learningValue) {
        this.botId = botId;
        this.eventType = eventType != null ? eventType : "unknown";
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        this.learningValue = Math.max(-1.0, Math.min(1.0, learningValue)); // Ограничиваем значение от -1.0 до 1.0
        this.timestamp = System.currentTimeMillis();
        this.description = generateDescription();
    }
    
    /**
     * Конструктор события обучения с описанием.
     * 
     * @param botId идентификатор бота
     * @param eventType тип события
     * @param parameters параметры события
     * @param learningValue значение обучения
     * @param description описание события
     */
    public LearningEvent(int botId, String eventType, Map<String, Object> parameters, 
                        double learningValue, String description) {
        this.botId = botId;
        this.eventType = eventType != null ? eventType : "unknown";
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        this.learningValue = Math.max(-1.0, Math.min(1.0, learningValue));
        this.timestamp = System.currentTimeMillis();
        this.description = description != null ? description : generateDescription();
    }
    
    /**
     * Получить идентификатор бота.
     * 
     * @return идентификатор бота
     */
    public int getBotId() {
        return botId;
    }
    
    /**
     * Получить тип события.
     * 
     * @return тип события
     */
    public String getEventType() {
        return eventType;
    }
    
    /**
     * Получить параметры события.
     * 
     * @return копия карты параметров
     */
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }
    
    /**
     * Получить значение параметра по ключу.
     * 
     * @param key ключ параметра
     * @return значение параметра или null, если не найден
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    /**
     * Получить значение параметра по ключу с приведением к указанному типу.
     * 
     * @param key ключ параметра
     * @param type класс типа для приведения
     * @param <T> тип параметра
     * @return значение параметра или null, если не найден или не может быть приведен
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, Class<T> type) {
        Object value = parameters.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Получить значение обучения.
     * 
     * @return значение обучения (от -1.0 до 1.0)
     */
    public double getLearningValue() {
        return learningValue;
    }
    
    /**
     * Получить временную метку создания события.
     * 
     * @return временная метка в миллисекундах
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Получить описание события.
     * 
     * @return описание события
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Проверить, является ли событие положительным (успешным).
     * 
     * @return true, если значение обучения больше 0
     */
    public boolean isPositive() {
        return learningValue > 0.0;
    }
    
    /**
     * Проверить, является ли событие отрицательным (неудачным).
     * 
     * @return true, если значение обучения меньше 0
     */
    public boolean isNegative() {
        return learningValue < 0.0;
    }
    
    /**
     * Проверить, является ли событие нейтральным.
     * 
     * @return true, если значение обучения равно 0
     */
    public boolean isNeutral() {
        return learningValue == 0.0;
    }
    
    /**
     * Получить возраст события в миллисекундах.
     * 
     * @return возраст события в миллисекундах
     */
    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }
    
    /**
     * Проверить, является ли событие устаревшим.
     * 
     * @param maxAge максимальный возраст в миллисекундах
     * @return true, если событие старше указанного возраста
     */
    public boolean isStale(long maxAge) {
        return getAge() > maxAge;
    }
    
    /**
     * Создать копию события с новым значением обучения.
     * 
     * @param newLearningValue новое значение обучения
     * @return новая копия события
     */
    public LearningEvent withLearningValue(double newLearningValue) {
        return new LearningEvent(botId, eventType, parameters, newLearningValue, description);
    }
    
    /**
     * Создать копию события с новым параметром.
     * 
     * @param key ключ параметра
     * @param value значение параметра
     * @return новая копия события с добавленным параметром
     */
    public LearningEvent withParameter(String key, Object value) {
        Map<String, Object> newParameters = new HashMap<>(parameters);
        newParameters.put(key, value);
        return new LearningEvent(botId, eventType, newParameters, learningValue, description);
    }
    
    /**
     * Генерировать автоматическое описание события на основе его параметров.
     * 
     * @return сгенерированное описание
     */
    private String generateDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("Learning event '").append(eventType).append("' for bot ").append(botId);
        desc.append(" (value: ").append(String.format("%.2f", learningValue)).append(")");
        
        if (!parameters.isEmpty()) {
            desc.append(" with parameters: ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (!first) desc.append(", ");
                desc.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }
        
        return desc.toString();
    }
    
    /**
     * Получить строковое представление события.
     * 
     * @return строковое представление события
     */
    @Override
    public String toString() {
        return String.format(
            "LearningEvent{botId=%d, eventType='%s', learningValue=%.2f, timestamp=%d, parameters=%d}",
            botId, eventType, learningValue, timestamp, parameters.size()
        );
    }
    
    /**
     * Проверить равенство событий.
     * 
     * @param obj объект для сравнения
     * @return true, если события равны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LearningEvent that = (LearningEvent) obj;
        return botId == that.botId &&
               Double.compare(that.learningValue, learningValue) == 0 &&
               timestamp == that.timestamp &&
               eventType.equals(that.eventType) &&
               parameters.equals(that.parameters);
    }
    
    /**
     * Получить хеш-код события.
     * 
     * @return хеш-код события
     */
    @Override
    public int hashCode() {
        int result = botId;
        result = 31 * result + eventType.hashCode();
        result = 31 * result + parameters.hashCode();
        result = 31 * result + Double.hashCode(learningValue);
        result = 31 * result + Long.hashCode(timestamp);
        return result;
    }
}
