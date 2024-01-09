package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.KeycloakLogoutHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class KeycloakLogoutHandlerTest {
    @InjectMocks
    KeycloakLogoutHandler keycloakLogoutHandler;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    private RedirectStrategy redirectStrategy;
    @Mock
    private TenantContext tenantContext;
    @Mock
    Authentication authentication;

    private String oauth2UserLogoutUri = "http://example.com/logout";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(keycloakLogoutHandler,"oauth2UserLogoutUri","http://example.com/logout");
    }
    @Test
    void  onLogoutSuccess () throws ServletException, IOException {
        String requestUrl = "http://example.com/app/somepath";
        String redirectUri = "http://example.com";
        String logoutUrl = oauth2UserLogoutUri + "?redirect_uri=" + redirectUri;
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(requestUrl));
        when(httpServletRequest.getContextPath()).thenReturn("/app");
        keycloakLogoutHandler.onLogoutSuccess(httpServletRequest,httpServletResponse,authentication);
        Mockito.verify(httpServletRequest,times(1)).getRequestURL();
    }
}
