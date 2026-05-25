package commands.macro;

import Utils.ConfigParser;
import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import commands.CommandRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for macro command definitions loaded from configuration.
 * Handles creation of MacroCommand instances from config definitions.
 * Integrates with CommandRegistry to register macros as first-class commands.
 */
public class MacroRegistry {
    private static final Logger log = LoggerFactory.getLogger("MacroRegistry");
    private static final String CONFIG_FILE = "AppData/config/remote-config.properties";
    private static final String MACRO_PREFIX = "macro.";
    private static final String ON_SUFFIX = ".on.commands";
    private static final String OFF_SUFFIX = ".off.commands";

    private static MacroRegistry instance;
    private final CommandRegistry commandRegistry;
    private final ConfigParser configParser;
    private final Map<String, MacroDefinition> macroDefinitions;

    private MacroRegistry() {
        this.commandRegistry = CommandRegistry.getInstance();
        this.macroDefinitions = new HashMap<>();
        try {
            this.configParser = new ConfigParser(CONFIG_FILE, true);
        } catch (Exception e) {
            log.error("Failed to initialize MacroRegistry config: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get singleton instance
     */
    public static synchronized MacroRegistry getInstance() {
        if (instance == null) {
            instance = new MacroRegistry();
        }
        return instance;
    }

    /**
     * Load and register all macros from configuration
     */
    public void loadAndRegisterMacros() {
        log.info("Loading macro definitions from configuration");

        Map<String, String> allProps = configParser.getAllProperties();
        Map<String, MacroDefinition> definitions = new HashMap<>();

        // Collect all macro ON command definitions
        for (Map.Entry<String, String> entry : allProps.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(MACRO_PREFIX) && key.endsWith(ON_SUFFIX)) {
                // Extract macro ID: "macro.partyMode.on.commands" -> "partyMode"
                String macroId = key.substring(MACRO_PREFIX.length(), key.length() - ON_SUFFIX.length());
                String commands = entry.getValue();

                MacroDefinition def = definitions.computeIfAbsent(macroId, k -> new MacroDefinition(k));
                def.onCommands = commands;
            }
        }

        // Collect all macro OFF command definitions
        for (Map.Entry<String, String> entry : allProps.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(MACRO_PREFIX) && key.endsWith(OFF_SUFFIX)) {
                // Extract macro ID
                String macroId = key.substring(MACRO_PREFIX.length(), key.length() - OFF_SUFFIX.length());
                String commands = entry.getValue();

                MacroDefinition def = definitions.computeIfAbsent(macroId, k -> new MacroDefinition(k));
                def.offCommands = commands;
            }
        }

        // Register all macros
        for (MacroDefinition def : definitions.values()) {
            try {
                registerMacro(def);
            } catch (Exception e) {
                log.error("Failed to register macro '" + def.macroId + "': " + e.getMessage());
            }
        }

        log.success("Loaded and registered " + definitions.size() + " macros");
        macroDefinitions.putAll(definitions);
    }

    /**
     * Register a single macro definition as two first-class commands (ON and OFF)
     */
    private void registerMacro(MacroDefinition def) {
        // Create ON version
        if (def.onCommands != null && !def.onCommands.isEmpty()) {
            MacroCommand onMacro = createMacroFromCommandList(def.onCommands, def.macroId + " On");
            commandRegistry.registerMacroCommand(def.macroId + "_On", onMacro);
            log.debug("Registered macro: " + def.macroId + "_On");
        }

        // Create OFF version
        if (def.offCommands != null && !def.offCommands.isEmpty()) {
            MacroCommand offMacro = createMacroFromCommandList(def.offCommands, def.macroId + " Off");
            commandRegistry.registerMacroCommand(def.macroId + "_Off", offMacro);
            log.debug("Registered macro: " + def.macroId + "_Off");
        }
    }

    /**
     * Create a MacroCommand from a comma-separated list of command class names
     */
    private MacroCommand createMacroFromCommandList(String commandList, String macroName) {
        String[] commandNames = commandList.split(",");
        List<Command> commands = new ArrayList<>();

        for (String cmdName : commandNames) {
            cmdName = cmdName.trim();
            try {
                Command cmd = commandRegistry.createCommand(cmdName);
                if (cmd != null) {
                    commands.add(cmd);
                } else {
                    log.warning("Command not found in macro: " + cmdName);
                }
            } catch (Exception e) {
                log.warning("Failed to create command '" + cmdName + "' in macro '" + macroName + "': " + e.getMessage());
            }
        }

        if (commands.isEmpty()) {
            log.error("Macro '" + macroName + "' has no valid commands");
        }

        return new MacroCommand(commands, macroName);
    }

    /**
     * Simple holder for macro definition from config
     */
    private static class MacroDefinition {
        String macroId;
        String onCommands;
        String offCommands;

        MacroDefinition(String macroId) {
            this.macroId = macroId;
        }
    }
}
