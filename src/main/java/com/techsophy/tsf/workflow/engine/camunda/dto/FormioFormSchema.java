package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.io.Serializable;

/**
 * form io schema
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormioFormSchema implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String display;
    private final FormComponent[] components;
}
