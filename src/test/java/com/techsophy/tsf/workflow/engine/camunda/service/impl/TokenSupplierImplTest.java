package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import com.techsophy.tsf.workflow.engine.camunda.config.KeycloakClientConfig;
import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthenticator;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.TokenCallable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
 class TokenSupplierImplTest {
    @Mock
    TokenCallable tokenCallable;
    @Mock
    KeycloakClientCredentials keycloakClientCredentials;


    @Mock
    KeycloakClientConfig keycloakClientConfig;
    @Mock
    ClientAuthenticator clientAuthenticator;

    @Mock
    AuthzClient authzClient;

    @Mock
    Configuration configuration;

    @InjectMocks
    TokenSupplierImpl tokenSupplier;

    @Test
    void getToken()
    {
        try(MockedStatic<TenantContext> tokenContext = Mockito.mockStatic(TenantContext.class) ;
            MockedStatic<KeycloakClientConfig> keycloakClientConfig = Mockito.mockStatic(KeycloakClientConfig.class);
            MockedStatic<MethodUtils> methodUtils = Mockito.mockStatic(MethodUtils.class);
            MockedStatic<AuthzClient> mockedStatic = Mockito.mockStatic(AuthzClient.class)){
            AuthzClient mockedAuthzClient = Mockito.mock(AuthzClient.class);
            KeycloakClientConfig clientConfig = new KeycloakClientConfig();
            mockedStatic.when(() -> AuthzClient.create((Configuration) any()))
                    .thenReturn(mockedAuthzClient);
            methodUtils.when(() -> MethodUtils.invokeMethod(mockedAuthzClient, true, "createPatSupplier")).thenReturn(tokenCallable);
            keycloakClientConfig.when(() -> KeycloakClientConfig.create(any(),any(),anyString(), anyString())).thenReturn(new KeycloakClientConfig());
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setClientId("1212");
            clientDetails.setSecret("121");
            tokenContext.when(TenantContext::getTenantId).thenReturn("techsophy-platform");
            Mockito.when(keycloakClientCredentials.fetchClientDetails(anyString(), anyBoolean())).thenReturn(clientDetails);
            tokenSupplier.getToken();
            tokenSupplier.getToken();
            Mockito.verify(keycloakClientCredentials,Mockito.times(1)).fetchClientDetails("techsophy-platform",false);
        }
    }
}
