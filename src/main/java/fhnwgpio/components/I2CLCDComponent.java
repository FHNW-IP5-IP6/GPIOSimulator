package fhnwgpio.components;

import fhnwgpio.components.base.I2CBase;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import fhnwgpio.components.helper.ComponentLogger;

import java.io.IOException;

/**
 * FHNW implementation for the I2C LCD display
 * <p>
 * Following library was used as additional reference for underlying logic
 * https://github.com/Poduzov/PI4J-I2C-LCD
 */
public class I2CLCDComponent extends I2CBase {

    private final int ROWS = 2;
    private final int COLUMNS = 16;


    /**
     * Standard Constructor that needs address and the busnumber to initialise
     *
     * @param address   address of the I2C LCD
     * @param busNumber bus number of the I2
     * @throws IOException                              Exception that can be thrown when trying to initialise
     * @throws I2CFactory.UnsupportedBusNumberException Exception can be thrown when trying to initialise
     */
    public I2CLCDComponent(int address, int busNumber) throws IOException, I2CFactory.UnsupportedBusNumberException {
        super(address, busNumber);
    }

    /**
     * Constructor that needs I2C information in the pi4j I2CDevice object
     *
     * @param device device object containing i2c info
     */
    public I2CLCDComponent(I2CDevice device) {
        super(device);
    }

    /**
     * sends initialisation commands to display
     */
    // tag::LCDInit[]
    public void init() {
        try {
            writeCommand((byte) 0x03);
            writeCommand((byte) 0x03);
            writeCommand((byte) 0x03);
            writeCommand((byte) 0x02);

            writeCommand((byte) (LCD_FUNCTIONSET | LCD_2LINE | LCD_5x8DOTS | LCD_4BITMODE));
            writeCommand((byte) (LCD_DISPLAYCONTROL | LCD_DISPLAYON));
            writeCommand(LCD_CLEARDISPLAY);
            writeCommand((byte) (LCD_ENTRYMODESET | LCD_ENTRYLEFT));
            Thread.sleep(0, 200000);
            ComponentLogger.logInfo("I2CLCDComponent: initialised");
        } catch (Exception ex) {
            ComponentLogger.logError("I2CLCDComponent: Error while trying to initialise " + ex.getMessage());
        }
    }
    // end::LCDInit[]

    /**
     * displays a text on the first line of the display.
     * If it's larger than 16 characters then it will jump over to the second line.
     * The excess characters (greater than 32) are not shown on the display
     *
     * @param text text to diplay on lcd
     */
    public void displayText(String text) {
        displayText(text, 1, 0, true);
    }

    /**
     * writes string to the lcd display on a specific line.
     * Excess characters (greater than 16) are not shown on the line
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
     * Excess characters (greater than 16-position) on the line will not be shown.
     *
     * @param text           text to show on the lcd text.
     * @param line           line on the lcd display
     * @param pos            is the position on the line.
     * @param jumpToNextLine jumps to the second line if the first line is
     */
    // tag::LCDDisplayText[]
    public void displayText(String text, int line, int pos, boolean jumpToNextLine) {
        ComponentLogger.logInfo("I2CLCDComponent: displaying text: '" + text + "' on line " + line);

        if (text.length() > 32 - pos) {
            text = text.substring(0, 31 - pos);
            ComponentLogger.logInfo("I2CLCDComponent: text length cut to 31 characters");
        }

        String firstLine = text, secondLine = text;
        byte posNew;

        //lcd only has 2 lines, so I assume the first one if the second one wasn't selected explicitly
        if (line != 2) {
            posNew = (byte) pos;
            boolean textLargerThanLine = text.length() > COLUMNS - pos;
            if (textLargerThanLine) {
                firstLine = text.substring(0, 16 - pos);
                secondLine = text.substring(COLUMNS - pos);
            }
            displayLine(firstLine, posNew);
            if (jumpToNextLine && textLargerThanLine) displayLine(secondLine, posNew + 0x40);
        } else {
            posNew = (byte) (0x40 + pos);
            displayLine(secondLine, posNew);
        }
    }
    // end::LCDDisplayText[]

    /**
     * shows text on display and scrolls it with a delay
     *
     * @param text  to display
     * @param line  on which the text should be visible
     * @param delay for every position jump
     * @throws InterruptedException can be thrown while on delay
     */
    // tag::LCDDisplayScrollText[]
    public void displayScrollText(String text, int line, int delay, boolean jumpToNextLine, boolean startAgain) throws InterruptedException {
        ComponentLogger.logInfo("I2CLCDComponent: displaying scroll text: '" + text + "' on line " + line + "with a delay of " + delay + " milliseconds");

        String paddedtext = getEmptyLine() + text;

        for (int i = 0; i < paddedtext.length(); i++) {
            displayText(paddedtext.substring(i), line, 0, jumpToNextLine);
            Thread.sleep(delay);
            if (jumpToNextLine)
                clearText(false);
            else
                clearLine(line);

            if (i == paddedtext.length() - 1 && startAgain)
                displayScrollText(text, line, delay, jumpToNextLine, startAgain);
        }
    }
    // end::LCDDisplayScrollText[]

    /**
     * displays the text and scrolls to the sides, but bounces back.
     * This only works for short texts, otherwise it would be unreadable
     *
     * @param text       to display
     * @param line       for the text
     * @param delay      for every position jump
     * @param startAgain decides wether it's done only once or again and again
     * @throws InterruptedException can be thrown while on delay
     */
    // tag::LCDDisplayBounceText[]
    public void displayBounceText(String text, int line, int delay, boolean startAgain) throws InterruptedException {
        ComponentLogger.logInfo("I2CLCDComponent: displaying bounce text: '" + text + "' on line " + line + "with a delay of " + delay + " milliseconds");

        // Bounce doesn't make sense for a large text
        if (text.length() >= COLUMNS - 1) {
            IllegalArgumentException ex = new IllegalArgumentException("text has to be smaller than 15 characters");
            ComponentLogger.logError("I2CLCDComponent: " + ex.getMessage());
            throw ex;

        }

        //shifts the whole text to the right end
        for (int i = 0; i < COLUMNS - text.length(); i++) {
            if (i + text.length() <= COLUMNS) {
                displayText(text, line, i, false);
                Thread.sleep(delay);
                clearLine(line);
            }
        }
        //shifts the whole text to the left end
        for (int i = COLUMNS - text.length(); i > 0; i--) {
            displayText(text, line, i, false);
            Thread.sleep(delay);
            clearLine(line);
        }
        if (startAgain) displayBounceText(text, line, delay, startAgain);
    }
    // end::LCDDisplayBounceText[]

    /**
     * Sets the backlight on or off
     *
     * @param state sets the backlight (true == on, false == off)
     */
    public void setBacklightState(boolean state) {
        ComponentLogger.logInfo("I2CLCDComponent: Set Backlight: " + state);
        if (state) {
            writeCmd(LCD_BACKLIGHT);
        } else {
            writeCmd(LCD_NOBACKLIGHT);
        }
    }

    /**
     * write a character to lcd
     */
    public void writeCharacter(byte charvalue) {
        writeSplitCommand(charvalue, Rs);
    }

    /**
     * clears the lcd text
     */
    public void clearText() {
        clearText(false);
    }

    private void clearText(boolean log) {
        if (log) ComponentLogger.logInfo("I2CLCDComponent: Display cleared");
        writeCommand(LCD_CLEARDISPLAY);
        writeCommand(LCD_RETURNHOME);
    }

    /**
     * displays a line on a specific position
     *
     * @param text to display
     * @param pos  for the start of the text
     */
    private void displayLine(String text, int pos) {
        writeCommand((byte) (0x80 + pos));

        for (int i = 0; i < text.length(); i++) {
            writeCharacter((byte) text.charAt(i));
        }
    }

    //Gets an empty line
    private String getEmptyLine() {
        String text = "";
        return String.format("%16s", text);
    }

    /**
     * clears the selected lin
     *
     * @param line line to clear
     */
    private void clearLine(int line) {
        displayText(getEmptyLine(), line);
    }


    /**
     * clocks EN to latch command
     */
    private void lcdStrobe(byte data) {
        try {
            writeCmd((byte) (data | En | LCD_BACKLIGHT));
            Thread.sleep(0, 500000);
            writeCmd((byte) ((data & ~En) | LCD_BACKLIGHT));
            Thread.sleep(0, 100000);
        } catch (Exception ex) {
            ComponentLogger.logError("I2CLCDComponent: Error sending cmd to device " + ex.getMessage());
        }
    }

    /**
     * write a command to lcd
     */
    private void writeCommand(byte cmd) {
        writeSplitCommand(cmd, (byte) 0);
    }

    /**
     * write byte split in two as it is initialized in 4Bit Mode
     * commands larger than 4 bits should use this
     */
    private void writeSplitCommand(byte cmd, byte mode) {
        //bitwise AND with 11110000 to remove last 4 bits
        writeFourBits((byte) (mode | (cmd & 0xF0)));
        //bitshift and bitwise AND to remove first 4 bits
        writeFourBits((byte) (mode | ((cmd << 4) & 0xF0)));
    }

    /**
     * writes four bits
     */
    private void writeFourBits(byte data) {
        try {
            writeCmd((byte) (data | LCD_BACKLIGHT));
            lcdStrobe(data);
        } catch (Exception ex) {
            ComponentLogger.logError("I2CLCDComponent: Error sending split cmd to device " + ex.getMessage());
        }
    }

    /**
     * Set of commands of LCD1602 based on page 6 of the datasheet:
     * https://www.openhacks.com/uploadsproductos/eone-1602a1.pdf
     * or
     * https://www.waveshare.com/datasheet/LCD_en_PDF/LCD1602.pdf
     */
    // tag::LCD1602Commands[]
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
    // end::LCD1602Commands[]
}
