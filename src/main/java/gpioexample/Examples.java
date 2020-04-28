package gpioexample;

import com.sun.tools.jdeprscan.scan.Scan;
import gpioexample.device.BlinkLedDevice;
import gpioexample.gpio.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Examples {
    private static List<Example> gpioExamples = new ArrayList<>();
    private static List<Example> deviceExamples = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        initializeGpioExamples();
        initializeDeviceExamples();

        showInitialInformation();

        System.out.println("Press any Button to close the application");
        int read = System.in.read();
    }

    /**
     * shows the initial types of examples that the user can choose from
     */
    private static void showInitialInformation() throws Exception{
        System.out.println("Press 0 to exit");
        System.out.println("Press 1 for the GPIO Examples");
        System.out.println("Press 2 for the Device Examples");

        int input = getNextScannerInt();

        switch (input) {
            case 0:
                System.exit(0);
                break;
            case 1:
                //GPIO Examples
                System.out.println("CHOOSE A GPIO EXAMPLE");
                System.out.println("--------------------");
                showCommandInformation(gpioExamples);
                chooseExample(gpioExamples);
                break;
            case 2:
                //GPIO Examples using pi4j-device
                System.out.println("CHOOSE A DEVICE EXAMPLE");
                System.out.println("--------------------");
                showCommandInformation(deviceExamples);
                chooseExample(deviceExamples);
                break;
            default:
                System.out.println("Did not recognize input");
                showInitialInformation();
        }
    }

    /**
     * Lets the user choose an example and executes it
     * @param examples all examples to choose from
     * @throws Exception can occur when the example is executed
     */
    private static void chooseExample( List<Example> examples) throws Exception {
        int input = getNextScannerInt();
        if (input == 0)  showInitialInformation();

        if (input>0 && input < examples.size()){
            Example example = examples.get(input);

            System.out.println("Run "+ example.getTitle());
            example.execute();
        }
        else{
            System.out.println("No example found for input: " + input+ " try again");
            chooseExample(examples);
        }
    }

    /**
     * shows all given examples with the title and the command to execute them
     * @param examples a list of the examples to show
     */
    private static void showCommandInformation(List<Example> examples) {
        System.out.println("Press 0 to go back");

        for (Example example: examples){
            System.out.println("Press "+ example.getKey() + " to start "+ example.getTitle());
        }
    }


    /**
     * initializes the GpioExamples
     */
    private static void initializeGpioExamples() {
        gpioExamples.add(new BlinkLed           (1, "Blink LED Test"));
        gpioExamples.add(new ButtonClick        (2, "Button Click Test"));
        gpioExamples.add(new LcdSystemTime      (3, "LCD System Time Test"));
        gpioExamples.add(new LcdSystemTimeI2C   (4, "I2C LCD System Time Test"));
        gpioExamples.add(new MotorDirection     (5, "Motor Direction Test"));
        gpioExamples.add(new MotorSpeedSoftPwm  (6, "Motor Speed Software PWM Test"));
        gpioExamples.add(new MotorSpeedHardPwm  (7, "Motor Speed Hardware PWM Test"));
        gpioExamples.add(new ServoMotor         (8, "Servo Motor Test"));
        gpioExamples.add(new StepperMotor       (9, "Stepper Motor Test"));
        gpioExamples.add(new KeyPad             (10, "Stepper Motor Test"));
        gpioExamples.add(new SerialCamera       (11, "Serial Camera Test"));
    }

    /**
     * initializes the device examples
     */
    private static void initializeDeviceExamples() {
        deviceExamples.add(new BlinkLedDevice   (1, "Blink LED Test"));
    }

    /**
     * asks for user input until it is an integer
     * @return a valid number entered by the user
     */
    private static int getNextScannerInt(){
        while (!scanner.hasNextInt()){
            System.out.println("Input was not a number, please try again");
            scanner.nextLine();
        }
        return scanner.nextInt();
    }
}
