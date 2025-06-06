package templates.logging_overhead;

import communication.message.impl.time.UTCTime;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

public class LoggingOverheadSource extends SimpleSource<UTCTime> {
    private int counter = 0;
    private final int messageCap;

    public LoggingOverheadSource(Configuration configuration) {
        super(configuration);
        this.messageCap = (int) configuration.get("n_messages");
    }

    @Override
    public UTCTime process() {
        if (counter == 0) { System.out.println("Source Started."); }

        counter++;
        if (counter > messageCap) {
            System.out.println("LoggingOverheadSource finished. Sent out " + messageCap + " messages.");
            try { Thread.sleep(5000); }
            catch (Exception e) { System.out.println("LoggingOverheadSource woke up"); }
        }

        return new UTCTime();
    }
}
