package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Stereoanlage;

@CommandInfo(
    name = "Stereo Off",
    description = "Turn the stereo system off",
    category = "Audio",
    receiverType = Stereoanlage.class
)
public class StereoOffCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("StereoOffCommand");
    private final Stereoanlage stereo;

    public StereoOffCommand(Stereoanlage stereo) {
        log.debug("Creating StereoOffCommand with target: " + stereo);
        this.stereo = stereo;
        log.success("Successfully created StereoOffCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing StereoOffCommand - turning stereo off");
        stereo.ausschalten();
        applyDuration();
    }
}
