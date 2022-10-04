//package com.techsophy.tsf.workflow.engine.camunda.services;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
//import com.techsophy.tsf.workflow.engine.camunda.model.PropertiesModel;
//import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
//import com.techsophy.tsf.workflow.engine.camunda.service.impl.AppUtilServiceImpl;
//import com.techsophy.tsf.workflow.engine.camunda.service.impl.WebClientWrapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URL;
//import java.util.List;
//
//import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.PROJECT_NAME_REQUEST_PARAM;
//import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.PROPERTIES_BY_PROJECT_NAME;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.mock;
//
//@ExtendWith(MockitoExtension.class)
//public class AppUtilServiceImplTest {
//    @Mock
//    WebClientWrapper webClientWrapper;
//    @Mock
//    TokenSupplier tokenSupplier;
//    @Mock
//    ObjectMapper objectMapper;
//    @Mock
//    UriComponentsBuilder builder;
//    @InjectMocks
//    AppUtilServiceImpl appUtilService;
//
//    @Test
//    void getPropertiesTest() throws JsonProcessingException {
//        PropertiesModel propertiesModel =  new PropertiesModel("abc","key","value");
//        ApiResponse<?> apiResponse = new ApiResponse<>(123,true,"abc");
//        WebClient webClient = mock(WebClient.class);
////        Mockito.when(builder.toUriString()).thenReturn("abc");
////        Mockito.when(builder.queryParam(any())).thenReturn(builder);
////        Mockito.when(UriComponentsBuilder.fromHttpUrl(anyString())).thenReturn(builder.uri(URI.create("https://www.techtarget.com")));
//        Mockito.when(tokenSupplier.getToken()).thenReturn("abc");
//        Mockito.when(webClientWrapper.createWebClient(any())).thenReturn(webClient);
//        Mockito.when(webClientWrapper.webclientRequest(any(),any(),any(),any())).thenReturn("abc");
//        Mockito.when(objectMapper.readValue(anyString(), eq(ApiResponse.class))).thenReturn(apiResponse);
//        Mockito.when(this.objectMapper.convertValue(any(ApiResponse.class),new TypeReference<List<PropertiesModel>>() {})).thenReturn(List.of(propertiesModel));
//        List<PropertiesModel> response = appUtilService.getProperties("awgmnt");
//        Assertions.assertNotNull(response);
//    }
//}
