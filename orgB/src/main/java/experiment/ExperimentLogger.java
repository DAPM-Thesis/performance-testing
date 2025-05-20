package experiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExperimentLogger {
    private final Path logFilePath;

    public ExperimentLogger(Path inputPath) {
        this.logFilePath = resolveUniquePath(inputPath);
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

    // made with ChatGPT: adds a number suffix for unique file names
    private static Path resolveUniquePath(Path inputPath) {
        String fileName = inputPath.getFileName().toString();
        Path parent = inputPath.getParent();

        String namePart, extPart;
        int dotIdx = fileName.lastIndexOf(".");
        if (dotIdx != -1) {
            namePart = fileName.substring(0, dotIdx);
            extPart = fileName.substring(dotIdx);
        } else {
            namePart = fileName;
            extPart = "";
        }

        // Pattern: ends with _number
        String regex = "^(.*)_(\\d+)$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(namePart);

        String baseName;
        int counter;
        if (matcher.matches()) {
            baseName = matcher.group(1);
            counter = Integer.parseInt(matcher.group(2));
        } else {
            baseName = namePart;
            counter = 1;
        }

        Path candidate;
        while (true) {
            String candidateName = baseName + "_" + counter + extPart;
            candidate = parent == null ? Path.of(candidateName) : parent.resolve(candidateName);
            if (Files.notExists(candidate)) {
                return candidate;
            }
            counter++;
        }
    }

}