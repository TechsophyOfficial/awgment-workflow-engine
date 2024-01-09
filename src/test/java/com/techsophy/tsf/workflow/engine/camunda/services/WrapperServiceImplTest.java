package com.techsophy.tsf.workflow.engine.camunda.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeFormServiceImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.TokenSupplierImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WrapperServiceImpl;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.camunda.bpm.model.cmmn.instance.PlanItem;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.CHECKLIST_INSTANCE_ID;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.DOCUMENT_ID;
import static com.techsophy.tsf.workflow.engine.camunda.constants.WorkflowEngineConstants.FORM_KEY;
import static javax.ws.rs.HttpMethod.GET;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class WrapperServiceImplTest
{
    private final String APP_NAME = "dms";
    private final String FILE_NAME = "test-file.txt";
    private final String FILENAME_WITH_GUID = "test-file-1c199b07-0873-4249-b69c-031db55352a3.txt";

    private HttpHeaders headers;
    private MultiValueMap<String, MockMultipartFile> body;



    @InjectMocks
    private WrapperServiceImpl wrapperService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TokenSupplierImpl tokenSupplier;

    @Value("${dms.hostUrl}")
    private String dmsUrl;

    @Mock
    RequestEntity request;

    @Mock
    Task task;

    @Mock
    TaskService taskService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    RepositoryService repositoryService;

    @Mock
    CmmnModelInstance cmmnModelInstance;

    @Mock
    ExtensionElements extensionElements;

    @Mock
    BpmnModelInstance bpmnModelInstance;

    @Mock
    UserTask userTask1, userTask2;

    @Mock
    RuntimeFormServiceImpl runtimeFormService;

    @Mock
    HistoryService historyService;

    @Mock
    RuntimeService runtimeService;


    @BeforeEach
    void init()
    {
        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, BEARER + " " + "Test");
        headers.add(HttpHeaders.ACCEPT_ENCODING, "*");
        MockMultipartFile sampleFile = new MockMultipartFile("file", FILE_NAME, MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        body = new LinkedMultiValueMap<>();
        body.add("file", sampleFile);
    }

    @Test
    void testHandleRequestForUpload() throws URISyntaxException
    {
        byte[] bytes = "InputStream resource [resource loaded through InputStream]".getBytes();
        MultiValueMap<String, String> params = getCommonParams();
        params.add("name", FILENAME_WITH_GUID);
        params.add(DOCUMENT_TYPE_ID,DOCUMENT_TYPE_ID);
        params.add(DOCUMENT_PATH,DOCUMENT_PATH);
        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);

        RequestEntity<?> entity = new RequestEntity<>(body, new HttpHeaders(), HttpMethod.POST, new URI("wrapper/dms"));
        when(tokenSupplier.getToken()).thenReturn("token");
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes,getResponseHeaders(), HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);
        wrapperService.handleRequest(params, entity, APP_NAME);
        verify(restTemplate,times(1)).exchange(any(RequestEntity.class), eq(byte[].class));
    }

    @Test
    void testHandleRequestForDownload() throws URISyntaxException
    {
        LinkedHashMap<String,Object> body1 = new LinkedHashMap<>();
        byte[] bytes = new byte[2];
        MultiValueMap<String, String> matcherParams = getCommonParams();
        matcherParams.add(FORM, "/" + FILE_NAME);
        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
        MultiValueMap<String, String> params = getCommonParams();
        params.add("form", "/" + FILENAME_WITH_GUID);
        RequestEntity<?> entity = new RequestEntity<>(body1, new HttpHeaders(), HttpMethod.GET, new URI("wrapper/dms"));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, getResponseHeaders(), HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);
        ResponseEntity<Resource> downloadResponse = wrapperService.handleRequest(params, entity, "form-runtime");
        verify(restTemplate,times(1)).exchange(any(RequestEntity.class), eq(byte[].class));
    }

    @Test
    void handleRequestTest() throws URISyntaxException {
        byte[] bytes = "InputStream resource [resource loaded through InputStream]".getBytes();
        MultiValueMap<String, String> params = getCommonParams();
        params.add("name", FILENAME_WITH_GUID);
        params.add(PROCESS_INSTANCE_ID,PROCESS_INSTANCE_ID);
        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);

        RequestEntity<?> entity = new RequestEntity<>(body, new HttpHeaders(), HttpMethod.POST, new URI("wrapper/dms"));
        when(tokenSupplier.getToken()).thenReturn("token");
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes,getResponseHeaders(), HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);

        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceId(any())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        when(processInstance.getBusinessKey()).thenReturn("abc");
        wrapperService.handleRequest(params, entity, APP_NAME);
        verify(runtimeService,times(1)).createProcessInstanceQuery();
    }

    @Test
    void handleRequestGetRequestTest() throws URISyntaxException {
        byte[] bytes = "InputStream resource [resource loaded through InputStream]".getBytes();
        MultiValueMap<String, String> params = getCommonParams();
        params.add("name", FILENAME_WITH_GUID);
        params.add(FORM, FILENAME_WITH_GUID);
        params.add(PROCESS_INSTANCE_ID,PROCESS_INSTANCE_ID);
        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
        ProcessInstance processInstance = Mockito.mock(ProcessInstance.class);
        ProcessInstanceQuery processInstanceQuery = Mockito.mock(ProcessInstanceQuery.class);

        RequestEntity<?> entity = new RequestEntity<>(body, new HttpHeaders(), HttpMethod.GET, new URI("wrapper/dms"));
        when(tokenSupplier.getToken()).thenReturn("token");
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes,getResponseHeaders(), HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);

        Mockito.when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.processInstanceId(any())).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        Mockito.when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        when(processInstance.getBusinessKey()).thenReturn("abc");
        wrapperService.handleRequest(params, entity, APP_NAME);
        verify(runtimeService,times(1)).createProcessInstanceQuery();
    }

    @Test
    void handleRequestRequestTest() throws URISyntaxException {
        byte[] bytes = "InputStream resource [resource loaded through InputStream]".getBytes();
        MultiValueMap<String, String> params = getCommonParams();
        params.add("name", FILENAME_WITH_GUID);
        params.add(FORM, FILENAME_WITH_GUID);
        params.add(PROCESS_INSTANCE_ID,PROCESS_INSTANCE_ID);
        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
        RequestEntity<?> entity = new RequestEntity<>(body, new HttpHeaders(), HttpMethod.GET, new URI("wrapper/dms"));
        when(tokenSupplier.getToken()).thenReturn("token");
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes,getResponseHeaders(), HttpStatus.OK);

        Map<String,String> maps = new HashMap<>();
        maps.put("key","value");
        maps.put("abc","qwe");
        RequestDataDTO requestDataDTO = new RequestDataDTO("abc.com",GET,"payload","requestparams");
        when(objectMapper.convertValue(entity.getBody(), RequestDataDTO.class)).thenReturn(requestDataDTO);
        when(objectMapper.convertValue(any(),any(TypeReference.class))).thenReturn(maps);
        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);
        wrapperService.handleRequest(params, entity, "formtype");
        verify(restTemplate,times(1)).exchange(any(RequestEntity.class), eq(byte[].class));
    }

    @Test
    void testGetMultipartRequestEntity()
    {
        MockMultipartHttpServletRequest mockMultipartHttpServletRequest = new MockMultipartHttpServletRequest();
        MultipartFile file = new MockMultipartFile("file", FILE_NAME, MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        mockMultipartHttpServletRequest.addFile(file);
        RequestEntity<MultiValueMap<String, Resource>> requestEntity = wrapperService.getMultipartRequestEntity(mockMultipartHttpServletRequest);
        Assertions.assertEquals(mockMultipartHttpServletRequest.getMultiFileMap().size(), Objects.requireNonNull(requestEntity.getBody()).size());
    }

    private MultiValueMap<String, String> getCommonParams()
    {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("documentTypeId", "Test");
        return params;
    }

    private MultiValueMap<String, String> getResponseHeaders()
    {
        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add("headers", "InputStream resource [resource loaded through InputStream]");
        return responseHeaders;
    }

    @Test
    void getAppUrlTest(){
        String response ;
        response = wrapperService.getAppUrl("");
        Assertions.assertNotNull(response);
    }

    @Test
    void getUserTaskExtensionByNameTest(){
        String taskId = null;
        String key = "key";
        String response;
        response = wrapperService.getUserTaskExtensionByName(taskId, key);
        Assertions.assertNotNull(response);
    }

    @Test
    void setExtensionElementsToTaskTest(){
        ActionsDTO actionsDTO = new ActionsDTO("test","test", "test","test");
        Date date = new Date();
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date, "test","test", "test", "test","test", 5, "test","test", "test", "test","test", "test", true, "test","test", "test", "test","test", "test", "test", List.of(actionsDTO));
        Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
        Mockito.verify(repositoryService,Mockito.times(1)).getBpmnModelInstance(any());
    }

    @Test
    void setExtensionElementsTest(){
        userTask1.setId("abc");
        userTask2.setId("qwe");
        Collection<UserTask> userTaskDefs = new ArrayList<>();
        userTaskDefs.add(userTask1);
        userTaskDefs.add(userTask2);
        ActionsDTO actionsDTO = new ActionsDTO("test","test", "test","test");
        Date date = new Date();
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date, "test","test", "test", "test","test", 5, "test","test", "test", "test","test", "test", true, "test","test", "test", "test","test", "test", "test", List.of(actionsDTO));
        Mockito.when(task.getProcessDefinitionId()).thenReturn("abc");
        Mockito.when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskDefs);
        Mockito.when(userTask1.getId()).thenReturn("abc");
        Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        Mockito.when(task.getTaskDefinitionKey()).thenReturn("abc");
        wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }
    @Test
    void setExtensionElementsTaskTest(){
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("cde");
        Map<String,Object> component = new HashMap<>();
        component.put(ACTIONS,list);
        component.put("abc","qwe");
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        when(camundaProperty.getCamundaName()).thenReturn("propertyName");
        when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty.getCamundaId()).thenReturn("id");
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        when(camundaProperty1.getCamundaName()).thenReturn(IS_DEFAULT);
        when(camundaProperty1.getCamundaValue()).thenReturn("true");
        when(camundaProperty1.getCamundaId()).thenReturn("id1");
        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        when(camundaProperty2.getCamundaName()).thenReturn(SCREEN_TYPE_ID);
        when(camundaProperty2.getCamundaValue()).thenReturn("true");
        when(camundaProperty2.getCamundaId()).thenReturn("id1");
        extensionElements.addExtensionElement("abc","key");
        extensionElements.addExtensionElement("qwe","pair");
        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        Mockito.when(query.count()).thenReturn(1);
        userTask1.setId("abc");
        userTask2.setId("qwe");
        Collection<UserTask> userTaskDefs = new ArrayList<>();
        userTaskDefs.add(userTask1);
        userTaskDefs.add(userTask2);
        Optional<UserTask> userTaskOptional = Optional.of(userTask1);
        ActionsDTO actionsDTO = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR, TYPE,"test");
        ActionsDTO actionsDTO1 = new ActionsDTO("test",DOCUMENT_ID_VAR,IS_DEFAULT,"test");
        ActionsDTO actionsDTO2 = new ActionsDTO("test",SCREEN_TYPE_ID_VAR,IS_DEFAULT,"test");
        List<ActionsDTO> actionsDTOList = new ArrayList<>();
        actionsDTOList.add(actionsDTO);
        actionsDTOList.add(actionsDTO1);
        Date date = new Date();
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date, "test","test", "test", "test","test", 5, "test","test", "test", "test","test", "test", true, "test","test", "test", "test","test", "test", "test", List.of(actionsDTO));
        Mockito.when(task.getProcessDefinitionId()).thenReturn("abc");
        Mockito.when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskDefs);
        Mockito.when(userTask1.getId()).thenReturn("abc");
        Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        Mockito.when(task.getTaskDefinitionKey()).thenReturn("abc");
        Mockito.when(userTaskOptional.get().getExtensionElements()).thenReturn(extensionElements);
        Mockito.when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        Mockito.when(extensionElements.getElementsQuery().filterByType(CamundaProperties.class)).thenReturn(query);
        Mockito.when(query.singleResult()).thenReturn(camundaProperties);
        Mockito.when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(task.getFormKey()).thenReturn("abc");
        when(runtimeFormService.fetchFormById(any())).thenReturn(new FormSchema(NAME,ID,component,1));
        when(taskService.getVariableLocal(task.getId(), CHECKLIST_INSTANCE_ID)).thenReturn(123);
        when(objectMapper.convertValue(any(),any(TypeReference.class))).thenReturn(actionsDTOList);
        wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }

@Test
void setExtensionElementsTaskWithZeroQueryCountTest(){
    extensionElements.addExtensionElement("abc","key");
    extensionElements.addExtensionElement("qwe","pair");
    Query<CamundaProperties> query = mock(Query.class);
    Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);
    Mockito.when(query.count()).thenReturn(0);
    userTask1.setId("abc");
    userTask2.setId("qwe");
    Collection<UserTask> userTaskDefs = new ArrayList<>();
    userTaskDefs.add(userTask1);
    userTaskDefs.add(userTask2);
    Optional<UserTask> userTaskOptional = Optional.of(userTask1);
    ActionsDTO actionsDTO = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR, TYPE,"test");
    Date date = new Date();
    TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date,
            "test","test", "test", "test","test", 5,
            "test","test", "test", "test","test",
            "test", true, "test","test", "test", "test",
            "test", "test", "test", List.of(actionsDTO));
    Mockito.when(task.getProcessDefinitionId()).thenReturn("abc");
    Mockito.when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskDefs);
    Mockito.when(userTask1.getId()).thenReturn("abc");
    Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
    Mockito.when(task.getTaskDefinitionKey()).thenReturn("abc");
    Mockito.when(userTaskOptional.get().getExtensionElements()).thenReturn(extensionElements);
    Mockito.when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
    Mockito.when(extensionElements.getElementsQuery().filterByType(CamundaProperties.class)).thenReturn(query);
    wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
    verify(repositoryService,times(1)).getBpmnModelInstance(any());
}


@Test
void setExtensionElementTests(){

    CamundaProperty camundaProperty = mock(CamundaProperty.class);
    when(camundaProperty.getCamundaName()).thenReturn(TYPE);
    when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
    when(camundaProperty.getCamundaId()).thenReturn("id");

    CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
    when(camundaProperty1.getCamundaName()).thenReturn(IS_DEFAULT);
    when(camundaProperty1.getCamundaValue()).thenReturn("true");
    when(camundaProperty1.getCamundaId()).thenReturn("id1");

    extensionElements.addExtensionElement("abc","key");
    extensionElements.addExtensionElement("qwe","pair");

    Query<CamundaProperties> query = mock(Query.class);
    Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);

    CamundaProperties camundaProperties = mock(CamundaProperties.class);
    Collection<CamundaProperty> property = new ArrayList<>();
    property.add(camundaProperty);
    property.add(camundaProperty1);

    Mockito.when(query.count()).thenReturn(1);
    userTask1.setId("abc");
    userTask2.setId("qwe");
    Collection<UserTask> userTaskDefs = new ArrayList<>();
    userTaskDefs.add(userTask1);
    userTaskDefs.add(userTask2);
    Optional<UserTask> userTaskOptional = Optional.of(userTask1);
    ActionsDTO actionsDTO = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR, TYPE,"test");
    Date date = new Date();
    TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date,
            "test","test", "test", "test","test", 5,
            "test","test", "test", "test","test",
            "test", true, "test","test", "test", "test",
            "test", "test", "test", List.of(actionsDTO));
    Mockito.when(task.getProcessDefinitionId()).thenReturn("abc");
    Mockito.when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskDefs);
    Mockito.when(userTask1.getId()).thenReturn("abc");
    Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
    Mockito.when(task.getTaskDefinitionKey()).thenReturn("abc");
    Mockito.when(userTaskOptional.get().getExtensionElements()).thenReturn(extensionElements);
    Mockito.when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
    Mockito.when(extensionElements.getElementsQuery().filterByType(CamundaProperties.class)).thenReturn(query);
    Mockito.when(query.singleResult()).thenReturn(camundaProperties);
    Mockito.when(camundaProperties.getCamundaProperties()).thenReturn(property);
    when(task.getFormKey()).thenReturn("");
    wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
    verify(repositoryService,times(1)).getBpmnModelInstance(any());
}

    @Test
    void setExtensionElementTest(){
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("cde");

        Map<String,Object> component = new HashMap<>();
        component.put(ACTIONS,list);
        component.put("abc","qwe");

        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        when(camundaProperty.getCamundaName()).thenReturn(TYPE);
        when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty.getCamundaId()).thenReturn("id");

        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        when(camundaProperty1.getCamundaName()).thenReturn(IS_DEFAULT);
        when(camundaProperty1.getCamundaValue()).thenReturn("true");
        when(camundaProperty1.getCamundaId()).thenReturn("id1");

        extensionElements.addExtensionElement("abc","key");
        extensionElements.addExtensionElement("qwe","pair");

        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);

        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);

        Mockito.when(query.count()).thenReturn(1);
        userTask1.setId("abc");
        userTask2.setId("qwe");
        Collection<UserTask> userTaskDefs = new ArrayList<>();
        userTaskDefs.add(userTask1);
        userTaskDefs.add(userTask2);
        Optional<UserTask> userTaskOptional = Optional.of(userTask1);
        ActionsDTO actionsDTO = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR, TYPE,"test");
        ActionsDTO actionsDTO1 = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR,IS_DEFAULT,"test");
        List<ActionsDTO> actionsDTOList = new ArrayList<>();
        actionsDTOList.add(actionsDTO);
        actionsDTOList.add(actionsDTO1);
        Date date = new Date();
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date, "test","test", "test", "test","test", 5, "test","test", "test", "test","test", "test", true, "test","test", "test", "test","test", "test", "test", List.of(actionsDTO));
        Mockito.when(task.getProcessDefinitionId()).thenReturn("abc");
        Mockito.when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskDefs);
        Mockito.when(userTask1.getId()).thenReturn("abc");
        Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        Mockito.when(task.getTaskDefinitionKey()).thenReturn("abc");
        Mockito.when(userTaskOptional.get().getExtensionElements()).thenReturn(extensionElements);
        Mockito.when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        Mockito.when(extensionElements.getElementsQuery().filterByType(CamundaProperties.class)).thenReturn(query);
        Mockito.when(query.singleResult()).thenReturn(camundaProperties);
        Mockito.when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(task.getFormKey()).thenReturn("abc");
        when(runtimeFormService.fetchFormById(any())).thenReturn(new FormSchema(NAME,ID,component,1));
        when(taskService.getVariableLocal(task.getId(), CHECKLIST_INSTANCE_ID)).thenReturn(123);
        when(objectMapper.convertValue(any(),any(TypeReference.class))).thenReturn(actionsDTOList);
        wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }


    @Test
    void extensionElementSetTest(){
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("cde");

        Map<String,Object> component = new HashMap<>();
        component.put(ACTIONS,list);
        component.put("abc","qwe");

        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        when(camundaProperty.getCamundaName()).thenReturn(TYPE);
        when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty.getCamundaId()).thenReturn("id");

        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        when(camundaProperty1.getCamundaName()).thenReturn(IS_DEFAULT);
        when(camundaProperty1.getCamundaValue()).thenReturn("true");
        when(camundaProperty1.getCamundaId()).thenReturn("id1");

        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        when(camundaProperty2.getCamundaName()).thenReturn(FORM_NAME);
        when(camundaProperty2.getCamundaValue()).thenReturn("abc");
        when(camundaProperty2.getCamundaId()).thenReturn("id1");

        CamundaProperty camundaProperty3 = mock(CamundaProperty.class);
        when(camundaProperty3.getCamundaName()).thenReturn(QUESTION);
        when(camundaProperty3.getCamundaValue()).thenReturn("ques");
        when(camundaProperty3.getCamundaId()).thenReturn("id1");

        extensionElements.addExtensionElement("abc","key");
        extensionElements.addExtensionElement("qwe","pair");

        Query<CamundaProperties> query = mock(Query.class);
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);

        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        property.add(camundaProperty3);

        Mockito.when(query.count()).thenReturn(1);
        userTask1.setId("abc");
        userTask2.setId("qwe");
        Collection<UserTask> userTaskDefs = new ArrayList<>();
        userTaskDefs.add(userTask1);
        userTaskDefs.add(userTask2);
        Optional<UserTask> userTaskOptional = Optional.of(userTask1);
        ActionsDTO actionsDTO = new ActionsDTO("test",DOCUMENT_ID_VAR, TYPE,"test");
        ActionsDTO actionsDTO1 = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR,IS_DEFAULT,"test");
        List<ActionsDTO> actionsDTOList = new ArrayList<>();
        actionsDTOList.add(actionsDTO);
        actionsDTOList.add(actionsDTO1);
        Date date = new Date();
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date, "test","test", "test", "test","test", 5, "test","test", "test", "test","test", "test", true, "test","test", "test", "test","test", "test", "test", List.of(actionsDTO));
        Mockito.when(task.getProcessDefinitionId()).thenReturn("abc");
        Mockito.when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskDefs);
        Mockito.when(userTask1.getId()).thenReturn("abc");
        Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        Mockito.when(task.getTaskDefinitionKey()).thenReturn("abc");
        Mockito.when(userTaskOptional.get().getExtensionElements()).thenReturn(extensionElements);
        Mockito.when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        Mockito.when(extensionElements.getElementsQuery().filterByType(CamundaProperties.class)).thenReturn(query);
        Mockito.when(query.singleResult()).thenReturn(camundaProperties);
        Mockito.when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(task.getFormKey()).thenReturn("abc");
        when(runtimeFormService.fetchFormById(any())).thenReturn(new FormSchema(NAME,ID,component,1));
        when(taskService.getVariableLocal(task.getId(), DOCUMENT_ID)).thenReturn(123);
        when(objectMapper.convertValue(any(),any(TypeReference.class))).thenReturn(actionsDTOList);
        wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }

    @Test
    void getUserTaskExtensionByNameWithProcessDefinitionIdTest(){
        TaskQuery taskQuery = mock(TaskQuery.class);
        CmmnModelInstance cmmnModelInstance = mock(CmmnModelInstance.class);
        PlanItem planItem = mock(PlanItem.class);
        PlanItem planItem1 = mock(PlanItem.class);
        Collection<PlanItem> planItemCollection = new ArrayList<>();
        planItemCollection.add(planItem);
        planItemCollection.add(planItem1);
        org.camunda.bpm.model.cmmn.instance.ExtensionElements extensionElements = mock(org.camunda.bpm.model.cmmn.instance.ExtensionElements.class);
        extensionElements.addExtensionElement("abc","abc");
        org.camunda.bpm.model.cmmn.Query<ModelElementInstance> modelElementInstanceQuery = mock(org.camunda.bpm.model.cmmn.Query.class);
        org.camunda.bpm.model.cmmn.Query<CamundaProperties> camundaPropertiesQuery = mock(org.camunda.bpm.model.cmmn.Query.class);
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);

        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(any())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(task.getProcessDefinitionId()).thenReturn(null);
        when(repositoryService.getCmmnModelInstance(task.getCaseDefinitionId())).thenReturn(cmmnModelInstance);
        when(cmmnModelInstance.getModelElementsByType(PlanItem.class)).thenReturn(planItemCollection);
        when(task.getTaskDefinitionKey()).thenReturn("abc");
        when(planItem.getId()).thenReturn("abc");
        when(planItem.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        when(modelElementInstanceQuery.filterByType(CamundaProperties.class)).thenReturn(camundaPropertiesQuery);
        when(camundaPropertiesQuery.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("key");
        when(camundaProperty.getCamundaValue()).thenReturn("value");
        wrapperService.getUserTaskExtensionByName(TASK_ID,"key");
        verify(repositoryService,times(1)).getCmmnModelInstance(any());
    }

    @Test
    void getUserTaskExtensionByNameWithNullExtentionElementTest(){
        TaskQuery taskQuery = mock(TaskQuery.class);
        CmmnModelInstance cmmnModelInstance = mock(CmmnModelInstance.class);
        PlanItem planItem = mock(PlanItem.class);
        PlanItem planItem1 = mock(PlanItem.class);
        Collection<PlanItem> planItemCollection = new ArrayList<>();
        planItemCollection.add(planItem);
        planItemCollection.add(planItem1);
        org.camunda.bpm.model.cmmn.instance.ExtensionElements extensionElements = mock(org.camunda.bpm.model.cmmn.instance.ExtensionElements.class);
        extensionElements.addExtensionElement("abc","abc");
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);

        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(any())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(task.getProcessDefinitionId()).thenReturn(null);
        when(repositoryService.getCmmnModelInstance(task.getCaseDefinitionId())).thenReturn(cmmnModelInstance);
        when(cmmnModelInstance.getModelElementsByType(PlanItem.class)).thenReturn(planItemCollection);
        when(task.getTaskDefinitionKey()).thenReturn("abc");
        when(planItem.getId()).thenReturn("abc");
        when(planItem.getExtensionElements()).thenReturn(null);
        wrapperService.getUserTaskExtensionByName(TASK_ID,"key");
        verify(repositoryService,times(1)).getCmmnModelInstance(any());
    }

    @Test
    void getUserTaskExtensionByNameWithNullProcessDefinitionIdTest(){
        TaskQuery taskQuery = mock(TaskQuery.class);
        BpmnModelInstance BpmnModelInstance = mock(BpmnModelInstance.class);
        PlanItem planItem = mock(PlanItem.class);
        PlanItem planItem1 = mock(PlanItem.class);
        Collection<PlanItem> planItemCollection = new ArrayList<>();
        planItemCollection.add(planItem);
        planItemCollection.add(planItem1);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        extensionElements.addExtensionElement("abc","abc");
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);
        Query<CamundaProperties> camundaPropertiesQuery = mock(Query.class);
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);
        Collection<UserTask> userTaskCollection = new ArrayList<>();
        userTaskCollection.add(userTask1);
        userTaskCollection.add(userTask2);

        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(any())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(task.getProcessDefinitionId()).thenReturn("process definition id");
        when(repositoryService.getBpmnModelInstance(task.getProcessDefinitionId())).thenReturn(bpmnModelInstance);
        when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskCollection);
        when(task.getTaskDefinitionKey()).thenReturn("abc");
        when(userTask1.getId()).thenReturn("abc");
        when(userTask1.getExtensionElements()).thenReturn(extensionElements);
        when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        when(modelElementInstanceQuery.filterByType(CamundaProperties.class)).thenReturn(camundaPropertiesQuery);
        when(camundaPropertiesQuery.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(camundaProperty.getCamundaName()).thenReturn("key");
        when(camundaProperty.getCamundaValue()).thenReturn("value");
        wrapperService.getUserTaskExtensionByName(TASK_ID,"key");
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }

    @Test
    void getUserTaskExtensionByNameWithNullExtenstionElementTest(){
        TaskQuery taskQuery = mock(TaskQuery.class);
        BpmnModelInstance BpmnModelInstance = mock(BpmnModelInstance.class);
        PlanItem planItem = mock(PlanItem.class);
        PlanItem planItem1 = mock(PlanItem.class);
        Collection<PlanItem> planItemCollection = new ArrayList<>();
        planItemCollection.add(planItem);
        planItemCollection.add(planItem1);
        ExtensionElements extensionElements = mock(ExtensionElements.class);
        extensionElements.addExtensionElement("abc","abc");
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);
        Collection<UserTask> userTaskCollection = new ArrayList<>();
        userTaskCollection.add(userTask1);
        userTaskCollection.add(userTask2);

        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(any())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(task.getProcessDefinitionId()).thenReturn("process definition id");
        when(repositoryService.getBpmnModelInstance(task.getProcessDefinitionId())).thenReturn(bpmnModelInstance);
        when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskCollection);
        when(task.getTaskDefinitionKey()).thenReturn("abc");
        when(userTask1.getId()).thenReturn("abc");
        when(userTask1.getExtensionElements()).thenReturn(null);
        wrapperService.getUserTaskExtensionByName(TASK_ID,"key");
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }

    @Test
    void setExtensionElementsToHistoricTaskTest(){
        org.camunda.bpm.model.bpmn.instance.Task task1 = mock(org.camunda.bpm.model.bpmn.instance.Task.class);
        HistoricInstanceDTO historicInstanceDTO = mock(HistoricInstanceDTO.class);
        HistoricTaskInstance historicTaskInstance = mock(HistoricTaskInstance.class);
        Collection<UserTask> userTaskCollection = new ArrayList<>();
        userTaskCollection.add(userTask1);
        userTaskCollection.add(userTask2);
        Optional<UserTask> userTaskOptional = Optional.of(userTask1);
        Query<ModelElementInstance> modelElementInstanceQuery = mock(Query.class);
        Query<CamundaProperties> query = mock(Query.class);
        CamundaProperties camundaProperties = mock(CamundaProperties.class);
        CamundaProperty camundaProperty = mock(CamundaProperty.class);
        CamundaProperty camundaProperty1 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty2 = mock(CamundaProperty.class);
        CamundaProperty camundaProperty3 = mock(CamundaProperty.class);
        Collection<CamundaProperty> property = new ArrayList<>();
        property.add(camundaProperty);
        property.add(camundaProperty1);
        property.add(camundaProperty2);
        property.add(camundaProperty3);
        HistoricVariableInstance historicVariableInstance = mock(HistoricVariableInstance.class);
        HistoricVariableInstanceQuery historicVariableInstanceQuery = mock(HistoricVariableInstanceQuery.class);
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("cde");
        Map<String,Object> component = new HashMap<>();
        component.put(ACTIONS,list);
        component.put(FORM_NAME,"qwe");
        component.put(QUESTION,"qwe");
        FormSchema formSchema =new FormSchema(NAME,ID,component,1);
        ActionsDTO actionsDTO = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR, TYPE,"test");
        ActionsDTO actionsDTO1 = new ActionsDTO("test",CHECKLIST_INSTANCE_ID_VAR,IS_DEFAULT,"test");
        List<ActionsDTO> actionsDTOList = new ArrayList<>();
        actionsDTOList.add(actionsDTO);
        actionsDTOList.add(actionsDTO1);

        when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        when(bpmnModelInstance.getModelElementsByType(UserTask.class)).thenReturn(userTaskCollection);
        when(historicTaskInstance.getTaskDefinitionKey()).thenReturn("abc");
        when(userTask1.getId()).thenReturn("abc");
        when(userTaskOptional.get().getExtensionElements()).thenReturn(extensionElements);
        when(query.count()).thenReturn(1);
        when(extensionElements.getElementsQuery()).thenReturn(modelElementInstanceQuery);
        when(extensionElements.getElementsQuery().filterByType(CamundaProperties.class)).thenReturn(query);
        when(query.singleResult()).thenReturn(camundaProperties);
        when(camundaProperties.getCamundaProperties()).thenReturn(property);
        when(historicTaskInstance.getTaskDefinitionKey()).thenReturn("abc");
        when(bpmnModelInstance.getModelElementById(any())).thenReturn(task1);
        when(task1.getAttributeValueNs(any(),any())).thenReturn(FORM_KEY);
        when(historicTaskInstance.getId()).thenReturn(TASK_ID);
        when(historyService.createHistoricVariableInstanceQuery()).thenReturn(historicVariableInstanceQuery);
        when(historicVariableInstanceQuery.taskIdIn(any())).thenReturn(historicVariableInstanceQuery);
        when(historicVariableInstanceQuery.variableName(anyString())).thenReturn(historicVariableInstanceQuery);
        when(historicVariableInstanceQuery.singleResult()).thenReturn(historicVariableInstance);
        when(historicVariableInstance.getValue()).thenReturn(1234);
        when(camundaProperty.getCamundaName()).thenReturn(TYPE);
        when(camundaProperty.getCamundaValue()).thenReturn(COMPONENT);
        when(camundaProperty.getCamundaId()).thenReturn("id");
        when(camundaProperty1.getCamundaName()).thenReturn(IS_DEFAULT);
        when(camundaProperty1.getCamundaValue()).thenReturn("true");
        when(camundaProperty1.getCamundaId()).thenReturn("id1");
        when(camundaProperty2.getCamundaName()).thenReturn(FORM_NAME);
        when(camundaProperty2.getCamundaValue()).thenReturn(FORM);
        when(camundaProperty2.getCamundaId()).thenReturn("id1");
        when(camundaProperty3.getCamundaName()).thenReturn(QUESTION);
        when(camundaProperty3.getCamundaValue()).thenReturn(QUESTION);
        when(camundaProperty3.getCamundaId()).thenReturn("id1");
        when(runtimeFormService.fetchFormById(any())).thenReturn(formSchema);
        when(objectMapper.convertValue(any(),any(TypeReference.class))).thenReturn(actionsDTOList);
        wrapperService.setExtensionElementsToHistoricTask(historicInstanceDTO,historicTaskInstance);
        verify(repositoryService,times(1)).getBpmnModelInstance(any());
    }
}
