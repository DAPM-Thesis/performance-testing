package templates;

import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import experiment.SleepAssistant;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class BackpressureSource extends SimpleSource<UTCTime> {
    private int counter = 0;
    private final long headStartSeconds;
    private boolean startedSleeping = false;
    Instant headstartEnd;
    private final ExperimentLogger logger;
    private final double sleepTimeMS;
    private final SleepAssistant sleepAssistant;

    public BackpressureSource(Configuration configuration) {
        super(configuration);
        this.headStartSeconds = ((Integer) configuration.get("head_start_seconds")).longValue();

        String sharedSavePath = "experiment_results/vms/backpressure/" + configuration.get("shared_save_file").toString();
        Path savePath = Paths.get(sharedSavePath).toAbsolutePath();
        this.logger = new ExperimentLogger(savePath);
        logger.log("--- EXPERIMENT ---");

        sleepTimeMS = 0.25;
        sleepAssistant = new SleepAssistant(sleepTimeMS);
    }

    @Override
    public UTCTime process() {
        counter++;
        sleepAssistant.maybeSleep();

        if (counter == 1) {
            System.out.println("BackpressureSource Started. Sleeping "+ sleepTimeMS + " ms between messages.");
            headstartEnd = Instant.now().plusSeconds(headStartSeconds);
        }

        if (Instant.now().isAfter(headstartEnd)) {
            if (!startedSleeping) {
                startedSleeping = true;
                logger.log("Source sent " + (counter - 1) + " messages in " + headStartSeconds + " seconds.");
                logger.log("Source started " + sleepTimeMS + " ms sleep at UTC: " + Instant.now() + '\n');
                System.out.println("BackpressureSource will start sleeping " + sleepTimeMS + " ms now.");
                System.out.println("Source sent " + (counter - 1) + " messages in " + headStartSeconds + " seconds.");
            }
        }

        return new UTCTime();
    }
}
