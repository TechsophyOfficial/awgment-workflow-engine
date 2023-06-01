package com.techsophy.tsf.workflow.engine.camunda.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({SpringExtension.class})
class LoggingHandlerTest
{
    @Mock
    JoinPoint joinPoint;
    @Mock
    Signature signature;
    @Mock
    Exception exception;
    @InjectMocks
    LoggingHandler loggingHandler;

    @Test
    void logAfterThrowingController() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.logAfterThrowingController(joinPoint, exception);
        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void logAfterThrowingService() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.logAfterThrowingService(joinPoint, exception);
        verify(joinPoint,times(1)).getSignature();
    }

    @Test
    void beforeControllerTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.beforeController(joinPoint);
        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void afterControllerTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.afterController(joinPoint);
        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void beforeServiceTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.beforeService(joinPoint);
        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void afterServiceTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.afterService(joinPoint);
        verify(joinPoint, times(1)).getSignature();
    }
}
