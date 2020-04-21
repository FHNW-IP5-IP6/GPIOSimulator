package gpioexample.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class BlinkLed implements Example{

    @Override
    public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on GPIO Extension Board

        final GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_02, "Blinking LED" , PinState.LOW);
        led.setShutdownOptions(true, PinState.LOW);

        Console console = new Console();
        console.promptForExit();

        while (console.isRunning()) {
            if (led.isLow()) {
                led.high();
                console.println("Led is high.");
            } else {
                led.low();
                console.println("Led is low.");
            }
            Thread.sleep(1000);
        }

        gpio.shutdown();
    }
}
