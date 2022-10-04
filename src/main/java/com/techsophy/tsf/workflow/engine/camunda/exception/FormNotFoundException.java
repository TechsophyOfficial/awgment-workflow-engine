package com.techsophy.tsf.workflow.engine.camunda.exception;

import lombok.Getter;

/**
 * class for form not found exception
 */
@Getter
public class FormNotFoundException extends RuntimeException
{
    private final String errorcode;
    private final String message;
    public FormNotFoundException(String errorcode,String message)
    {
        super(message);
        this.errorcode=errorcode;
        this.message=message;
    }
}
