package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Stereoanlage;

@CommandInfo(
    name = "Stop Playback",
    description = "Stop playback on stereo",
    category = "Audio",
    receiverType = Stereoanlage.class
)
public class StereoStopPlaybackCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("StereoStopPlaybackCommand");
    private final Stereoanlage stereo;

    public StereoStopPlaybackCommand(Stereoanlage stereo) {
        log.debug("Creating StereoStopPlaybackCommand with target: " + stereo);
        this.stereo = stereo;
        log.success("Successfully created StereoStopPlaybackCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing StereoStopPlaybackCommand - stopping playback");
        stereo.stoppePlayback();
    }
}
