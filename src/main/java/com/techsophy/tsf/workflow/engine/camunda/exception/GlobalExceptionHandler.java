package com.techsophy.tsf.workflow.engine.camunda.exception;

import com.techsophy.tsf.workflow.engine.camunda.model.ApiErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.text.ParseException;
import java.time.Instant;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{

    /**
     * Form not found exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(FormNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleFormException(FormNotFoundException ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.message, ex.errorcode,
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Entity not found by Id exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(EntityNotFoundByIdException.class)
    public ResponseEntity<ApiErrorResponse> entityNotFoundException(EntityNotFoundByIdException ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.message, ex.errorcode,
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Constraint Validation Exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> constraintValidationException(ConstraintViolationException ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.getMessage(), ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Task not found Exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> taskNotFoundException(TaskNotFoundException ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.message, ex.errorcode,
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Parse exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ApiErrorResponse> parseException(ParseException ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.getMessage(), ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Rest Client Response Exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleRestClientResponseException(RestClientResponseException ex, WebRequest request)
    {
        return new ResponseEntity<>(ex.getMessage(), INTERNAL_SERVER_ERROR);
    }

    /**
     * Generic Exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.getMessage(), ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Process Exception
     * @param ex
     * @param request
     * @return ResponseEntity
     */
    @ExceptionHandler(ProcessException.class)
    public ResponseEntity<ApiErrorResponse> handleProcessException(Exception ex, WebRequest request)
    {
        ApiErrorResponse errorDetails = new ApiErrorResponse(Instant.now(), ex.getMessage(), ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    /**
     * Error Details
     * @param request
     * @param ex
     * @param status
     * @return ApiErrorResponse
     */
//    private ApiErrorResponse getErrorDetails(WebRequest request, Exception ex, HttpStatus status)
//    {
//        return this.getErrorResponse(status, ex, request.getDescription(false));
//    }

    /**
     * Error Response
     * @param status
     * @param e
     * @param path
     * @return
     */
//    public ApiErrorResponse getErrorResponse(HttpStatus status, Throwable e, String path)
//    {
//        return new ApiErrorResponse(Instant.now(), e.getMessage(), e.getMessage(), status,  Map.of());
//    }
}
