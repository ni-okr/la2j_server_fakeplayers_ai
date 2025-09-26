package net.sf.l2j.botmanager.ai;

/**
 * Состояние ядра ИИ для конкретного бота.
 * 
 * Содержит информацию о активности, приоритете и времени последнего решения.
 * 
 * @author AI Bot System
 * @version 1.0
 */
public class AICoreState {
    
    private boolean active;
    private double priority;
    private long lastDecisionTime;
    
    /**
     * Конструктор.
     */
    public AICoreState() {
        this.active = false;
        this.priority = 0.0;
        this.lastDecisionTime = 0L;
    }
    
    /**
     * Проверить активность ядра.
     * 
     * @return true если ядро активно
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Установить активность ядра.
     * 
     * @param active активность
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Получить приоритет ядра.
     * 
     * @return приоритет (0.0 - 1.0)
     */
    public double getPriority() {
        return priority;
    }
    
    /**
     * Установить приоритет ядра.
     * 
     * @param priority приоритет (0.0 - 1.0)
     */
    public void setPriority(double priority) {
        this.priority = Math.max(0.0, Math.min(1.0, priority));
    }
    
    /**
     * Получить время последнего решения.
     * 
     * @return время последнего решения в миллисекундах
     */
    public long getLastDecisionTime() {
        return lastDecisionTime;
    }
    
    /**
     * Установить время последнего решения.
     * 
     * @param lastDecisionTime время последнего решения в миллисекундах
     */
    public void setLastDecisionTime(long lastDecisionTime) {
        this.lastDecisionTime = lastDecisionTime;
    }
    
    @Override
    public String toString() {
        return String.format("AICoreState{active=%s, priority=%.2f, lastDecisionTime=%d}",
            active, priority, lastDecisionTime);
    }
}
