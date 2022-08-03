package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.model.InvokeChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;

@Component
@AllArgsConstructor
public class CustomExecutionListener
{
    private ChecklistService checklistService;

    @EventListener(condition = EXECUTION_END_EVENT_LISTENER_CONDITION)
    public void endEvent(DelegateExecution delegateExecution)
    {
        String checklistInstanceForProcessInstanceVariable = CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE + delegateExecution.getProcessInstanceId();
        if((delegateExecution.getBpmnModelElementInstance()!=null && delegateExecution.getBpmnModelElementInstance().getElementType() !=null && delegateExecution.getBpmnModelElementInstance().getElementType().getTypeName() !=null && !delegateExecution.getBpmnModelElementInstance().getElementType().getTypeName().equals(END_EVENT_ACTIVITY)) && delegateExecution.hasVariable(checklistInstanceForProcessInstanceVariable))
        {
            ChecklistInstanceResponseModel checklistInstanceResponseModel = this.checklistService.getChecklistInstanceById(delegateExecution.getVariable(checklistInstanceForProcessInstanceVariable).toString());
            if(!checklistInstanceResponseModel.getStatus().equals(COMPLETE) && !checklistInstanceResponseModel.getStatus().equals(COMPLETE_WITH_OVERRIDE))
            {
                throw new RuntimeException(CHECKLIST_ITEMS_INCOMPLETE_EXCEPTION);
            }
        }
    }

    @EventListener(condition = EXECUTION_START_EVENT_LISTENER_CONDITION)
    public void startEvent(DelegateExecution delegateExecution)
    {
        String checklistInstanceForProcessInstanceVariable = CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE + delegateExecution.getProcessInstanceId();
        if(delegateExecution.getBpmnModelElementInstance() == null || (delegateExecution.getBpmnModelElementInstance()!=null && delegateExecution.getBpmnModelElementInstance().getElementType() !=null && delegateExecution.getBpmnModelElementInstance().getElementType().getTypeName() !=null && !delegateExecution.getBpmnModelElementInstance().getElementType().getTypeName().equals(START_EVENT_ACTIVITY)) || delegateExecution.hasVariable(checklistInstanceForProcessInstanceVariable))
        {
            return;
        }
        ExtensionElements extensionElements = delegateExecution.getBpmnModelElementInstance().getExtensionElements();
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
        if(checklistIdOptional.isPresent())
        {
            String businessKey = delegateExecution.getBusinessKey();
            InvokeChecklistInstanceResponseModel checklistInstanceResponseModel = this.checklistService.invokeChecklist(checklistIdOptional.get().getCamundaValue(), businessKey, delegateExecution.getVariables());
            delegateExecution.setVariable(checklistInstanceForProcessInstanceVariable, checklistInstanceResponseModel.getChecklistInstanceId());
        }
    }

    private Optional<CamundaProperty> getCamundaProperty(Collection<CamundaProperty> camundaProperties, String propertyName)
    {
        return camundaProperties.stream()
                .filter(camundaProperty -> camundaProperty.getCamundaName() != null && camundaProperty.getCamundaName().equals(propertyName)).findFirst();
    }

}
