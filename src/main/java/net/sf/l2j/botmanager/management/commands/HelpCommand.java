package net.sf.l2j.botmanager.management.commands;

import net.sf.l2j.botmanager.management.commands.AdminCommandManager;
import net.sf.l2j.botmanager.utils.Logger;

import java.util.Set;

/**
 * Команда помощи
 */
public class HelpCommand implements IAdminCommand {
    
    private static final Logger logger = Logger.getLogger(HelpCommand.class);
    
    @Override
    public String getCommandName() {
        return "help";
    }
    
    @Override
    public String getDescription() {
        return "Show available commands";
    }
    
    @Override
    public String getSyntax() {
        return "help [command]";
    }
    
    @Override
    public CommandResult execute(String[] args, int executorId) {
        try {
            AdminCommandManager commandManager = AdminCommandManager.getInstance();
            
            if (args.length > 0) {
                // Show help for specific command
                String commandName = args[0].toLowerCase();
                IAdminCommand command = commandManager.getCommand(commandName);
                
                if (command == null) {
                    return CommandResult.failure("Unknown command: " + commandName);
                }
                
                StringBuilder result = new StringBuilder();
                result.append("=== Command Help ===\n");
                result.append("Command: ").append(command.getCommandName()).append("\n");
                result.append("Description: ").append(command.getDescription()).append("\n");
                result.append("Syntax: ").append(command.getSyntax()).append("\n");
                result.append("Required Access Level: ").append(command.getRequiredAccessLevel()).append("\n");
                
                return CommandResult.success(result.toString());
                
            } else {
                // Show all available commands
                Set<String> commands = commandManager.getRegisteredCommands();
                
                StringBuilder result = new StringBuilder();
                result.append("=== Available Commands ===\n");
                result.append("Total commands: ").append(commands.size()).append("\n\n");
                
                for (String commandName : commands) {
                    IAdminCommand command = commandManager.getCommand(commandName);
                    if (command != null) {
                        result.append(commandName).append(" - ").append(command.getDescription()).append("\n");
                    }
                }
                
                result.append("\nUse 'help <command>' for detailed information about a specific command.");
                
                return CommandResult.success(result.toString(), commands);
            }
            
        } catch (Exception e) {
            logger.error("Error in HelpCommand: " + e.getMessage());
            return CommandResult.failure("Error showing help: " + e.getMessage());
        }
    }
    
    @Override
    public boolean hasPermission(int executorId) {
        // Everyone can use help command
        return true;
    }
    
    @Override
    public int getRequiredAccessLevel() {
        return 0; // Everyone can use help
    }
}
