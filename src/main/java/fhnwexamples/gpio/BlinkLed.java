package fhnwexamples.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;

public class BlinkLed extends Example {

    public BlinkLed(int key, String title) {
        super(key, title);
    }

    // tag::BlinkLed[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_02, "Blinking LED", PinState.LOW);
        led.setShutdownOptions(true, PinState.LOW);

        Console console = new Console();
        console.promptForExit();

        long delay = 1000;
        console.println("start blinking with " + delay + " delay");

        while (console.isRunning()) {
            if (led.isLow()) {
                led.high();
                console.println("Led is high.");
            } else {
                led.low();
                console.println("Led is low.");
            }
            Thread.sleep(delay);
        }

        gpio.shutdown();
    }
    // end::BlinkLed[]
}
