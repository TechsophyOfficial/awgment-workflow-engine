package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;
import java.util.List;

/**
 * Task instance DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInstanceDTO
{
    private String id;
    private String name;
    private String assignee;
    private Date created;
    private Date due;
    private Date followUp;
    private String delegationState;
    private String description;
    private String executionId;
    private String owner;
    private String parentTaskId;
    private int priority;
    private String processDefinitionId;
    private String processInstanceId;
    private String taskDefinitionKey;
    private String caseExecutionId;
    private String caseInstanceId;
    private String caseDefinitionId;
    private boolean suspended;
    private String formKey;
    private String tenantId;
    private String businessKey;
    private String componentId;
    private String componentType;
    private String question;
    private String checklistInstanceId;
    private List<ActionsDTO> actions;
}