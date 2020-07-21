package fhnwgpio.components.base;

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
    private I2CDevice i2CDevice;
    private Console console;

    public I2CBase(I2CDevice device) {
        i2CDevice = device;
    }

    public I2CBase(int address, int busNumber) throws IOException, I2CFactory.UnsupportedBusNumberException {
        this(address, busNumber, new Console());
    }

    public I2CBase(int address, int busNumber, Console console)
            throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CBus bus = I2CFactory.getInstance(busNumber);
        this.i2CDevice = bus.getDevice(address);
        this.setConsole(console);
    }

    /**
     * send a single command
     *
     * @param cmd command to send
     */
    protected void writeCmd(byte cmd) {
        try {
            i2CDevice.write(cmd);
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
    }

    /**
     * Write Block of data
     *
     * @param cmd
     * @param data
     */
    public void writeBlockData(byte cmd, byte[] data) {
        try {
            i2CDevice.write(cmd, data);
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
    }

    /**
     * Read a single byte
     */
    public byte read() {
        try {
            return (byte) i2CDevice.read();
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return (byte) 0;
    }

    /**
     * Read a single byte
     *
     * @param dataAddress
     * @return
     */
    public byte read(byte dataAddress) {
        try {
            return (byte) i2CDevice.read(dataAddress);
        } catch (Exception ex) {
            getConsole().println();
        }
        return (byte) 0;
    }

    /**
     * Reads a byte array
     */
    public byte[] readData(byte size) {
        byte[] bytes = new byte[size];
        try {
            i2CDevice.read(bytes, 0, size);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return bytes;
    }

    /**
     * Reads a byte array
     *
     * @param dataAddress
     * @param size
     * @return
     */
    public byte[] readData(byte dataAddress, byte size) {
        byte[] bytes = new byte[size];
        try {
            i2CDevice.read(dataAddress, bytes, 0, size);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return bytes;
    }

    /**
     * Reads a block of data
     */
    public byte[] readBlockData(byte size) {
        byte[] bytes = new byte[size];
        try {
            i2CDevice.read(bytes, 0, size);
        } catch (Exception ex) {
            getConsole().println(ex.getMessage());
        }
        return bytes;
    }

    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }
}
