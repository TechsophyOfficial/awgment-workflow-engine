package com.techsophy.tsf.workflow.engine.camunda.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class TestAuthentication
{
    public static void setAuthenticationToSecurityContextHolder()
    {
        JwtAuthenticationToken auth = new JwtAuthenticationToken(
                Jwt.withTokenValue("test")
                        .header("test","test")
                        .claim("preferred_username","user1@test.com").build());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
