package templates;

import communication.message.Message;
import communication.message.impl.event.Event;
import experiment.ExperimentLogger;
import pipeline.processingelement.Sink;

import java.nio.file.Paths;
import java.util.Map;

public class AlignmentSink extends Sink {
    ExperimentLogger logger = new ExperimentLogger(Paths.get(
            "experiment_results/alignment/sink_experiment_2.txt"
    ).toAbsolutePath());

    @Override
    public void observe(Message message, int i) {
        String activity = ((Event) message).getActivity();
        logger.log(activity);

        int count = Integer.parseInt(activity);
        if (count == 0) { System.out.println("Received first message."); }
        else if (count % 1000 == 0 && count != 0) {
            System.out.println("Received " + activity + " messages.");
        }
        if (count >= 5200) {
            System.out.println("Sink finished.\nShutting down. Last received number: " + activity + '.');
            System.exit(0);
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(Event.class, 1);
    }
}
