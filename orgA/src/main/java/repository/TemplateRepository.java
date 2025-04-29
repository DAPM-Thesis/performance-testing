package repository;

import org.springframework.stereotype.Repository;
import pipeline.processingelement.ProcessingElement;
import templates.SinkA;
import templates.SourceA;
import templates.TimeSource;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TemplateRepository {

    private Map<String, Class<? extends ProcessingElement>> templates;

    public TemplateRepository() {
        templates = new HashMap<>();
        templates.put("SimpleSource", SourceA.class);
        templates.put("SimpleSink", SinkA.class);
    }

    public <T extends ProcessingElement> T createInstanceFromTemplate(String templateID) {
        Class<? extends ProcessingElement> template = templates.get(templateID);
        if(template == null) {throw new RuntimeException("No template found for template ID: " + templateID); }
            try {
                return (T) template.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
    }
}
