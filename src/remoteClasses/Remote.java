package remoteClasses;

import Utils.*;
import commands.Command;
import commands.common.FallbackCommand;

import java.util.*;

public class Remote {
    private final static Logger log = LoggerFactory.getLogger("Remote");
    private Command[] onButtons = new Command[7];
    private Command[] offButtons = new Command[7];
    private ArrayList<Integer> history = new ArrayList<>();
    private ArrayList<Integer> historyOnOff = new ArrayList<>();

    public Remote() {
        log.debug("Creating new commands object with args: ");
        log.debug("Assigning Buttons with fallback commands");
        for (int i = 0; i < onButtons.length; i++) {
            onButtons[i] = new FallbackCommand();
            log.debug("added fallback to on-button["+i+"]");
        }
        for (int i = 0; i < offButtons.length; i++) {
            offButtons[i] = new FallbackCommand();
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

    public void undo(){
        log.info("Undo last Action");

        // Check if history is empty
        if(history.isEmpty() || historyOnOff.isEmpty()){
            log.warning("Cannot undo: History is empty");
            return;
        }

        log.debug("Command history:["+ history.toString()+"]["+historyOnOff+"]");

        // Remove the last command from history FIRST
        int command = history.removeLast();
        int onOffValue = historyOnOff.removeLast();
        boolean wasOn = (onOffValue == 1);

        // Execute the opposite command without adding to history
        boolean executeOpposite = !wasOn;
        log.debug("Undo action: Command:["+command+"], was:["+wasOn+"], executing:["+executeOpposite+"]");

        executeCommandDirect(command, executeOpposite);
    }

    public void executeFunction(int index, boolean isOnButton){
        log.debug("Executing button: "+index);

        // Execute the command
        executeCommandDirect(index, isOnButton);

        // Add to history
        int t = isOnButton ? 1 : 0;
        history.add(index);
        historyOnOff.add(t);
        log.success("Function ["+index+"]["+isOnButton+"]");
    }

    private void executeCommandDirect(int index, boolean isOnButton){
        if(index < 0 || index >= onButtons.length){
            log.error("INVALID index:'"+index+"'");
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        if(isOnButton) {
            onButtons[index].execute();
        } else {
            offButtons[index].execute();
        }
    }

}
