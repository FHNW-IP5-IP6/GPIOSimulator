package FHNWExamples.Component;

import FHNWGPIO.Components.I2CLCD;
import com.pi4j.io.i2c.I2CBus;
import FHNWExamples.Example;
import com.pi4j.util.Console;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Pi4j LCD1602 example with the PCF8574 I2C extender.
 * Documentation of the I2C extender:
 * http://www.ti.com/lit/ds/symlink/pcf8574.pdf?ts=1588064881394
 */
public class LcdSystemTimeI2C extends Example {

    public LcdSystemTimeI2C(int key, String title) {
        super(key, title);
    }

    @Override
    // tag::LcdSystemTimeI2C[]
    public void execute() {
        Console console = new Console();
        try {
            //find the address and the bus with the following command on the pi
            //sudo i2cdetect -y 1
            I2CLCD lcd = new I2CLCD(0x27, I2CBus.BUS_1);
            lcd.init();

            Scanner scanner = new Scanner(System.in);
            console.println("Please enter a text to be displayed above the system time");
            String text = scanner.nextLine();

            lcd.displayText(text, 1, 1000, false);

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            while (true) {
                lcd.displayText(formatter.format((new Date())), 2, 4, false);
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            console.println(ex.toString());
        }
    }
    // end::LcdSystemTimeI2C[]
}

