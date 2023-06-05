package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.config.TenantAuthenticationManagerResolver;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.KeycloakLogoutHandler;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.KeycloakRealmRepository;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.WebAppSecurityConfig;
import com.techsophy.tsf.workflow.engine.camunda.utils.TokenUtils;
import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.ForwardedHeaderFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebAppSecurityConfigTest
{
    KeycloakLogoutHandler keycloakLogoutHandler = new KeycloakLogoutHandler("http://localhost:8080");
    KeycloakRealmRepository keycloakRealmRepository=new KeycloakRealmRepository();
    @Mock
    TokenUtils tokenUtils;
    TenantAuthenticationManagerResolver tenantAuthenticationManagerResolver=new TenantAuthenticationManagerResolver("http://keycloak",tokenUtils);
    WebAppSecurityConfig webAppSecurityConfig = new WebAppSecurityConfig(keycloakLogoutHandler,keycloakRealmRepository,tenantAuthenticationManagerResolver);

    @Test
    void testContainerBasedAuthFilter()
    {
        FilterRegistrationBean<ContainerBasedAuthenticationFilter> containerBasedAuthenticationFilter = webAppSecurityConfig.containerBasedAuthenticationFilter();
        assertNotNull(containerBasedAuthenticationFilter);
        // Order is important while registering filter beans
        assertThat(containerBasedAuthenticationFilter.getOrder()).isEqualTo(101);
    }

    @Test
    void testRequestContextListener()
    {
        RequestContextListener requestContextListener = webAppSecurityConfig.requestContextListener();
        assertNotNull(requestContextListener);
    }

    @Test
    void testForwardedHeaderFilter()
    {
        FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter= webAppSecurityConfig.forwardedHeaderFilter();
        assertNotNull(forwardedHeaderFilter);
        // Order is important while registering filter beans
        assertThat(forwardedHeaderFilter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }
}