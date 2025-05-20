package templates;

import communication.message.Message;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.Sink;
import utils.Pair;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class BackpressureSink extends Sink {
    private boolean shouldSleep = true;
    private long sleepTimeMs = 20 * 1000;
    private long meanLatencyMs_1MsSleep = 45;
    private int messageCounter = 0;

    private final ExperimentLogger sharedLogger = new ExperimentLogger(Paths.get(
            "experiment_results/backpressure/experiment_2.txt"
    ).toAbsolutePath());

    private final ExperimentLogger latencyLogger = new ExperimentLogger(Paths.get(
            "experiment_results/backpressure/latency_of_experiment_2.txt"
    ).toAbsolutePath());

    public BackpressureSink(Configuration configuration) {
        super(configuration);
    }

    @Override
    public void observe(Pair<Message,Integer> messageAndPort) {
        if (shouldSleep) {
            try { Thread.sleep(sleepTimeMs); } catch (InterruptedException e) { throw new RuntimeException(e); }
            shouldSleep = false;
            sharedLogger.log("Sink started processing at UTC: " + Instant.now());
            System.out.println("Sink started processing at UTC: " + Instant.now());
        }

        Instant sent = ((UTCTime) messageAndPort.first()).getTime();
        Instant received = Instant.now();
        long latencyMs = Duration.between(sent, received).toMillis();
        if (messageCounter++ % 2500 == 0) {
            latencyLogger.log(Long.toString(latencyMs));
        }
        if (latencyMs <= meanLatencyMs_1MsSleep) {
            sharedLogger.log("Sink caught up at UTC: " + Instant.now());
            System.out.println("Sink caught up at UTC: " + Instant.now());
            System.exit(0);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
