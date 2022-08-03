package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Process instance DTO
 */
@Value
public class ProcessInstanceDTO
{
    @NotBlank(message = "processDefinitionKey should not be null")
    private final String processDefinitionKey;
    @NotNull(message = "variables should not be null")
    private final Map<String,Object> variables;
    @NotBlank(message = "businessKey should not be null")
    private final String businessKey;
    private final String formKey;
}
