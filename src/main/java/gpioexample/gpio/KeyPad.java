package gpioexample.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class KeyPad implements Example {
    Console console;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        console = new Console();
        console.promptForExit();

        char keys[][] = {
                { '1', '2', '3', 'A' },
                { '4', '5', '6', 'B' },
                { '7', '8', '9', 'C' },
                { '*', '0', '#', 'D' }
        };

        GpioPinDigitalInput colPins[] = {
                gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_18),
                gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_23),
                gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_24),
                gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_25)
        };

        GpioPinDigitalOutput rowPins[] = {
                gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17, PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_27, PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_22, PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_05, PinState.LOW)
        };

        while (console.isRunning()) {
            for (int i = 0; i < rowPins.length; i++) {
                GpioPinDigitalOutput rowPin = rowPins[i];
                rowPin.high();

                for (int j = 0; j < colPins.length; j++) {
                    GpioPinDigitalInput colPin = colPins[j];
                    if (colPin.isHigh()) {
                        console.println(keys[i][j]);
                        while (colPin.isHigh()){}
                    }
                }

                rowPin.low();
            }
        }

        gpio.shutdown();
    }
}
