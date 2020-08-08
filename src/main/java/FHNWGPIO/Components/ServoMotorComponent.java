package fhnwgpio.components;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.components.helper.PwmHelper;

/**
 * FHNW implementation for controlling servo motors. This class allows its user to control a wide range of 50 hertz
 * servo motors. The position of the motor can either be moved to a specific angle by providing the desired degree
 * or it can be set to an exact pulse length.
 */
public class ServoMotorComponent {
    private Console console;
    private GpioPinPwmOutput pin;
    private int pulseMin;
    private int pulseMax;
    private int maxDegrees;

    /**
     * Constructor with all possible values.
     *
     * @param console    Pi4J Console
     * @param pin        The hardware PWM pin
     * @param pulseMin   Minimal high time of the 50hz pulse in microseconds
     * @param pulseMax   Maximal high time of the 50hz pulse in microseconds
     * @param maxDegrees Maximal degrees the servo is able to spin
     * @throws IllegalArgumentException Thrown when no hardware pwm pin is provided
     */
    public ServoMotorComponent(Console console, GpioPinPwmOutput pin, int pulseMin, int pulseMax, int maxDegrees)
            throws IllegalArgumentException {
        if (!PwmHelper.isHardwarePwmPin(pin.getPin())) {
            throw new IllegalArgumentException("please use one of the Pis hardware PWM pins");
        }

        this.console = console;
        this.pin = pin;
        this.pulseMin = pulseMin;
        this.pulseMax = pulseMax;
        this.maxDegrees = maxDegrees;
        // tag::ServoMotorComponentSetPWMFrequency[]
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetClock(192);
        Gpio.pwmSetRange(2000);
        // end::ServoMotorComponentSetPWMFrequency[]
    }

    /**
     * Constructor with default value for maxDegrees of 180 degrees.
     *
     * @param console  Pi4J Console
     * @param pin      The hardware PWM pin
     * @param pulseMin Minimal high time of the 50hz pulse in microseconds
     * @param pulseMax Maximal high time of the 50hz pulse in microseconds
     */
    public ServoMotorComponent(Console console, GpioPinPwmOutput pin, int pulseMin, int pulseMax) {
        this(console, pin, pulseMin, pulseMax, 180);
    }

    /**
     * Constructor with default value for pulseMin = 500, pulseMax = 2400 and maxDegrees = 180. This constructor can
     * be used for most micro and mini servo motors.
     *
     * @param console Pi4J Console
     * @param pin     The hardware PWM pin
     */
    public ServoMotorComponent(Console console, GpioPinPwmOutput pin) {
        this(console, pin, 500, 2400, 180);
    }

    /**
     * Sets the servo position to a specific pulse high time in microseconds
     *
     * @param pulse The desired pulse length in microseconds
     * @throws IllegalArgumentException Thrown when a value smaller than pulseMin or bigger than pulse max is provided
     */
    // tag::ServoMotorComponentSetPosition[]
    public void setPosition(int pulse) throws IllegalArgumentException {
        if (pulse < pulseMin || pulse > pulseMax) {
            throw new IllegalArgumentException("please provide a value in the range pulseMin to pulseMax");
        }

        setPwm(pulse);
        console.println("set the position to pulse " + pulse);
    }
    // end::ServoMotorComponentSetPosition[]

    /**
     * Sets the servo position to a desired angle in degrees
     *
     * @param degree The desired rotation in degrees
     * @throws IllegalArgumentException Thrown when a value smaller than 0 or bigger than maxDegrees is provided
     */
    // tag::ServoMotorComponentSetPositionDegrees[]
    public void setPositionDegrees(int degree) throws IllegalArgumentException {
        if (degree < 0 || degree > maxDegrees) {
            throw new IllegalArgumentException("please provide a vlaue in the range 0 to maxDegrees");
        }

        double stepSize = ((double) maxDegrees / (pulseMax - pulseMin));
        int pulse = pulseMin + (int) Math.round(degree / stepSize);
        setPwm(pulse);
        console.println("set the position to degrees " + degree + " which is a pulse of " + pulse);
    }
    // end::ServoMotorComponentSetPositionDegrees[]

    /**
     * Sets the servo to its maximal position
     */
    public void setMax() {
        setPwm(pulseMax);
        console.println("set servo to maximal position");
    }

    /**
     * Sets the servo to its center position
     */
    public void setCenter() {
        setPwm(pulseMin + Math.round((pulseMax - pulseMin) / 2));
        console.println("set servo to center position");
    }

    /**
     * Sets the servo to its minimal position
     */
    public void setMin() {
        setPwm(pulseMin);
        console.println("set servo to minimal position");
    }

    /**
     * Sets the PWM value of the pin
     *
     * @param pwm The desired PWM value
     */
    // tag::ServoMotorComponentSetPWM[]
    private void setPwm(int pwm) {
        pin.setPwm(Math.round(pwm / 10));
    }
    // end::ServoMotorComponentSetPWM[]
}
