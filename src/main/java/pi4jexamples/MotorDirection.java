package pi4jexamples;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class MotorDirection implements Example {
    private GpioPinDigitalOutput moveForward;
    private GpioPinDigitalOutput moveBackward;
    private Console console;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(                new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();
        moveForward = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17, "Motor Forward", PinState.LOW);
        moveBackward = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_27, "Motor Backward", PinState.LOW);

        console = new Console();
        console.promptForExit();

        while (console.isRunning()) {
            motorForward();
            Thread.sleep(1000);
            motorHalt();
            Thread.sleep(1000);
            motorBackward();
            Thread.sleep(1000);
            motorHalt();
            Thread.sleep(1000);
        }

        gpio.shutdown();
    }

    private void motorForward() {
        moveForward.high();
        moveBackward.low();
        console.println("Motor is spinning forward.");
    }

    private void motorBackward() {
        moveForward.low();
        moveBackward.high();
        console.println("Motor is spinning backward.");
    }

    private void motorHalt() {
        moveForward.low();
        moveBackward.low();
        console.println("Motor is stopped.");
    }
}
