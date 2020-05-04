package gpiosimulator;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import gpiosimulator.component.light.LEDBase;
import gpiosimulator.component.light.impl.GpioLEDComponent;
import gpiosimulator.component.light.sim.GpioLEDSimulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GpioSimulatorFactory {
    private boolean simulator;
    private static Logger logger;

    public GpioSimulatorFactory(boolean simulator, boolean logger) {
        this.simulator = simulator;
        GpioSimulatorFactory.logger = logger? LogManager.getLogger(getClass()): null;
    }

    public boolean isSimulator() {
        return simulator;
    }

    public static Logger getLogger(){
        return logger;
    }

    public LEDBase getLED(GpioPinDigitalOutput pin) {
        LEDBase led = simulator ? new GpioLEDSimulator(pin) : new GpioLEDComponent(pin);
        return led;
    }
}