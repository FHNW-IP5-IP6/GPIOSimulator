package gpioexample.component;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Lcd;
import gpioexample.Example;
import gpiosimulator.component.lcd.LCDTextAlignment;
import gpiosimulator.component.lcd.impl.GpioLcdDisplay;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LcdSystemTimeDevice extends Example {
    private final static int LCD_ROWS = 2;
    private final static int LCD_COLUMNS = 16;
    private final static int LCD_BITS = 4;

    public LcdSystemTimeDevice(int key, String title) {
        super(key, title);
    }


    @Override public void execute() throws Exception {

        GpioLcdDisplay lcdDisplay = new GpioLcdDisplay(LCD_ROWS, LCD_COLUMNS,
                RaspiPin.GPIO_06,      // LCD RS pin
                RaspiPin.GPIO_05,      // LCD strobe pin
                RaspiPin.GPIO_04,      // LCD data bit 0
                RaspiPin.GPIO_00,      // LCD data bit 1
                RaspiPin.GPIO_01,      // LCD data bit 2
                RaspiPin.GPIO_03);

        lcdDisplay.write(1,"GPIOSimulator", LCDTextAlignment.ALIGN_CENTER);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        // update time every one second
        while (true) {
            // write time to line 2 on LCD
            lcdDisplay.write(2,formatter.format(new Date()), LCDTextAlignment.ALIGN_CENTER);
            Thread.sleep(1000);
        }
    }
}
