package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistModel;
import com.techsophy.tsf.workflow.engine.camunda.model.InvokeChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.model.PropertiesModel;
import com.techsophy.tsf.workflow.engine.camunda.service.AppUtilService;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import com.techsophy.tsf.workflow.engine.camunda.service.DMNService;
import com.techsophy.tsf.workflow.engine.camunda.service.DMSWrapperService;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.TaskAssignmentService;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomEventListenerTest {
    @Mock
    DelegateTask delegateTask;
    @Mock
    GlobalMessageSource globalMessageSource;
    @Mock
    ChecklistService checklistService;
    @Mock
    DMSWrapperService dmsWrapperService;
    @Mock
    AppUtilService appUtilService;
    @Mock
    DelegateExecution delegateExecution;
    @Mock
    CamundaProperty camundaProperty;
    @Mock
    DMNService dmnService;
    @Mock
    TaskAssignmentService taskAssignmentService;
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
        when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(List.of("abc"));
        when(delegateTask.getVariable(CHECKLIST_INSTANCE_ID)).thenReturn("abc");
        when(checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString())).thenReturn(checklistInstanceResponseModel);
        customEventListener.onTaskCompleteEvent(delegateTask);
        Mockito.verify(delegateTask,times(2)).getVariable(CHECKLIST_INSTANCE_ID);

    }

    @Test
    void onTaskCompleteEventExceptionTest(){
        ChecklistInstanceResponseModel checklistInstanceResponseModel = new ChecklistInstanceResponseModel("1",true,true,"ok","1","cat",ASSIGNEE,true,"reviewer","type",true,true,"createdbyid","updatedbyid","createdby","updatedby", Instant.now(),Instant.now(),map,list,List.of(map));
        when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(List.of("abc"));
        when(delegateTask.getVariable(CHECKLIST_INSTANCE_ID)).thenReturn("abc");
        when(checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString())).thenReturn(checklistInstanceResponseModel);
        Assertions.assertThrows(RuntimeException.class,()->customEventListener.onTaskCompleteEvent(delegateTask));
    }

    @Test
    void onTaskCompleteEventIllegalArgumentExceptionTest(){
        when(delegateTask.getExecution()).thenReturn(delegateExecution);
        when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn("abc");
        when(globalMessageSource.get(anyString(),any())).thenReturn("abc");
        Assertions.assertThrows(RuntimeException.class,()->customEventListener.onTaskCompleteEvent(delegateTask));
    }

    @Test
    void onTaskCreatedEventNullTest(){
        UserTask userTask = mock(UserTask.class);
        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }

    @Test
    void onTaskCreatedEventZeroCountTest(){
        UserTask userTask = mock(UserTask.class);
        Query<ModelElementInstance> query = mock(Query.class);
        Query<CamundaProperties> propertiesQuery = mock(Query.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        when(userTask.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(query);
        when(query.filterByType(CamundaProperties.class)).thenReturn(propertiesQuery);
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

    @Test
    void onTaskCreatedEventRuntimeExceptionTest(){
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        when(camundaProperty.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty.getCamundaId()).thenReturn("id");
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        UserTask userTask = mock(UserTask.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElement = mock(Query.class);
        Optional<CamundaProperty> camundaPropertyOptional = Optional.of(camundaProperty);
        Map<String,Object> map = new HashMap<>();
        map.put(RESULT,"value");
        List<Map<String,Object>> resultOptional = new ArrayList<>();
        resultOptional.add(map);

        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        when(userTask.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElement);
        when(modelElement.filterByType(CamundaProperties.class)).thenReturn(query);
        when(query.count()).thenReturn(1);
        when(query.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("executionRule");
        when(camundaPropertyOptional.get().getCamundaValue()).thenReturn("abc");
        when(dmnService.executeDMN(any(),any())).thenReturn(resultOptional);
        Assertions.assertThrows(RuntimeException.class,()->customEventListener.onTaskCreatedEvent(delegateTask));
    }

    @Test
    void onTaskCreatedEventCountValueTest(){
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty3 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty4 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty5 = mock(CamundaProperty.class);

        when(camundaProperty1.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty1.getCamundaValue()).thenReturn("group");
        when(camundaProperty1.getCamundaId()).thenReturn("id");
        when(camundaProperty2.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty2.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty2.getCamundaId()).thenReturn("id");
        when(camundaProperty3.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty3.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty3.getCamundaId()).thenReturn("id");
        when(camundaProperty4.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty4.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty4.getCamundaId()).thenReturn("id");
        when(camundaProperty5.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty5.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty5.getCamundaId()).thenReturn("id");

        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        property.add(camundaProperty3);
        property.add(camundaProperty4);
        property.add(camundaProperty5);
        UserTask userTask = mock(UserTask.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElement = mock(Query.class);
        Optional<CamundaProperty> camundaPropertyOptional = Optional.of(camundaProperty4);
        Optional<CamundaProperty> documentTypeIdOptional = Optional.of(camundaProperty5);
        Map<String,Object> map = new HashMap<>();
        map.put("group","value");
        map.put(ID,"value");
        List<Map<String,Object>> resultOptional = new ArrayList<>();
        resultOptional.add(map);
        List<String> groupList = new ArrayList<>();
        groupList.add("abc");
        groupList.add("qwe");
        ChecklistModel checklistModel = new ChecklistModel(ID,groupList,Instant.now(),CREATED,CREATED,"updatedbyid","updatedby",Instant.now());
        checklistModel.setChecklist(ASSIGNEE,"value");
        InvokeChecklistInstanceResponseModel invokeChecklistInstanceResponseModel = new InvokeChecklistInstanceResponseModel(CHECKLIST_INSTANCE_ID);
        PropertiesModel propertiesModel = new PropertiesModel(COMPONENT,TEMPLATE_ID_PROP,"value");
        PropertiesModel propertiesModel1 = new PropertiesModel(COMPONENT,DOCUMENT_NAME_PROP,"value");
        PropertiesModel propertiesModel2 = new PropertiesModel(COMPONENT,DOCUMENT_DESCRIPTION_PROP,"value");
        PropertiesModel propertiesModel3 = new PropertiesModel(COMPONENT, DOCUMENT_PATH_PROP,"value");

        List<PropertiesModel> properties = new ArrayList<>();
        properties.add(propertiesModel);
        properties.add(propertiesModel1);
        properties.add(propertiesModel2);
        properties.add(propertiesModel3);

        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        when(userTask.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElement);
        when(modelElement.filterByType(CamundaProperties.class)).thenReturn(query);
        when(query.count()).thenReturn(1);
        when(query.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("checklistId");
        when(camundaProperty4.getCamundaName()).thenReturn("assignee");
        when(camundaProperty1.getCamundaName()).thenReturn("assigneeType");
        when(camundaProperty2.getCamundaName()).thenReturn("assignmentRule");
        when(camundaProperty3.getCamundaName()).thenReturn("algorithm");
        when(camundaProperty5.getCamundaName()).thenReturn("documentTypeId");
        when(camundaPropertyOptional.get().getCamundaValue()).thenReturn("${group}");
        when(dmnService.executeDMN(any(),any())).thenReturn(resultOptional);
        when(checklistService.getChecklistById(any())).thenReturn(checklistModel);
        when(delegateTask.getExecution()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("execution");
        when(checklistService.invokeChecklist(any(),anyString(),any())).thenReturn(invokeChecklistInstanceResponseModel);
        when(appUtilService.getProperties(any())).thenReturn(properties);
        when(documentTypeIdOptional.get().getCamundaValue()).thenReturn(COMPONENT);
        when(dmsWrapperService.generateDocument(any())).thenReturn(map);
        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }

    @Test
    void onTaskCreatedEventInvalidAssignmentRuleTest(){
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty3 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty4 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty5 = mock(CamundaProperty.class);

        when(camundaProperty.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty.getCamundaId()).thenReturn("id");
        when(camundaProperty1.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty1.getCamundaValue()).thenReturn("group");
        when(camundaProperty1.getCamundaId()).thenReturn("id");
        when(camundaProperty2.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty2.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty2.getCamundaId()).thenReturn("id");
        when(camundaProperty3.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty3.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty3.getCamundaId()).thenReturn("id");
        when(camundaProperty4.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty4.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty4.getCamundaId()).thenReturn("id");
        when(camundaProperty5.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty5.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty5.getCamundaId()).thenReturn("id");

        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        property.add(camundaProperty3);
        property.add(camundaProperty4);
        property.add(camundaProperty5);
        UserTask userTask = mock(UserTask.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElement = mock(Query.class);
        Optional<CamundaProperty> camundaPropertyOptional = Optional.of(camundaProperty4);
        Optional<CamundaProperty> documentTypeIdOptional = Optional.of(camundaProperty5);
        Map<String,Object> map = new HashMap<>();
        map.put("group","value");
        map.put(ID,"value");
        List<Map<String,Object>> resultOptional = new ArrayList<>();
        resultOptional.add(map);
        List<String> groupList = new ArrayList<>();
        groupList.add("abc");
        groupList.add("qwe");
        ChecklistModel checklistModel = new ChecklistModel(ID,groupList,Instant.now(),CREATED,CREATED,"updatedbyid","updatedby",Instant.now());
        checklistModel.setChecklist(ASSIGNEE,"value");
        InvokeChecklistInstanceResponseModel invokeChecklistInstanceResponseModel = new InvokeChecklistInstanceResponseModel(CHECKLIST_INSTANCE_ID);
        PropertiesModel propertiesModel = new PropertiesModel(COMPONENT,TEMPLATE_ID_PROP,"value");
        PropertiesModel propertiesModel1 = new PropertiesModel(COMPONENT,DOCUMENT_NAME_PROP,"value");
        PropertiesModel propertiesModel2 = new PropertiesModel(COMPONENT,DOCUMENT_DESCRIPTION_PROP,"value");
        PropertiesModel propertiesModel3 = new PropertiesModel(COMPONENT, DOCUMENT_PATH_PROP,"value");

        List<PropertiesModel> properties = new ArrayList<>();
        properties.add(propertiesModel);
        properties.add(propertiesModel1);
        properties.add(propertiesModel2);
        properties.add(propertiesModel3);

        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        when(userTask.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElement);
        when(modelElement.filterByType(CamundaProperties.class)).thenReturn(query);
        when(query.count()).thenReturn(1);
        when(query.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("checklistId");
        when(camundaProperty4.getCamundaName()).thenReturn("assignee");
        when(camundaProperty1.getCamundaName()).thenReturn("assigneeType");
        when(camundaProperty2.getCamundaName()).thenReturn("assignmentRule");
        when(camundaProperty3.getCamundaName()).thenReturn("algorithm");
        when(camundaProperty5.getCamundaName()).thenReturn("documentTypeId");
        when(camundaPropertyOptional.get().getCamundaValue()).thenReturn("${group}");
        when(dmnService.executeDMN(any(),any())).thenReturn(resultOptional);
        when(checklistService.getChecklistById(any())).thenReturn(checklistModel);
        when(delegateTask.getExecution()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("execution");
        when(checklistService.invokeChecklist(any(),anyString(),any())).thenReturn(invokeChecklistInstanceResponseModel);
        when(appUtilService.getProperties(any())).thenReturn(properties);
        when(documentTypeIdOptional.get().getCamundaValue()).thenReturn(COMPONENT);
        when(dmsWrapperService.generateDocument(any())).thenReturn(map);

        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }

    @Test
    void onTaskCreatedEventWithCamundaValueTest(){
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty3 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty4 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty5 = mock(CamundaProperty.class);

        when(camundaProperty1.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty1.getCamundaValue()).thenReturn("user");
        when(camundaProperty1.getCamundaId()).thenReturn("id");
        when(camundaProperty2.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty2.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty2.getCamundaId()).thenReturn("id");
        when(camundaProperty3.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty3.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty3.getCamundaId()).thenReturn("id");
        when(camundaProperty4.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty4.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty4.getCamundaId()).thenReturn("id");
        when(camundaProperty5.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty5.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty5.getCamundaId()).thenReturn("id");

        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        property.add(camundaProperty3);
        property.add(camundaProperty4);
        property.add(camundaProperty5);
        UserTask userTask = mock(UserTask.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElement = mock(Query.class);
        Optional<CamundaProperty> camundaPropertyOptional = Optional.of(camundaProperty4);
        Optional<CamundaProperty> documentTypeIdOptional = Optional.of(camundaProperty5);
        Map<String,Object> map = new HashMap<>();
        map.put("group","value");
        map.put(ID,"value");
        List<Map<String,Object>> resultOptional = new ArrayList<>();
        resultOptional.add(map);
        List<String> groupList = new ArrayList<>();
        groupList.add("abc");
        groupList.add("qwe");
        ChecklistModel checklistModel = new ChecklistModel(ID,groupList,Instant.now(),CREATED,CREATED,"updatedbyid","updatedby",Instant.now());
        checklistModel.setChecklist(ASSIGNEE,"value");
        InvokeChecklistInstanceResponseModel invokeChecklistInstanceResponseModel = new InvokeChecklistInstanceResponseModel(CHECKLIST_INSTANCE_ID);
        PropertiesModel propertiesModel = new PropertiesModel(COMPONENT,TEMPLATE_ID_PROP,"value");
        PropertiesModel propertiesModel1 = new PropertiesModel(COMPONENT,DOCUMENT_NAME_PROP,"value");
        PropertiesModel propertiesModel2 = new PropertiesModel(COMPONENT,DOCUMENT_DESCRIPTION_PROP,"value");
        PropertiesModel propertiesModel3 = new PropertiesModel(COMPONENT, DOCUMENT_PATH_PROP,"value");

        List<PropertiesModel> properties = new ArrayList<>();
        properties.add(propertiesModel);
        properties.add(propertiesModel1);
        properties.add(propertiesModel2);
        properties.add(propertiesModel3);

        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        when(userTask.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElement);
        when(modelElement.filterByType(CamundaProperties.class)).thenReturn(query);
        when(query.count()).thenReturn(1);
        when(query.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("checklistId");
        when(camundaProperty4.getCamundaName()).thenReturn("assignee");
        when(camundaProperty1.getCamundaName()).thenReturn("assigneeType");
        when(camundaProperty2.getCamundaName()).thenReturn("assignmentRule");
        when(camundaProperty3.getCamundaName()).thenReturn("algorithm");
        when(camundaProperty5.getCamundaName()).thenReturn("documentTypeId");
        when(camundaPropertyOptional.get().getCamundaValue()).thenReturn("${group}");
        when(dmnService.executeDMN(any(),any())).thenReturn(resultOptional);
        when(checklistService.getChecklistById(any())).thenReturn(checklistModel);
        when(delegateTask.getExecution()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("execution");
        when(checklistService.invokeChecklist(any(),anyString(),any())).thenReturn(invokeChecklistInstanceResponseModel);
        when(appUtilService.getProperties(any())).thenReturn(properties);
        when(documentTypeIdOptional.get().getCamundaValue()).thenReturn(COMPONENT);
        when(dmsWrapperService.generateDocument(any())).thenReturn(map);

        customEventListener.onTaskCreatedEvent(delegateTask);
        Mockito.verify(delegateTask,times(1)).getBpmnModelElementInstance();
    }

    @Test
    void onTaskCreatedEventWithExceptionTest(){
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty3 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty4 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty5 = mock(CamundaProperty.class);

        when(camundaProperty1.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty1.getCamundaValue()).thenReturn("group");
        when(camundaProperty1.getCamundaId()).thenReturn("id");
        when(camundaProperty2.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty2.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty2.getCamundaId()).thenReturn("id");
        when(camundaProperty3.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty3.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty3.getCamundaId()).thenReturn("id");
        when(camundaProperty4.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty4.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty4.getCamundaId()).thenReturn("id");
        when(camundaProperty5.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty5.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty5.getCamundaId()).thenReturn("id");

        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        property.add(camundaProperty3);
        property.add(camundaProperty4);
        property.add(camundaProperty5);
        UserTask userTask = mock(UserTask.class);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElement = mock(Query.class);
        Optional<CamundaProperty> camundaPropertyOptional = Optional.of(camundaProperty4);
        Optional<CamundaProperty> documentTypeIdOptional = Optional.of(camundaProperty5);
        Map<String,Object> map = new HashMap<>();
        map.put("group","value");
        map.put(ID,"value");
        List<Map<String,Object>> resultOptional = new ArrayList<>();
        resultOptional.add(map);
        List<String> groupList = new ArrayList<>();
        groupList.add("abc");
        groupList.add("qwe");
        ChecklistModel checklistModel = new ChecklistModel(ID,groupList,Instant.now(),CREATED,CREATED,"updatedbyid","updatedby",Instant.now());
        checklistModel.setChecklist(ASSIGNEE,"value");
        InvokeChecklistInstanceResponseModel invokeChecklistInstanceResponseModel = new InvokeChecklistInstanceResponseModel(CHECKLIST_INSTANCE_ID);
        PropertiesModel propertiesModel = new PropertiesModel(COMPONENT,TEMPLATE_ID_PROP,"value");
        PropertiesModel propertiesModel1 = new PropertiesModel(COMPONENT,DOCUMENT_NAME_PROP,"value");
        PropertiesModel propertiesModel2 = new PropertiesModel(COMPONENT,DOCUMENT_DESCRIPTION_PROP,"value");
        PropertiesModel propertiesModel3 = new PropertiesModel(COMPONENT, DOCUMENT_PATH_PROP,"http://va:lue");

        List<PropertiesModel> properties = new ArrayList<>();
        properties.add(propertiesModel);
        properties.add(propertiesModel1);
        properties.add(propertiesModel2);
        properties.add(propertiesModel3);

        when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
        when(userTask.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElement);
        when(modelElement.filterByType(CamundaProperties.class)).thenReturn(query);
        when(query.count()).thenReturn(1);
        when(query.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("checklistId");
        when(camundaProperty4.getCamundaName()).thenReturn("assignee");
        when(camundaProperty1.getCamundaName()).thenReturn("assigneeType");
        when(camundaProperty2.getCamundaName()).thenReturn("assignmentRule");
        when(camundaProperty3.getCamundaName()).thenReturn("algorithm");
        when(camundaProperty5.getCamundaName()).thenReturn("documentTypeId");
        when(camundaPropertyOptional.get().getCamundaValue()).thenReturn("${group}");
        when(dmnService.executeDMN(any(),any())).thenReturn(resultOptional);
        when(checklistService.getChecklistById(any())).thenReturn(checklistModel);
        when(delegateTask.getExecution()).thenReturn(delegateExecution);
        when(delegateExecution.getBusinessKey()).thenReturn("execution");
        when(checklistService.invokeChecklist(any(),anyString(),any())).thenReturn(invokeChecklistInstanceResponseModel);
        when(appUtilService.getProperties(any())).thenReturn(properties);
        when(documentTypeIdOptional.get().getCamundaValue()).thenReturn(COMPONENT);
        when(dmsWrapperService.generateDocument(any())).thenReturn(map);
        Assertions.assertThrows(RuntimeException.class,()->customEventListener.onTaskCreatedEvent(delegateTask));
    }

}
