package logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "system.log";
    private static Logger instance;
    private PrintWriter writer;

    // Singleton pattern to ensure only one logger instance
    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true), true);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    // Get the single instance of the logger
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    // Log a message to the console and the log file
    public synchronized void log(String message) {
        String timestamp = getCurrentTimestamp();
        String logMessage = "[" + timestamp + "] " + message;

        // Log to the console
        System.out.println(logMessage);

        // Log to the file
        if (writer != null) {
            writer.println(logMessage);
        }
    }

    // Close the logger to release resources
    public synchronized void close() {
        if (writer != null) {
            writer.close();
        }
    }

    // Utility method to get the current timestamp
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}