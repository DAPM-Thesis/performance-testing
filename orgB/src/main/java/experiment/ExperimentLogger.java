package experiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExperimentLogger {
    private final Path logFilePath;

    public ExperimentLogger(Path logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void log(String string) {
        try {
            // Create the logFilePath if it doesn't exist
            Path parentDir = logFilePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            // Write/appends the input string as a new line
            Files.writeString(
                    logFilePath,
                    string + System.lineSeparator(),
                    java.nio.charset.StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
            );
        } catch (IOException e) { e.printStackTrace(); }
    }
}