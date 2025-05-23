package templates;

import communication.message.Message;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.Sink;
import utils.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class BackpressureSink extends Sink {
    private boolean shouldSleep = true;
    private final long sleepTimeMs;
    private final long latency_threshold = 45;
    private int messageCounter = 0;
    private boolean caughtUp = false;

    private final ExperimentLogger sharedLogger;


    public BackpressureSink(Configuration configuration) {
        super(configuration);

        String sharedSavePathStr = "experiment_results/virtual_machine/backpressure/" + configuration.get("shared_save_file").toString();
        Path sharedSavePath = Paths.get(sharedSavePathStr).toAbsolutePath();
        this.sharedLogger = new ExperimentLogger(sharedSavePath, true);
        this.sleepTimeMs = 1000L * ((Integer) configuration.get("lag_seconds")).longValue();
    }

    @Override
    public void observe(Pair<Message,Integer> messageAndPort) {
        messageCounter++;

        if (shouldSleep) {
            shouldSleep = false;
            try { Thread.sleep(sleepTimeMs); }
            catch (InterruptedException e) { throw new RuntimeException(e); }

            String output = "BackpressureSink started processing at UTC: " + Instant.now();
            sharedLogger.log(output);
            System.out.println(output);
        }

        Instant timeSent = ((UTCTime) messageAndPort.first()).getTime();
        long latencyMs = Duration.between(timeSent, Instant.now()).toMillis();

        if (latencyMs <= latency_threshold && !caughtUp) {
            caughtUp = true;
            String output = "BackpressureSink caught up after " + messageCounter + " messages at UTC: " + Instant.now();
            sharedLogger.log(output + "\n");
            System.out.println(output);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
