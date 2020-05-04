package gpiosimulator.component.light.sim;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import gpiosimulator.GpioSimulatorFactory;
import gpiosimulator.component.light.LEDBase;
import gpiosimulator.component.light.LightStateChangeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GpioLEDSimulator extends LEDBase {

    // internal class members
    GpioPinDigitalOutput pin = null;
    PinState onState = PinState.HIGH;
    PinState offState = PinState.LOW;

    private Logger logger = GpioSimulatorFactory.getLogger();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    boolean pinState = false;

    // create a GPIO PIN listener for change changes; use this to send LED light state change events
    private GpioPinListenerDigital listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            // notify any state change listeners
            if (event.getState() == onState) {
                notifyListeners(new LightStateChangeEvent(GpioLEDSimulator.this, true));
            }
            if (event.getState() == offState) {
                notifyListeners(new LightStateChangeEvent(GpioLEDSimulator.this, false));
            }
        }
    };

    /**
     * using this constructor requires that the consumer
     * define the LIGHT ON and LIGHT OFF pin states
     *
     * @param pin      GPIO digital output pin
     * @param onState  pin state to set when power is ON
     * @param offState pin state to set when power is OFF
     */
    public GpioLEDSimulator(GpioPinDigitalOutput pin, PinState onState, PinState offState) {
        this(pin);
        this.onState = onState;
        this.offState = offState;
    }

    /**
     * default constructor; using this constructor assumes that:
     * (1) a pin state of HIGH is LIGHT ON
     * (2) a pin state of LOW  is LIGHT OFF
     *
     * @param pin GPIO digital output pin
     */
    public GpioLEDSimulator(GpioPinDigitalOutput pin) {
        this.pin = pin;
        this.pin.addListener(listener);
    }

    @Override
    public void on() {
        pinState = true;
        logger.info("LED is on");
    }

    @Override
    public void off() {
        pinState = false;
        logger.info("LED is off");
    }

    @Override
    public boolean isOn() {
        return pinState;
    }

    @Override
    public Future<?> blink(long delay) {
        return executor.submit(() -> {
            while (true) {
                if (isOn())
                    off();
                else
                    on();
                Thread.sleep(delay);
            }
        });
    }

    @Override
    public Future<?> blink(long delay, long duration) {
        return null;
    }

    @Override
    public Future<?> pulse(long duration) {
        return null;
    }

    @Override
    public Future<?> pulse(long duration, boolean blocking) {
        return null;
    }
}
