package com.techsophy.tsf.workflow.engine.camunda.keycloak.rest;

import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilter;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.IdentityService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * Optional Security Configuration for Camunda REST Api.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Order(SecurityProperties.BASIC_AUTH_ORDER - 20)
@ConditionalOnProperty(name = "rest.security.enabled", havingValue = "true", matchIfMissing = true)
public class RestApiSecurityConfig extends WebSecurityConfigurerAdapter
{
    private final IdentityService identityService;
    private final ApplicationContext applicationContext;
    private final RestApiSecurityConfigurationProperties configProps;
    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;


    @Override
    public void configure(final HttpSecurity http) throws Exception
    {
        http
                .csrf().ignoringAntMatchers("/api/**", "/engine-rest/**", "/service/**", "/wrapper/**")
                .and()
                .requestMatchers()
                .antMatchers("/engine-rest/**", "/service/**", "/wrapper/**")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(this.authenticationManagerResolver));
        http.addFilterAfter(new OAuth2AndJwtAwareRequestFilter(), SecurityContextHolderAwareRequestFilter.class);
    }

    @Bean
    public FilterRegistrationBean<KeycloakAuthenticationFilter> keycloakAuthenticationFilter()
    {
        FilterRegistrationBean<KeycloakAuthenticationFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new KeycloakAuthenticationFilter(identityService));
        filterRegistration.setOrder(102); // make sure the filter is registered after the Spring Security Filter Chain
        filterRegistration.addUrlPatterns("/engine-rest/*", "/service/*");
        return filterRegistration;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter()
    {
        FilterRegistrationBean<CorsFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new CorsFilter(corsConfigurationSource));
        filterRegistration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistration.addUrlPatterns("/engine-rest/*", "/service/*");
        return filterRegistration;
    }
}
