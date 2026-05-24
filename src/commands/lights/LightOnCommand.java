package commands.lights;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import receiver.Licht;

public class LightOnCommand implements Command {
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
    }
}
