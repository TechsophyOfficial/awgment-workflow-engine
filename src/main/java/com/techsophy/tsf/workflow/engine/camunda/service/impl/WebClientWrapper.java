package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

@Service
public class WebClientWrapper
{
    public WebClient createWebClient(String token)
    {
      return WebClient.builder()
                .defaultHeader(AUTHORIZATION, BEARER.getValue()+ " "+token)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .build();
    }

    public String webclientRequest(WebClient client, String url, @NotBlank HttpMethod requestType, Object data)
    {
        if(requestType.matches(GET.toString()))
        {
            return Objects.requireNonNull(client.get()
                            .uri(url)
                            .retrieve())
                    .bodyToMono(String.class)
                    .block();
        }
        else
        {
            if(requestType.matches(DELETE.toString()))
            {
                if (data == null)
                {
                    return Objects.requireNonNull(client.method(DELETE)
                                    .uri(url)
                                    .retrieve())
                            .bodyToMono(String.class)
                            .block();
                }
                else
                {
                    return Objects.requireNonNull(client.method(DELETE)
                                    .uri(url)
                                    .bodyValue(data)
                                    .retrieve())
                            .bodyToMono(String.class)
                            .block();
                }
            }
            if(requestType.matches(PUT.toString()))
            {
                return Objects.requireNonNull(client.put()
                                .uri(url)
                                .bodyValue(data)
                                .retrieve())
                        .bodyToMono(String.class)
                        .block();
            }
            return Objects.requireNonNull(client.post()
                            .uri(url)
                            .bodyValue(data)
                            .retrieve())
                    .bodyToMono(String.class)
                    .block();
        }
    }
}

