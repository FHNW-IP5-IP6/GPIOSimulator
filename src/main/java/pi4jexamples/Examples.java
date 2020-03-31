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
            var led = new BlinkLed();
            led.execute();
            break;
        case 2:
            System.out.println("Run Button Click Example");
            var button = new ButtonClick();
            button.execute();
            break;
        case 3:
            System.out.println("Run Hello World LCD Example");
            var lcd = new LcdSystemTime();
            lcd.execute();
            break;
        case 4:
            System.out.println("Run Motor Direction Example");
            var motorDirection = new MotorDirection();
            motorDirection.execute();
        case 5:
            System.out.println("Run Motor Speed Example");
            var motorSpeed = new MotorSpeed();
            motorSpeed.execute();
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
        System.out.println("Press 5 to start Motor Speed Test");
    }
}
