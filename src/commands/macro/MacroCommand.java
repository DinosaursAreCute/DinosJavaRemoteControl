package commands.macro;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;

import java.util.Arrays;

public class MacroCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger("MacroCommand");
    private final Command[] commands;

    public MacroCommand(Command[] commands) {
        log.debug("Creating MacroCommand with " + commands.length + " commands: " + Arrays.toString(commands));
        this.commands = commands;
        log.value("Macro command list: " + Arrays.toString(commands));
        log.success("Successfully created MacroCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing MacroCommand - running " + commands.length + " commands");
        for (int i = 0; i < commands.length; i++) {
            log.debug("Executing macro command [" + i + "]: " + commands[i]);
            commands[i].execute();
        }
        log.success("MacroCommand execution completed");
    }
}
