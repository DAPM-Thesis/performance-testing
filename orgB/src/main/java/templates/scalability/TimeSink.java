package templates.scalability;

import communication.message.Message;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Sink;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class TimeSink extends Sink {
    private final ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/scalability/experiment_1.txt"
    ).toAbsolutePath());
    private boolean hasStarted = false;
    private Instant deadline;
    private long experimentLengthSeconds = 60 * 5;

    @Override
    public void observe(Message message, int i) {
        if (!hasStarted) {
            hasStarted = true;
            deadline = Instant.now().plusSeconds(experimentLengthSeconds);
            System.out.println("Sink started. Stopping after " + experimentLengthSeconds + " seconds.");
        }
        Instant receivedTime = Instant.now();
        Instant sentTime = ((UTCTime) message).getTime();
        long latencyNs = Duration.between(sentTime, receivedTime).toNanos();
        String logInfo = String.valueOf(i) + ':' + latencyNs;
        logger.log(logInfo);

        if (receivedTime.isAfter(deadline)) {
            System.out.println("Finished experiment after " + experimentLengthSeconds + " seconds.");
            System.out.println("Shutting down.");
            System.exit(0);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 2);
    }
}
