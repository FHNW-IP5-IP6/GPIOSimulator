package fhnwgpio.components;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.grove.GroveAdapter;

/**
 * FHNW implementation for controlling light emitting diodes. This class allows to control the state of the LED. If
 * a PWM pin is provide, the brightness of the LED can be controlled, too.
 */
public class LedComponent {
    private Console console;
    private GpioPinDigitalOutput pin;
    private GpioPinPwmOutput pwmPin;
    private boolean dimmable;

    /**
     * Constructor for directly controlling digital LEDs without PWM.
     *
     * @param console Pi4J Console
     * @param pin     Digital GPIO pin
     */
    public LedComponent(Console console, GpioPinDigitalOutput pin) {
        this.console = console;
        this.pin = pin;
        this.dimmable = false;
    }

    /**
     * Constructor for controlling Grove LEDs.
     *
     * @param console Pi4J Console
     * @param adapter The digital pin
     */
    public LedComponent(Console console, GroveAdapter adapter) {
        this(console, GpioFactory.getInstance().provisionDigitalOutputPin(adapter.getAdapter().getUpperPin()));
    }

    /**
     * Constructor for controlling LEDs connected to pins with hard- or software PWM. This constructor allows
     * controlling the brightness of the LED.
     *
     * @param console Pi4J Console
     * @param pwmPin  Hard- or software PWM pin
     */
    // tag::LedComponentPwmPin[]
    public LedComponent(Console console, GpioPinPwmOutput pwmPin) {
        this.console = console;
        this.pwmPin = pwmPin;
        this.dimmable = true;

        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetClock(192);
        Gpio.pwmSetRange(2000);
        pwmPin.setPwmRange(100);
    }
    // end::LedComponentPwmPin[]

    /**
     * Turns the LED on. If a PWM pin was provided in the constructor, the brightness is set to 100%.
     */

    // tag::LedComponentTurnLedOn[]
    public void turnLedOn() {
        if (dimmable) {
            setBrightness(100);
        } else {
            pin.high();
        }
    }
    // end::LedComponentTurnLedOn[]

    /**
     * Turns the LED off. If a PWM pin was provided in the constructor, the brightness is set to 0%.
     */
    // tag::LedComponentTurnLedOff[]
    public void turnLedOff() {
        if (dimmable) {
            setBrightness(0);
        } else {
            pin.high();
        }
    }
    // end::LedComponentTurnLedOff[]

    /**
     * Set the brightness of a LED through PWM. Method may only be used if a PWM pin is provided.
     *
     * @param brightness The desired brightness of the LED
     * @throws IllegalArgumentException Thrown when a brightness smaller than 0 or bigger than 100 is provided or
     *                                  the pin provided is not a PWM pin.
     */
    // tag::LedComponentSetBrightness[]
    public void setBrightness(int brightness) throws IllegalArgumentException {
        if (!dimmable) {
            throw new IllegalArgumentException(
                    "please provide a GpioPinPwmOutput Pin in the constructor if you want to dim your led");
        }

        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("please enter a brightness between 0 and 100");
        }

        pwmPin.setPwm(brightness);
    }
    // end::LedComponentSetBrightness[]
}
