package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.io.Serializable;

/**
 * validation properties schema for form fields
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationPropsSchema implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final boolean required;
    private final String pattern;
    private final String customMessage;
    private final Integer minLength;
    private final Integer maxLength;
    private final Integer min;
    private final Integer max;
}
