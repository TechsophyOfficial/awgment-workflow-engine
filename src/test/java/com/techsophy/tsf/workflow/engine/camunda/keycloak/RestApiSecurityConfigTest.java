package com.techsophy.tsf.workflow.engine.camunda.keycloak;

import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.KeycloakAuthenticationFilter;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.RestApiSecurityConfig;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AllArgsConstructor(onConstructor_ = {@Autowired})
@SpringBootTest(classes = TestSecurityConfig.class, webEnvironment = RANDOM_PORT)
public class RestApiSecurityConfigTest
{
    private final RestApiSecurityConfig restApiSecurityConfig;

//    @Test
//    void testKeycloakAuthenticationFilter()
//    {
//        FilterRegistrationBean<KeycloakAuthenticationFilter> filterRegistrationBean = restApiSecurityConfig.keycloakAuthenticationFilter();
//
//        assertNotNull(filterRegistrationBean);
//
//        // Order is important while registering filter beans
//        assertThat(filterRegistrationBean.getOrder()).isEqualTo(102);
//    }

//    @Test
//    void testCorsFilter()
//    {
//        FilterRegistrationBean<CorsFilter> filterRegistrationBean = restApiSecurityConfig.corsFilter();
//
//        assertNotNull(filterRegistrationBean);
//
//        // Order is important while registering filter beans
//        assertThat(filterRegistrationBean.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
//    }
}
