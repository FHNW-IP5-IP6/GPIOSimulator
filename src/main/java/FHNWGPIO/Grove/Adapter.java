package FHNWGPIO.Grove;

import com.pi4j.io.gpio.Pin;

public class Adapter {
    private Pin upperPin;
    private Pin lowerPin;
    private AdapterType type;

    protected Adapter(Pin upperPin, Pin lowerPin, AdapterType type) {
        this.upperPin = upperPin;
        this.lowerPin = lowerPin;
        this.type = type;
    }

    public Pin getUpperPin() {
        return upperPin;
    }

    public Pin getLowerPin() {
        return lowerPin;
    }

    public AdapterType getAdapterType() {
        return type;
    }
}
