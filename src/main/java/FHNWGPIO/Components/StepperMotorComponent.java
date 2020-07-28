package fhnwgpio.components;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.util.Console;
import fhnwgpio.components.helper.StepperMotorMode;

public class StepperMotorComponent {
    private Console console;
    private GpioPinDigitalOutput digitalOut1;
    private GpioPinDigitalOutput digitalOut2;
    private GpioPinDigitalOutput digitalOut3;
    private GpioPinDigitalOutput digitalOut4;
    private int stepDelay;
    private StepperMotorMode mode;

    private final byte[] half_step = new byte[] { 0b0001, 0b0011, 0b0010, 0b0110, 0b0100, 0b1100, 0b1000, 0b1001 };
    private final byte[] single_step = new byte[] { 0b0001, 0b0010, 0b0100, 0b1000 };
    private final byte[] double_step = new byte[] { 0b0011, 0b0110, 0b1100, 0b1001 };
    private final byte[] rev_half_step = new byte[] { 0b1001, 0b1000, 0b1100, 0b0100, 0b0110, 0b0010, 0b0011, 0b0001 };
    private final byte[] rev_single_step = new byte[] { 0b1000, 0b0100, 0b0010, 0b0001 };
    private final byte[] rev_double_step = new byte[] { 0b1001, 0b1100, 0b0110, 0b0011 };

    public StepperMotorComponent(Console console, GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4, int stepDelay, StepperMotorMode mode) {
        this.console = console;
        this.digitalOut1 = digitalOut1;
        this.digitalOut2 = digitalOut2;
        this.digitalOut3 = digitalOut3;
        this.digitalOut4 = digitalOut4;
        setStepDelay(stepDelay);
        setMode(mode);
    }

    public StepperMotorComponent(Console console, GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4, int stepDelay) {
        this(console, digitalOut1, digitalOut2, digitalOut3, digitalOut4, stepDelay, StepperMotorMode.SINGLE_STEP);
    }

    public StepperMotorComponent(Console console, GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4, StepperMotorMode mode) {
        this(console, digitalOut1, digitalOut2, digitalOut3, digitalOut4, 2, mode);
    }

    public StepperMotorComponent(Console console, GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4) {
        this(console, digitalOut1, digitalOut2, digitalOut3, digitalOut4, 2, StepperMotorMode.SINGLE_STEP);
    }

    public void setStepDelay(int stepDelay) throws IllegalArgumentException {
        if (stepDelay < 2) {
            throw new IllegalArgumentException("stepDelay must be at least 2 milliseconds.");
        }

        this.stepDelay = stepDelay;
        console.println("stepDelay set to " + this.stepDelay + " milliseconds.");
    }

    public int getStepDelay() {
        return stepDelay;
    }

    public void setMode(StepperMotorMode mode) {
        this.mode = mode;
        console.println("set mode to " + mode + ".");
    }

    public StepperMotorMode getMode() {
        return mode;
    }

    public void stepForwards(int steps) throws InterruptedException {
        step(steps, true);
        console.println("did " + steps + " steps forwards.");
    }

    public void stepBackwards(int steps) throws InterruptedException {
        step(steps, false);
        console.println("did " + steps + " steps backwards.");
    }

    private void step(int steps, boolean stepForwards) throws InterruptedException {
        for (int i = 0; i < steps; i++) {
            byte element = getElement(i, stepForwards);
            digitalOut1.setState((element >> 0 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            digitalOut2.setState((element >> 1 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            digitalOut3.setState((element >> 2 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            digitalOut4.setState((element >> 3 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            Thread.sleep(stepDelay);
        }
    }

    public void stop() {
        digitalOut1.low();
        digitalOut2.low();
        digitalOut3.low();
        digitalOut4.low();
        console.println("motor stopped.");
    }

    private byte getElement(int position, boolean forwards) {
        switch (mode) {
        case HALF_STEP:
            return forwards ? half_step[position % half_step.length] : rev_half_step[position % rev_half_step.length];
        case SINGLE_STEP:
            return forwards ?
                    single_step[position % single_step.length] :
                    rev_single_step[position % rev_single_step.length];
        case DOUBLE_STEP:
            return forwards ?
                    double_step[position % double_step.length] :
                    rev_double_step[position % rev_double_step.length];
        default:
            throw new IllegalArgumentException("StepperMotorMode is invalid.");
        }
    }
}
