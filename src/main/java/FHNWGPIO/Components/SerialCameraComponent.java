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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

// TODO: Make different settings available
// TODO: Make different file types available

/**
 * FHNW implementation for the grove serial camera. This class implements the communication protocol and simplifies
 * accessing the grove serial camera via the built-in default serial bus of the Raspberry Pi.
 * Grove Serial Camera: https://wiki.seeedstudio.com/Grove-Serial_Camera_Kit/
 * Documentation: https://files.seeedstudio.com/wiki/Grove-Serial_Camera_Kit/res/cj-ov528_protocol.pdf
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
    private byte lastWrittenByte = (byte) 0x00;

    /**
     * Constructor of the SerialCameraComponent. Configures the default serial port of the Raspberry Pi.
     *
     * @param console         Pi4J Console
     * @param packageSize     Desired package size. Must be between 15 and 2049 bytes.
     * @param activateLogging Activates / deactivates logging.
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(Console console, int packageSize, boolean activateLogging)
            throws IOException, InterruptedException {
        if (packageSize < 16 || packageSize > 2048) {
            throw new IllegalArgumentException("package size needs to be bigger than 15 and smaller than 2049");
        }

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
     * @param console     Pi4j Console
     * @param packageSize Desired package size. Must be between 15 and 2049 bytes.
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
     * @param console         Pi4j Console
     * @param activateLogging Activates / deactivates logging.
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
     * @param console Pi4j Console
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(Console console) throws IOException, InterruptedException {
        this(console, 512, false);
    }

    /**
     * This method request a jpg image of the camera and returns it as a byte array.
     *
     * @return Byte array of the jpg image
     * @throws IOException
     * @throws InterruptedException
     */
    public byte[] getImageAsJpgBytes() throws IOException, InterruptedException {
        int pictureLength = getPictureLengthFromCamera();
        return getJpgBytes(pictureLength);
    }

    /**
     * Requests a jpg image from the camera and streams it to a file. The file name will be extended with a time stamp
     * and the .jpg file extension.
     *
     * @param fileName The desired file name.
     * @return Tile name of the image without file extension.
     * @throws IOException
     * @throws InterruptedException
     */
    public String saveImageAsJpg(String fileName) throws IOException, InterruptedException {
        int pictureLength = getPictureLengthFromCamera();
        return saveAsJpg(fileName, getJpgBytes(pictureLength));
    }

    /**
     * Requests a jpg image from the camera and streams it to a file. The file name will be extended with a time stamp
     * and the .jpg file extension.
     *
     * @param relativePath The desired relative path.
     * @param fileName     The desired file name.
     * @return Tile name of the image without file extension.
     * @throws IOException
     * @throws InterruptedException
     */
    public String saveImageAsJpg(String relativePath, String fileName) throws IOException, InterruptedException {
        String absolutePath = new File(relativePath).getAbsolutePath();
        logMessage("Absolute save location will be " + absolutePath);
        Path path = Paths.get(absolutePath);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        int pictureLength = getPictureLengthFromCamera();
        return saveAsJpg(absolutePath + "/" + fileName, getJpgBytes(pictureLength));
    }

    /**
     * This method initializes the serial bus communication for the grove serial camera.
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

    /**
     * Transmitting of the desired camera settings.
     */
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
     * Request image from the camera. Returns the assumed length of the picture.
     *
     * @return The length of the picture received by the serial camera.
     */
    private int getPictureLengthFromCamera() throws IOException, InterruptedException {
        int pictureLength = 0;

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

        return pictureLength;
    }

    /**
     * Request the picture from the camera package by package. Returns the image as a byte array.
     *
     * @param pictureLength Picture length according to serial camera.
     * @return The jpg image as a byte array.
     * @throws IOException
     * @throws InterruptedException
     */
    private byte[] getJpgBytes(int pictureLength) throws IOException, InterruptedException {
        byte[] receiveDataPackageCommand = { (byte) 0xaa, 0x0e, 0x00, 0x00, 0x00, 0x00 };
        byte[] ackPackageEndCommand = { (byte) 0xaa, 0x0e, 0x00, 0x00, (byte) 0xf0, (byte) 0xF0 };

        int successCount = 0;
        boolean pictureEnd = false;
        ByteArrayOutputStream camStream = new ByteArrayOutputStream();
        ByteBuffer imageBuffer = ByteBuffer.allocate(packageSize);

        while (!pictureEnd) {
            receiveDataPackageCommand[4] = (byte) successCount;
            receiveDataPackageCommand[5] = (byte) (successCount >> 8);

            serial.write(receiveDataPackageCommand);

            while (imageBuffer.position() < packageSize && !pictureEnd) {
                pictureEnd = lastPackageComplete(imageBuffer);
                if (!pictureEnd) {
                    Thread.sleep(10);
                    imageBuffer.put(serial.read());
                }
            }

            byte[] receivedData = imageBuffer.array();
            imageBuffer.position(0);
            byte calculatedCheckSum = getCheckSum(receivedData);
            byte receivedCheckSum = receivedData[receivedData.length - 1 + lowCheckSumBitPosition];

            if (calculatedCheckSum == receivedCheckSum || pictureEnd) {
                int byteCount = getIntegerFromBytes(receivedData[lowDataSizeBit], receivedData[highDataSizeBit]);
                lastWrittenByte = receivedData[receivedData.length - 3];
                camStream.write(receivedData, 4, byteCount);
                logMessage("package at " + successCount + " was handled successfully");
                successCount++;
            } else {
                logMessage("calculated check sum is " + calculatedCheckSum);
                logMessage("received check sum is " + receivedCheckSum);
                logMessage("package error at package " + successCount);
                logMessage(" => retry");
            }
        }

        logMessage("read a total of " + camStream.size() + " bytes");
        logMessage("pure picture length was " + pictureLength);
        logMessage("sending package end acknowledgement to camera");
        serial.write(ackPackageEndCommand);

        return camStream.toByteArray();
    }

    /**
     * Saves the byte array in a file with the file name specified.
     *
     * @param defaultFileName The desired file name.
     * @param imageBytes      The jpg image as a byte array.
     * @return The file name of the saved image.
     * @throws IOException
     */
    private String saveAsJpg(String defaultFileName, byte[] imageBytes) throws IOException {
        String fileName = getFileName(defaultFileName);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        ImageIO.write(image, "JPG", new File(fileName));
        logMessage("picture saved in file " + fileName);
        return fileName;
    }

    /**
     * Checks if the end tag of the jpg was received. JPG files end with 0xFF 0xD9.
     *
     * @param imageBuffer Byte buffer.
     * @return Boolean indicating if the last package was received.
     */
    private boolean lastPackageComplete(ByteBuffer imageBuffer) {
        if (imageBuffer.position() == nofNoDataBits + 1) {
            byte endTagD9 = imageBuffer.get(imageBuffer.position() - 3);
            return lastWrittenByte == (byte) 0xFF && endTagD9 == (byte) 0xD9;
        } else if (imageBuffer.position() > nofNoDataBits + 1) {
            byte endTagFF = imageBuffer.get(imageBuffer.position() - 4);
            byte endTagD9 = imageBuffer.get(imageBuffer.position() - 3);
            return endTagFF == (byte) 0xFF && endTagD9 == (byte) 0xD9;
        }

        return false;
    }

    /**
     * Calculates the check sum of a package according to the camera documentation.
     *
     * @param bytes The bytes of the package.
     * @return Byte of the calculated check sum.
     */
    private byte getCheckSum(byte[] bytes) {
        byte result = 0;
        for (byte b : Arrays.copyOf(bytes, bytes.length - 2)) {
            result += b;
        }
        return result;
    }

    /**
     * Converts a byte into an integer.
     *
     * @param low  Low byte
     * @param high High byte
     * @return Integer value of the byte.
     */
    private static int getIntegerFromBytes(byte low, byte high) {
        return (0xff & (byte) 0x00) << 24 | (0xff & (byte) 0x00) << 16 | (0xff & high) << 8 | (0xff & low) << 0;
    }

    /**
     * Adds a time stamp to the file name.
     *
     * @param defaultFileName The desired file name.
     * @return the desired file name extended with a time stamp an the .jpg extension.
     */
    private String getFileName(String defaultFileName) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return defaultFileName + formatter.format(now) + ".jpg";

    }

    /**
     * Reads all remaining bytes on the serial bus and disposes them.
     *
     * @throws IOException
     */
    private void clearDataFromSerialInput() throws IOException {
        int byteCount = serial.available();
        if (byteCount > 0) {
            serial.read(byteCount);
        }
    }

    /**
     * Logs a given message to the console if logging is activated.
     *
     * @param message The log message.
     */
    private void logMessage(String message) {
        if (logIsActive) {
            console.println(message);
        }
    }
}
