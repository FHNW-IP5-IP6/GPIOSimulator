package fhnwgpio.components;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.components.helper.ComponentLogger;
import fhnwgpio.grove.GroveAdapter;
import org.apache.logging.log4j.Level;

/**
 * FHNW implementation for controlling light emitting diodes. This class allows to control the state of the LED. If
 * a PWM pin is provide, the brightness of the LED can be controlled, too.
 */
public class LedComponent {
    private GpioPinDigitalOutput pin;
    private GpioPinPwmOutput pwmPin;
    private boolean dimmable;

    /**
     * Constructor for directly controlling digital LEDs without PWM.
     *
     * @param pin Digital GPIO pin
     */
    public LedComponent(GpioPinDigitalOutput pin) {
        this.pin = pin;
        this.dimmable = false;
        ComponentLogger.logInfo("LedComponent: Created component for pin " + pin.getPin().getAddress());
    }

    /**
     * Constructor for controlling Grove LEDs.
     *
     * @param adapter The digital pin
     */
    public LedComponent(GroveAdapter adapter) {
        this(GpioFactory.getInstance().provisionDigitalOutputPin(adapter.getAdapter().getUpperPin()));
    }

    /**
     * Constructor for controlling LEDs connected to pins with hard- or software PWM. This constructor allows
     * controlling the brightness of the LED.
     *
     * @param pwmPin Hard- or software PWM pin
     */
    // tag::LedComponentPwmPin[]
    public LedComponent(GpioPinPwmOutput pwmPin) {
        this.pwmPin = pwmPin;
        this.dimmable = true;

        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetClock(192);
        Gpio.pwmSetRange(2000);
        pwmPin.setPwmRange(100);
        ComponentLogger.logInfo("LedComponent: Created component for pwm pin " + pwmPin.getPin().getAddress());
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

        ComponentLogger.logInfo("LedComponent: LED turned on");
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
            pin.low();
        }

        ComponentLogger.logInfo("LedComponent: LED turned off");
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
            IllegalArgumentException exception = new IllegalArgumentException(
                    "LedComponent: Please provide a GpioPinPwmOutput Pin in the constructor if you want to dim your led");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        if (brightness < 0 || brightness > 100) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "LedComponent: Please enter a brightness between 0 and 100");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        pwmPin.setPwm(brightness);
        ComponentLogger.log(Level.INFO, "LedComponent: LED set to " + brightness + "% brightness");
    }
    // end::LedComponentSetBrightness[]
}
