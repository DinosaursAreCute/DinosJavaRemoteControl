package remote;

import Utils.Logger;
import Utils.LoggerFactory;
import receiver.Stereoanlage;

public class CommandStereoVolumeDown implements Command{
    private final Stereoanlage commandTarget;
    private final static Logger log = LoggerFactory.getLogger("StereoDown");

    public CommandStereoVolumeDown(Stereoanlage commandTarget){
        log.debug("Creating new StereoVolumeDown Object with args["+commandTarget.toString()+"]");
        this.commandTarget = commandTarget;
        log.success("Finished Creating StereoVolumeDown object: "+this);
    }

    public void execute(){
        log.info(this+"; Volume down: -10");
        commandTarget.setLautstaerke(commandTarget.getLautstaerke()-10);
    }
}
