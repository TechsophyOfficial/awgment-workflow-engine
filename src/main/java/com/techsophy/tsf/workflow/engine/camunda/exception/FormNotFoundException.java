package com.techsophy.tsf.workflow.engine.camunda.exception;

/**
 * class for form not found exception
 */
public class FormNotFoundException extends RuntimeException
{
    String errorcode;
    String message;
    public FormNotFoundException(String errorcode,String message)
    {
        super(message);
        this.errorcode=errorcode;
        this.message=message;
    }
}
