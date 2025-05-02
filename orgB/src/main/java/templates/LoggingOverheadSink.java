package templates;

import communication.message.Message;
import communication.message.impl.InstantTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Sink;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class LoggingOverheadSink extends Sink {
    private int counter = 0;
    private final int messageCap = 500000;
    private final ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/logging_overhead/experiment_1.txt"
    ).toAbsolutePath());
    private final ExperimentLogger overheadLogger = new ExperimentLogger(Paths.get(
            "experiment_results/logging_overhead/logged_count_for_experiment_1.txt"
    ).toAbsolutePath());
    Instant startTime;

    @Override
    public void observe(Message message, int i) {
        if (counter == 0) {
            startTime = Instant.now();
            System.out.println("Sink Started.");
        }

        counter++;
        if (counter % 10000 == 0) { System.out.println("Sink processed " + counter + " messages."); }

        overheadLogger.log(String.valueOf(counter));
        if (counter == messageCap) {
            long processingTime = Duration.between(startTime, Instant.now()).toMillis();
            String logMessage = "With logging every message: processed " + messageCap + " messages in " + processingTime + " ms";
            logger.log(logMessage);
            System.out.println("Sink finished processing all messages.");
            System.exit(0);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(InstantTime.class, 1);
    }
}
