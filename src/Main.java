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
        log.info("Starting Dinos Remote Control System - CLI Regression Mode (FULL)");

        ReceiverRegistry receiverRegistry = initializeReceivers(log);
        scanCommands(log);
        Remote remote = initializeRemote(log);

        runFullRegressionScenario(remote, log);
        printSessionSummary(remote, log);

        log.success("=== CLI FULL REGRESSION COMPLETE ===");
    }

    private static ReceiverRegistry initializeReceivers(Logger log) {
        step(log, "Initialize Receivers", () -> {
            ReceiverRegistry receiverRegistry = ReceiverRegistry.getInstance();

            // One singleton-like instance per receiver type.
            Licht light = new Licht();
            receiverRegistry.registerReceiver(Licht.class, light);

            Garage garage = new Garage();
            receiverRegistry.registerReceiver(Garage.class, garage);

            Stereoanlage stereo = new Stereoanlage();
            stereo.legeCDEin("Drugs and Guns for Everyone - The handsome devil");
            receiverRegistry.registerReceiver(Stereoanlage.class, stereo);

            log.success("All receivers registered");
        });

        return ReceiverRegistry.getInstance();
    }

    private static void scanCommands(Logger log) {
        step(log, "Scan and Register Commands", () -> {
            CommandRegistry commandRegistry = CommandRegistry.getInstance();
            commandRegistry.scanAndRegister("commands");
            log.success("Command scanning complete");
        });
    }

    private static Remote initializeRemote(Logger log) {
        final Remote remote = new Remote();

        step(log, "Initialize Remote from Config", () -> {
            try {
                remote.loadConfiguration();
                log.success("Remote configured with " + remote.getNumSlots() + " slots");
            } catch (Exception e) {
                log.error("Failed to load configuration: " + e.getMessage());
                log.warning("Remote will use fallback commands for all slots");
            }
        });

        return remote;
    }

    private static void runFullRegressionScenario(Remote remote, Logger log) {
        log.info("=== SCENARIO START: FULL ===");

        step(log, "Execute Base ON/OFF Commands", () -> {
            execute(log, remote, 0, true, "Slot 0 ON");
            execute(log, remote, 0, false, "Slot 0 OFF");
            execute(log, remote, 1, true, "Slot 1 ON");
            execute(log, remote, 1, false, "Slot 1 OFF");
        });

        step(log, "Execute Stereo Flow", () -> {
            execute(log, remote, 2, true, "Slot 2 ON");
            execute(log, remote, 4, true, "Slot 4 ON");
            execute(log, remote, 3, true, "Slot 3 ON");
        });

        step(log, "Execute Macro Slot If Present", () -> {
            if (remote.getNumSlots() > 5) {
                execute(log, remote, 5, true, "Slot 5 ON (Macro)");
                execute(log, remote, 5, false, "Slot 5 OFF (Macro)");
            } else {
                log.warning("Macro slot 5 not configured; skipping macro regression step");
            }
        });

        step(log, "Undo Chain", () -> {
            log.info("Undo available before chain: " + remote.canUndo());
            while (remote.canUndo()) {
                remote.undo();
            }
            log.info("Undo available after chain: " + remote.canUndo());
            log.info("Redo available after chain: " + remote.canRedo());
        });

        step(log, "Redo Chain", () -> {
            log.info("Redo available before chain: " + remote.canRedo());
            while (remote.canRedo()) {
                remote.redo();
            }
            log.info("Undo available after chain: " + remote.canUndo());
            log.info("Redo available after chain: " + remote.canRedo());
        });

        step(log, "Redo Clear Check", () -> {
            execute(log, remote, 3, false, "Slot 3 OFF (new command)");
            log.info("Redo available after new command: " + remote.canRedo() + " (expected false)");
        });

        log.info("=== SCENARIO END: FULL ===");
    }

    private static void printSessionSummary(Remote remote, Logger log) {
        step(log, "Session Statistics", () -> {
            SessionStats stats = remote.getSessionStats();

            log.info("Commands Executed: " + stats.getCommandsExecuted());
            log.info("Macros Executed:   " + stats.getMacrosExecuted());
            log.info("Undo Operations:   " + stats.getUndoOperations());
            log.info("Redo Operations:   " + stats.getRedoOperations());
            log.info("Failed Commands:   " + stats.getFailedCommands());
            log.info("Session Duration:  " + stats.getSessionDurationFormatted());
            log.info("Avg Exec Time:     " + stats.getAverageExecutionTimeMs() + "ms");
            log.info("Most Used Command: " + stats.getMostExecutedCommand());

            System.out.println(stats.getFormattedReport());
            log.info("Undo stack size: " + remote.getCommandHistory().getUndoStackSize());
            log.info("Redo stack size: " + remote.getCommandHistory().getRedoStackSize());
        });
    }

    private static void execute(Logger log, Remote remote, int slot, boolean isOn, String label) {
        log.info("Execute -> " + label + " [slot=" + slot + ", isOn=" + isOn + "]");
        remote.executeFunction(slot, isOn);
    }

    private static void step(Logger log, String title, Runnable action) {
        log.info("=== STEP START: " + title + " ===");
        action.run();
        log.success("=== STEP END: " + title + " ===");
    }
}
