package fhnwexamples.gpio;

import fhnwexamples.Example;

/**
 * Pi4j LCD1602 example with the PCF8574 I2C extender.
 * Documentation of the I2C extender:
 * http://www.ti.com/lit/ds/symlink/pcf8574.pdf?ts=1588064881394
 */
public class LcdSystemTimeI2C extends Example {

    public LcdSystemTimeI2C(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute() {
        try {
            //TODO: Write I2C Example without component class
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}

