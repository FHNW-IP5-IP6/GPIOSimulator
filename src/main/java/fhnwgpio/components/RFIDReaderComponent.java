package fhnwgpio.components;

import com.pi4j.io.serial.*;
import fhnwgpio.components.helper.ComponentLogger;

/**
 * FHNW implementation for the grove serial 125KHz RFID Reader.
 * Grove 125KHz RFID Reader: https://wiki.seeedstudio.com/Grove-125KHz_RFID_Reader/
 */
public class RFIDReaderComponent {
    private Serial serial;

    /**
     * Constructor of the RFID Reader
     */
    public RFIDReaderComponent() {
        serial = SerialFactory.createInstance();
        init();
    }

    /**
     * Adds Listeners when the for when the device receives data
     *
     * @param listener listeners to add
     */
    public void addListener(SerialDataEventListener... listener) {
        serial.addListener(listener);
        ComponentLogger.logInfo("RFIDReaderComponent: Event Listener added for receiving data");
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
            ComponentLogger.logInfo("RFIDReaderComponent:  Initialised with " + config.toString());
            serial.open(config);
        } catch (Exception ex) {
            ComponentLogger.logError("RFIDReaderComponent: serial setup failed : " + ex.getMessage());
        }
    }
}
