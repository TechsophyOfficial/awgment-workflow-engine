package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.model.PublishRequestModel;
import com.techsophy.tsf.workflow.engine.camunda.service.DMSWrapperService;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.GATEWAY_URI;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.GENERATE_DOCUMENT;

@Service
@RequiredArgsConstructor
public class DMSWrapperServiceImpl implements DMSWrapperService
{

    private final WebClientWrapper webClientWrapper;
    private final TokenSupplier tokenSupplier;
    private final ObjectMapper objectMapper;
    @Value(GATEWAY_URI)
    private String gatewayURI;


    @Override
    @SneakyThrows
    public Map<String, Object> generateDocument(PublishRequestModel publishRequestModel)
    {
        String url = UriComponentsBuilder.fromHttpUrl(gatewayURI + GENERATE_DOCUMENT)
                .toUriString();
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.POST, publishRequestModel);
        ApiResponse apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        Map<String, Object> data= this.objectMapper.convertValue(apiResponse.getData(), Map.class);
        return data;
    }
}
