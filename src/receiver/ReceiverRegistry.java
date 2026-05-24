package receiver;

import Utils.Logger;
import Utils.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton registry for managing receiver instances.
 * Ensures only one instance of each receiver type exists.
 */
public class ReceiverRegistry {
    private static final Logger log = LoggerFactory.getLogger("ReceiverRegistry");
    private static ReceiverRegistry instance;
    private final Map<Class<?>, Object> receivers;

    private ReceiverRegistry() {
        receivers = new HashMap<>();
        log.debug("ReceiverRegistry initialized");
    }

    /**
     * Get the singleton instance of ReceiverRegistry
     */
    public static synchronized ReceiverRegistry getInstance() {
        if (instance == null) {
            instance = new ReceiverRegistry();
        }
        return instance;
    }

    /**
     * Register a receiver instance
     * @param receiverClass The class type of the receiver
     * @param receiver The receiver instance
     * @param <T> The receiver type
     */
    public <T> void registerReceiver(Class<T> receiverClass, T receiver) {
        if (receivers.containsKey(receiverClass)) {
            log.warning("Receiver already registered: " + receiverClass.getSimpleName() + ", replacing with new instance");
        }
        receivers.put(receiverClass, receiver);
        log.info("Registered receiver: " + receiverClass.getSimpleName());
    }

    /**
     * Get a receiver instance by its class type
     * @param receiverClass The class type of the receiver
     * @param <T> The receiver type
     * @return The receiver instance, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getReceiver(Class<T> receiverClass) {
        T receiver = (T) receivers.get(receiverClass);
        if (receiver == null) {
            log.error("Receiver not found: " + receiverClass.getSimpleName());
            throw new IllegalStateException("Receiver not registered: " + receiverClass.getSimpleName());
        }
        log.debug("Retrieved receiver: " + receiverClass.getSimpleName());
        return receiver;
    }

    /**
     * Check if a receiver is registered
     * @param receiverClass The class type to check
     * @return true if registered, false otherwise
     */
    public boolean isReceiverRegistered(Class<?> receiverClass) {
        return receivers.containsKey(receiverClass);
    }

    /**
     * Get all registered receiver types
     * @return Map of receiver types to instances
     */
    public Map<Class<?>, Object> getAllReceivers() {
        return new HashMap<>(receivers);
    }

    /**
     * Clear all receivers (useful for testing)
     */
    public void clearAll() {
        log.warning("Clearing all receivers from registry");
        receivers.clear();
    }
}
