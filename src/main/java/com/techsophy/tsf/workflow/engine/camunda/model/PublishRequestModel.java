package com.techsophy.tsf.workflow.engine.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.Map;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class PublishRequestModel
{
    private  String documentId;

    private String templateId;

    private Map<String, Object> data;

    private String documentName;

    private String documentPath;

    private String documentTypeId;

    private String documentDescription;

    private Map<String, Object> metaInfo;
}
