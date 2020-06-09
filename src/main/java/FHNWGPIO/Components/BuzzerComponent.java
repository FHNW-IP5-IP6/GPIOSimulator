package FHNWGPIO.Components;

import FHNWGPIO.Grove.Adapter;
import FHNWGPIO.Grove.GroveAdapter;
import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;

/**
 * FHNW implementation for the grove buzzer.
 * https://www.seeedstudio.com/Grove-Buzzer.html
 */
public class BuzzerComponent {
    private Adapter adapter;
    private int piFrequency = 19200000;
    private int dutyCycle = 512;

    /**
     * Constructor of the BuzzerComponent.
     *
     * @param groveAdapter
     */
    public BuzzerComponent(GroveAdapter groveAdapter) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        this.adapter = groveAdapter.getAdapter();
        Gpio.pinMode(adapter.getUpperPin().getAddress(), Gpio.PWM_OUTPUT);
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
    }

    /**
     * Plays a tone at a frequency for a specified duration in milliseconds.
     *
     * @param frequency in hz
     * @param duration  in ms
     * @throws InterruptedException
     */
    public void playTone(int frequency, int duration) throws InterruptedException {
        int clock = calculateClock(frequency);
        Gpio.pwmSetClock(clock);
        Gpio.pwmSetRange(dutyCycle);
        Gpio.pwmWrite(adapter.getUpperPin().getAddress(), clock);
        Thread.sleep(duration);
        stop();
    }

    /**
     * Plays a tone with a specific frequency.
     *
     * @param frequency in hz
     * @throws InterruptedException
     */
    public void playTone(int frequency) throws InterruptedException {
        playTone(frequency, -1);
    }

    /**
     * Pauses for e specified amount of milliseconds.
     *
     * @param duration
     * @throws InterruptedException
     */
    public void stop(int duration) throws InterruptedException {
        Gpio.pwmWrite(adapter.getUpperPin().getAddress(), 0);
        Thread.sleep(duration);
    }

    /**
     * Pauses forever.
     *
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        stop(0);
    }

    /**
     * Calculates the PWM clock divider.
     *
     * @param frequency
     * @return
     */
    private int calculateClock(int frequency) {
        double divider = (double) piFrequency / frequency;
        return (int) divider / dutyCycle;
    }
}
