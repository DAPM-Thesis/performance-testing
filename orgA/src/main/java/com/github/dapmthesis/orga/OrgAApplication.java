package com.github.dapmthesis.orga;


import candidate_validation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import pipeline.PipelineBuilder;
import pipeline.service.PipelineExecutionService;
import repository.TemplateRepository;
import templates.AlignmentSource;
import templates.KafkaOverheadSink;
import templates.ThroughputSink;
import templates.backpressure.BackpressureSink;
import templates.backpressure.BackpressureSource;
import templates.logging_overhead.LoggingOverheadSink;
import templates.logging_overhead.LoggingOverheadSource;
import templates.scalability.TimeOperator;
import templates.scalability.TimeSink;
import templates.scalability.TimeSource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "pipeline", "communication", "repository"})
public class OrgAApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OrgAApplication.class, args);
        PipelineBuilder pipelineBuilder = context.getBean(PipelineBuilder.class);
        PipelineExecutionService executionService = context.getBean(PipelineExecutionService.class);
        TemplateRepository templateRepository = context.getBean(TemplateRepository.class);
        storeTemplates(templateRepository);


        String orgID = "orgA";
        int minuteCount = 5;
        int experimentLengthSeconds = 60 * minuteCount;
        int runCount = 3;

        // TODO: run throughput experiments and alignment experiment
        /*
        List<String> pipelineNames = List.of(
                "throughput/025ms_sleep_pipeline.json",
                "throughput/033ms_sleep_pipeline.json",
                "throughput/05ms_sleep_pipeline.json",
                "throughput/075ms_sleep_pipeline.json",
                "throughput/1ms_sleep_pipeline.json"

                //"alignment_pipeline.json",
                //"throughput/5ms_sleep_pipeline.json"
        );

         */


        // TODO: Do statistics and take note of the throughput threshold. If it is not reached, make a 0.25 ms sleep experiment.
            // TODO: start by checking 0.33 ms sleep pipeline
            // TODO: At this stage, don't worry about formatting the output yet; Do that while the next experiment is running. Just see if the experiments are stable or not, and sanity check that everyone above the first stable experiment is stable too.
        // TODO: Adjust sleep_ms in kafka_overhead to be slightly above the threshold, but still quite low.
            // TODO: Adjust in all pipelines both source:  "configuration": {"sleep_ms": 0.33}   and    sink:  "configuration": {"save_file": "2_elem_075_ms_sleep.txt", "log_frequency": 10000 }
        // TODO: Update the backpressure sleep time to be 1 sleep "step" above the lowest sleep stable sleep time from the throughput experiments
        // TODO: Update the backpressure mean_sleep threshold in the sink to be adjusted_mean + adjusted_variance for the sleep time used above
        // TODO: Run kafka_overhead, backpressure, and scalability_pipeline experiments

        List<String> pipelineNames = List.of(
                "kafka_overhead/3_pipeline.json",
                "kafka_overhead/5_pipeline.json",
                "kafka_overhead/9_pipeline.json",
                "kafka_overhead/13_pipeline.json",
                "kafka_overhead/17_pipeline.json",
                "kafka_overhead/25_pipeline.json"

                //"backpressure_pipeline.json",
                //"logging_overhead/log_pipeline.json",
                //"logging_overhead/no_log_pipeline.json",
                //"scalability/scalability_05_pipeline.json",
                //"scalability/scalability_075_pipeline.json",
                //"scalability/scalability_1_pipeline.json"
        );


        // TODO: make control experiment; static void main method without kafka

        runExperiments(pipelineNames,
                experimentLengthSeconds,
                runCount,
                Paths.get(orgID + "/src/main/representations"),
                Paths.get(orgID + "/src/main/config_schemas").toUri(),
                pipelineBuilder,
                executionService);
    }

    private static void runExperiments(List<String> pipelineNames, int experimentLengthSeconds, int runCount, Path pipelineFolderPath, URI configURI, PipelineBuilder pipelineBuilder, PipelineExecutionService executionService) {

        for (String pipelineName : pipelineNames) {
            System.out.println("\n\nAbout to run " + runCount + " " + pipelineName + " experiments.\n\n");
            int runs = pipelineName.equals("alignment_pipeline.json") ? 1 : runCount;
            for (int i = 0; i < runs; i++) {
                Path pipelinePath = pipelineFolderPath.resolve(pipelineName);
                String contents = retrieveContents(pipelinePath);
                ValidatedPipeline validatedPipeline = new ValidatedPipeline(contents, configURI);

                pipelineBuilder.buildPipeline(pipelineName, validatedPipeline);
                executionService.start(pipelineName);
                System.out.println("\n\nExperiment started (" + pipelineName + "), run " + (i + 1) + " of " + runs + ". Running for " + experimentLengthSeconds + " seconds.\n\n");

                try { Thread.sleep(1000L * experimentLengthSeconds); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                executionService.terminate(pipelineName);
                System.out.println("\n\nFinished experiment (" + pipelineName + "), run " + (i + 1) + ".\n\n");
                cleanExperiment();
            }
            System.out.println("\n\nConcluded running " + runCount + " " + pipelineName + " experiments.\n\n");
        }
        System.out.println("\n\nFinished running all experiments.");
        System.exit(0);
    }

    private static void cleanExperiment() {
        System.gc();
        try { Thread.sleep(1000L); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static String retrieveContents(Path pipelinePath) {
        try { return Files.readString(pipelinePath); }
        catch (IOException e) { throw new RuntimeException("Failed to read contents from " + pipelinePath + ".\n" + e); }
    }

    private static void storeTemplates(TemplateRepository templateRepository) {
        templateRepository.storeTemplate("AlignmentSource", AlignmentSource.class);
        templateRepository.storeTemplate("TimeSource", TimeSource.class);
        templateRepository.storeTemplate("BackpressureSource", BackpressureSource.class);
        templateRepository.storeTemplate("LoggingOverheadSource", LoggingOverheadSource.class);
        templateRepository.storeTemplate("ThroughputSource", templates.ThroughputSource.class);
        templateRepository.storeTemplate("TimeOperator", TimeOperator.class);

        templateRepository.storeTemplate("ThroughputSink", ThroughputSink.class);
        templateRepository.storeTemplate("KafkaOverheadSink", KafkaOverheadSink.class);
        templateRepository.storeTemplate("BackpressureSink", BackpressureSink.class);
        templateRepository.storeTemplate("LoggingOverheadSink", LoggingOverheadSink.class);
        templateRepository.storeTemplate("TimeSink", TimeSink.class);
    }
}
