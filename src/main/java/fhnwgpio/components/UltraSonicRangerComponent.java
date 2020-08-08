package fhnwgpio.components;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.grove.Adapter;
import fhnwgpio.grove.GroveAdapter;

/**
 * FHNW implementation of the Grove Ultra Sonic Ranger Component:
 * https://wiki.seeedstudio.com/Grove-Ultrasonic_Ranger/
 * Code is based on the python implementation
 */
public class UltraSonicRangerComponent {
    private int pin;

    private final int TIMEOUT1 = 1000000; //for pulse start
    private final int TIMEOUT2 = 1000000; //for pulse end


    /**
     * Standard Constructor for the Ultra Sonic Ranger that only needs grove hat connection information
     *
     * @param groveAdapter Adapter that contains the connection information
     */
    public UltraSonicRangerComponent(GroveAdapter groveAdapter) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        pin = groveAdapter.getAdapter().getUpperPin().getAddress();
    }

    /**
     * Measures the distance between the device and an object in cm
     *
     * @return the distance to an object in cm
     * @throws InterruptedException
     */
    public long measureInCentimeter() throws InterruptedException {
        trigger();
        Gpio.pinMode(pin, Gpio.INPUT);
        return getPulseDifference() / 29 / 2;
    }

    /**
     * Measures the distance between the device and an object in inches
     *
     * @return distance to object in inches
     * @throws InterruptedException
     */
    public long measureInInches() throws InterruptedException {
        trigger();
        Gpio.pinMode(pin, Gpio.INPUT);
        return getPulseDifference() / 74 / 2;
    }

    /**
     * Triggers the Ultra Sonic Ranger to start measuring. It starts to send an ultra sonic wave to the target after this method.
     */
    private void trigger() throws InterruptedException {
        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, false);
        Thread.sleep(0, 2000);
        Gpio.digitalWrite(pin, true);
        Thread.sleep(0, 10000);
        Gpio.digitalWrite(pin, false);
    }

    /**
     * gets the pulse difference between the emission and reception of the ultra sonic wave and clears the zeros for timeouts
     *
     * @return the pulse difference
     */
    private long getPulseDifference() {
        long diff = 0;
        while (diff == 0) {
            diff = pulseIn();
        }
        return diff;
    }

    /**
     * returns the pulse time in microseconds (time between sending and receiving the ultra sonic wave)
     */
    private long pulseIn() {
        long t0 = microTime();
        int count = 0;
        //wait for the pulse to start (ultra sonic wave sent)
        while (count < TIMEOUT1 && Gpio.digitalRead(pin) == 0)
            count++;

        if (count >= TIMEOUT1) return 0;
        long t1 = microTime();

        count = 0;
        //wait for the pulse to end (ultra sonic wave received)
        while (count < TIMEOUT2 && Gpio.digitalRead(pin) == 1)
            count++;
        if (count >= TIMEOUT2) return 0;
        long t2 = microTime();

        return t2 - t1; //return the time difference between emission and reception of the ultra sonic wave
    }

    /**
     * Gets the current time in micro seconds
     *
     * @return current time in micro seconds
     */
    private long microTime() {
        return System.nanoTime() / 1000;
    }
}

