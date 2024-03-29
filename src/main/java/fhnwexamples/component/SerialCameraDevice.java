package fhnwexamples.component;

import fhnwexamples.Example;
import fhnwgpio.components.SerialCameraComponent;
import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

/**
 * Example for SerialCameraComponent usage. This example initializes the grove serial camera and request a JPG
 * picture form the camera. The picture is saved in a file with a given file name under the specified path.
 */
public class SerialCameraDevice extends Example {
    public SerialCameraDevice(int key, String title) {
        super(key, title);
    }

    // tag::SerialCameraDevice[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        Console console = new Console();
        console.promptForExit();

        console.println("taking a picture. please smile :)");
        SerialCameraComponent cam = new SerialCameraComponent(1024);
        String fileName = cam.saveImageAsJpg("pictures", "GroveCamPic_");
        console.println("picture was taken and saved in file " + fileName);
    }
    // end::SerialCameraDevice[]
}
