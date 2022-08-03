package com.techsophy.tsf.workflow.engine.camunda.model;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class RulesExecutionRequestModel
{
    String id;
    Map<String, Object> variables;
}
