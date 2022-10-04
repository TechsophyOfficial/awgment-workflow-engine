package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.service.DMNService;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DMNServiceImplTest {

    @Mock
    TokenSupplier tokenSupplier;
    @Mock
    WebClientWrapper webClientWrapper;
    @Mock
    WebClient webClient;
    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    DMNServiceImpl dmnServiceImpl;

    @Test
    void executeDMNTestThrowsNull() throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        data.put("key","value");
        Mockito.when(tokenSupplier.getToken()).thenReturn("test");
        Mockito.when(webClientWrapper.createWebClient(anyString())).thenReturn(webClient);
        Mockito.when(objectMapper.readValue((String) null, Map.class)).thenReturn(data);
        Assertions.assertThrows(NullPointerException.class,()->dmnServiceImpl.executeDMN("12",data));
    }

    @Test
    void executeDMNTest() throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        data.put("data",List.of("value"));
        Mockito.when(tokenSupplier.getToken()).thenReturn("test");
        Mockito.when(webClientWrapper.createWebClient(anyString())).thenReturn(webClient);
        Mockito.when(objectMapper.readValue((String) null, Map.class)).thenReturn(data);
        List<Map<String, Object>> response = dmnServiceImpl.executeDMN("12",data);
        Assertions.assertNotNull(response);
    }
}