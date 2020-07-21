package fhnwexamples.component;

import fhnwexamples.Example;
import fhnwgpio.components.PotentiometerComponent;
import fhnwgpio.grove.GroveAdapter;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;

/**
 * Example for the grove potentiometer device. Sets the desired value range returned by the potentiometer from
 * 0 to 100 and displays the current value every 500 ms.
 */
public class PotentiometerDevice extends Example {
    public PotentiometerDevice(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        Console console = new Console();
        console.promptForExit();

        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        I2CDevice device = bus.getDevice(4);

        PotentiometerComponent potentiometer = new PotentiometerComponent(GroveAdapter.A0);
        potentiometer.setRange(0, 100);

        while (console.isRunning()) {
            Thread.sleep(500);
            console.println(potentiometer.getValue());
        }
    }
}
