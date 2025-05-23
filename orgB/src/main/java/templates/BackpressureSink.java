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
    private boolean caughtUp = false;

    private final ExperimentLogger sharedLogger;


    public BackpressureSink(Configuration configuration) {
        super(configuration);

        String sharedSavePath = "experiment_results/virtual_machine/backpressure/" + configuration.get("shared_save_file").toString();
        this.sharedLogger = new ExperimentLogger(Paths.get(sharedSavePath).toAbsolutePath());
        this.sleepTimeMs = 1000L * ((Integer) configuration.get("lag_seconds")).longValue();

        // add counter that can see how many messages were sent before it caught up.

    }

    @Override
    public void observe(Pair<Message,Integer> messageAndPort) {
        if (shouldSleep) {
            try { Thread.sleep(sleepTimeMs); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
            shouldSleep = false;
            String output = "BackpressureSink started processing at UTC: " + Instant.now();
            sharedLogger.log(output);
            System.out.println(output);
        }

        messageCounter++;

        Instant sent = ((UTCTime) messageAndPort.first()).getTime();
        Instant received = Instant.now();
        long latencyMs = Duration.between(sent, received).toMillis();
        if (latencyMs <= meanLatencyMs_1MsSleep && !caughtUp) {
            String output = "BackpressureSink caught up after " + messageCounter + " messages at UTC: " + Instant.now();
            sharedLogger.log(output + "\n");
            System.out.println(output);
            caughtUp = true;
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
