package fhnwgpio.components.helper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger for logs in the Component classes
 */
public class ComponentLogger {
    private static final Logger logger = LogManager.getLogger("ComponentLogger");

    /**
     * logs a text with a loglevel and a message
     *
     * @param level   for the log
     * @param message text to log
     */
    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * logs a text with the loglevel Error
     *
     * @param message text to log
     */
    public static void logError(String message) {
        log(Level.ERROR, message);
    }

    /**
     * logs a text with the loglevel Info
     *
     * @param message text to log
     */
    public static void logInfo(String message) {
        log(Level.INFO, message);
    }

    /**
     * logs a text with the loglevel Debug
     *
     * @param message
     */
    public static void logDebug(String message) {
        log(Level.DEBUG, message);
    }
}
