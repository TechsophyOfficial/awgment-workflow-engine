package com.techsophy.tsf.workflow.engine.camunda.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.io.Serializable;

/**
 * Form columns schema
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColumnSchema implements Serializable {
    private static final long serialVersionUID = 1L;

    private final FormComponent[] components;
    private final Number offset;
    private final Number pull;
    private final Number push;
    private final String size;
    private final Number width;
}
