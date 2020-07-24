package fhnwgpio.components;

import fhnwgpio.components.base.I2CBase;
import fhnwgpio.grove.Adapter;
import fhnwgpio.grove.GroveAdapter;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

// TODO: Add Logging
// TODO: Support other Potentiometers

/**
 * FHNW implementation for the analog Grove potentiometer.
 */
public class PotentiometerComponent extends I2CBase {
    private int minValue = 0;
    private int maxValue = 999;
    private Adapter adapter;

    /**
     * Constructor of the PotentiometerComponent with Grove adapter.
     *
     * @param groveAdapter  The Grove adapter where the potentiometer ist connected.
     * @throws IOException
     * @throws I2CFactory.UnsupportedBusNumberException
     */
    public PotentiometerComponent(GroveAdapter groveAdapter)
            throws IOException, I2CFactory.UnsupportedBusNumberException {
        super(groveAdapter.getAdapter().getAnalogI2CAddress(), I2CBus.BUS_1);
        this.adapter = groveAdapter.getAdapter();
    }

    /**
     * Set the value returned by getValue() to a specific range. The difference between minValue and MaxValue must be
     * between 10 and 999.
     *
     * @param minValue  The desired minimum value returned.
     * @param maxValue  The desired maximum value returned.
     */
    public void setRange(int minValue, int maxValue) {
        int difference = maxValue - minValue;
        if (difference < 10 || difference > 999) {
            throw new IllegalArgumentException(
                    "the difference between min and max value needs to be between 10 and 999");
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Get the current value from the potentiometer.
     *
     * @return  The current value of the potentiometer adjusted to the desired range.
     */
    public int getValue() {
        byte[] bytes = readData((byte) adapter.getDeviceAddress(), (byte) 2);
        int readValue = getIntegerFromBytes(bytes[0], bytes[1]);
        if (minValue == 0 && maxValue == 999) {
            return readValue;
        } else {
            int difference = maxValue - minValue;
            if (readValue == 0) {
                return readValue;
            } else {
                double reducedValue = ((double) readValue / 1000) * difference;
                return (int) Math.round(minValue + reducedValue);
            }
        }
    }

    /**
     * Converts a byte into an integer.
     *
     * @param low  Low byte
     * @param high High byte
     * @return  Integer value of the bytes.
     */
    private static int getIntegerFromBytes(byte low, byte high) {
        return (0xff & (byte) 0x00) << 24 | (0xff & (byte) 0x00) << 16 | (0xff & high) << 8 | (0xff & low) << 0;
    }
}
