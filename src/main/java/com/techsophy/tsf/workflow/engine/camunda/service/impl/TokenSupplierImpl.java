package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.tsf.workflow.engine.camunda.config.KeycloakClientConfig;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.TokenCallable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.lang.reflect.InvocationTargetException;

/**
 * get token with new time stamp
 */
@Component
@Profile("!test")
public class TokenSupplierImpl implements TokenSupplier
{
    private final TokenCallable tokenCallable;

    public TokenSupplierImpl(KeycloakClientConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // unfortunate that we have to resort to reflection but 'TokenCallable' is quite useful so it's ok
        this.tokenCallable = (TokenCallable) MethodUtils.invokeMethod(AuthzClient.create(config), true, "createPatSupplier");
    }

    public String getToken()
    {
        // will cache token and automatically refresh when expired
        return this.tokenCallable.call();
    }
}
