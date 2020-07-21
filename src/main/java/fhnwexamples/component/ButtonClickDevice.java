package fhnwexamples.component;

import com.pi4j.component.button.impl.GpioButtonComponent;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;
import fhnwexamples.Example;

public class ButtonClickDevice extends Example {

    public ButtonClickDevice(int key, String title) {
        super(key, title);
    }

    // tag::ButtonClickDevice[]
    @Override public void execute() throws Exception {

        GpioProvider provider = new RaspiGpioProvider(
                RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING); //like on gpio Extension Board
        GpioFactory.setDefaultProvider(provider);

        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        final GpioPinDigitalInput pin = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_03, PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        pin.setShutdownOptions(true);

        GpioButtonComponent buttonComponent = new GpioButtonComponent(pin, PinState.HIGH, PinState.LOW);

        pin.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isHigh()) {
                console.println("button pressed");
            } else {
                console.println("button released");
            }
        });

        while (console.isRunning()) {
            Thread.sleep(10);
        }

        gpio.shutdown();
    }
    // end::ButtonClickDevice[]
}


