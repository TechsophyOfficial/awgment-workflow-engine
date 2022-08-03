package com.techsophy.tsf.workflow.engine.camunda.model;

import lombok.Value;

/**
 * Api response
 * @param <T>
 */
@Value
public class ApiResponse<T>
{
    private final T data;
    private final Boolean success;
    private final String message;
}
