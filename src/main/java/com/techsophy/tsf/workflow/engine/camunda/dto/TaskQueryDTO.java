package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.camunda.bpm.engine.rest.dto.SortingDto;

import java.util.Date;
import java.util.List;

/**
 * Task query DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskQueryDTO
{
    private String processInstanceBusinessKey;
    private String processInstanceBusinessKeyExpression;
    private String[] processInstanceBusinessKeyIn;
    private String processInstanceBusinessKeyLike;
    private String processInstanceBusinessKeyLikeExpression;
    private String processDefinitionKey;
    private Boolean finished;
    private Boolean unfinished;
    private String[] processDefinitionKeyIn;
    private String processDefinitionId;
    private String executionId;
    private String[] activityInstanceIdIn;
    private String processDefinitionName;
    private String processDefinitionNameLike;
    private String processInstanceId;
    private String[] processInstanceIdIn;
    private String assignee;
    private String assigneeExpression;
    private String assigneeLike;
    private String assigneeLikeExpression;
    private String[] assigneeIn;
    private String[] assigneeNotIn;
    private String candidateGroup;
    private String candidateGroupExpression;
    private String candidateUser;
    private String candidateUserExpression;
    private Boolean includeAssignedTasks;
    private String taskDefinitionKey;
    private String[] taskDefinitionKeyIn;
    private String taskDefinitionKeyLike;
    private String description;
    private String descriptionLike;
    private String involvedUser;
    private String involvedUserExpression;
    private Integer maxPriority;
    private Integer minPriority;
    private String name;
    private String nameNotEqual;
    private String nameLike;
    private String nameNotLike;
    private String owner;
    private String ownerExpression;
    private Integer priority;
    private String parentTaskId;
    protected Boolean assigned;
    private Boolean unassigned;
    private Boolean active;
    private Boolean suspended;
    private String caseDefinitionKey;
    private String caseDefinitionId;
    private String caseDefinitionName;
    private String caseDefinitionNameLike;
    private String caseInstanceId;
    private String caseInstanceBusinessKey;
    private String caseInstanceBusinessKeyLike;
    private String caseExecutionId;
    private Date dueAfter;
    private String dueAfterExpression;
    private Date dueBefore;
    private String dueBeforeExpression;
    private Date dueDate;
    private String dueDateExpression;
    private Date followUpAfter;
    private String followUpAfterExpression;
    private Date followUpBefore;
    private String followUpBeforeExpression;
    private Date followUpBeforeOrNotExistent;
    private String followUpBeforeOrNotExistentExpression;
    private Date followUpDate;
    private String followUpDateExpression;
    private Date createdAfter;
    private String createdAfterExpression;
    private Date createdBefore;
    private String createdBeforeExpression;
    private Date createdOn;
    private String createdOnExpression;
    private String delegationState;
    private String[] tenantIdIn;
    private Boolean withoutTenantId;
    private List<String> candidateGroups;
    private String candidateGroupsExpression;
    protected Boolean withCandidateGroups;
    protected Boolean withoutCandidateGroups;
    protected Boolean withCandidateUsers;
    protected Boolean withoutCandidateUsers;
    protected Boolean variableNamesIgnoreCase;
    protected Boolean variableValuesIgnoreCase;
    private List<Object> orQueries;
    private List<VariableDTO> processVariables;
    private List<SortingDto> sorting;
}
