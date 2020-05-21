package FHNWExamples.Component;

import FHNWExamples.Example;
import FHNWGPIO.Components.SerialCameraComponent;
import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class SerialCamera extends Example {
    public SerialCamera(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        Console console = new Console();
        console.promptForExit();

        console.println("taking a picture. please smile :)");
        SerialCameraComponent cam = new SerialCameraComponent(console, 1024, true);
        String fileName = cam.saveImageAsJpg("GroveCamPic_");
        console.println("picture was taken and saved in file " + fileName);
    }
}
