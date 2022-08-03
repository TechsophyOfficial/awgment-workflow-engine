package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Process Variable with business key
 */
@Value
public class ProcessVariablesDTO
{
    @NotBlank(message = "businessKey should not be null")
    private final String businessKey;
    @NotNull(message = "variables should not be null")
    private final Map<String,Object> variables;
}
