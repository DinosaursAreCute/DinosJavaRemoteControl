package remote;

import Utils.Logger;
import Utils.LoggerFactory;

import java.util.Arrays;

public class CommandMakroCommand implements Command {
    private final Command[] commands;
    private final Logger log = LoggerFactory.getLogger("MakroCommand");

    public CommandMakroCommand(Command[] commandTarget){
        log.debug("Creating new Makro Command Object with args["+ Arrays.toString(commandTarget) +"]");
        this.commands = commandTarget;
        log.value("Makro commands: "+ Arrays.toString(commands));
        log.success("Finished Creating Makro Command object: "+this);
    }

    @Override
    public void execute() {
        log.info("executing ["+commands.length+"] commands.");
        for (int i = 0; i < commands.length; i++) {
            log.debug("Executing makro command ["+i+"]["+commands[i].toString()+"]");
            commands[i].execute();
        }
    }
}
