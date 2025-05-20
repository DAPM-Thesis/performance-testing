package templates;

import communication.message.impl.time.UTCTime;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class TimeSource extends SimpleSource<UTCTime> {
    private final int sleepFrequency;
    private final long sleepTimeMs;
    int counter = 0;

    public TimeSource(Configuration configuration) {
        super(configuration);
        this.sleepFrequency = (int) configuration.get("sleep_freq"); // when given value 4, then it will sleep every 4th observation.
        this.sleepTimeMs = ((Integer) configuration.get("sleep_time_ms")).longValue();
    }

    @Override
    public UTCTime process() {
        if (counter == 0) { System.out.println("ThroughputSource started with sleep time = " + sleepTimeMs + " ms."); }
        counter++;
        if (counter % sleepFrequency != 0) {
            try { Thread.sleep(sleepTimeMs); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new UTCTime();
    }
}
