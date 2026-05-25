package commands;

/**
 * Metadata describing a Command for GUI display and command registry.
 * This is a record class providing immutable command information.
 */
public record CommandMetadata(
    String name,
    String description,
    String category,
    String iconPath,
    boolean requiresParameters,
    Class<?> receiverType,
    Class<? extends Command> commandClass,
    Integer durationMs
) {
    /**
     * Get a display-friendly string representation
     */
    @Override
    public String toString() {
        return name + " (" + category + ")";
    }

    /**
     * Get the simple class name of the command
     */
    public String getCommandClassName() {
        return commandClass.getSimpleName();
    }

    /**
     * Resolve effective duration with fallback chain:
     * 1. command-specific override from config
     * 2. global default from config
     * 3. hardcoded 250ms fallback
     */
    public long getEffectiveDurationMs() {
        if (durationMs != null) {
            return durationMs;
        }
        return CommandDurationConfig.getInstance().getGlobalDefaultDurationMs();
    }
}
