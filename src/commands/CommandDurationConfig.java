package commands;

import Utils.ConfigParser;
import Utils.Logger;
import Utils.LoggerFactory;

/**
 * Manages command duration configuration with fallback chain support.
 * Supports per-command overrides and global default.
 * Fallback: command-specific -> global default -> hardcoded 250ms
 */
public class CommandDurationConfig {
    private static final Logger log = LoggerFactory.getLogger("CommandDurationConfig");
    private static final String CONFIG_FILE = "AppData/config/remote-config.properties";
    private static final String GLOBAL_DEFAULT_KEY = "command.duration.default.ms";
    private static final String COMMAND_OVERRIDE_PREFIX = "command.duration.";
    private static final String COMMAND_OVERRIDE_SUFFIX = ".ms";
    private static final long HARDCODED_FALLBACK_MS = 250; // 0.25 seconds

    private static CommandDurationConfig instance;
    private long globalDefaultDurationMs;
    private ConfigParser configParser;

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
     * Load duration configuration from file
     */
    private void loadConfiguration() {
        try {
            configParser = new ConfigParser(CONFIG_FILE, true);
            globalDefaultDurationMs = configParser.parseLong(GLOBAL_DEFAULT_KEY, HARDCODED_FALLBACK_MS);
            log.info("Global default command duration configured: " + globalDefaultDurationMs + "ms");
        } catch (Exception e) {
            log.warning("Failed to load command duration config, using hardcoded fallback: " + HARDCODED_FALLBACK_MS + "ms");
            globalDefaultDurationMs = HARDCODED_FALLBACK_MS;
            configParser = null;
        }
    }

    /**
     * Get global default duration (legacy, for backward compatibility)
     */
    public long getDurationMs() {
        return globalDefaultDurationMs;
    }

    /**
     * Get global default command duration in milliseconds
     */
    public long getGlobalDefaultDurationMs() {
        return globalDefaultDurationMs;
    }

    /**
     * Get duration for a specific command with fallback chain:
     * 1. Command-specific override (command.duration.<ClassName>.ms)
     * 2. Global default
     * 3. Hardcoded fallback (250ms)
     */
    public long getDurationForCommand(String commandClassName) {
        if (configParser == null) {
            return globalDefaultDurationMs;
        }

        try {
            String overrideKey = COMMAND_OVERRIDE_PREFIX + commandClassName + COMMAND_OVERRIDE_SUFFIX;
            String value = configParser.getProperty(overrideKey);
            if (value != null && !value.isEmpty()) {
                long customDuration = Long.parseLong(value);
                log.debug("Using command-specific duration for " + commandClassName + ": " + customDuration + "ms");
                return customDuration;
            }
        } catch (Exception e) {
            log.debug("No override found or parse error for " + commandClassName + ", using global default");
        }

        // Per-command overrides are optional; falling back to global default is intended behavior
        log.debug("No duration override for " + commandClassName + ", using global default: " + globalDefaultDurationMs + "ms");
        return globalDefaultDurationMs;
    }

    /**
     * Set the global default command duration (also saves to config)
     */
    public void setGlobalDefaultDurationMs(long durationMs) {
        this.globalDefaultDurationMs = durationMs;
        log.info("Global default command duration updated: " + durationMs + "ms");

        try {
            ConfigParser config = new ConfigParser(CONFIG_FILE, true);
            config.setProperty(GLOBAL_DEFAULT_KEY, String.valueOf(durationMs));
            config.saveConfig("Remote Control Configuration");
            log.success("Global default command duration saved to configuration");
        } catch (Exception e) {
            log.error("Failed to save global default command duration: " + e.getMessage());
        }
    }

    /**
     * Set a command-specific duration override
     */
    public void setCommandDurationOverride(String commandClassName, long durationMs) {
        String overrideKey = COMMAND_OVERRIDE_PREFIX + commandClassName + COMMAND_OVERRIDE_SUFFIX;
        log.info("Setting duration override for " + commandClassName + ": " + durationMs + "ms");

        try {
            if (configParser == null) {
                configParser = new ConfigParser(CONFIG_FILE, true);
            }
            configParser.setProperty(overrideKey, String.valueOf(durationMs));
            configParser.saveConfig("Remote Control Configuration");
            log.success("Command duration override saved: " + overrideKey);
        } catch (Exception e) {
            log.error("Failed to save command duration override: " + e.getMessage());
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
