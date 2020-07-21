package fhnwgpio.components.helper;

import java.util.*;

/**
 * This class is used to handle the raspivid commands for taking videos.
 * Here's a list of the raspivid commands
 * https://www.raspberrypi.org/documentation/usage/camera/raspicam/raspivid.md
 * <p>
 * the syntax and usage is similar to the CameraConfiguration of the picam library for raspistill.
 */
public class RaspiVidConfiguration {
    private HashMap<String, String> commands;

    /**
     * New Configuration without any changes will save a video with the default settings to the path "/home/pi/Videos/video.h264"
     */
    public RaspiVidConfiguration() {
        commands = new HashMap<>();
        output("/home/pi/Videos/video.h264");
    }

    /**
     * default is 5 seconds
     *
     * @param time length of the video in milliseconds
     */
    public RaspiVidConfiguration time(long time) {
        commands.put("time", "-t " + time);
        return this;
    }

    /**
     * @param path path to the file that should hold the video
     */
    public RaspiVidConfiguration output(String path) {
        commands.put("output", "-o " + path);
        return this;
    }

    /**
     * default width is 1920
     * @param w width of the video
     */
    public RaspiVidConfiguration width(int w) {
        if (w < 64 || w > 1920)
            throw new IllegalArgumentException("width must be in the range 64 - 1920");

        commands.put("width", "-w " + w);
        return this;
    }

    /**
     * default height is 1080
     * @param h height of the video
     */
    public RaspiVidConfiguration height(int h) {
        if (h < 64 || h > 1080)
            throw new IllegalArgumentException("height must be in the range 64 - 1920");

        commands.put("height", "-h " + h);
        return this;
    }

    /**
     * horizontally flips the video
     */
    public RaspiVidConfiguration horizontalFlip() {
        commands.put("flip", "-hf");
        return this;
    }

    /**
     * vertically flips the video
     */
    public RaspiVidConfiguration verticalflip() {
        commands.put("flip", "-vf");
        return this;
    }

    /**
     * disables the preview window settings
     */
    public RaspiVidConfiguration previewOff() {
        commands.put("preview", "-n");
        return this;
    }

    /**
     * enables the preview with maximum window settings
     */
    public RaspiVidConfiguration previewFullscreen() {
        commands.put("preview", "-f");
        return this;
    }

    /**
     * enables the preview with own parameters
     *
     * @param x x position for preview
     * @param y y position for preview
     * @param w width for preview
     * @param h height for preview
     */
    public RaspiVidConfiguration preview(int x, int y, int w, int h) {
        if (x < 0 || x > w)
            throw new IllegalArgumentException("x must be in the range 0 - w");
        if (y < 0 || y > h)
            throw new IllegalArgumentException("x must be in the range 0 - h");
        if (w < 64 || w > getWidth())
            throw new IllegalArgumentException("width must be in the range 64 - specified width or default 1920");
        if (h < 64 || h > getHeight())
            throw new IllegalArgumentException("height must be in the range 64 - specified height or default 1080");

        commands.put("preview", "-p " + x + "," + y + "," + w + "," + h);
        return this;
    }

    public RaspiVidConfiguration videoStabilisation() {
        commands.put("stabilisation", "-vs");
        return this;
    }

    /**
     * @param fps frames per second for the video
     */
    public RaspiVidConfiguration framerate(int fps) {
        if (fps < 2 || fps > 30) {
            throw new IllegalArgumentException("FPS must be in the range 2 to 30");
        }
        commands.put("framerate", "-fps " + fps);
        return this;
    }

    /**
     * @param sharpness defines sharpness in range of
     */
    public RaspiVidConfiguration sharpness(int sharpness) {
        if (sharpness < -100 || sharpness > 100) {
            throw new IllegalArgumentException("sharpness must be in the range -100 to 100");
        }
        commands.put("sharpness", "-sh " + sharpness);
        return this;
    }

    /**
     * @param constrast contrast in range of -100 to 100
     */
    public RaspiVidConfiguration contrast(int constrast) {
        if (constrast < -100 || constrast > 100) {
            throw new IllegalArgumentException("constrast must be in the range -100 to 100");
        }
        commands.put("framerate", "-co " + constrast);
        return this;
    }

    /**
     * @param brightness in range of 0 to 100
     */
    public RaspiVidConfiguration brightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("brightness must be in the range 0 to 100");
        }
        commands.put("brightness", "-br " + brightness);
        return this;
    }

    /**
     * @param saturation in range of -100 to 100
     */
    public RaspiVidConfiguration saturation(int saturation) {
        if (saturation < -100 || saturation > 100) {
            throw new IllegalArgumentException("saturation must be in the range -100 to 100");
        }
        commands.put("saturation", "-sa " + saturation);
        return this;
    }

    /**
     * default bitrate is 10MBit (10_000_000 Bits)
     * @param bitrate of the video in bits
     */
    public RaspiVidConfiguration bitrate(long bitrate) {
        if (bitrate < 1_000_000 || bitrate > 25_000_000) {
            throw new IllegalArgumentException("Bitrate must be in the range 2 to 30");
        }
        commands.put("bitrate", "-bitrate " + bitrate);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : commands.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getValue());
                sb.append(" ");
            }
        }

        sb.insert(0, "raspivid ");
        return sb.toString();
    }

    public long getTime() {
        String command = commands.get("time");
        if (command == null) return 5000; //default if nothing was specified

        command = command.substring(3);
        return Long.parseLong(command);
    }

    public int getHeight(){
        String command = commands.get("height");
        if (command == null) return 1080; //default if nothing was specified

        //parse the number after -h
        return Integer.parseInt(command.substring(3));
    }

    public int getWidth(){
        String command = commands.get("width");
        if (command == null) return 1920; //default if nothing was specified

        //parse the number after -w
        return Integer.parseInt(command.substring(3));
    }
}