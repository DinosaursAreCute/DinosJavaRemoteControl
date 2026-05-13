package remote;
import receiver.Licht;
import Utils.LoggerFactory;
import Utils.Logger;
public class CommandLightOff implements Command {
    private static Logger log = LoggerFactory.getLogger("Lights_off");
    private Licht commandTarget;
    public CommandLightOff(Licht commandTarget){
        log.debug("Creating new LightsOff Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating LightsOff object: "+this);
    }
    public void execute(){
        log.info(this+"; Turning lights off");
        commandTarget.setLichtAus();
    }

}
