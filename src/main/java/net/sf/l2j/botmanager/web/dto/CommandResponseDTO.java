package net.sf.l2j.botmanager.web.dto;

/**
 * DTO для ответа выполнения команды через REST API
 */
public class CommandResponseDTO {
    
    private boolean success;
    private String message;
    private Object data;
    private long executionTime;
    private String command;
    private int executorId;
    private long timestamp;
    
    /**
     * Конструктор по умолчанию
     */
    public CommandResponseDTO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Конструктор с параметрами
     */
    public CommandResponseDTO(boolean success, String message, Object data, long executionTime, 
                             String command, int executorId) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.executionTime = executionTime;
        this.command = command;
        this.executorId = executorId;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Создать успешный ответ
     */
    public static CommandResponseDTO success(String message) {
        return new CommandResponseDTO(true, message, null, 0, null, 0);
    }
    
    /**
     * Создать успешный ответ с данными
     */
    public static CommandResponseDTO success(String message, Object data) {
        return new CommandResponseDTO(true, message, data, 0, null, 0);
    }
    
    /**
     * Создать неуспешный ответ
     */
    public static CommandResponseDTO failure(String message) {
        return new CommandResponseDTO(false, message, null, 0, null, 0);
    }
    
    /**
     * Создать ответ с временем выполнения
     */
    public static CommandResponseDTO withTime(boolean success, String message, long executionTime) {
        return new CommandResponseDTO(success, message, null, executionTime, null, 0);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public int getExecutorId() {
        return executorId;
    }
    
    public void setExecutorId(int executorId) {
        this.executorId = executorId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Получить строковое представление ответа
     * @return строка ответа
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
        if (command != null) {
            sb.append(" [").append(command).append("]");
        }
        return sb.toString();
    }
}
