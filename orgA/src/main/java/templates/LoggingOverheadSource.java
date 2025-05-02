package templates;

import communication.message.impl.InstantTime;
import pipeline.processingelement.source.SimpleSource;

public class LoggingOverheadSource extends SimpleSource<InstantTime> {
    private int counter = 0;
    private final int messageCap = 500000;
    @Override
    public InstantTime process() {
        if (counter == 0) { System.out.println("Source Started."); }

        counter++;
        if (counter <= messageCap)
            { return new InstantTime(); }
        else {
            System.out.println("Source finished. Sent out " + messageCap + " messages.");
            System.exit(0);
        }
        throw new RuntimeException("Should not be reached.");
    }
}
