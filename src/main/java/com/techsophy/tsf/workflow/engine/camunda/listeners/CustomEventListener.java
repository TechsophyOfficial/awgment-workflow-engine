package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.EvaluationException;
import com.techsophy.tsf.workflow.engine.camunda.model.*;
import com.techsophy.tsf.workflow.engine.camunda.service.AppUtilService;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import com.techsophy.tsf.workflow.engine.camunda.service.DMNService;
import com.techsophy.tsf.workflow.engine.camunda.service.DMSWrapperService;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.TaskAssignmentService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.DOCUMENT_ID;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.ErrorMessageConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.LogMessages.*;
import static com.techsophy.tsf.workflow.engine.camunda.utils.CommonUtils.isValidCamundaValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
@AllArgsConstructor
@Slf4j
public class CustomEventListener
{
  public static final String ASSIGNEE_TYPE_NOT_FOUND = "assignee type not found";
  public static final String CAN_T_FIND_ASSIGNEE_OPTIONAL = "can't find assignee optional";
  public static final String ASSIGNMENT_RULE_NOT_FOUND = "assignment rule not found";
  public static final String ALGORITHM_NOT_FOUND = "algorithm not found";
  public static final String CHECK_LIST_ID_NOT_FOUND = "check list Id not found";
  public static final String DOCUMENT_TYPE_ID_NOT_FOUND = "document type id not found";
  private final ChecklistService checklistService;
    private final DMNService dmnService;
    private final AppUtilService appUtilService;
    private final DMSWrapperService dmsWrapperService;
    private final GlobalMessageSource globalMessageSource;
    private final TaskAssignmentService taskAssignmentService;

    @EventListener(condition = TASK_COMPLETE_EVENT_LISTENER_CONDITION)
    public void onTaskCompleteEvent(DelegateTask delegateTask)
    {
        TenantContext.setTenantId(delegateTask.getTenantId());
        log.info(String.format(COMPLETE_TASK_EVENT_START, delegateTask.getName()));
        if(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID))
        {
            if(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST) && delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST) != null)
            {
                List<String> checklistItemInstanceIdList;
                try {
                    checklistItemInstanceIdList = (List) delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST);
                } catch (Exception e) {
                    String msg = globalMessageSource.get(UNABLE_TO_PARSE_CHECKLIST_ITEM_INSTANCE_ID_LIST_VARIABLE, delegateTask.getExecution().getBusinessKey());
                    log.error(msg);
                    throw new EvaluationException(msg,msg);
                }

                this.checklistService.completeChecklistItemsByIds(Map.of(ID_LIST, checklistItemInstanceIdList));
            }
            ChecklistInstanceResponseModel checklistInstanceResponseModel = this.checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString());
            if(!checklistInstanceResponseModel.getStatus().equals(COMPLETE) && !checklistInstanceResponseModel.getStatus().equals(COMPLETE_WITH_OVERRIDE))
            {
                throw new EvaluationException(CHECKLIST_ITEMS_INCOMPLETE_EXCEPTION,CHECKLIST_ITEMS_INCOMPLETE_EXCEPTION);
            }
        }
        log.info(String.format(COMPLETE_TASK_EVENT_END, delegateTask.getName()));
    }

    @EventListener(condition = TASK_CREATE_EVENT_LISTENER_CONDITION)
    @SneakyThrows
    public void onTaskCreatedEvent(DelegateTask delegateTask)
    {
        TenantContext.setTenantId(delegateTask.getTenantId());
        log.info(String.format(CREATE_TASK_EVENT_START, delegateTask.getName()));
        ExtensionElements extensionElements = delegateTask.getBpmnModelElementInstance().getExtensionElements();
        if(extensionElements == null)
        {
            return;
        }
        Query<CamundaProperties> query = extensionElements
                .getElementsQuery().filterByType(CamundaProperties.class);
        if(query.count() == 0)
        {
            return;
        }
        Collection<CamundaProperty> camundaProperties = query.singleResult().getCamundaProperties();
        Optional<CamundaProperty> checklistIdOptional = getCamundaProperty(camundaProperties, "checklistId");
        Optional<CamundaProperty> executionRuleOptional = getCamundaProperty(camundaProperties, "executionRule");
        Optional<CamundaProperty> assignmentRuleOptional = getCamundaProperty(camundaProperties, "assignmentRule");
        Optional<CamundaProperty> assigneeOptional = getCamundaProperty(camundaProperties, "assignee");
        Optional<CamundaProperty> assigneeTypeOptional = getCamundaProperty(camundaProperties, "assigneeType");
        Optional<CamundaProperty> algorithmOptional = getCamundaProperty(camundaProperties, "algorithm");
        Optional<CamundaProperty> documentTypeIdOptional = getCamundaProperty(camundaProperties, "documentTypeId");

        Map<String, Object> variables = delegateTask.getVariables();
        // handle immutable task event
        if(isValidCamundaValue(executionRuleOptional))
        {
            log.info(EXECUTION_RULE_START);
          executionRuleOptional.ifPresent(property ->
            this.dmnService.executeDMN(property.getCamundaValue(),variables)
              .stream().filter(temp-> temp.containsKey(RESULT)).findFirst()
              .ifPresent( r-> {
                if ((boolean) r.get(RESULT)) delegateTask.complete();
              } ));
            log.info(EXECUTION_RULE_END);
        }
        if(isValidCamundaValue(assigneeOptional) && isValidCamundaValue(assigneeTypeOptional) && checklistIdOptional.isEmpty())
        {
            log.info(ASSIGNMENT_RULE_START);
            if(assigneeTypeOptional.isPresent()) {
              String assigneeType;
              assigneeType= assigneeTypeOptional.get().getCamundaValue();
              //assign task to user or group based on camunda element
              assignTaskToUserOrGroup(delegateTask, assigneeType,assignmentRuleOptional, assigneeOptional, algorithmOptional, variables);
            }
            else{
              throw new EvaluationException(ASSIGNEE_TYPE_NOT_FOUND,globalMessageSource.get(ASSIGNEE_TYPE_NOT_FOUND));
            }
            log.info(ASSIGNMENT_RULE_END);
        }
        if(isValidCamundaValue(checklistIdOptional)) {
          assignTaskInTaskList(delegateTask, checklistIdOptional, variables);
        }
        if(isValidCamundaValue(documentTypeIdOptional))
        {
          generateDMSDocument(delegateTask, documentTypeIdOptional, variables);
        }
        log.info(String.format(CREATE_TASK_EVENT_END, delegateTask.getName()));
    }

  private void assignTaskInTaskList(DelegateTask delegateTask, Optional<CamundaProperty> checklistIdOptional, Map<String, Object> variables) {
    if (checklistIdOptional.isPresent())
    {
      log.info(String.format(CHECKLIST_INVOCATION_START, checklistIdOptional.get().getCamundaValue()));
    // Fetch assignee from checklist and assign to the user task
    ChecklistModel checklistModel = checklistService.getChecklistById(checklistIdOptional.get().getCamundaValue());
    if (checklistModel.getChecklist().containsKey(ASSIGNEE) && checklistModel.getChecklist().get(ASSIGNEE) != null) {
      String assignee = checklistModel.getChecklist().get(ASSIGNEE).toString();
      delegateTask.setAssignee(assignee);
    } else {
      throw new EvaluationException(NOT_VALID_CHECKLIST_ASSIGNEE, globalMessageSource.get(NOT_VALID_CHECKLIST_ASSIGNEE, checklistIdOptional.get().getCamundaValue()));
    }
    // Invoke the checklist
    String businessKey = delegateTask.getExecution().getBusinessKey();
    InvokeChecklistInstanceResponseModel checklistInstanceResponseModel = this.checklistService.invokeChecklist(checklistIdOptional.get().getCamundaValue(), businessKey, variables);
    delegateTask.setVariableLocal(CHECKLIST_INSTANCE_ID, checklistInstanceResponseModel.getChecklistInstanceId());
    log.info(String.format(CHECKLIST_INVOCATION_END, checklistInstanceResponseModel.getChecklistInstanceId()));
  } else {
      throw new EvaluationException(CHECK_LIST_ID_NOT_FOUND, CHECK_LIST_ID_NOT_FOUND);
    }
  }

  private void assignTaskToUserOrGroup(DelegateTask delegateTask,String assigneeType, Optional<CamundaProperty> assignmentRuleOptional, Optional<CamundaProperty> assigneeOptional, Optional<CamundaProperty> algorithmOptional, Map<String, Object> variables) {
    if(assigneeOptional.isPresent()) {
      if (assignmentRuleOptional.isPresent() && isNotEmpty(assignmentRuleOptional.get().getCamundaValue()) && isNotBlank(assignmentRuleOptional.get().getCamundaValue()))
      {
        if (!(assigneeOptional.get().getCamundaValue().startsWith("${") && assigneeOptional.get().getCamundaValue().endsWith("}"))) {
          throw new EvaluationException(INVALID_VARIABLE_IN_ASSIGNEE_FIELD, globalMessageSource.get(INVALID_VARIABLE_IN_ASSIGNEE_FIELD, delegateTask.getName()));
        }
        String dmnOutputVariable = assigneeOptional.get().getCamundaValue().replace("${", "").replace("}", "");
        List<Map<String, Object>> output = this.dmnService.executeDMN(assignmentRuleOptional.get().getCamundaValue(), variables);
        Map<String, Object> dmnOutput = output.stream().filter(temp -> temp.containsKey(dmnOutputVariable)).findFirst()
          .orElseThrow(() -> new RuntimeException(globalMessageSource.get(MISMATCHED_DMN_OUTPUT_AND_ASSIGNEE_VALUE, dmnOutputVariable)));

        if (assigneeType.equals("group")) {
          assignTaskToGroup(delegateTask, algorithmOptional, dmnOutputVariable, dmnOutput);
        } else if (assigneeType.equals("user")) {
          delegateTask.setAssignee(dmnOutput.get(dmnOutputVariable).toString());
        }

      }
      else {
        assigningBasedOnAssigneeType(delegateTask, assigneeType, assigneeOptional);
      }
    }
    else {
      throw new EvaluationException(CAN_T_FIND_ASSIGNEE_OPTIONAL,globalMessageSource.get(CAN_T_FIND_ASSIGNEE_OPTIONAL));
    }
  }

  private static void assigningBasedOnAssigneeType(DelegateTask delegateTask, String assigneeType, Optional<CamundaProperty> assigneeOptional) {
    if (assigneeType.equals("group")) {
      assigneeOptional.ifPresent(assignee -> delegateTask.addCandidateGroup(assigneeOptional.get().getCamundaValue()));
    } else if (assigneeType.equals("user")) {
      assigneeOptional.ifPresent(assignee ->delegateTask.setAssignee(assigneeOptional.get().getCamundaValue()));
    }
  }

  private void assignTaskToGroup(DelegateTask delegateTask, Optional<CamundaProperty> algorithmOptional, String dmnOutputVariable, Map<String, Object> dmnOutput) {
    delegateTask.addCandidateGroup(dmnOutput.get(dmnOutputVariable).toString());
    if (isValidCamundaValue(algorithmOptional)) {
      if(algorithmOptional.isPresent()) {
        taskAssignmentService.setAssigneeByAlgorithm(delegateTask, algorithmOptional.get().getCamundaValue());
      }
      else {
        throw new EvaluationException(ALGORITHM_NOT_FOUND,ALGORITHM_NOT_FOUND);
      }
    }
  }

  private void generateDMSDocument(DelegateTask delegateTask, Optional<CamundaProperty> documentTypeIdOptional, Map<String, Object> variables) {
    if(documentTypeIdOptional.isPresent()) {
      log.info(String.format(DOCUMENT_GENERATION_START, documentTypeIdOptional.get().getCamundaValue()));
      List<PropertiesModel> properties = this.appUtilService.getProperties(DOCUMENT_TYPE_AND_TEMPLATE_MAPPING);
      String templateId = getProperty(properties, documentTypeIdOptional.get().getCamundaValue(), TEMPLATE_ID_PROP);
      String documentName = getProperty(properties, documentTypeIdOptional.get().getCamundaValue(), DOCUMENT_NAME_PROP);
      String documentDescription = getProperty(properties, documentTypeIdOptional.get().getCamundaValue(), DOCUMENT_DESCRIPTION_PROP);
      String documentPath = getProperty(properties, documentTypeIdOptional.get().getCamundaValue(), DOCUMENT_PATH_PROP);

      PublishRequestModel publishRequestModel = new PublishRequestModel();
      publishRequestModel.setDocumentTypeId(documentTypeIdOptional.get().getCamundaValue());
      publishRequestModel.setData(variables);
      publishRequestModel.setTemplateId(templateId);
      publishRequestModel.setDocumentDescription(documentDescription);
      publishRequestModel.setDocumentName(documentName + "_" + ZonedDateTime.now().toInstant().toEpochMilli());
      publishRequestModel.setDocumentPath(constructDocumentPath(documentPath, variables));

      Map<String, Object> response = this.dmsWrapperService.generateDocument(publishRequestModel);
      if (response.containsKey(ID)) {
        delegateTask.setVariableLocal(DOCUMENT_ID, response.get(ID));
        log.info(String.format(DOCUMENTATION_GENERATION_END, response.get(ID)));
      }
    }
    else
    {
      throw new EvaluationException(DOCUMENT_TYPE_ID_NOT_FOUND, DOCUMENT_TYPE_ID_NOT_FOUND);
    }
  }

  private Optional<CamundaProperty> getCamundaProperty(Collection<CamundaProperty> camundaProperties, String propertyName)
    {
        return camundaProperties.stream()
                .filter(camundaProperty -> camundaProperty.getCamundaName() != null && camundaProperty.getCamundaName().equals(propertyName)).findFirst();
    }

    private String getProperty(List<PropertiesModel> properties, String category, String propertyName)
    {
        return properties.stream().filter(prop -> prop.getCategory().equals(category) && prop.getKey().equals(propertyName)).findFirst()
                .orElseThrow(() -> new RuntimeException(String.format(globalMessageSource.get(MISSING_PROPERTY),propertyName))).getValue();
    }
    private String constructDocumentPath(String documentPathProp, Map<String, Object> processVariables)
    {
        if(documentPathProp.contains(":"))
        {
            String[] paths = documentPathProp.split("/");
            for(String path : paths)
            {
                if(path.contains(":"))
                {
                    String key = path.replace(":", "");
                    String value = Optional.ofNullable(processVariables.get(key)).map(Object::toString).orElseThrow(() -> new RuntimeException(String.format(globalMessageSource.get(UNABLE_TO_FRAME_DOCUMENT_PATH), key)));
                    documentPathProp = documentPathProp.replace(path, value);
                }
            }
        }
        return documentPathProp;
    }
}
