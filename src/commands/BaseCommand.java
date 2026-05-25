package commands;

import Utils.ConfigParser;
import Utils.Logger;
import Utils.LoggerFactory;

/**
 * Abstract base class for commands that provides default duration support.
 * All commands can extend this to automatically get duration functionality.
 */
public abstract class BaseCommand implements CommandWithProgress {
    private static final Logger log = LoggerFactory.getLogger("BaseCommand");
    private static final String CONFIG_FILE = "AppData/config/remote-config.properties";
    private static final String DURATION_LOG_KEY = "command.duration.logExecution";
    private static boolean logDurationEnabled = false;

    static {
        try {
            ConfigParser config = new ConfigParser(CONFIG_FILE, true);
            logDurationEnabled = config.getProperty(DURATION_LOG_KEY) != null
                && config.getProperty(DURATION_LOG_KEY).equalsIgnoreCase("true");
        } catch (Exception e) {
            logDurationEnabled = true; // default to true if config load fails
        }
    }

    /**
     * Get the configured global command duration
     */
    @Override
    public long getDurationMs() {
        return CommandDurationConfig.getInstance().getDurationMs();
    }

    /**
     * Apply command duration (sleep) if configured.
     * Called after command execution to simulate real-world delay.
     */
    protected void applyDuration() {
        long durationMs = getDurationMs();
        if (durationMs > 0) {
            if (logDurationEnabled) {
                log.debug(this.getClass().getSimpleName() + " applying duration: " + durationMs + "ms");
            }
            try {
                Thread.sleep(durationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warning("Duration sleep interrupted for " + this.getClass().getSimpleName());
            }
        }
    }

    /**
     * Get metadata for this command as a formatted string.
     * Returns: "ClassName [name=..., category=..., durationMs=...]"
     */
    public String getMetadataAsString() {
        String className = this.getClass().getSimpleName();
        CommandRegistry registry = CommandRegistry.getInstance();
        CommandMetadata metadata = registry.getCommandMetadata(className);

        if (metadata == null) {
            return className + " [metadata not found]";
        }

        long effectiveDuration = metadata.getEffectiveDurationMs();
        return className + " [name=" + metadata.name()
            + ", category=" + metadata.category()
            + ", durationMs=" + effectiveDuration + "]";
    }

    /**
     * Execute the command (must be implemented by subclasses)
     */
    @Override
    public abstract void execute();
}
