package com.techsophy.tsf.workflow.engine.camunda.keycloak.rest;


import com.techsophy.tsf.workflow.engine.camunda.config.TenantAuthenticationManagerResolver;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        log.debug("Extracted userId from bearer token: {}", userId);

        try
        {
            String tenant = OAuth2AndJwtAwareRequestFilter.getTenant().orElse("techsophy-platform");
            TenantAuthenticationManagerResolver.TENANT_CONTEXT.set(tenant);
            identityService.setAuthentication(userId, this.getUserGroups(userId));
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
    private List<String> getUserGroups(String userId)
    {
        // query groups using KeycloakIdentityProvider plugin
        return identityService.createGroupQuery().groupMember(userId)
                .list().stream().map(Group::getId).collect(Collectors.toList());
    }
}
