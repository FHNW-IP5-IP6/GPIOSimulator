package pi4jexamples;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class MotorSpeed implements Example {
    private Console console;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_17);
        pwm.setPwm(0); // Set software PWM to 0

        while (console.isRunning()){
            for (int i = 0; i < 10; i++) {
                pwm.setPwm(i * 10);
                console.println("Motor is at " + pwm.getPwm() + "%");
                Thread.sleep(500);
            }
            for (int i = 10; i > 0; i--) {
                pwm.setPwm(i * 10);
                console.println("Motor is at " + pwm.getPwm() + "%");
                Thread.sleep(500);
            }
        }
        pwm.setPwm(0);

        gpio.shutdown();
    }
}
