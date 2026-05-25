package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Stereoanlage;

@CommandInfo(
    name = "Play CD",
    description = "Start CD playback on stereo",
    category = "Audio",
    receiverType = Stereoanlage.class
)
public class StereoPlayCDCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("StereoPlayCDCommand");
    private final Stereoanlage stereo;

    public StereoPlayCDCommand(Stereoanlage stereo) {
        log.debug("Creating StereoPlayCDCommand with target: " + stereo);
        this.stereo = stereo;
        log.success("Successfully created StereoPlayCDCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing StereoPlayCDCommand - starting CD playback");
        stereo.spieleCDAb();
        applyDuration();
    }
}
