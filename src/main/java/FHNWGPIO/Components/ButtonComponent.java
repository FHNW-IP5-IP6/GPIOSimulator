package fhnwgpio.components;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.util.Console;
import fhnwgpio.grove.GroveAdapter;

/**
 * HNW implementation for reading the current value of a button. This implementation supports Grove buttons,
 * Grove touch sensors, four legged buttons and many other digital input devices.
 */
public class ButtonComponent {
    private Console console;
    private GpioPinDigitalInput pin;
    private boolean reverse;

    /**
     * Constructor for GPIO pins. User can set if the buttons value should be reversed.
     *
     * @param console The Pi4J Console
     * @param pin     Digital GPIO input pin
     * @param reverse The buttons values are reversed if this boolean is set to true
     */
    public ButtonComponent(Console console, GpioPinDigitalInput pin, boolean reverse) {
        this.console = console;
        this.pin = pin;
        this.reverse = reverse;
    }

    /**
     * Constructor for digital grove devices. User can set if the buttons value should be reversed.
     *
     * @param console The Pi4J Console
     * @param adapter Digital grove adapter
     * @param reverse The buttons values are reversed if this boolean is set to true
     */
    public ButtonComponent(Console console, GroveAdapter adapter, boolean reverse) {
        this(console, GpioFactory.getInstance().provisionDigitalInputPin(adapter.getAdapter().getUpperPin()), reverse);
    }

    /**
     * Constructor for GPIO pins without value reversion.
     *
     * @param console The Pi4J Console
     * @param pin     Digital GPIO input pin
     */
    public ButtonComponent(Console console, GpioPinDigitalInput pin) {
        this(console, pin, false);
    }

    /**
     * Constructor for digital grove devices without value reversion.
     *
     * @param console The Pi4J Console
     * @param adapter Digital grove adapter
     */
    public ButtonComponent(Console console, GroveAdapter adapter) {
        this(console, GpioFactory.getInstance().provisionDigitalInputPin(adapter.getAdapter().getUpperPin()), false);
    }

    /**
     * This function allows the user to change the buttons behaviour.
     *
     * @param reverse The buttons values are reversed if this boolean is set to true
     */
    public void setReverse(boolean reverse) {
        if (reverse) {
            console.println("Button values will be reversed");
        } else {
            console.println("Button values will not be reversed");
        }
        this.reverse = reverse;
    }

    /**
     * Returns if the button is pressed.
     *
     * @return True if the button is pressed.
     */
    // tag::ButtonComponentIsPressed[]
    public boolean isPressed() {
        return reverse ? !pin.isHigh() : pin.isHigh();
    }
    // end::ButtonComponentIsPressed[]

    /**
     * Returns if the button is released.
     *
     * @return True if the button is released
     */
    // tag::ButtonComponentIsReleased[]
    public boolean isReleased() {
        return reverse ? !pin.isLow() : pin.isLow();
    }
    // end::ButtonComponentIsReleased[]
}
