package templates;

import communication.message.Message;
import communication.message.impl.event.Event;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.operator.SimpleOperator;

import java.util.Map;

public class TrivialEventOperator extends SimpleOperator<Event> {
    public TrivialEventOperator(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected Event process(Message event, int i) { return (Event) event; }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        return Map.of();
    }
}
