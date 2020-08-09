package fhnwgpio.components;

import fhnwgpio.components.helper.ComponentLogger;
import fhnwgpio.components.helper.PwmHelper;
import fhnwgpio.grove.AdapterType;
import fhnwgpio.grove.GroveAdapter;
import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;

/**
 * FHNW implementation for the grove buzzer.
 * https://www.seeedstudio.com/Grove-Buzzer.html
 */
public class BuzzerComponent {
    private Pin pin;
    private int piFrequency = 19200000;
    private int dutyCycle = 512;

    /**
     * Private master constructor. All public constructors call this constructor.
     *
     * @param pin GPIO PWM pin
     */
    private BuzzerComponent(Pin pin) {
        this.pin = pin;
        Gpio.pinMode(this.pin.getAddress(), Gpio.PWM_OUTPUT);
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange(dutyCycle);
        ComponentLogger.logInfo("BuzzerComponent: Buzzer created for GPIO pin " + pin.getAddress());
    }

    /**
     * Constructor for GPIO buzzer usage
     *
     * @param pin Pi4J GPIO PWM output pin
     * @throws IllegalArgumentException Thrown if the provided pin is a non hardware pwm pin
     */
    public BuzzerComponent(GpioPinPwmOutput pin) throws IllegalArgumentException {
        this(pin.getPin());

        if (!PwmHelper.isHardwarePwmPin(pin.getPin())) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "BuzzerComponent: Please provide a valid pwm pin");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Constructor for Grove buzzer usage
     *
     * @param groveAdapter The PWM Grove adapter
     * @throws IllegalArgumentException Thrown if the provided grove adapter is not the PWM adapter
     */
    public BuzzerComponent(GroveAdapter groveAdapter) throws IllegalArgumentException {
        this(groveAdapter.getAdapter().getUpperPin());

        if (groveAdapter.getAdapter().getAdapterType() != AdapterType.PWM) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "BuzzerComponent: Please provide a grove pwm pin");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Plays a tone at a frequency for a specified duration in milliseconds.
     *
     * @param frequency in hz
     * @param duration  in ms
     * @throws InterruptedException Might be thrown because of Thread.sleep() usage
     */
    // tag::BuzzerComponentPlayTone[]
    public void playTone(int frequency, int duration) throws InterruptedException {
        ComponentLogger
                .logInfo("BuzzerComponent: Play tone with " + frequency + "Hz for " + duration + " milliseconds");

        if (frequency != 0) {
            int clock = calculateClock(frequency);
            Gpio.pwmSetClock(clock);
            Gpio.pwmWrite(pin.getAddress(), clock);
            Thread.sleep(duration);
        } else {
            stop(duration);
        }
    }
    // end::BuzzerComponentPlayTone[]

    /**
     * Plays a tone with a specific frequency.
     *
     * @param frequency in hz
     * @throws InterruptedException Might be thrown because of Thread.sleep() usage
     */
    public void playTone(int frequency) throws InterruptedException {
        playTone(frequency, 0);
    }

    /**
     * Pauses for a specified amount of milliseconds.
     *
     * @param duration Desired pause length in ms
     * @throws InterruptedException Might be thrown because of Thread.sleep() usage
     */
    public void stop(int duration) throws InterruptedException {
        ComponentLogger.logInfo("BuzzerComponent: Pause for " + duration + " milliseconds");
        Gpio.pwmWrite(pin.getAddress(), 0);
        Thread.sleep(duration);
    }

    /**
     * Pauses forever.
     *
     * @throws InterruptedException Might be thrown because of Thread.sleep() usage
     */
    public void stop() throws InterruptedException {
        stop(0);
    }

    /**
     * Calculates the PWM clock divider.
     *
     * @param frequency Desired frequency
     * @return The calculated clock divider
     */
    // tag::BuzzerComponentCalculateClock[]
    private int calculateClock(int frequency) {
        double divider = (double) piFrequency / frequency;
        return (int) divider / dutyCycle;
    }
    // end::BuzzerComponentCalculateClock[]
}
