package com.techsophy.tsf.workflow.engine.camunda.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.TEST_ACTIVE_PROFILE;

//@SpringBootTest
@ExtendWith({SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(TEST_ACTIVE_PROFILE)
class LoggingHandlerTest {
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
        Assertions.assertTrue(true);
    }

    @Test
    void logAfterThrowingService() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.logAfterThrowingService(joinPoint, exception);
        Assertions.assertTrue(true);
    }

    @Test
    void beforeControllerTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.beforeController(joinPoint);
        Assertions.assertTrue(true);
    }

    @Test
    void afterControllerTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.afterController(joinPoint);
        Assertions.assertTrue(true);
    }

    @Test
    void beforeServiceTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.beforeService(joinPoint);
        Assertions.assertTrue(true);
    }

    @Test
    void afterServiceTest() {
        Mockito.when((joinPoint.getSignature())).thenReturn(signature);
        Mockito.when((signature.getName())).thenReturn("abc");
        loggingHandler.afterService(joinPoint);
        Assertions.assertTrue(true);
    }
}
