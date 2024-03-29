package com.techsophy.tsf.workflow.engine.camunda.keycloak.sso;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.UNABLE_TO_FIND_TENANT;

/**
 * OAuth2 Authentication Provider for usage with Keycloak and KeycloakIdentityProviderPlugin.
 */

@Slf4j
public class KeycloakAuthenticationProvider extends ContainerBasedAuthenticationProvider
{
    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine)
    {
        // Extract user-name-attribute of the OAuth2 token
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof OAuth2AuthenticationToken) || !(authentication.getPrincipal() instanceof OAuth2User))
		{
			return AuthenticationResult.unsuccessful();
		}
        String userId = ((OAuth2User)authentication.getPrincipal()).getName();
        if (!StringUtils.hasLength(userId))
        {
            return AuthenticationResult.unsuccessful();
        }
        // Authentication successful
        AuthenticationResult authenticationResult = new AuthenticationResult(userId, true);

        authenticationResult.setGroups(OAuth2AndJwtAwareRequestFilter.getUserGroups());





        try {
            authenticationResult.setTenants(
                    List.of(OAuth2AndJwtAwareRequestFilter.getTenantName().orElse(null))
            );
        }catch (Exception e){
            log.info(UNABLE_TO_FIND_TENANT,e);
        }
        return authenticationResult;
    }
}
