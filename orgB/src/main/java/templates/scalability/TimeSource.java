package templates.scalability;

import communication.message.impl.time.UTCTime;
import pipeline.processingelement.source.SimpleSource;

public class TimeSource extends SimpleSource<UTCTime> {
    int counter = 0;
    @Override
    public UTCTime process() {
        counter++;
        if (counter % 4 != 0) {
            try { Thread.sleep(1); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new UTCTime();
    }
}
