package com.techsophy.tsf.workflow.engine.camunda.connectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.connect.httpclient.HttpRequest;
import org.camunda.connect.httpclient.HttpResponse;
import org.camunda.connect.httpclient.impl.AbstractHttpConnector;
import org.camunda.connect.httpclient.impl.HttpRequestImpl;
import org.camunda.connect.httpclient.impl.HttpResponseImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

/**
 * this class  uses http resquest and response of camunda and create
 * custom request and response body
 */
public abstract class AbstractApiConnector extends AbstractHttpConnector<HttpRequest, HttpResponse>
{
    private final TokenSupplier tokenSupplier;
    private final RuntimeContextPropertyProvider propertyProvider;

    /**
     * Parameterized constructor
     * @param connectorId
     * @param tokenSupplier
     * @param propertyProvider
     */
    public AbstractApiConnector(String connectorId, TokenSupplier tokenSupplier, RuntimeContextPropertyProvider propertyProvider)
    {
        super(connectorId);
        this.tokenSupplier = tokenSupplier;
        this.propertyProvider = propertyProvider;
    }

    /**
     * Override create response method
     * @param closeableHttpResponse
     * @return HttpResponse
     */
    @Override
    protected HttpResponse createResponse(CloseableHttpResponse closeableHttpResponse)
    {
        if(!String.valueOf(closeableHttpResponse.getStatusLine().getStatusCode()).startsWith(
                String.valueOf(HttpStatus.Series.SUCCESSFUL.value()))){
            Map<String, String> apiErrorResponse=new HashMap<>();
            try  {

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(closeableHttpResponse.getEntity().getContent()));

                        String httpBodyResponse = reader.lines().collect(Collectors.joining(""));
                        if(!StringUtils.isEmpty(httpBodyResponse)){
                        String errorMessage = httpBodyResponse;
                        ObjectMapper objectMapper = new ObjectMapper();
                        apiErrorResponse = objectMapper.readValue(errorMessage, Map.class);
                    }


            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String error=closeableHttpResponse.getStatusLine().getReasonPhrase();
            if(apiErrorResponse.containsKey(ERROR)){
                error=apiErrorResponse.get(ERROR);
            }
            throw new BpmnError(String.valueOf(closeableHttpResponse.getStatusLine().getStatusCode()),
                    error);
        }
        return new HttpResponseImpl(closeableHttpResponse);
    }

    /**
     * Override create response method
     * @return HttpRequest
     */
    @Override
    public HttpRequest createRequest()
    {
        return new HttpRequestImpl(this);
    }

    /**
     * This is method is used to execute the rest api call from
     * process engine
     * @param request
     * @return HttpResponse
     */
    @Override
    public final HttpResponse execute(HttpRequest request)
    {
        request.url(this.getGatewayURI() + request.getUrl());
        request.header(HttpHeaders.AUTHORIZATION, BEARER + " " + tokenSupplier.getToken());
        request.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if(this.propertyProvider.getProcessInstance() != null)
        {
            request.header(USERNAME, this.propertyProvider.getProcessInitiator());
        }
        return super.execute(request);
    }

    protected abstract String getGatewayURI();
}
