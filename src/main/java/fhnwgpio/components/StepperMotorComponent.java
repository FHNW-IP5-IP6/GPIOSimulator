package fhnwgpio.components;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import fhnwgpio.components.helper.ComponentLogger;
import fhnwgpio.components.helper.StepperMotorMode;

/**
 * FHNW implementation for controlling stepper motors. The implementation allows to control 1/64 gear reduced
 * stepper motors by stepping them in half step, single step and double step mode.
 */
public class StepperMotorComponent {
    private GpioPinDigitalOutput digitalOut1;
    private GpioPinDigitalOutput digitalOut2;
    private GpioPinDigitalOutput digitalOut3;
    private GpioPinDigitalOutput digitalOut4;
    private int stepDelay;
    private StepperMotorMode mode;

    // tag::StepperMotorComponentHalfStepping[]
    private final byte[] half_step = new byte[] { 0b1000, 0b1100, 0b0100, 0b0110, 0b0010, 0b0011, 0b0001, 0b1001 };
    // end::StepperMotorComponentHalfStepping[]
    // tag::StepperMotorComponentSingleStepping[]
    private final byte[] single_step = new byte[] { 0b1000, 0b0100, 0b0010, 0b0001 };
    // end::StepperMotorComponentSingleStepping[]
    // tag::StepperMotorComponentDoubleStepping[]
    private final byte[] double_step = new byte[] { 0b1100, 0b0110, 0b0011, 0b1001 };
    // end::StepperMotorComponentDoubleStepping[]
    // tag::StepperMotorComponentRevHalfStepping[]
    private final byte[] rev_half_step = new byte[] { 0b1001, 0b0001, 0b0011, 0b0010, 0b0110, 0b0100, 0b1100, 0b1000 };
    // end::StepperMotorComponentRevHalfStepping[]
    private final byte[] rev_single_step = new byte[] { 0b0001, 0b0010, 0b0100, 0b1000 };
    private final byte[] rev_double_step = new byte[] { 0b1001, 0b0011, 0b0110, 0b1100 };

    private int currentPosition = 0;
    private boolean lastDirectionForward = true;

    /**
     * Constructor which allows to specify all the different Parameters.
     *
     * @param digitalOut1 The first digital output pin
     * @param digitalOut2 The second digital output pin
     * @param digitalOut3 The third digital output pin
     * @param digitalOut4 The fourth digital output pin
     * @param stepDelay   The delay between the steps. Must be at least 2 ms.
     * @param mode        The desired stepper motor mode.
     */
    public StepperMotorComponent(GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4, int stepDelay, StepperMotorMode mode) {
        this.digitalOut1 = digitalOut1;
        this.digitalOut2 = digitalOut2;
        this.digitalOut3 = digitalOut3;
        this.digitalOut4 = digitalOut4;
        setStepDelay(stepDelay);
        setMode(mode);
        ComponentLogger.logInfo(
                "StepperMotorComponent: StepperMotor created for GPIO pins " + digitalOut1.getPin().getAddress() + ", "
                        + digitalOut2.getPin().getAddress() + ", " + digitalOut3.getPin().getAddress() + " and "
                        + digitalOut4.getPin().getAddress() + " with a stepDelay of " + stepDelay + " milliseconds");
    }

    /**
     * Constructor which uses the default value 'SINGLE_STEP' for the stepper motor mode.
     *
     * @param digitalOut1 The first digital output pin
     * @param digitalOut2 The second digital output pin
     * @param digitalOut3 The third digital output pin
     * @param digitalOut4 The fourth digital output pin
     * @param stepDelay   The delay between the steps. Must be at least 2 ms.
     */
    public StepperMotorComponent(GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4, int stepDelay) {
        this(digitalOut1, digitalOut2, digitalOut3, digitalOut4, stepDelay, StepperMotorMode.SINGLE_STEP);
    }

    /**
     * Constructor which uses the default value of 2 milliseconds for the step delay.
     *
     * @param digitalOut1 The first digital output pin
     * @param digitalOut2 The second digital output pin
     * @param digitalOut3 The third digital output pin
     * @param digitalOut4 The fourth digital output pin
     * @param mode        The desired stepper motor mode.
     */
    public StepperMotorComponent(GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4, StepperMotorMode mode) {
        this(digitalOut1, digitalOut2, digitalOut3, digitalOut4, 2, mode);
    }

    /**
     * Constructor which uses the default value of 2 milliseconds for the step delay and the default stepper motor mode
     * 'SINGLE_STEP'.
     *
     * @param digitalOut1 The first digital output pin
     * @param digitalOut2 The second digital output pin
     * @param digitalOut3 The third digital output pin
     * @param digitalOut4 The fourth digital output pin
     */
    public StepperMotorComponent(GpioPinDigitalOutput digitalOut1, GpioPinDigitalOutput digitalOut2,
            GpioPinDigitalOutput digitalOut3, GpioPinDigitalOutput digitalOut4) {
        this(digitalOut1, digitalOut2, digitalOut3, digitalOut4, 2, StepperMotorMode.SINGLE_STEP);
    }

    /**
     * Setter of the step delay value.
     *
     * @param stepDelay The step delay in milliseconds to be set.
     * @throws IllegalArgumentException Exception is thrown when a value smaller than 2 is provided.
     */
    public void setStepDelay(int stepDelay) throws IllegalArgumentException {
        if (stepDelay < 2) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "StepperMotorComponent: stepDelay must be at least 2 milliseconds");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        this.stepDelay = stepDelay;
        ComponentLogger.logInfo("StepperMotorComponent: stepDelay set to " + this.stepDelay + " milliseconds.");
    }

    /**
     * Getter of the step delay value.
     *
     * @return The current step delay value.
     */
    public int getStepDelay() {
        return stepDelay;
    }

    /**
     * Setter of the stepper motor mode value.
     *
     * @param mode The mode to be set.
     */
    public void setMode(StepperMotorMode mode) {
        this.mode = mode;
        ComponentLogger.logInfo("StepperMotorComponent: Set mode to " + mode);
    }

    /**
     * Getter of the stepper motor mode value.
     *
     * @return The current stepper motor mode value.
     */
    public StepperMotorMode getMode() {
        return mode;
    }

    /**
     * Step the stepper motor forwards using the currently set mode for a specific amount of steps.
     *
     * @param steps Number of steps the motor should move.
     * @throws InterruptedException Exception might be thrown because of Thread.sleep().
     */
    public void stepForwards(int steps) throws InterruptedException {
        step(steps, true);
        ComponentLogger.logInfo("StepperMotorComponent: Did " + steps + " steps forwards");
    }

    /**
     * Step the stepper motor backwards using the currently set mode for a specific amount of steps.
     *
     * @param steps Number of steps the motor should move.
     * @throws InterruptedException Exception might be thrown because of Thread.sleep().
     */
    public void stepBackwards(int steps) throws InterruptedException {
        step(steps, false);
        ComponentLogger.logInfo("StepperMotorComponent: Did " + steps + " steps backwards");
    }

    /**
     * This method controls the motor through the GPIO pins.
     *
     * @param steps        Number of steps the motor should move.
     * @param stepForwards The moving direction.
     * @throws InterruptedException Exception might be thrown because of Thread.sleep().
     */
    // tag::StepperMotorComponentStep[]
    private void step(int steps, boolean stepForwards) throws InterruptedException {
        if (stepForwards != lastDirectionForward) {
            shiftCurrentPosition();
            lastDirectionForward = stepForwards;
        }

        for (int i = 0; i < steps; i++) {
            byte element = getElement(i, stepForwards);
            digitalOut1.setState((element >> 0 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            digitalOut2.setState((element >> 1 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            digitalOut3.setState((element >> 2 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            digitalOut4.setState((element >> 3 & 0b0001) == 1 ? PinState.HIGH : PinState.LOW);
            Thread.sleep(stepDelay);
        }

        setCurrentPosition(steps);
    }
    // end::StepperMotorComponentStep[]

    /**
     * This methods stops the motor and clears the magnetic field.
     */
    public void stop() {
        digitalOut1.low();
        digitalOut2.low();
        digitalOut3.low();
        digitalOut4.low();
        ComponentLogger.logInfo("StepperMotorComponent: Motor stopped");
    }

    /**
     * Returns the element of the array at the current position from the actually set stepper motor mode.
     *
     * @param stepNumber The number of the current step.
     * @param forwards   The moving direction.
     * @return The correlating element of the array according to the stepper motor mode.
     */
    // tag::StepperMotorComponentGetElement[]
    private byte getElement(int stepNumber, boolean forwards) {
        int elementPosition = stepNumber + currentPosition;
        switch (mode) {
        case HALF_STEP:
            return forwards ?
                    half_step[Math.floorMod(elementPosition, half_step.length)] :
                    rev_half_step[Math.floorMod(elementPosition, rev_half_step.length)];
        case SINGLE_STEP:
            return forwards ?
                    single_step[Math.floorMod(elementPosition, single_step.length)] :
                    rev_single_step[Math.floorMod(elementPosition, rev_single_step.length)];
        case DOUBLE_STEP:
            return forwards ?
                    double_step[Math.floorMod(elementPosition, double_step.length)] :
                    rev_double_step[Math.floorMod(elementPosition, rev_double_step.length)];
        default:
            IllegalArgumentException exception = new IllegalArgumentException("StepperMotorMode is invalid.");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }
    }
    // end::StepperMotorComponentGetElement[]

    /**
     * Calculates the current position in the array of the current stepper motor mode.
     *
     * @param steps The number of steps made.
     */
    private void setCurrentPosition(int steps) {
        switch (mode) {
        case HALF_STEP:
            currentPosition = steps % half_step.length;
            break;
        case SINGLE_STEP:
            currentPosition = steps % single_step.length;
            break;
        case DOUBLE_STEP:
            currentPosition = steps % double_step.length;
            break;
        }
    }

    /**
     * This method needs to be called whenever the direction of the motor is changed since the position must be
     * recalculated on direction change.
     */
    // tag::StepperMotorComponentShiftPosition[]
    private void shiftCurrentPosition() {
        switch (mode) {
        case HALF_STEP:
            currentPosition = half_step.length - 1 - currentPosition;
            break;
        case SINGLE_STEP:
            currentPosition = single_step.length - 1 - currentPosition;
            break;
        case DOUBLE_STEP:
            currentPosition = double_step.length - 1 - currentPosition;
            break;
        }
    }
    // end::StepperMotorComponentShiftPosition[]
}
