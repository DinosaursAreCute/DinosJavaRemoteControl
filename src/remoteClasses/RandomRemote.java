package remoteClasses;

import commands.Command;
import commands.lights.*;
import commands.garage.*;
import commands.stereo.*;
import commands.macro.MacroCommand;
import receiver.Garage;
import receiver.Licht;
import receiver.Stereoanlage;

public class RandomRemote {
    Licht light = new Licht();
    Garage garage = new Garage();

    Stereoanlage stereo = new Stereoanlage();
    Remote remote = new Remote();
    LightOnCommand on = new LightOnCommand(light);
    LightOffCommand off = new LightOffCommand(light);
    GarageDownCommand down = new GarageDownCommand(garage);
    GarageUpCommand up = new GarageUpCommand(garage);
    StereoVolumeDownCommand volDown = new StereoVolumeDownCommand(stereo);
    StereoVolumeUpCommand volUp = new StereoVolumeUpCommand(stereo);
    StereoPlayCDCommand playCD = new StereoPlayCDCommand(stereo);
    StereoOnCommand stereoOn = new StereoOnCommand(stereo);
    StereoOffCommand stereoOff = new StereoOffCommand(stereo);
    Command[] makro1On = {on,up,stereoOn,playCD};
    Command[] makro1Off = {off,down,stereoOff};
    Command[] makroStereoOnWithStuff = {stereoOn,playCD};
    MacroCommand getMakroCommand1dOn = new MacroCommand(makro1On);
    MacroCommand makroCommand1Off = new MacroCommand(makro1Off);
    MacroCommand stereoOnWithStuff = new MacroCommand(makroStereoOnWithStuff);
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

    public LightOnCommand getOn() {
        return on;
    }

    public void setOn(LightOnCommand on) {
        this.on = on;
    }

    public LightOffCommand getOff() {
        return off;
    }

    public void setOff(LightOffCommand off) {
        this.off = off;
    }

    public Licht getLight() {
        return light;
    }

}
