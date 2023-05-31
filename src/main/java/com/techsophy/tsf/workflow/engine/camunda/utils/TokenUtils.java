package com.techsophy.tsf.workflow.engine.camunda.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.ErrorMessageConstants.*;

@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TokenUtils
{
    private final ObjectMapper objectMapper;
    private GlobalMessageSource globalMessageSource;

    public String getIssuerFromToken(String idToken)
    {
        String tenantName = EMPTY_STRING;
        final Base64.Decoder decoder = Base64.getDecoder();
        if (idToken.startsWith(BEARER))
        {
            idToken=idToken.substring(SEVEN);
        }
        Map<String, Object> tokenBody = new HashMap<>();
        List<String> tokenizer = Arrays.asList(idToken.split(REGEX_SPLIT));
        for(String token:tokenizer)
        {
            if(token.equals(tokenizer.get(ONE)))
            {
                tokenBody=string2JSONMap(new String(decoder.decode(token)));
            }
        }
        if( tokenBody == null )
        {
            throw new InvalidInputException(INVALID_TOKEN,globalMessageSource.get(INVALID_TOKEN));
        }
        if( tokenBody.containsKey(ISS))
        {
            List<String> elements= Arrays.asList(tokenBody.get(ISS).toString().split(URL_SEPERATOR));
            tenantName=elements.get(elements.size()-1);
        }
        return tenantName;
    }

    public String getTokenFromContext()
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null)
        {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            {
                Object principal = authentication.getPrincipal();
                if (principal instanceof OAuth2User)
                {
                    return Optional.of(((OAuth2User) principal).getName()).orElseThrow(()->new SecurityException(AUTHENTICATION_FAILED));
                }
                else if (principal instanceof Jwt)
                {
                    Jwt jwt = (Jwt) principal;

                    return jwt.getTokenValue();
                }
                else
                {
                    throw new SecurityException(UNABLE_GET_TOKEN);
                }
            }
            else
            {
                throw new SecurityException(UNABLE_GET_TOKEN);
            }
        }
        else
        {
            throw new SecurityException(UNABLE_GET_TOKEN);
        }
    }

    public String getLoggedInUserId()
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null)
        {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            {
                Object principal = authentication.getPrincipal();
                if (principal instanceof OAuth2User)
                {
                    return Optional.of(((OAuth2User) principal).getName()).orElseThrow(()->new SecurityException(AUTHENTICATION_FAILED));
                }
                else if (principal instanceof Jwt)
                {
                    Jwt jwt = (Jwt) principal;
                    return jwt.getClaim(PREFERED_USERNAME);
                }
                else
                {
                    throw new SecurityException(AUTHENTICATION_FAILED);
                }
            }
            else
            {
                throw new SecurityException(AUTHENTICATION_FAILED);
            }
        }
        else
        {
            throw new SecurityException(AUTHENTICATION_FAILED);
        }
    }

    @SneakyThrows
    public Map<String, Object> string2JSONMap(String json)
    {
        // convert JSON string to Map
        return objectMapper.readValue(json, new TypeReference<>(){});
    }
}
