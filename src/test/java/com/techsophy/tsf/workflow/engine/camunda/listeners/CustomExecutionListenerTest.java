package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.model.InvokeChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.START_EVENT_ACTIVITY;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomExecutionListenerTest {
    @Mock
    ChecklistService checklistService;
    @Mock
    DelegateExecution delegateExecution;
    @InjectMocks
    CustomExecutionListener customExecutionListener;

    Map<String,Object> map = new HashMap<>();
    List<String> list = new ArrayList<>();
    @BeforeEach
    public void init() {
        map.put("key", "abc");
        map.put("key2", "abc");
        list.add("abc");
        list.add("qwe");
    }

    @Test
    void endEventTest(){

        FlowElement flowElement = mock(FlowElement.class);
        ModelElementType modelElementType = mock(ModelElementType.class);
        ChecklistInstanceResponseModel checklistInstanceResponseModel =
                new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,
                        true,"reviewer","type",true,true,
                        "createdbyid","updatedbyid","createdby","updatedby", Instant.now(),
                        Instant.now(),map,list, List.of(map));

        Mockito.when(delegateExecution.getProcessInstanceId()).thenReturn("processInstanceId");
        Mockito.when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
        Mockito.when(flowElement.getElementType()).thenReturn(modelElementType);
        when(modelElementType.getTypeName()).thenReturn("abc");
        when(delegateExecution.hasVariable(any())).thenReturn(true);
        when(delegateExecution.getVariable(any())).thenReturn(CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE + "abc");
        when(checklistService.getChecklistInstanceById(anyString())).thenReturn(checklistInstanceResponseModel);
        customExecutionListener.endEvent(delegateExecution);
        verify(checklistService,times(1)).getChecklistInstanceById(anyString());
    }

    @Test
    void endEventExceptionTest(){

        FlowElement flowElement = mock(FlowElement.class);
        ModelElementType modelElementType = mock(ModelElementType.class);
        ChecklistInstanceResponseModel checklistInstanceResponseModel =
                new ChecklistInstanceResponseModel("1",true,true,"abc",CHECKLIST_ID,"cat",ASSIGNEE,
                        true,"reviewer","type",true,true,
                        "createdbyid","updatedbyid","createdby","updatedby", Instant.now(),
                        Instant.now(),map,list, List.of(map));

        Mockito.when(delegateExecution.getProcessInstanceId()).thenReturn("processInstanceId");
        Mockito.when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
        Mockito.when(flowElement.getElementType()).thenReturn(modelElementType);
        when(modelElementType.getTypeName()).thenReturn("abc");
        when(delegateExecution.hasVariable(any())).thenReturn(true);
        when(delegateExecution.getVariable(any())).thenReturn(CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE + "abc");
        when(checklistService.getChecklistInstanceById(anyString())).thenReturn(checklistInstanceResponseModel);
        Assertions.assertThrows(IllegalArgumentException.class,()->customExecutionListener.endEvent(delegateExecution));
    }

    @Test
    void startEventTest(){
        FlowElement flowElement = mock(FlowElement.class);
        ModelElementType modelElementType = mock(ModelElementType.class);
        ChecklistInstanceResponseModel checklistInstanceResponseModel =
                new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,
                        true,"reviewer","type",true,true,
                        "createdbyid","updatedbyid","createdby","updatedby", Instant.now(),
                        Instant.now(),map,list, List.of(map));
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);
        Query<CamundaProperties> query = mock(Query.class);
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        InvokeChecklistInstanceResponseModel invokeChecklistInstanceResponseModel = new InvokeChecklistInstanceResponseModel("abc");

        Mockito.when(delegateExecution.getProcessInstanceId()).thenReturn("processInstanceId");
        Mockito.when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
        Mockito.when(flowElement.getElementType()).thenReturn(modelElementType);
        when(flowElement.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        Mockito.when(modelElementInstanceQuery.filterByType(CamundaProperties.class)).thenReturn(query);
        when(modelElementType.getTypeName()).thenReturn(START_EVENT_ACTIVITY);
        when(delegateExecution.hasVariable(any())).thenReturn(false);
        when(delegateExecution.getVariable(any())).thenReturn(CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE + "abc");
        when(checklistService.getChecklistInstanceById(anyString())).thenReturn(checklistInstanceResponseModel);
        Mockito.when(query.count()).thenReturn(1);
        Mockito.when(query.singleResult()).thenReturn(camundaProperties);
        Mockito.when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(delegateExecution.getBusinessKey()).thenReturn("abc");
        when(camundaProperty.getCamundaName()).thenReturn("checklistId");
        when(checklistService.invokeChecklist(any(),any(),any())).thenReturn(invokeChecklistInstanceResponseModel);
        customExecutionListener.startEvent(delegateExecution);
        verify(checklistService,times(1)).invokeChecklist(any(),any(),any());
    }

    @Test
    void startEventWithReturnTest(){
        FlowElement flowElement = mock(FlowElement.class);
        ModelElementType modelElementType = mock(ModelElementType.class);
        ChecklistInstanceResponseModel checklistInstanceResponseModel =
                new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,
                        true,"reviewer","type",true,true,
                        "createdbyid","updatedbyid","createdby","updatedby", Instant.now(),
                        Instant.now(),map,list, List.of(map));
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);

        Mockito.when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
        Mockito.when(flowElement.getElementType()).thenReturn(modelElementType);
        when(flowElement.getExtensionElements()).thenReturn(extensionElements);
        when(modelElementType.getTypeName()).thenReturn("abc");
        when(delegateExecution.hasVariable(any())).thenReturn(false);
        customExecutionListener.startEvent(delegateExecution);
        verify(checklistService,times(0)).getChecklistInstanceById(anyString());
    }

    @Test
    void startEventReturnTest(){
        FlowElement flowElement = mock(FlowElement.class);
        ModelElementType modelElementType = mock(ModelElementType.class);
        ChecklistInstanceResponseModel checklistInstanceResponseModel =
                new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,
                        true,"reviewer","type",true,true,
                        "createdbyid","updatedbyid","createdby","updatedby", Instant.now(),
                        Instant.now(),map,list, List.of(map));
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);

        Mockito.when(delegateExecution.getProcessInstanceId()).thenReturn("processInstanceId");
        Mockito.when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
        Mockito.when(flowElement.getElementType()).thenReturn(modelElementType);
        when(flowElement.getExtensionElements()).thenReturn(null);
        when(modelElementType.getTypeName()).thenReturn(START_EVENT_ACTIVITY);
        when(delegateExecution.hasVariable(any())).thenReturn(false);
        customExecutionListener.startEvent(delegateExecution);
        verify(checklistService,times(0)).getChecklistInstanceById(anyString());
    }

    @Test
    void startEventQueryCountZeroTest(){
        FlowElement flowElement = mock(FlowElement.class);
        ModelElementType modelElementType = mock(ModelElementType.class);
        ChecklistInstanceResponseModel checklistInstanceResponseModel =
                new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,
                        true,"reviewer","type",true,true,
                        "createdbyid","updatedbyid","createdby","updatedby", Instant.now(),
                        Instant.now(),map,list, List.of(map));
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);
        Query<CamundaProperties> query = mock(Query.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);

        Mockito.when(delegateExecution.getProcessInstanceId()).thenReturn("processInstanceId");
        Mockito.when(delegateExecution.getBpmnModelElementInstance()).thenReturn(flowElement);
        Mockito.when(flowElement.getElementType()).thenReturn(modelElementType);
        when(flowElement.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        Mockito.when(modelElementInstanceQuery.filterByType(CamundaProperties.class)).thenReturn(query);
        when(modelElementType.getTypeName()).thenReturn(START_EVENT_ACTIVITY);
        when(delegateExecution.hasVariable(any())).thenReturn(false);
        Mockito.when(query.count()).thenReturn(0);
        customExecutionListener.startEvent(delegateExecution);
        verify(checklistService,times(0)).getChecklistInstanceById(anyString());
    }
}
