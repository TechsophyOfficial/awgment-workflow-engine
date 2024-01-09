package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class RuntimeFormServiceImplTest {
    @Mock
    TokenSupplier tokenSupplier;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    RestTemplate restTemplate;
    @Mock
    WrapperServiceImpl wrapperService;
    @Mock
    WebClient webClient;
    @Mock
    WebClientWrapper webClientWrapper;
    @InjectMocks
    RuntimeFormServiceImpl runtimeFormService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(runtimeFormService, "gatewayURI", "http://apigateway.techsophy.com");
    }

    @Test
    void fetchFormByIdTest() {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        ResponseEntity<Map> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.any(), ArgumentMatchers.<Class<Map>>any()))
                .thenReturn(response);
        Mockito.when(wrapperService.getUserTaskExtensionByName(anyString(), anyString())).thenReturn("taskExtension");
        Mockito.when(objectMapper.convertValue(response.getBody(), ApiResponse.class)).thenReturn(apiResponse);
        FormSchema expectedOutput = runtimeFormService.fetchFormById("formKey", "task");
        FormSchema actualOutput = objectMapper.convertValue(apiResponse.getData(), FormSchema.class);
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void fetchFormByIdTestWhileTaskIsNull() {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        ResponseEntity<Map> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.any(), ArgumentMatchers.<Class<Map>>any()))
                .thenReturn(response);
        Mockito.when(objectMapper.convertValue(response.getBody(), ApiResponse.class)).thenReturn(apiResponse);
        FormSchema expectedOutput = runtimeFormService.fetchFormById("formKey", null);
        FormSchema actualOutput = objectMapper.convertValue(apiResponse.getData(), FormSchema.class);
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void fetchFormByIdTestSingleArg() throws JsonProcessingException {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        FormSchema actualOutput = runtimeFormService.fetchFormById("123");
        FormSchema expectedOutput = objectMapper.convertValue(apiResponse.getData(), FormSchema.class);
        Assertions.assertSame(expectedOutput, actualOutput);
    }
}
