package fhnwgpio.components.helper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComponentLogger {
    private static final Logger logger = LogManager.getLogger("ComponentLogger");

    public static void log(Level level, String message) {
        logger.log(level, message);
    }
}
