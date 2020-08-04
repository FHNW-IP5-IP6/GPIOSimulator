package fhnwgpio.components;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;

/**
 * FHNW implementation for controlling dc motors using a motor control driver such as L293D. It allows to run motors
 * forwards or backwards. If either a software or hardware PWM pin is provided, the speed of the motor can be
 * controlled too.
 */
public class MotorComponent {
    private final int range = 1024;
    private final int clock = 512;
    private Console console;
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
     * @param console   Pi4J Console
     * @param forwards  Digital pin used to move the motor forwards
     * @param backwards Digital pin used to move the motor backwards
     */
    public MotorComponent(Console console, GpioPinDigitalOutput forwards, GpioPinDigitalOutput backwards) {
        this.console = console;
        this.forwards = forwards;
        this.backwards = backwards;
        isPwm = false;
        forwards.low();
        backwards.low();
    }

    /**
     * Constructor of the MotorComponent. This constructor allows controlling the motors direction and speed using
     * hardware or software pwm output pins.
     *
     * @param console   Pi4J Console
     * @param forwards  PWM pin used to move the motor forwards
     * @param backwards PWM pin used to move the motor backwards
     */
    public MotorComponent(Console console, GpioPinPwmOutput forwards, GpioPinPwmOutput backwards) {
        this.console = console;
        this.pwmForwards = forwards;
        this.pwmBackwards = backwards;
        isPwm = true;
        rangeAdjustment = isHardwarePwmPin(forwards.getPin()) && isHardwarePwmPin(backwards.getPin()) ? 10.24 : 1.0;
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange(range);
        Gpio.pwmSetClock(clock);
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
            console.println("motor is moving forwards with maximum power");
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
            throw new UnsupportedOperationException(
                    "void moveForwards(int power) can only be used with hardware pwm pin. please use void moveForwards() instead.");
        }

        if (power < 0 || power > 100) {
            throw new IllegalArgumentException("power must be between 0 and 100 percent");
        }

        pwmBackwards.setPwm(0);
        pwmForwards.setPwm((int) (power * rangeAdjustment));
        console.println("motor is moving forwards at " + power + "%");
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
            console.println("motor is moving backwards with maximum power");
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
            throw new UnsupportedOperationException(
                    "void moveBackwards(int power) can only be used with hardware pwm pin. please use void moveBackwards() instead.");
        }

        if (power < 0 || power > 100) {
            throw new IllegalArgumentException("power must be between 0 and 100 percent");
        }

        pwmForwards.setPwm(0);
        pwmBackwards.setPwm((int) (power * rangeAdjustment));
        console.println("motor is moving backwards at " + power + "%");
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
        console.println("motor stopped");
    }
    // end::MotorComponentStop[]

    /**
     * Checks if the provided pin is a valid hardware or software PWM pin.
     *
     * @param pin The pin which should be checked
     * @return True if the pin is a hardware
     */
    private boolean isHardwarePwmPin(Pin pin) {
        return pin.getAddress() == 12 || pin.getAddress() == 13 || pin.getAddress() == 18 || pin.getAddress() == 19;
    }
}
