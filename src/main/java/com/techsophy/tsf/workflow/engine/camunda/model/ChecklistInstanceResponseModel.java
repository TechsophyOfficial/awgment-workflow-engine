package com.techsophy.tsf.workflow.engine.camunda.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@With
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChecklistInstanceResponseModel
{
    private String id;
    private boolean active;
    private boolean ok;
    private String status;
    private String checklistId;
    private String category;
    private String assignee;
    private Boolean isCompleted;
    private String reviewer;
    private String reviewerType;
    private Boolean isReviewRequired;
    private Boolean isReviewCompleted;
    private String createdById;
    private String updatedById;
    private String createdBy;
    private String updatedBy;
    private Instant createdOn;
    private Instant updatedOn;
    private Map<String, Object> checklist;
    private List<String> groupList;
    private List<Map<String, Object>> output;
}
