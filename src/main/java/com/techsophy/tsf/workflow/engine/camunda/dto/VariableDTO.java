package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;

@Value
public class VariableDTO
{
    private final String name;

    private final Object value;

    private final String operator;
}
