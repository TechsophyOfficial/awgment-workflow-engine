package com.techsophy.tsf.workflow.engine.camunda.keycloak.sso;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public static Optional<String> getTenantName()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null)
        {
            Authentication authentication = context.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            {
                Object principal = authentication.getPrincipal();
                if (principal instanceof Jwt)
                {
                    Jwt jwt = (Jwt) principal;
                    List<String> iss = List.of(jwt.getClaim("iss").toString().split(URL_SEPERATOR));
                    return Optional.of(iss.get(iss.size()-1));
                }
                if(principal instanceof OAuth2User)
                {
                    return Optional.of(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
                }
            }
        }
        return Optional.empty();
    }

    public static List<String> getUserGroups()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null)
        {
            Authentication authentication = context.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            {
                Object principal = authentication.getPrincipal();
                if (principal instanceof Jwt)
                {
                    Jwt jwt = (Jwt) principal;
                    List<String> groups = getGroups(jwt.getClaim("groups"));
                    return groups;
                }
                if(principal instanceof OAuth2User)
                {
//                    return List.of(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
                    return getGroups((
                            (OAuth2User)SecurityContextHolder.getContext().getAuthentication()
                                    .getPrincipal()).getAttribute("groups"));
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getGroups(List<String> groupPath){
        if(groupPath==null) return Collections.EMPTY_LIST;
        return groupPath.stream().map(s -> {
            return s.substring(s.lastIndexOf('/')+1);
        }).collect(Collectors.toList());
    }
}
