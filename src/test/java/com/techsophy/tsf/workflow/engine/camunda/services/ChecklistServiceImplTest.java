package com.techsophy.tsf.workflow.engine.camunda.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.model.*;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.ChecklistServiceImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WebClientWrapper;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistServiceImplTest {
    @Mock
    WebClientWrapper webClientWrapper;
    @Mock
    WebClient webClient;
    @Mock
    TokenSupplier tokenSupplier;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    ChecklistServiceImpl checklistService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(checklistService, "gatewayURI", "http://apigateway.techsophy.com");
    }

    @Test
    void invokeChecklistTest() throws JsonProcessingException {
        Map<String, Object> data = Map.of("key","value");
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        InvokeChecklistInstanceResponseModel expectedOutput = checklistService.invokeChecklist("test", "test", data);
        InvokeChecklistInstanceResponseModel actualOutput = objectMapper.convertValue(apiResponse.getData(), InvokeChecklistInstanceResponseModel.class);
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void getItemInstancesByChecklistInstanceIdTest() throws JsonProcessingException {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        List<ChecklistItemInstanceResponseModel> expectedOutput = checklistService.getItemInstancesByChecklistInstanceId("test");
        List<ChecklistItemInstanceResponseModel> actualOutput = objectMapper.convertValue(apiResponse.getData(), new TypeReference<>() {});
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void getChecklistInstanceByIdTest() throws JsonProcessingException {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        ChecklistInstanceResponseModel expectedOutput = checklistService.getChecklistInstanceById("test");
        ChecklistInstanceResponseModel actualOutput = objectMapper.convertValue(apiResponse.getData(), new TypeReference<>() {});
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void getChecklistByIdTest() throws JsonProcessingException {
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        Mockito.when(webClientWrapper.webclientRequest(any(), any(), any(), any()))
                .thenReturn("{\"data\":\"test\",\"success\":true,\"message\":\"test\"}");
        Mockito.when(objectMapper.readValue(anyString(),eq(ApiResponse.class))).thenReturn(apiResponse);
        ChecklistModel expectedOutput = checklistService.getChecklistById("test");
        ChecklistModel actualOutput = objectMapper.convertValue(apiResponse.getData(), new TypeReference<>() {});
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void completeChecklistItemsByIdsTest() throws JsonProcessingException {
        Map<String, List<String>> idList = Map.of("key", List.of("val1"));
        ApiResponse<String> apiResponse = new ApiResponse<>("test",true, "message");
        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
        checklistService.completeChecklistItemsByIds(idList);
        verify(webClientWrapper,times(1)).createWebClient(any());
    }


}
