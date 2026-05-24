//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import Utils.LoggerFactory;
import Utils.Logger;
import remote.*;
import remote.RandomRemote;

public class Main {
    public static void main(String[] args) {
            Logger log = LoggerFactory.getLogger("Main");
            log.info("Starting up main ");
            Remote remote = new RandomRemote().getRemote();
        for (int i = 0; i < 7; i++) {
            remote.executeFunction(i,true);
            remote.executeFunction(i,false);
        }
        remote.executeFunction(1,false);
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();
        remote.undo();

    }
    }
