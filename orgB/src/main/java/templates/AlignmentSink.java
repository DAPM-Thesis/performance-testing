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
    private final ExperimentLogger logger;
    private boolean capReached = false;

    public AlignmentSink(Configuration configuration) {
        super(configuration);

        this.messageCap = (int) configuration.get("message_send_count");
        String savePath = "experiment_results/vms/alignment/" + configuration.get("save_file").toString();
        this.logger = new ExperimentLogger(Paths.get(savePath).toAbsolutePath());
    }

    @Override
    public void observe(Pair<Message, Integer> messageAndPort) {
        String activity = ((Event) messageAndPort.first()).getActivity();
        int count = Integer.parseInt(activity);
        maybeSendUserOutput(count);

        if (count > messageCap) {
            if (!capReached) {
                System.out.println("AlignmentSink finished. Sleeping.\n");
                capReached = true;
            }
            try {Thread.sleep(10000);}
            catch (Exception e) { System.out.println(this.getClass().getSimpleName() + " woke up."); }
        } else {
            logger.log(activity);
        }
    }

    private void maybeSendUserOutput(int count) {
        if (count == 0) { System.out.println("Received first message."); }
        else if (count % 10000 == 0) {
            System.out.println("Received " + count + " messages.");
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(Event.class, 1);
    }
}
