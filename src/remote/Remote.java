package remote;
import Utils.*;
public class Remote {
    private static Logger log = LoggerFactory.getLogger("Remote");
    private Command[] onButtons = new Command[7];
    private Command[] offButtons = new Command[7];


    public Remote() {
        log.debug("Creating new remote object with args: ");
        log.debug("Assigning Buttons with fallback commands");
        for (int i = 0; i < onButtons.length; i++) {
            onButtons[i] = new CommandFallback();
            log.debug("added fallback to on-button["+i+"]");
        }
        for (int i = 0; i < offButtons.length; i++) {
            offButtons[i] = new CommandFallback();
            log.debug("added fallback to off-button["+i+"]");
        }
    }

    public void setCommand(int index, String descript, Command on, Command off){
        if(index > onButtons.length || index < 0){
            log.error("INVALID index:'"+index+"' for button: '"+descript);
            throw new IllegalArgumentException("Invalid index position for button; "+descript);
        }
        log.debug("Setting button at index: "+index+", with args["+descript+", "+on.toString()+off.toString());
        onButtons[index] = on;
        offButtons[index] = off;
        log.success("Successfully set commands for buttons at index: "+index);
    }

    public void executeFunction(int index, boolean isOnButton){
        log.debug("Executing button: "+index);
        if(index < 0 || index > onButtons.length){
            log.error("INVALID index:'"+index);
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if(isOnButton) onButtons[index].execute();
        else offButtons[index].execute();
    }

}
