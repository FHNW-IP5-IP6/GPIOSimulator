package fhnwgpio.components;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.grove.Adapter;
import fhnwgpio.grove.GroveAdapter;

/**
 * FHNW implementation of the Grove Strip Driver.
 */
public class LedStripDriverComponent {
    private Adapter adapter;
    private int dataPin, clkPin;

    /**
     * Standard constructor of the LED Strip Driver that needs the Grove Adapter
     * @param groveAdapter Contains Information where the device is connected
     */
    public LedStripDriverComponent(GroveAdapter groveAdapter) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        this.adapter = groveAdapter.getAdapter();
        dataPin =   adapter.getLowerPin().getAddress();
        clkPin =    adapter.getUpperPin().getAddress();

        Gpio.pinMode(dataPin, Gpio.OUTPUT);
        Gpio.pinMode(clkPin, Gpio.OUTPUT);
    }

    /**
     * Before setting the Color of the LED Strip this command has to run
     */
    public void begin() {
        send32Zeors();
    }

    /**
     * After setting the color of the LED Strip this command has to run
     */
    public void end(){
        send32Zeors();
    }

    /**
     * Sets the color of the LED Strip on the LED Strip Driver
     * @param red value between 0-255
     * @param green value between 0-255
     * @param blue value between 0-255
     */
    public void setColor(int red, int green, int blue) {
        if (red < 0 || red > 255|| green < 0 || green > 255 || blue < 0 || blue > 255)
            throw new IllegalArgumentException("RGB values must be in the range 0-255");

        int dx = 0;

        dx |= 0x03 << 30;             // highest two bits 1，flag bits
        dx |= takeAntiCode(blue) << 28;
        dx |= takeAntiCode(green) << 26;
        dx |= takeAntiCode(red) << 24;

        dx |= blue << 16;
        dx |= green << 8;
        dx |= red;

        datSend(dx);
    }

    private void clkRise() throws InterruptedException {
        Gpio.digitalWrite(clkPin, false);
        Thread.sleep(20);
        Gpio.digitalWrite(clkPin, true);
        Thread.sleep(20);
    }

    private void send32Zeors() {
        for (int i = 0; i < 32; i++) {
            Gpio.digitalWrite(dataPin, false);
        }
    }

    private int takeAntiCode(int dat) {
        int tmp = 0;

        if ((dat & 0x80) == 0)
            tmp |= 0x02;
        if ((dat & 0x40) == 0)
            tmp |= 0x01;

        return tmp;
    }

    private void datSend(int dx) {
        for (int i = 0; i < 32; i++) {

            if ((dx & 0x80000000) != 0) {
                Gpio.digitalWrite(dataPin, true);
            } else {
                Gpio.digitalWrite(dataPin, false);
            }
        }
    }
}
