package fhnwexamples.gpio;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Lcd;
import fhnwexamples.Example;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LcdSystemTime extends Example {
    private final static int LCD_ROWS = 2;
    private final static int LCD_COLUMNS = 16;
    private final static int LCD_BITS = 4;

    public LcdSystemTime(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> gpio SETUP FAILED");
            return;
        }

        // setup the lcd handler
        int lcdHandle = Lcd.lcdInit(LCD_ROWS,       // number of row supported by LCD
                LCD_COLUMNS,                        // number of columns supported by LCD
                LCD_BITS,                           // number of bits used to communicate to LCD
                RaspiPin.GPIO_06.getAddress(),      // LCD RS pin
                RaspiPin.GPIO_05.getAddress(),      // LCD strobe pin
                RaspiPin.GPIO_04.getAddress(),      // LCD data bit 0
                RaspiPin.GPIO_00.getAddress(),      // LCD data bit 1
                RaspiPin.GPIO_01.getAddress(),      // LCD data bit 2
                RaspiPin.GPIO_03.getAddress(),      // LCD data bit 3
                0,                              // LCD data bit 4
                0,                              // LCD data bit 5
                0,                              // LCD data bit 6
                0);                             // LCD data bit 7

        // verify initialization
        if (lcdHandle == -1) {
            System.out.println(" ==>> LCD INIT FAILED");
            return;
        }

        // clear LCD
        Lcd.lcdClear(lcdHandle);
        Thread.sleep(1000);

        // write line 1 to LCD
        Lcd.lcdHome(lcdHandle);
        Lcd.lcdPuts(lcdHandle, "gpiosimulator");

        Lcd.lcdPosition(lcdHandle, 0, 1);
        Lcd.lcdPuts(lcdHandle, "----------------");

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        // update time every one second
        while (true) {
            // write time to line 2 on LCD
            Lcd.lcdPosition(lcdHandle, 0, 1);
            Lcd.lcdPuts(lcdHandle, "--- " + formatter.format(new Date()) + " ---");
            Thread.sleep(1000);
        }
    }
}
