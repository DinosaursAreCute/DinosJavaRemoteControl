//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import Utils.LoggerFactory;
import Utils.Logger;
import Utils.SessionStats;
import commands.CommandRegistry;
import receiver.*;
import remoteClasses.Remote;

public class Main {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("Main");
        log.info("Starting Dinos Remote Control System - Testing New Features");

        // Step 1: Initialize ReceiverRegistry with singleton receivers
        log.info("=== Step 1: Initializing Receivers ===");
        ReceiverRegistry receiverRegistry = ReceiverRegistry.getInstance();

        // Create one instance of each receiver type
        Licht light = new Licht();
        receiverRegistry.registerReceiver(Licht.class, light);

        Garage garage = new Garage();
        receiverRegistry.registerReceiver(Garage.class, garage);

        Stereoanlage stereo = new Stereoanlage();
        stereo.legeCDEin("Drugs and Guns for Everyone - The handsome devil");
        receiverRegistry.registerReceiver(Stereoanlage.class, stereo);

        log.success("All receivers registered");

        // Step 2: Initialize CommandRegistry and scan for commands
        log.info("=== Step 2: Scanning for Commands ===");
        CommandRegistry commandRegistry = CommandRegistry.getInstance();
        commandRegistry.scanAndRegister("commands");
        log.success("Command scanning complete");

        // Step 3: Create Remote and load configuration
        log.info("=== Step 3: Initializing Remote Control ===");
        Remote remote = new Remote();

        try {
            remote.loadConfiguration();
            log.success("Remote control initialized and configured with " + remote.getNumSlots() + " slots");
        } catch (Exception e) {
            log.error("Failed to load configuration: " + e.getMessage());
            log.warning("Remote will use FallbackCommand for all slots");
        }

        // Step 4: Test command execution with all features
        log.info("=== Step 4: Testing Command Execution ===");

        // Execute some individual commands
        log.info("Executing individual commands...");
        remote.executeFunction(0, true);   // Light ON
        remote.executeFunction(0, false);  // Light OFF
        remote.executeFunction(1, true);   // Garage UP
        remote.executeFunction(1, false);  // Garage DOWN

        // Execute stereo commands
        remote.executeFunction(2, true);   // Stereo ON
        remote.executeFunction(4, true);   // Play CD
        remote.executeFunction(3, true);   // Volume UP

        // Execute macro if available (slot 5)
        if (remote.getNumSlots() > 5) {
            log.info("Testing Party Mode macro...");
            remote.executeFunction(5, true);   // Party Mode ON (macro)
        }

        // Step 5: Test Undo/Redo functionality
        log.info("=== Step 5: Testing Undo/Redo Functionality ===");

        log.info("Can undo? " + remote.canUndo());
        log.info("Can redo? " + remote.canRedo());

        // Undo last 3 commands
        log.info("Performing 3 undo operations...");
        for (int i = 0; i < 3; i++) {
            if (remote.canUndo()) {
                remote.undo();
            }
        }

        log.info("After undo - Can redo? " + remote.canRedo());

        // Redo 2 commands
        log.info("Performing 2 redo operations...");
        for (int i = 0; i < 2; i++) {
            if (remote.canRedo()) {
                remote.redo();
            }
        }

        log.info("After redo - Can undo? " + remote.canUndo());
        log.info("After redo - Can redo? " + remote.canRedo());

        // Execute new command (should clear redo stack)
        log.info("Executing new command (should clear redo stack)...");
        remote.executeFunction(3, false);  // Volume DOWN
        log.info("After new command - Can redo? " + remote.canRedo() + " (should be false)");

        // Step 6: Display Session Statistics
        log.info("=== Step 6: Session Statistics ===");
        SessionStats stats = remote.getSessionStats();

        log.info("Commands Executed:     " + stats.getCommandsExecuted());
        log.info("Macros Executed:       " + stats.getMacrosExecuted());
        log.info("Undo Operations:       " + stats.getUndoOperations());
        log.info("Redo Operations:       " + stats.getRedoOperations());
        log.info("Failed Commands:       " + stats.getFailedCommands());
        log.info("Session Duration:      " + stats.getSessionDurationFormatted());
        log.info("Average Exec Time:     " + stats.getAverageExecutionTimeMs() + "ms");
        log.info("Most Used Command:     " + stats.getMostExecutedCommand());

        // Step 7: Display full formatted report
        log.info("=== Step 7: Full Statistics Report ===");
        System.out.println(stats.getFormattedReport());

        // Step 8: Test command history
        log.info("=== Step 8: Command History Info ===");
        log.info("Undo stack size: " + remote.getCommandHistory().getUndoStackSize());
        log.info("Redo stack size: " + remote.getCommandHistory().getRedoStackSize());

        log.success("=== All Tests Complete! ===");
        log.info("System is ready for GUI implementation");
    }
}




