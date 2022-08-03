package com.techsophy.tsf.workflow.engine.camunda.connectors;

import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.Getter;
import org.camunda.connect.impl.DebugRequestInterceptor;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
class TestConnector extends AbstractApiConnector
{
    @Getter
    private final DebugRequestInterceptor interceptor;

    protected TestConnector(TokenSupplier tokenSupplier)
    {
        super("test-connector", tokenSupplier,new TestRuntimeContextPropertyProvider() );
        this.addRequestInterceptor(this.interceptor = new DebugRequestInterceptor(false));
    }

//    @Override
//    protected void createResponse(HttpRequest request)
//    {
//        request.payload("gsft");
//
//        request.header("gsft", "gsft");
//        request.header("hello", "world");
//
//        request.setRequestParameter("apiId", "api1");
//        request.setRequestParameter("operationId", "operation1");
//    }


    @Override protected String getGatewayURI() {
        return URI.create("http://localhost:8888/test-service").toString();
    }
}
