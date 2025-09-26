package net.sf.l2j.botmanager.management.commands;

import net.sf.l2j.botmanager.utils.Logger;

/**
 * Команда удаления бота
 */
public class RemoveBotCommand implements IAdminCommand {
    
    private static final Logger logger = Logger.getLogger(RemoveBotCommand.class);
    
    @Override
    public String getCommandName() {
        return "removebot";
    }
    
    @Override
    public String getDescription() {
        return "Remove a bot";
    }
    
    @Override
    public String getSyntax() {
        return "removebot <botId>";
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
            
            // TODO: Implement actual bot removal
            // boolean success = botManagement.removeBot(botId);
            
            logger.info("Bot removal requested: " + botId + " by " + executorId);
            
            return CommandResult.success("Bot removal request submitted for bot ID: " + botId);
            
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid bot ID: " + args[0]);
        } catch (Exception e) {
            logger.error("Error in RemoveBotCommand: " + e.getMessage());
            return CommandResult.failure("Error removing bot: " + e.getMessage());
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
