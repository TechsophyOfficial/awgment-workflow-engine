package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Map;

/**
 * Form schema response for form key
 */
@Builder
@With
@Value
public class FormSchemaResponse
{
    private final String formName;
    private final String formKey;
    private final FormioFormSchema formContent;
    private final Integer formVersion;
    private final String tenantId;
    private final String taskId;
    private final String taskName;
    private final Map<String,Object> variables;
}
