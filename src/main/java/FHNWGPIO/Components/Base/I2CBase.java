package FHNWGPIO.Components.Base;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;

import java.io.IOException;

/**
 * Abstract class for I2C devices
 * Provides methods that all I2C devices might need
 */
public abstract class I2CBase {
    protected I2CDevice I2Cdevice;
    private Console console;

    public I2CBase(I2CDevice device) {
        I2Cdevice = device;
    }

    public I2CBase(int address, int busNumber) throws IOException, I2CFactory.UnsupportedBusNumberException {
        this(address, busNumber, new Console());
    }

    public I2CBase(int address, int busNumber, Console console) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CBus bus = I2CFactory.getInstance(busNumber);
        this.I2Cdevice = bus.getDevice(address);
        this.setConsole(console);
    }

    /**
     * send a single command
     * @param cmd command to send
     */
    protected void writeCmd(byte cmd) {
        try {
            I2Cdevice.write(cmd);
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
    }

    /**
     * Write Block of data
     * @param cmd
     * @param data
     */
    private void writeBlockData(byte cmd, byte[] data) {
        try {
            I2Cdevice.write(cmd, data);
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
    }

    /**
     * Read a single byte
     *
     */
    private byte read() {
        try {
            return (byte) I2Cdevice.read();
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return (byte) 0;
    }

    /**
     * Reads a byte array
     */
    private byte[] readData(byte cmd) {
        byte[] buffer = new byte[cmd];
        try {
            I2Cdevice.read(buffer, 0, cmd);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return buffer;
    }

    /**
     * Reads a block of data
     */
    private byte[] readBlockData(byte cmd) {
        byte[] buffer = new byte[cmd];
        try {
            I2Cdevice.read(buffer, 0, cmd);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return buffer;
    }


    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }
}
