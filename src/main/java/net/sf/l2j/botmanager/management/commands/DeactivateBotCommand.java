package net.sf.l2j.botmanager.management.commands;

import net.sf.l2j.botmanager.utils.Logger;

/**
 * Команда деактивации бота
 */
public class DeactivateBotCommand implements IAdminCommand {
    
    private static final Logger logger = Logger.getLogger(DeactivateBotCommand.class);
    
    @Override
    public String getCommandName() {
        return "deactivatebot";
    }
    
    @Override
    public String getDescription() {
        return "Deactivate a bot";
    }
    
    @Override
    public String getSyntax() {
        return "deactivatebot <botId>";
    }
    
    @Override
    public CommandResult execute(String[] args, int executorId) {
        if (args.length < 1) {
            return CommandResult.failure("Usage: " + getSyntax());
        }
        
        try {
            int botId = Integer.parseInt(args[0]);
            
            if (botId <= 0) {
                return CommandResult.failure("Invalid bot ID: " + botId);
            }
            
            // TODO: Implement actual bot deactivation
            // boolean success = botManagement.deactivateBot(botId);
            
            logger.info("Bot deactivation requested: " + botId + " by " + executorId);
            
            return CommandResult.success("Bot deactivation request submitted for bot ID: " + botId);
            
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid bot ID: " + args[0]);
        } catch (Exception e) {
            logger.error("Error in DeactivateBotCommand: " + e.getMessage());
            return CommandResult.failure("Error deactivating bot: " + e.getMessage());
        }
    }
    
    @Override
    public boolean hasPermission(int executorId) {
        // TODO: Implement permission checking
        return true;
    }
    
    @Override
    public int getRequiredAccessLevel() {
        return 1; // Basic admin level
    }
}
