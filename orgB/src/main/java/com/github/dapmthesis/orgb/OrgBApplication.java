package com.github.dapmthesis.orgb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import repository.TemplateRepository;
import templates.scalability.TimeOperator;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "pipeline", "communication", "repository"})
public class OrgBApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OrgBApplication.class, args);

        TemplateRepository templateRepository = context.getBean(TemplateRepository.class);
        storeTemplates(templateRepository);


    }

    private static void storeTemplates(TemplateRepository templateRepository) {
        templateRepository.storeTemplate("TimeOperator", TimeOperator.class);
        templateRepository.storeTemplate("TimeSource", templates.scalability.TimeSource.class);
        templateRepository.storeTemplate("TimeSink", templates.scalability.TimeSink.class);
        templateRepository.storeTemplate("LoggingOverheadSink", templates.LoggingOverheadSink.class);
        templateRepository.storeTemplate("BackpressureSink", templates.BackpressureSink.class);
        templateRepository.storeTemplate("ThroughputSink", templates.ThroughputSink.class);
        templateRepository.storeTemplate("AlignmentSink", templates.AlignmentSink.class);
    }

}
