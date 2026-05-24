package commands;

import Utils.ConfigParser;
import Utils.Logger;
import Utils.LoggerFactory;

/**
 * Manages global command duration configuration.
 * All commands use the same duration for simplicity, configurable via properties file.
 */
public class CommandDurationConfig {
    private static final Logger log = LoggerFactory.getLogger("CommandDurationConfig");
    private static final String CONFIG_FILE = "AppData/config/remote-config.properties";
    private static final String DURATION_KEY = "command.duration.ms";
    private static final long DEFAULT_DURATION_MS = 250; // 0.25 seconds

    private static CommandDurationConfig instance;
    private long durationMs;

    private CommandDurationConfig() {
        loadConfiguration();
    }

    /**
     * Get singleton instance
     */
    public static synchronized CommandDurationConfig getInstance() {
        if (instance == null) {
            instance = new CommandDurationConfig();
        }
        return instance;
    }

    /**
     * Load duration from configuration file
     */
    private void loadConfiguration() {
        try {
            ConfigParser config = new ConfigParser(CONFIG_FILE, true);
            durationMs = config.parseLong(DURATION_KEY, DEFAULT_DURATION_MS);
            log.info("Command duration configured: " + durationMs + "ms");
        } catch (Exception e) {
            log.warning("Failed to load command duration config, using default: " + DEFAULT_DURATION_MS + "ms");
            durationMs = DEFAULT_DURATION_MS;
        }
    }

    /**
     * Get the configured command duration in milliseconds
     */
    public long getDurationMs() {
        return durationMs;
    }

    /**
     * Set the command duration (also saves to config)
     */
    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
        log.info("Command duration updated: " + durationMs + "ms");

        try {
            ConfigParser config = new ConfigParser(CONFIG_FILE, true);
            config.setProperty(DURATION_KEY, String.valueOf(durationMs));
            config.saveConfig("Remote Control Configuration");
            log.success("Command duration saved to configuration");
        } catch (Exception e) {
            log.error("Failed to save command duration: " + e.getMessage());
        }
    }

    /**
     * Reload configuration from file
     */
    public void reload() {
        log.info("Reloading command duration configuration");
        loadConfiguration();
    }
}
