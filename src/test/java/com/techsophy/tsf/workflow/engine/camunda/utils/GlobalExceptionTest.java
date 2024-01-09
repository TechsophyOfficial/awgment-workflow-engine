package com.techsophy.tsf.workflow.engine.camunda.utils;

import com.techsophy.tsf.workflow.engine.camunda.exception.*;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.nio.charset.Charset;
import java.text.ParseException;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionTest {
    @Mock
    WebRequest webRequest;
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleCreateFormDataExceptionTest()
    {
        FormNotFoundException formNotFoundException = new FormNotFoundException("error","args");
        ResponseEntity<ApiErrorResponse> response =globalExceptionHandler.handleFormException(formNotFoundException,webRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    void entityNotFoundExceptionTest()
    {
        EntityNotFoundByIdException entityNotFoundByIdException = new EntityNotFoundByIdException("args","args");
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.entityNotFoundException(entityNotFoundByIdException,webRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    void constraintViolationExceptionTest(){
        ConstraintViolationException constraintViolationException = mock(ConstraintViolationException.class);
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.constraintValidationException(constraintViolationException,webRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    void TaskNotFoundExceptionTest(){
        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("error","args");
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.taskNotFoundException(taskNotFoundException,webRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    void ParseExceptionTest(){
        ParseException parseException =  new ParseException("error",12);
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.parseException(parseException,webRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    void RestClientResponseExceptionTest(){
        Charset charset = mock(Charset.class);
        RestClientResponseException restClientResponseException = new RestClientResponseException("abc",1,"abc",new HttpHeaders(),new byte[2],charset);
        ResponseEntity<String> response = globalExceptionHandler.handleRestClientResponseException(restClientResponseException,webRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    void ExceptionTest(){
        Exception exception = new Exception();
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleGenericException(exception,webRequest);
        Assertions.assertNotNull(response);
    }


    @Test
    void ProcessExceptionTest(){
        ProcessException processException = new ProcessException("error","args");
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleProcessException(processException,webRequest);
        Assertions.assertNotNull(response);
    }
}
