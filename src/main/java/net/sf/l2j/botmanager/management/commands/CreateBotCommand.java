package net.sf.l2j.botmanager.management.commands;

import net.sf.l2j.botmanager.core.BotType;
import net.sf.l2j.botmanager.core.EnhancedFakePlayer;
import net.sf.l2j.botmanager.management.IBotManagementSystem;
import net.sf.l2j.botmanager.utils.Logger;

/**
 * Команда создания бота
 */
public class CreateBotCommand implements IAdminCommand {
    
    private static final Logger logger = Logger.getLogger(CreateBotCommand.class);
    private final IBotManagementSystem botManagement;
    
    public CreateBotCommand() {
        this.botManagement = null; // TODO: Inject dependency
    }
    
    @Override
    public String getCommandName() {
        return "createbot";
    }
    
    @Override
    public String getDescription() {
        return "Create a new bot";
    }
    
    @Override
    public String getSyntax() {
        return "createbot <type> <name> <level>";
    }
    
    @Override
    public CommandResult execute(String[] args, int executorId) {
        if (args.length < 3) {
            return CommandResult.failure("Usage: " + getSyntax());
        }
        
        try {
            String typeStr = args[0].toUpperCase();
            String name = args[1];
            int level = Integer.parseInt(args[2]);
            
            BotType botType = BotType.valueOf(typeStr);
            if (botType == null) {
                return CommandResult.failure("Invalid bot type: " + typeStr);
            }
            
            if (level < 1 || level > 80) {
                return CommandResult.failure("Level must be between 1 and 80");
            }
            
            if (name.length() < 3 || name.length() > 16) {
                return CommandResult.failure("Name must be between 3 and 16 characters");
            }
            
            // TODO: Implement actual bot creation
            // EnhancedFakePlayer bot = botManagement.createBot(botType, name, level);
            
            logger.info("Bot creation requested: " + name + " (" + botType + ", level " + level + ") by " + executorId);
            
            return CommandResult.success("Bot creation request submitted: " + name + " (" + botType + ", level " + level + ")");
            
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid level: " + args[2]);
        } catch (IllegalArgumentException e) {
            return CommandResult.failure("Invalid bot type: " + args[0]);
        } catch (Exception e) {
            logger.error("Error in CreateBotCommand: " + e.getMessage());
            return CommandResult.failure("Error creating bot: " + e.getMessage());
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
