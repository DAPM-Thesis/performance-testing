package templates;

import communication.message.Message;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Sink;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ThroughputSink extends Sink {
    private final ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/DELETE/DELETE.txt"
            ).toAbsolutePath()); // currently set to DELETE.txt in case pipeline is run just to see if it compiles (and so no tests are overwritten)
    private Instant firstReceivedTime = null;
    private Instant deadline;
    private final int experimentLengthSeconds = 60 * 5;
    private int counter = 0;

    @Override
    public void observe(Message message, int i) {
        if (firstReceivedTime == null) {
            System.out.println("Received first message.");
            firstReceivedTime = Instant.now();
            deadline = firstReceivedTime.plusSeconds(experimentLengthSeconds);
        }

        Instant receivedTime = Instant.now();
        counter++;
        if (counter % 10000 == 0) {
            Instant sentTime = ((UTCTime) message).getTime();
            Duration elapsedTime = Duration.between(sentTime, receivedTime);
            long elapsedNanoseconds = elapsedTime.toNanos();
            logger.log(Long.toString(elapsedNanoseconds));
        }
        if (receivedTime.isAfter(deadline)) {
            System.out.println("Finished experiment after " + experimentLengthSeconds + " seconds.");
            System.out.println("Shutting down.");
            System.exit(0);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        Map<Class<? extends Message>, Integer> map = new HashMap<>();
        map.put(UTCTime.class, 1);
        return map;
    }
}
