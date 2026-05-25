package commands.lights;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Licht;

@CommandInfo(
    name = "Light On",
    description = "Turn the light on",
    category = "Lighting",
    receiverType = Licht.class
)
public class LightOnCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("LightOnCommand");
    private final Licht light;

    public LightOnCommand(Licht light) {
        log.debug("Creating LightOnCommand with target: " + light);
        this.light = light;
        log.success("Successfully created LightOnCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing LightOnCommand - turning lights on");
        light.setLichtAn();
        applyDuration();
    }
}
