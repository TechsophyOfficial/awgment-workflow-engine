package com.techsophy.tsf.workflow.engine.camunda.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WebClientWrapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

@RefreshScope
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TokenUtils
{
    GlobalMessageSource globalMessageSource;
    @Value(KEYCLOAK_ISSUER_URI)
    private final String keyCloakApi;

    ObjectMapper objectMapper;
//    WebClientWrapper webClientWrapper;

    WebClientWrapper webClientWrapper;

    private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    public String getLoggedInUserId()
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
                    return Optional.of(((OAuth2User) principal).getName()).filter(StringUtils::isNotEmpty).orElseThrow(RuntimeException::new);
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

    public String getIssuerFromContext()
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
                    return Optional.of(((OAuth2User) principal).getName()).filter(StringUtils::isNotEmpty).orElseThrow(RuntimeException::new);
                }
                else if (principal instanceof Jwt)
                {
                    Jwt jwt = (Jwt) principal;
                    List<String> issuerUrl= Arrays.asList(jwt.getClaim(ISS).toString().split(URL_SEPERATOR));
                    return issuerUrl.get(issuerUrl.size()-1);
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
           throw new IllegalArgumentException(INVALID_TOKEN);
       }
       if(tokenBody.containsKey(ISS))
       {
           List<String> elements= Arrays.asList(tokenBody.get(ISS).toString().split(URL_SEPERATOR));
           tenantName=elements.get(elements.size()-1);
       }
       return tenantName;
   }

    public String getTokenFromContext()
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
                    return Optional.of(((OAuth2User) principal).getName()).filter(StringUtils::isNotEmpty).orElseThrow(RuntimeException::new);
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

    @SneakyThrows
    public Map<String, Object> string2JSONMap(String json)
    {
        ObjectMapper mapper = new ObjectMapper();
        // convert JSON string to Map
        return mapper.readValue(json, new TypeReference<>(){});
    }



}
