package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.KeycloakAuthenticationProvider;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakAuthenticationProviderTest
{
    private final KeycloakAuthenticationProvider keycloakAuthenticationProvider = new KeycloakAuthenticationProvider();
    private final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    private final OidcUser oidcUser = mock(OidcUser.class);
    private final OAuth2AuthenticationToken oAuth2AuthenticationToken = mock(OAuth2AuthenticationToken.class);
    private final GroupQuery groupQuery = mock(GroupQuery.class);
    private final IdentityService identityService = mock(IdentityService.class);
    private final ProcessEngine engine = mock(ProcessEngine.class);

    @Test
    void extractAuthenticatedUserTest()
    {
        final String userId = "user@test.com";
        when(oAuth2AuthenticationToken.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getName()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);

        when(engine.getIdentityService()).thenReturn(identityService);

        Group testGroup = new GroupEntity("sampleGroupId");
        List<Group> groups = new ArrayList<>();
        groups.add(testGroup);

        when(identityService.createGroupQuery()).thenReturn(groupQuery);
        when(groupQuery.groupMember(userId)).thenReturn(groupQuery);
        when(groupQuery.list()).thenReturn(groups);

        AuthenticationResult authenticationResult = keycloakAuthenticationProvider.extractAuthenticatedUser(mockRequest, engine);
        assertThat(authenticationResult.isAuthenticated()).isTrue();
        assertThat(authenticationResult.getAuthenticatedUser()).isEqualTo(userId);
    }

    @Test
    void unsuccessfulAuthenticationTest()
    {
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        AuthenticationResult authenticationResult = keycloakAuthenticationProvider.extractAuthenticatedUser(mockRequest, engine);
        assertThat(authenticationResult.isAuthenticated()).isFalse();
    }

    @Test
    void emptyUserIdTest()
    {
        final String userId = "";
        when(oAuth2AuthenticationToken.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getName()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);
        AuthenticationResult authenticationResult = keycloakAuthenticationProvider.extractAuthenticatedUser(mockRequest, engine);
        assertThat(authenticationResult.isAuthenticated()).isFalse();
    }
}
