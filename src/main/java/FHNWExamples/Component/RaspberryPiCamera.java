package FHNWExamples.Component;

import FHNWExamples.Example;
import java.io.File;
import uk.co.caprica.picam.*;
import uk.co.caprica.picam.enums.*;
import static uk.co.caprica.picam.CameraConfiguration.cameraConfiguration;
import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

/**
 * This is an example of the raspberry pi camera using a java library.
 *
 * The raspberry pi camera has to be enabled first in the raspberry pi configuration:
 * sudo raspi-config -> Interfacing Options -> Camera
 *
 * For additional information on how to take pictures or videos from the console with the Camera go to:
 * https://www.raspberrypi.org/documentation/configuration/camera.md
 *
 */
public class RaspberryPiCamera extends Example {

    public RaspberryPiCamera(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute() throws Exception {
        // Extract the bundled picam native library to a temporary file and load it
        installTempLibrary();

        CameraConfiguration config = cameraConfiguration()
                .width(1920)
                .height(1080)
                .encoding(Encoding.JPEG)
                .quality(85);

        try (Camera camera = new Camera(config)) {
            camera.takePicture(new FilePictureCaptureHandler(new File("picam1.jpg")));
            camera.takePicture(new FilePictureCaptureHandler(new File("picam2.jpg")));
        }
        catch (CameraException e) {
            e.printStackTrace();
        }
    }
}
