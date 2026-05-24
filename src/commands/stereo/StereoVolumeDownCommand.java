package commands.stereo;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import receiver.Stereoanlage;

public class StereoVolumeDownCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger("StereoVolumeDownCommand");
    private final Stereoanlage stereo;

    public StereoVolumeDownCommand(Stereoanlage stereo) {
        log.debug("Creating StereoVolumeDownCommand with target: " + stereo);
        this.stereo = stereo;
        log.success("Successfully created StereoVolumeDownCommand: " + this);
    }

    @Override
    public void execute() {
        int currentVolume = stereo.getLautstaerke();
        int newVolume = currentVolume - 10;
        log.info("Executing StereoVolumeDownCommand - decreasing volume from " + currentVolume + " to " + newVolume);
        stereo.setLautstaerke(newVolume);
    }
}
