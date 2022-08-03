package com.techsophy.tsf.workflow.engine.camunda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * used to initialize beans
 */
@Configuration
public class BeanConfig
{
    /**
     * Bean initialization for rest template
     * @return
     */
    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}

