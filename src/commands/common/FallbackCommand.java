package commands.common;

import Utils.Logger;
import Utils.LoggerFactory;
import commands.BaseCommand;

/**
 * Fallback command used when no command is assigned to a button.
 */
public class FallbackCommand extends BaseCommand {
    private static final Logger log = LoggerFactory.getLogger("FallbackCommand");

    @Override
    public void execute() {
        log.warning("No command assigned - executing fallback (no-op)");
    }
}
