package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.LedStripDriverComponent;
import fhnwgpio.grove.GroveAdapter;

public class LedStripDriverDevice extends Example {
    public LedStripDriverDevice(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));

        Console console = new Console();
        console.promptForExit();

        try {

            // tag::LEDStripDriver[]
            //Single LED-Strip Example:
            //This is a direct translation of the library written for Arduino, but we were not yet able to test it,
            // because we do not have 12 volt charger to run the LED Strip Driver
            LedStripDriverComponent ledStrip = new LedStripDriverComponent(GroveAdapter.D16);

            ledStrip.begin();
            ledStrip.setColor(255, 0, 0);
            ledStrip.end();
            Thread.sleep(1000);

            ledStrip.begin();
            ledStrip.setColor(0, 255, 0);
            ledStrip.end();
            Thread.sleep(1000);

            ledStrip.begin();
            ledStrip.setColor(0, 0, 255);
            ledStrip.end();
            Thread.sleep(1000);
            // end::LEDStripDriver[]

        }catch (Exception e){
            console.println(e.getMessage());
        }
    }
}
