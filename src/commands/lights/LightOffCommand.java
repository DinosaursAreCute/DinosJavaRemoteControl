package commands.lights;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import receiver.Licht;

public class LightOffCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger("LightOffCommand");
    private final Licht light;

    public LightOffCommand(Licht light) {
        log.debug("Creating LightOffCommand with target: " + light);
        this.light = light;
        log.success("Successfully created LightOffCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing LightOffCommand - turning lights off");
        light.setLichtAus();
    }
}
