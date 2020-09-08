package fhnwexamples.component;

import com.pi4j.io.gpio.*;
import fhnwexamples.Example;
import fhnwgpio.components.BuzzerComponent;
import fhnwgpio.components.helper.Note;
import com.pi4j.util.Console;

/**
 * Example for BuzzerComponent usage with a GPIO buzzer. The buzzer will play the Imperial March by John Williams.
 * https://gist.github.com/StevenNunez/6786124
 */
public class BuzzerDevice extends Example {

    public BuzzerDevice(int key, String title) {
        super(key, title);
    }

    // tag::BuzzerDevice[]
    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        Console console = new Console();
        console.promptForExit();

        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(RaspiBcmPin.GPIO_18);
        BuzzerComponent buzzer = new BuzzerComponent(pwm);

        Note notes[] = { Note.G4, Note.G4, Note.G4, Note.DS4, Note.AS4, Note.G4, Note.DS4, Note.AS4, Note.G4, Note.D5,
                Note.D5, Note.D5, Note.DS5, Note.AS4, Note.FS4, Note.DS4, Note.AS4, Note.G4, Note.G5, Note.G4, Note.G4,
                Note.G5, Note.FS5, Note.F5, Note.E5, Note.DS5, Note.E5, Note.PAUSE, Note.G4, Note.PAUSE, Note.CS5,
                Note.C5, Note.B4, Note.AS4, Note.A4, Note.AS4, Note.PAUSE, Note.DS4, Note.PAUSE, Note.FS4, Note.DS4,
                Note.AS4, Note.G4, Note.DS4, Note.AS4, Note.G4 };
        int beats[] = { 8, 8, 8, 6, 2, 8, 6, 2, 16, 8, 8, 8, 6, 2, 8, 6, 2, 16, 8, 6, 2, 8, 6, 2, 2, 2, 2, 6, 2, 2, 8,
                6, 2, 2, 2, 2, 6, 2, 2, 9, 6, 2, 8, 6, 2, 16 };
        int tempo = 103;

        while (console.isRunning()) {
            for (int i = 0; i < notes.length; i++) {
                buzzer.playTone(notes[i].getFrequency(), tempo * beats[i]);
            }

            buzzer.stop();
            Thread.sleep(3000);

        }

        buzzer.stop();
    }
    // end::BuzzerDevice[]
}
