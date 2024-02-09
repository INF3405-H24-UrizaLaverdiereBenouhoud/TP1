package ca.qc.urizalaverdierebenouhoud.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class INF3405Logger extends Logger {

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    public INF3405Logger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new INF3405Formatter());
        addHandler(consoleHandler);
    }

    /**
     *  A custom formatter for the logger
     */
    private static class INF3405Formatter extends Formatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @Override
        public String format(LogRecord logRecord) {
            String color = "";
            switch (logRecord.getLevel().getName()) {
                case "INFO":
                    color = "\u001B[90m";
                    break;
                case "WARNING":
                    color = "\u001B[33m";
                    break;
                case "SEVERE":
                    color = "\u001B[31m";
                    break;
                default:
                    break;
            }
            String resetColor = "\u001B[0m";
            Date date = new Date(logRecord.getMillis());
            return color + "(" + this.dateFormat.format(date) + ") [" + logRecord.getLevel() + "] " + logRecord.getMessage() + resetColor + "\n";
        }
    }
}
