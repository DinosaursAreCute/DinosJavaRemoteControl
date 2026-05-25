package remoteClasses;

import Utils.*;
import commands.Command;
import commands.CommandRegistry;
import commands.macro.MacroRegistry;
import commands.common.FallbackCommand;

import java.util.*;

public class Remote {
    private final static Logger log = LoggerFactory.getLogger("Remote");
    private static final String DEFAULT_CONFIG_FILE = "AppData/config/remote-config.properties";
    private static final String CONFIG_SLOTS_KEY = "remote.slots.count";
    private static final int DEFAULT_SLOT_COUNT = 7;

    private int numSlots;
    private Command[] onButtons;
    private Command[] offButtons;
    private String[] onCommandIds;  // Track command IDs (macro IDs) for proper labeling
    private String[] offCommandIds;
    private final CommandHistory commandHistory;
    private final SessionStats sessionStats;
    private final CommandRegistry commandRegistry;

    /**
     * Create a remote with default configuration file
     */
    public Remote() {
        this(DEFAULT_SLOT_COUNT);
    }

    /**
     * Create a remote with specified number of slots
     */
    public Remote(int numSlots) {
        this.numSlots = numSlots;
        this.commandRegistry = CommandRegistry.getInstance();
        this.commandHistory = new CommandHistory();
        this.sessionStats = SessionStats.getInstance();

        log.debug("Creating new Remote with " + numSlots + " slots");
        onButtons = new Command[numSlots];
        offButtons = new Command[numSlots];
        onCommandIds = new String[numSlots];
        offCommandIds = new String[numSlots];

        // Initialize all slots with FallbackCommand
        initializeWithFallbackCommands();
    }

    /**
     * Initialize all slots with FallbackCommand
     */
    private void initializeWithFallbackCommands() {
        log.debug("Assigning buttons with fallback commands");
        for (int i = 0; i < numSlots; i++) {
            onButtons[i] = new FallbackCommand();
            offButtons[i] = new FallbackCommand();
            log.debug("Added fallback to slot [" + i + "]");
        }
    }

    /**
     * Load configuration from file
     */
    public void loadConfiguration() {
        loadConfiguration(DEFAULT_CONFIG_FILE);
    }

    /**
     * Load configuration from specified file
     */
    public void loadConfiguration(String configFile) {
        log.info("Loading remote control configuration from: " + configFile);

        try {
            ConfigParser config = new ConfigParser(configFile, true);

            // Load number of slots from config
            numSlots = config.parseInt(CONFIG_SLOTS_KEY, DEFAULT_SLOT_COUNT);
            log.info("Remote control configured for " + numSlots + " slots");

            // Reinitialize arrays if slot count changed
            if (onButtons.length != numSlots) {
                onButtons = new Command[numSlots];
                offButtons = new Command[numSlots];
                onCommandIds = new String[numSlots];
                offCommandIds = new String[numSlots];
                initializeWithFallbackCommands();
            }

            // Validate and load slot assignments
            List<ConfigError> errors = validateConfiguration(config);

            if (!errors.isEmpty()) {
                log.error("Configuration errors detected:");
                errors.forEach(error -> log.error(error.toString()));
            }

            // Step 1: Load macro definitions from config into registry
            log.info("Loading macros from configuration...");
            try {
                MacroRegistry macroRegistry = MacroRegistry.getInstance();
                macroRegistry.loadAndRegisterMacros();
            } catch (Exception e) {
                log.warning("Failed to load macros: " + e.getMessage());
            }

            // Step 2: Load all slots uniformly (both macro and regular commands)
            int loadedSlots = 0;

            for (int slot = 0; slot < numSlots; slot++) {
                String onCmdName = config.getProperty("slot." + slot + ".on");
                String offCmdName = config.getProperty("slot." + slot + ".off");

                if (onCmdName != null && offCmdName != null) {
                    // Load command (macro or regular) by ID
                    try {
                        Command onCmd = commandRegistry.createCommand(onCmdName);
                        Command offCmd = commandRegistry.createCommand(offCmdName);

                        if (onCmd != null && offCmd != null) {
                            onButtons[slot] = onCmd;
                            offButtons[slot] = offCmd;
                            onCommandIds[slot] = onCmdName;  // Store the ID for proper display
                            offCommandIds[slot] = offCmdName;
                            log.info("Assigned slot " + slot + ": ON=" + onCmdName + ", OFF=" + offCmdName);
                            loadedSlots++;
                        } else {
                            log.warning("Failed to load commands for slot " + slot + ", using FallbackCommand");
                        }
                    } catch (Exception e) {
                        log.warning("Error loading slot " + slot + ": " + e.getMessage());
                    }
                } else {
                    log.debug("Slot " + slot + " not configured, using FallbackCommand");
                }
            }

            log.success("Configuration loaded successfully: " + loadedSlots + " slots configured");

        } catch (Exception e) {
            log.error("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
            log.warning("Using default FallbackCommand for all slots");
        }
    }

    /**
     * Save current configuration to file
     */
    public void saveConfiguration() {
        saveConfiguration(DEFAULT_CONFIG_FILE);
    }

    /**
     * Save current configuration to specified file
     */
    public void saveConfiguration(String configFile) {
        log.info("Saving remote control configuration to: " + configFile);

        try {
            ConfigParser config = new ConfigParser(configFile, true);
            config.setProperty(CONFIG_SLOTS_KEY, String.valueOf(numSlots));

            for (int slot = 0; slot < numSlots; slot++) {
                if (onButtons[slot] != null && !(onButtons[slot] instanceof FallbackCommand)) {
                    config.setProperty("slot." + slot + ".on", onButtons[slot].getClass().getSimpleName());
                }
                if (offButtons[slot] != null && !(offButtons[slot] instanceof FallbackCommand)) {
                    config.setProperty("slot." + slot + ".off", offButtons[slot].getClass().getSimpleName());
                }
            }

            config.saveConfig("Remote Control Configuration - Generated on " + new Date());
            log.success("Configuration saved successfully");

        } catch (Exception e) {
            log.error("Failed to save configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate configuration properties
     */
    private List<ConfigError> validateConfiguration(ConfigParser config) {
        List<ConfigError> errors = new ArrayList<>();

        for (int slot = 0; slot < numSlots; slot++) {
            String onCmd = config.getProperty("slot." + slot + ".on");
            String offCmd = config.getProperty("slot." + slot + ".off");

            if (onCmd != null && !commandRegistry.isCommandAvailable(onCmd)) {
                errors.add(new ConfigError(slot, "on", onCmd, "Command not found in registry"));
            }
            if (offCmd != null && !commandRegistry.isCommandAvailable(offCmd)) {
                errors.add(new ConfigError(slot, "off", offCmd, "Command not found in registry"));
            }
        }

        return errors;
    }

    /**
     * Assign a command to a slot by command name
     */
    public void assignCommand(int slot, String onCommandName, String offCommandName) {
        if (slot < 0 || slot >= numSlots) {
            log.error("Invalid slot index: " + slot);
            throw new IllegalArgumentException("Invalid slot index: " + slot);
        }

        Command onCommand = commandRegistry.createCommand(onCommandName);
        Command offCommand = commandRegistry.createCommand(offCommandName);

        onButtons[slot] = onCommand;
        offButtons[slot] = offCommand;

        log.debug("Assigned commands to slot " + slot + ": ON=" + onCommandName + ", OFF=" + offCommandName);
    }



    /**
     * Undo the last action
     */
    public void undo(){
        log.info("Undo last Action");

        // Get last command from history
        CommandHistory.HistoryEntry entry = commandHistory.getLastForUndo();
        if (entry == null) {
            log.warning("Cannot undo: History is empty");
            return;
        }

        // Execute opposite command
        boolean executeOpposite = !entry.wasOnButton();
        log.debug("Undo action: slot=" + entry.slot() + ", was=" + entry.wasOnButton() + ", executing opposite=" + executeOpposite);

        executeCommandDirect(entry.slot(), executeOpposite);
        sessionStats.recordUndo();
    }

    /**
     * Redo the last undone action
     */
    public void redo(){
        log.info("Redo last undone Action");

        // Get last undone command from redo stack
        CommandHistory.HistoryEntry entry = commandHistory.getLastForRedo();
        if (entry == null) {
            log.warning("Cannot redo: Nothing to redo");
            return;
        }

        // Execute original command again
        log.debug("Redo action: slot=" + entry.slot() + ", wasOn=" + entry.wasOnButton());

        executeCommandDirect(entry.slot(), entry.wasOnButton());
        sessionStats.recordRedo();
    }

    /**
     * Execute a function at the specified slot
     */
    public void executeFunction(int index, boolean isOnButton){
        log.debug("Executing button: " + index);

        long startTime = System.currentTimeMillis();
        executeCommandDirect(index, isOnButton);
        long executionTime = System.currentTimeMillis() - startTime;

        // Add to history
        commandHistory.addExecution(index, isOnButton);

        // Record statistics
        Command cmd = isOnButton ? onButtons[index] : offButtons[index];
        String commandName = cmd.getClass().getSimpleName();

        // Track if this is a macro command
        if (commandName.equals("MacroCommand")) {
            sessionStats.recordMacroExecution(commandName, executionTime);
        } else {
            sessionStats.recordCommandExecution(commandName, executionTime);
        }

        log.success("Function [" + index + "][" + isOnButton + "]");
    }

    /**
     * Execute command directly without adding to history
     */
    private void executeCommandDirect(int index, boolean isOnButton){
        if(index < 0 || index >= numSlots){
            log.error("INVALID index:'" + index + "'");
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        if(isOnButton) {
            onButtons[index].execute();
        } else {
            offButtons[index].execute();
        }
    }

    /**
     * Get number of slots in this remote
     */
    public int getNumSlots() {
        return numSlots;
    }

    /**
     * Check if undo is available
     */
    public boolean canUndo() {
        return commandHistory.canUndo();
    }

    /**
     * Check if redo is available
     */
    public boolean canRedo() {
        return commandHistory.canRedo();
    }

    /**
     * Get command history
     */
    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    /**
     * Get session statistics
     */
    public SessionStats getSessionStats() {
        return sessionStats;
    }

    /**
     * Check if a slot has non-fallback commands assigned
     */
    public boolean isSlotConfigured(int slot) {
        if (slot < 0 || slot >= numSlots) {
            return false;
        }
        return !(onButtons[slot] instanceof FallbackCommand) ||
               !(offButtons[slot] instanceof FallbackCommand);
    }

    /**
     * Get the command name for a specific slot and button type
     */
    public String getCommandName(int slot, boolean isOnButton) {
        if (slot < 0 || slot >= numSlots) {
            return null;
        }
        Command cmd = isOnButton ? onButtons[slot] : offButtons[slot];
        return cmd != null ? cmd.getClass().getSimpleName() : null;
    }

    /**
     * Get the command ID for a specific slot and button type
     * Returns the macro ID (e.g., "partyMode_On") or class name for regular commands
     */
    public String getCommandId(int slot, boolean isOnButton) {
        if (slot < 0 || slot >= numSlots) {
            return null;
        }
        String id = isOnButton ? onCommandIds[slot] : offCommandIds[slot];
        // If no ID was stored, fall back to class name
        if (id == null) {
            Command cmd = isOnButton ? onButtons[slot] : offButtons[slot];
            return cmd != null ? cmd.getClass().getSimpleName() : null;
        }
        return id;
    }

    /**
     * Get the ON command for a specific slot
     */
    public Command getOnCommand(int slot) {
        if (slot < 0 || slot >= numSlots) {
            return null;
        }
        return onButtons[slot];
    }

    /**
     * Get the OFF command for a specific slot
     */
    public Command getOffCommand(int slot) {
        if (slot < 0 || slot >= numSlots) {
            return null;
        }
        return offButtons[slot];
    }

    /**
     * Configuration error record
     */
    public record ConfigError(int slot, String type, String commandName, String reason) {
        @Override
        public String toString() {
            return String.format("Slot %d (%s): '%s' - %s", slot, type, commandName, reason);
        }
    }
}





