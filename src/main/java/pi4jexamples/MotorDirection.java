package pi4jexamples;

import com.pi4j.io.gpio.*;

public class MotorDirection implements Example {
    private GpioPinDigitalOutput moveForward;
    private GpioPinDigitalOutput moveBackward;

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(
                new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on GPIO Extension Board
        final GpioController gpio = GpioFactory.getInstance();
        moveForward = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17, "Motor Forward", PinState.LOW);
        moveBackward = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_27, "Motor Backward", PinState.LOW);

        while (true) {
            motorForward();
            Thread.sleep(1000);
            motorHalt();
            Thread.sleep(1000);
            motorBackward();
            Thread.sleep(1000);
            motorHalt();
            Thread.sleep(1000);
        }
    }

    private void motorForward() {
        moveForward.high();
        moveBackward.low();
    }

    private void motorBackward() {
        moveForward.low();
        moveBackward.high();
    }

    private void motorHalt() {
        moveForward.low();
        moveBackward.low();
    }
}
