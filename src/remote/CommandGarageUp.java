package remote;
import Utils.LoggerFactory;
import Utils.Logger;
import receiver.Garage;

public class CommandGarageUp implements Command {
    private static Logger log = LoggerFactory.getLogger("Garage_Up");
    private Garage commandTarget;

    public CommandGarageUp(Garage commandTarget){
        log.debug("Creating new GarageUp Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating GarageUp object: "+this);
    }


    @Override
    public void execute() {
        log.info(this+"; Garage_open");
        commandTarget.hoch();
    }
}
