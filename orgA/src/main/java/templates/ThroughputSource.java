package templates;

import communication.message.impl.time.UTCTime;
import experiment.SleepAssistant;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class ThroughputSource extends SimpleSource<UTCTime> {
    private final SleepAssistant sleepAssistant;

    public ThroughputSource(Configuration configuration) {
        super(configuration);
        Object sleepMsRaw = configuration.get("sleep_ms");
        double sleepMs = sleepMsRaw instanceof Integer ? ((Integer) sleepMsRaw).doubleValue() : (Double) sleepMsRaw;
        sleepAssistant = new SleepAssistant(sleepMs);

        System.out.println("\nThroughputSource created with sleep time: " + sleepMs + " ms.");
    }

    @Override
    public UTCTime process() {
        sleepAssistant.maybeSleep();
        return new UTCTime();
    }
}
