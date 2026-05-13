package remote;

import Utils.Logger;
import Utils.LoggerFactory;

public class CommandFallback implements Command{
    private static Logger log = LoggerFactory.getLogger("EMPTY_COMMAND");
    @Override
    public void execute() {
        log.warning("NO COMMAND ASSIGNED");
    }
}
