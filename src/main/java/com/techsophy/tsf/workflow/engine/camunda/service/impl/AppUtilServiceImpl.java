package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.model.PropertiesModel;
import com.techsophy.tsf.workflow.engine.camunda.service.AppUtilService;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

@Service
@RequiredArgsConstructor
public class AppUtilServiceImpl implements AppUtilService
{
    private final WebClientWrapper webClientWrapper;
    private final TokenSupplier tokenSupplier;
    private final ObjectMapper objectMapper;

    @Value(GATEWAY_URI)
    private String gatewayURI;

    @Override
    @SneakyThrows
    public List<PropertiesModel> getProperties(String projectName)
    {
        String url = UriComponentsBuilder.fromHttpUrl(gatewayURI + PROPERTIES_BY_PROJECT_NAME)
                .queryParam(PROJECT_NAME_REQUEST_PARAM, projectName)
                .toUriString();
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.GET, null);
        ApiResponse apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        return this.objectMapper.convertValue(apiResponse.getData(), new TypeReference<List<PropertiesModel>>() {});
    }
}
