package com.techsophy.tsf.workflow.engine.camunda.keycloak.rest;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Keycloak Authentication Filter - used for REST API Security.
 */
@Slf4j
@AllArgsConstructor

public class KeycloakAuthenticationFilter implements Filter
{
    private static final Supplier<ServletException> UNKNOWN_USER_EXCEPTION = () ->
            new ServletException("Unable to extract user-name-attribute from token");

    /**
     * Access to Camunda's IdentityService.
     */
    private final IdentityService identityService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        String userId = OAuth2AndJwtAwareRequestFilter.getUserName().orElseThrow(UNKNOWN_USER_EXCEPTION);
        String tenantName = OAuth2AndJwtAwareRequestFilter.getTenantName().orElseThrow();
        List<String> groups = OAuth2AndJwtAwareRequestFilter.getUserGroups();
        log.debug("Extracted userId from bearer token: {}", userId);

        try {
            identityService.setAuthentication(userId, groups, List.of(tenantName));
            TenantContext.setTenantId(tenantName);
            chain.doFilter(request, response);
        }
        finally
        {
            identityService.clearAuthentication();
        }
    }

    /**
     * Queries the groups of a given user.
     *
     * @param userId the user's ID
     * @return list of groups the user belongs to
     */

}
