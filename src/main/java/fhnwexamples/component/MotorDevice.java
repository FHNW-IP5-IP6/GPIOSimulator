package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.MotorComponent;

/**
 * Example for MotorComponent usage. This example initializes a new MotorComponent object using the Raspberry Pi's
 * pulse width modulation pins. The motor runs forward increasing its speed until the maximum power is reached. Then
 * the motor stops for 5 seconds and starts to run backwards. For controlling the motor, the dc motor driver L293D
 * is used.
 */
public class MotorDevice extends Example {

    public MotorDevice(int key, String title) {
        super(key, title);
    }

    // tag::MotorDevice[]
    @Override public void execute() throws Exception {
        GpioProvider provider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(provider);
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwmForwards = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_12);
        GpioPinPwmOutput pwmBackwards = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_13);
        MotorComponent pwmMotor = new MotorComponent(pwmForwards, pwmBackwards);

        while (console.isRunning()) {
            for (int i = 0; i <= 100; i = i + 10) {
                pwmMotor.moveForwards(i);
                Thread.sleep(1000);
            }

            pwmMotor.stop();
            Thread.sleep(5000);

            for (int i = 0; i <= 100; i = i + 10) {
                pwmMotor.moveBackwards(i);
                Thread.sleep(1000);
            }

            pwmMotor.stop();
            Thread.sleep(5000);
        }

        gpio.shutdown();
        // end::MotorDevice[]
    }
}
