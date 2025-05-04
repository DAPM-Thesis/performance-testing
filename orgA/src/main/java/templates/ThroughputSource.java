package templates;

import communication.message.impl.time.UTCTime;
import pipeline.processingelement.source.SimpleSource;

public class ThroughputSource extends SimpleSource<UTCTime> {
    /** Sends out the current LocalDateTime as a message. */
    private int counter = 0;
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
