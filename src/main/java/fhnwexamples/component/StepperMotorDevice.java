package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.StepperMotorComponent;
import fhnwgpio.components.helper.StepperMotorMode;

public class StepperMotorDevice extends Example {
    public StepperMotorDevice(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        GpioProvider provider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(provider);
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();

        GpioPinDigitalOutput out1 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17);
        GpioPinDigitalOutput out2 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_18);
        GpioPinDigitalOutput out3 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_27);
        GpioPinDigitalOutput out4 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_22);
        StepperMotorComponent stepperMotor = new StepperMotorComponent(console, out1, out2, out3, out4);

        console.println("Make 2048 steps forwards with half step mode");
        stepperMotor.setMode(StepperMotorMode.HALF_STEP);
        stepperMotor.stepForwards(2048);
        stepperMotor.stop();
        Thread.sleep(500);
        console.println("Make 2048 steps backwards with half step mode");
        stepperMotor.stepBackwards(2048);
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
        console.promptForExit();
    }
}
