package templates;

import communication.message.Message;
import communication.message.impl.Time;
import experiment.ExperimentLogger;
import pipeline.processingelement.Sink;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ElapsedTimeSink extends Sink {
    private final ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/kafka_transfer_time/experiment_1.txt"
            ).toAbsolutePath());
    private final int nSamples = 10000;
    private int counter = 0;

    @Override
    public void observe(Message message, int i) {
        counter++;
        LocalDateTime receivedTime = LocalDateTime.now();
        LocalDateTime sentTime = ((Time) message).getTime();
        Duration elapsedTime = Duration.between(sentTime, receivedTime);
        long milliseconds = elapsedTime.toMillis();
        logger.log(Long.toString(milliseconds));

        if (counter % 500 == 0) {
            System.out.println(counter + " samples written.");
        }

        if (counter % nSamples == 0) {
            System.out.println("Wrote all samples.\nShutting down.");
            System.exit(0);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        Map<Class<? extends Message>, Integer> map = new HashMap<>();
        map.put(Time.class, 1);
        return map;
    }
}
