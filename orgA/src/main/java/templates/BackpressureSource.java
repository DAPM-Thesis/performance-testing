package templates;

import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class BackpressureSource extends SimpleSource<UTCTime> {
    private int counter = 0;
    private final long headStartSeconds;
    private boolean startedSleeping = false;
    Instant sleepStart;
    private final ExperimentLogger logger;

    public BackpressureSource(Configuration configuration) {
        super(configuration);
        this.headStartSeconds = ((Integer) configuration.get("head_start_seconds")).longValue();

        String sharedSavePath = "experiment_results/virtual_machine/backpressure/" + configuration.get("shared_save_file").toString();
        Path savePath = Paths.get(sharedSavePath).toAbsolutePath();
        this.logger = new ExperimentLogger(savePath, true);
        logger.log("--- EXPERIMENT ---");
    }

    @Override
    public UTCTime process() {
        if (counter == 0) {
            System.out.println("BackpressureSource Started. No sleep yet.");
            sleepStart = Instant.now().plusSeconds(headStartSeconds);
        }

        counter++;
        if (Instant.now().isAfter(sleepStart)) {
            if (!startedSleeping) {
                startedSleeping = true;
                logger.log("Source sent " + (counter-1) + " messages in " + headStartSeconds + " seconds.");
                logger.log("Source started 1 ms sleep at UTC: " + Instant.now());
                System.out.println("BackpressureSource will start sleeping 1 ms now.");
            }
            try { Thread.sleep(1); }
            catch (InterruptedException e) { System.out.println("Source woke up from sleep."); }
        }
        return new UTCTime();
    }
}
