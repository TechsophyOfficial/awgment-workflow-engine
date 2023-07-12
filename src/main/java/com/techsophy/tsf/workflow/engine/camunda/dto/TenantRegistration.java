package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "tenants")
@Component
@Data
public class TenantRegistration
{
    private List<String> registrations;
}
