package gui;

import javafx.application.Application;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import Utils.Logger;
import Utils.LoggerFactory;
import Utils.SessionStats;
import commands.Command;
import commands.CommandRegistry;
import commands.CommandWithProgress;
import receiver.*;
import remoteClasses.Remote;

/**
 * Main JavaFX GUI for the Remote Control application.
 * Features:
 * - 2xN button grid layout
 * - Edit Commands and Open Debug buttons
 * - Undo/Redo functionality
 * - Dark theme with external CSS
 */
public class mainView extends Application {
    private static final Logger log = LoggerFactory.getLogger("MainView");

    private Remote remote;
    private VBox buttonGrid;
    private Button btnUndo;
    private Button btnRedo;
    private Stage debugWindow;
    private ProgressOverlay progressOverlay;  // Global progress overlay

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting JavaFX GUI");

        // Initialize backend
        initializeBackend();

        // Create main layout
        BorderPane root = new BorderPane();

        // Create progress overlay (will be on top)
        progressOverlay = new ProgressOverlay();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create toolbar with Edit Commands and Open Debug buttons
        HBox toolbar = createToolbar();

        // Create button grid with scroll pane
        ScrollPane scrollPane = createButtonGrid();

        // Create undo/redo controls
        HBox controls = createControls();

        // Combine toolbar, grid, and controls
        VBox centerContent = new VBox(10);
        centerContent.getChildren().addAll(toolbar, scrollPane, controls);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.setCenter(centerContent);

        // Wrap in StackPane to layer progress overlay on top
        StackPane sceneRoot = new StackPane();
        sceneRoot.getChildren().addAll(root, progressOverlay);
        StackPane.setAlignment(progressOverlay, Pos.CENTER);

        // Create scene with layered root
        Scene scene = new Scene(sceneRoot, 600, 700);

        // Try multiple CSS loading strategies
        boolean cssLoaded = false;

        // Strategy 1: Try loading from resources (Maven standard)
        try {
            String cssResource = getClass().getResource("/remote-control-dark.css").toExternalForm();
            scene.getStylesheets().add(cssResource);
            log.info("CSS loaded from resources: " + cssResource);
            cssLoaded = true;
        } catch (Exception e) {
            log.debug("Could not load CSS from resources: " + e.getMessage());
        }

        // Strategy 2: Try loading from file system
        if (!cssLoaded) {
            try {
                String cssPath = "file:src/gui/remote-control-dark.css";
                scene.getStylesheets().add(cssPath);
                log.info("CSS loaded from file: " + cssPath);
                cssLoaded = true;
            } catch (Exception e) {
                log.debug("Could not load CSS from file: " + e.getMessage());
            }
        }

        // Strategy 3: Apply inline CSS as fallback
        if (!cssLoaded) {
            log.warning("Could not load external CSS, applying inline fallback styling");
            applyInlineCSS(root, menuBar, buttonGrid);
        }

        primaryStage.setTitle("Dinos Remote Control");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            log.info("Application closing");
            if (debugWindow != null) {
                debugWindow.close();
            }
        });

        primaryStage.show();
        log.success("JavaFX GUI started successfully");
    }

    /**
     * Initialize the backend system
     */
    private void initializeBackend() {
        log.info("Initializing backend system");

        // Initialize receivers
        ReceiverRegistry receiverRegistry = ReceiverRegistry.getInstance();
        receiverRegistry.registerReceiver(Licht.class, new Licht());
        receiverRegistry.registerReceiver(Garage.class, new Garage());

        Stereoanlage stereo = new Stereoanlage();
        stereo.legeCDEin("Drugs and Guns for Everyone - The handsome devil");
        receiverRegistry.registerReceiver(Stereoanlage.class, stereo);

        // Scan for commands
        CommandRegistry.getInstance().scanAndRegister("commands");

        // Create and configure remote
        remote = new Remote();
        remote.loadConfiguration();

        log.success("Backend initialized");
    }

    /**
     * Create the menu bar
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(
            new MenuItem("New"),
            new MenuItem("Open..."),
            new MenuItem("Save"),
            new MenuItem("Save As..."),
            new SeparatorMenuItem(),
            exitItem
        );

        // Edit menu
        Menu editMenu = new Menu("Edit");
        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction(e -> performUndo());
        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction(e -> performRedo());
        editMenu.getItems().addAll(undoItem, redoItem);

        // Options menu
        Menu optionsMenu = new Menu("Options");
        optionsMenu.getItems().addAll(
            new MenuItem("Preferences..."),
            new MenuItem("Configure Commands...")
        );

        // Tools menu
        Menu toolsMenu = new Menu("Tools");
        MenuItem debugItem = new MenuItem("Open Debug Window");
        debugItem.setOnAction(e -> openDebugWindow());
        toolsMenu.getItems().add(debugItem);

        // Help menu
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(
            new MenuItem("Documentation"),
            new MenuItem("About")
        );

        // Window menu
        Menu windowMenu = new Menu("Window");
        windowMenu.getItems().addAll(
            new MenuItem("Minimize"),
            new MenuItem("Zoom")
        );

        menuBar.getMenus().addAll(fileMenu, editMenu, optionsMenu, toolsMenu, helpMenu, windowMenu);
        return menuBar;
    }

    /**
     * Create toolbar with Edit Commands and Open Debug buttons
     */
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER);
        toolbar.getStyleClass().add("toolbar");

        Button btnEditCommands = new Button("Edit Commands");
        btnEditCommands.getStyleClass().add("toolbar-button");
        btnEditCommands.setOnAction(e -> openEditCommandsDialog());

        Button btnOpenDebug = new Button("Open Debug");
        btnOpenDebug.getStyleClass().add("toolbar-button");
        btnOpenDebug.setOnAction(e -> openDebugWindow());

        toolbar.getChildren().addAll(btnEditCommands, btnOpenDebug);
        return toolbar;
    }

    /**
     * Create the button grid with ON/OFF buttons for each slot
     */
    private ScrollPane createButtonGrid() {
        buttonGrid = new VBox(15);
        buttonGrid.setPadding(new Insets(20));
        buttonGrid.setAlignment(Pos.TOP_CENTER);
        buttonGrid.getStyleClass().add("button-grid");

        // Create buttons for each configured slot
        for (int slot = 0; slot < remote.getNumSlots(); slot++) {
            if (remote.isSlotConfigured(slot)) {
                HBox slotBox = createSlotButtons(slot);
                buttonGrid.getChildren().add(slotBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(buttonGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return scrollPane;
    }

    /**
     * Create ON/OFF button pair for a slot
     */
    private HBox createSlotButtons(int slot) {
        HBox slotBox = new HBox(15);
        slotBox.setAlignment(Pos.CENTER);

        // Get command IDs (macro IDs for macros, class names for regular commands)
        String onCommandId = remote.getCommandId(slot, true);
        String offCommandId = remote.getCommandId(slot, false);

        // Format command names for button labels
        String onLabel = formatCommandLabel(onCommandId);
        String offLabel = formatCommandLabel(offCommandId);

        // Create ON button
        Button onButton = new Button(onLabel);
        onButton.getStyleClass().add("remote-button");
        onButton.setTooltip(new Tooltip(onCommandId != null ? onCommandId : "No command"));
        onButton.setOnAction(e -> executeCommand(slot, true, onCommandId, onLabel));

        // Create OFF button
        Button offButton = new Button(offLabel);
        offButton.getStyleClass().add("remote-button");
        offButton.setTooltip(new Tooltip(offCommandId != null ? offCommandId : "No command"));
        offButton.setOnAction(e -> executeCommand(slot, false, offCommandId, offLabel));

        slotBox.getChildren().addAll(onButton, offButton);

        return slotBox;
    }


    /**
     * Format command name for button label
     * For macros: retrieves display name from registry (e.g., "partyMode_On" -> "Party Mode")
     * For regular commands: converts camelCase (e.g., "LightOnCommand" -> "Light On")
     */
    private String formatCommandLabel(String commandName) {
        if (commandName == null || commandName.isEmpty()) {
            return "Empty";
        }

        // Check if this is a macro (contains underscore pattern like "macroId_On" or "macroId_Off")
        if (commandName.contains("_")) {
            // Try to get metadata from registry to get the display name
            try {
                CommandRegistry registry = CommandRegistry.getInstance();
                commands.CommandMetadata metadata = registry.getCommandMetadata(commandName);
                if (metadata != null) {
                    // Extract macro name from metadata name (e.g., "partyMode_On" -> "Party Mode")
                    String name = metadata.name();
                    if (name != null && !name.isEmpty()) {
                        return name;
                    }
                }
            } catch (Exception e) {
                log.debug("Could not retrieve metadata for " + commandName);
            }

            // Fallback: parse macro ID from command name (e.g., "partyMode_On" -> "Party Mode")
            String[] parts = commandName.split("_");
            if (parts.length > 0) {
                String macroId = parts[0];
                // Convert camelCase to Title Case (partyMode -> Party Mode)
                String formatted = macroId.replaceAll("([A-Z])", " $1").trim();
                if (formatted.isEmpty()) {
                    formatted = macroId;
                }
                return formatted;
            }
        }

        // Regular command handling
        String label = commandName.replace("Command", "");

        // Split camelCase into words
        // e.g., "LightOn" -> "Light On", "StereoVolumeUp" -> "Stereo Volume Up"
        label = label.replaceAll("([A-Z])", " $1").trim();

        // Limit length to fit on button (max 15 chars)
        if (label.length() > 15) {
            label = label.substring(0, 12) + "...";
        }

        return label;
    }

    /**
     * Create undo/redo control buttons
     */
    private HBox createControls() {
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        btnUndo = new Button("UNDO");
        btnUndo.getStyleClass().add("control-button");
        btnUndo.setOnAction(e -> performUndo());

        btnRedo = new Button("REDO");
        btnRedo.getStyleClass().add("control-button");
        btnRedo.setOnAction(e -> performRedo());

        controls.getChildren().addAll(btnUndo, btnRedo);

        updateControlButtons();
        return controls;
    }

    /**
     * Execute a command with global progress overlay showing actual elapsed time
     */
    private void executeCommand(int slot, boolean isOn, String commandId, String displayLabel) {
        log.info("Button pressed: Slot " + slot + " " + (isOn ? "ON" : "OFF"));

        // Get command for duration and execution
        Command cmd = isOn ? remote.getOnCommand(slot) : remote.getOffCommand(slot);
        long durationMs = (cmd instanceof CommandWithProgress)
            ? ((CommandWithProgress) cmd).getDurationMs()
            : 250;

        // Show progress overlay with task name
        progressOverlay.show(displayLabel);

        // If this is a macro, set progress listener to track child commands
        if (cmd instanceof commands.macro.MacroCommand) {
            ((commands.macro.MacroCommand) cmd).setProgressListener(progressOverlay);
        }

        // Use array to hold timeline reference (allows modification in lambda)
        final Timeline[] progressTimeline = new Timeline[1];

        // Run command asynchronously
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long startTime = System.currentTimeMillis();

                // Execute command on background thread
                remote.executeFunction(slot, isOn);

                // Ensure we track actual elapsed time
                long elapsedMs = System.currentTimeMillis() - startTime;
                log.debug("Command executed in " + elapsedMs + "ms (expected " + durationMs + "ms)");

                return null;
            }
        };

        // Update progress bar based on actual elapsed time during execution
        task.setOnRunning(e -> {
            long startTime = System.currentTimeMillis();

            // Update progress every 50ms
            Timeline progressUpdate = new Timeline(
                new KeyFrame(
                    Duration.millis(50),
                    event -> {
                        long elapsedMs = System.currentTimeMillis() - startTime;
                        double progress = Math.min((double) elapsedMs / durationMs, 1.0);

                        // Only update if not a macro (macros update via ProgressListener)
                        if (!(cmd instanceof commands.macro.MacroCommand)) {
                            progressOverlay.updateProgress(progress);
                        }
                    }
                )
            );
            progressUpdate.setCycleCount(Timeline.INDEFINITE);
            progressUpdate.play();

            // Store timeline reference for cleanup
            progressTimeline[0] = progressUpdate;
        });

        // Clean up after command completes
        task.setOnSucceeded(e -> {
            // Stop progress animation
            if (progressTimeline[0] != null) {
                progressTimeline[0].stop();
            }

            // Hide overlay
            progressOverlay.hide();
            updateControlButtons();
        });

        task.setOnFailed(e -> {
            log.error("Command execution failed: " + task.getException().getMessage());

            // Stop progress animation
            if (progressTimeline[0] != null) {
                progressTimeline[0].stop();
            }

            progressOverlay.hide();
        });

        // Start execution on background thread
        new Thread(task).start();
    }


    /**
     * Perform undo operation
     */
    private void performUndo() {
        if (remote.canUndo()) {
            remote.undo();
            updateControlButtons();
        }
    }

    /**
     * Perform redo operation
     */
    private void performRedo() {
        if (remote.canRedo()) {
            remote.redo();
            updateControlButtons();
        }
    }

    /**
     * Update undo/redo button states
     */
    private void updateControlButtons() {
        btnUndo.setDisable(!remote.canUndo());
        btnRedo.setDisable(!remote.canRedo());
    }

    /**
     * Open the Edit Commands dialog
     */
    private void openEditCommandsDialog() {
        log.info("Opening Edit Commands dialog");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit Commands");
        alert.setHeaderText("Edit Command Configuration");
        alert.setContentText("Edit Commands dialog - Coming soon!\n\nYou can edit the configuration file at:\nAppData/config/remote-config.properties");
        alert.showAndWait();
    }

    /**
     * Open the Debug window
     */
    private void openDebugWindow() {
        if (debugWindow != null && debugWindow.isShowing()) {
            debugWindow.toFront();
            return;
        }

        log.info("Opening Debug window");

        debugWindow = new Stage();
        debugWindow.setTitle("Debug Panel");

        TabPane tabPane = new TabPane();

        // Tab 1: Log Output
        Tab logTab = new Tab("Log Output");
        logTab.setClosable(false);
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setText("Log output will appear here...\n(Real-time log integration coming soon)");
        logTab.setContent(logArea);

        // Tab 2: Receiver States
        Tab statesTab = new Tab("Receiver States");
        statesTab.setClosable(false);
        TextArea statesArea = new TextArea();
        statesArea.setEditable(false);
        statesArea.setText("Receiver States:\n\n(State tracking coming soon)");
        statesTab.setContent(statesArea);

        // Tab 3: Session Stats
        Tab statsTab = new Tab("Session Stats");
        statsTab.setClosable(false);
        TextArea statsArea = new TextArea();
        statsArea.setEditable(false);
        updateSessionStats(statsArea);
        statsTab.setContent(statsArea);

        // Refresh stats when tab is selected
        statsTab.setOnSelectionChanged(e -> {
            if (statsTab.isSelected()) {
                updateSessionStats(statsArea);
            }
        });

        tabPane.getTabs().addAll(logTab, statesTab, statsTab);

        Scene scene = new Scene(tabPane, 600, 400);

        // Load CSS file
        try {
            String cssPath = "file:src/gui/remote-control-dark.css";
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            log.warning("Could not load CSS for debug window: " + e.getMessage());
        }

        debugWindow.setScene(scene);
        debugWindow.show();
    }

    /**
     * Update session statistics display
     */
    private void updateSessionStats(TextArea statsArea) {
        SessionStats stats = remote.getSessionStats();
        statsArea.setText(stats.getFormattedReport());
    }

    /**
     * Apply inline CSS styling as fallback
     */
    private void applyInlineCSS(BorderPane root, MenuBar menuBar, VBox buttonGrid) {
        // Root background
        root.setStyle("-fx-background-color: #1e1e1e;");

        // Menu bar
        menuBar.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #555555; -fx-border-width: 0 0 1 0;");

        // Button grid
        buttonGrid.setStyle("-fx-background-color: #1e1e1e;");

        // Style all buttons
        buttonGrid.getChildren().forEach(node -> {
            if (node instanceof HBox hbox) {
	            hbox.getChildren().forEach(child -> {
                    if (child instanceof Button btn) {
	                    btn.setStyle(
                            "-fx-background-color: #0078d4; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 15 30 15 30; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand;"
                        );
                    }
                });
            }
        });

        // Style undo/redo buttons
        if (btnUndo != null) {
            btnUndo.setStyle(
                "-fx-background-color: #555555; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 13px; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-background-radius: 5;"
            );
        }
        if (btnRedo != null) {
            btnRedo.setStyle(
                "-fx-background-color: #555555; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 13px; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-background-radius: 5;"
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
