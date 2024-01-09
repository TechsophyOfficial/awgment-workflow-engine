package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;
import com.techsophy.tsf.workflow.engine.camunda.dto.TenantRegistration;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.KeycloakRealmRepository;
import com.techsophy.tsf.workflow.engine.camunda.service.FetchClientCredentials;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.WorkflowEngineConstants.*;

@ExtendWith(MockitoExtension.class)
class KeycloakRealmRepositoryTest {
  @Mock
  FetchClientCredentials fetchClientCredentials;
  @InjectMocks
  KeycloakRealmRepository keycloakRealmRepository;
  private String tenants = "techsophy-platform,KIMS";

  @Test
  void iteratorTest() {
    TenantRegistration tenantRegistration = new TenantRegistration();
    List<String> registrationsList = new ArrayList<>();
    registrationsList.add(tenants);
    tenantRegistration.setRegistrations(registrationsList);
    ReflectionTestUtils.setField(keycloakRealmRepository, "tenants", tenantRegistration);
    ReflectionTestUtils.setField(keycloakRealmRepository, "clientId", "camunda-identity-service");
    ReflectionTestUtils.setField(keycloakRealmRepository, "keycloakIssuerURI", "https://keycloak-tsplatform.techsophy.com/auth/realms/");
    Iterator<ClientRegistration> iterator = keycloakRealmRepository.iterator();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertEquals(KIMS, iterator.next().getRegistrationId());
    Assertions.assertEquals("camunda-identity-service", iterator.next().getClientId());
  }

  @Test
  void testFindByRegistrationIdRefreshSecretFalse() {
    Map<String, ClientRegistration> registrationMap = new HashMap<>();
    registrationMap.put(TECHSOPHY_PLATFORM, ClientRegistration
      .withRegistrationId(TECHSOPHY_PLATFORM)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .clientId(CLIENT_ID).redirectUri(TEST_URL + REALMS + TECHSOPHY_PLATFORM)
      .authorizationUri(TEST_URL + REALMS + TECHSOPHY_PLATFORM)
      .tokenUri(TEST_URL)
      .build());
    ReflectionTestUtils.setField(keycloakRealmRepository, "registrationMap", registrationMap);
    List<String> tenantsList = new ArrayList<>();
    tenantsList.add(tenants);
    List<String> registrationsList = new ArrayList<>();
    registrationsList.add(TECHSOPHY_PLATFORM);
    registrationsList.add(KIMS);
    TenantRegistration tenantRegistration = new TenantRegistration();
    tenantRegistration.setRegistrations(tenantsList);
    ReflectionTestUtils.setField(keycloakRealmRepository, "tenants", tenantRegistration);
    ReflectionTestUtils.setField(keycloakRealmRepository, "registrationsList", registrationsList);
    ClientDetails clientDetails = new ClientDetails();
    clientDetails.setSecret("101");
    clientDetails.setClientId("camunda-identity-service");
    Mockito.when(fetchClientCredentials.fetchClientDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(clientDetails);
    ClientRegistration clientRegistration = keycloakRealmRepository.findByRegistrationId(TECHSOPHY_PLATFORM);
    Assertions.assertNotNull(clientRegistration);
    Assertions.assertEquals(CLIENT_SECRET, clientRegistration.getClientSecret());
    Assertions.assertEquals(CLIENT_ID, clientRegistration.getClientId());
  }

  @Test
  void testFindByRegistrationIdWithException() {
    Map<String, ClientRegistration> registrationMap = new HashMap<>();
    registrationMap.put(TECHSOPHY_PLATFORM, ClientRegistration
      .withRegistrationId(TECHSOPHY_PLATFORM)
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .clientId(CLIENT_ID).redirectUri(TEST_URL + REALMS + TECHSOPHY_PLATFORM)
      .authorizationUri(TEST_URL + REALMS + TECHSOPHY_PLATFORM)
      .tokenUri(TEST_URL)
      .build());
    ReflectionTestUtils.setField(keycloakRealmRepository, "registrationMap", registrationMap);
    List<String> tenantsList = new ArrayList<>();
    tenantsList.add(tenants);
    List<String> registrationsList = new ArrayList<>();
    registrationsList.add(TECHSOPHY_PLATFORM);
    registrationsList.add(KIMS);
    ReflectionTestUtils.setField(keycloakRealmRepository, "registrationsList", registrationsList);
    TenantRegistration tenantRegistration = new TenantRegistration();
    tenantRegistration.setRegistrations(tenantsList);
    ReflectionTestUtils.setField(keycloakRealmRepository, "tenants", tenantRegistration);
    ClientDetails clientDetails = new ClientDetails();
    clientDetails.setSecret("101");
    clientDetails.setClientId("camunda-identity-service");
    Mockito.when(fetchClientCredentials.fetchClientDetails("techsophy-platform", false)).thenThrow(new IllegalArgumentException());
    Mockito.when(fetchClientCredentials.fetchClientDetails("techsophy-platform", true)).thenReturn(clientDetails);
    ClientRegistration clientRegistration = keycloakRealmRepository.findByRegistrationId(TECHSOPHY_PLATFORM);
    Assertions.assertNotNull(clientRegistration);
    Assertions.assertEquals(CLIENT_SECRET, clientRegistration.getClientSecret());
    Assertions.assertEquals(CLIENT_ID, clientRegistration.getClientId());
  }
}
