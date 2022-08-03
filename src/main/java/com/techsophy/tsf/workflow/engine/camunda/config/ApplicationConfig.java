package com.techsophy.tsf.workflow.engine.camunda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import java.awt.image.BufferedImage;
import java.util.List;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

@Configuration
public class ApplicationConfig
{
    @Bean
    public HttpMessageConverter<BufferedImage> httpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @Value(GATEWAY_URL)
    String gatewayUrl;
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info().title(WORKFLOW_ENGINE).version(VERSION_V1).description(WORKFLOW_ENGINE_API_VERSION_V1))
                .servers( List.of(new Server().url(gatewayUrl)));
    }
}
