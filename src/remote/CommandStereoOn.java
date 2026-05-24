package remote;

import Utils.Logger;
import Utils.LoggerFactory;
import receiver.Stereoanlage;

public class CommandStereoOn implements Command{
    private final Stereoanlage commandTarget;
    private final static Logger log = LoggerFactory.getLogger("StereoOn");

    public CommandStereoOn(Stereoanlage commandTarget){
        log.debug("Creating new StereoOn Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating StereoOn object: "+this);
    }

    public void execute(){
        log.info(this+"; Turning Stereo on");
        commandTarget.anschalten();
        commandTarget.setLautstaerke(10);
    }
}
