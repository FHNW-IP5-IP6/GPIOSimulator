package FHNWExamples.Component;

import FHNWExamples.Example;
import FHNWGPIO.Components.PotentiometerComponent;
import FHNWGPIO.Grove.GroveAdapter;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.util.Console;

public class PotentiometerDevice extends Example {
    public PotentiometerDevice(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        Console console = new Console();
        console.promptForExit();

        PotentiometerComponent potentiometer = new PotentiometerComponent(GroveAdapter.A0);
        potentiometer.setRange(0, 100);

        while (console.isRunning()) {
            Thread.sleep(1000);
            console.println(potentiometer.getValue());
        }
    }
}
