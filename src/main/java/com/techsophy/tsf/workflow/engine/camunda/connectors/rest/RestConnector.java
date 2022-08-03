package com.techsophy.tsf.workflow.engine.camunda.connectors.rest;

import com.techsophy.tsf.workflow.engine.camunda.connectors.AbstractApiConnector;
import com.techsophy.tsf.workflow.engine.camunda.connectors.RuntimeContextPropertyProvider;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.GATEWAY_URI;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.REST_CONNECTOR;

/**
 * This class is used to overide the gateway uri
 */
@RefreshScope
@Component
public class RestConnector extends AbstractApiConnector
{
    @Value(GATEWAY_URI)
    private String gatewayURI;

    /**
     * Access the Rest connector feature from camunda and add the gateway url
     * @param tokenSupplier
     * @param propertyProvider
     */
    public RestConnector(TokenSupplier tokenSupplier, RuntimeContextPropertyProvider propertyProvider)
    {
        super(REST_CONNECTOR, tokenSupplier, propertyProvider);
    }

    /**
     * override the gateway uri
     * @return String
     */
    @Override
    protected String getGatewayURI()
    {
        return this.gatewayURI;
    }
}
