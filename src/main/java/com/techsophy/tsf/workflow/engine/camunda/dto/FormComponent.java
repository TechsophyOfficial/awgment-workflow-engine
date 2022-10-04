package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.io.Serializable;

/**
 * Form components fields at run time
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormComponent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String label;
    private final String placeholder;
    private final String description;
    private final boolean disabled;
    private final String validateOn;
    private final ValidationPropsSchema validate;
    private final String key;
    private final String fieldKey;
    private final String type;
    private final boolean input;
    private final transient Object defaultValue;
    private final String inputType;
    private final DataSchema data;
    private final DataValueSchema[] values;
    private final boolean disableOnInvalid;
    private final String action;
    private final String event;
    private final String provider;
    private final transient Object providerOptions;
    private final transient Object rows;
    private final FormComponent[] components;
    private final String storage;
    private final DataValueSchema[] fileTypes;
    private final boolean multiple;
    private final String url;
    private final String format;
    private final String documentType;
    private final boolean hideLabel;
    private final String html;
    private final String title;
    private final ColumnSchema[] columns;
    private final String prefix;
    private final String suffix;
    private final boolean mask;
    private final DataValueSchema[] questions;
}
