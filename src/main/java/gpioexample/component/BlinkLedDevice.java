package gpioexample.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import gpiosimulator.GpioSimulatorFactory;
import gpiosimulator.component.light.LEDBase;
import gpiosimulator.component.light.impl.GpioLEDComponent;
import gpioexample.Example;

public class BlinkLedDevice extends Example {

    public BlinkLedDevice(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on GPIO Extension Board

        final GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_02, "Blinking LED" , PinState.LOW);
        led.setShutdownOptions(true, PinState.LOW);

        GpioSimulatorFactory gpioSimulatorFactory = new GpioSimulatorFactory(false);

        LEDBase ledComponent =  gpioSimulatorFactory.getLED(led);

        Console console = new Console();
        console.promptForExit();

        long delay = 1000;
        console.println("start blinking with "+delay+" delay");

        ledComponent.blink(delay);

        gpio.shutdown();
    }
}
