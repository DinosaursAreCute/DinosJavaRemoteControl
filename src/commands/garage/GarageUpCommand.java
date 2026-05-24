package commands.garage;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.Command;
import receiver.Garage;

public class GarageUpCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger("GarageUpCommand");
    private final Garage garage;

    public GarageUpCommand(Garage garage) {
        log.debug("Creating GarageUpCommand with target: " + garage);
        this.garage = garage;
        log.success("Successfully created GarageUpCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing GarageUpCommand - raising garage door");
        garage.hoch();
    }
}
