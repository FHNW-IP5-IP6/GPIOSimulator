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
    private Serial serial;

    /**
     * Constructor of the RFID Reader
     * @param console Pi4J Console object for loggin
     */
    public RFIDReaderComponent(Console console){
        this.console = console;
        serial = SerialFactory.createInstance();
        init();
    }

    /**
     * Adds Listeners when the for when the device receives data
     * @param listener listeners to add
     */
    public void addListener(SerialDataEventListener... listener) {
        serial.addListener(listener);
        this.console.println("Event Listener for added for received data");
    }

    /**
     * Initialises the serial port of the device
     */
    private void init() {
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
            this.console.println(" ==> SERIAL SETUP FAILED : " + ex.getMessage());
        }
    }
}
