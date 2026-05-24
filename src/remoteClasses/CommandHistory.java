package remoteClasses;

import Utils.Logger;
import Utils.LoggerFactory;

import java.util.Stack;

/**
 * Manages command history for undo/redo functionality.
 * Maintains two stacks: one for undo operations and one for redo operations.
 */
public class CommandHistory {
    private static final Logger log = LoggerFactory.getLogger("CommandHistory");

    private final Stack<HistoryEntry> undoStack;
    private final Stack<HistoryEntry> redoStack;

    public CommandHistory() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        log.debug("CommandHistory initialized");
    }

    /**
     * Add a command execution to history
     * Clears redo stack when new command is executed
     */
    public void addExecution(int slot, boolean wasOnButton) {
        HistoryEntry entry = new HistoryEntry(slot, wasOnButton);
        undoStack.push(entry);

        // Clear redo stack when new command is executed
        if (!redoStack.isEmpty()) {
            log.debug("Clearing redo stack due to new command execution");
            redoStack.clear();
        }

        log.debug("Added to history: slot=" + slot + ", wasOn=" + wasOnButton + " | Undo size: " + undoStack.size());
    }

    /**
     * Get the last executed command for undo
     * Moves it to redo stack
     */
    public HistoryEntry getLastForUndo() {
        if (undoStack.isEmpty()) {
            log.warning("Cannot undo: History is empty");
            return null;
        }

        HistoryEntry entry = undoStack.pop();
        redoStack.push(entry);

        log.debug("Undo: slot=" + entry.slot() + ", wasOn=" + entry.wasOnButton() +
                  " | Undo size: " + undoStack.size() + ", Redo size: " + redoStack.size());

        return entry;
    }

    /**
     * Get the last undone command for redo
     * Moves it back to undo stack
     */
    public HistoryEntry getLastForRedo() {
        if (redoStack.isEmpty()) {
            log.warning("Cannot redo: Nothing to redo");
            return null;
        }

        HistoryEntry entry = redoStack.pop();
        undoStack.push(entry);

        log.debug("Redo: slot=" + entry.slot() + ", wasOn=" + entry.wasOnButton() +
                  " | Undo size: " + undoStack.size() + ", Redo size: " + redoStack.size());

        return entry;
    }

    /**
     * Check if undo is available
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Check if redo is available
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Get undo stack size
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    /**
     * Get redo stack size
     */
    public int getRedoStackSize() {
        return redoStack.size();
    }

    /**
     * Clear all history
     */
    public void clear() {
        log.info("Clearing all command history");
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Get a copy of undo stack for debugging
     */
    public Stack<HistoryEntry> getUndoStackCopy() {
        return new Stack<HistoryEntry>() {{
            addAll(undoStack);
        }};
    }

    /**
     * Get a copy of redo stack for debugging
     */
    public Stack<HistoryEntry> getRedoStackCopy() {
        return new Stack<HistoryEntry>() {{
            addAll(redoStack);
        }};
    }

    /**
     * Record for a command execution in history
     */
    public record HistoryEntry(int slot, boolean wasOnButton) {
        @Override
        public String toString() {
            return "HistoryEntry{slot=" + slot + ", wasOn=" + wasOnButton + "}";
        }
    }
}
