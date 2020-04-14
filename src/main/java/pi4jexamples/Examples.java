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
        default:
            System.out.println("Did not recognize input");
        }
        System.out.println("Press any Button to close the application");
        int read = System.in.read();

    }

    private static void showInfo() {
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
    }
}
