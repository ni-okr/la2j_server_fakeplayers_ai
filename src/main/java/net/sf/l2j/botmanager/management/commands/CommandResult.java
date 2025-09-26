package net.sf.l2j.botmanager.management.commands;

/**
 * Класс для хранения результата выполнения команды
 */
public class CommandResult {
    
    private final boolean success;
    private final String message;
    private final Object data;
    private final long executionTime;
    
    /**
     * Конструктор
     */
    public CommandResult(boolean success, String message, Object data, long executionTime) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.executionTime = executionTime;
    }
    
    /**
     * Создать успешный результат
     */
    public static CommandResult success(String message) {
        return new CommandResult(true, message, null, 0);
    }
    
    /**
     * Создать успешный результат с данными
     */
    public static CommandResult success(String message, Object data) {
        return new CommandResult(true, message, data, 0);
    }
    
    /**
     * Создать неуспешный результат
     */
    public static CommandResult failure(String message) {
        return new CommandResult(false, message, null, 0);
    }
    
    /**
     * Создать результат с временем выполнения
     */
    public static CommandResult withTime(boolean success, String message, long executionTime) {
        return new CommandResult(success, message, null, executionTime);
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Object getData() {
        return data;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    /**
     * Получить строковое представление результата
     * @return строка результата
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(success ? "SUCCESS" : "FAILURE");
        if (message != null) {
            sb.append(": ").append(message);
        }
        if (executionTime > 0) {
            sb.append(" (executed in ").append(executionTime).append("ms)");
        }
        return sb.toString();
    }
}
