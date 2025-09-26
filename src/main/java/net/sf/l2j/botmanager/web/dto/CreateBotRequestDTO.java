package net.sf.l2j.botmanager.web.dto;

import net.sf.l2j.botmanager.core.BotType;

/**
 * DTO для запроса создания бота через REST API
 */
public class CreateBotRequestDTO {
    
    private BotType type;
    private String name;
    private int level;
    
    /**
     * Конструктор по умолчанию
     */
    public CreateBotRequestDTO() {
    }
    
    /**
     * Конструктор с параметрами
     */
    public CreateBotRequestDTO(BotType type, String name, int level) {
        this.type = type;
        this.name = name;
        this.level = level;
    }
    
    // Getters and Setters
    public BotType getType() {
        return type;
    }
    
    public void setType(BotType type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    /**
     * Проверить валидность запроса
     * @return true если запрос валиден
     */
    public boolean isValid() {
        return type != null && 
               name != null && 
               !name.trim().isEmpty() && 
               name.length() >= 3 && 
               name.length() <= 16 && 
               level >= 1 && 
               level <= 80;
    }
    
    /**
     * Получить строковое представление запроса
     * @return строка запроса
     */
    @Override
    public String toString() {
        return String.format("CreateBotRequest{type=%s, name='%s', level=%d}", 
                type, name, level);
    }
}
