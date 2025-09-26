package net.sf.l2j.botmanager.management.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для менеджера команд администратора
 */
public class AdminCommandManagerTest {
    
    private AdminCommandManager commandManager;
    
    @BeforeEach
    void setUp() {
        commandManager = AdminCommandManager.getInstance();
        commandManager.initialize();
    }
    
    @Test
    @DisplayName("AdminCommandManager should be created successfully")
    void testCommandManagerCreation() {
        assertNotNull(commandManager);
        assertTrue(commandManager.isInitialized());
    }
    
    @Test
    @DisplayName("AdminCommandManager should initialize and shutdown correctly")
    void testInitializationAndShutdown() {
        assertTrue(commandManager.isInitialized());
        
        commandManager.shutdown();
        assertFalse(commandManager.isInitialized());
        
        commandManager.initialize();
        assertTrue(commandManager.isInitialized());
    }
    
    @Test
    @DisplayName("AdminCommandManager should register commands")
    void testCommandRegistration() {
        Set<String> commands = commandManager.getRegisteredCommands();
        assertNotNull(commands);
        assertTrue(commands.size() > 0);
        
        // Check that basic commands are registered
        assertTrue(commands.contains("createbot"));
        assertTrue(commands.contains("removebot"));
        assertTrue(commands.contains("listbots"));
        assertTrue(commands.contains("botinfo"));
        assertTrue(commands.contains("setbehavior"));
        assertTrue(commands.contains("activatebot"));
        assertTrue(commands.contains("deactivatebot"));
        assertTrue(commands.contains("botstats"));
        assertTrue(commands.contains("help"));
    }
    
    @Test
    @DisplayName("AdminCommandManager should execute commands")
    void testCommandExecution() {
        // Test help command
        CommandResult result = commandManager.executeCommand("help", 1);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        
        // Test help with specific command
        result = commandManager.executeCommand("help createbot", 1);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        
        // Test unknown command
        result = commandManager.executeCommand("unknowncommand", 1);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Unknown command"));
    }
    
    @Test
    @DisplayName("AdminCommandManager should handle empty commands")
    void testEmptyCommands() {
        CommandResult result = commandManager.executeCommand("", 1);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Empty command"));
        
        result = commandManager.executeCommand("   ", 1);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Empty command"));
    }
    
    @Test
    @DisplayName("AdminCommandManager should handle null commands")
    void testNullCommands() {
        CommandResult result = commandManager.executeCommand(null, 1);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Empty command"));
    }
    
    @Test
    @DisplayName("AdminCommandManager should handle command registration and unregistration")
    void testCommandRegistrationAndUnregistration() {
        // Create a test command
        IAdminCommand testCommand = new IAdminCommand() {
            @Override
            public String getCommandName() { return "testcommand"; }
            @Override
            public String getDescription() { return "Test command"; }
            @Override
            public String getSyntax() { return "testcommand"; }
            @Override
            public CommandResult execute(String[] args, int executorId) {
                return CommandResult.success("Test executed");
            }
            @Override
            public boolean hasPermission(int executorId) { return true; }
            @Override
            public int getRequiredAccessLevel() { return 0; }
        };
        
        // Register command
        commandManager.registerCommand(testCommand);
        assertTrue(commandManager.getRegisteredCommands().contains("testcommand"));
        
        // Execute command
        CommandResult result = commandManager.executeCommand("testcommand", 1);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Test executed", result.getMessage());
        
        // Unregister command
        commandManager.unregisterCommand("testcommand");
        assertFalse(commandManager.getRegisteredCommands().contains("testcommand"));
        
        // Try to execute unregistered command
        result = commandManager.executeCommand("testcommand", 1);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Unknown command"));
    }
    
    @Test
    @DisplayName("AdminCommandManager should handle access levels")
    void testAccessLevels() {
        // Set access level for user
        commandManager.setAccessLevel(1, 5);
        assertEquals(5, commandManager.getAccessLevel(1));
        
        // Test default access level
        assertEquals(0, commandManager.getAccessLevel(999));
    }
    
    @Test
    @DisplayName("AdminCommandManager should provide statistics")
    void testStatistics() {
        Map<String, Object> stats = commandManager.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalCommands"));
        assertTrue(stats.containsKey("registeredCommands"));
        assertTrue(stats.containsKey("usersWithAccess"));
        assertTrue(stats.containsKey("initialized"));
        
        assertTrue((Integer) stats.get("totalCommands") > 0);
        assertTrue((Boolean) stats.get("initialized"));
    }
    
    @Test
    @DisplayName("AdminCommandManager should handle shutdown when not initialized")
    void testShutdownWhenNotInitialized() {
        commandManager.shutdown();
        assertFalse(commandManager.isInitialized());
        
        // Shutdown again should not cause errors
        commandManager.shutdown();
        assertFalse(commandManager.isInitialized());
    }
}
