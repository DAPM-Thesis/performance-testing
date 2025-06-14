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
import java.util.HashMap;
import java.util.Map;

public class ThroughputSink extends Sink {
    private final ExperimentLogger logger;
    private final int logFrequency;
    private int counter = 0;

    public ThroughputSink(Configuration configuration) {
        super(configuration);
        String savePath = "experiment_results/vms_updated/throughput/" + configuration.get("save_file").toString();
        Object logFrequency = configuration.get("log_frequency");

        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());
        this.logFrequency = (logFrequency == null) ? 1 : (int) logFrequency;
    }

    @Override
    public void observe(Pair<Message, Integer> messageAndPort) {
        counter++;
        if (counter % logFrequency == 0) {
            Instant sentTime = ((UTCTime) messageAndPort.first()).getTime();
            long latencyNs = Duration.between(sentTime, Instant.now()).toNanos();
            logger.log(Long.toString(latencyNs/logFrequency));
        }

    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        Map<Class<? extends Message>, Integer> map = new HashMap<>();
        map.put(UTCTime.class, 1);
        return map;
    }
}
