package fhnwexamples;

import fhnwexamples.component.*;
import fhnwexamples.gpio.*;
import fhnwexamples.gpio.LcdSystemTimeI2C;
import fhnwexamples.gpio.SerialCamera;

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
     * shows the initial types of fhnwexamples that the user can choose from
     */
    private static void showInitialInformation() throws Exception {
        System.out.println("Press 0 to exit");
        System.out.println("Press 1 for the gpio Examples");
        System.out.println("Press 2 for the Device Examples");

        int input = getNextScannerInt();

        switch (input) {
        case 0:
            System.exit(0);
            break;
        case 1:
            //gpio Examples
            System.out.println("CHOOSE A gpio EXAMPLE");
            System.out.println("--------------------");
            showCommandInformation(gpioExamples);
            chooseExample(gpioExamples);
            break;
        case 2:
            //gpio Examples using pi4j-component
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
     * @param examples all fhnwexamples to choose from
     * @throws Exception can occur when the example is executed
     */
    private static void chooseExample(List<Example> examples) throws Exception {
        int input = getNextScannerInt();
        if (input == 0)
            showInitialInformation();

        if (examples.stream().anyMatch(e -> e.getKey() == input)) {
            Example example = examples.stream().filter(e -> e.getKey() == input).findFirst().orElse(null);

            System.out.println("Run " + example.getTitle());
            example.execute();
        } else {
            System.out.println("No example found for input: " + input + " try again");
            chooseExample(examples);
        }
    }

    /**
     * shows all given fhnwexamples with the title and the command to execute them
     *
     * @param examples a list of the fhnwexamples to show
     */
    private static void showCommandInformation(List<Example> examples) {
        System.out.println("Press 0 to go back");

        for (Example example : examples) {
            System.out.println("Press " + example.getKey() + " to start " + example.getTitle());
        }
    }

    /**
     * initializes the GpioExamples
     */
    private static void initializeGpioExamples() {
        gpioExamples.add(new BlinkLed(1, "Blink LED Test"));
        gpioExamples.add(new ButtonClick(2, "Button Click Test"));
        gpioExamples.add(new LcdSystemTime(3, "LCD System Time Test"));
        gpioExamples.add(new LcdSystemTimeI2C(4, "I2C LCD System Time Test"));
        gpioExamples.add(new MotorDirection(5, "Motor Direction Test"));
        gpioExamples.add(new MotorSpeedSoftPwm(6, "Motor Speed Software PWM Test"));
        gpioExamples.add(new MotorSpeedHardPwm(7, "Motor Speed Hardware PWM Test"));
        gpioExamples.add(new ServoMotor(8, "Servo Motor Test"));
        gpioExamples.add(new StepperMotor(9, "Stepper Motor Test"));
        gpioExamples.add(new KeyPad(10, "Key Pad Test"));
        gpioExamples.add(new SerialCamera(11, "Serial Camera Test"));
    }

    /**
     * initializes the component fhnwexamples
     */
    private static void initializeDeviceExamples() {
        deviceExamples.add(new BlinkLedDevice(1, "Blink LED Test"));
        deviceExamples.add(new ButtonClickDevice(2, "Button Click Test using pi4j-device"));
        deviceExamples.add(new LcdSystemTimeI2C(3, "LCD System Time I2C Test"));
        deviceExamples.add(new SerialCamera(4, "grove Serial Camera example"));
        deviceExamples.add(new PotentiometerDevice(5, "Potentiometer example"));
        deviceExamples.add(new RaspberryPiCamera(6, "Raspberry Pi Camera example"));
        deviceExamples.add(new BuzzerDevice(7, "Buzzer example"));
        deviceExamples.add(new MotorDevice(8, "Motor example"));
        deviceExamples.add(new StepperMotorDevice(9, "Stepper Motor example"));
        deviceExamples.add(new RFIDReader(10, "RFID Reader example"));
        deviceExamples.add(new LEDStripDriver(11, "LED Strip Driver example"));
        deviceExamples.add(new UltraSonicRanger(12, "Ultra Sonic Ranger example"));
    }

    /**
     * asks for user input until it is an integer
     *
     * @return a valid number entered by the user
     */
    private static int getNextScannerInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Input was not a number, please try again");
            scanner.nextLine();
        }
        return scanner.nextInt();
    }
}
