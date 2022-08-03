package com.techsophy.tsf.workflow.engine.camunda.controller;

import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl.WrapperControllerImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WrapperServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WrapperControllerTest
{
    private final String APP_NAME = "dms";
    private final String FILE_NAME_KEY="fileName";
    private final String FILE_NAME="test.txt";
    private final String CONSTANT_URL = "http://localhost:8080/service/v1/wrapper/";
    private static final String CONTACT_DETAILS_DATA = "testdata/contact-details.json";

//    private MockMvc mockMvc;
//
//    @Mock
//    WrapperServiceImpl wrapperServiceImpl;
//
//    @InjectMocks
//    WrapperControllerImpl wrapperControllerImpl;
//
//
//    @BeforeEach
//    public void setup()
//    {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    void testHandleForPost()
//    {
//        URI uri = URI.create(CONSTANT_URL + APP_NAME);
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add(FILE_NAME_KEY,FILE_NAME);
//        ResponseEntity<Resource> response = new ResponseEntity(mock(Object.class), new LinkedMultiValueMap<>(),
//                HttpStatus.OK);
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        RequestEntity<MultiValueMap<String, Resource>> requestEntity = new RequestEntity(body, new HttpHeaders(), HttpMethod.POST, uri, null);
//        MockMultipartHttpServletRequest mockMultipartHttpServletRequest = new MockMultipartHttpServletRequest();
//        MultipartFile file = new MockMultipartFile("file", FILE_NAME, MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
//        mockMultipartHttpServletRequest.addFile(file);
//        when(wrapperServiceImpl.getMultipartRequestEntity(mockMultipartHttpServletRequest)).thenReturn(requestEntity);
//        when(wrapperServiceImpl.handleRequest(params, requestEntity, APP_NAME)).thenReturn(response);
//        ResponseEntity<Resource> result = wrapperControllerImpl.handle(params,mockMultipartHttpServletRequest,APP_NAME);
//        Assertions.assertEquals(result,response);
//    }
//
//    @Test
//    void testHandleForGet()
//    {
//        URI uri = URI.create(CONSTANT_URL + APP_NAME);
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add(FILE_NAME_KEY,FILE_NAME);
//        ResponseEntity<Resource> response = new ResponseEntity(mock(Object.class),new LinkedMultiValueMap<>(),
//                HttpStatus.OK);
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        RequestEntity<?> requestEntity = new RequestEntity(body, new HttpHeaders(), HttpMethod.GET, uri, null);
//        when(wrapperServiceImpl.handleRequest(params, requestEntity,APP_NAME)).thenReturn(response);
//        ResponseEntity<Resource> result = wrapperControllerImpl.handle(params,requestEntity,APP_NAME);
//        Assertions.assertEquals(result,response);
//    }
}