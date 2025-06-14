package templates.backpressure;

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
    private final long latencyThresholdMs;
    private int messageCounter = 0;
    private boolean caughtUp = false;

    private final ExperimentLogger sharedLogger;


    public BackpressureSink(Configuration configuration) {
        super(configuration);

        String sharedSavePathStr = "experiment_results/vms_updated/backpressure/" + configuration.get("shared_save_file").toString();
        Path sharedSavePath = Paths.get(sharedSavePathStr).toAbsolutePath();
        this.sharedLogger = new ExperimentLogger(sharedSavePath, true);
        this.sleepTimeMs = 1000L * ((Integer) configuration.get("lag_seconds")).longValue();

        /*
          latency_threshold is set based on the mean latency and variance of the 0.33 ms sleep throughput/latency
          experiment results (3 runs):

          adjusted mean latency : [4.54 6.24 4.51] ms
          adjusted variance     : [577.94 479.93 442.24] ms
        * */
        latencyThresholdMs = (long) (5.1 + 500.0);
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

        if (messageCounter % 50000 == 0)
            { System.out.println("Latency after " + messageCounter + " messages: " + latencyMs + " ms."); }

        if (latencyMs <= latencyThresholdMs && !caughtUp) {
            caughtUp = true;
            String output = "BackpressureSink caught up after " + messageCounter + " messages at UTC: " + Instant.now();
            sharedLogger.log(output + "\n\n");
            System.out.println(output);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
