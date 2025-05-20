package templates;

import communication.message.impl.time.UTCTime;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class ThroughputSource extends SimpleSource<UTCTime> {
    /** Sends out the current LocalDateTime as a message. */
    private int counter = 0;
    private Long sleepTime;
    private int sleepFrequency;

    public ThroughputSource(Configuration configuration) {
        super(configuration);

        this.sleepTime = 1L;
        this.sleepFrequency = 1;
        if (configuration.get("sleep_ms") != null) {
            if (configuration.get("sleep_ms") instanceof Double d) {
                this.sleepFrequency = (Math.abs(d - 0.75) < 0.01) ? 4 : 2;
            } else if (configuration.get("sleep_ms") instanceof Integer i) {
                this.sleepTime = (long) i;
            }
        }
    }

    @Override
    public UTCTime process() {
        counter++;
        if (counter % 3 == 0) {
            try { Thread.sleep(1); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new UTCTime();
    }
}
