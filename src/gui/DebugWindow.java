package gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import Utils.Logger;
import Utils.LoggerFactory;
import receiver.Garage;
import receiver.Licht;
import receiver.ReceiverRegistry;
import receiver.Stereoanlage;
import remoteClasses.Remote;

/**
 * Debug Window for monitoring application state and logs
 * Displays in a separate window with 3 tabs:
 * 1. Log Output - Console logs
 * 2. Receiver States - Current state of each receiver
 * 3. Session Stats - Statistics about executed commands
 */
public class DebugWindow {
    private static final Logger log = LoggerFactory.getLogger("DebugWindow");

    private Stage window;
    private Remote remote;
    private TextArea logArea;
    private TextArea statesArea;
    private TextArea statsArea;
    private StringBuilder logBuffer = new StringBuilder();
    private static final int MAX_LOG_LINES = 500;
    private static final int MAX_LOG_SIZE = 50000;

    public DebugWindow(Remote remote) {
        this.remote = remote;
    }

    /**
     * Show the debug window
     */
    public void show() {
        if (window != null && window.isShowing()) {
            window.toFront();
            return;
        }

        log.debug("Opening Debug Window");

        window = new Stage();
        window.setTitle("Debug Panel");
        window.setWidth(800);
        window.setHeight(600);

        // Create tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Log Output
        //Tab logTab = createLogTab();

        // Tab 2: Receiver States
        Tab statesTab = createStatesTab();

        // Tab 3: Session Stats
        Tab statsTab = createStatsTab();

        tabPane.getTabs().addAll(statesTab, statsTab);// ,logTab);

        // Wrap in a container that will fill the scene
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root);

        // Load CSS
        try {
            String cssPath = "file:src/gui/remote-control-dark.css";
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            log.warning("Could not load CSS for debug window: " + e.getMessage());
        }

        window.setScene(scene);
        window.show();

        // Initialize with sample content
        addLog("=== Debug Window Started ===");
        addLog("Log Output: Console messages will appear here");
        updateReceiverStates();
        updateSessionStats();

        log.success("Debug Window opened");
    }

    /**
     * Close the debug window
     */
    public void close() {
        if (window != null) {
            window.close();
        }
    }

    /**
     * Check if debug window is showing
     */
    public boolean isShowing() {
        return window != null && window.isShowing();
    }

    /**
     * Create Log Output tab
     */
    private Tab createLogTab() {
        Tab logTab = new Tab("Log Output");
        logTab.setClosable(false);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(false);
        logArea.setPrefRowCount(20);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00;");

        // Add refresh button
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(5));
        Button clearButton = new Button("Clear Log");
        clearButton.setOnAction(e -> clearLog());
        controls.getChildren().add(clearButton);

        // Create container with controls on top and log area below
        VBox container = new VBox(5);
        container.setPadding(new Insets(5));
        container.getChildren().addAll(controls, logArea);
        VBox.setVgrow(logArea, javafx.scene.layout.Priority.ALWAYS);

        logTab.setContent(container);
        return logTab;
    }

    /**
     * Create Receiver States tab
     */
    private Tab createStatesTab() {
        Tab statesTab = new Tab("Receiver States");
        statesTab.setClosable(false);

        statesArea = new TextArea();
        statesArea.setEditable(false);
        statesArea.setWrapText(true);
        statesArea.setPrefRowCount(20);
        statesArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");

        // Add refresh button
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(5));
        Button refreshButton = new Button("Refresh States");
        refreshButton.setOnAction(e -> updateReceiverStates());
        controls.getChildren().add(refreshButton);

        // Create container with controls on top and states area below
        VBox container = new VBox(5);
        container.setPadding(new Insets(5));
        container.getChildren().addAll(controls, statesArea);
        VBox.setVgrow(statesArea, javafx.scene.layout.Priority.ALWAYS);

        statesTab.setContent(container);

        // Update on tab selection
        statesTab.setOnSelectionChanged(e -> {
            if (statesTab.isSelected()) {
                updateReceiverStates();
            }
        });

        return statesTab;
    }

    /**
     * Create Session Stats tab
     */
    private Tab createStatsTab() {
        Tab statsTab = new Tab("Session Stats");
        statsTab.setClosable(false);

        statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setWrapText(true);
        statsArea.setPrefRowCount(20);
        statsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");

        // Add refresh button
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(5));
        Button refreshButton = new Button("Refresh Stats");
        refreshButton.setOnAction(e -> updateSessionStats());
        controls.getChildren().add(refreshButton);

        // Create container with controls on top and stats area below
        VBox container = new VBox(5);
        container.setPadding(new Insets(5));
        container.getChildren().addAll(controls, statsArea);
        VBox.setVgrow(statsArea, javafx.scene.layout.Priority.ALWAYS);

        statsTab.setContent(container);

        // Update on tab selection
        statsTab.setOnSelectionChanged(e -> {
            if (statsTab.isSelected()) {
                updateSessionStats();
            }
        });

        return statsTab;
    }

    /**
     * Add a log message (thread-safe)
     */
    public void addLog(String message) {
        Platform.runLater(() -> {
            logBuffer.append(message).append("\n");

            // Trim buffer if it gets too large
            if (logBuffer.length() > MAX_LOG_SIZE) {
                String content = logBuffer.toString();
                int firstNewline = content.indexOf('\n', content.length() - MAX_LOG_SIZE / 2);
                if (firstNewline > 0) {
                    logBuffer = new StringBuilder(content.substring(firstNewline + 1));
                }
            }

            if (logArea != null) {
                // Set text directly to show all content
                String allText = logBuffer.toString();
                logArea.setText(allText);
                // Auto-scroll to bottom
                logArea.positionCaret(allText.length());
            }
        });
    }

    /**
     * Clear the log
     */
    private void clearLog() {
        logBuffer = new StringBuilder();
        if (logArea != null) {
            logArea.clear();
        }
    }

    /**
     * Update receiver states display
     */
    private void updateReceiverStates() {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=== RECEIVER STATES ===\n\n");

            ReceiverRegistry registry = ReceiverRegistry.getInstance();

            // Get each receiver and display its state
            try {
                // Lights
                Licht licht = registry.getReceiver(Licht.class);
                if (licht != null) {
                    sb.append("🔦 LICHT (Lights)\n");
                    sb.append("   State: ").append(licht.isLicht() ? "ON" : "OFF").append("\n");
                    sb.append("   Brightness: N/A\n\n");
                }

                // Garage
                Garage garage = registry.getReceiver(Garage.class);
                if (garage != null) {
                    sb.append("🚪 GARAGE\n");
                    sb.append("   State: ").append(garage.isHoch() ? "OPEN" : "CLOSED").append("\n");
                    sb.append("   Position: N/A\n\n");
                }

                // Stereo
                Stereoanlage stereo = registry.getReceiver(Stereoanlage.class);
                if (stereo != null) {
                    sb.append("🔊 STEREOANLAGE (Stereo System)\n");
                    sb.append("   Power: ").append(stereo.isAn() ? "ON" : "OFF").append("\n");
                    sb.append("   Mode: ").append(stereo.getCurrentMode()).append("\n");
                    sb.append("   Volume: ").append(stereo.getLautstaerke()).append("\n");
                    sb.append("   Current CD: N/A\n\n");
                }
            } catch (Exception e) {
                sb.append("ERROR: ").append(e.getMessage());
            }

            sb.append("\n=== Last Updated ===\n");
            sb.append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            if (statesArea != null) {
                statesArea.setText(sb.toString());
            }
        });
    }

    /**
     * Update session statistics display
     */
    private void updateSessionStats() {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();

            try {
                if (remote != null) {
                    String stats = remote.getSessionStats().getFormattedReport();
                    sb.append(stats);
                } else {
                    sb.append("Remote not initialized");
                }
            } catch (Exception e) {
                sb.append("ERROR reading statistics: ").append(e.getMessage());
            }

            if (statsArea != null) {
                statsArea.setText(sb.toString());
            }
        });
    }

    /**
     * Refresh all displays (called after a command executes)
     */
    public void refreshAll() {
        if (window != null && window.isShowing()) {
            updateReceiverStates();
            updateSessionStats();
        }
    }
}
