package com.techsophy.tsf.workflow.engine.camunda.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl.FormActionControllerImpl;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.exception.GlobalExceptionHandler;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeProcessServiceImpl;
import org.camunda.bpm.engine.task.Task;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.*;
import static org.camunda.bpm.engine.rest.impl.DeploymentRestServiceImpl.TENANT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@JsonIgnoreProperties(ignoreUnknown = true)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FormActionControllerImplTest {

    private static  final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRead = jwt().authorities(new SimpleGrantedAuthority("READ"));

    @Mock
    RuntimeProcessServiceImpl runtimeProcessService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    TaskQueryDTO taskQueryDTO;

    @Autowired
    FormSchemaResponse response;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    FormActionControllerImpl formActionController;


    @BeforeEach
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(formActionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllTasksTest() throws Exception {
        ActionsDTO actionsDTO = new ActionsDTO("label","http://abc.com","action","abc");
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO(ID,NAME,ASSIGNEEE,new Date(),new Date(),new Date(),DELEGATION_STATE,DESCRIPTION,"1",
                "owner",PARENT_TASK_ID,1,"processDefinitionId",PROCESS_INSTANCE_ID,"taskdefinitionKey",
                "caseExecutionId","caseInstanceId","caseDefinitionId",false,FORM_KEY,TENANT_ID,BUSINESS_KEY,
                "componentId","type","abc",CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE,List.of(actionsDTO));
        ObjectMapper objectMapperTest=new ObjectMapper();
        List<TaskInstanceDTO> list = new ArrayList<>();
        list.add(taskInstanceDTO);
        PaginationDTO<List<TaskInstanceDTO>> paginationDTO= new PaginationDTO<>(list,1,1,1, 1L,1L);
        Mockito.when(runtimeProcessService.getAllTasks(any(),any(),any())).thenReturn(paginationDTO);
        RequestBuilder requestBuilderTest = MockMvcRequestBuilders.get(BASE_URL + TASK_URL)
                .with(jwtRead).content(objectMapperTest.writeValueAsString(taskInstanceDTO)).contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = this.mockMvc.perform(requestBuilderTest).andExpect(status().isOk()).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    void getNextTaskTest() {
        Mockito.when(runtimeProcessService.getNextTask(anyString())).thenReturn(response);
        ApiResponse<FormSchemaResponse> expected = new ApiResponse<>(response,true,GET_FORM_SUCCESS);
        ApiResponse<FormSchemaResponse> actual = formActionController.getNextTask(BUSINESS_KEY);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void getSubTasksTest() throws Exception {
        Task task = mock(Task.class);
        Mockito.when(runtimeProcessService.getSubTasks(any())).thenReturn(List.of(task));
        ApiResponse<List<Task>> expected = new ApiResponse<>(List.of(task),true,"sub tasks fetched  successfully");
        ApiResponse<List<Task>> actual = formActionController.getSubTasks(TASK_ID);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void createProcessInstanceTest(){
        Map<String,Object> variable = new HashMap<>();
        variable.put("key","value");
        ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO("processDefinitionKey",variable,BUSINESS_KEY,FORM_KEY);
        ProcessInstanceResponseDTO processInstanceResponseDTO = new ProcessInstanceResponseDTO(PROCESS_INSTANCE_ID,BUSINESS_KEY,variable,true);
        Mockito.when(runtimeProcessService.createProcessInstance(any())).thenReturn(processInstanceResponseDTO);
        ApiResponse<ProcessInstanceResponseDTO> expected = new ApiResponse<>(processInstanceResponseDTO,true,PROCESS_CREATION);
        ApiResponse<ProcessInstanceResponseDTO> actual = formActionController.createProcessInstance(processInstanceDTO);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void getAllTasksByQueryTest(){
        ActionsDTO actionsDTO = new ActionsDTO("label","http://abc.com","action","abc");
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO(ID,NAME,ASSIGNEEE,new Date(),new Date(),new Date(),DELEGATION_STATE,DESCRIPTION,"1",
                "owner",PARENT_TASK_ID,1,"processDefinitionId",PROCESS_INSTANCE_ID,"taskdefinitionKey",
                "caseExecutionId","caseInstanceId","caseDefinitionId",false,FORM_KEY,TENANT_ID,BUSINESS_KEY,
                "componentId","type","abc",CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE,List.of(actionsDTO));
        PaginationDTO<List<TaskInstanceDTO>> expected = new PaginationDTO<>(List.of(taskInstanceDTO),1,1,1,1L,1L);
        Mockito.when(runtimeProcessService.getAllTasks(any(),any(),any())).thenReturn(expected);
        PaginationDTO<List<TaskInstanceDTO>> actual = formActionController.getAllTasksByQuery(taskQueryDTO,1,1);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void getAllHistoryTasksByQueryTest(){
        HistoricInstanceDTO historicInstanceDTO = mock(HistoricInstanceDTO.class);
        HistoricQueryInstanceDTO historicQueryInstanceDTO = mock(HistoricQueryInstanceDTO.class);
        PaginationDTO<List<HistoricInstanceDTO>> expected = new PaginationDTO<>(List.of(historicInstanceDTO),1,1,1,1L,1L);
        Mockito.when(runtimeProcessService.getAllHistoryTask(any(),any(),any())).thenReturn(expected);
        PaginationDTO<List<HistoricInstanceDTO>> actual = formActionController.getAllHistoryTasksByQuery(historicQueryInstanceDTO,1,1);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void completeAndFetchNextTaskTest(){
        Map<String,Object> variable = new HashMap<>();
        variable.put("key","value");
        TaskDTO taskDTO = new TaskDTO(BUSINESS_KEY,TASK_ID,variable);
        Mockito.when(runtimeProcessService.completeAndFetchNextTask(any())).thenReturn(response);
        ApiResponse<FormSchemaResponse> expected = new ApiResponse<>(response,true,NO_FORM_AVAILABLE);
        ApiResponse<FormSchemaResponse> actual = formActionController.completeAndFetchNextTask(taskDTO);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void completeCurrentActiveTaskTest(){
        Map<String,Object> variable = new HashMap<>();
        variable.put("key","value");
        TaskDTO taskDTO = new TaskDTO(BUSINESS_KEY,TASK_ID,variable);
        ApiResponse<Void> expected = new ApiResponse<>(null,true,COMPLETE_TASK_SUCCESS);
        ApiResponse<Void> actual = formActionController.completeCurrentActiveTask(taskDTO);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void updateProcessVariablesTest(){
        Map<String,Object> variable = new HashMap<>();
        variable.put("key","value");
        ProcessVariablesDTO processVariablesDTO = new ProcessVariablesDTO(BUSINESS_KEY,variable);
        ApiResponse<Void> expected = new ApiResponse<>(null,true,FORM_UPDATE_SUCCESS);
        ApiResponse<Void> actual = formActionController.updateProcessVariables(processVariablesDTO);
        Assertions.assertEquals(expected,actual);
    }



    @Test
    void  createOrFetchProcessInstanceTest(){
        Map<String,Object> variable = new HashMap<>();
        variable.put("key","value");
        ProcessInstanceResponseDTO processInstanceResponseDTO = new ProcessInstanceResponseDTO(PROCESS_INSTANCE_ID,BUSINESS_KEY,variable,false);
        ProcessInstanceDTO processInstanceDTO =  new ProcessInstanceDTO("processDefinitionKey",variable,BUSINESS_KEY,FORM_KEY);
        Mockito.when(runtimeProcessService.createOrFetchProcessInstance(processInstanceDTO)).thenReturn(processInstanceResponseDTO);
        ApiResponse<ProcessInstanceResponseDTO> expected = new ApiResponse<>(null,true,PROCESS_CREATION);
        ApiResponse<ProcessInstanceResponseDTO> actual = formActionController.createProcessInstance(processInstanceDTO);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void resumeProcessTest(){
        ResumeProcessRequestDto reqDto =  new ResumeProcessRequestDto();
        ApiResponse<Void> expected = new ApiResponse<>(null,true,"Process instance resumed successfully");
        ApiResponse<Void> actual = formActionController.resumeProcess(reqDto);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void completeTaskTest(){
        ApiResponse<Void> expected = new ApiResponse<>(null,true,"Task is completed successfully");
        ApiResponse<Void> actual = formActionController.completeTask(CHECKLIST_INSTANCE_ID_VAR);
        Assertions.assertEquals(expected,actual);
    }

}
