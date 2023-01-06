package com.techsophy.tsf.workflow.engine.camunda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Process Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ProcessException extends RuntimeException {
     final String errorCode;
     final String message;
    public ProcessException(String errorCode, String message)
    {
        super(message);
        this.errorCode = errorCode;
        this.message=message;
    }
}
