package FHNWGPIO.Components;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

/**
 * I2C LCD Component for easier usage
 * Inspired by https://github.com/Poduzov/PI4J-I2C-LCD
 */
public class I2CLCD {

    private final int ROWS = 2;
    private final int COLUMNS = 16;
    private I2CDevice I2Cdevice;

    public I2CLCD(I2CDevice device) {
        I2Cdevice = device;
    }

    public I2CLCD(int address, int busNumber) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CBus bus = I2CFactory.getInstance(busNumber);
        I2Cdevice = bus.getDevice(address);
    }

    /**
     * sends initialisation commands to display
     */
    public void init() {
        try {
            lcdWrite((byte) 0x03);
            lcdWrite((byte) 0x03);
            lcdWrite((byte) 0x03);
            lcdWrite((byte) 0x02);

            lcdWrite((byte) (LCD_FUNCTIONSET | LCD_2LINE | LCD_5x8DOTS | LCD_4BITMODE));
            lcdWrite((byte) (LCD_DISPLAYCONTROL | LCD_DISPLAYON));
            lcdWrite((byte) (LCD_CLEARDISPLAY));
            lcdWrite((byte) (LCD_ENTRYMODESET | LCD_ENTRYLEFT));
            Thread.sleep(0, 200000);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * displays a text on the first line of the display.
     * If it's larger than 16 characters then it will jump over to the second line.
     * The excess characters (>32) are not shown on the display
     *
     * @param text text to diplay on lcd
     */
    public void displayText(String text) {
        displayText(text, 1, 0, true);
    }

    /**
     * writes string to the lcd display on a specific line.
     * Excess characters (>16) are not shown on the line
     *
     * @param text to be displayed
     * @param line on the display for the text to appear
     */
    public void displayText(String text, int line) {
        displayText(text, line, 0, false);
    }

    /**
     * displays the string with an additional position. There are 16 fields on one LCD1602 line.
     * The position gives the number of fields that should be empty before the text.
     * Excess characters (>16-position) on the line will not be shown.
     *
     * @param text           text to show on the lcd text.
     * @param line           line on the lcd display
     * @param pos            is the position on the line.
     * @param jumpToNextLine jumps to the second line if the first line is
     */
    public void displayText(String text, int line, int pos, boolean jumpToNextLine) {
        if (text.length() > 32 - pos)
            text = text.substring(0, 31 - pos);

        String firstLine = text, secondLine = text;

        byte posNew = 0;

        if (line != 2) {
            posNew = (byte) pos;
            if (text.length() > COLUMNS - pos) {
                firstLine = text.substring(0, 15 - pos);
                secondLine = text.substring(COLUMNS - pos, text.length() - 1);
            }
            displayLine(firstLine, posNew);
            if (jumpToNextLine) displayLine(secondLine, posNew + 0x40);
        } else {
            posNew = (byte) (0x40 + pos);
            displayLine(secondLine, posNew);
        }
    }

    /**
     * shows text on display scrolls it with a delay
     * @param text to display
     * @param line on which the text should be visible
     * @param delay for every position jump
     * @throws InterruptedException
     */
    public void displayScrollText(String text, int line, int delay, boolean jumpToNextLine, boolean startAgain) throws InterruptedException {
        String paddedtext = getEmptyLine() + text;

        for (int i = 0; i < paddedtext.length(); i++) {
            displayText(paddedtext.substring(i), line, 0, jumpToNextLine);
            Thread.sleep(delay);
            if (jumpToNextLine)
                clearText();
            else
                clearLine(line);

            if (i == paddedtext.length() - 1 && startAgain)
                displayScrollText(text, line, delay, startAgain, jumpToNextLine);
        }
    }

    /**
     * displays the text and scrolls to the sides, but bounces back.
     * This only works for short texts, otherwise it would be unreadable
     * @param text to display
     * @param line for the text
     * @param delay for every position jump
     * @param jumpToNextLine jumps to the second line before bouncing back
     * @param startAgain decides wether it's done only once or again and again
     * @throws InterruptedException
     */
    public void displayBounceText(String text, int line, int delay, boolean jumpToNextLine, boolean startAgain) throws InterruptedException {
        if (text.length() > COLUMNS - 1) {
            displayText(text, line);
        }

        for (int i = 0; i < COLUMNS - text.length(); i++) {
            if (i + text.length() <= COLUMNS) {
                displayText(text, line, i, jumpToNextLine);
                Thread.sleep(delay);
                if (jumpToNextLine)
                    clearText();
                else
                    clearLine(line);
            }
        }
        for (int i = COLUMNS - text.length(); i > 0; i--) {
            displayText(text, line, i, jumpToNextLine);
            Thread.sleep(delay);
            if (jumpToNextLine)
                clearText();
            else
                clearLine(line);
        }
        if (startAgain) displayBounceText(text, line, delay, jumpToNextLine, startAgain);
    }

    /**
     * define backlight on / off(lcd.backlight(1) off = lcd.backlight(0)
     *
     * @param state sets the backlight (1 == on, 0 == off)
     */
    public void setBacklightState(boolean state) {
        if (state) {
            writeCmd(LCD_BACKLIGHT);
        } else {
            writeCmd(LCD_NOBACKLIGHT);
        }
    }

    /**
     * clears the lcd text
     */
    public void clearText() {
        lcdWrite((byte) LCD_CLEARDISPLAY);
        lcdWrite((byte) LCD_RETURNHOME);
    }

    /**
     * displays a line on a specific position
     * @param text to display
     * @param pos for the start of the text
     */
    private void displayLine(String text, int pos) {
        lcdWrite((byte) (0x80 + pos));

        for (int i = 0; i < text.length(); i++) {
            writeChar((byte) text.charAt(i), Rs);
        }
    }

    /**
     * write a character to lcd
     */
    public void writeChar(byte charvalue) {
        byte mode = 1;
        lcdWriteFourBits((byte) (mode | (charvalue & 0xF0)));
        lcdWriteFourBits((byte) (mode | ((charvalue << 4) & 0xF0)));
    }

    //Gets an empty line
    private String getEmptyLine() {
        String text = "";
        return String.format("%16s", text);
    }

    //Clears a line
    private void clearLine(int line) {
        displayText(getEmptyLine(), line);
    }

    /**
     * send a single command
     *
     * @param cmd command to send
     */
    private void writeCmd(byte cmd) {
        try {
            I2Cdevice.write(cmd);
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Write Block of data
     *
     * @param cmd
     * @param data
     */
    private void writeBlockData(byte cmd, byte[] data) {
        try {
            I2Cdevice.write(cmd, data);
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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
            System.out.println(ex.getMessage());
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
            System.out.println(ex.getMessage());
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
            System.out.println(ex.getMessage());
        }
        return buffer;
    }

    /**
     * clocks EN to latch command
     */
    private void lcdStrobe(byte data) {
        try {
            I2Cdevice.write((byte) (data | En | LCD_BACKLIGHT));
            Thread.sleep(0, 500000);
            I2Cdevice.write((byte) ((data & ~En) | LCD_BACKLIGHT));
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * writes four bits
     */
    private void lcdWriteFourBits(byte data) {
        try {
            I2Cdevice.write((byte) (data | LCD_BACKLIGHT));
            lcdStrobe(data);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * write characters
     */
    private void writeChar(byte cmd, byte mode) {
        lcdWriteFourBits((byte) (mode | (cmd & 0xF0)));
        lcdWriteFourBits((byte) (mode | ((cmd << 4) & 0xF0)));
    }

    /**
     * write a command to lcd
     */
    private void lcdWrite(byte cmd) {
        writeChar(cmd, (byte) 0);
    }

    /**
     * add custom characters(0 - 7)
     */
    private void loadCustomChars(byte[][] fontdata) {
        lcdWrite((byte) 0x40);
        for (int i = 0; i < fontdata.length; i++) {
            for (int j = 0; j < fontdata[i].length; j++) {
                writeChar(fontdata[i][j]);
            }
        }
    }

    /**
     * Set of commands of LCD1602 based on page 6 of the datasheet:
     * https://www.openhacks.com/uploadsproductos/eone-1602a1.pdf
     */
    private final byte LCD_CLEARDISPLAY = (byte) 0x01;
    private final byte LCD_RETURNHOME = (byte) 0x02;
    private final byte LCD_ENTRYMODESET = (byte) 0x04;
    private final byte LCD_DISPLAYCONTROL = (byte) 0x08;
    private final byte LCD_CURSORSHIFT = (byte) 0x10;
    private final byte LCD_FUNCTIONSET = (byte) 0x20;
    private final byte LCD_SETCGRAMADDR = (byte) 0x40;
    private final byte LCD_SETDDRAMADDR = (byte) 0x80;

    // flags for display entry mode
    private final byte LCD_ENTRYRIGHT = (byte) 0x00;
    private final byte LCD_ENTRYLEFT = (byte) 0x02;
    private final byte LCD_ENTRYSHIFTINCREMENT = (byte) 0x01;
    private final byte LCD_ENTRYSHIFTDECREMENT = (byte) 0x00;

    // flags for display on/off control
    private final byte LCD_DISPLAYON = (byte) 0x04;
    private final byte LCD_DISPLAYOFF = (byte) 0x00;
    private final byte LCD_CURSORON = (byte) 0x02;
    private final byte LCD_CURSOROFF = (byte) 0x00;
    private final byte LCD_BLINKON = (byte) 0x01;
    private final byte LCD_BLINKOFF = (byte) 0x00;

    // flags for display/cursor shift
    private final byte LCD_DISPLAYMOVE = (byte) 0x08;
    private final byte LCD_CURSORMOVE = (byte) 0x00;
    private final byte LCD_MOVERIGHT = (byte) 0x04;
    private final byte LCD_MOVELEFT = (byte) 0x00;

    // flags for function set
    private final byte LCD_8BITMODE = (byte) 0x10;
    private final byte LCD_4BITMODE = (byte) 0x00;
    private final byte LCD_2LINE = (byte) 0x08;
    private final byte LCD_1LINE = (byte) 0x00;
    private final byte LCD_5x10DOTS = (byte) 0x04;
    private final byte LCD_5x8DOTS = (byte) 0x00;

    // flags for backlight control
    private final byte LCD_BACKLIGHT = (byte) 0x08;
    private final byte LCD_NOBACKLIGHT = (byte) 0x00;

    private final byte En = (byte) 0b00000100; // Enable bit
    private final byte Rw = (byte) 0b00000010; // Read/Write bit
    private final byte Rs = (byte) 0b00000001; // Register select bit
}
