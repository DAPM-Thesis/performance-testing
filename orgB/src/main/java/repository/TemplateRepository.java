package repository;

import org.springframework.stereotype.Repository;
import pipeline.processingelement.ProcessingElement;
import templates.AlignmentSink;
import templates.BackpressureSink;
import templates.ThroughputSink;
import templates.LoggingOverheadSink;
import templates.scalability.TimeOperator;
import templates.scalability.TimeSink;
import templates.scalability.TimeSource;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TemplateRepository {

    private Map<String, Class<? extends ProcessingElement>> templates;

    public TemplateRepository() {
        templates = new HashMap<>();
        templates.put("ThroughputSink", ThroughputSink.class);
        templates.put("AlignmentSink", AlignmentSink.class);
        templates.put("BackpressureSink", BackpressureSink.class);
        templates.put("LoggingOverheadSink", LoggingOverheadSink.class);
        templates.put("TimeSource", TimeSource.class);
        templates.put("TimeOperator", TimeOperator.class);
        templates.put("TimeSink", TimeSink.class);
    }

    // TODO: make it more generic later
    public <T extends ProcessingElement> T createInstanceFromTemplate(String templateID) {
        Class<? extends ProcessingElement> template = templates.get(templateID);
        if(template == null) { throw new RuntimeException("Template " + templateID + " not found"); }
            try {
                return (T) template.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
    }
}