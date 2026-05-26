package commands.macro;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.Command;
import commands.CommandInfo;
import commands.CommandWithProgress;
import commands.ProgressListener;

import java.util.Arrays;
import java.util.List;

/**
 * MacroCommand executes multiple commands in sequence.
 * This allows combining multiple actions into a single button press.
 * Supports progress tracking for GUI progress bars.
 */
@CommandInfo(
    name = "Macro Command",
    description = "Execute a sequence of commands in order",
    category = "Macro",
    receiverType = Object.class
)
public class MacroCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("MacroCommand");
    private final Command[] commands;
    private final String name;
    private ProgressListener progressListener;  // Track progress of child commands

    /**
     * Create a macro command with an array of commands
     * @param commands Array of commands to execute in sequence
     */
    public MacroCommand(Command[] commands) {
        this(commands, "Macro");
    }

    /**
     * Create a named macro command
     * @param commands Array of commands to execute in sequence
     * @param name Display name for the macro
     */
    public MacroCommand(Command[] commands, String name) {
        this.commands = commands;
        this.name = name;
        log.debug("Creating MacroCommand '" + name + "' with " + commands.length + " commands");
        log.value("Macro command list: " + Arrays.toString(commands));
        log.success("Successfully created MacroCommand: " + this);
    }

    /**
     * Create a macro command from a list of commands
     * @param commands List of commands to execute in sequence
     */
    public MacroCommand(List<Command> commands) {
        this(commands.toArray(new Command[0]), "Macro");
    }

    /**
     * Create a named macro command from a list
     * @param commands List of commands to execute in sequence
     * @param name Display name for the macro
     */
    public MacroCommand(List<Command> commands, String name) {
        this(commands.toArray(new Command[0]), name);
    }

    /**
     * Set progress listener for tracking child command execution
     */
    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }

    @Override
    public void execute() {
        log.info("Executing MacroCommand '" + name + "' - running " + commands.length + " commands");
        for (int i = 0; i < commands.length; i++) {
            String cmdName = getCommandDisplayName(commands[i]);
            log.debug("Executing macro command [" + i + "]: " + cmdName);

            // Report progress to listener
            if (progressListener != null) {
                progressListener.onProgress(i, commands.length, cmdName);
            }

            try {
                commands[i].execute();

                // Report step complete
                if (progressListener != null) {
                    progressListener.onStepComplete();
                }
            } catch (Exception e) {
                log.error("Error executing command [" + i + "] in macro '" + name + "': " + e.getMessage());
                if (progressListener != null) {
                    progressListener.onError("Command " + i + " failed: " + e.getMessage());
                }
                e.printStackTrace();
                // Continue executing remaining commands
            }
        }
        log.success("MacroCommand '" + name + "' execution completed");
        if (progressListener != null) {
            progressListener.onComplete();
        }
    }

    /**
     * Get human-readable display name for a command
     * Converts class name to readable format: "LightOnCommand" -> "Light On"
     */
    private String getCommandDisplayName(Command cmd) {
        String className = cmd.getClass().getSimpleName();
        // Remove "Command" suffix if present
        String withoutSuffix = className.replace("Command", "");
        // Convert camelCase to spaces: "LightOn" -> "Light On"
        String spaced = withoutSuffix.replaceAll("([A-Z])", " $1").trim();
        return spaced.isEmpty() ? className : spaced;
    }

    /**
     * Get the name of this macro
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of commands in this macro
     */
    public int getCommandCount() {
        return commands.length;
    }

    /**
     * Get the commands in this macro
     */
    public Command[] getCommands() {
        return commands.clone(); // Return copy for safety
    }

    @Override
    public String toString() {
        return "MacroCommand{name='" + name + "', commands=" + commands.length + "}";
    }
}
