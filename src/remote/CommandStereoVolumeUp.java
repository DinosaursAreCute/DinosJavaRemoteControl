package remote;

import Utils.Logger;
import Utils.LoggerFactory;
import receiver.Stereoanlage;

public class CommandStereoVolumeUp implements Command{
    private final Stereoanlage commandTarget;
    private final static Logger log = LoggerFactory.getLogger("StereoOn");

    public CommandStereoVolumeUp(Stereoanlage commandTarget){
        log.debug("Creating new StereoVolumeUp Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating StereoVolumeUp object: "+this);
    }

    public void execute(){
        log.info(this+"; Volume up: +10");
        commandTarget.setLautstaerke(commandTarget.getLautstaerke()+10);
    }
}
