package com.techsophy.tsf.workflow.engine.camunda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Process Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ProcessException extends RuntimeException {
    private final String errorcode;
    private final String message;
    public ProcessException(String errorcode,String message)
    {
        super(message);
        this.errorcode=errorcode;
        this.message=message;
    }
}
