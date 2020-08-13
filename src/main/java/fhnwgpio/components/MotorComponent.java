package fhnwgpio.components;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.components.helper.ComponentLogger;
import fhnwgpio.components.helper.PwmHelper;

/**
 * FHNW implementation for controlling dc motors using a motor control driver such as L293D. It allows to run motors
 * forwards or backwards. If either a software or hardware PWM pin is provided, the speed of the motor can be
 * controlled too.
 */
public class MotorComponent {
    private final int range = 1024;
    private final int clock = 512;
    private GpioPinDigitalOutput forwards;
    private GpioPinDigitalOutput backwards;
    private GpioPinPwmOutput pwmForwards;
    private GpioPinPwmOutput pwmBackwards;
    private boolean isPwm;
    private double rangeAdjustment;

    /**
     * Constructor of the MotorComponent. This constructor allows controlling the motors direction using digital
     * output pins.
     *
     * @param forwards  Digital pin used to move the motor forwards
     * @param backwards Digital pin used to move the motor backwards
     */
    public MotorComponent(GpioPinDigitalOutput forwards, GpioPinDigitalOutput backwards) {
        this.forwards = forwards;
        this.backwards = backwards;
        isPwm = false;
        forwards.low();
        backwards.low();
        ComponentLogger.logInfo(
                "MotorComponent: Digital motor created for GPIO pins " + forwards.getPin().getAddress() + " and "
                        + backwards.getPin().getAddress());
    }

    /**
     * Constructor of the MotorComponent. This constructor allows controlling the motors direction and speed using
     * hardware or software pwm output pins.
     *
     * @param forwards  PWM pin used to move the motor forwards
     * @param backwards PWM pin used to move the motor backwards
     */
    public MotorComponent(GpioPinPwmOutput forwards, GpioPinPwmOutput backwards) {
        this.pwmForwards = forwards;
        this.pwmBackwards = backwards;
        isPwm = true;
        rangeAdjustment =
                PwmHelper.isHardwarePwmPin(forwards.getPin()) && PwmHelper.isHardwarePwmPin(backwards.getPin()) ?
                        10.24 :
                        1.0;
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange(range);
        Gpio.pwmSetClock(clock);
        ComponentLogger.logInfo(
                "MotorComponent: PWM motor created for GPIO pins " + pwmForwards.getPin().getAddress() + " and "
                        + pwmBackwards.getPin().getAddress());
    }

    /**
     * Runs the motor run forwards at full speed.
     */
    // tag::MotorComponentMoveForwards[]
    public void moveForwards() {
        if (isPwm) {
            moveForwards(100);
        } else {
            backwards.low();    //Input 2
            forwards.high();    //Input 1
            ComponentLogger.logInfo("MotorComponent: Motor is moving forwards with maximum power");
        }
    }
    // end::MotorComponentMoveForwards[]

    /**
     * Runs the motor forwards at a specific speed.
     *
     * @param power Desired power. Value needs to be in the range from 0 to 100 percent.
     * @throws UnsupportedOperationException Exception is thrown when method is called using digital pins.
     * @throws IllegalArgumentException      Exception is thrown when the power value is not in the range from 0 to 100.
     */
    // tag::MotorComponentMoveForwardsPwm[]
    public void moveForwards(int power) throws UnsupportedOperationException, IllegalArgumentException {
        if (!isPwm) {
            UnsupportedOperationException exception = new UnsupportedOperationException(
                    "MotorComponent: void moveForwards(int power) can only be used with hardware pwm pin. Please use void moveForwards() instead.");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        if (power < 0 || power > 100) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "MotorComponent: Power must be between 0 and 100 percent");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        pwmBackwards.setPwm(0);
        pwmForwards.setPwm((int) (power * rangeAdjustment));

        ComponentLogger.logInfo("MotorComponent: Motor is moving forwards at " + power + "%");
    }
    // end::MotorComponentMoveForwardsPwm[]

    /**
     * Runs the motor run backwards at full speed.
     */
    public void moveBackwards() {
        if (isPwm) {
            moveBackwards(100);
        } else {
            forwards.low();     //Input 1
            backwards.high();   //Input 2
            ComponentLogger.logInfo("MotorComponent: Motor is moving backwards with maximum power");
        }
    }

    /**
     * Runs the motor backwards at a specific speed.
     *
     * @param power Desired power. Value needs to be in the range from 0 to 100 percent.
     * @throws UnsupportedOperationException Exception is thrown when method is called using digital pins.
     * @throws IllegalArgumentException      Exception is thrown when the power value is not in the range from 0 to 100.
     */
    public void moveBackwards(int power) throws UnsupportedOperationException, IllegalArgumentException {
        if (!isPwm) {
            UnsupportedOperationException exception = new UnsupportedOperationException(
                    "MotorComponent: void moveBackwards(int power) can only be used with hardware pwm pin. Please use void moveBackwards() instead.");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        if (power < 0 || power > 100) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "MotorComponent: Power must be between 0 and 100 percent");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        pwmForwards.setPwm(0);
        pwmBackwards.setPwm((int) (power * rangeAdjustment));
        ComponentLogger.logInfo("MotorComponent Motor is moving backwards at " + power + "%");
    }

    /**
     * Stops the motor.
     */
    // tag::MotorComponentStop[]
    public void stop() {
        if (isPwm) {
            pwmForwards.setPwm(0);
            pwmBackwards.setPwm(0);
        } else {
            forwards.low();     //Input 1
            backwards.low();    //Input 2
        }

        ComponentLogger.logInfo("MotorComponent: Motor stopped");
    }
    // end::MotorComponentStop[]
}
