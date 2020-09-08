package fhnwexamples;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import fhnwgpio.components.*;
import fhnwgpio.components.helper.Note;
import fhnwgpio.grove.GroveAdapter;
import uk.co.caprica.picam.CameraConfiguration;
import uk.co.caprica.picam.enums.Encoding;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SampleProject extends Example {
    private GpioController gpio;
    private ButtonComponent button;
    private I2CLCDComponent lcd;
    private UltraSonicRangerComponent ultraSonicRanger;
    private RaspberryPiCameraComponent raspberryPiCamera;
    private StepperMotorComponent stepperMotor;
    private BuzzerComponent buzzer;
    private LedStripDriverComponent ledStripDriver;

    public SampleProject(int key, String title) {
        super(key, title);
    }


    // tag::ProjectStart[]
    @Override
    public void execute() throws Exception {
        init();

        while (true) {
            waitForButtonPress();
            waitForCorrectRange();
            countdown();
            takePicture();
        }
    }
    // end::ProjectStart[]

    // tag::ProjectInit[]
    private void init() throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        GpioProvider provider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(provider);
        gpio = GpioFactory.getInstance();

        ultraSonicRanger = new UltraSonicRangerComponent(GroveAdapter.D18);
        button = new ButtonComponent(GroveAdapter.D16);
        buzzer = new BuzzerComponent(GroveAdapter.PWM);

        GpioPinDigitalOutput out1 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_22);
        GpioPinDigitalOutput out2 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_23);
        GpioPinDigitalOutput out3 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_24);
        GpioPinDigitalOutput out4 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_25);
        stepperMotor = new StepperMotorComponent(out1, out2, out3, out4);

        ledStripDriver = new LedStripDriverComponent(GroveAdapter.D5);
        ledStripDriver.start();
        ledStripDriver.setColor(0, 0, 0);
        ledStripDriver.stop();

        lcd = new I2CLCDComponent(0x27, I2CBus.BUS_1);
        lcd.init();
        CameraConfiguration stillConfig = RaspberryPiCameraComponent.createCameraConfiguration()
                .width(1920)
                .height(1080)
                .encoding(Encoding.JPEG)
                .quality(85)
                .rotation(0);
        raspberryPiCamera = new RaspberryPiCameraComponent(stillConfig);
    }
    // end::ProjectInit[]

    // tag::ProjectButtonPressed[]
    private void waitForButtonPress() throws InterruptedException {
        lcd.clearText();
        lcd.displayText("Press Button", 1);
        lcd.displayText("To Start!", 2);
        Thread.sleep(1000);

        while (!button.isPressed()) {
            //Avoid vibration error
            Thread.sleep(10);
        }
    }
    // end::ProjectButtonPressed[]

    // tag::ProjectDistanceInRange[]
    private void waitForCorrectRange() throws InterruptedException {
        lcd.clearText();
        lcd.displayText("Stay 1m away", 1);

        ledStripDriver.start();
        ledStripDriver.setColor(255, 0, 0);
        ledStripDriver.stop();

        long distance = ultraSonicRanger.measureInCentimeter();
        lcd.displayText("Distance = " + distance + "cm", 2);

        while (distance < 100 || distance > 200) {
            distance = ultraSonicRanger.measureInCentimeter();
            lcd.displayText("Distance = " + distance + "cm", 2);
            Thread.sleep(1000);
        }

        ledStripDriver.start();
        ledStripDriver.setColor(0, 255, 0);
        ledStripDriver.stop();
    }
    // end::ProjectDistanceInRange[]

    // tag::ProjectCountDown[]
    private void countdown() throws InterruptedException {
        lcd.clearText();
        for (int i = 5; i > 0; i--) {
            lcd.displayText("Countdown: " + i);
            Thread.sleep(1000);
            buzzer.playTone(Note.A4.getFrequency(), 200);
            stepperMotor.stepBackwards(400);
        }
        lcd.clearText();
    }
    // end::ProjectCountDown[]


    // tag::ProjectTakePicture[]
    private void takePicture() throws InterruptedException {
        ledStripDriver.start();
        ledStripDriver.setColor(255, 255, 255);
        ledStripDriver.stop();

        buzzer.playTone(Note.A6.getFrequency(), 500);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        raspberryPiCamera.takeStill("/home/pi/Pictures/pipic -" + dateFormat.format(date) + ".jpg", 500);
        lcd.displayText("Picture taken!");

        ledStripDriver.start();
        ledStripDriver.setColor(0, 0, 0);
        ledStripDriver.stop();

        stepperMotor.stepForwards(2000);
    }
    // end::ProjectTakePicture[]

}
