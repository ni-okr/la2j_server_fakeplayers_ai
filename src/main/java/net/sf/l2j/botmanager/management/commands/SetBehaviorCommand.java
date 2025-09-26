package net.sf.l2j.botmanager.management.commands;

import net.sf.l2j.botmanager.behaviors.BehaviorType;
import net.sf.l2j.botmanager.utils.Logger;

/**
 * Команда установки поведения бота
 */
public class SetBehaviorCommand implements IAdminCommand {
    
    private static final Logger logger = Logger.getLogger(SetBehaviorCommand.class);
    
    @Override
    public String getCommandName() {
        return "setbehavior";
    }
    
    @Override
    public String getDescription() {
        return "Set behavior for a bot";
    }
    
    @Override
    public String getSyntax() {
        return "setbehavior <botId> <behaviorType>";
    }
    
    @Override
    public CommandResult execute(String[] args, int executorId) {
        if (args.length < 2) {
            return CommandResult.failure("Usage: " + getSyntax());
        }
        
        try {
            int botId = Integer.parseInt(args[0]);
            String behaviorStr = args[1].toUpperCase();
            
            if (botId <= 0) {
                return CommandResult.failure("Invalid bot ID: " + botId);
            }
            
            BehaviorType behaviorType = BehaviorType.valueOf(behaviorStr);
            if (behaviorType == null) {
                return CommandResult.failure("Invalid behavior type: " + behaviorStr);
            }
            
            // TODO: Implement actual behavior setting
            // boolean success = botManagement.setBotBehavior(botId, behaviorType);
            
            logger.info("Behavior change requested: Bot " + botId + " -> " + behaviorType + " by " + executorId);
            
            return CommandResult.success("Behavior change request submitted: Bot " + botId + " -> " + behaviorType);
            
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid bot ID: " + args[0]);
        } catch (IllegalArgumentException e) {
            return CommandResult.failure("Invalid behavior type: " + args[1]);
        } catch (Exception e) {
            logger.error("Error in SetBehaviorCommand: " + e.getMessage());
            return CommandResult.failure("Error setting behavior: " + e.getMessage());
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
