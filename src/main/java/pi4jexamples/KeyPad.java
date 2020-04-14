package pi4jexamples;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class KeyPad implements Example {
    Console console;

    @Override public void execute() {
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

        GpioPinDigitalInput rowPins[] = {
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_UP),
                gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_UP)
        };

        GpioPinDigitalOutput colPins[] = {
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, PinState.HIGH),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.HIGH),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.HIGH),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.HIGH)
        };

        char key = 0;

        while (console.isRunning()) {
            for (int i = 0; i < colPins.length; i++) {
                GpioPinDigitalOutput colPin = colPins[i];
                colPin.low();

                for (int j = 0; j < rowPins.length; j++) {
                    GpioPinDigitalInput rowPin = rowPins[j];
                    if (rowPin.isLow()) {
                        console.println(keys[i][j]);
                        while (rowPin.isLow()){}
                    }
                }

                colPin.high();
            }
        }

        gpio.shutdown();
    }
}
