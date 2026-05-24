package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Stereoanlage;

@CommandInfo(
    name = "Stereo On",
    description = "Turn the stereo system on",
    category = "Audio",
    receiverType = Stereoanlage.class
)
public class StereoOnCommand extends BaseCommand {
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
