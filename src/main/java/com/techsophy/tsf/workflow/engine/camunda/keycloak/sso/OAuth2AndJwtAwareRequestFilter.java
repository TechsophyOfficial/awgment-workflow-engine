package com.techsophy.tsf.workflow.engine.camunda.keycloak.sso;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.ISS;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.URL_SEPERATOR;

/*
 *   SecurityContextHolderAwareRequestFilter doesn't seem to be able to extract
 *   username properly from as OAuth2 authenticated user token. As a fallback,
 *   it ends up calling DefaultOIDCUser.toString() which has a lot of info about the user
 *   including the roles and groups which pollutes the logs for each request. This filter
 *   solves that problem by extracting the username properly from the OAuth2 token
 * */
public class OAuth2AndJwtAwareRequestFilter extends HttpFilter
{

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        super.doFilter(new OAuth2SecurityContextAwareRequestWrapper(request), response, chain);
    }

    private static class OAuth2SecurityContextAwareRequestWrapper extends HttpServletRequestWrapper
    {
        public OAuth2SecurityContextAwareRequestWrapper(HttpServletRequest request)
        {
            super(request);
        }

        @Override
        public String getRemoteUser()
        {
            return getUserName().orElseGet(super::getRemoteUser);
        }
    }

    public static Optional<String> getUserName()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null)
        {
            Authentication authentication = context.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            {
                Object principal = authentication.getPrincipal();
                if (principal instanceof OAuth2User)
                {
                    return Optional.of(((OAuth2User) principal).getName()).filter(StringUtils::isNotEmpty);
                }
                if (principal instanceof Jwt)
                {
                    Jwt jwt = (Jwt) principal;
                    return Optional.of(jwt.getClaim("preferred_username"));
                }
            }
        }
        return Optional.empty();
    }
    public static Optional<String> getTenant() {

        String tenantName = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof OAuth2User) {
                    return Optional.empty();//.of(((OAuth2User) principal).getName()).filter(StringUtils::isNotEmpty);
                }

                if (principal instanceof Jwt) {
                    Jwt jwt = (Jwt) principal;
                    String issuer = String.valueOf(jwt.getIssuer());

                    List<String> elements = Arrays.asList(issuer.split(URL_SEPERATOR));
                    tenantName = elements.get(elements.size() - 1);

                    return Optional.ofNullable(tenantName);

                }
            }
        }
        return Optional.empty();
    }

}
