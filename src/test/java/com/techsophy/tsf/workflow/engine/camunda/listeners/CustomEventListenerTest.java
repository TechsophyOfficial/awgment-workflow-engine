package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CustomEventListenerTest {
    @Mock
    DelegateTask delegateTask;
    @Mock
    GlobalMessageSource globalMessageSource;
    @Mock
    ChecklistService checklistService;
    @Mock
    DelegateExecution delegateExecution;
    @Mock
    CamundaProperty camundaProperty;
    @InjectMocks
    CustomEventListener customEventListener;

    Map<String,Object> map = new HashMap<>();
    List<String> list = new ArrayList<>();
    @BeforeEach
    public void init()
    {
        map.put("key","abc");
        map.put("key2","abc");
        list.add("abc");
        list.add("qwe");
        delegateTask.setName("delegate");
        delegateTask.setVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST,CHECKLIST_ITEM_INSTANCE_ID_LIST);
    }


    @Test
    void onTaskCompleteEventTest(){
        ChecklistInstanceResponseModel checklistInstanceResponseModel = new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,true,"reviewer","type",true,true,"createdbyid","updatedbyid","createdby","updatedby", Instant.now(),Instant.now(),map,list,List.of(map));
        Mockito.when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        Mockito.when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        Mockito.when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(List.of("abc"));
        Mockito.when(delegateTask.getVariable(CHECKLIST_INSTANCE_ID)).thenReturn("abc");
        Mockito.when(checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString())).thenReturn(checklistInstanceResponseModel);
        customEventListener.onTaskCompleteEvent(delegateTask);
        Mockito.verify(delegateTask,times(2)).getVariable(CHECKLIST_INSTANCE_ID);

    }

    @Test
    void onTaskCompleteEventExceptionTest(){
        ChecklistInstanceResponseModel checklistInstanceResponseModel = new ChecklistInstanceResponseModel("1",true,true,"ok","1","cat",ASSIGNEE,true,"reviewer","type",true,true,"createdbyid","updatedbyid","createdby","updatedby", Instant.now(),Instant.now(),map,list,List.of(map));
        Mockito.when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        Mockito.when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        Mockito.when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(List.of("abc"));
        Mockito.when(delegateTask.getVariable(CHECKLIST_INSTANCE_ID)).thenReturn("abc");
        Mockito.when(checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString())).thenReturn(checklistInstanceResponseModel);
        Assertions.assertThrows(RuntimeException.class,()->customEventListener.onTaskCompleteEvent(delegateTask));
    }

    @Test
    void onTaskCompleteEventIllegalArgumentExceptionTest(){
        Mockito.when(delegateTask.getExecution()).thenReturn(delegateExecution);
        Mockito.when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        Mockito.when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        Mockito.when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn("abc");
        Mockito.when(globalMessageSource.get(anyString(),any())).thenReturn("abc");
        Assertions.assertThrows(RuntimeException.class,()->customEventListener.onTaskCompleteEvent(delegateTask));
    }

    @Test
    void onTaskCreatedEventNullTest(){
        UserTask userTask = mock(UserTask.class);
        Mockito.when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }

    @Test
    void onTaskCreatedEventZeroCountTest(){
        UserTask userTask = mock(UserTask.class);
        Query<ModelElementInstance> query = mock(Query.class);
        Query<CamundaProperties> propertiesQuery = mock(Query.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Mockito.when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        Mockito.when(userTask.getExtensionElements()).thenReturn(extensionElements);
        Mockito.when(extensionElements.getElementsQuery()).thenReturn(query);
        Mockito.when(query.filterByType(CamundaProperties.class)).thenReturn(propertiesQuery);
        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }

    @Test
    void onTaskCreatedEventWithCountValueTest(){
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        Collection<CamundaProperty> cproperty=mock(Collection.class);
        cproperty.add(camundaProperty);
        System.out.println("text "+camundaProperty.getCamundaName());
        UserTask userTask = mock(UserTask.class);
        Mockito.when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Mockito.when(userTask.getExtensionElements()).thenReturn(extensionElements);
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElement = mock(Query.class);
        Mockito.when(extensionElements.getElementsQuery()).thenReturn(modelElement);
        Mockito.when(modelElement.filterByType(CamundaProperties.class)).thenReturn(query);
        Mockito.when(query.count()).thenReturn(1);
        Mockito.when(query.singleResult()).thenReturn(camundaProperties);
        Mockito.when(camundaProperties.getCamundaProperties()).thenReturn(cproperty);
        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }
}
