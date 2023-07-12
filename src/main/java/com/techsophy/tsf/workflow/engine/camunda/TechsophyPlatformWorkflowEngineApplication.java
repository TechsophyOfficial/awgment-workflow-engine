package com.techsophy.tsf.workflow.engine.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application starter
 */
@RefreshScope
@SpringBootApplication
@EnableJpaAuditing
public class TechsophyPlatformWorkflowEngineApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(TechsophyPlatformWorkflowEngineApplication.class, args);
	}
}
