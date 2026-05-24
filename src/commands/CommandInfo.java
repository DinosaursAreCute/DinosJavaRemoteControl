package commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark and describe Command classes for automatic discovery.
 * Used by CommandRegistry to scan and register available commands.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    /**
     * Display name for the command in the GUI
     */
    String name();

    /**
     * Description of what the command does
     */
    String description();

    /**
     * Category for grouping related commands (e.g., "Lighting", "Audio", "Climate")
     */
    String category() default "General";

    /**
     * Icon path for GUI representation (optional)
     */
    String iconPath() default "";

    /**
     * Whether this command requires parameters (for future extension)
     */
    boolean requiresParameters() default false;

    /**
     * The receiver type this command operates on
     */
    Class<?> receiverType();
}
