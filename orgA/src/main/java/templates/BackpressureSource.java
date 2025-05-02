package templates;

import communication.message.impl.InstantTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.source.SimpleSource;

import java.nio.file.Paths;
import java.time.Instant;

public class BackpressureSource extends SimpleSource<InstantTime> {
    private int counter = 0;
    private long secondsBeforeSleep = 20;
    private boolean startedSleeping = false;
    Instant sleepStart;
    private final ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/backpressure/experiment_2.txt"
    ).toAbsolutePath());

    @Override
    public InstantTime process() {
        if (counter == 0) {
            System.out.println("Source Started. No sleep yet.");
            sleepStart = Instant.now().plusSeconds(secondsBeforeSleep);
        }

        counter++;
        if (Instant.now().isAfter(sleepStart)) {
            if (!startedSleeping) {
                startedSleeping = true;
                logger.log("Source sent " + String.valueOf(counter-1) + " messages in 20 seconds.");
                logger.log("Source started 1 ms sleep at UTC: " + Instant.now());
                System.out.println("Source will start sleeping 1 ms now.");
            }
            try { Thread.sleep(1); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new InstantTime();
    }
}
