package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Form schema for form io
 */
@With
@Value
public class FormSchema
{
    @NotBlank(message = "name should not be null")
    private final String name;
    @NotBlank(message = "id should not be null")
    private final String id;
    @NotNull(message = "components should not be null")
    private final Map<String, Object> components;
    private final Integer version;
}
