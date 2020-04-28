package gpioexample.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;
import gpioexample.Example;

public class ButtonClick extends Example {

    public ButtonClick(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on GPIO Extension Board

        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        final GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_03,
                PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        button.setShutdownOptions(true);

        button.addListener((GpioPinListenerDigital) event -> {
            if(event.getState().isHigh()){
                console.println("button pressed");
            }
            else {
                console.println("button released");
            }
        });

        while (console.isRunning()) {
            Thread.sleep(1000);
        }

        gpio.shutdown();
    }
}
