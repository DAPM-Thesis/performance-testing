package templates.scalability;

import communication.message.impl.time.UTCTime;
import experiment.SleepAssistant;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class TimeSource extends SimpleSource<UTCTime> {
    int counter = 0;
    private final SleepAssistant sleepAssistant;

    public TimeSource(Configuration configuration) {
        super(configuration);

        Object sleepMs = configuration.get("sleep_ms");
        sleepAssistant = new SleepAssistant((double) sleepMs);

        System.out.println("\nTimeSource created with sleep time: " + sleepMs + " ms.");
    }

    @Override
    public UTCTime process() {
        sleepAssistant.maybeSleep();
        return new UTCTime();
    }
}
