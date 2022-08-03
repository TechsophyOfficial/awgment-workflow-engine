package com.techsophy.tsf.workflow.engine.camunda.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.DATE_PATTERN;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.TIME_ZONE;

/**
 * api error response
 */
@Value
@AllArgsConstructor
public class ApiErrorResponse
{
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN, timezone = TIME_ZONE)
    Instant timestamp;
    String message;
    String error;
    HttpStatus status;
    String path;
}
