package ru.utlc.referencedataservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Pointcuts for controllers and services
    @Pointcut("within(ru.utlc.referencedataservice.controller..*)")
    public void controllerPackage() {}

    @Pointcut("within(ru.utlc.referencedataservice.service..*)")
    public void servicePackage() {}

    // Logging for controllers
    @Before("controllerPackage()")
    public void logControllerMethods(JoinPoint joinPoint) {
        log.info("Request to {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    // Logging for services
    @Before("servicePackage()")
    public void logServiceMethods(JoinPoint joinPoint) {
        log.info("Executing {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    // Logging results for controllers
    @AfterReturning(pointcut = "controllerPackage()", returning = "result")
    public void logControllerResults(JoinPoint joinPoint, Object result) {
        log.info("Result from {}: {}", joinPoint.getSignature().toShortString(), result);
    }

    // Logging results for services
    @AfterReturning(pointcut = "servicePackage()", returning = "result")
    public void logServiceResults(JoinPoint joinPoint, Object result) {
        log.info("Result from {}: {}", joinPoint.getSignature().toShortString(), result);
    }

    // Logging exceptions
    @AfterThrowing(pointcut = "controllerPackage() || servicePackage()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in {} with cause: {}", joinPoint.getSignature().toShortString(), error.getMessage());
        if (error.getCause() != null) log.error("Underlying cause: {}", error.getCause().getMessage());
    }
}
