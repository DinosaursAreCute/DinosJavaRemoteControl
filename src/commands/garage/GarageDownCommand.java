package commands.garage;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;
import commands.CommandInfo;
import receiver.Garage;

@CommandInfo(
    name = "Garage Down",
    description = "Lower the garage door",
    category = "Garage",
    receiverType = Garage.class
)
public class GarageDownCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("GarageDownCommand");
    private final Garage garage;

    public GarageDownCommand(Garage garage) {
        log.debug("Creating GarageDownCommand with target: " + garage);
        this.garage = garage;
        log.success("Successfully created GarageDownCommand: " + this);
    }

    @Override
    public void execute() {
        log.info("Executing GarageDownCommand - lowering garage door");
        garage.runter();
    }
}
