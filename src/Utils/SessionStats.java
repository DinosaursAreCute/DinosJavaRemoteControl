package Utils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks statistics for the current session.
 * Used for debug panel to show session stats.
 */
public class SessionStats {
    private static final Logger log = LoggerFactory.getLogger("SessionStats");
    private static SessionStats instance;

    private final Instant sessionStart;
    private final AtomicInteger commandsExecuted;
    private final AtomicInteger macrosExecuted;
    private final AtomicInteger undoOperations;
    private final AtomicInteger redoOperations;
    private final AtomicInteger failedCommands;
    private final AtomicLong totalExecutionTime;

    private final Map<String, Integer> commandExecutionCounts;

    private SessionStats() {
        sessionStart = Instant.now();
        commandsExecuted = new AtomicInteger(0);
        macrosExecuted = new AtomicInteger(0);
        undoOperations = new AtomicInteger(0);
        redoOperations = new AtomicInteger(0);
        failedCommands = new AtomicInteger(0);
        totalExecutionTime = new AtomicLong(0);
        commandExecutionCounts = new HashMap<>();

        log.info("Session statistics tracking started at " + sessionStart);
    }

    /**
     * Get singleton instance
     */
    public static synchronized SessionStats getInstance() {
        if (instance == null) {
            instance = new SessionStats();
        }
        return instance;
    }

    /**
     * Record a command execution
     */
    public void recordCommandExecution(String commandName, long executionTimeMs) {
        commandsExecuted.incrementAndGet();
        totalExecutionTime.addAndGet(executionTimeMs);

        synchronized (commandExecutionCounts) {
            commandExecutionCounts.merge(commandName, 1, Integer::sum);
        }

        log.debug("Recorded execution: " + commandName + " (" + executionTimeMs + "ms)");
    }

    /**
     * Record a macro execution
     */
    public void recordMacroExecution(String macroName, long executionTimeMs) {
        macrosExecuted.incrementAndGet();
        recordCommandExecution(macroName, executionTimeMs);
        log.debug("Recorded macro execution: " + macroName);
    }

    /**
     * Record an undo operation
     */
    public void recordUndo() {
        undoOperations.incrementAndGet();
        log.debug("Recorded undo operation");
    }

    /**
     * Record a redo operation
     */
    public void recordRedo() {
        redoOperations.incrementAndGet();
        log.debug("Recorded redo operation");
    }

    /**
     * Record a failed command
     */
    public void recordFailedCommand(String commandName) {
        failedCommands.incrementAndGet();
        log.warning("Recorded failed command: " + commandName);
    }

    /**
     * Get total commands executed
     */
    public int getCommandsExecuted() {
        return commandsExecuted.get();
    }

    /**
     * Get total macros executed
     */
    public int getMacrosExecuted() {
        return macrosExecuted.get();
    }

    /**
     * Get total undo operations
     */
    public int getUndoOperations() {
        return undoOperations.get();
    }

    /**
     * Get total redo operations
     */
    public int getRedoOperations() {
        return redoOperations.get();
    }

    /**
     * Get total failed commands
     */
    public int getFailedCommands() {
        return failedCommands.get();
    }

    /**
     * Get session duration
     */
    public Duration getSessionDuration() {
        return Duration.between(sessionStart, Instant.now());
    }

    /**
     * Get session duration as formatted string
     */
    public String getSessionDurationFormatted() {
        Duration duration = getSessionDuration();
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Get average command execution time
     */
    public long getAverageExecutionTimeMs() {
        int count = commandsExecuted.get();
        if (count == 0) {
            return 0;
        }
        return totalExecutionTime.get() / count;
    }

    /**
     * Get most executed command
     */
    public String getMostExecutedCommand() {
        synchronized (commandExecutionCounts) {
            return commandExecutionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " (" + entry.getValue() + "x)")
                .orElse("None");
        }
    }

    /**
     * Get execution count for specific command
     */
    public int getExecutionCount(String commandName) {
        synchronized (commandExecutionCounts) {
            return commandExecutionCounts.getOrDefault(commandName, 0);
        }
    }

    /**
     * Get all command execution counts
     */
    public Map<String, Integer> getAllExecutionCounts() {
        synchronized (commandExecutionCounts) {
            return new HashMap<>(commandExecutionCounts);
        }
    }

    /**
     * Get formatted statistics report
     */
    public String getFormattedReport() {
	    String report = "=== Session Statistics ===\n" +
			    "Session Duration:      " + getSessionDurationFormatted() + "\n" +
			    "Commands Executed:     " + getCommandsExecuted() + "\n" +
			    "Macros Executed:       " + getMacrosExecuted() + "\n" +
			    "Undo Operations:       " + getUndoOperations() + "\n" +
			    "Redo Operations:       " + getRedoOperations() + "\n" +
			    "Failed Commands:       " + getFailedCommands() + "\n" +
			    "Average Exec Time:     " + getAverageExecutionTimeMs() + "ms\n" +
			    "Most Used Command:     " + getMostExecutedCommand() + "\n";
        return report;
    }

    /**
     * Reset all statistics (for new session)
     */
    public void reset() {
        log.info("Resetting session statistics");
        commandsExecuted.set(0);
        macrosExecuted.set(0);
        undoOperations.set(0);
        redoOperations.set(0);
        failedCommands.set(0);
        totalExecutionTime.set(0);

        synchronized (commandExecutionCounts) {
            commandExecutionCounts.clear();
        }
    }
}
