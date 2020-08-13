package fhnwexamples.component;

import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.UltraSonicRangerComponent;
import fhnwgpio.grove.GroveAdapter;

public class UltraSonicRangerDevice extends Example {
    public UltraSonicRangerDevice(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute() {
        Console console = new Console();
        console.promptForExit();

        try {
            // tag::UltraSonicRangerDevice[]
            UltraSonicRangerComponent ultraSonicRangerComponent = new UltraSonicRangerComponent(GroveAdapter.D5);

            //Shows the measured distance every second
            while (true) {
                long distance = ultraSonicRangerComponent.measureInCentimeter();
                console.println(distance);
                Thread.sleep(1000);
            }
            // end::UltraSonicRangerDevice[]
        } catch (Exception e) {
            console.println(e.getMessage());
        }


    }
}
