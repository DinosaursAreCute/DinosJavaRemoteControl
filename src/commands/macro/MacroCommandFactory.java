package commands.macro;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import commands.CommandRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating MacroCommands from command name lists.
 * This enables defining macros in configuration files.
 */
public class MacroCommandFactory {
    private static final Logger log = LoggerFactory.getLogger("MacroCommandFactory");
    private final CommandRegistry commandRegistry;

    public MacroCommandFactory() {
        this.commandRegistry = CommandRegistry.getInstance();
    }

    /**
     * Create a macro command from a list of command class names
     * @param commandNames List of command class names (e.g., ["LightOnCommand", "StereoOnCommand"])
     * @param macroName Display name for the macro
     * @return MacroCommand instance
     */
    public MacroCommand createMacro(List<String> commandNames, String macroName) {
        log.info("Creating macro '" + macroName + "' with " + commandNames.size() + " commands");

        List<Command> commands = new ArrayList<>();
        List<String> failedCommands = new ArrayList<>();

        for (String commandName : commandNames) {
            try {
                Command cmd = commandRegistry.createCommand(commandName);
                if (cmd != null) {
                    commands.add(cmd);
                    log.debug("Added command to macro: " + commandName);
                } else {
                    log.warning("Command not found: " + commandName);
                    failedCommands.add(commandName);
                }
            } catch (Exception e) {
                log.error("Failed to create command '" + commandName + "': " + e.getMessage());
                failedCommands.add(commandName);
            }
        }

        if (!failedCommands.isEmpty()) {
            log.warning("Macro '" + macroName + "' could not include commands: " + failedCommands);
        }

        if (commands.isEmpty()) {
            log.error("Macro '" + macroName + "' has no valid commands");
            throw new IllegalArgumentException("Cannot create macro with no valid commands");
        }

        MacroCommand macro = new MacroCommand(commands, macroName);
        log.success("Created macro '" + macroName + "' with " + commands.size() + " commands");
        return macro;
    }

    /**
     * Create a macro from a comma-separated string of command names
     * @param commandNamesString Comma-separated command names (e.g., "LightOnCommand,StereoOnCommand")
     * @param macroName Display name for the macro
     * @return MacroCommand instance
     */
    public MacroCommand createMacroFromString(String commandNamesString, String macroName) {
        String[] commandNames = commandNamesString.split(",");
        List<String> commandList = new ArrayList<>();

        for (String name : commandNames) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                commandList.add(trimmed);
            }
        }

        return createMacro(commandList, macroName);
    }

    /**
     * Create a macro from paired commands (for creating opposite macros)
     * @param onCommands List of "on" command names
     * @param offCommands List of "off" command names (in same order)
     * @param macroName Display name for the macro
     * @return Array with [onMacro, offMacro]
     */
    public MacroCommand[] createPairedMacros(List<String> onCommands, List<String> offCommands, String macroName) {
        if (onCommands.size() != offCommands.size()) {
            log.warning("On/Off command lists have different sizes for macro '" + macroName + "'");
        }

        MacroCommand onMacro = createMacro(onCommands, macroName + " On");
        MacroCommand offMacro = createMacro(offCommands, macroName + " Off");

        return new MacroCommand[] { onMacro, offMacro };
    }
}
