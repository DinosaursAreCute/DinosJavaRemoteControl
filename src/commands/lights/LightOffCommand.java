package commands.lights;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Licht;

@CommandInfo(
    name = "Light Off",
    description = "Turn the light off",
    category = "Lighting",
    receiverType = Licht.class
)
public class LightOffCommand extends BaseCommand {
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
