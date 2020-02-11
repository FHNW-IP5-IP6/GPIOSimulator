package gpiosimulator;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;

public class HelloGPIO {

    public static void main(String[] args) throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on GPIO Extension Board

        final GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_16, // PIN NUMBER
                                                                  "My LED" ,           // PIN FRIENDLY NAME (optional)
                                                                   PinState.LOW);      // PIN STARTUP STATE (optional)


        led.setShutdownOptions(true, PinState.LOW);

        final GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_05,
                                                                         PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        button.setShutdownOptions(true);

        button.addListener((GpioPinListenerDigital) event -> {
            if(event.getState().isHigh()){
                led.high();
            }
            else {
                led.low();
            }
        });

        Console console = new Console();
        console.promptForExit();

        while (console.isRunning()) {
            Thread.sleep(1000);
        }

        gpio.shutdown();
    }
}
