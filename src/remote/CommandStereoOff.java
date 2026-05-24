package remote;

import Utils.Logger;
import Utils.LoggerFactory;
import receiver.Stereoanlage;

public class CommandStereoOff implements Command{
    private final Stereoanlage commandTarget;
    private final static Logger log = LoggerFactory.getLogger("StereoOff");

    public CommandStereoOff(Stereoanlage commandTarget){
        log.debug("Creating new StereoOff Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating StereoOff object: "+this);
    }

    public void execute(){
        log.info(this+"; Turning Stereo off");
        commandTarget.ausschalten();
    }
}
