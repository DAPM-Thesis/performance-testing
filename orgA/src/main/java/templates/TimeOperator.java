package templates;

import communication.message.Message;
import communication.message.impl.InstantTime;
import pipeline.processingelement.operator.SimpleOperator;

import java.util.Map;

public class TimeOperator extends SimpleOperator<InstantTime> {
    @Override
    protected InstantTime process(Message message, int i) {
        return (InstantTime) message;
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of(InstantTime.class, 1);
    }
}
