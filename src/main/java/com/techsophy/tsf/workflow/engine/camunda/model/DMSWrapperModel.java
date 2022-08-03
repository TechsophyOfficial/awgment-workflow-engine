package com.techsophy.tsf.workflow.engine.camunda.model;

import lombok.Value;

import java.util.Map;

@Value
public class DMSWrapperModel
{
    String documentId;

    String documentTypeId;

    String applicationId;

    String documentName;

    String description;

    Map<String, Object> metaInfo;
}
