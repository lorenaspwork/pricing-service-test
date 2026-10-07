package com.lsp.pricingservice.infrastructure.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {

    @Around("""
    execution(* com.lsp.pricingservice.application.usecase.*UseCaseImpl.*(..))
    || execution(* com.lsp.pricingservice.infrastructure.out.persistence.jpa.adapter.*Adapter.*(..))
    """)
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String parameters = getParameters(joinPoint);

        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        logger.info(
                "{} ({})",
                methodName,
                parameters
        );

        long start = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long elapsedTime = (System.nanoTime() - start) / 1_000_000;

            logger.info(
                    "{} -> {} [{} ms]",
                    methodName,
                    result,
                    elapsedTime
            );

            return result;

        } catch (Exception exception) {

            long elapsedTime = (System.nanoTime() - start) / 1_000_000;

            logger.info(
                    "{} -> ERROR [{} ms]",
                    methodName,
                    elapsedTime
            );

            throw exception;
        }
    }

    private String getParameters(ProceedingJoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        Map<String, Object> parameters = new LinkedHashMap<>();

        for (int i = 0; i < parameterValues.length; i++) {
            parameters.put(parameterNames[i], parameterValues[i]);
        }

        return parameters.toString();
    }
}