package FHNWGPIO.Grove;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiBcmPin;

/**
 * This enum provides Adapter objects for programming the adaptors of the grove hat.
 * Attention! Board only supports 3.3V Grove Devices!
 * https://wiki.seeedstudio.com/Grove_Base_Hat_for_Raspberry_Pi/
 */
public enum GroveAdapter {
    // TODO: Handle Analog Pins
    // TODO: Handle I2C Pins
    D5(RaspiBcmPin.GPIO_05, RaspiBcmPin.GPIO_06, AdapterType.DIGITAL),
    D16(RaspiBcmPin.GPIO_16, RaspiBcmPin.GPIO_17, AdapterType.DIGITAL),
    D18(RaspiBcmPin.GPIO_18, RaspiBcmPin.GPIO_19, AdapterType.DIGITAL),
    D22(RaspiBcmPin.GPIO_22, RaspiBcmPin.GPIO_23, AdapterType.DIGITAL),
    D24(RaspiBcmPin.GPIO_24, RaspiBcmPin.GPIO_25, AdapterType.DIGITAL),
    D26(RaspiBcmPin.GPIO_26, RaspiBcmPin.GPIO_27, AdapterType.DIGITAL),
    // I2C(RaspiBcmPin.GPIO_02, RaspiBcmPin.GPIO_03, AdapterType.I2C),
    PWM(RaspiBcmPin.GPIO_12, RaspiBcmPin.GPIO_13, AdapterType.PWM),
    UART(RaspiBcmPin.GPIO_14, RaspiBcmPin.GPIO_15, AdapterType.UART);

    private Adapter adapter;

    GroveAdapter(Pin upperPin, Pin lowerPin, AdapterType type) {
        this.adapter = new Adapter(upperPin, lowerPin, type);
    }

    public Adapter getAdapter() {
        return adapter;
    }
}
