package templates;

import communication.message.impl.time.UTCTime;
import experiment.SleepAssistant;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class ThroughputSource extends SimpleSource<UTCTime> {
    private final SleepAssistant sleepAssistant;

    public ThroughputSource(Configuration configuration) {
        super(configuration);
        Object sleepMs = configuration.get("sleep_ms");
        sleepAssistant = new SleepAssistant((double) sleepMs);

        System.out.println("\nThroughputSource created with sleep time: " + sleepMs + " ms.");
    }

    @Override
    public UTCTime process() {
        sleepAssistant.maybeSleep();
        return new UTCTime();
    }
}
