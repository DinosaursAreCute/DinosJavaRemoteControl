package commands;

/**
 * Interface for commands that support duration and progress tracking.
 * Used for showing progress bars during macro execution.
 */
public interface CommandWithProgress extends Command {

    /**
     * Get the estimated duration of this command in milliseconds.
     * This is used for progress bar calculations during macro execution.
     *
     * @return Duration in milliseconds
     */
    long getDurationMs();

    /**
     * Execute the command with a progress callback.
     * Default implementation just calls execute() after the duration.
     *
     * @param progressCallback Callback to report progress (0.0 to 1.0)
     * @throws InterruptedException if execution is interrupted
     */
    default void executeWithProgress(ProgressCallback progressCallback) throws InterruptedException {
        long duration = getDurationMs();

        // Report start
        if (progressCallback != null) {
            progressCallback.onProgress(0.0);
        }

        // Execute the command
        execute();

        // Simulate command execution time
        if (duration > 0) {
            Thread.sleep(duration);
        }

        // Report completion
        if (progressCallback != null) {
            progressCallback.onProgress(1.0);
        }
    }

    /**
     * Callback interface for progress updates
     */
    @FunctionalInterface
    interface ProgressCallback {
        /**
         * Called to report progress
         * @param progress Value between 0.0 (start) and 1.0 (complete)
         */
        void onProgress(double progress);
    }
}
