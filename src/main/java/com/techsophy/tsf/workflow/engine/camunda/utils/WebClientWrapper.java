package com.techsophy.tsf.workflow.engine.camunda.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.ExternalServiceErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

//@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebClientWrapper
{
    private final GlobalMessageSource globalMessageSource;
    private final ObjectMapper objectMapper;

    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();
    public WebClient createWebClient(String token)
    {
        return   WebClient.builder()
                .defaultHeader(AUTHORIZATION,BEARER+token)
                .defaultHeader(CONTENT_TYPE,APPLICATION_JSON)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    public String webclientRequest(WebClient client, String url, @NotBlank String requestType, Object data)
    {
        if (!requestType.equalsIgnoreCase(GET)) {
            if(requestType.equalsIgnoreCase(DELETE))
            {
                if (data == null)
                {
                    return Objects.requireNonNull(client.method(HttpMethod.DELETE)
                            .uri(url)
                            .retrieve()
                                    .onStatus(HttpStatus::isError, clientResponse -> {
                                        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
                                        return errorMessage.flatMap(msg -> {
                                            throw new ExternalServiceErrorException(msg,msg);
                                        });
                                    }))
                            .bodyToMono(String.class)
                            .block();
                }
                else {
                    return Objects.requireNonNull(client.method(HttpMethod.DELETE)
                            .uri(url)
                            .bodyValue(data)
                            .retrieve()
                                    .onStatus(HttpStatus::isError, clientResponse -> {
                                        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
                                        return errorMessage.flatMap(msg -> {
                                            throw new ExternalServiceErrorException(msg,msg);
                                        });
                                    }))
                            .bodyToMono(String.class)
                            .block();
                }
            }
            if(requestType.equalsIgnoreCase(PUT))
            {
                return Objects.requireNonNull(client.put()
                        .uri(url)
                        .bodyValue(data)
                        .retrieve()
                                .onStatus(HttpStatus::isError, clientResponse -> {
                                    Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
                                    return errorMessage.flatMap(msg -> {
                                        throw new ExternalServiceErrorException(msg,msg);
                                    });
                                }))
                        .bodyToMono(String.class)
                        .block();
            }
            return Objects.requireNonNull(client.post()
                    .uri(url)
                    .bodyValue(data)
                    .retrieve()
                            .onStatus(HttpStatus::isError, clientResponse -> {
                                Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
                                return errorMessage.flatMap(msg -> {
                                    throw new ExternalServiceErrorException(msg,msg);
                                });
                            }))
                    .bodyToMono(String.class)
                    .block();
        } else {
            return Objects.requireNonNull(client.get()
                    .uri(url)
                    .retrieve().onStatus(HttpStatus::isError, clientResponse -> {
                                Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
                                return errorMessage.flatMap(msg -> {
                                    throw new ExternalServiceErrorException(msg,msg);
                                });
                            }))
                    .bodyToMono(String.class)
                    .block();
        }
    }
}
