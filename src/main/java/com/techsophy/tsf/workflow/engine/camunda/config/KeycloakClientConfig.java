package com.techsophy.tsf.workflow.engine.camunda.config;

import org.keycloak.authorization.client.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "keycloak")
@org.springframework.context.annotation.Configuration
public class KeycloakClientConfig extends Configuration
{
    public KeycloakClientConfig(){

    }
    public KeycloakClientConfig(KeycloakClientConfig config,String clientId, Map<String, Object> clientCredentials) {
        super(config.getAuthServerUrl(),config.getRealm(),clientId,clientCredentials,config.getHttpClient());
    }
        public static KeycloakClientConfig create(KeycloakClientConfig config, String clientId, String clientSecret){
        Map<String,Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret",clientSecret);
        clientCredentials.put("clientId",clientId);
        return new KeycloakClientConfig(config,clientId,clientCredentials);
    }
}
