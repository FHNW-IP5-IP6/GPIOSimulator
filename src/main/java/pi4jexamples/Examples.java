package pi4jexamples;

import java.util.Scanner;

public class Examples {

    public static void main(String[] args) throws Exception {
        showInfo();
        Scanner scanner = new Scanner(System.in);
        int input;
        input = scanner.nextInt();
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
            System.out.println("Run Motor Direction Example");
            MotorDirection motorDirection = new MotorDirection();
            motorDirection.execute();
        case 5:
            System.out.println("Run Motor Speed Software PWM Example");
            MotorSpeedSoftPwm motorSpeedSoftPwm = new MotorSpeedSoftPwm();
            motorSpeedSoftPwm.execute();
        case 6:
            System.out.println("Run Motor Speed Hardware PWM Example");
            MotorSpeedHardPwm motorSpeedHardPwm = new MotorSpeedHardPwm();
            motorSpeedHardPwm.execute();
        default:
            System.out.println("Did not recognize input");
        }
        System.out.println("Press any Button to close the application");
        System.in.read();
    }

    private static void showInfo() {
        System.out.println("Press 0 to exit");
        System.out.println("Press 1 to start Blink LED Test");
        System.out.println("Press 2 to start Button Test");
        System.out.println("Press 3 to start LCD Display Test");
        System.out.println("Press 4 to start Motor Direction Test");
        System.out.println("Press 5 to start Motor Speed Software PWM Test");
        System.out.println("Press 6 to start Motor Speed Hardware PWM Test");
    }
}
