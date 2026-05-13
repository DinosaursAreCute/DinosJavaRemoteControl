package remote;

import receiver.Garage;
import receiver.Licht;
import receiver.Stereoanlage;

import java.util.Random;

public class RandomRemote {
    Licht light = new Licht();
    Garage garage = new Garage();
    Stereoanlage stereo = new Stereoanlage();
    Remote remote = new Remote();
    CommandLightOn on = new CommandLightOn(light);
    CommandLightOff off = new CommandLightOff(light);
    CommandGarageDown down = new CommandGarageDown(garage);
    CommandGarageUp up = new CommandGarageUp(garage);
    public RandomRemote(){
        remote.setCommand(0,"Lights on/off",on,off);
        remote.setCommand(1,"Garage Up/Down",up,down);
    }

    public void setLight(Licht light) {
        this.light = light;
    }

    public Remote getRemote() {
        return remote;
    }

    public void setRemote(Remote remote) {
        this.remote = remote;
    }

    public CommandLightOn getOn() {
        return on;
    }

    public void setOn(CommandLightOn on) {
        this.on = on;
    }

    public CommandLightOff getOff() {
        return off;
    }

    public void setOff(CommandLightOff off) {
        this.off = off;
    }

    public Licht getLight() {
        return light;
    }



}
