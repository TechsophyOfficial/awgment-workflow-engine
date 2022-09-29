package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.model.PropertiesModel;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppUtilServiceImplTest {

    @Mock
    WebClientWrapper webClientWrapper;
    @Mock
    TokenSupplier tokenSupplier;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    WebClient webClient;
    @InjectMocks
    AppUtilServiceImpl appUtilService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appUtilService, "gatewayURI", "http://apigateway.techsophy.com");
    }

    @Test
    void getPropertiesTest() throws JsonProcessingException {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        List<PropertiesModel> expectedOutput = appUtilService.getProperties("test");
        List<PropertiesModel> actualOutput = objectMapper.convertValue(apiResponse.getData(), new TypeReference<List<PropertiesModel>>() {});
        Assertions.assertSame(expectedOutput, actualOutput);
    }
}