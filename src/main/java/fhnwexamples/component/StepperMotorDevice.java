package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.StepperMotorComponent;
import fhnwgpio.components.helper.StepperMotorMode;

/**
 * Example for the StepperMotorComponent usage. This example initializes a new StepperMotorComponent object by using
 * the constructor with the most default values. The initial stepper motor mode is set to the default value
 * 'SINGLE_STEP' and the step delay is set to the default value of 2 milliseconds. The stepper motor makes one full
 * turn forwards followed by one full turn backwards in every mode.  After each mode there is a 5 second pause.
 */
public class StepperMotorDevice extends Example {
    public StepperMotorDevice(int key, String title) {
        super(key, title);
    }

    // tag::StepperMotorDevice[]
    @Override public void execute() throws Exception {
        GpioProvider provider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(provider);
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        GpioPinDigitalOutput out1 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17);
        GpioPinDigitalOutput out2 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_18);
        GpioPinDigitalOutput out3 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_27);
        GpioPinDigitalOutput out4 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_22);
        StepperMotorComponent stepperMotor = new StepperMotorComponent(out1, out2, out3, out4);

        console.println("Make 4096 steps forwards with half step mode");
        stepperMotor.setMode(StepperMotorMode.HALF_STEP);
        stepperMotor.stepForwards(4096);
        stepperMotor.stop();
        Thread.sleep(500);
        console.println("Make 4096 steps backwards with half step mode");
        stepperMotor.stepBackwards(4096);
        stepperMotor.stop();

        Thread.sleep(5000);

        console.println("Make 2048 steps forwards with single step mode");
        stepperMotor.setMode(StepperMotorMode.SINGLE_STEP);
        stepperMotor.stepForwards(2048);
        stepperMotor.stop();
        Thread.sleep(500);
        console.println("Make 2048 steps backwards with single step mode");
        stepperMotor.stepBackwards(2048);
        stepperMotor.stop();

        Thread.sleep(5000);

        console.println("Make 2048 steps forwards with double step mode");
        stepperMotor.setMode(StepperMotorMode.DOUBLE_STEP);
        stepperMotor.stepForwards(2048);
        stepperMotor.stop();
        Thread.sleep(500);
        console.println("Make 2048 steps backwards with double step mode");
        stepperMotor.stepBackwards(2048);
        stepperMotor.stop();

        gpio.shutdown();
    }
    // end::StepperMotorDevice[]
}
