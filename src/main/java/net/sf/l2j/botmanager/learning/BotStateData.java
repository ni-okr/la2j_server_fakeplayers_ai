package net.sf.l2j.botmanager.learning;

/**
 * Данные состояния бота
 * 
 * Этот класс содержит информацию о текущем состоянии бота,
 * включая здоровье, мана, уровень, опыт и позицию.
 * 
 * @author AI Assistant
 * @version 3.1
 * @since 2025-09-26
 */
public class BotStateData {
    
    // ==================== ПОЛЯ ====================
    
    /**
     * Текущее здоровье (0.0 - 1.0)
     */
    private double health;
    
    /**
     * Текущая мана (0.0 - 1.0)
     */
    private double mana;
    
    /**
     * Уровень бота
     */
    private int level;
    
    /**
     * Опыт бота (0.0 - 1.0)
     */
    private double experience;
    
    /**
     * X координата
     */
    private double x;
    
    /**
     * Y координата
     */
    private double y;
    
    /**
     * Z координата
     */
    private double z;
    
    /**
     * Временная метка
     */
    private long timestamp;
    
    // ==================== КОНСТРУКТОР ====================
    
    /**
     * Конструктор по умолчанию
     */
    public BotStateData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Конструктор с параметрами
     * 
     * @param health здоровье
     * @param mana мана
     * @param level уровень
     * @param experience опыт
     * @param x X координата
     * @param y Y координата
     * @param z Z координата
     */
    public BotStateData(double health, double mana, int level, double experience, 
                       double x, double y, double z) {
        this.health = health;
        this.mana = mana;
        this.level = level;
        this.experience = experience;
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = System.currentTimeMillis();
    }
    
    // ==================== GETTERS И SETTERS ====================
    
    public double getHealth() { return health; }
    public void setHealth(double health) { this.health = health; }
    
    public double getMana() { return mana; }
    public void setMana(double mana) { this.mana = mana; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public double getExperience() { return experience; }
    public void setExperience(double experience) { this.experience = experience; }
    
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
