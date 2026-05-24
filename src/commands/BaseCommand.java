package commands;

/**
 * Abstract base class for commands that provides default duration support.
 * All commands can extend this to automatically get duration functionality.
 */
public abstract class BaseCommand implements CommandWithProgress {

    /**
     * Get the configured global command duration
     */
    @Override
    public long getDurationMs() {
        return CommandDurationConfig.getInstance().getDurationMs();
    }

    /**
     * Execute the command (must be implemented by subclasses)
     */
    @Override
    public abstract void execute();
}
