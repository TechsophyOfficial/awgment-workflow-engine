package com.techsophy.tsf.workflow.engine.camunda.keycloak.sso;

import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;
import com.techsophy.tsf.workflow.engine.camunda.dto.TenantRegistration;
import com.techsophy.tsf.workflow.engine.camunda.service.FetchClientCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KeycloakRealmRepository implements ClientRegistrationRepository, Iterable<ClientRegistration>
{
    @Autowired
    private TenantRegistration tenants;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.issuer-uri}")
    private String keycloakIssuerURI;
    @Autowired
    private FetchClientCredentials service;

    private Map<String, ClientRegistration> registrationMap;

    @Override
    public Iterator<ClientRegistration> iterator()
    {
        registrationMap = tenants.getRegistrations().parallelStream().map(s -> ClientRegistrations
                .fromOidcIssuerLocation(keycloakIssuerURI+s)
                .registrationId(s).clientName(s).clientId(clientId).build()).collect(
                Collectors.toMap(ClientRegistration::getRegistrationId, clientRegistration -> clientRegistration
                ));
        return new ArrayList<>(registrationMap.values()).iterator();
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId)
    {
       return tenants.getRegistrations().stream().filter(s->s.equals(registrationId)).map(s->{
        ClientDetails secret;
        try
        {
            secret= service.fetchClientDetails(s, false);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            secret=service.fetchClientDetails(s,true);
        }
        return ClientRegistration.withClientRegistration(registrationMap.get(s))
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .clientSecret(secret.getSecret())
                .userNameAttributeName("preferred_username")
                .build();
              }).findFirst().orElseThrow();
    }
}
