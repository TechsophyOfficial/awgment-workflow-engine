package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Map;

/**
 * Process instance response DTO
 */
@With
@Value
@AllArgsConstructor
public class ProcessInstanceResponseDTO
{
    private final  String processInstanceId;
    private final String businessKey;
    private final Map<String, Object> variables;
    private Boolean alreadyExists;
}
