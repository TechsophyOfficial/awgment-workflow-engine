package com.techsophy.tsf.workflow.engine.camunda.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Array;

import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.*;
import static org.mockito.ArgumentMatchers.*;

class GlobalMessageSourceTest {

    @Mock
    MessageSource mockMessageSource;
    @InjectMocks
    GlobalMessageSource mockGlobalMessageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTestSingleArgumwent() {
        Mockito.when(mockMessageSource.getMessage(any(),any(),any())).thenReturn(KEY);
        String actualOutput=mockGlobalMessageSource.get(KEY);
        String expectedOutput = mockMessageSource.getMessage(KEY, null, LocaleContextHolder.getLocale());
        Assertions.assertSame(expectedOutput, actualOutput);
    }

    @Test
    void getTestDoubleArguments()
    {
        Mockito.when(mockMessageSource.getMessage(any(),any(),any())).thenReturn(KEY);
        String actualOutput = mockGlobalMessageSource.get(KEY,ARGS);
        String expectedOutput = mockMessageSource.getMessage(KEY, new Object[]{ARGS}, LocaleContextHolder.getLocale());
        Assertions.assertSame(expectedOutput, actualOutput);
    }
}