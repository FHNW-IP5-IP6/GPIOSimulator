package gpioexample;

import gpioexample.device.BlinkLedDevice;
import gpioexample.gpio.*;

import java.util.Scanner;

public class Examples {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int input;
        showInitialInformation();
        input = scanner.nextInt();
        switch (input){
            case 0:
                System.exit(0);
                break;
            case 1:
                //GPIO Examples
                System.out.println("CHOOSE A GPIO EXAMPLE");
                System.out.println("--------------------");
                showGpioExamplesInformation();
                chooseGpioExample(scanner.nextInt());
                break;
            case 2:
                //GPIO Examples using pi4j-device
                System.out.println("CHOOSE A DEVICE EXAMPLE");
                System.out.println("--------------------");
                showDeviceExamplesInformation();
                chooseDeviceExample(scanner.nextInt());
                break;
            default:
                System.out.println("Did not recognize input");
        }

        showGpioExamplesInformation();
        System.out.println("Press any Button to close the application");
        int read = System.in.read();

    }

    private static void chooseGpioExample(int input) throws Exception {
        switch (input) {
            case 0:
                System.exit(0);
                break;
            case 1:
                System.out.println("Run Blink LED Example");
                BlinkLedDevice ledDevice = new BlinkLedDevice();
                ledDevice.execute();
                break;
            default:
                System.out.println("Did not recognize input");
        }
    }

    private static void chooseDeviceExample(int input) throws Exception {
        switch (input) {
            case 0:
                System.exit(0);
                break;
            case 1:
                System.out.println("Run Blink LED Example");
                BlinkLed led = new BlinkLed();
                led.execute();
                break;
            case 2:
                System.out.println("Run Button Click Example");
                ButtonClick button = new ButtonClick();
                button.execute();
                break;
            case 3:
                System.out.println("Run Hello World LCD Example");
                LcdSystemTime lcd = new LcdSystemTime();
                lcd.execute();
                break;
            case 4:
                System.out.println("Run Hello World I2C LCD Example");
                LcdSystemTimeI2C lcdI2C = new LcdSystemTimeI2C();
                lcdI2C.execute();
                break;
            case 5:
                System.out.println("Run Motor Direction Example");
                MotorDirection motorDirection = new MotorDirection();
                motorDirection.execute();
                break;
            case 6:
                System.out.println("Run Motor Speed Software PWM Example");
                MotorSpeedSoftPwm motorSpeedSoftPwm = new MotorSpeedSoftPwm();
                motorSpeedSoftPwm.execute();
                break;
            case 7:
                System.out.println("Run Motor Speed Hardware PWM Example");
                MotorSpeedHardPwm motorSpeedHardPwm = new MotorSpeedHardPwm();
                motorSpeedHardPwm.execute();
                break;
            case 8:
                System.out.println("Run Servo Motor Example");
                ServoMotor servoMotor = new ServoMotor();
                servoMotor.execute();
                break;
            case 9:
                System.out.println("Run Stepper Motor Example");
                StepperMotor stepperMotor = new StepperMotor();
                stepperMotor.execute();
                break;
            case 10:
                System.out.println("Run Key Pad Example");
                KeyPad keyPad = new KeyPad();
                keyPad.execute();
                break;
            case 11:
                System.out.println("Run Serial Camera Example");
                SerialCamera serialCam = new SerialCamera();
                serialCam.execute();
                break;
            default:
                System.out.println("Did not recognize input");
        }
    }

    private static void showInitialInformation(){
        System.out.println("Press 0 to exit");
        System.out.println("Press 1 for the GPIO Examples");
        System.out.println("Press 2 for the Device Examples");
    }

    private static void showGpioExamplesInformation() {
        System.out.println("Press 0 to exit");
        System.out.println("Press 1 to start Blink LED Test");
        System.out.println("Press 2 to start Button Test");
        System.out.println("Press 3 to start LCD Display Test");
        System.out.println("Press 4 to start I2C LCD Display Test");
        System.out.println("Press 5 to start Motor Direction Test");
        System.out.println("Press 6 to start Motor Speed Software PWM Test");
        System.out.println("Press 7 to start Motor Speed Hardware PWM Test");
        System.out.println("Press 8 to start Servo Motor Test");
        System.out.println("Press 9 to start Stepper Motor Test");
        System.out.println("Press 10 to start Key Pad Test");
        System.out.println("Press 11 to start Serial Camera Test");
    }

    private static void showDeviceExamplesInformation() {
        System.out.println("Press 0 to exit");
        System.out.println("Press 1 to start Blink LED Test");
    }
}
