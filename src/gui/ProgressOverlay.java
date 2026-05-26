package gui;

import commands.ProgressListener;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Global progress overlay for command execution
 * Displays task name, progress bar, and percentage in center of screen
 * Implements ProgressListener to track macro child commands accurately
 */
public class ProgressOverlay extends StackPane implements ProgressListener {
    private final ProgressBar progressBar;
    private final Label taskLabel;
    private final Label percentLabel;
    private final Label stepLabel;
    private final VBox container;
    private int currentStep = 0;
    private int totalSteps = 1;

    public ProgressOverlay() {
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        this.setVisible(false);

        // Container for progress UI
        container = new VBox(15);
        container.setStyle("-fx-padding: 30; -fx-background-color: rgba(45, 45, 45, 0.95); -fx-border-radius: 10; -fx-background-radius: 10;");
        container.setPrefWidth(400);
        container.setPrefHeight(180);
        container.setAlignment(Pos.CENTER);
        container.setStyle(container.getStyle() + " -fx-border-color: #007bff; -fx-border-width: 2;");

        // Task name label
        taskLabel = new Label("Executing...");
        taskLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16; -fx-font-weight: bold;");

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);
        progressBar.setPrefHeight(20);
        progressBar.setStyle("-fx-accent: #4CAF50;");

        // Percentage label
        percentLabel = new Label("0%");
        percentLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14;");

        // Step label (for showing child command progress)
        stepLabel = new Label("");
        stepLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12;");

        container.getChildren().addAll(taskLabel, progressBar, percentLabel, stepLabel);
        this.getChildren().add(container);
        StackPane.setAlignment(container, Pos.CENTER);
    }

    /**
     * Show progress overlay with task name
     */
    public void show(String taskName) {
        taskLabel.setText(taskName);
        progressBar.setProgress(0);
        percentLabel.setText("0%");
        stepLabel.setText("");
        currentStep = 0;
        totalSteps = 1;
        this.setVisible(true);
    }

    /**
     * Update progress (0.0 to 1.0)
     * Must be called from FX thread
     */
    private void updateProgressUI(double progress) {
        progressBar.setProgress(Math.min(progress, 1.0));
        int percent = (int) (progress * 100);
        percentLabel.setText(percent + "%");
    }

    /**
     * Update progress safely from any thread
     */
    public void updateProgress(double progress) {
        Platform.runLater(() -> updateProgressUI(progress));
    }

    /**
     * Hide overlay with fade delay
     */
    public void hide() {
        PauseTransition pause = new PauseTransition(Duration.millis(200));
        pause.setOnFinished(e -> this.setVisible(false));
        pause.play();
    }

    // ===== ProgressListener Implementation =====

    @Override
    public void onProgress(int currentStep, int totalSteps, String stepName) {
        Platform.runLater(() -> {
            this.currentStep = currentStep;
            this.totalSteps = totalSteps;

            // Update step label with child command info
            String stepText = String.format("Step %d/%d: %s", currentStep + 1, totalSteps, stepName);
            stepLabel.setText(stepText);

            // Update progress based on step completion (currentStep + 1 because we're executing that step)
            // Progress goes from 1/N to N/N as we move through steps 0 to totalSteps-1
            double progress = (double) (currentStep + 1) / totalSteps;
            updateProgressUI(progress);  // Direct call since we're already on FX thread via Platform.runLater
        });
    }

    @Override
    public void onStepComplete() {
        Platform.runLater(() -> {
            // Progress bar will be updated by onProgress for next step
        });
    }

    @Override
    public void onComplete() {
        Platform.runLater(() -> {
            // Set to 100% when complete
            updateProgressUI(1.0);  // Direct call since we're on FX thread
            stepLabel.setText("Complete!");
        });
    }

    @Override
    public void onError(String errorMessage) {
        Platform.runLater(() -> {
            stepLabel.setText("Error: " + errorMessage);
            stepLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
        });
    }
}

