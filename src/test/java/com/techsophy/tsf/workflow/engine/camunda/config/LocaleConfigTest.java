package com.techsophy.tsf.workflow.engine.camunda.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LocaleConfigTest extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

    @Mock
    HttpServletRequest request;
    @InjectMocks
    LocaleConfig localeConfig;


    @Test
    void resolveLocaleTest() {
        Mockito.when(request.getHeader(any())).thenReturn("test");
        Locale response = localeConfig.resolveLocale(request);
        Assertions.assertNotNull(response);
    }

    @Test
    void resolveLocaleEmptyHeaderTest() {
        Mockito.when(request.getHeader(any())).thenReturn("");
        Locale response = localeConfig.resolveLocale(request);
        Assertions.assertNotNull(response);
    }

    @Test
    void messageSourceTest() {
        MessageSource response = localeConfig.messageSource();
        Assertions.assertNotNull(response);
    }
}
