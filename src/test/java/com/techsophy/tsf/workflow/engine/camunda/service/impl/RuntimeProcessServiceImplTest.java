package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilter;
import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeFormService;
import com.techsophy.tsf.workflow.engine.camunda.service.WrapperService;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstanceQuery;
import org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.task.TaskQueryDto;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RuntimeProcessServiceImplTest {

    @Mock
    ObjectMapper objectMapper;
    @Mock
    TaskService taskService;
    @Mock
    RuntimeService runtimeService;
    @Mock
    HistoryService historyService;
    @Mock
    RuntimeFormService runtimeFormService;
    @Mock
    CaseService caseService;
    @Mock
    WrapperService wrapperService;
    @Mock
    GlobalMessageSource globalMessageSource;
    @Mock
    ProcessEngine processEngine;
    @Mock
    TaskQuery taskQuery;
    @Mock
    Task task;
    @Mock
    ProcessInstantiationBuilder processInstantiationBuilder;
    @Mock
    ProcessInstance processInstance;
    @InjectMocks
    RuntimeProcessServiceImpl runtimeProcessService;

    @Test
    void getNextTaskTest() throws IOException {
        FormSchema formSchema = new FormSchema("name", "id", Map.of("key", "value"), 1);
        FormioFormSchema formioFormSchema = new FormioFormSchema("display", null);

        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.processInstanceBusinessKey("123")).thenReturn(taskQuery);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(runtimeFormService.fetchFormById(any(), any())).thenReturn(formSchema);
        Mockito.when(objectMapper.convertValue(formSchema.getComponents(), FormioFormSchema.class)).thenReturn(formioFormSchema);

        String actualOutputFormKey = runtimeProcessService.getNextTask("123").getFormKey();
        String expectedOutputFormKey = formSchema.getId();

        Assertions.assertSame(expectedOutputFormKey, actualOutputFormKey);
    }

    @Test
    void completeAndFetchNextTaskTest() throws IOException {
        TaskDTO taskDTO = new TaskDTO("123", "taskId", Map.of("key", "value"));
        FormSchema formSchema = new FormSchema("name", "id", Map.of("key", "value"), 1);
        FormioFormSchema formioFormSchema = new FormioFormSchema("display", null);

        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.processInstanceBusinessKey("123")).thenReturn(taskQuery);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(runtimeFormService.fetchFormById(any(), any())).thenReturn(formSchema);
        Mockito.when(objectMapper.convertValue(formSchema.getComponents(), FormioFormSchema.class)).thenReturn(formioFormSchema);

        String actualOutputFormKey = runtimeProcessService.completeAndFetchNextTask(taskDTO).getFormKey();
        String expectedOutputFormKey = formSchema.getId();
        Assertions.assertSame(expectedOutputFormKey, actualOutputFormKey);
    }

    @Test
    void completeAndFetchNextTaskTestWhileFormKeyisNull() throws IOException {
        TaskDTO taskDTO = new TaskDTO("123", "taskId", Map.of("key", "value"));
        FormSchema formSchema = new FormSchema("name", "id", Map.of("key", "value"), 1);
        FormComponent formComponent = new FormComponent("label", "ph", "desc", true, "vo", null, "key",
                "fk", "type", true, null, "it", null, null, true, "action",
                "event", "provider", null, null, null, "storage", null, true, "url",
                "format", "dT", true, "html", "title", null, "pre", "suf", true, null);
        FormioFormSchema formioFormSchema = new FormioFormSchema("display", new FormComponent[]{formComponent});

        Mockito.when(task.getFormKey()).thenReturn("abc");
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.processInstanceBusinessKey("123")).thenReturn(taskQuery);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(runtimeFormService.fetchFormById(any(), any())).thenReturn(formSchema);
        Mockito.when(objectMapper.convertValue(formSchema.getComponents(), FormioFormSchema.class)).thenReturn(formioFormSchema);

        String actualOutputFormKey = runtimeProcessService.completeAndFetchNextTask(taskDTO).getFormKey();
        String expectedOutputFormKey = formSchema.getId();
        Assertions.assertSame(expectedOutputFormKey, actualOutputFormKey);
    }

    @Test
    void completeTaskTest() {
        TaskDTO taskDTO = new TaskDTO("123", "taskId", Map.of("key", "value"));
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.taskId(taskDTO.getTaskId())).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        runtimeProcessService.completeTask(taskDTO);
        verify(taskService, times(1)).complete(task.getId(), taskDTO.getVariables());
    }

    @Test
    void completeTaskWhileHasParentTaskTest() {
        TaskDTO taskDTO = new TaskDTO("123", "taskId", Map.of("key", "value"));
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.taskId(taskDTO.getTaskId())).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(task.getId()).thenReturn("taskId");
        Mockito.when(task.getParentTaskId()).thenReturn("taskId");
        runtimeProcessService.completeTask(taskDTO);
        verify(taskService, times(1)).complete(task.getId(), taskDTO.getVariables());
    }

    @Test
    void completeTaskTestWhileTaskIdIsNull() {
        TaskDTO taskDTO = new TaskDTO("123", null, Map.of("key", "value"));
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.taskId(taskDTO.getTaskId())).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(taskQuery.processInstanceBusinessKey(anyString())).thenReturn(taskQuery);

        runtimeProcessService.completeTask(taskDTO);
        verify(taskService, times(1)).complete(task.getId(), taskDTO.getVariables());
    }

    @Test
    void completeTaskTestWhileHasSubTasks() {
        TaskDTO taskDTO = new TaskDTO(null, null, Map.of("key", "value"));
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.taskId(taskDTO.getTaskId())).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(taskService.getSubTasks(taskDTO.getTaskId())).thenReturn(List.of(task));
        Assertions.assertThrows(IllegalArgumentException.class, () -> runtimeProcessService.completeTask(taskDTO));
    }

    @Test
    void completeTaskTestWhileThrowingException() {
        TaskDTO taskDTO = new TaskDTO(null, null, Map.of("key", "value"));
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.taskId(taskDTO.getTaskId())).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Assertions.assertThrows(IllegalArgumentException.class, () -> runtimeProcessService.completeTask(taskDTO));
    }

    @Test
    void updateProcessVariablesTest() throws IOException {
        FormSchema formSchema = new FormSchema("name", "id", Map.of("key", "value"), 1);
        FormComponent formComponent = new FormComponent("label", "ph", "desc", true, "vo", null, "key",
                "fk", "type", true, null, "it", null, null, true, "action",
                "event", "provider", null, null, null, "storage", null, true, "url",
                "format", "dT", true, "html", "title", null, "pre", "suf", true, null);
        FormioFormSchema formioFormSchema = new FormioFormSchema("display", new FormComponent[]{formComponent});
        ProcessVariablesDTO processVariablesDTO = new ProcessVariablesDTO("abc", Map.of("key", "value"));

        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.processInstanceBusinessKey(anyString())).thenReturn(taskQuery);
        Mockito.when(taskQuery.active()).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(taskQuery.singleResult()).thenReturn(task);
        Mockito.when(task.getFormKey()).thenReturn("abc");
        Mockito.when(objectMapper.convertValue(formSchema.getComponents(), FormioFormSchema.class)).thenReturn(formioFormSchema);
        Mockito.when(runtimeFormService.fetchFormById(any(), any())).thenReturn(formSchema);

        runtimeProcessService.updateProcessVariables(processVariablesDTO);
        verify(runtimeService, times(1)).setVariables(task.getExecutionId(), processVariablesDTO.getVariables());
    }

    @Test
    void createProcessInstanceTest() throws IOException {
        try (MockedStatic<OAuth2AndJwtAwareRequestFilter> mockedStatic = Mockito.mockStatic(OAuth2AndJwtAwareRequestFilter.class)) {
            mockedStatic.when(()->OAuth2AndJwtAwareRequestFilter.getTenantName()).thenReturn(Optional.of("abc"));
            HistoricVariableInstanceQuery historicVariableInstanceQuery = Mockito.mock(HistoricVariableInstanceQuery.class);
            HistoricVariableInstance historicVariableInstance = Mockito.mock(HistoricVariableInstance.class);
            ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
            ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO("key", Map.of("key", "val"), "bKey", "fKey");
            Map<String, Object> processVariables = processInstanceDTO.getVariables();
            FormSchema formSchema = new FormSchema("name", "id", Map.of("key", "value"), 1);
            FormComponent formComponent = new FormComponent("label", "ph", "desc", true, "vo", null, "key",
                    "fk", "type", true, null, "it", null, null, true, "action",
                    "event", "provider", null, null, null, "storage", null, true, "url",
                    "format", "dT", true, "html", "title", null, "pre", "suf", true, null);
            FormioFormSchema formioFormSchema = new FormioFormSchema("display", new FormComponent[]{formComponent});

            Mockito.when(objectMapper.convertValue(formSchema.getComponents(), FormioFormSchema.class)).thenReturn(formioFormSchema);
            Mockito.when(runtimeFormService.fetchFormById(any(), any())).thenReturn(formSchema);
            Mockito.when(runtimeService.startProcessInstanceByKey(processInstanceDTO.getProcessDefinitionKey(), processInstanceDTO.getBusinessKey(), processVariables)).thenReturn(processInstance);
            Mockito.when(historyService.createHistoricVariableInstanceQuery()).thenReturn(historicVariableInstanceQuery);
            Mockito.when(historicVariableInstanceQuery.processInstanceId(any())).thenReturn(historicVariableInstanceQuery);
            Mockito.when(historicVariableInstanceQuery.list()).thenReturn(List.of(historicVariableInstance));
            Mockito.when(historicVariableInstance.getName()).thenReturn("name");
            Mockito.when(historicVariableInstance.getValue()).thenReturn("value");
            Mockito.when(runtimeService.createProcessInstanceByKey(anyString())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.processDefinitionTenantId(anyString())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.businessKey(anyString())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.setVariables(any())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.execute()).thenReturn(processInstance);
            String actualOutput = runtimeProcessService.createProcessInstance(processInstanceDTO).getProcessInstanceId();
            String expectedOutput = processInstance.getProcessInstanceId();
            Assertions.assertSame(expectedOutput, actualOutput);
        }
    }

    @Test
    void createProcessInstanceThrowExceptionTest() throws IOException {
        try (MockedStatic<OAuth2AndJwtAwareRequestFilter> mockedStatic = Mockito.mockStatic(OAuth2AndJwtAwareRequestFilter.class)) {
            mockedStatic.when(()->OAuth2AndJwtAwareRequestFilter.getTenantName()).thenReturn(Optional.of("abc"));
            HistoricVariableInstanceQuery historicVariableInstanceQuery = Mockito.mock(HistoricVariableInstanceQuery.class);
            HistoricVariableInstance historicVariableInstance = Mockito.mock(HistoricVariableInstance.class);
            ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
            ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO("key", Map.of("key", "val"), "bKey", "fKey");
            Map<String, Object> processVariables = processInstanceDTO.getVariables();
            FormSchema formSchema = new FormSchema("name", "id", Map.of("key", "value"), 1);
            FormComponent formComponent = new FormComponent("label", "ph", "desc", true, "vo", null, "key",
                    "fk", "type", true, null, "it", null, null, true, "action",
                    "event", "provider", null, null, null, "storage", null, true, "url",
                    "format", "dT", true, "html", "title", null, "pre", "suf", true, null);
            FormioFormSchema formioFormSchema = new FormioFormSchema("display", new FormComponent[]{formComponent});

            Mockito.when(objectMapper.convertValue(formSchema.getComponents(), FormioFormSchema.class)).thenReturn(formioFormSchema);
            Mockito.when(runtimeFormService.fetchFormById(any(), any())).thenReturn(formSchema);
            Mockito.when(runtimeService.startProcessInstanceByKey(processInstanceDTO.getProcessDefinitionKey(), processInstanceDTO.getBusinessKey(), processVariables)).thenReturn(processInstance);
            Mockito.when(historyService.createHistoricVariableInstanceQuery()).thenReturn(historicVariableInstanceQuery);
            Mockito.when(historicVariableInstanceQuery.processInstanceId(any())).thenReturn(historicVariableInstanceQuery);
            Mockito.when(historicVariableInstanceQuery.list()).thenReturn(List.of(historicVariableInstance));
            Mockito.when(historicVariableInstance.getName()).thenReturn("errorMessage");
            Mockito.when(historicVariableInstance.getValue()).thenReturn("value");
            Mockito.when(runtimeService.createProcessInstanceByKey(anyString())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.processDefinitionTenantId(anyString())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.businessKey(anyString())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.setVariables(any())).thenReturn(processInstantiationBuilder);
            Mockito.when(processInstantiationBuilder.execute()).thenReturn(processInstance);
            Assertions.assertThrows(IllegalArgumentException.class, () -> runtimeProcessService.createProcessInstance(processInstanceDTO));
        }
    }

    @Test
    void createOrFetchProcessInstanceTest(){
        String expectedOutput = "InstanceId";
        ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO("key", Map.of("key", "val"), "bKey", "fKey");
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        when(processInstance.getProcessInstanceId()).thenReturn(expectedOutput);
        HistoricVariableInstanceQuery historicVariableInstanceQuery = Mockito.mock(HistoricVariableInstanceQuery.class);
        HistoricVariableInstance historicVariableInstance = Mockito.mock(HistoricVariableInstance.class);
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);
        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceBusinessKey(processInstanceDTO.getBusinessKey())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        Mockito.when(historyService.createHistoricVariableInstanceQuery()).thenReturn(historicVariableInstanceQuery);
        Mockito.when(historicVariableInstanceQuery.processInstanceId(any())).thenReturn(historicVariableInstanceQuery);
        Mockito.when(historicVariableInstanceQuery.list()).thenReturn(List.of(historicVariableInstance));
        Mockito.when(historicVariableInstance.getName()).thenReturn("name");
        Mockito.when(historicVariableInstance.getValue()).thenReturn("value");
        String actualOutput = runtimeProcessService.createOrFetchProcessInstance(processInstanceDTO).getProcessInstanceId();
        Assertions.assertSame(expectedOutput, actualOutput);
    }

@Test
void createOrFetchProcessInstanceTestWithNullProcessInstance() throws IOException {
    try (MockedStatic<OAuth2AndJwtAwareRequestFilter> mockedStatic = Mockito.mockStatic(OAuth2AndJwtAwareRequestFilter.class)) {
        mockedStatic.when(()->OAuth2AndJwtAwareRequestFilter.getTenantName()).thenReturn(Optional.of("abc"));
        String expectedOutput = "ProcessInstanceId";
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        FormComponent formComponent = new FormComponent("label", "ph", "desc", true, "vo", null, "key",
                "fk", "type", true, null, "it", null, null, true, "action",
                "event", "provider", null, null, null, "storage", null, true, "url",
                "format", "dT", true, "html", "title", null, "pre", "suf", true, null);
        FormioFormSchema formioFormSchema = new FormioFormSchema("display", new FormComponent[]{formComponent});
        ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO("key", map, "bKey", "fKey");
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        when(processInstance.getProcessInstanceId()).thenReturn(expectedOutput);
        HistoricVariableInstanceQuery historicVariableInstanceQuery = Mockito.mock(HistoricVariableInstanceQuery.class);
        HistoricVariableInstance historicVariableInstance = Mockito.mock(HistoricVariableInstance.class);
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);
        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceBusinessKey(processInstanceDTO.getBusinessKey())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.singleResult()).thenReturn(null);
        when(runtimeFormService.fetchFormById(any(), any())).thenReturn(new FormSchema("name", "id", map, 1));
        when(objectMapper.convertValue(any(), eq(FormioFormSchema.class))).thenReturn(formioFormSchema);
        Mockito.when(runtimeService.startProcessInstanceByKey(processInstanceDTO.getProcessDefinitionKey(), processInstanceDTO.getBusinessKey(), map)).thenReturn(processInstance);
        Mockito.when(historyService.createHistoricVariableInstanceQuery()).thenReturn(historicVariableInstanceQuery);
        Mockito.when(historicVariableInstanceQuery.processInstanceId(any())).thenReturn(historicVariableInstanceQuery);
        Mockito.when(historicVariableInstanceQuery.list()).thenReturn(List.of(historicVariableInstance));
        Mockito.when(historicVariableInstance.getName()).thenReturn("name");
        Mockito.when(historicVariableInstance.getValue()).thenReturn("value");
        Mockito.when(runtimeService.createProcessInstanceByKey(anyString())).thenReturn(processInstantiationBuilder);
        Mockito.when(processInstantiationBuilder.processDefinitionTenantId(anyString())).thenReturn(processInstantiationBuilder);
        Mockito.when(processInstantiationBuilder.businessKey(anyString())).thenReturn(processInstantiationBuilder);
        Mockito.when(processInstantiationBuilder.setVariables(any())).thenReturn(processInstantiationBuilder);
        Mockito.when(processInstantiationBuilder.execute()).thenReturn(processInstance);
        String actualOutput = runtimeProcessService.createOrFetchProcessInstance(processInstanceDTO).getProcessInstanceId();
        Assertions.assertSame(expectedOutput, actualOutput);
    }
}

    @Test
    void resumeProcessTest() {
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);
        MessageCorrelationBuilder messageCorrelationBuilder = Mockito.mock(MessageCorrelationBuilder.class);
        ResumeProcessRequestDto resumeProcessRequestDto = new ResumeProcessRequestDto();
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        Map<String, Object> variable = Map.of("key", "val");
        resumeProcessRequestDto.setProcessInstanceId("id");
        resumeProcessRequestDto.setBusinessKey("bKey");
        resumeProcessRequestDto.setVariables(variable);
        resumeProcessRequestDto.setMessage("msg");

        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceId(resumeProcessRequestDto.getProcessInstanceId())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        when(runtimeService.createMessageCorrelation(any())).thenReturn(messageCorrelationBuilder);
        when(messageCorrelationBuilder.processInstanceBusinessKey(any())).thenReturn(messageCorrelationBuilder);
        when(messageCorrelationBuilder.setVariables(any())).thenReturn(messageCorrelationBuilder);
        runtimeProcessService.resumeProcess(resumeProcessRequestDto);
        verify(runtimeService, times(1)).createProcessInstanceQuery();
    }


    @Test
    void resumeProcessWithGetBusinessKeyTest() {
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);
        MessageCorrelationBuilder messageCorrelationBuilder = Mockito.mock(MessageCorrelationBuilder.class);
        ResumeProcessRequestDto resumeProcessRequestDto = new ResumeProcessRequestDto();
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        Map<String, Object> variable = Map.of("key", "val");
        resumeProcessRequestDto.setProcessInstanceId(null);
        resumeProcessRequestDto.setBusinessKey("bKey");
        resumeProcessRequestDto.setVariables(variable);
        resumeProcessRequestDto.setMessage("msg");

        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceBusinessKey(resumeProcessRequestDto.getBusinessKey())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.unlimitedList()).thenReturn(List.of(processInstance));
        when(runtimeService.createMessageCorrelation(any())).thenReturn(messageCorrelationBuilder);
        when(messageCorrelationBuilder.processInstanceBusinessKey(any())).thenReturn(messageCorrelationBuilder);
        when(messageCorrelationBuilder.setVariables(any())).thenReturn(messageCorrelationBuilder);
        runtimeProcessService.resumeProcess(resumeProcessRequestDto);
        verify(runtimeService, times(1)).createProcessInstanceQuery();
    }


    @Test
    void getAllTasksTest() {
        TaskQueryDto taskQueryDto = Mockito.mock(TaskQueryDto.class);
        TaskQueryDTO taskQueryDTO = Mockito.mock(TaskQueryDTO.class);
        TaskInstanceDTO taskInstanceDTO = Mockito.mock(TaskInstanceDTO.class);
        TaskQuery taskQuery = Mockito.mock(TaskQuery.class);
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        Integer page = 1;
        Integer size = 1;

        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(taskQuery.orderByTaskCreateTime()).thenReturn(taskQuery);
        Mockito.when(objectMapper.convertValue(taskQueryDTO, TaskQueryDto.class)).thenReturn(taskQueryDto);
        Mockito.when(taskQueryDto.toQuery(processEngine)).thenReturn(taskQuery);
        Mockito.when(taskQuery.initializeFormKeys()).thenReturn(taskQuery);
        Mockito.when(taskQuery.orderByTaskCreateTime()).thenReturn(taskQuery);
        Mockito.when(taskQuery.desc()).thenReturn(taskQuery);
        Mockito.when(taskQuery.listPage(page * size, size)).thenReturn(List.of(task));

        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceId(task.getProcessInstanceId())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        Mockito.when(processInstance.getBusinessKey()).thenReturn("abc");
        Mockito.when(wrapperService.setExtensionElementsToTask(any(), any())).thenReturn(taskInstanceDTO);

        runtimeProcessService.getAllTasks(taskQueryDTO, page, size);
        verify(taskInstanceDTO, times(1)).setBusinessKey(anyString());
    }

    @Test
    void getSubTasksTest() {
        String taskId = "abc";
        Mockito.when(taskService.getSubTasks(taskId)).thenReturn(List.of(task));
        runtimeProcessService.getSubTasks(taskId);
        verify(objectMapper, times(1)).registerModule(any());
    }

    @Test
    void getSubTasksTestWhileThrowingException() {
        String taskId = "abc";
        Assertions.assertThrows(IllegalArgumentException.class, () -> runtimeProcessService.getSubTasks(taskId));
    }

    @Test
    void getAllHistoryTaskTest() {
        HistoricTaskInstance historicTaskInstance = Mockito.mock(HistoricTaskInstance.class);
        HistoricQueryInstanceDTO historicQueryInstanceDTO = Mockito.mock(HistoricQueryInstanceDTO.class);
        HistoricTaskInstanceQuery historicTaskInstanceQuery = Mockito.mock(HistoricTaskInstanceQuery.class);
        HistoricTaskInstanceQueryDto historicTaskInstanceQueryDto = Mockito.mock(HistoricTaskInstanceQueryDto.class);
        Integer page = 1, size = 1;

        Mockito.when(historyService.createHistoricTaskInstanceQuery()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.orderByHistoricActivityInstanceStartTime()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(objectMapper.convertValue(historicQueryInstanceDTO, HistoricTaskInstanceQueryDto.class)).thenReturn(historicTaskInstanceQueryDto);
        Mockito.when(historicTaskInstanceQueryDto.toQuery(processEngine)).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.orderByHistoricActivityInstanceStartTime()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.desc()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.endOr()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.endOr().listPage(page * size, size)).thenReturn(List.of(historicTaskInstance));

        runtimeProcessService.getAllHistoryTask(historicQueryInstanceDTO, page, size);
        verify(historicTaskInstanceQueryDto, times(1)).toQuery(any());
    }

    @Test
    void getAllHistoryTaskTestWhileThrowingException() {
        HistoricTaskInstance historicTaskInstance = Mockito.mock(HistoricTaskInstance.class);
        HistoricQueryInstanceDTO historicQueryInstanceDTO = Mockito.mock(HistoricQueryInstanceDTO.class);
        HistoricTaskInstanceQuery historicTaskInstanceQuery = Mockito.mock(HistoricTaskInstanceQuery.class);
        HistoricTaskInstanceQueryDto historicTaskInstanceQueryDto = Mockito.mock(HistoricTaskInstanceQueryDto.class);
        Integer page = 1, size = 1;

        Mockito.when(historyService.createHistoricTaskInstanceQuery()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.orderByHistoricActivityInstanceStartTime()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(objectMapper.convertValue(historicQueryInstanceDTO, HistoricTaskInstanceQueryDto.class)).thenReturn(historicTaskInstanceQueryDto);
        Mockito.when(historicTaskInstanceQueryDto.toQuery(processEngine)).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.orderByHistoricActivityInstanceStartTime()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.desc()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.endOr()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.endOr().listPage(page * size, size)).thenReturn(List.of(historicTaskInstance));
        Mockito.when(wrapperService.setExtensionElementsToHistoricTask(any(), any())).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(Exception.class, () -> runtimeProcessService.getAllHistoryTask(historicQueryInstanceDTO, page, size));
    }
}
