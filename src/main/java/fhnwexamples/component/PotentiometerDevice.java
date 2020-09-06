package fhnwexamples.component;

import fhnwexamples.Example;
import fhnwgpio.components.PotentiometerComponent;
import fhnwgpio.grove.GroveAdapter;
import com.pi4j.util.Console;

/**
 * Example for the grove potentiometer device. Sets the desired value range returned by the potentiometer from
 * 0 to 100 and displays the current value every 500 ms.
 */
public class PotentiometerDevice extends Example {
    public PotentiometerDevice(int key, String title) {
        super(key, title);
    }

    // tag::PotentiometerDevice[]
    @Override public void execute() throws Exception {
        Console console = new Console();
        console.promptForExit();

        PotentiometerComponent potentiometer = new PotentiometerComponent(GroveAdapter.A0);
        potentiometer.setRange(0, 100);

        while (console.isRunning()) {
            Thread.sleep(500);
            console.println(potentiometer.getValue());
        }
    }
    // end::PotentiometerDevice[]
}
