package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import com.techsophy.tsf.workflow.engine.camunda.config.KeycloakClientConfig;
import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.TokenCallable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.CREATE_PATH_SUPPLIER;

/**
 * get token with new time stamp
 */
@Component
@Profile("!test")
public class TokenSupplierImpl implements TokenSupplier
{

    private  TokenCallable tokenCallable;
    @Autowired
    private  KeycloakClientCredentials keycloakClientCredentials;

    private Map<String, TokenCallable> tenantTokenMap = new ConcurrentHashMap<>();
    @Autowired
    private  KeycloakClientConfig config;

    @SneakyThrows
    public String getToken() {
        String tenant = TenantContext.getTenantId();
        TokenCallable tenantCallable = tenantTokenMap.get(tenant);
        try
        {
        if (tenantCallable == null) {
            tokenCallable = getTokenCallable(config,tenant,false);
        }
        // will cache token and automatically refresh when expired
        return this.tokenCallable.call();
        }
        catch (Exception e)
        {
            tokenCallable = getTokenCallable(config,tenant,true);
            return this.tokenCallable.call();
        }
    }
    @SneakyThrows
    TokenCallable getTokenCallable(KeycloakClientConfig tenantConfig,String tenant , boolean value)
    {
        ClientDetails details = keycloakClientCredentials.fetchClientDetails(tenant,value);
        tenantConfig = KeycloakClientConfig.create(tenantConfig,details.getClientId(),details.getSecret(),tenant);
        tokenCallable = (TokenCallable) MethodUtils.invokeMethod(AuthzClient.create(tenantConfig), true, CREATE_PATH_SUPPLIER);
        tenantTokenMap.put(tenant,tokenCallable);
        return tokenCallable;
    }
}
