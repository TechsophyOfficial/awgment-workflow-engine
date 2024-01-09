package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.KeycloakAuthenticationFilter;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.RestApiSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RestApiSecurityConfigTest {
    @InjectMocks
    RestApiSecurityConfig restApiSecurityConfig;

    @Mock
    CorsConfigurationSource CorsConfigurationSource;

    @Test
    void testKeycloakAuthenticationFilter() {
        FilterRegistrationBean<KeycloakAuthenticationFilter> filterRegistrationBean = restApiSecurityConfig.keycloakAuthenticationFilter();

        assertNotNull(filterRegistrationBean);

        // Order is important while registering filter beans
        assertThat(filterRegistrationBean.getOrder()).isEqualTo(102);
    }

    @Test
    void testCorsFilter() {
        FilterRegistrationBean<CorsFilter> filterRegistrationBean = restApiSecurityConfig.corsFilter();

        assertNotNull(filterRegistrationBean);

        // Order is important while registering filter beans
        assertThat(filterRegistrationBean.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }
}
