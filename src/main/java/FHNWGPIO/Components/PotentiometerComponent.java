package FHNWGPIO.Components;

import FHNWGPIO.Components.Base.I2CBase;
import FHNWGPIO.Grove.Adapter;
import FHNWGPIO.Grove.GroveAdapter;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

public class PotentiometerComponent extends I2CBase {
    private int minValue = 0;
    private int maxValue = 999;
    private Adapter adapter;

    public PotentiometerComponent(GroveAdapter groveAdapter)
            throws IOException, I2CFactory.UnsupportedBusNumberException {
        super(groveAdapter.getAdapter().getAnalogI2CAddress(), 1);
        this.adapter = groveAdapter.getAdapter();
    }

    public void setRange(int minValue, int maxValue) {
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue needs to be bigger than -1");
        }

        if (maxValue > 999) {
            throw new IllegalArgumentException("maxValue needs to be smaller than 1000");
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int getValue() {
        byte[] bytes = super.readData((byte) adapter.getDeviceAddress(), (byte) 0x02);
        int readValue = getIntegerFromBytes(bytes[0], bytes[1]);
        if (minValue == 0 && maxValue == 999) {
            return readValue;
        } else {
            int difference = maxValue - minValue;
            if (readValue == 0) {
                return readValue;
            } else {
                double reducedValue = (readValue / 1000) * difference;
                return (int) Math.round(minValue + reducedValue);
            }
        }
    }

    private static int getIntegerFromBytes(byte low, byte high) {
        return (0xff & (byte) 0x00) << 24 | (0xff & (byte) 0x00) << 16 | (0xff & high) << 8 | (0xff & low) << 0;
    }
}
