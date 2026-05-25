package commands.macro;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.Command;
import commands.CommandInfo;
import commands.CommandWithProgress;

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

    @Override
    public void execute() {
        log.info("Executing MacroCommand '" + name + "' - running " + commands.length + " commands");
        for (int i = 0; i < commands.length; i++) {
            log.debug("Executing macro command [" + i + "]: " + commands[i].getClass().getSimpleName());
            try {
                commands[i].execute();
            } catch (Exception e) {
                log.error("Error executing command [" + i + "] in macro '" + name + "': " + e.getMessage());
                e.printStackTrace();
                // Continue executing remaining commands
            }
        }
        log.success("MacroCommand '" + name + "' execution completed");
        applyDuration();
    }

    /**
     * Execute with progress tracking for each command
     */
    @Override
    public void executeWithProgress(ProgressCallback progressCallback) throws InterruptedException {
        log.info("Executing MacroCommand '" + name + "' with progress tracking - running " + commands.length + " commands");

        for (int i = 0; i < commands.length; i++) {
            log.debug("Executing macro command [" + i + "/" + commands.length + "]: " + commands[i].getClass().getSimpleName());

            try {
                // Report progress before command
                if (progressCallback != null) {
                    double progress = (double) i / commands.length;
                    progressCallback.onProgress(progress);
                }

                // Execute command with duration
                if (commands[i] instanceof CommandWithProgress) {
                    CommandWithProgress cmdWithProgress = (CommandWithProgress) commands[i];
                    cmdWithProgress.executeWithProgress(null); // Execute with its own duration
                } else {
                    // Fallback for commands that don't implement CommandWithProgress
                    commands[i].execute();
                    Thread.sleep(250); // Default 250ms delay
                }

            } catch (Exception e) {
                log.error("Error executing command [" + i + "] in macro '" + name + "': " + e.getMessage());
                // Continue executing remaining commands
            }
        }

        // Report completion
        if (progressCallback != null) {
            progressCallback.onProgress(1.0);
        }

        log.success("MacroCommand '" + name + "' execution completed");
        applyDuration();
    }

    /**
     * Get total duration of all child commands in macro (not including macro's own duration)
     * Used for progress tracking and timing calculations
     */
    public long getChildrenDurationMs() {
        long totalDuration = 0;
        for (Command cmd : commands) {
            if (cmd instanceof CommandWithProgress) {
                totalDuration += ((CommandWithProgress) cmd).getDurationMs();
            } else {
                totalDuration += 250; // Default duration
            }
        }
        return totalDuration;
    }

    /**
     * Get macro's own configured duration from BaseCommand
     */
    @Override
    public long getDurationMs() {
        return super.getDurationMs();
    }

    /**
     * Undo all commands in reverse order
     * Note: This requires commands to implement proper undo functionality
     */
    public void undo() {
        log.info("Undoing MacroCommand '" + name + "' - reversing " + commands.length + " commands");
        // Execute commands in reverse order for undo
        for (int i = commands.length - 1; i >= 0; i--) {
            log.debug("Undoing macro command [" + i + "]: " + commands[i].getClass().getSimpleName());
            try {
                // For now, we'll need to execute the opposite command
                // This assumes each command has a corresponding opposite
                // TODO: Implement proper Command.undo() interface method
                commands[i].execute(); // Placeholder - needs proper undo
            } catch (Exception e) {
                log.error("Error undoing command [" + i + "] in macro '" + name + "': " + e.getMessage());
            }
        }
        log.success("MacroCommand '" + name + "' undo completed");
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
