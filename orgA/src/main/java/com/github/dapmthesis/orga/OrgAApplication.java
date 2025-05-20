package com.github.dapmthesis.orga;


import candidate_validation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import pipeline.Pipeline;
import pipeline.PipelineBuilder;
import pipeline.service.PipelineExecutionService;
import repository.TemplateRepository;
import templates.AlignmentSource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        Path pipelineFolderPath = Paths.get(orgID + "/src/main/representations");
        URI configURI = Paths.get(orgID + "/src/main/config_schemas").toUri();

        int minuteCount = 1;
        int experimentLengthSeconds = 60 * minuteCount;
        List<String> pipelineNames = List.of("throughput_pipeline");

        runExperiments(pipelineNames, experimentLengthSeconds, orgID, pipelineFolderPath, configURI, pipelineBuilder, executionService);
    }

    private static void runExperiments(List<String> pipelineNames, int experimentLengthSeconds, String organizationID, Path pipelineFolderPath, URI configURI, PipelineBuilder pipelineBuilder, PipelineExecutionService executionService) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        for (String pipelineName : pipelineNames) {
            Path pipelinePath = pipelineFolderPath.resolve(pipelineName + ".json");
            String contents = retrieveContents(pipelinePath);
            PipelineCandidate pipelineCandidate = new PipelineCandidate(contents, configURI);
            ValidatedPipeline validatedPipeline = new ValidatedPipeline(pipelineCandidate);

            Pipeline pipeline =  pipelineBuilder.buildPipeline(organizationID, validatedPipeline);
            executionService.start(pipeline);
            System.out.println("Experiment started. Running for " + experimentLengthSeconds + " seconds.");

            try { Thread.sleep(experimentLengthSeconds); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            executionService.terminate(pipeline);
            System.out.println("Finished experiment.");
            cleanExperiment();
        }
    }

    private static void cleanExperiment() {
        System.gc();
        try { Thread.sleep(1000); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static String retrieveContents(Path pipelinePath) {
        try { return Files.readString(pipelinePath); }
        catch (IOException e) { throw new RuntimeException("Failed to read contents from " + pipelinePath + ".\n" + e); }
    }

    private static void storeTemplates(TemplateRepository templateRepository) {
        templateRepository.storeTemplate("AlignmentSource", AlignmentSource.class);
        templateRepository.storeTemplate("TimeSource", templates.TimeSource.class);
        templateRepository.storeTemplate("BackpressureSource", templates.BackpressureSource.class);
        templateRepository.storeTemplate("LoggingOverheadSource", templates.LoggingOverheadSource.class);
        templateRepository.storeTemplate("ThroughputSource", templates.ThroughputSource.class);
        templateRepository.storeTemplate("TimeOperator", templates.TimeOperator.class);
    }
}
