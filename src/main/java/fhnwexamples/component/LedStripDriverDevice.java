package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.LedStripDriverComponent;
import fhnwgpio.grove.GroveAdapter;

/**
 * Example for LedStripDriverComponent usage. This example changes the led strips colour from red to green to blue
 * in a while loop. Every colour stays for five second. This example only works with the Grove LED Strip Driver.
 * Driver: https://www.seeedstudio.com/Grove-LED-Strip-Driver.html
 */
public class LedStripDriverDevice extends Example {
    public LedStripDriverDevice(int key, String title) {
        super(key, title);
    }

    // tag::LEDStripDriverDevice[]
    @Override public void execute() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));

        Console console = new Console();
        console.promptForExit();

        LedStripDriverComponent ledStrip = new LedStripDriverComponent(GroveAdapter.D16);

        try {
            while (console.isRunning()) {
                ledStrip.start();
                ledStrip.setColor(255, 0, 0);
                console.println("red");
                ledStrip.stop();
                Thread.sleep(5000);
                ledStrip.start();
                ledStrip.setColor(0, 255, 0);
                console.println("green");
                ledStrip.stop();
                Thread.sleep(5000);
                ledStrip.start();
                ledStrip.setColor(0, 0, 255);
                console.println("blue");
                ledStrip.stop();
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            console.println(e.getMessage());
        }
        // end::LEDStripDriverDevice[]
    }
}
