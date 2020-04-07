package pi4jexamples;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class ServoMotor implements Example {
    private Console console;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_13);
        pwm.setShutdownOptions(true, PinState.LOW);
        pwm.setPwmRange(1929 * 200);

        while (console.isRunning()){

        }

        gpio.shutdown();
    }
}
