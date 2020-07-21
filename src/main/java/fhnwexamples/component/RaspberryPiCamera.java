package fhnwexamples.component;

import fhnwexamples.Example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import fhnwgpio.components.RaspberryPiCameraComponent;
import com.hopding.jrpicam.*;
import com.hopding.jrpicam.exceptions.*;
import com.pi4j.util.Console;
import uk.co.caprica.picam.*;
import uk.co.caprica.picam.enums.Encoding;

import static uk.co.caprica.picam.CameraConfiguration.cameraConfiguration;
import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

/**
 * This is an example of the raspberry pi camera using our own component.
 *
 * There are also examples that use that use the two libraries for raspistill directly
 * https://github.com/caprica/picam
 * https://github.com/Hopding/JRPiCam
 *
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
        long start = System.currentTimeMillis();

        //configuration for pictures using raspistill
        CameraConfiguration stillConfig = RaspberryPiCameraComponent.createCameraConfiguration()
                .width(1920)
                .height(1080)
                .encoding(Encoding.JPEG)
                .quality(85)
                .rotation(180);


        //configuration for videos using raspivid
        RaspiVidConfiguration vidConfig = new RaspiVidConfiguration()
                .verticalflip()
                .previewOff()
                .time(15000);

        //initialise the camera component using the two configurations
        RaspberryPiCameraComponent raspberryPiCamera = new RaspberryPiCameraComponent(console, stillConfig, vidConfig);

        console.println("Config done");

        raspberryPiCamera.takeStill("/home/pi/Pictures/picam1.jpg", 3000);
        raspberryPiCamera.takeStill("/home/pi/Pictures/picam2.jpg");

        //take video
        raspberryPiCamera.takeVid("/home/pi/Pictures/video.h264");

        long diff = System.currentTimeMillis() - start;
        System.out.println("Elapsed time" + diff);
    }
    // end::RaspberryPiCamera[]


    /**
     * Compares the two Raspberry Pi Camera Libraries in terms of speed
     * @param console
     * @throws FailedToRunRaspistillException
     * @throws IOException
     * @throws NativeLibraryException
     */
    private static void compareLibraries(Console console) throws FailedToRunRaspistillException, IOException, NativeLibraryException {
        long start = System.currentTimeMillis();

        takeRPiCameraPics(console);
        long end = System.currentTimeMillis();
        long diff = end - start;
        System.out.println("Elapsed Time: " + diff);

        takePiCamPics(console);
        end = System.currentTimeMillis();
        diff = end - start;

        System.out.println("Elapsed Time: " + diff);
    }

    //taking 10 pictures using RpiCamera Library
    private static void takeRPiCameraPics(Console console) throws FailedToRunRaspistillException {
        // Create a Camera that saves images to the Pi's Pictures directory.
        RPiCamera piCamera = new RPiCamera("/home/pi/Pictures");
        piCamera.setWidth(1920).setHeight(1080)
                .setVerticalFlipOn();
        // Sets all Camera options to their default settings, overriding any changes previously made.
        piCamera.setToDefaults();
        console.println("Settings done");
        console.println("Takes 10 pictures in home directory");
        try {

            for (int i = 0; i < 10; i++) {
                piCamera.takeStill("AnAwesomePic" + i + ".jpg");
                console.println("Pic taken");
            }
        } catch (Exception e) {
            console.println(e.getStackTrace());
        }
    }

    //taking 10 pictures using the PiCam library directly
    private static void takePiCamPics(Console console) throws NativeLibraryException, IOException {
        // Extract the bundled picam native library to a temporary file and load it
        installTempLibrary();

        //Create Configuration object with builder approach
        CameraConfiguration config = cameraConfiguration()
                .width(1920)
                .height(1080)
                .encoding(Encoding.JPEG)
                .quality(85)
                .rotation(180);

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
            for (int i = 2; i <= 10; i++) {
                camera.takePicture(new FilePictureCaptureHandler(new File(path + "/picam" + i + ".jpg")));
                console.println("picture " + i + " successfully taken");
            }
        } catch (CameraException | CaptureFailedException e) {
            console.println(e.getMessage());
        }
    }

    //takes video directly using a java process
    private static void takeVideo(Console console) {
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
