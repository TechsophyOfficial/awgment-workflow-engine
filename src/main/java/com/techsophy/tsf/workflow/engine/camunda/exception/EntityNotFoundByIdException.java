package com.techsophy.tsf.workflow.engine.camunda.exception;

/**
 * class for entiy not found by id exception
 */
public class EntityNotFoundByIdException extends RuntimeException
{
    String errorcode;
    String message;
    public EntityNotFoundByIdException(String errorcode,String message)
    {
        super(message);
        this.errorcode=errorcode;
        this.message=message;
    }
}
