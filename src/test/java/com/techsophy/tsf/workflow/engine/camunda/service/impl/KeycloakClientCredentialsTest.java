package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
 class KeycloakClientCredentialsTest {

    @Mock
    private Keycloak keycloak;
    @Mock
    RealmResource realmResource;
    @Mock
    ClientsResource clientsResource;
    @Mock
    ClientResource clientResource;
    @Mock
    CredentialRepresentation credentialRepresentation;
    @InjectMocks
    private KeycloakClientCredentials keycloakClientCredentials;
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(keycloakClientCredentials,"clientId","camunda-identity-service");
    }

    @Test
    void fetchClientDetailsBooleanFalseArg()
    {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClientId("1212");
        clientDetails.setSecret("121");
        Mockito.when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        Mockito.when(realmResource.clients()).thenReturn(clientsResource);
        Mockito.when(clientsResource.get(ArgumentMatchers.anyString())).thenReturn(clientResource);
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId("123");
        Mockito.when(clientResource.getSecret()).thenReturn(credentialRepresentation);
        Mockito.when(clientsResource.findByClientId(ArgumentMatchers.anyString())).thenReturn(List.of(clientRepresentation));
        keycloakClientCredentials.fetchClientDetails("techsophy-platform",false);
        keycloakClientCredentials.fetchClientDetails("techsophy-platform",false);
        Mockito.verify(clientsResource,Mockito.times(1)).findByClientId(ArgumentMatchers.anyString());
    }
    @Test
    void fetchClientDetailsBooleanTrueArg()
    {
        Mockito.when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        Mockito.when(realmResource.clients()).thenReturn(clientsResource);
        Mockito.when(clientsResource.get(ArgumentMatchers.anyString())).thenReturn(clientResource);
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId("123");
        Mockito.when(clientResource.getSecret()).thenReturn(credentialRepresentation);
        Mockito.when(clientsResource.findByClientId(ArgumentMatchers.anyString())).thenReturn(List.of(clientRepresentation));
        keycloakClientCredentials.fetchClientDetails("techsophy-platform",true);
        Mockito.verify(credentialRepresentation,Mockito.times(1)).getValue();
    }
    @Test
    void fetchClientDetailsClientIdNotFoundException()
    {
        Mockito.when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(realmResource);
        Mockito.when(realmResource.clients()).thenReturn(clientsResource);
        List<ClientRepresentation> lisOfData = new ArrayList<>();
        Mockito.when(clientsResource.findByClientId(ArgumentMatchers.anyString())).thenReturn(lisOfData);
        Assertions.assertThrows(IllegalArgumentException.class,()->keycloakClientCredentials.fetchClientDetails("techsophy-platform",true));
    }
    @Test
    void fetchClientDetailsRealmNotFoundException()
    {
        Mockito.when(keycloak.realm(ArgumentMatchers.anyString())).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class,()->keycloakClientCredentials.fetchClientDetails("techsophy-platform",true));
    }
}
