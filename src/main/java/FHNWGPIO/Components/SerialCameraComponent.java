package FHNWGPIO.Components;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

// TODO: JavaDoc
// TODO: Make different settings available
// TODO: Make different file types available

/**
 * FHNW component for the grove serial camera. This class implements the communication protocol and simplifies
 * accessing the grove serial camera via the built-in default serial bus of the Raspberry Pi.
 * Grove Serial Camera: https://wiki.seeedstudio.com/Grove-Serial_Camera_Kit/
 */
public class SerialCameraComponent {
    private Console console;
    private Serial serial = null;
    private int packageSize;
    private boolean logIsActive;
    private int nofNoDataBits = 6;
    private int lowDataSizeBit = 2;
    private int highDataSizeBit = 3;
    private int lowCheckSumBitPosition = -1;
    private int syncRetryCount = 64;

    /**
     * Constructor of the SerialCameraComponent. Configures the default serial port of the Raspberry Pi.
     *
     * @param console
     * @param packageSize
     * @param activateLogging
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(Console console, int packageSize, boolean activateLogging)
            throws IOException, InterruptedException {
        this.console = console;
        this.packageSize = packageSize;
        logIsActive = activateLogging;
        serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        config.device(SerialPort.getDefaultPort()).baud(Baud._9600);
        serial.open(config);

        initializeSerialBusCommunication();
        sendSettingsToCamera();
    }

    /**
     * Constructor of the SerialCameraComponent without logging. Configures the default serial port of the
     * Raspberry Pi.
     *
     * @param console
     * @param packageSize
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(Console console, int packageSize) throws IOException, InterruptedException {
        this(console, packageSize, false);
    }

    /**
     * Constructor of the SerialCameraComponent with a default package size of 512 bytes. Configures the
     * default serial port of the Raspberry Pi.
     *
     * @param console
     * @param activateLogging
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(Console console, boolean activateLogging) throws IOException, InterruptedException {
        this(console, 512, activateLogging);
    }

    /**
     * Constructor of the SerialCameraComponent without logging and a default package size of 512 bytes.
     * Configures the default serial port of the Raspberry Pi.
     *
     * @param console
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(Console console) throws IOException, InterruptedException {
        this(console, 512, false);
    }

    public String getPicture(String fileName, String extension) {
        int pictureLength = getPictureLengthFromCamera();
        return savePicture(pictureLength, fileName, extension);
    }

    /**
     * This method initializes the serial bus communication for the grove serial camera.
     * Documentation: https://files.seeedstudio.com/wiki/Grove-Serial_Camera_Kit/res/cj-ov528_protocol.pdf
     */
    private void initializeSerialBusCommunication() {
        try {
            int tryCount = 0;
            byte[] syncCommand = { (byte) 0xaa, 0x0d, 0x00, 0x00, 0x00, 0x00 };
            byte[] ackCommand = { (byte) 0xaa, 0x0e, 0x0d, 0x00, 0x00, 0x00 };

            logMessage("initializing communication with the camera");

            while (serial.available() == 0 && tryCount < syncRetryCount) {
                serial.write(syncCommand);
                tryCount++;
                Thread.sleep(100);
            }

            if (tryCount >= syncRetryCount) {
                throw new IOException("tried to sync with camera for " + tryCount + " without success");
            }

            logMessage("camera responded after " + tryCount + " requests");
            byte[] bytes = serial.read(6);
            clearDataFromSerialInput();

            if (bytes[0] == 0xaa && bytes[1] == 0x0e && bytes[2] == 0x0d && bytes[4] == 0x00 && bytes[5] == 0x00) {
                logMessage("received response is a valid acknowledgement");
                logMessage("waiting for sync from camera");
                bytes = serial.read(6);

                if (bytes[0] == 0xaa && bytes[1] == 0x0d && bytes[2] == 0x00 && bytes[3] == 0x00 && bytes[4] == 0x00
                        && bytes[5] == 0x00) {
                    logMessage("received sync from the camera");
                    logMessage("sending sync acknowledgement to camera");
                    serial.write(ackCommand);
                    logMessage("serial bus communication between camera and pi is ready");
                }
            }

            clearDataFromSerialInput();
        } catch (Exception ex) {
            console.println(ex);
        }
    }

    private void sendSettingsToCamera() {
        try {
            byte[] initialCommand = { (byte) 0xaa, 0x01, 0x00, 0x07, 0x03, 0x07 };

            logMessage("sending settings to the camera");
            serial.write(initialCommand);

            while (serial.available() < 6) {
                Thread.sleep(10);
            }

            logMessage("received a response from the camera");
            byte[] bytes = serial.read(6);

            if (bytes[0] == 0xaa && bytes[1] == 0x0e && bytes[2] == 0x01 && bytes[4] == 0x00 && bytes[5] == 0x00) {
                logMessage("received response is a valid settings acknowledgement");
                logMessage("settings where successfully sent to the camera");
            }

            clearDataFromSerialInput();
        } catch (Exception ex) {
            console.println(ex);
        }
    }

    /**
     * @return pictureLength
     */
    private int getPictureLengthFromCamera() {
        int pictureLength = 0;

        try {
            byte[] setPackageSizeCommand = { (byte) 0xaa, 0x06, 0x08, (byte) (packageSize & 0xff),
                    (byte) ((packageSize >> 8) & 0xff), 0x00 };
            byte[] snapshotCommand = { (byte) 0xaa, 0x05, 0x00, 0x00, 0x00, 0x00 };
            byte[] getPictureCommand = { (byte) 0xaa, 0x04, 0x01, 0x00, 0x00, 0x00 };

            logMessage("sending the desired package size to the camera");
            serial.write(setPackageSizeCommand);

            while (serial.available() < 6) {
                Thread.sleep(10);
            }

            logMessage("received a response from the camera");
            byte[] bytes = serial.read(6);

            if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x06 && bytes[4] == (byte) 0x00
                    && bytes[5] == (byte) 0x00) {
                logMessage("response was a valid package size acknowledgement");
            }

            logMessage("sending snapshot command");
            serial.write(snapshotCommand);

            while (serial.available() < 6) {
                Thread.sleep(10);
            }

            logMessage("received a response from the camera");
            bytes = serial.read(6);

            if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x05 && bytes[4] == (byte) 0x00
                    && bytes[5] == (byte) 0x00) {
                logMessage("response was a valid snapshot acknowledgement");
            }

            logMessage("sending get picture length command");
            serial.write(getPictureCommand);

            while (serial.available() < 6) {
                Thread.sleep(10);
            }

            logMessage("received a response from the camera");
            bytes = serial.read(6);

            if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x04 && bytes[4] == (byte) 0x00
                    && bytes[5] == (byte) 0x00) {
                logMessage("response was a valid picture length acknowledgement");
                logMessage("reading the next 6 bytes to get the picture length");
                bytes = serial.read(6);

                if (bytes[0] == (byte) 0xaa && bytes[1] == 0x0a && bytes[2] == 0x01) {
                    pictureLength = (int) bytes[3] + (bytes[4] << 8) + (bytes[5] << 16);
                    logMessage("picture length is: " + pictureLength);
                }
            }
        } catch (Exception ex) {
            console.println(ex);
        }
        return pictureLength;
    }

    /**
     * @param pictureLength
     * @param defaultFileName
     * @param defaultFileExtension
     */
    private String savePicture(int pictureLength, String defaultFileName, String defaultFileExtension) {
        String fileName = "ERROR";
        int bytesRemaining = pictureLength;
        try {
            byte[] receiveDataPackageCommand = { (byte) 0xaa, 0x0e, 0x00, 0x00, 0x00, 0x00 };
            byte[] ackPackageEndCommand = { (byte) 0xaa, 0x0e, 0x00, 0x00, (byte) 0xf0, (byte) 0xF0 };

            int packageCount = pictureLength % (packageSize - nofNoDataBits) == 0 ?
                    pictureLength / (packageSize - nofNoDataBits) :
                    (pictureLength / (packageSize - nofNoDataBits)) + 1;

            logMessage("camera will send " + packageCount + " packages");

            int successCount = 0;
            ByteArrayOutputStream camStream = new ByteArrayOutputStream();
            ByteBuffer imageBuffer = ByteBuffer.allocate(packageSize);

            while (successCount < packageCount) {
                receiveDataPackageCommand[4] = (byte) successCount;
                receiveDataPackageCommand[5] = (byte) (successCount >> 8);

                serial.write(receiveDataPackageCommand);

                boolean lastItem = successCount == packageCount - 1;
                int readSize = lastItem ? bytesRemaining + nofNoDataBits - 1 : packageSize - 1;

                while (imageBuffer.position() < readSize) {
                    imageBuffer.put(serial.read());
                }

                byte[] receivedData = imageBuffer.array();
                imageBuffer.position(0);
                byte calculatedCheckSum = getCheckSum(receivedData);
                byte receivedCheckSum = receivedData[receivedData.length - 1 + lowCheckSumBitPosition];

                if (calculatedCheckSum == receivedCheckSum || lastItem) {
                    int byteCount = getIntegerFromBytes(receivedData[lowDataSizeBit], receivedData[highDataSizeBit]);
                    bytesRemaining -= byteCount;
                    logMessage(bytesRemaining + " bytes remaining");
                    camStream.write(receivedData, 4, byteCount);
                    successCount++;
                } else {
                    logMessage("calculated check sum is " + calculatedCheckSum);
                    logMessage("received check sum is " + receivedCheckSum);
                    logMessage("package error at package " + successCount);
                    logMessage(" => retry");
                }
            }

            logMessage("read a total of " + camStream.size() + " bytes");
            logMessage("sending package end acknowledgement to camera");
            serial.write(ackPackageEndCommand);

            fileName = getFileName(defaultFileName, defaultFileExtension);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(camStream.toByteArray()));
            ImageIO.write(image, "JPG", new File(fileName));
            logMessage("picture saved in file " + fileName);
        } catch (Exception ex) {
            console.println(ex);
        }
        return fileName;
    }

    private byte getCheckSum(byte[] bytes) {
        byte result = 0;

        for (byte b : Arrays.copyOf(bytes, bytes.length - 2)) {
            result += b;
        }

        return result;
    }

    private static int getIntegerFromBytes(byte low, byte high) {
        return (0xff & (byte) 0x00) << 24 | (0xff & (byte) 0x00) << 16 | (0xff & high) << 8 | (0xff & low) << 0;
    }

    private String getFileName(String defaultFileName, String defaultFileExtension) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return defaultFileName + formatter.format(now) + defaultFileExtension;

    }

    private void clearDataFromSerialInput() throws IOException {
        int byteCount = serial.available();
        if (byteCount > 0) {
            serial.read(byteCount);
        }
    }

    private void logMessage(String message) {
        if (logIsActive) {
            console.println(message);
        }
    }
}
