package templates.scalability;

import communication.message.impl.InstantTime;
import pipeline.processingelement.source.SimpleSource;

public class TimeSource extends SimpleSource<InstantTime> {
    int counter = 0;
    @Override
    public InstantTime process() {
        counter++;
        if (counter % 4 != 0) {
            try { Thread.sleep(1); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        return new InstantTime();
    }
}
