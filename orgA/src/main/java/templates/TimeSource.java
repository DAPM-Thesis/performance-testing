package templates;

import communication.message.impl.Time;
import pipeline.processingelement.Source;

public class TimeSource extends Source<Time> {

    /** Sends out the current LocalDateTime as a message. */
    @Override
    public Time process() {
        return new Time();
    }
}
