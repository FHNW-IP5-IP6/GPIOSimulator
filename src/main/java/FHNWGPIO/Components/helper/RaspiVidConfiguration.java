package FHNWGPIO.Components.helper;

import java.util.*;

public class RaspiVidConfiguration {
    private HashMap<String, String> commands;

    public RaspiVidConfiguration(HashMap<String, String> commands) {
        this.commands = commands;
    }

    public RaspiVidConfiguration setTime(long time){
        commands.put("time","-t " + time);
        return this;
    }

    public RaspiVidConfiguration setHorizontalFlip(){
        commands.put("flip","-hf");
        return this;
    }

    public RaspiVidConfiguration setVerticalFlip(){
        commands.put("flip","-vf");
        return this;
    }

    public RaspiVidConfiguration setPreviewOff(){
        commands.put("preview","-n");
        return this;
    }

    public List<String> getValues() {
        for (Map.Entry<String, String> entry : commands.entrySet()) {
            if (entry.getValue() != null) {
                return Arrays.asList(entry.getValue());
            }
        }
        return null;
    }
}
