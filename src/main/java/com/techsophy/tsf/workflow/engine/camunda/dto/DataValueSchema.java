package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.io.Serializable;

/**
 * data value of form with label and value
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataValueSchema implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String label;
    private final String value;
}
