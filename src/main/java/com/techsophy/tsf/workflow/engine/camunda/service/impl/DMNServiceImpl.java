package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.EvaluationException;
import com.techsophy.tsf.workflow.engine.camunda.model.RulesExecutionRequestModel;
import com.techsophy.tsf.workflow.engine.camunda.service.DMNService;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.Execute_DMN_RULES;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.GATEWAY_URL;
import static com.techsophy.tsf.workflow.engine.camunda.constants.ErrorMessageConstants.UNABLE_TO_EVALUATE;
import static com.techsophy.tsf.workflow.engine.camunda.utils.CommonUtils.isValidString;

@Service
@RequiredArgsConstructor
public class DMNServiceImpl implements DMNService
{
    private final WebClientWrapper webClientWrapper;
    private final GlobalMessageSource globalMessageSource;
    private final ObjectMapper objectMapper;
    private final TokenSupplier tokenSupplier;

    @Value(GATEWAY_URL)
    String gatewayApi;

    @Override
    @SneakyThrows
    public List<Map<String, Object>> executeDMN(String ruleId, Map<String, Object> data)
    {
        WebClient webClient = null;
        //String token = tokenUtils.getTokenFromContext();
        String token = tokenSupplier.getToken();
        if (isValidString(token))
        {
            webClient = webClientWrapper.createWebClient(token);
        }
        String url = gatewayApi + Execute_DMN_RULES;
        String response = webClientWrapper.webclientRequest(webClient, url, HttpMethod.POST, new RulesExecutionRequestModel(ruleId, data));
        Map<String, Object> responseData = this.objectMapper.readValue(response, Map.class);
        if(responseData.containsKey("data"))
        {
            return (List)responseData.get("data");
        }
        else
        {
            throw new EvaluationException(UNABLE_TO_EVALUATE, globalMessageSource.get(UNABLE_TO_EVALUATE));
        }
    }
}
