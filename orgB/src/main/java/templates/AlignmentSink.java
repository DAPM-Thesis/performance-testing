package templates;

import communication.message.Message;
import communication.message.impl.event.Event;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.Sink;
import utils.Pair;

import java.nio.file.Paths;
import java.util.Map;

public class AlignmentSink extends Sink {
    private final int messageCap;
    ExperimentLogger logger;

    public AlignmentSink(Configuration configuration) {
        super(configuration);

        this.messageCap = (int) configuration.get("message_send_count");
        String savePath = "experiment_results/alignment/" + configuration.get("save_file").toString();
        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());
    }

    @Override
    public void observe(Pair<Message, Integer> messageAndPort) {
        String activity = ((Event) messageAndPort.first()).getActivity();
        logger.log(activity);
        int count = Integer.parseInt(activity);
        maybeSendUserOutput(count);

        if (count >= messageCap) {
            System.out.println("AlignmentSink finished.\nTerminating. Last received number: " + count + '.');
            terminate();
        }
    }

    private void maybeSendUserOutput(int count) {
        if (count == 0) { System.out.println("Received first message."); }
        else if (count % 1000 == 0) {
            System.out.println("Received " + count + " messages.");
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(Event.class, 1);
    }
}
