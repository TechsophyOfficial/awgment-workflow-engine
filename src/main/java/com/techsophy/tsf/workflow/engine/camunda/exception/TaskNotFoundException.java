package com.techsophy.tsf.workflow.engine.camunda.exception;

/**
 * Task not found exception
 */
public class TaskNotFoundException extends RuntimeException
{
    String errorcode;
    String message;
    public TaskNotFoundException(String errorcode,String message)
    {
        super(message);
        this.errorcode=errorcode;
        this.message=message;
    }
}
