package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.ServoMotorComponent;

/**
 * Example for ServoMotorComponent usage. This example initializes a new ServoMotorComponent using the hardware pwm
 * pin 18.  The sample code sets the motor to its minimal position which corresponds to a value of 0°. After a pause
 * the servo rotates to its central position of 90° followed by another rotation to its maximal position of 180°.
 * In a for-Loop the motor now rotates degree for degree from 0° to 180°. In our example we use the Tower Pro Micro
 * Servo 9g SG90.
 */
public class ServoMotorDevice extends Example {
    public ServoMotorDevice(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        GpioProvider provider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(provider);
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_18);
        ServoMotorComponent servo = new ServoMotorComponent(console, pwm);

        while (console.isRunning()) {
            servo.setMin();
            Thread.sleep(1000);
            servo.setCenter();
            Thread.sleep(1000);
            servo.setMax();
            Thread.sleep(1000);

            for (int i = 0; i <= 180; i++) {
                servo.setPositionDegrees(i);
                Thread.sleep(100);
            }
        }
    }
}
