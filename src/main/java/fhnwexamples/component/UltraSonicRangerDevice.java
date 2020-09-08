package fhnwexamples.component;

import com.pi4j.io.gpio.*;
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
            GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
            final GpioController gpio = GpioFactory.getInstance();

            //Ultrasonic Ranger on the CrowPi
            GpioPinDigitalInput echo = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_12);
            GpioPinDigitalInput trigger = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_16);

            UltraSonicRangerComponent ultraSonicRangerComponent = new UltraSonicRangerComponent(trigger, echo);

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
