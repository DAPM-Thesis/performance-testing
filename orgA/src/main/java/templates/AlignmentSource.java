package templates;

import communication.message.impl.event.Event;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.source.SimpleSource;

import java.nio.file.Paths;
import java.util.HashSet;

public class AlignmentSource extends SimpleSource<Event> {
    private int counter = 0;
    private final int messageCap;
    private final ExperimentLogger logger;

    public AlignmentSource(Configuration configuration) {
        super(configuration);
        this.messageCap = (int) configuration.get("message_send_count");

        String savePath = "experiment_results/alignment/" + configuration.get("save_file").toString();
        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());
    }

    @Override
    public Event process() {
        if (counter == 0) { System.out.println("AlignmentSource started. Messages to be sent: " + messageCap + '.'); }

        counter++;
        if (counter > messageCap) { // stop operation to allow sink to catch up
            System.out.println("AlignmentSource finished. Terminating.");
            terminate();
        }

        logger.log(String.valueOf(counter));

        return new Event(
                "alignment",
                String.valueOf(counter),
                "12:00",
                new HashSet<>());
    }
}
