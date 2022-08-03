package com.techsophy.tsf.workflow.engine.camunda.exception;

public class EvaluationException extends RuntimeException
{
    final String errorCode;
    final String message;
    public EvaluationException(String errorCode, String message)
    {
        super(message);
        this.errorCode=errorCode;
        this.message=message;
    }
}
