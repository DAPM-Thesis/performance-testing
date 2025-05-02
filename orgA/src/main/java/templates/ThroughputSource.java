package templates;

import communication.message.impl.InstantTime;
import pipeline.processingelement.source.SimpleSource;

public class ThroughputSource extends SimpleSource<InstantTime> {
    /** Sends out the current LocalDateTime as a message. */
    private int counter = 0;
    @Override
    public InstantTime process() {
        counter++;
        if (counter % 3 == 0) {
            try { Thread.sleep(1); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new InstantTime();
    }
}
