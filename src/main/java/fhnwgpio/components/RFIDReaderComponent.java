package fhnwgpio.components;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.IOException;

/**
 * FHNW implementation for the grove serial 125KHz RFID Reader.
 * Grove 125KHz RFID Reader: https://wiki.seeedstudio.com/Grove-125KHz_RFID_Reader/
 */
public class RFIDReaderComponent {
    private Console console;
    private Serial serial = null;

    public RFIDReaderComponent(Console console) throws IOException, InterruptedException {

        this.console = console;
        serial = SerialFactory.createInstance();
        init();

    }

    public void addListener(SerialDataEventListener... listener) {
        serial.addListener(listener);
        this.console.println("Event Listener " + listener.toString() + " added");
    }

    private void init() throws IOException, InterruptedException {
        try {
            SerialConfig config = new SerialConfig();
            config.device(SerialPort.getDefaultPort())
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .stopBits(StopBits._1);
            // display connection details
            console.box(" Connecting to: " + config.toString(),
                    " Testing the RFID Reader");
            serial.open(config);
        } catch (Exception ex) {
            this.console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
        }
    }
}
