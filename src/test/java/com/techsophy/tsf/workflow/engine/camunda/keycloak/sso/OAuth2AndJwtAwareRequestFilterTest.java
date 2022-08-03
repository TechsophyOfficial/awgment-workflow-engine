package com.techsophy.tsf.workflow.engine.camunda.keycloak.sso;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.oauth2.core.oidc.IdTokenClaimNames.SUB;

public class OAuth2AndJwtAwareRequestFilterTest
{
    @Test
    void getUserName()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        try
        {
            Map<String, Object> claims = Map.of(SUB, "user1","preferred_username","user1");
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("role1"));

            context.setAuthentication(getAnonymousToken(authorities));

            // no username for anonymous token
            assertThat(OAuth2AndJwtAwareRequestFilter.getUserName()).isEmpty();

            context.setAuthentication(getOAuth2Token(authorities, claims));

            // use 'sub' claim from OAuth token
            assertThat(OAuth2AndJwtAwareRequestFilter.getUserName()).contains("user1");

            context.setAuthentication(new JwtAuthenticationToken(getJwt(claims)));

            // use 'sub' claim from JWT token
            assertThat(OAuth2AndJwtAwareRequestFilter.getUserName()).contains("user1");

            context.setAuthentication(new JwtAuthenticationToken(getJwt(Map.of("preferred_username", "user2",SUB,
                    "user2"))));

            // use 'sub' claim from JWT token
            assertThat(OAuth2AndJwtAwareRequestFilter.getUserName()).contains("user2");

            context.setAuthentication(new JwtAuthenticationToken(getJwt(Map.of("preferred_username", "user2", "clientId", "client1"))));

        }
        finally
        {
            context.setAuthentication(authentication);
        }
    }

    private static AnonymousAuthenticationToken getAnonymousToken(List<SimpleGrantedAuthority> authorities)
    {
        return new AnonymousAuthenticationToken("anon", "anon", authorities);
    }

    private static OAuth2AuthenticationToken getOAuth2Token(List<SimpleGrantedAuthority> authorities, Map<String, Object> claims)
    {
        OidcIdToken token = new OidcIdToken("ttt", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), claims);
        DefaultOidcUser user = new DefaultOidcUser(authorities, token, new OidcUserInfo(claims));
        return new OAuth2AuthenticationToken(user, List.of(), "sdsd");
    }

    public static Jwt getJwt(Map<String, Object> claims)
    {
        return new Jwt("ttt", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), Map.of("blah", "blah"), claims);
    }
}
