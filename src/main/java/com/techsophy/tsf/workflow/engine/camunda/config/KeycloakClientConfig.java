package com.techsophy.tsf.workflow.engine.camunda.config;

import org.keycloak.authorization.client.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
@org.springframework.context.annotation.Configuration
public class KeycloakClientConfig extends Configuration
{

}
