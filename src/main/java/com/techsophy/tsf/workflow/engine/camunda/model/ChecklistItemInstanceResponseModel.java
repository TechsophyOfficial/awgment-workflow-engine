package com.techsophy.tsf.workflow.engine.camunda.model;

import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Value
public class ChecklistItemInstanceResponseModel
{
    private String id;
    private String checklistInstanceId;
    private String checklistId;
    private String checklistGroupId;
    private String checklistItemId;
    private Boolean isCompleted;
    private String createdById;
    private String updatedById;
    private String createdBy;
    private String updatedBy;
    private Instant createdOn;
    private Instant updatedOn;
    private Map<String, Object> item;
    private List<Map<String, Object>> output;
}
