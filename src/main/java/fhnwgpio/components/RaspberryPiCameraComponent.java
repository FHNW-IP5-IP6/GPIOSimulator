package fhnwgpio.components;

import fhnwgpio.components.helper.RaspiVidConfiguration;
import com.pi4j.util.Console;
import uk.co.caprica.picam.*;

import java.io.File;
import java.io.IOException;

import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

/**
 * FHNW implementation for the Raspberry Pi Camera.
 */
public class RaspberryPiCameraComponent {
    private Console console;
    private CameraConfiguration raspiStillConfiguration;
    private RaspiVidConfiguration raspiVidConfiguration;
    private Camera camera;
    private boolean isRaspiStillAvailable, isRaspiVidAvailable;

    /**
     * Constructor for using the picture and video functionality
     * @param console pi4J console object for logging
     * @param raspiStillConfiguration configuration object that represents the raspistill command
     * @param raspiVidConfiguration configuration object that represents the raspivid command
     */
    public RaspberryPiCameraComponent(Console console, CameraConfiguration raspiStillConfiguration, RaspiVidConfiguration raspiVidConfiguration) {
        this.console = console;
        this.raspiStillConfiguration = raspiStillConfiguration;
        this.raspiVidConfiguration = raspiVidConfiguration;
        isRaspiVidAvailable = true;
        isRaspiStillAvailable = true;

        init();
    }

    /**
     * Constructor for only using the picture functionality
     * @param console pi4J console object for logging
     * @param raspiStillConfiguration configuration object that represents the raspistill command
     */
    public RaspberryPiCameraComponent(Console console, CameraConfiguration raspiStillConfiguration){
        this.console = console;
        this.raspiStillConfiguration = raspiStillConfiguration;
        isRaspiStillAvailable = true;

        init();
    }

    /**
     * Constructor for only using the video functionality
     * @param console pi4J console object for logging
     * @param raspiVidConfiguration configuration object that represents the raspivid command
     */
    public RaspberryPiCameraComponent(Console console, RaspiVidConfiguration raspiVidConfiguration) {
        this.console = console;
        this.raspiVidConfiguration = raspiVidConfiguration;
        isRaspiVidAvailable = true;
    }

    /**
     * initialisation of the picam native library.
     */
    private void init() {
        try {
            // Extract the bundled picam native library to a temporary file and load it
            installTempLibrary();
            camera = new Camera(raspiStillConfiguration);

        } catch (NativeLibraryException | CameraException e) {
            console.println("Error while initialising: " + e.getMessage());
        }
    }

    /**
     * Takes picture and saves it to the default Pictures folder
     */
    public void takeStill() {
        takeStill("/home/pi/Pictures/picam.jpg", 0);
    }

    /**
     * takes picture and saves it to the outputPath
     * @param outputPath path to save picture to
     */
    public void takeStill(String outputPath) {
        takeStill(outputPath, 0);
    }

    /**
     * Takes a picture using the picam library and saves it to the specified output after a delay
     * @param outputPath path to the output file that saves the picture
     * @param delay before taking the picture
     */
    public void takeStill(String outputPath, int delay) {
        if (!isRaspiStillAvailable) {
            throw new ExceptionInInitializerError("RaspiStill has to be initialised with the Configuration for taking pictures");
        }
        try {
            //For the first picture a longer delay is sometimes needed
            camera.takePicture(new FilePictureCaptureHandler(new File(outputPath)), delay);

        } catch (Exception e) {
            console.println("Error while taking picture: " + e.getMessage());
        }
    }

    /**
     * The picam library has a private constructor and therefore needs this method
     * @return new CameraConfiguration object of the picam library
     */
    public static CameraConfiguration createCameraConfiguration() {
        return CameraConfiguration.cameraConfiguration();
    }

    /**
     * Takes a video with the configuration and saves it to the output path
     * @param outputPath path to the .h264 file
     */
    public void takeVid(String outputPath) {
        // tag::RasPiCamTakeVid[]
        if (!isRaspiVidAvailable) {
            throw new ExceptionInInitializerError("RaspiVid has to be initialised with the Configuration for taking videos");
        }
        try {
            raspiVidConfiguration.output(outputPath);
            String command = raspiVidConfiguration.toString();

            //This will just run in the background and save the video in a file
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            Thread.sleep(raspiVidConfiguration.getTime());

            p.destroy();
            if (p.isAlive())
                p.destroyForcibly();

        } catch (IOException | InterruptedException ieo) {
            console.println("Error while taking video: " + ieo.getMessage());
        }
        // end::RasPiCamTakeVid[]
    }
}
