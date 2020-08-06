package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.ButtonComponent;

/**
 * Example for the ButtonComponent usage. In this example a button is connected to the GPIO pin BMC 17. The state
 * of the button is logged to the console every time it changes.
 */
public class ButtonDevice extends Example {
    public ButtonDevice(int key, String title) {
        super(key, title);
    }

    // tag::ButtonDevice[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();

        GpioPinDigitalInput pin = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_17);
        ButtonComponent button = new ButtonComponent(pin);

        boolean lastStatePressed = false;

        while (console.isRunning()) {
            if (button.isPressed() && !lastStatePressed) {
                console.println("button is pressed");
                lastStatePressed = true;
            } else if (button.isReleased() && lastStatePressed) {
                console.println("button is released");
                lastStatePressed = false;
            }

            // Avoid vibration error
            Thread.sleep(10);
        }

        gpio.shutdown();
    }
    // end::ButtonDevice[]
}
