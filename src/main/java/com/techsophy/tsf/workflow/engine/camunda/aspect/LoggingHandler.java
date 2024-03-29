package com.techsophy.tsf.workflow.engine.camunda.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

@Aspect
@EnableAspectJAutoProxy
@Component
public class LoggingHandler
{
    Logger logger= LoggerFactory.getLogger(this.getClass());
    static final String PLACEHOLDER = "{} {}";

    @Before(CONTROLLER_CLASS_PATH)
    void beforeController(JoinPoint joinPoint)
    {
    String name= joinPoint.getSignature().getName();
    logger.info(PLACEHOLDER,name,IS_INVOKED_IN_CONTROLLER);
    }

    @After(CONTROLLER_CLASS_PATH)
    void afterController(JoinPoint joinPoint)
    {
    String name= joinPoint.getSignature().getName();
    logger.info(PLACEHOLDER,name,EXECUTION_IS_COMPLETED_IN_CONTROLLER);
    }

    @Before(SERVICE_CLASS_PATH)
    void beforeService(JoinPoint joinPoint)
    {
        String name = joinPoint.getSignature().getName();
        logger.info(PLACEHOLDER,name,IS_INVOKED_IN_SERVICE);
    }

    @After(SERVICE_CLASS_PATH)
    void afterService(JoinPoint joinPoint)
    {
        String name = joinPoint.getSignature().getName();
        logger.info(PLACEHOLDER,name,EXECUTION_IS_COMPLETED_IN_SERVICE);
    }

    @AfterThrowing(value=CONTROLLER_CLASS_PATH,throwing=EXCEPTION)
    public void logAfterThrowingController(JoinPoint joinPoint, Exception ex)
    {
        logger.error("{} {} {}",EXCEPTION_THROWN,joinPoint.getSignature().getName(),BRACKETS_IN_CONTROLLER);
        logger.error(PLACEHOLDER,CAUSE,ex.getMessage());
        ex.printStackTrace();
    }

    @AfterThrowing(value=SERVICE_CLASS_PATH,throwing=EXCEPTION)
    public void logAfterThrowingService(JoinPoint joinPoint, Exception ex)
    {
        logger.error("{} {} {}",EXCEPTION_THROWN,joinPoint.getSignature().getName(),BRACKETS_IN_SERVICE);
        logger.error(PLACEHOLDER,CAUSE,ex.getMessage());
        ex.printStackTrace();
    }
}
