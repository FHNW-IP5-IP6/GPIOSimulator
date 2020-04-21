package gpioexample.gpio;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SerialCamera implements Example {
    private Console console;
    private Serial serial = null;
    private static String defaultFileName = "RaspiCamImage_";
    private static String defaultFileExtension = ".jpg";

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(
                new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on GPIO Extension Board
        console = new Console();
        console.promptForExit();

        serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        console.println(SerialPort.getDefaultPort());
        config.device(SerialPort.getDefaultPort()).baud(Baud._9600).dataBits(DataBits._8).parity(Parity.NONE)
                .stopBits(StopBits._1).flowControl(FlowControl.SOFTWARE);
        serial.open(config);
        initialize();
        preCapture();

        int pictureLength = getPictureLength();
        getPicture(pictureLength);
    }

    private void initialize() {
        try {
            byte[] syncCommand = { (byte) 0xaa, (byte) 0x0d, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
            byte[] ackCommand = { (byte) 0xaa, (byte) 0x0e, (byte) 0x0d, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

            console.println("initializing camera");

            while (true) {
                serial.write(syncCommand);
                byte[] bytes = serial.read(6);
                if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x0d
                        && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00) {

                    bytes = serial.read(6);
                    if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0d && bytes[2] == (byte) 0x00
                            && bytes[3] == (byte) 0x00 && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00)
                        break;
                }
            }
            serial.write(ackCommand);
            console.println("initialization done");
        } catch (Exception ex) {
            console.println(ex);
        }
    }

    private void preCapture() {
        try {
            byte[] initialCommand = { (byte) 0xaa, (byte) 0x01, (byte) 0x00, (byte) 0x07, (byte) 0x03, (byte) 0x07 };

            console.println("send camera settings");

            while (true) {
                serial.write(initialCommand);
                byte[] bytes = serial.read(6);

                if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x01
                        && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00) {
                    break;
                }
            }
            console.println("camera settings send");
        } catch (Exception ex) {
            console.println(ex);
        }
    }

    private int getPictureLength() {
        int pictureLength = 0;

        try {
            // Load size is 128 bytes. Needs to be separated into 2 bytes.
            // First is low byte (pos 4), second is high byte (pos 5).
            byte[] setPackageSizeCommand = { (byte) 0xaa, (byte) 0x06, (byte) 0x08, (byte) (128 & 0xff),
                    (byte) ((128 >> 8) & 0xff), (byte) 0x00 };
            byte[] snapshotCommand = { (byte) 0xaa, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
            byte[] getPictureCommand = { (byte) 0xaa, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

            console.println("set package size");

            while (true) {
                serial.write(setPackageSizeCommand);
                byte[] bytes = serial.read(6);

                if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x06
                        && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00) {
                    console.println("package size set");
                    break;
                }
            }

            console.println("send snapshot command");

            while (true) {
                serial.write(snapshotCommand);
                byte[] bytes = serial.read(6);

                if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x05
                        && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00)
                    console.println("snapshot command sent");
                break;
            }

            console.println("send get picture command");

            while (true) {
                serial.write(getPictureCommand);
                byte[] bytes = serial.read(6);

                if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x04
                        && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00) {
                    console.println("get picture command sent");
                    bytes = serial.read(6);
                    console.println("received picture length");

                    if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0a && bytes[2] == (byte) 0x01) {
                        pictureLength = (int) bytes[3] + (bytes[4] << 8) + (bytes[5] << 16);
                        console.println("picture length is: " + pictureLength);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            console.println(ex);
        }
        return pictureLength;
    }

    private void getPicture(long pictureLength) {
        try {
            int packageCount = (int) Math.ceil(pictureLength / (128 - 6));
            byte[] receiveDataPackageCommand = { (byte) 0xaa, (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00 };
            byte[] ackPackageEndCommand = { (byte) 0xaa, (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0xf0,
                    (byte) 0xF0 };

            console.println("will receive " + packageCount + " packages");

            File picture = new File(getFileName());

            console.println("created file " + picture.getName());

            if (picture.createNewFile()) {
                FileOutputStream stream = new FileOutputStream(picture.getName());

                for (int i = 0; i < packageCount; i++) {
                    receiveDataPackageCommand[4] = (byte) (i & 0xff);
                    receiveDataPackageCommand[5] = (byte) ((i >> 8) & 0xff);

                    serial.write(receiveDataPackageCommand);
                    byte[] bytes = serial.read(128);
                    stream.write(bytes);
                }

                stream.close();
                serial.write(ackPackageEndCommand);
                console.println("picture received and saved");
            } else {
                console.println("file already exists");
            }
        } catch (Exception ex) {
            console.println(ex);
        }

    }

    private String getFileName() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return defaultFileName + formatter.format(now) + defaultFileExtension;

    }
}
