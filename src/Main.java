//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import Utils.LoggerFactory;
import Utils.Logger;
import receiver.Licht;
import remote.*;
import remote.RandomRemote;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
            Logger log = LoggerFactory.getLogger("Main");
            log.info("Starting up main ");
            Remote remote = new RandomRemote().getRemote();

            remote.executeFunction(0,true);
            remote.executeFunction(0,false);
            remote.executeFunction(1,true);
            remote.executeFunction(1,false);
        }
    }
