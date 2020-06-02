package FHNWExamples.Component;

import com.pi4j.component.light.impl.GpioLEDComponent;
import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import FHNWExamples.Example;

import java.util.concurrent.Future;

public class BlinkLedDevice extends Example {

    public BlinkLedDevice(int key, String title) {
        super(key, title);
    }

    // tag::BlinkLedDevice[]
    // end::BlinkLedDevice[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_02, "Blinking LED", PinState.LOW);
        led.setShutdownOptions(true, PinState.LOW);

        GpioLEDComponent ledComponent = new GpioLEDComponent(led);

        Console console = new Console();
        console.promptForExit();

        long delay = 1000;
        console.println("start blinking with " + delay + " delay");

        Future<?> blinkTask = ledComponent.blink(delay);
        while (!blinkTask.isDone() && console.isRunning());

        gpio.shutdown();
    }
    // end::BlinkLedDevice[]
}
