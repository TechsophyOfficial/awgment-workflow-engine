package com.techsophy.tsf.workflow.engine.camunda.keycloak.rest;

import com.techsophy.tsf.workflow.engine.camunda.config.TenantAuthenticationManagerResolver;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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

    private TenantAuthenticationManagerResolver tenantAuthenticationManagerResolver;

    @Override
    public void configure(final HttpSecurity http) throws Exception
    {
//        String jwkSetUri = applicationContext.getEnvironment().getRequiredProperty(
//                "spring.security.oauth2.client.provider." + configProps.getProvider() + ".jwk-set-uri");

        http
                .csrf().ignoringAntMatchers("/api/**", "/engine-rest/**", "/service/**", "/wrapper/**")
                .and()
                .requestMatchers()
                .antMatchers("/engine-rest/**", "/service/**", "/wrapper/**")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oAuth ->
                    oAuth.authenticationManagerResolver(tenantAuthenticationManagerResolver)
                );

//                .jwt().jwkSetUri(jwkSetUri);

        http.addFilterAfter(new OAuth2AndJwtAwareRequestFilter(), SecurityContextHolderAwareRequestFilter.class);
    }

    /**
     * Create a JWT decoder with issuer and audience claim validation.
     *
     * @return the JWT decoder
     */
    @Bean
    public JwtDecoder jwtDecoder()
    {
        String issuerUri = applicationContext.getEnvironment().getRequiredProperty(
                "spring.security.oauth2.client.provider." + configProps.getProvider() + ".issuer-uri");

        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
                JwtDecoders.fromOidcIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(configProps.getRequiredAudience());
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
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
