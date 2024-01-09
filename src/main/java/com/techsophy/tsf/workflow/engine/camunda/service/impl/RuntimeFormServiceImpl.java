package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeFormService;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

@Slf4j
@RefreshScope
@Service
@RequiredArgsConstructor
public class RuntimeFormServiceImpl implements RuntimeFormService
{
    private final TokenSupplier tokenSupplier;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final WrapperServiceImpl wrapperService;
    private final WebClientWrapper webClientWrapper;
    @Value("${gateway.uri}")
    private String gatewayURI;

    @Override
    @Transactional
    public FormSchema fetchFormById(String formKey,String task)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        String url;
        String formVersion=LATEST;
        if(task!=null) {
            formVersion = wrapperService.getUserTaskExtensionByName(task, FORM_VERSION);
        }
        log.info("form version {}",formVersion);
        if(formVersion.equals(LATEST)){
            url=gatewayURI + FORM_RUNTIME_ENDPOINT_URL + "/" + formKey;
        }else{
            url=gatewayURI+FORM_MODELER_ENDPOINT_URL+formKey+"/"+formVersion;
        }
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer "+tokenSupplier.getToken());
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<?> responseEntity =  restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
        ApiResponse<?> apiResponse = this.objectMapper.convertValue(responseEntity.getBody(), ApiResponse.class);
        return this.objectMapper.convertValue(apiResponse.getData(), FormSchema.class);
    }

    @SneakyThrows
    public FormSchema fetchFormById(String formKey)
    {
        String url=gatewayURI + FORM_RUNTIME_ENDPOINT_URL + "/" + formKey;
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.GET, String.class);
        ApiResponse<?> apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        return this.objectMapper.convertValue(apiResponse.getData(), FormSchema.class);
    }
}
