package com.techsophy.tsf.workflow.engine.camunda.exception;

import lombok.Getter;

/**
 * Task not found exception
 */
@Getter
public class TaskNotFoundException extends RuntimeException
{
    private final String errorcode;
    private final String message;
    public TaskNotFoundException(String errorcode,String message)
    {
        super(message);
        this.errorcode=errorcode;
        this.message=message;
    }
}
