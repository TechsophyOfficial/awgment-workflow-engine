package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.KeycloakAuthenticationFilter;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilterTest;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KeycloakAuthenticationFilterTest
{
    private final String userId = "user@test.com";
    private final String groupId = "sampleGroupId";

    private final IdentityService identityService = mock(IdentityService.class);
    private final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    private final FilterChain mockFilterChain = mock(FilterChain.class);
    private final GroupQuery groupQuery = mock(GroupQuery.class);
    private final Authentication authentication = mock(Authentication.class);
    private final KeycloakAuthenticationFilter keycloakAuthenticationFilter = new KeycloakAuthenticationFilter(identityService);

    @BeforeEach
    void mockGroups()
    {
        Group testGroup = new GroupEntity(this.groupId);
        List<Group> groups = new ArrayList<>();
        groups.add(testGroup);

        when(identityService.createGroupQuery()).thenReturn(groupQuery);
        when(groupQuery.groupMember(this.userId)).thenReturn(groupQuery);
        when(groupQuery.list()).thenReturn(groups);
    }

    @SneakyThrows
    @Test
    void testFilterWithJwtAuthenticationToken()
    {
        Jwt jwt = OAuth2AndJwtAwareRequestFilterTest.getJwt(Map.of("preferred_username", this.userId));

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        keycloakAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        List<String> groupsIds = new ArrayList<>();
        groupsIds.add(this.groupId);

        verify(identityService).setAuthentication(this.userId, groupsIds);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(identityService).clearAuthentication();
    }

    @SneakyThrows
    @Test
    void testFilterWithOidcUser()
    {
        OidcUser oidcUser = mock(OidcUser.class);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getName()).thenReturn(this.userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        keycloakAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        List<String> groupsIds = new ArrayList<>();
        groupsIds.add(this.groupId);
        verify(identityService).setAuthentication(this.userId, groupsIds);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        verify(identityService).clearAuthentication();
    }

    @Test
    void testInvalidAuthentication()
    {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Exception exception = Assertions.assertThrows(ServletException.class, () ->
                keycloakAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertThat(exception.getMessage()).isEqualTo("Unable to extract user-name-attribute from token");
    }

    @Test
    void testEmptyUserId()
    {
        OidcUser oidcUser = mock(OidcUser.class);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getName()).thenReturn("");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Exception exception = Assertions.assertThrows(ServletException.class,
                () -> keycloakAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain));

        assertThat(exception.getMessage()).isEqualTo("Unable to extract user-name-attribute from token");
    }
}
