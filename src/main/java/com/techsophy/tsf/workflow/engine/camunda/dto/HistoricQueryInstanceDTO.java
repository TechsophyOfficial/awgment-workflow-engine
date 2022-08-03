package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoricQueryInstanceDTO
{
    private String taskId;
    private String taskParentTaskId;
    private String processInstanceId;
    private String processInstanceBusinessKey;
    private String[] processInstanceBusinessKeyIn;
    private String processInstanceBusinessKeyLike;
    private String executionId;
    private String[] activityInstanceIdIn;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private String taskName;
    private String taskNameLike;
    private String taskDescription;
    private String taskDescriptionLike;
    private String taskDefinitionKey;
    private String[] taskDefinitionKeyIn;
    private String taskDeleteReason;
    private String taskDeleteReasonLike;
    private Boolean assigned;
    private Boolean unassigned;
    private String taskAssignee;
    private String taskAssigneeLike;
    private String taskOwner;
    private String taskOwnerLike;
    private Integer taskPriority;
    private Boolean finished;
    private Boolean unfinished;
    private Boolean processFinished;
    private Boolean processUnfinished;
    private Date taskDueDate;
    private Date taskDueDateBefore;
    private Date taskDueDateAfter;
    private Date taskFollowUpDate;
    private Date taskFollowUpDateBefore;
    private Date taskFollowUpDateAfter;
    private List<String> tenantIds;
    private Boolean withoutTenantId;
    private Date startedBefore;
    private Date startedAfter;
    private Date finishedBefore;
    private Date finishedAfter;

    private String caseDefinitionId;
    private String caseDefinitionKey;
    private String caseDefinitionName;
    private String caseInstanceId;
    private String caseExecutionId;
    private String taskInvolvedUser;
    private String taskInvolvedGroup;
    private String taskHadCandidateUser;
    private String taskHadCandidateGroup;
    private Boolean withCandidateGroups;
    private Boolean withoutCandidateGroups;
    private List<VariableDTO> processVariables;
    private Boolean variableValuesIgnoreCase;
    private Boolean variableNamesIgnoreCase;

    private List<Object> orQueries;
}
