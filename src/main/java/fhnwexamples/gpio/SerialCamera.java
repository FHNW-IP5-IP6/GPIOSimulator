package fhnwexamples.gpio;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.serial.*;
import com.pi4j.util.Console;
import fhnwexamples.Example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SerialCamera extends Example {
    private Console console;
    private Serial serial = null;
    private int packageSize = 512;
    private int nofNoDataBits = 6;
    private int lowDataSizeBit = 2;
    private int highDataSizeBit = 3;
    private static String defaultFileName = "RasPiCamImage_";
    private static String defaultFileExtension = ".jpg";

    public SerialCamera(int key, String title) {
        super(key, title);
    }

    @Override public void execute() throws Exception {
        GpioFactory.setDefaultProvider(
                new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING)); //like on gpio Extension Board
        console = new Console();
        console.promptForExit();

        serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        console.println(SerialPort.getDefaultPort());
        config.device(SerialPort.getDefaultPort()).baud(Baud._9600);
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

            while (serial.available() == 0) {
                console.println(serial.available());
                serial.write(syncCommand);
                Thread.sleep(10);
            }

            byte[] bytes = serial.read(6);
            clearDataFromSerialInput();

            if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x0d && bytes[4] == (byte) 0x00
                    && bytes[5] == (byte) 0x00) {

                bytes = serial.read(6);

                if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0d && bytes[2] == (byte) 0x00
                        && bytes[3] == (byte) 0x00 && bytes[4] == (byte) 0x00 && bytes[5] == (byte) 0x00) {
                    serial.write(ackCommand);
                }
            }

            clearDataFromSerialInput();
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
            console.println("camera settings sent");
        } catch (Exception ex) {
            console.println(ex);
        }
    }

    private int getPictureLength() {
        int pictureLength = 0;

        try {
            // Load size is 128 bytes. Needs to be separated into 2 bytes.
            // First is low byte (pos 4), second is high byte (pos 5).
            byte[] setPackageSizeCommand = { (byte) 0xaa, (byte) 0x06, (byte) 0x08, (byte) (packageSize & 0xff),
                    (byte) ((packageSize >> 8) & 0xff), (byte) 0x00 };
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
            byte[] receiveDataPackageCommand = { (byte) 0xaa, (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00 };
            byte[] ackPackageEndCommand = { (byte) 0xaa, (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0xf0,
                    (byte) 0xF0 };

            int packageCount = pictureLength % (packageSize - nofNoDataBits) == 0 ?
                    (int) pictureLength / (packageSize - nofNoDataBits) :
                    (int) Math.ceil(pictureLength / (packageSize - nofNoDataBits));
            console.println("package count is " + packageCount);

            int successCount = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteBuffer byteBuffer = ByteBuffer.allocate(packageSize);

            while (successCount < packageCount) {
                receiveDataPackageCommand[4] = (byte) successCount;
                receiveDataPackageCommand[5] = (byte) (successCount >> 8);

                serial.write(receiveDataPackageCommand);

                while (byteBuffer.position() < packageSize - 1) {
                    byteBuffer.put(serial.read());
                }

                byte[] receivedData = byteBuffer.array();
                byteBuffer.position(0);
                int relevantByteCount = getIntegerFromBytes(receivedData[lowDataSizeBit],
                        receivedData[highDataSizeBit]);
                byteArrayOutputStream.write(receivedData, 4, relevantByteCount);

                successCount++;
            }

            console.println("read a total of " + byteArrayOutputStream.size() + " bytes");
            serial.write(ackPackageEndCommand);
            console.println("sent acknowledgement to camera");

            String fileName = getFileName();
            console.println("save file " + fileName);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            ImageIO.write(image, "JPG", new File(fileName));
            console.println("file " + fileName + " saved");

        } catch (Exception ex) {
            console.println(ex);
        }

    }

    private static int getIntegerFromBytes(byte low, byte high) {
        return (0xff & (byte) 0x00) << 24 | (0xff & (byte) 0x00) << 16 | (0xff & high) << 8 | (0xff & low) << 0;
    }

    private String getFileName() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return defaultFileName + formatter.format(now) + defaultFileExtension;

    }

    private void clearDataFromSerialInput() throws IOException {
        int byteCount = serial.available();
        console.println("removing " + byteCount + " bytes from serial");
        if (byteCount > 0) {
            serial.read(byteCount);
        }
    }
}
