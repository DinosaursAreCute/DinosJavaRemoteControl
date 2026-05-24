package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import receiver.Stereoanlage;

public class StereoOnCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger("StereoOnCommand");
    private final Stereoanlage stereo;

    public StereoOnCommand(Stereoanlage stereo) {
        log.debug("Creating StereoOnCommand with target: " + stereo);
        this.stereo = stereo;
        log.success("Successfully created StereoOnCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing StereoOnCommand - turning stereo on");
        stereo.anschalten();
        stereo.setLautstaerke(10);
    }
}
