package commands;

/**
 * Listener interface for tracking detailed progress of command execution
 * Used by macros to report progress of child commands
 */
public interface ProgressListener {
    /**
     * Called when progress updates during command execution
     * @param currentStep Current step number (0-based)
     * @param totalSteps Total number of steps
     * @param stepName Name/description of current step
     */
    void onProgress(int currentStep, int totalSteps, String stepName);

    /**
     * Called when a step completes
     */
    void onStepComplete();

    /**
     * Called when command execution completes
     */
    void onComplete();

    /**
     * Called on error
     */
    void onError(String errorMessage);
}
