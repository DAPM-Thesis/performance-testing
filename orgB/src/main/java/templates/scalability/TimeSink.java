package templates.scalability;

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

public class TimeSink extends Sink {
    private final ExperimentLogger logger;

    public TimeSink(Configuration configuration) {
        super(configuration);

        String savePath = "experiment_results/vms/scalability/" + configuration.get("save_file").toString();
        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());
    }

    @Override
    public void observe(Pair<Message, Integer> messageAndPort) {
        Instant receivedTime = Instant.now();
        Instant sentTime = ((UTCTime) messageAndPort.first()).getTime();
        long latencyNs = Duration.between(sentTime, receivedTime).toNanos();
        String logInfo = String.valueOf(messageAndPort.second()) + ':' + latencyNs;
        logger.log(logInfo);
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 2);
    }
}
