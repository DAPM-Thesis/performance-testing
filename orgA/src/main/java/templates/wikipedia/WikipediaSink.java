package templates.wikipedia;

import communication.message.Message;
import communication.message.impl.event.Event;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.Sink;
import utils.Pair;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class WikipediaSink extends Sink {
    private boolean hasBegun = false;
    private final Duration experimentDuration;
    private final ExperimentLogger logger;
    private Instant deadline;

    public WikipediaSink(Configuration configuration) {
        super(configuration);
        String savePath = "experiment_results/vms_updated/wikipedia/latency.txt";
        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());
        experimentDuration = Duration.ofMinutes(5);
    }

    @Override
    public void observe(Pair<Message, Integer> eventAndPortNumber) {
        if (!hasBegun) {
            hasBegun = true;
            deadline = Instant.now().plus(experimentDuration);
            System.out.println("\nSink received first event\n");
        }

        if (Instant.now().isAfter(deadline)) {
            System.out.println("Experiment ended, sink sleeping.");
            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); }
        } else {
            Event event = (Event) eventAndPortNumber.first();
            UTCTime sentTime = (UTCTime) event.getAttributes().stream()
                    .filter(a -> a.getName().equals("sent time"))
                    .findFirst()
                    .get().getValue();

            logger.log(Long.toString(Duration.between(sentTime.getTime(), Instant.now()).toNanos()));
        }

    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(Event.class, 1);
    }
}
