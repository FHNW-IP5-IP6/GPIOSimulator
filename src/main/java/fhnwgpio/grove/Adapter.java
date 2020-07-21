package fhnwgpio.grove;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;

public class Adapter {
    private Pin upperPin;
    private Pin lowerPin;
    private int analogI2CAddress;
    private int deviceAddress;
    private I2CBus i2cBus;
    private AdapterType type;

    protected Adapter(Pin upperPin, Pin lowerPin, AdapterType type) {
        this.upperPin = upperPin;
        this.lowerPin = lowerPin;
        this.type = type;
    }

    protected Adapter(int analogI2CAddress, int deviceAddress, AdapterType type) {
        this.analogI2CAddress = analogI2CAddress;
        this.deviceAddress = deviceAddress;
        this.type = type;
    }

    protected Adapter(I2CBus bus, AdapterType type) {
        this.i2cBus = bus;
        this.type = type;
    }

    public Pin getUpperPin() {
        return upperPin;
    }

    public Pin getLowerPin() {
        return lowerPin;
    }

    public int getAnalogI2CAddress() {
        return analogI2CAddress;
    }

    public int getDeviceAddress() {
        return deviceAddress;
    }

    public AdapterType getAdapterType() {
        return type;
    }
}
