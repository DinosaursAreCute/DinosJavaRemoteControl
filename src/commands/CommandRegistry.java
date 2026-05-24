package commands;

import Utils.Logger;
import Utils.LoggerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import receiver.ReceiverRegistry;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton registry for managing command discovery and instantiation.
 * Automatically scans for @CommandInfo annotated classes and provides factory methods.
 */
public class CommandRegistry {
    private static final Logger log = LoggerFactory.getLogger("CommandRegistry");
    private static CommandRegistry instance;

    private final Map<String, CommandMetadata> commandMetadata;
    private final ReceiverRegistry receiverRegistry;

    private CommandRegistry() {
        commandMetadata = new HashMap<>();
        receiverRegistry = ReceiverRegistry.getInstance();
        log.debug("CommandRegistry initialized");
    }

    /**
     * Get the singleton instance of CommandRegistry
     */
    public static synchronized CommandRegistry getInstance() {
        if (instance == null) {
            instance = new CommandRegistry();
        }
        return instance;
    }

    /**
     * Scan classpath for @CommandInfo annotated classes and register them
     * @param basePackage The base package to scan (e.g., "commands")
     */
    public void scanAndRegister(String basePackage) {
        log.info("Scanning for commands in package: " + basePackage);

        try {
            Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CommandInfo.class);

            log.debug("Found " + annotatedClasses.size() + " annotated command classes");

            for (Class<?> clazz : annotatedClasses) {
                if (Command.class.isAssignableFrom(clazz)) {
                    registerCommand(clazz.asSubclass(Command.class));
                } else {
                    log.warning("Class " + clazz.getSimpleName() + " has @CommandInfo but doesn't implement Command interface");
                }
            }

            log.success("Command scanning complete. Registered " + commandMetadata.size() + " commands");
        } catch (Exception e) {
            log.error("Failed to scan for commands: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manually register a command class
     * @param commandClass The command class to register
     */
    public void registerCommand(Class<? extends Command> commandClass) {
        CommandInfo info = commandClass.getAnnotation(CommandInfo.class);
        if (info == null) {
            log.warning("Cannot register " + commandClass.getSimpleName() + " - missing @CommandInfo annotation");
            return;
        }

        CommandMetadata metadata = new CommandMetadata(
            info.name(),
            info.description(),
            info.category(),
            info.iconPath(),
            info.requiresParameters(),
            info.receiverType(),
            commandClass
        );

        String key = commandClass.getSimpleName();
        commandMetadata.put(key, metadata);
        log.debug("Registered command: " + key + " (" + info.name() + ")");
    }

    /**
     * Create a command instance by class name
     * @param commandClassName Simple class name (e.g., "LightOnCommand")
     * @return Command instance, or FallbackCommand if not found
     */
    public Command createCommand(String commandClassName) {
        CommandMetadata metadata = commandMetadata.get(commandClassName);

        if (metadata == null) {
            log.error("Command not found: " + commandClassName);
            return createFallbackCommand();
        }

        try {
            // Get the receiver for this command
            Object receiver = receiverRegistry.getReceiver(metadata.receiverType());

            // Find constructor that takes the receiver type
            Constructor<? extends Command> constructor = metadata.commandClass()
                .getConstructor(metadata.receiverType());

            Command command = constructor.newInstance(receiver);
            log.debug("Created command instance: " + commandClassName);
            return command;

        } catch (Exception e) {
            log.error("Failed to create command " + commandClassName + ": " + e.getMessage());
            e.printStackTrace();
            return createFallbackCommand();
        }
    }

    /**
     * Create a FallbackCommand (NoCommand equivalent)
     */
    private Command createFallbackCommand() {
        try {
            // Try to create FallbackCommand
            Class<?> fallbackClass = Class.forName("commands.common.FallbackCommand");
            return (Command) fallbackClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Failed to create FallbackCommand: " + e.getMessage());
            // Return a simple no-op command
            return () -> log.warning("NoOp command executed");
        }
    }

    /**
     * Check if a command is available in the registry
     * @param commandClassName Simple class name
     * @return true if command exists and can be created
     */
    public boolean isCommandAvailable(String commandClassName) {
        CommandMetadata metadata = commandMetadata.get(commandClassName);
        if (metadata == null) {
            return false;
        }
        // Check if receiver is available
        return receiverRegistry.isReceiverRegistered(metadata.receiverType());
    }

    /**
     * Get all registered command metadata
     * @return List of all command metadata
     */
    public List<CommandMetadata> getAllCommands() {
        return new ArrayList<>(commandMetadata.values());
    }

    /**
     * Get commands grouped by category
     * @return Map of category name to list of commands
     */
    public Map<String, List<CommandMetadata>> getCommandsByCategory() {
        return commandMetadata.values().stream()
            .collect(Collectors.groupingBy(CommandMetadata::category));
    }

    /**
     * Get commands for a specific category
     * @param category The category name
     * @return List of commands in that category
     */
    public List<CommandMetadata> getCommandsInCategory(String category) {
        return commandMetadata.values().stream()
            .filter(cmd -> cmd.category().equals(category))
            .collect(Collectors.toList());
    }

    /**
     * Get metadata for a specific command
     * @param commandClassName Simple class name
     * @return CommandMetadata or null if not found
     */
    public CommandMetadata getCommandMetadata(String commandClassName) {
        return commandMetadata.get(commandClassName);
    }

    /**
     * Get all category names
     * @return Set of unique category names
     */
    public Set<String> getAllCategories() {
        return commandMetadata.values().stream()
            .map(CommandMetadata::category)
            .collect(Collectors.toSet());
    }

    /**
     * Clear all registered commands (useful for testing)
     */
    public void clearAll() {
        log.warning("Clearing all commands from registry");
        commandMetadata.clear();
    }
}
