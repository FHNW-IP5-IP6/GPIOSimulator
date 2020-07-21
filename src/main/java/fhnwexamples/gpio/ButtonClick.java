package fhnwexamples.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;
import fhnwexamples.Example;

public class ButtonClick extends Example {

    public ButtonClick(int key, String title) {
        super(key, title);
    }

    // tag::ButtonClick[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_03);
        button.setShutdownOptions(true);

        Console console = new Console();
        console.promptForExit();

        button.addListener((GpioPinListenerDigital) event -> {
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
    // end::ButtonClick[]
}
