package com.techsophy.tsf.workflow.engine.camunda.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.*;
import static org.mockito.ArgumentMatchers.any;

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
        String responseTest=mockGlobalMessageSource.get(KEY);
        Assertions.assertNotNull(responseTest);
    }

    @Test
    void getTestDoubleArguments()
    {
        Mockito.when(mockMessageSource.getMessage(any(),any(),any())).thenReturn(KEY);
        String responseTest=mockGlobalMessageSource.get(KEY,ARGS);
        Assertions.assertNotNull(responseTest);
    }
}