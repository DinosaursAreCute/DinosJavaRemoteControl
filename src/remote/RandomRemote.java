package remote;

import receiver.Garage;
import receiver.Licht;
import receiver.Stereoanlage;

public class RandomRemote {
    Licht light = new Licht();
    Garage garage = new Garage();

    Stereoanlage stereo = new Stereoanlage();
    Remote remote = new Remote();
    CommandLightOn on = new CommandLightOn(light);
    CommandLightOff off = new CommandLightOff(light);
    CommandGarageDown down = new CommandGarageDown(garage);
    CommandGarageUp up = new CommandGarageUp(garage);
    CommandStereoVolumeDown volDown = new CommandStereoVolumeDown(stereo);
    CommandStereoVolumeUp volUp = new CommandStereoVolumeUp(stereo);
    CommandStereoPlayCD playCD = new CommandStereoPlayCD(stereo);
    CommandStereoOn stereoOn = new CommandStereoOn(stereo);
    CommandStereoOff stereoOff = new CommandStereoOff(stereo);
    Command[] makro1On = {on,up,stereoOn,playCD};
    Command[] makro1Off = {off,down,stereoOff};
    Command[] makroStereoOnWithStuff = {stereoOn,playCD};
    CommandMakroCommand getMakroCommand1dOn = new CommandMakroCommand(makro1On);
    CommandMakroCommand makroCommand1Off = new CommandMakroCommand(makro1Off);
    CommandMakroCommand stereoOnWithStuff = new CommandMakroCommand(makroStereoOnWithStuff);
    public RandomRemote(){
        this.stereo.legeCDEin("Drugs and Guns for Everyone - The handsome devil");
        remote.setCommand(0,"Lights on/off",on,off);
        remote.setCommand(1,"Garage Up/Down",up,down);
        remote.setCommand(2,"Stereo On/off",stereoOnWithStuff,stereoOff);
        remote.setCommand(3,"Stereo Vol Up/Down",volUp,volDown);
        remote.setCommand(4,"Makro Garage On/off)",getMakroCommand1dOn,makroCommand1Off);
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
