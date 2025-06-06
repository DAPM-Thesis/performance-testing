package templates;

import communication.message.Message;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.Sink;
import utils.Pair;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class KafkaOverheadSink extends Sink {
    private final ExperimentLogger logger;
    private final int logFrequency;
    private int counter = 0;

    public KafkaOverheadSink(Configuration configuration) {
        super(configuration);

        String savePath = "experiment_results/vms_updated/kafka_overhead/" + configuration.get("save_file").toString();
        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());

        logFrequency = (int) configuration.get("log_frequency");
    }

    @Override
    public void observe(Pair<Message, Integer> messageAndPort) {
        Instant receivedTime = Instant.now();
        Instant sentTime = ((UTCTime) messageAndPort.first()).getTime();
        long latencyNs = Duration.between(sentTime, receivedTime).toNanos();
        counter++;

        if (counter % logFrequency == 0) { logger.log(String.valueOf(latencyNs)); }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
