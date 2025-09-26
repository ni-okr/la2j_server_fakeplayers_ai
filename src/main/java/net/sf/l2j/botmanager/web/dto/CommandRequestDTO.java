package net.sf.l2j.botmanager.web.dto;

/**
 * DTO для запроса выполнения команды через REST API
 */
public class CommandRequestDTO {
    
    private String command;
    private String[] args;
    private int executorId;
    private String executorName;
    
    /**
     * Конструктор по умолчанию
     */
    public CommandRequestDTO() {
    }
    
    /**
     * Конструктор с параметрами
     */
    public CommandRequestDTO(String command, String[] args, int executorId, String executorName) {
        this.command = command;
        this.args = args;
        this.executorId = executorId;
        this.executorName = executorName;
    }
    
    // Getters and Setters
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String[] getArgs() {
        return args;
    }
    
    public void setArgs(String[] args) {
        this.args = args;
    }
    
    public int getExecutorId() {
        return executorId;
    }
    
    public void setExecutorId(int executorId) {
        this.executorId = executorId;
    }
    
    public String getExecutorName() {
        return executorName;
    }
    
    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }
    
    /**
     * Получить полную строку команды
     * @return строка команды с аргументами
     */
    public String getFullCommand() {
        if (args == null || args.length == 0) {
            return command;
        }
        
        StringBuilder sb = new StringBuilder(command);
        for (String arg : args) {
            sb.append(" ").append(arg);
        }
        return sb.toString();
    }
    
    /**
     * Проверить валидность запроса
     * @return true если запрос валиден
     */
    public boolean isValid() {
        return command != null && !command.trim().isEmpty() && executorId > 0;
    }
}
