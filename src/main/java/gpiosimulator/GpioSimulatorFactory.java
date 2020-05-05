package gpiosimulator;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import gpiosimulator.component.light.LEDBase;
import gpiosimulator.component.light.impl.GpioLEDComponent;
import gpiosimulator.component.light.sim.GpioLEDSimulator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GpioSimulatorFactory {
    private boolean simulator;
    private static Logger logger;

    public GpioSimulatorFactory(boolean simulator) {
        this.simulator = simulator;
        System.setProperty("logFilename", getLogFileName());
        this.logger = LogManager.getLogger(getClass());
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

    private String getLogFileName() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return "gpio_log_" + formatter.format(now) + ".log";
    }
}
