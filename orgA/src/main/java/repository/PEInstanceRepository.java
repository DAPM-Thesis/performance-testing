package repository;

import org.springframework.stereotype.Repository;
import pipeline.processingelement.ProcessingElement;
import utils.IDGenerator;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PEInstanceRepository {

    public Map<String, ProcessingElement> instances;

    public PEInstanceRepository() {
        instances = new HashMap<>();
    }

    public String storeInstance(ProcessingElement instance) {
        String instanceID = IDGenerator.generateInstanceID();
        instances.put(instanceID, instance);
        return instanceID;
    }

    public <T extends ProcessingElement> T getInstance(String instanceID) {
        return (T) instances.get(instanceID);
    }
}
