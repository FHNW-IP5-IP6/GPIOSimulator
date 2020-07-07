package FHNWExamples.Component;

import FHNWExamples.Example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.pi4j.util.Console;
import uk.co.caprica.picam.*;
import uk.co.caprica.picam.enums.*;

import static uk.co.caprica.picam.CameraConfiguration.cameraConfiguration;
import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

/**
 * This is an example of the raspberry pi camera using a java library:
 * https://github.com/caprica/picam
 * <p>
 * The raspberry pi camera has to be enabled first in the raspberry pi configuration:
 * sudo raspi-config -> Interfacing Options -> Camera
 * <p>
 * For additional information on how to take pictures or videos from the console with the camera go to:
 * https://www.raspberrypi.org/documentation/configuration/camera.md
 */
public class RaspberryPiCamera extends Example {

    public RaspberryPiCamera(int key, String title) {
        super(key, title);
    }

    @Override
    // tag::RaspberryPiCamera[]
    public void execute() throws Exception {
        Console console = new Console();
        console.promptForExit();

        // Extract the bundled picam native library to a temporary file and load it
        installTempLibrary();

        //Create Configuration object with builder approach
        CameraConfiguration config = cameraConfiguration()
                .width(1920)
                .height(1080)
                .encoding(Encoding.JPEG)
                .quality(85);

        //select default home directory for pictures
        String path = "/home/pi/Pictures/PiCamPics";
        Files.createDirectories(Paths.get(path));

        console.println("tries to take 10 pictures to the directory" + path);

        //takes picture to the added jpg file
        try (Camera camera = new Camera(config)) {
            //For the first picture a longer delay is sometimes needed
            camera.takePicture(new FilePictureCaptureHandler(new File(path + "/picam1.jpg")), 3000);
            console.println("picture 1 successfully taken");

            //takes 9 pictures quicker now to get 10 in total
            for (int i = 2; i <= 3; i++) {
                camera.takePicture(new FilePictureCaptureHandler(new File(path + "/picam" + i + ".jpg")));
                console.println("picture " + i + " successfully taken");
            }
        } catch (CameraException e) {
            console.println(e.getMessage());
        }

        TakeVideo(console);
    }
    // end::RaspberryPiCamera[]

    public static void TakeVideo(Console console) {
        console.println("START VIDEO RECORDING FOR 15 SEC");
        long start = System.currentTimeMillis();
        try {
            // takes a 5 second full hd video and -hf or -vf flip the video
            String command = "raspivid -n -vf -t 15000 -o testvid.h264";

            //always writes it to a file, but with command "-o -" it writes to stdout
            //This will just run in the background and save the video in a file
            Process p = Runtime.getRuntime().exec(command);
            Thread.sleep(15000);

        } catch (IOException | InterruptedException ieo) {
            ieo.printStackTrace();
        }
        console.println("END OF VIDEO RECORDING");
    }
}
