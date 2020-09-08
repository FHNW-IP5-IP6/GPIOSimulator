package fhnwexamples;

import com.pi4j.io.gpio.*;
import fhnwgpio.components.ButtonComponent;
import fhnwgpio.components.BuzzerComponent;
import fhnwgpio.components.UltraSonicRangerComponent;

public class PresentationProject extends Example {
    private GpioController gpio;
    private ButtonComponent button;
    private UltraSonicRangerComponent ranger;
    private BuzzerComponent buzzer;
    private boolean buzzerIsOn = false;

    public PresentationProject(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        init();

        while (true) {
            if (button.isPressed()) {
                buzzer.stop();
                buzzerIsOn = !buzzerIsOn;
                Thread.sleep(900);
            }

            if (buzzerIsOn) {
                long cm = ranger.measureInCentimeter();
                buzzer.playTone((int) (250 + (Math.pow(cm, 1.5))));
            } else {
                buzzer.stop();
            }

            Thread.sleep(100);
        }
    }

    public void init() {
        GpioProvider provider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(provider);
        gpio = GpioFactory.getInstance();

        GpioPinDigitalInput buttonPin = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_26);
        button = new ButtonComponent(buttonPin, true);

        GpioPinDigitalInput echoPin = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_12);
        GpioPinDigitalInput triggerPin = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_16);
        ranger = new UltraSonicRangerComponent(triggerPin, echoPin);

        GpioPinPwmOutput buzzerPin = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_18);
        buzzer = new BuzzerComponent(buzzerPin);
    }
}
