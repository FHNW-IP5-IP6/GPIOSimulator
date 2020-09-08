package fhnwgpio.components;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;
import fhnwgpio.components.helper.ComponentLogger;
import fhnwgpio.grove.GroveAdapter;

/**
 * FHNW implementation of the Grove Ultra Sonic Ranger Component:
 * https://wiki.seeedstudio.com/Grove-Ultrasonic_Ranger/
 * Code is based on the python implementation
 */
public class UltraSonicRangerComponent {
    private int triggerPin;
    private int echoPin;

    /**
     * Standard Constructor for the Grove Ultrasonic Ranger that only needs grove hat connection information
     *
     * @param groveAdapter Adapter that contains the connection information
     */
    public UltraSonicRangerComponent(GroveAdapter groveAdapter) {
        this(groveAdapter.getAdapter().getUpperPin(), groveAdapter.getAdapter().getUpperPin());
    }

    /**
     * Constructor for the Ultrasonic Ranger that uses the same pin for the trigger and the echo
     *
     * @param pin pin for trigger and echo
     */
    public UltraSonicRangerComponent(Pin pin) {
        this(pin, pin);
    }

    /**
     * Constructor for using the Grove Ultrasonic Ranger without the Grove Base Hat.
     * This is also compatible with other Ultrasonic Rangers that use different pins for the trigger and the echo
     *
     * @param trigger trigger pin for triggering the Ultrasonic wave
     * @param echo    echo pin for measuring the echo
     */
    public UltraSonicRangerComponent(GpioPinDigitalInput trigger, GpioPinDigitalInput echo) {
        this(trigger.getPin(), echo.getPin());
    }

    /**
     * Constructor for using the Grove Ultrasonic Ranger without the Grove Base Hat.
     * This is also compatible with other Ultrasonic Rangers that use different pins for the trigger and the echo
     *
     * @param trigger trigger pin for triggering the Ultrasonic wave
     * @param echo    echo pin for measuring the echo
     */
    public UltraSonicRangerComponent(Pin trigger, Pin echo) {
        triggerPin = trigger.getAddress();
        echoPin = echo.getAddress();
    }

    /**
     * Measures the distance between the device and an object in cm
     *
     * @return the distance to an object in cm
     * @throws InterruptedException exception when waiting for the echo
     */
    public long measureInCentimeter() throws InterruptedException {
        trigger();
        //distance there and back again has to be divided by 2, to get the one way distance
        //and 29 is the constant to get the distance in cm
        return getPulseDifference() / 29 / 2;
    }

    /**
     * Measures the distance between the device and an object in inches
     *
     * @return distance to object in inches
     * @throws InterruptedException exception when waiting for the echo
     */
    public long measureInInches() throws InterruptedException {
        trigger();
        //distance there and back again has to be divided by 2, to get the one way distance
        //and 74 is the constant to get the distance in inches
        return getPulseDifference() / 74 / 2;
    }

    /**
     * Triggers the Ultra Sonic Ranger to start measuring. It starts to send an ultra sonic wave to the target after this method.
     */
    private void trigger() throws InterruptedException {
        // tag::UltraSonicRangerTrigger[]
        ComponentLogger.logInfo("UltraSonicRangerComponent:  Trigger to measure distance");
        Gpio.pinMode(triggerPin, Gpio.OUTPUT);
        Gpio.digitalWrite(triggerPin, false);
        Thread.sleep(0, 2000);
        Gpio.digitalWrite(triggerPin, true);
        Thread.sleep(0, 10000);
        Gpio.digitalWrite(triggerPin, false);
        // end::UltraSonicRangerTrigger[]
    }

    /**
     * gets the pulse difference between the emission and reception of the ultra sonic wave and clears the zeros for timeouts
     *
     * @return the pulse difference
     */
    private long getPulseDifference() {
        Gpio.pinMode(echoPin, Gpio.INPUT);
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
        // tag::UltraSonicRangerMeasure[]
        int count = 0;
        int TIMEOUT = 1000000; //just a large number to avoid an endless loop when there is a device problem

        //wait for the pulse to start (ultra sonic wave sent)
        while (count < TIMEOUT && Gpio.digitalRead(echoPin) == 0)
            count++;

        if (count >= TIMEOUT) return 0;
        long t1 = microTime();

        count = 0;
        //wait for the pulse to end (ultra sonic wave received)
        while (count < TIMEOUT && Gpio.digitalRead(echoPin) == 1)
            count++;
        if (count >= TIMEOUT) return 0;
        long t2 = microTime();

        return t2 - t1; //return the time difference between emission and reception of the ultra sonic wave
        // end::UltraSonicRangerMeasure[]
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

