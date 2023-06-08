package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.KeycloakAuthenticationFilter;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.RestApiSecurityConfig;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ActiveProfiles("test")
//@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
//@AllArgsConstructor(onConstructor_ = {@Autowired})
//@SpringBootTest(classes = TestSecurityConfig.class, webEnvironment = RANDOM_PORT)
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
