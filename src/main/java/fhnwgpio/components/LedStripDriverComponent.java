package fhnwgpio.components;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.grove.Adapter;
import fhnwgpio.grove.GroveAdapter;

/**
 * FHNW implementation of the Grove Strip Driver. This library represents the Java equivalent of the official Seed
 * Studio LED Strip Suli Library for Arduino with added support for Grove Base Hat usage.
 * Library: https://github.com/Seeed-Studio/LED_Strip_Suli
 * Driver: https://www.seeedstudio.com/Grove-LED-Strip-Driver.html
 */
public class LedStripDriverComponent {
    GpioPinDigitalOutput data;
    GpioPinDigitalOutput clock;

    /**
     * * Standard constructor of the LED Strip Driver connected to the Pi's GPIO pins
     *
     * @param data  Data pin
     * @param clock Clock pin
     */
    public LedStripDriverComponent(GpioPinDigitalOutput data, GpioPinDigitalOutput clock) {
        this.data = data;
        this.clock = clock;
    }

    /**
     * Standard constructor of the LED Strip Driver connected to the Grove Base Hat
     *
     * @param groveAdapter Contains Information where the device is connected
     */
    public LedStripDriverComponent(GroveAdapter groveAdapter) {
        this(GpioFactory.getInstance().provisionDigitalOutputPin(groveAdapter.getAdapter().getLowerPin()),
                GpioFactory.getInstance().provisionDigitalOutputPin(groveAdapter.getAdapter().getUpperPin()));
    }

    /**
     * Before setting the Color of the LED Strip this command has to run
     */
    public void start() throws InterruptedException {
        send32Zeros();
    }

    /**
     * After setting the color of the LED Strip this command has to run
     */
    public void stop() throws InterruptedException {
        send32Zeros();
    }

    /**
     * Sets the color of the LED Strip on the LED Strip Driver
     *
     * @param red   value between 0-255
     * @param green value between 0-255
     * @param blue  value between 0-255
     * @throws IllegalArgumentException Thrown if a invalid value for either red green or blue is provided
     * @throws InterruptedException     Might be thrown because of Thread.sleep() usage
     */
    public void setColor(int red, int green, int blue) throws IllegalArgumentException, InterruptedException {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new IllegalArgumentException("RGB values must be in the range 0-255");
        }

        int dx = 0;

        dx |= 0x03 << 30;                   // highest two bits 1，flag bits
        dx |= takeAntiCode(blue) << 28;
        dx |= takeAntiCode(green) << 26;
        dx |= takeAntiCode(red) << 24;

        dx |= blue << 16;
        dx |= green << 8;
        dx |= red;

        dataSend(dx);
    }

    private void clockRise() throws InterruptedException {
        clock.low();
        Thread.sleep(0, 20000);
        clock.high();
        Thread.sleep(0, 20000);
    }

    private void send32Zeros() throws InterruptedException {
        for (int i = 0; i < 32; i++) {
            data.low();
            clockRise();
        }
    }

    private int takeAntiCode(int dat) {
        int tmp = 0;

        if ((dat & 0x80) == 0) {
            tmp |= 0x02;
        }

        if ((dat & 0x40) == 0) {
            tmp |= 0x01;
        }

        return tmp;
    }

    private void dataSend(int dx) throws InterruptedException {
        for (int i = 0; i < 32; i++) {
            if ((dx & 0x80000000) != 0) {
                data.high();
            } else {
                data.low();
            }
            dx <<= 1;
            clockRise();
        }
    }
}
