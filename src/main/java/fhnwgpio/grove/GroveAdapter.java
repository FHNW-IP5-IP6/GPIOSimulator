package fhnwgpio.grove;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiBcmPin;

/**
 * This enum provides Adapter objects for programming the adaptors of the grove hat.
 * Attention! Board only supports 3.3V grove Devices!
 * https://wiki.seeedstudio.com/Grove_Base_Hat_for_Raspberry_Pi/
 * The analog adapters are managed via the I2C bus. The addresses correspond to the python implementation
 * of the grove Hat from Seed Studio.
 * https://github.com/Seeed-Studio/grove.py/blob/master/grove/adc.py
 */
public enum GroveAdapter {
    A0(0x30, AdapterType.ANALOG),
    A2(0x32, AdapterType.ANALOG),
    A4(0x34, AdapterType.ANALOG),
    A6(0x35, AdapterType.ANALOG),
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
    private int analogI2CBus = 0x04;

    GroveAdapter(Pin upperPin, Pin lowerPin, AdapterType type) {
        this.adapter = new Adapter(upperPin, lowerPin, type);
    }

    GroveAdapter(int deviceAddress, AdapterType type) {
        this.adapter = new Adapter(analogI2CBus, deviceAddress, type);
    }

    public Adapter getAdapter() {
        return adapter;
    }
}
