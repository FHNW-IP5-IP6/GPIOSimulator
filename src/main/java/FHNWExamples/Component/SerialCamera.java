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
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();

        SerialCameraComponent cam = new SerialCameraComponent(console);
        cam.initialize();
        cam.preCapture();
        long pictureLength = cam.getPictureLength();
        cam.getPicture(pictureLength, "GroveCamPic_", ".jpg");

        console.promptForExit();
    }
}
