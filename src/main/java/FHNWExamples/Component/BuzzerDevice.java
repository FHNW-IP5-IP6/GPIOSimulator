package FHNWExamples.Component;

import FHNWExamples.Example;
import FHNWGPIO.Components.BuzzerComponent;
import FHNWGPIO.Components.Helper.Note;
import FHNWGPIO.Grove.GroveAdapter;
import com.pi4j.util.Console;

/**
 * Example for BuzzerComponent usage with a Grove Buzzer. If the buzzer is connected to the PWM port of the Grove Hat
 * it will play the Imperial March by John Williams.
 * https://gist.github.com/StevenNunez/6786124
 */
public class BuzzerDevice extends Example {

    public BuzzerDevice(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        Console console = new Console();
        console.promptForExit();

        BuzzerComponent buzzer = new BuzzerComponent(GroveAdapter.PWM);

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
                if (notes[i] != Note.PAUSE) {
                    buzzer.playTone(notes[i].getFrequency(), tempo * beats[i]);
                } else {
                    buzzer.stop(tempo * beats[i]);
                }
            }

            Thread.sleep(3000);
        }

        buzzer.stop();
    }
}
