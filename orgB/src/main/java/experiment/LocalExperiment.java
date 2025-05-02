package experiment;

import communication.message.Message;
import communication.message.impl.InstantTime;
import communication.message.serialization.MessageSerializer;
import communication.message.serialization.deserialization.MessageFactory;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class LocalExperiment {

    public static void main(String[] args) {
        int experimentLengthSeconds = 60 * 2;
        ExperimentLogger logger = new ExperimentLogger(Paths.get(
                "experiment_results/throughput/control_1.txt"
        ).toAbsolutePath());

        System.out.println("experiment started. Running for " + experimentLengthSeconds + " seconds.");
        Instant start = Instant.now();
        Instant deadline = start.plusSeconds(experimentLengthSeconds);
        while (Instant.now().isBefore(deadline)) {
            InstantTime sent = new InstantTime();
            MessageSerializer serializer = new MessageSerializer();
            sent.acceptVisitor(serializer);
            String serialization = serializer.getSerialization();
            InstantTime deserialize = (InstantTime) MessageFactory.deserialize(serialization);
            Duration controlLatency = Duration.between(sent.getTime(), Instant.now());
            logger.log(Long.toString(controlLatency.toNanos()));
        }
        System.out.println("Experiment finished.\nShutting down.");
    }
}
