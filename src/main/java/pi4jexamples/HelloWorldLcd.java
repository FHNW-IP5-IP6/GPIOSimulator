package pi4jexamples;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.util.BitSet;

public class HelloWorldLcd implements Example {
    boolean             rsFlag                = false;
    boolean             eFlag                 = false;
    private static I2CDevice dev              = null;
    private final int[] LCD_LINE_ADDRESS      = { 0x80, 0xC0};  //Address for LCD Lines 0 and 1

    private final boolean LCD_CHR = true; //To decide sent data is data or command
    private final static boolean LCD_CMD = false;

    int         RS_PIN=0;//Pin of MCP23017 PORTB/A connected LCD RS pin
    int         EN_PIN=1;//Pin of MCP23017 PORTB/A connected LCD E pin
    int         D7_PIN=5;//Pin of MCP23017 PORTB/A connected LCD D7 pin
    int         D6_PIN=4;//Pin of MCP23017  PORTB/A connected LCD D6 pin
    int         D5_PIN=3;//Pin of MCP23017  PORTB/A connected LCD D5 pin
    int         D4_PIN=2;//Pin of MCP23017 PORTB/A connected LCD D4 pin

    @Override public void execute() throws Exception {
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        System.out.println("Init I2C Bus");

        dev = bus.getDevice(0x27); //Address for MCp23017 change if A0,A1,A2 are connected to diff potenrial

        dev.write(0x01, (byte) 0x00); //Initialized PORT B of MCP23017 to use as ouput.

        HelloWorldLcd lcd= new HelloWorldLcd();

        lcd.init(); //LCD Initialization Routine

        lcd.lcd_byte(0x01, LCD_CMD); //LCD Clear Command
        lcd.lcd_byte(0x02, LCD_CMD); //LCD Home Command
        lcd.write("WeArGenius");
        lcd.setCursorPosition(1, 0);
        lcd.write("weargenius.in");

        Thread.sleep(2000);
        while(true){
            lcd.lcd_byte(0x01, LCD_CMD); //LCD Clear
            lcd.setCursorPosition(1, 0);
            lcd.write("Embedded");
            Thread.sleep(3000);

            lcd.lcd_byte(0x01, LCD_CMD); //LCD Clear
            lcd.setCursorPosition(1, 0);
            lcd.write("Home Automation");
            Thread.sleep(3000);

            lcd.lcd_byte(0x01, LCD_CMD); //LCD Clear
            lcd.setCursorPosition(1, 0);
            lcd.write("IOT");
            Thread.sleep(3000);

            lcd.lcd_byte(0x01, LCD_CMD); //LCD Clear
            lcd.setCursorPosition(1, 0);
            lcd.write("Programming");
            Thread.sleep(3000);
        }
    }
    public void write(byte data) { //Writes 1 Byte data to LCD
        try {
            lcd_byte(data, LCD_CHR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(String data) {//Writes a string to LCD
        System.out.println(data);
        for (int i = 0; i < data.length(); i++) {
            try {
                lcd_byte(data.charAt(i), LCD_CHR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void lcd_byte(int val, boolean type) throws Exception { //Sets RS flag and send value to ports depending on DATA or COMMAND

        rsFlag=type;

        write(val >> 4);
        pulse_en(type, val >> 4);    // cmd or display data

        write(val & 0x0f);
        pulse_en(type, val & 0x0f);
    }

    public static BitSet fromByte(byte b) { //Convert a byte into Bitset
        BitSet bits = new BitSet(8);

        for (int i = 0; i < 8; i++) {
            bits.set(i, (b & 1) == 1);
            b >>= 1;
        }

        return bits;
    }

    private void init() throws Exception { //Initialization routine of LCD
        lcd_byte(0x33, LCD_CMD);    // 4 bit
        lcd_byte(0x32, LCD_CMD);    // 4 bit
        lcd_byte(0x28, LCD_CMD);    // 4bit - 2 line
        lcd_byte(0x08, LCD_CMD);    // don't shift, hide cursor
        lcd_byte(0x01, LCD_CMD);    // clear and home display
        lcd_byte(0x06, LCD_CMD);    // move cursor right
        lcd_byte(0x0c, LCD_CMD);    // turn on
    }

    private void pulse_en(boolean type, int val) throws Exception {// Make the enable pin high and low to provide a pulse.
        eFlag = true;
        write(val);
        eFlag =false;
        write(val);

        if (type == LCD_CMD) {
            Thread.sleep(1);
        }
    }
    private void write(int incomingData) throws Exception { // Arrange the respective bit of value to be send depending upon the pins the LCD is connected to.
        int    tmpData = incomingData;
        BitSet bits    = fromByte((byte) tmpData);
        byte   out     = (byte) ((bits.get(3)
                ? 1 << D7_PIN
                : 0 << D7_PIN) | (bits.get(2)
                ? 1 << D6_PIN
                : 0 << D6_PIN) | (bits.get(1)
                ? 1 << D5_PIN
                : 0 << D5_PIN) | (bits.get(0)
                ? 1 << D4_PIN
                : 0 << D4_PIN) | (rsFlag
                ? 1 << RS_PIN
                : 0 << RS_PIN) | (eFlag
                ? 1 << EN_PIN
                : 0 << EN_PIN));

        dev.write(0x13,out); //Set the value to PORT B register.
    }

    public void setCursorPosition(int row, int column) {
        try {
            lcd_byte(LCD_LINE_ADDRESS[row] + column, LCD_CMD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
