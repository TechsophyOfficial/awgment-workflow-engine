package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.model.PublishRequestModel;
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

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DMSWrapperServiceImplTest {
    @Mock
    WebClientWrapper webClientWrapper;
    @Mock
    TokenSupplier tokenSupplier;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    WebClient webClient;
    @InjectMocks
    DMSWrapperServiceImpl dmsWrapperService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dmsWrapperService, "gatewayURI", "http://apigateway.techsophy.com");
    }

    @Test
    void generateDocumentTest() throws JsonProcessingException {
        PublishRequestModel publishRequestModel = new PublishRequestModel();
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        Map<String, Object> expectedOutput = dmsWrapperService.generateDocument(publishRequestModel);
        Map<String, Object> actualOutput = objectMapper.convertValue(apiResponse.getData(), Map.class);
        Assertions.assertSame(expectedOutput, actualOutput);
    }
}
