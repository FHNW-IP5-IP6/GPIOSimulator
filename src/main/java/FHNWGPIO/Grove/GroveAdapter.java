package FHNWGPIO.Grove;

import com.pi4j.io.gpio.RaspiBcmPin;

// https://wiki.seeedstudio.com/Grove_Base_Hat_for_Raspberry_Pi/
// Board only supports 3.3V Grove Devices
public enum GroveAdapter {
    /*A0 {
        @Override public Adapter getAdapter() {
            return new Adapter();
        }
    }, A2 {
        @Override public Adapter getAdapter() {
            return new Adapter();
        }
    }, A4 {
        @Override public Adapter getAdapter() {
            return new Adapter();
        }
    }, A6 {
        @Override public Adapter getAdapter() {
            return new Adapter();
        }
    },*/ D5 {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_05, RaspiBcmPin.GPIO_06, AdapterType.DIGITAL);
        }
    }, D16 {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_16, RaspiBcmPin.GPIO_17, AdapterType.DIGITAL);
        }
    }, D18 {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_18, RaspiBcmPin.GPIO_19, AdapterType.DIGITAL);
        }
    }, D22 {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_22, RaspiBcmPin.GPIO_23, AdapterType.DIGITAL);
        }
    }, D24 {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_24, RaspiBcmPin.GPIO_25, AdapterType.DIGITAL);
        }
    }, D26 {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_26, RaspiBcmPin.GPIO_27, AdapterType.DIGITAL);
        }
    }, I2C {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_02, RaspiBcmPin.GPIO_03, AdapterType.I2C);
        }
    }, PWM {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_12, RaspiBcmPin.GPIO_13, AdapterType.PWM);
        }
    }, UART {
        @Override public Adapter getAdapter() {
            return new Adapter(RaspiBcmPin.GPIO_14, RaspiBcmPin.GPIO_15, AdapterType.UART);
        }
    };

    public abstract Adapter getAdapter();
}
