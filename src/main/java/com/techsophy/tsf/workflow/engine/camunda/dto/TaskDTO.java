package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * TASK DTO with variables and business key
 */
@Value
public class TaskDTO {
    @NotBlank(message = "businessKey should not be null")
    private final String businessKey;

    @NotBlank(message = "taskId should not be null")
    private final String taskId;


    @NotNull(message = "variables should not be null")
    private final Map<String, Object> variables;
}
