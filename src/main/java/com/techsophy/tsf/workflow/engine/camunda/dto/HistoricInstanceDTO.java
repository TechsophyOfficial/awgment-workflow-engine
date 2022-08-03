package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

/**
 * Historic instance DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricInstanceDTO
{
    private String id;
    private String processDefinitionKey;
    private String name;
    private String assignee;
    private String caseDefinitionKey;
    private String deleteReason;
    private Date startTime;
    private Date endTime;
    private Date due;
    private Date followUp;
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
//    private String businessKey;
    private String componentId;
    private String componentType;
    private String question;
    private String rootProcessInstanceId;
    private String checklistInstanceId;
    private List<ActionsDTO> actions;
}