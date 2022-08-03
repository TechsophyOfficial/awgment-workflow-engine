package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.model.*;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.GATEWAY_URI;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService
{
    private final WebClientWrapper webClientWrapper;
    private final TokenSupplier tokenSupplier;
    private final ObjectMapper objectMapper;

    @Value(GATEWAY_URI)
    private String gatewayURI;

    @Override
    @SneakyThrows
    public InvokeChecklistInstanceResponseModel invokeChecklist(String checklistId, String businessKey, Map<String, Object> data)
    {
        String url = gatewayURI + INVOKE_CHECKLIST;
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.POST, Map.of(CHECKLIST_ID, checklistId,
                BUSINESS_KEY, businessKey,
                DATA, data));
        ApiResponse apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        InvokeChecklistInstanceResponseModel checklistInstanceResponseModel = this.objectMapper.convertValue(apiResponse.getData(), InvokeChecklistInstanceResponseModel.class);
        return checklistInstanceResponseModel;
    }

    @Override
    @SneakyThrows
    public List<ChecklistItemInstanceResponseModel> getItemInstancesByChecklistInstanceId(String checklistInstanceId)
    {
        String url = UriComponentsBuilder.fromHttpUrl(gatewayURI + GET_ITEM_INSTANCES)
                .queryParam(CHECKLIST_INSTANCE_ID_PARAM, checklistInstanceId)
                .toUriString();
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.GET, null);
        ApiResponse apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        List<ChecklistItemInstanceResponseModel> itemInstances = this.objectMapper.convertValue(apiResponse.getData(), new TypeReference<>() {});
        return itemInstances;
    }

    @Override
    @SneakyThrows
    public ChecklistInstanceResponseModel getChecklistInstanceById(String id)
    {
        String url = UriComponentsBuilder.fromHttpUrl(gatewayURI + String.format(GET_CHECKLIST_INSTANCE_BY_ID, id))
                .toUriString();
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.GET, null);
        ApiResponse apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        return this.objectMapper.convertValue(apiResponse.getData(), new TypeReference<>() {});
    }

    @Override
    @SneakyThrows
    public ChecklistModel getChecklistById(String checklistId)
    {
        String url = UriComponentsBuilder.fromHttpUrl(gatewayURI + String.format(GET_CHECKLIST_BY_ID, checklistId))
                .toUriString();
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.GET, null);
        ApiResponse apiResponse = this.objectMapper.readValue(response, ApiResponse.class);
        return this.objectMapper.convertValue(apiResponse.getData(), new TypeReference<>() {});
    }

    @Override
    public void completeChecklistItemsByIds(Map<String, List<String>> idList)
    {
        String url = UriComponentsBuilder.fromHttpUrl(gatewayURI + COMPLETE_CHECKLIST_ITEMS).toUriString();
        WebClient webClient = webClientWrapper.createWebClient(tokenSupplier.getToken());
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.PUT, idList);
    }
}
