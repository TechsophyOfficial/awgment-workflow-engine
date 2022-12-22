package com.techsophy.tsf.workflow.engine.camunda.config;

import com.techsophy.tsf.workflow.engine.camunda.SwaggerApiEnroll;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.List;
import java.util.function.Predicate;

import static com.techsophy.tsf.workflow.engine.camunda.constants.LogMessages.DESCRYPTION;

@Configuration
@EnableSwagger2
@AllArgsConstructor
public class SwaggerConfig
{
    private final ApplicationContext context;

    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(getApiSelector())
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo()
    {
        List<String> profiles = List.of(this.context.getEnvironment().getActiveProfiles());
        return new ApiInfoBuilder().title("DMP Application API")
                .description(getDescription())
                .version(String.join(",", profiles))
                .build();
    }
    private static Predicate<RequestHandler> getApiSelector()
    {
        return RequestHandlerSelectors.withClassAnnotation(SwaggerApiEnroll.class);
    }

    private static String getDescription()
    {
        return DESCRYPTION;
    }
}
