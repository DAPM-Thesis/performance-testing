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

public class LoggingOverheadSink extends Sink {
    private int counter = 0;
    private final int messageCap;
    private final ExperimentLogger logger;
    private final ExperimentLogger finalTimeLogger;
    Instant startTime;

    public LoggingOverheadSink(Configuration configuration) {
        super(configuration);

        this.messageCap = (int) configuration.get("n_messages");

        String finalTimePath = "experiment_results/virtual_machine/logging_overhead/" + configuration.get("final_time_file").toString();
        this.finalTimeLogger = new ExperimentLogger(Paths.get(finalTimePath).toAbsolutePath());

        Object logFilename = configuration.get("log_file");
        if (logFilename != null) {
            String logPath = "experiment_results/virtual_machine/logging_overhead/" + logFilename;
            this.logger = new ExperimentLogger(Paths.get(logPath).toAbsolutePath());
        } else {
            this.logger = null;
        }
    }

    @Override
    public void observe(Pair<Message,Integer> messageAndPort) {
        if (counter == 0) {
            startTime = Instant.now();
            System.out.println("Sink Started.");
        }

        counter++;
        if (counter % 100000 == 0) { System.out.println("Sink processed " + counter + " messages."); }

        if (logger != null && counter <= messageCap) { logger.log(String.valueOf(counter)); }
        if (counter == messageCap) {
            long processingTime = Duration.between(startTime, Instant.now()).toMillis();
            String messageStart = (logger == null) ? "Without logging" : "Logging every message";
            String logMessage = messageStart + ": processed " + messageCap + " messages in " + processingTime + " ms";
            finalTimeLogger.log(logMessage);
            System.out.println("LoggingOverheadSink finished processing all messages.");

        } else if (counter > messageCap) {
            try { Thread.sleep(5000); }
            catch (Exception e) {System.out.println("LoggingOverheadSink Woke up.");}
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
