package gpioexample.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class ServoMotor implements Example {
    private Console console;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        GpioPin pinBcm = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_18);

        console = new Console();
        console.promptForExit();

        com.pi4j.wiringpi.Gpio.pinMode(pinBcm.getPin().getAddress(), com.pi4j.wiringpi.Gpio.PWM_OUTPUT);
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);

        com.pi4j.wiringpi.Gpio.pwmSetClock(192);
        com.pi4j.wiringpi.Gpio.pwmSetRange(2000);

        while (console.isRunning()) {
            for (int i = 55; i < 260; i = i + 5) {
                com.pi4j.wiringpi.Gpio.pwmWrite(pinBcm.getPin().getAddress(), i);
                Thread.sleep(100);
            }

            com.pi4j.wiringpi.Gpio.pwmWrite(pinBcm.getPin().getAddress(), 55);
            Thread.sleep(1000);
        }

        gpio.shutdown();
    }
}
