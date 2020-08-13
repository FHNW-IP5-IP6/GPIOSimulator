package fhnwgpio.components;

import com.pi4j.io.serial.*;
import fhnwgpio.components.helper.ComponentLogger;

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

/**
 * FHNW implementation for the grove serial camera. This class implements the communication protocol and simplifies
 * accessing the grove serial camera via the built-in default serial bus of the Raspberry Pi.
 * grove Serial Camera: https://wiki.seeedstudio.com/Grove-Serial_Camera_Kit/
 * Documentation: https://files.seeedstudio.com/wiki/Grove-Serial_Camera_Kit/res/cj-ov528_protocol.pdf
 */
public class SerialCameraComponent {
    private Serial serial = null;
    private int packageSize;
    private int nofNoDataBits = 6;
    private int lowDataSizeBit = 2;
    private int highDataSizeBit = 3;
    private int lowCheckSumBitPosition = -1;
    private int syncRetryCount = 64;
    private byte lastWrittenByte = (byte) 0x00;

    /**
     * Constructor of the SerialCameraComponent. Configures the default serial port of the Raspberry Pi.
     *
     * @param packageSize Desired package size. Must be between 15 and 2049 bytes.
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent(int packageSize) throws IOException, InterruptedException {
        if (packageSize < 16 || packageSize > 2048) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "SerialCameraComponent: Package size needs to be bigger than 15 and smaller than 2049");
            ComponentLogger.logError(exception.getMessage());
            throw exception;
        }

        this.packageSize = packageSize;
        serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();
        config.device(SerialPort.getDefaultPort()).baud(Baud._9600);
        serial.open(config);

        ComponentLogger.logInfo("SerialCameraComponent: SerialCamera created with a packageSize of " + packageSize);
        initializeSerialBusCommunication();
        sendSettingsToCamera();
    }

    /**
     * Constructor of the SerialCameraComponent with a default package size of 512 bytes. Configures the
     * default serial port of the Raspberry Pi.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public SerialCameraComponent() throws IOException, InterruptedException {
        this(512);
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
        ComponentLogger.logInfo("SerialCameraComponent: The absolute save location will be " + absolutePath);
        Path path = Paths.get(absolutePath);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        int pictureLength = getPictureLengthFromCamera();
        return saveAsJpg(absolutePath + "/" + fileName, getJpgBytes(pictureLength));
    }

    /**
     * This method initializes the serial bus communication for the grove serial camera.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    // tag::SerialCamInit[]
    private void initializeSerialBusCommunication() throws InterruptedException, IOException {
        try {
            int tryCount = 0;
            byte[] syncCommand = { (byte) 0xaa, 0x0d, 0x00, 0x00, 0x00, 0x00 };
            byte[] ackCommand = { (byte) 0xaa, 0x0e, 0x0d, 0x00, 0x00, 0x00 };

            ComponentLogger.logInfo("SerialCameraComponent: Initializing communication with the camera");

            while (serial.available() == 0 && tryCount < syncRetryCount) {
                serial.write(syncCommand);
                tryCount++;
                Thread.sleep(100);
            }

            if (tryCount >= syncRetryCount) {
                IOException exception = new IOException(
                        "SerialCameraComponent: Tried to sync with camera for " + tryCount + " without success");
                ComponentLogger.logError(exception.getMessage());
                throw exception;
            }

            ComponentLogger.logInfo("SerialCameraComponent: Camera responded after " + tryCount + " requests");
            byte[] bytes = serial.read(6);
            clearDataFromSerialInput();

            if (bytes[0] == 0xaa && bytes[1] == 0x0e && bytes[2] == 0x0d && bytes[4] == 0x00 && bytes[5] == 0x00) {
                ComponentLogger.logInfo("SerialCameraComponent: Received response is a valid acknowledgement");
                ComponentLogger.logInfo("SerialCameraComponent: Waiting for sync from camera");
                bytes = serial.read(6);

                if (bytes[0] == 0xaa && bytes[1] == 0x0d && bytes[2] == 0x00 && bytes[3] == 0x00 && bytes[4] == 0x00
                        && bytes[5] == 0x00) {
                    ComponentLogger.logInfo("SerialCameraComponent: Received sync from the camera");
                    ComponentLogger.logInfo("SerialCameraComponent: Sending sync acknowledgement to camera");
                    serial.write(ackCommand);
                    ComponentLogger.logInfo("SerialCameraComponent: Serial bus communication ready");
                }
            }

            clearDataFromSerialInput();
        } catch (Exception ex) {
            ComponentLogger.logError("SerialCameraComponent: " + ex.getMessage());
            throw ex;
        }
    }
    // end::SerialCamInit[]

    /**
     * Transmitting of the desired camera settings.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void sendSettingsToCamera() throws InterruptedException, IOException {
        try {
            byte[] initialCommand = { (byte) 0xaa, 0x01, 0x00, 0x07, 0x03, 0x07 };

            ComponentLogger.logInfo("SerialCameraComponent: Sending settings to the camera");
            serial.write(initialCommand);

            while (serial.available() < 6) {
                Thread.sleep(10);
            }

            ComponentLogger.logInfo("SerialCameraComponent: Received a response from the camera");
            byte[] bytes = serial.read(6);

            if (bytes[0] == 0xaa && bytes[1] == 0x0e && bytes[2] == 0x01 && bytes[4] == 0x00 && bytes[5] == 0x00) {
                ComponentLogger.logInfo("SerialCameraComponent: Received response is a valid settings acknowledgement");
                ComponentLogger.logInfo("SerialCameraComponent: Settings where successfully sent to the camera");
            }

            clearDataFromSerialInput();
        } catch (Exception ex) {
            ComponentLogger.logError("SerialCameraComponent: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Request image from the camera. Returns the assumed length of the picture.
     *
     * @return The length of the picture received by the serial camera.
     */
    // tag::SerialCamGetPictureLength[]
    private int getPictureLengthFromCamera() throws IOException, InterruptedException {
        int pictureLength = 0;

        byte[] setPackageSizeCommand = { (byte) 0xaa, 0x06, 0x08, (byte) (packageSize & 0xff),
                (byte) ((packageSize >> 8) & 0xff), 0x00 };
        byte[] snapshotCommand = { (byte) 0xaa, 0x05, 0x00, 0x00, 0x00, 0x00 };
        byte[] getPictureCommand = { (byte) 0xaa, 0x04, 0x01, 0x00, 0x00, 0x00 };

        ComponentLogger.logInfo("SerialCameraComponent: Sending the desired package size to the camera");
        serial.write(setPackageSizeCommand);

        while (serial.available() < 6) {
            Thread.sleep(10);
        }

        ComponentLogger.logInfo("SerialCameraComponent: Received a response from the camera");
        byte[] bytes = serial.read(6);

        if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x06 && bytes[4] == (byte) 0x00
                && bytes[5] == (byte) 0x00) {
            ComponentLogger.logInfo("SerialCameraComponent: Response was a valid package size acknowledgement");
        }

        ComponentLogger.logInfo("SerialCameraComponent: Sending snapshot command");
        serial.write(snapshotCommand);

        while (serial.available() < 6) {
            Thread.sleep(10);
        }

        ComponentLogger.logInfo("SerialCameraComponent: Received a response from the camera");
        bytes = serial.read(6);

        if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x05 && bytes[4] == (byte) 0x00
                && bytes[5] == (byte) 0x00) {
            ComponentLogger.logInfo("SerialCameraComponent: Response was a valid snapshot acknowledgement");
        }

        ComponentLogger.logInfo("SerialCameraComponent: Sending get picture length command");
        serial.write(getPictureCommand);

        while (serial.available() < 6) {
            Thread.sleep(10);
        }

        ComponentLogger.logInfo("SerialCameraComponent: Received a response from the camera");
        bytes = serial.read(6);

        if (bytes[0] == (byte) 0xaa && bytes[1] == (byte) 0x0e && bytes[2] == (byte) 0x04 && bytes[4] == (byte) 0x00
                && bytes[5] == (byte) 0x00) {
            ComponentLogger.logInfo("SerialCameraComponent: Response was a valid picture length acknowledgement");
            ComponentLogger.logInfo("SerialCameraComponent: Reading the next 6 bytes to get the picture length");
            bytes = serial.read(6);

            if (bytes[0] == (byte) 0xaa && bytes[1] == 0x0a && bytes[2] == 0x01) {
                pictureLength = (int) bytes[3] + (bytes[4] << 8) + (bytes[5] << 16);
                ComponentLogger.logInfo("SerialCameraComponent: Picture length is: " + pictureLength);
            }
        }

        return pictureLength;
    }
    // end::SerialCamGetPictureLength[]

    /**
     * Request the picture from the camera package by package. Returns the image as a byte array.
     *
     * @param pictureLength Picture length according to serial camera.
     * @return The jpg image as a byte array.
     * @throws IOException
     * @throws InterruptedException
     */
    // tag::SerialCamGetPicture[]
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
                ComponentLogger.logInfo("SerialCameraComponent: Package at " + successCount + " successfully handled");
                successCount++;
            } else {
                ComponentLogger.logInfo("SerialCameraComponent: Calculated check sum is " + calculatedCheckSum);
                ComponentLogger.logInfo("SerialCameraComponent: Received check sum is " + receivedCheckSum);
                ComponentLogger.logInfo("SerialCameraComponent: Package error at package " + successCount);
                ComponentLogger.logInfo("SerialCameraComponent:  => retry");
            }
        }

        ComponentLogger.logInfo("SerialCameraComponent: Read a total of " + camStream.size() + " bytes");
        ComponentLogger.logInfo("SerialCameraComponent: Pure picture length was " + pictureLength);
        ComponentLogger.logInfo("SerialCameraComponent: Sending package end acknowledgement to camera");
        serial.write(ackPackageEndCommand);

        return camStream.toByteArray();
    }
    // end::SerialCamGetPicture[]

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
        ComponentLogger.logInfo("SerialCameraComponent: Picture saved in file " + fileName);
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
}
