package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.io.Serializable;

/**
 * it has data schema with value
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSchema implements Serializable
{
    private static final long serialVersionUID = 1L;
    private  final DataValueSchema[] values;
    @JsonCreator
    public DataSchema( DataValueSchema[] values) {
        this.values = values;
    }
}
