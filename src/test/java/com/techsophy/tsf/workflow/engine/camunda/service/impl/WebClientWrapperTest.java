package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;

@ExtendWith(MockitoExtension.class)
class WebClientWrapperTest {
    @InjectMocks
    WebClientWrapper webClientWrapper;
    @Mock
    private WebClient webClientMock;


    @Order(1)
    @Test
    void createWebClientTest()
    {
        WebClient webClientTest=  webClientWrapper.createWebClient(TOKEN);
        Assertions.assertNotNull(webClientTest);
    }

    @Order(2)
    @Test
    void getWebClientRequestTest()
    {
        WebClient.RequestBodyUriSpec requestBodyUriMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.RequestBodySpec requestBodyMock = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);
        webClientMock = mock(WebClient.class);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(webClientMock.method(DELETE)).thenReturn(requestBodyUriMock);
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(webClientMock.put()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(LOCAL_HOST_URL)).thenReturn(requestBodyMock);
        when(requestHeadersUriSpec.uri(LOCAL_HOST_URL)).thenReturn(requestHeadersMock);
        when(requestBodyMock.bodyValue(TOKEN)).thenReturn(requestHeadersMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(String.class))
                .thenReturn(Mono.just(TEST));

        String getResponse= webClientWrapper.webclientRequest(webClientMock,LOCAL_HOST_URL, GET,null);
        assertEquals(TEST,getResponse);
        String putResponse= webClientWrapper.webclientRequest(webClientMock,LOCAL_HOST_URL,PUT,TOKEN);
        assertEquals(TEST,putResponse);
        String deleteResponse= webClientWrapper.webclientRequest(webClientMock,LOCAL_HOST_URL,DELETE,null);
        assertEquals(TEST,deleteResponse);
        String deleteBodyResponse= webClientWrapper.webclientRequest(webClientMock,LOCAL_HOST_URL,DELETE,TOKEN);
        assertEquals(TEST,deleteBodyResponse);
        String postResponse= webClientWrapper.webclientRequest(webClientMock,LOCAL_HOST_URL,POST,TOKEN);
        assertEquals(TEST,postResponse);
    }
}
