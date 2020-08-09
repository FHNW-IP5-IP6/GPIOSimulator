package fhnwgpio.components;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import fhnwgpio.components.helper.ComponentLogger;
import fhnwgpio.grove.GroveAdapter;

/**
 * HNW implementation for reading the current value of a button. This implementation supports Grove buttons,
 * Grove touch sensors, four legged buttons and many other digital input devices.
 */
public class ButtonComponent {
    private GpioPinDigitalInput pin;
    private boolean reverse;

    /**
     * Constructor for GPIO pins. User can set if the buttons value should be reversed.
     *
     * @param pin     Digital GPIO input pin
     * @param reverse The buttons values are reversed if this boolean is set to true
     */
    public ButtonComponent(GpioPinDigitalInput pin, boolean reverse) {
        this.pin = pin;
        this.reverse = reverse;
        ComponentLogger.logInfo("ButtonComponent: Button created for GPIO pin " + pin.getPin().getAddress());
    }

    /**
     * Constructor for digital grove devices. User can set if the buttons value should be reversed.
     *
     * @param adapter Digital grove adapter
     * @param reverse The buttons values are reversed if this boolean is set to true
     */
    public ButtonComponent(GroveAdapter adapter, boolean reverse) {
        this(GpioFactory.getInstance().provisionDigitalInputPin(adapter.getAdapter().getUpperPin()), reverse);
    }

    /**
     * Constructor for GPIO pins without value reversion.
     *
     * @param pin Digital GPIO input pin
     */
    public ButtonComponent(GpioPinDigitalInput pin) {
        this(pin, false);
    }

    /**
     * Constructor for digital grove devices without value reversion.
     *
     * @param adapter Digital grove adapter
     */
    public ButtonComponent(GroveAdapter adapter) {
        this(GpioFactory.getInstance().provisionDigitalInputPin(adapter.getAdapter().getUpperPin()), false);
    }

    /**
     * This function allows the user to change the buttons behaviour.
     *
     * @param reverse The buttons values are reversed if this boolean is set to true
     */
    public void setReverse(boolean reverse) {
        if (reverse) {
            ComponentLogger.logInfo("ButtonComponent: Button values will be reversed");
        } else {
            ComponentLogger.logInfo("ButtonComponent: Button values will not be reversed");
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
