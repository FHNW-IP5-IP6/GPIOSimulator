package fhnwexamples.component;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.serial.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;
import fhnwgpio.components.RFIDReaderComponent;
import java.io.IOException;

public class RFIDReaderDevice extends Example {

    public RFIDReaderDevice(int key, String title) {
        super(key, title);
    }

    @Override
    public void execute(){
        GpioFactory.setDefaultProvider(
                new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on gpio Extension Board
        Console console =  new Console();
        console.promptForExit();

        // tag::RFIDReaderDevice[]
        RFIDReaderComponent rfidReaderComponent = new RFIDReaderComponent();
        // create and register the serial data listener
        rfidReaderComponent.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                // print out the data received to the console
                try {
                    console.println("[HEX DATA]   " + event.getHexByteString());
                    console.println("[ASCII DATA] " + event.getAsciiString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // end::RFIDReaderDevice[]
    }
}
