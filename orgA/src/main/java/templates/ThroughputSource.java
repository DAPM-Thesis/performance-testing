package templates;

import communication.message.impl.time.UTCTime;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class ThroughputSource extends SimpleSource<UTCTime> {
    /** Sends out the current LocalDateTime as a message. */
    private int counter = 0;
    private final Long sleepTime;
    private int sleepFrequency;

    public ThroughputSource(Configuration configuration) {
        super(configuration);

        Object sleepMs = configuration.get("sleep_ms");
        this.sleepTime = (sleepMs instanceof Double) ? 1 : (long) sleepMs;
        this.sleepFrequency = 1;
        if (sleepMs instanceof Double d) {
            this.sleepFrequency = (Math.abs(d - 0.75) < 0.01) ? 4 : 2;
        }
    }

    @Override
    public UTCTime process() {
        counter++;
        if (counter % sleepFrequency == 0) {
            try { Thread.sleep(sleepTime); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new UTCTime();
    }
}
