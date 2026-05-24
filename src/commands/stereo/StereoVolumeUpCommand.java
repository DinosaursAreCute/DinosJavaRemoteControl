package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import receiver.Stereoanlage;

public class StereoVolumeUpCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger("StereoVolumeUpCommand");
    private final Stereoanlage stereo;

    public StereoVolumeUpCommand(Stereoanlage stereo) {
        log.debug("Creating StereoVolumeUpCommand with target: " + stereo);
        this.stereo = stereo;
        log.success("Successfully created StereoVolumeUpCommand: " + this);
    }

    @Override
    public void execute() {
        int currentVolume = stereo.getLautstaerke();
        int newVolume = currentVolume + 10;
        log.info("Executing StereoVolumeUpCommand - increasing volume from " + currentVolume + " to " + newVolume);
        stereo.setLautstaerke(newVolume);
    }
}
