package templates;


import communication.message.Message;
import communication.message.impl.Metrics;
import communication.message.impl.event.Attribute;
import communication.message.impl.event.Event;
import communication.message.impl.time.UTCTime;
import experiment.ExperimentLogger;
import pipeline.processingelement.Configuration;
import pipeline.processingelement.operator.SimpleOperator;
import utils.JsonUtil;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class BehaviouralPatternsConformance extends SimpleOperator<Metrics> {

    private final Object processLock = new Object();

    private Process process;
    private BufferedWriter jarInput;
    private BufferedReader jarOutput;

    public BehaviouralPatternsConformance(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        Map<Class<? extends Message>, Integer> map = new HashMap<>();
        map.put(Event.class, 1);
        return map;
    }

    private void startProcess() {
        synchronized (processLock) {
            try {
                if (process == null || !process.isAlive()) {
                    String jarPath = "orgB/src/main/java/templates/algorithm/behavioural-patterns-conformance.jar";
                    ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarPath);
                    processBuilder.redirectErrorStream(true);
                    process = processBuilder.start();

                    jarInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                    jarOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to start JAR process", e);
            }
        }
    }

    @Override
    protected Metrics process(Message message, int portNumber) {
        synchronized (processLock) {
            try {
                startProcess();

                Event incomingEvent = (Event) message;
                Set<Attribute<?>> noTimeAttributes = incomingEvent.getAttributes().stream()
                        .filter(a -> !a.getName().equals("time sent"))
                        .collect(Collectors.toSet());

                message = new Event(incomingEvent.getCaseID(), incomingEvent.getActivity(), incomingEvent.getTimestamp(), noTimeAttributes);

                String event = JsonUtil.toJson(message);

                jarInput.write(event);
                jarInput.newLine();
                jarInput.flush();

                // Collect output from the JAR with timeout
                long startTime = System.currentTimeMillis();
                List<String> scores = new ArrayList<>(3);
                String line;
                while (scores.size() < 3 && (line = jarOutput.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        scores.add(line.trim());
                    }
                    if (System.currentTimeMillis() - startTime > 10000) {
                        throw new IOException("Timeout while reading behavioural-patterns conformance scores from JAR");
                    }
                }

                String conformance = scores.get(0);
                String completeness = scores.get(1);
                String confidence = scores.get(2);

                /*
                System.out.println("\n__________________");
                System.out.println("event: " + incomingEvent);
                System.out.println("attributes size: " + incomingEvent.getAttributes().size());
                System.out.println("contains 'sent time' attribute: " + incomingEvent.getAttributes().stream().anyMatch(a -> a.getName().equals("time sent")));
                System.out.println("attribute: " + incomingEvent.getAttributes().stream().filter(a -> a.getName().equals("time sent")));
                */
                UTCTime sentTime = (UTCTime) incomingEvent.getAttributes().stream()
                        .filter(a -> a.getName().equals("time sent"))
                        .findFirst()
                        .map(Attribute::getValue)
                        .orElse(null);

                double epochNanos = (sentTime == null) ? -10000.0 : sentTime.getTime().getEpochSecond() * 1_000_000_000.0 + sentTime.getTime().getNano();

                return new Metrics(
                        Double.parseDouble(conformance),
                        Double.parseDouble(completeness),
                        Double.parseDouble(confidence),
                        epochNanos
                        );

            } catch (IOException e) {
                throw new RuntimeException("Error during processing in BehaviouralConformance", e);
            }
        }
    }

    @Override
    public boolean terminate() {
        super.terminate();
        synchronized (processLock) {
            try {
                if (jarInput != null) {
                    jarInput.close();
                }
                if (jarOutput != null) {
                    jarOutput.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to close JAR process", e);
            } finally {
                jarInput = null;
                jarOutput = null;
                process = null;
            }
        }
        return true;
    }

}
