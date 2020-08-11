package fhnwgpio.components.helper;

import com.pi4j.io.gpio.Pin;

public class PwmHelper {
    /**
     * Checks if the provided pin is a valid hardware or software PWM pin.
     *
     * @param pin The pin which should be checked
     * @return True if the pin is a hardware
     */
    public static boolean isHardwarePwmPin(Pin pin) {
        return pin.getAddress() == 12 || pin.getAddress() == 13 || pin.getAddress() == 18 || pin.getAddress() == 19;
    }
}
