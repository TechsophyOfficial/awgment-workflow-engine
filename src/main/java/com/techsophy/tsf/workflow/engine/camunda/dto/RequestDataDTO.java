package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class RequestDataDTO {

    @NotBlank(message = "url should not be null")
    String url;
    @NotBlank(message = "method should not be null")
    String method;
    Object payload;
    Object requestParams;
}
