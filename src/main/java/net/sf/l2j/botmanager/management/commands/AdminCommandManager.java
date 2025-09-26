package net.sf.l2j.botmanager.management.commands;

import net.sf.l2j.botmanager.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер команд администратора
 * Управляет регистрацией и выполнением команд
 */
public class AdminCommandManager {
    
    private static final Logger logger = Logger.getLogger(AdminCommandManager.class);
    private static AdminCommandManager instance;
    
    private final Map<String, IAdminCommand> commands;
    private final Map<Integer, Integer> accessLevels;
    private boolean initialized;
    
    /**
     * Конструктор
     */
    private AdminCommandManager() {
        this.commands = new ConcurrentHashMap<>();
        this.accessLevels = new ConcurrentHashMap<>();
        this.initialized = false;
    }
    
    /**
     * Получить экземпляр менеджера
     * @return экземпляр менеджера
     */
    public static synchronized AdminCommandManager getInstance() {
        if (instance == null) {
            instance = new AdminCommandManager();
        }
        return instance;
    }
    
    /**
     * Инициализировать менеджер команд
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        
        logger.info("Initializing Admin Command Manager...");
        
        // Регистрируем базовые команды
        registerCommand(new CreateBotCommand());
        registerCommand(new RemoveBotCommand());
        registerCommand(new ListBotsCommand());
        registerCommand(new BotInfoCommand());
        registerCommand(new SetBehaviorCommand());
        registerCommand(new ActivateBotCommand());
        registerCommand(new DeactivateBotCommand());
        registerCommand(new BotStatisticsCommand());
        registerCommand(new HelpCommand());
        
        initialized = true;
        logger.info("Admin Command Manager initialized with " + commands.size() + " commands");
    }
    
    /**
     * Завершить работу менеджера
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        logger.info("Shutting down Admin Command Manager...");
        commands.clear();
        accessLevels.clear();
        initialized = false;
        logger.info("Admin Command Manager shutdown complete");
    }
    
    /**
     * Зарегистрировать команду
     * @param command команда для регистрации
     */
    public void registerCommand(IAdminCommand command) {
        if (command == null) {
            logger.warn("Cannot register null command");
            return;
        }
        
        String commandName = command.getCommandName().toLowerCase();
        if (commands.containsKey(commandName)) {
            logger.warn("Command '" + commandName + "' is already registered");
            return;
        }
        
        commands.put(commandName, command);
        logger.info("Registered command: " + commandName);
    }
    
    /**
     * Отменить регистрацию команды
     * @param commandName имя команды
     */
    public void unregisterCommand(String commandName) {
        if (commandName == null) {
            return;
        }
        
        String name = commandName.toLowerCase();
        IAdminCommand removed = commands.remove(name);
        if (removed != null) {
            logger.info("Unregistered command: " + name);
        }
    }
    
    /**
     * Выполнить команду
     * @param commandLine строка команды
     * @param executorId ID исполнителя
     * @return результат выполнения
     */
    public CommandResult executeCommand(String commandLine, int executorId) {
        if (!initialized) {
            return CommandResult.failure("Command manager not initialized");
        }
        
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return CommandResult.failure("Empty command");
        }
        
        String[] parts = commandLine.trim().split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        
        IAdminCommand command = commands.get(commandName);
        if (command == null) {
            return CommandResult.failure("Unknown command: " + commandName);
        }
        
        if (!command.hasPermission(executorId)) {
            return CommandResult.failure("Insufficient permissions for command: " + commandName);
        }
        
        long startTime = System.currentTimeMillis();
        try {
            CommandResult result = command.execute(args, executorId);
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (result.getExecutionTime() == 0) {
                result = CommandResult.withTime(result.isSuccess(), result.getMessage(), executionTime);
            }
            
            logger.info("Command '" + commandName + "' executed by " + executorId + " in " + executionTime + "ms");
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Error executing command '" + commandName + "': " + e.getMessage());
            return CommandResult.withTime(false, "Error executing command: " + e.getMessage(), executionTime);
        }
    }
    
    /**
     * Получить команду по имени
     * @param commandName имя команды
     * @return команда или null
     */
    public IAdminCommand getCommand(String commandName) {
        return commands.get(commandName.toLowerCase());
    }
    
    /**
     * Получить все зарегистрированные команды
     * @return множество имен команд
     */
    public Set<String> getRegisteredCommands() {
        return commands.keySet();
    }
    
    /**
     * Установить уровень доступа для пользователя
     * @param userId ID пользователя
     * @param accessLevel уровень доступа
     */
    public void setAccessLevel(int userId, int accessLevel) {
        accessLevels.put(userId, accessLevel);
        logger.info("Set access level " + accessLevel + " for user " + userId);
    }
    
    /**
     * Получить уровень доступа пользователя
     * @param userId ID пользователя
     * @return уровень доступа
     */
    public int getAccessLevel(int userId) {
        return accessLevels.getOrDefault(userId, 0);
    }
    
    /**
     * Проверить инициализирован ли менеджер
     * @return true если инициализирован
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Получить статистику команд
     * @return статистика команд
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCommands", commands.size());
        stats.put("registeredCommands", commands.keySet());
        stats.put("usersWithAccess", accessLevels.size());
        stats.put("initialized", initialized);
        return stats;
    }
}
