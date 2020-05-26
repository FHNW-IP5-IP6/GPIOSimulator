package FHNWGPIO.Grove;

import com.pi4j.io.gpio.Pin;

public class Adapter {
    private Pin upperPin;
    private Pin lowerPin;
    private int analotI2CAddress;
    private int upperDeviceAddress;
    private int lowerDeviceAddress;
    private AdapterType type;

    protected Adapter(Pin upperPin, Pin lowerPin, AdapterType type) {
        this.upperPin = upperPin;
        this.lowerPin = lowerPin;
        this.type = type;
    }

    protected Adapter(int analotI2CAddress, int upperDeviceAddress, int lowerDeviceAddress, AdapterType type) {
        this.analotI2CAddress = analotI2CAddress;
        this.upperDeviceAddress = upperDeviceAddress;
        this.lowerDeviceAddress = lowerDeviceAddress;
        this.type = type;
    }

    public Pin getUpperPin() {
        return upperPin;
    }

    public Pin getLowerPin() {
        return lowerPin;
    }

    private int getAnalotI2CAddress() {
        return analotI2CAddress;
    }

    private int getUpperDeviceAddress() {
        return upperDeviceAddress;
    }

    private int getLowerDeviceAddress() {
        return lowerDeviceAddress;
    }

    public AdapterType getAdapterType() {
        return type;
    }
}
