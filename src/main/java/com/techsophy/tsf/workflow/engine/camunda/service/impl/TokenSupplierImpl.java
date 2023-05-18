package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import com.techsophy.tsf.workflow.engine.camunda.config.KeycloakClientConfig;
import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.TokenCallable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * get token with new time stamp
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
public class TokenSupplierImpl implements TokenSupplier
{
    private  TokenCallable tokenCallable;
    private final KeycloakClientCredentials keycloakClientCredentials;

    private Map<String, TokenCallable> tenantTokenMap = new ConcurrentHashMap<>();
    KeycloakClientConfig config;

//    public TokenSupplierImpl(KeycloakClientConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
//    {
////        keycloakClientCredentials.fetchClientDetails(TenantContext.getTenantId(),)
//        // unfortunate that we have to resort to reflection but 'TokenCallable' is quite useful so it's ok
//        this.tokenCallable = (TokenCallable) MethodUtils.invokeMethod(AuthzClient.create(config), true, "createPatSupplier");
//    }

    @SneakyThrows
    public String getToken()  {
        String tenant = TenantContext.getTenantId();
        TokenCallable tenantCallable = tenantTokenMap.get(tenant);
        if(tenantCallable==null){
            ClientDetails details = keycloakClientCredentials.fetchClientDetails(tenant,false);
            config = KeycloakClientConfig.create(config,details.getClientId(),details.getSecret());
            tokenCallable = (TokenCallable) MethodUtils.invokeMethod(AuthzClient.create(config), true, "createPatSupplier");
        }
        // will cache token and automatically refresh when expired
        return this.tokenCallable.call();
    }
}
