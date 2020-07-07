package FHNWGPIO.Components;

import FHNWGPIO.Components.helper.RaspiVidConfiguration;
import com.pi4j.util.Console;
import uk.co.caprica.picam.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.co.caprica.picam.PicamNativeLibrary.installTempLibrary;

public class RaspberryPiCameraComponent {
    private Console console;
    private CameraConfiguration raspiStillConfiguration;
    private RaspiVidConfiguration raspiVidConfiguration;
    private Process process;
    private ProcessBuilder processBuilder;
    private Camera camera;

    public RaspberryPiCameraComponent(Console console, CameraConfiguration raspiStillConfiguration) {
        this.console = console;
        this.raspiStillConfiguration = raspiStillConfiguration;

        init();
    }

    private void init() {
        try {
            // Extract the bundled picam native library to a temporary file and load it
            installTempLibrary();
            camera = new Camera(raspiStillConfiguration);

        } catch (NativeLibraryException | CameraException e) {
            e.printStackTrace();
        }
    }

    public void takeStill(CameraConfiguration config) {
        takeStill(config, "/home/pi/Pictures/picam.jpg", 0);
    }

    public void takeStill(CameraConfiguration config, String path) {
        takeStill(config, path, 0);
    }

    public void takeStill(CameraConfiguration config, String path, int delay) {
        try {
            //For the first picture a longer delay is sometimes needed
            camera.takePicture(new FilePictureCaptureHandler(new File(path)), delay);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CameraConfiguration createCameraConfiguration() {
        return CameraConfiguration.cameraConfiguration();
    }

    public File takeVid(String saveDir) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("raspivid");
        command.add("-o");
        command.add(saveDir);
        //command.addAll(raspiVidConfiguration.getValues());

        processBuilder = new ProcessBuilder(command);
        process = processBuilder.start();
        process.waitFor();
        return new File(saveDir);
    }
}
