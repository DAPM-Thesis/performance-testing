package templates.scalability;

import communication.message.Message;
import communication.message.impl.time.UTCTime;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.operator.SimpleOperator;

import java.util.Map;

public class TimeOperator extends SimpleOperator<UTCTime> {
    public TimeOperator(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected UTCTime process(Message message, int i) {
        return (UTCTime) message;
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(UTCTime.class, 1);
    }
}
