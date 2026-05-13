package remote;
import receiver.Licht;
import Utils.LoggerFactory;
import Utils.Logger;
public class CommandLightOn implements Command {
    private static Logger log = LoggerFactory.getLogger("Lights_on");
    private Licht commandTarget;
    public CommandLightOn(Licht commandTarget){
        log.debug("Creating new LightsOn Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating LightsOn object: "+this);
    }
    public void execute(){
        log.info(this+"; Turning lights on");
        commandTarget.setLichtAn();
    }

}
