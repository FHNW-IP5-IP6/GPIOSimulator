package gpioexample.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import gpioexample.Example;

public class MotorSpeedSoftPwm implements Example {
    private Console console;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwm = gpio.provisionSoftPwmOutputPin(RaspiBcmPin.GPIO_02);
        pwm.setShutdownOptions(true, PinState.LOW);
        pwm.setPwm(0); // Set software PWM to 0

        while (console.isRunning()){
            for (int i = 0; i < 10; i++) {
                pwm.setPwm(i * 10);
                console.println("Motor is at " + (100 - pwm.getPwm()) + "%");
                Thread.sleep(1000);
            }
            for (int i = 10; i > 1; i--) {
                pwm.setPwm(i * 10);
                console.println("Motor is at " + (100 - pwm.getPwm()) + "%");
                Thread.sleep(1000);
            }
        }

        gpio.shutdown();
    }
}
