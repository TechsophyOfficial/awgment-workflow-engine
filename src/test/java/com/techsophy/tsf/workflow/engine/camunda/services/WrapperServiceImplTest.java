//package com.techsophy.tsf.workflow.engine.camunda.services;
//
//import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
//import com.techsophy.tsf.workflow.engine.camunda.service.impl.TokenSupplierImpl;
//import com.techsophy.tsf.workflow.engine.camunda.service.impl.WrapperServiceImpl;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.Resource;
//import org.springframework.http.*;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.mock.web.MockMultipartHttpServletRequest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Objects;
//
//import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ActiveProfiles("test")
//@SpringBootTest(classes = TestSecurityConfig.class)
//public class WrapperServiceImplTest
//{
//    private final String APP_NAME = "dms";
//    private final String FILE_NAME = "test-file.txt";
//    private final String FILENAME_WITH_GUID = "test-file-1c199b07-0873-4249-b69c-031db55352a3.txt";
//
//    private HttpHeaders headers;
//    private MultiValueMap<String, MockMultipartFile> body;
//
//    @InjectMocks
//    private WrapperServiceImpl wrapperService;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @Mock
//    private TokenSupplierImpl tokenSupplier;
//
//    @Value("${dms.hostUrl}")
//    private String dmsUrl;
//
//    @BeforeEach
//    void init()
//    {
//        when(tokenSupplier.getToken()).thenReturn("Test");
//
//        headers = new HttpHeaders();
//        headers.add(HttpHeaders.AUTHORIZATION, BEARER + " " + "Test");
//        headers.add(HttpHeaders.ACCEPT_ENCODING, "*");
//        MockMultipartFile sampleFile = new MockMultipartFile("file", FILE_NAME, MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//        body = new LinkedMultiValueMap<>();
//        body.add("file", sampleFile);
//    }
//
//    @Test
//    void testHandleRequestForUpload() throws URISyntaxException
//    {
//        MultiValueMap<String, String> params = getCommonParams();
//        params.add("name", FILENAME_WITH_GUID);
//        params.add(DOCUMENT_TYPE_ID,DOCUMENT_TYPE_ID);
//        params.add(DOCUMENT_PATH,DOCUMENT_PATH);
//        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
//        URI uri = UriComponentsBuilder.fromUri(new URI(wrapperService.getAppUrl(APP_NAME))).queryParams(params).build().toUri();
//
//        RequestEntity<?> entityMatcher = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
//        RequestEntity<?> entity = new RequestEntity<>(body, new HttpHeaders(), HttpMethod.POST, new URI("wrapper/dms"));
//        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(mock(Resource.class), getResponseHeaders(), HttpStatus.OK);
//
//        when(restTemplate.exchange(eq(entityMatcher), eq(Resource.class))).thenReturn(responseEntity);
//
//        ResponseEntity<Resource> response = wrapperService.handleRequest(params, entity, APP_NAME);
//        Assertions.assertEquals(response, responseEntity);
//    }
//
//    @Test
//    void testHandleRequestForDownload() throws URISyntaxException
//    {
//        MultiValueMap<String, String> matcherParams = getCommonParams();
//        matcherParams.add(FORM, "/" + FILE_NAME);
//        ReflectionTestUtils.setField(wrapperService, "gatewayUrl", dmsUrl);
//        URI uri = UriComponentsBuilder.fromUri(new URI(wrapperService.getAppUrl(APP_NAME))).queryParams(matcherParams).build().toUri();
//        RequestEntity<?> entityMatcher = new RequestEntity<>(body, headers, HttpMethod.GET, uri);
//
//        MultiValueMap<String, String> params = getCommonParams();
//        params.add("form", "/" + FILENAME_WITH_GUID);
//        RequestEntity<?> entity = new RequestEntity<>(body, new HttpHeaders(), HttpMethod.GET, new URI("wrapper/dms"));
//        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(mock(Resource.class), getResponseHeaders(), HttpStatus.OK);
//
//        when(restTemplate.exchange(eq(entityMatcher), eq(Resource.class))).thenReturn(responseEntity);
//
//        ResponseEntity<Resource> downloadResponse = wrapperService.handleRequest(params, entity, APP_NAME);
//        Assertions.assertEquals(downloadResponse, responseEntity);
//    }
//
//    @Test
//    void testGetMultipartRequestEntity()
//    {
//        MockMultipartHttpServletRequest mockMultipartHttpServletRequest = new MockMultipartHttpServletRequest();
//        MultipartFile file = new MockMultipartFile("file", FILE_NAME, MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//
//        mockMultipartHttpServletRequest.addFile(file);
//
//        RequestEntity<MultiValueMap<String, Resource>> requestEntity = wrapperService.getMultipartRequestEntity(mockMultipartHttpServletRequest);
//        Assertions.assertEquals(mockMultipartHttpServletRequest.getMultiFileMap().size(), Objects.requireNonNull(requestEntity.getBody()).size());
//    }
//
//    private MultiValueMap<String, String> getCommonParams()
//    {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("documentTypeId", "Test");
//        return params;
//    }
//
//    private MultiValueMap<String, String> getResponseHeaders()
//    {
//        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
//        responseHeaders.add("Set-Cookie", "test-cookie");
//        return responseHeaders;
//    }
//}
