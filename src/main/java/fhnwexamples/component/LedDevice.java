package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.LedComponent;

public class LedDevice extends Example {

    public LedDevice(int key, String title) {
        super(key, title);
    }

    // tag::LedDevice[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwmPin = gpio.provisionSoftPwmOutputPin(RaspiBcmPin.GPIO_02);
        LedComponent led = new LedComponent(pwmPin);

        while (console.isRunning()) {
            led.turnLedOff();
            Thread.sleep(250);
            led.turnLedOn();
            Thread.sleep(250);
            led.turnLedOff();
            Thread.sleep(250);
            led.turnLedOn();
            Thread.sleep(250);

            for (int i = 0; i <= 100; i++) {
                led.setBrightness(i);
                Thread.sleep(15);
            }

            for (int i = 100; i >= 0; i--) {
                led.setBrightness(i);
                Thread.sleep(15);
            }

        }

        gpio.shutdown();
    }
    // end::LedDevice[]
}
