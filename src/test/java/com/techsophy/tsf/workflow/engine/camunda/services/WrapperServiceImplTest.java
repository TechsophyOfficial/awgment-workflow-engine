package com.techsophy.tsf.workflow.engine.camunda.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.dto.ActionsDTO;
import com.techsophy.tsf.workflow.engine.camunda.dto.TaskInstanceDTO;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.TokenSupplierImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WrapperServiceImpl;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
//@SpringBootTest(classes = TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class WrapperServiceImplTest
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
    BpmnModelInstance bpmnModelInstance;

    @Mock
    UserTask userTask1, userTask2;


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
        ResponseEntity<Resource> response = wrapperService.handleRequest(params, entity, APP_NAME);
        Assertions.assertNotNull(response);
    }

    @Test
    void testHandleRequestForDownload() throws URISyntaxException
    {
        LinkedHashMap<String,Object> body1 = new LinkedHashMap<>();
        byte[] bytes = new byte[2];
        MultiValueMap<String, String> matcherParams = getCommonParams();
        matcherParams.add(FORM, "/" + FILE_NAME);
        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
//        URI uri = UriComponentsBuilder.fromUri(new URI(wrapperService.getAppUrl(APP_NAME))).queryParams(matcherParams).build().toUri();
//        RequestEntity<?> entityMatcher = new RequestEntity<>(body, headers, HttpMethod.GET, uri);
        MultiValueMap<String, String> params = getCommonParams();
        params.add("form", "/" + FILENAME_WITH_GUID);
        RequestEntity<?> entity = new RequestEntity<>(body1, new HttpHeaders(), HttpMethod.GET, new URI("wrapper/dms"));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, getResponseHeaders(), HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);
        ResponseEntity<Resource> downloadResponse = wrapperService.handleRequest(params, entity, "form-runtime");
        Assertions.assertNotEquals(downloadResponse, responseEntity);
    }

//    @Test
//    void testHandleRequest() throws URISyntaxException
//    {
//        RequestDataDTO requestDataDTO = new RequestDataDTO("http://abc.com","method",new Payload("abc"),"abc");
//        LinkedHashMap<String,Object> body1 = new LinkedHashMap<>();;
////        byte[] bytes = new byte[2];
////        MultiValueMap<String, String> matcherParams = getCommonParams();
////        matcherParams.add(FORM, "/" + FILE_NAME);
//        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
////        URI uri = UriComponentsBuilder.fromUri(new URI(wrapperService.getAppUrl(APP_NAME))).queryParams(matcherParams).build().toUri();
////        RequestEntity<?> entityMatcher = new RequestEntity<>(body, headers, HttpMethod.GET, uri);
//
//        MultiValueMap<String, String> params = getCommonParams();
//        params.add("form", "/" + FILENAME_WITH_GUID);
//        RequestEntity<?> entity = new RequestEntity<>(body1, new HttpHeaders(), HttpMethod.GET, new URI("wrapper/dms"));
////        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, getResponseHeaders(), HttpStatus.OK);
////
////        when(restTemplate.exchange(any(RequestEntity.class), eq(byte[].class))).thenReturn(responseEntity);
//        when(objectMapper.convertValue(any(),eq(RequestDataDTO.class))).thenReturn(requestDataDTO);
//        when(HttpMethod.valueOf(any())).thenReturn(method);
//
//        ResponseEntity<Resource> downloadResponse = wrapperService.handleRequest(params, entity, "abc");
////        Assertions.assertEquals(downloadResponse, responseEntity);
//    }

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
        String response = wrapperService.getUserTaskExtensionByName(taskId, key);
        response = wrapperService.getUserTaskExtensionByName(taskId, key);
        Assertions.assertNotNull(response);
    }

    @Test
    void setExtensionElementsToTaskTest(){
        ActionsDTO actionsDTO = new ActionsDTO("test","test", "test","test");
        Date date = new Date();
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO("test","test", "test", date, date, date, "test","test", "test", "test","test", 5, "test","test", "test", "test","test", "test", true, "test","test", "test", "test","test", "test", "test", List.of(actionsDTO));
        Mockito.when(repositoryService.getBpmnModelInstance(any())).thenReturn(bpmnModelInstance);
        TaskInstanceDTO response = wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
        Assertions.assertNotNull(response);
    }
}
