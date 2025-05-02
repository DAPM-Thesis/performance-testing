package templates;

import communication.message.impl.event.Event;
import experiment.ExperimentLogger;
import pipeline.processingelement.Source;

import java.nio.file.Paths;
import java.util.HashSet;

public class AlignmentSource extends Source<Event> {
    private int counter = 0;
    private int messageCap = 5200;
    private final ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/alignment/source_experiment_2.txt"
    ).toAbsolutePath());
    @Override
    public Event process() {
        if (counter == 0) { System.out.println("Source Started. Message cap: " + messageCap + '.'); }

        counter++;
        if (counter > 5200) { // stop operation to allow sink to catch up
            System.out.println("Source finished.\nSleeping.");
            try { Thread.sleep(20 * 1000); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        logger.log(String.valueOf(counter));
        return new Event(
                "alignment",
                String.valueOf(counter),
                "12:00",
                new HashSet<>());
    }
}
