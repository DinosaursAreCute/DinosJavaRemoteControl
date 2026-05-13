package remote;
import Utils.LoggerFactory;
import Utils.Logger;
import receiver.Garage;

public class CommandGarageDown implements Command {
    private static Logger log = LoggerFactory.getLogger("Garage_Down");
    private Garage commandTarget;

    public CommandGarageDown(Garage commandTarget){
        log.debug("Creating new GarageDown Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating GarageDown object: "+this);
    }


    @Override
    public void execute() {
        log.info(this+"; Garage_open");
        commandTarget.runter();
    }
}
