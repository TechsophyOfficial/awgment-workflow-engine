package com.techsophy.tsf.workflow.engine.camunda.services;

import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class TestTokenSupplier implements TokenSupplier
{
    @Override
    public String getToken()
    {
        return "test-token";
    }
}
