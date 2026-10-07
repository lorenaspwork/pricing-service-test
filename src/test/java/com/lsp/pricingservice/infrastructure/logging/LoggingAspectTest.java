package com.lsp.pricingservice.infrastructure.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoggingAspectTest {

    private final LoggingAspect aspect = new LoggingAspect();
    private final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    private final MethodSignature signature = mock(MethodSignature.class);
    private final LoggingTarget target = new LoggingTarget();
    private final Logger logger = (Logger) LoggerFactory.getLogger(LoggingTarget.class);
    private final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("findApplicablePrice");
        when(signature.getParameterNames()).thenReturn(new String[0]);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.getTarget()).thenReturn(target);

        logAppender.setContext(logger.getLoggerContext());
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(logAppender);
        logAppender.stop();
    }

    @Test
    void givenSuccessfulInvocation_whenLogExecution_thenLogOkAndElapsedTime() throws Throwable {
        when(joinPoint.proceed()).thenReturn("OK");

        Object result = aspect.logExecution(joinPoint);

        assertThat(result).isEqualTo("OK");
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(message ->
                        assertThat(message).matches("findApplicablePrice -> OK \\[\\d+ ms]"));
    }

    @Test
    void givenInvocationThrows_whenLogExecution_thenLogErrorAndRethrowSameException() throws Throwable {
        RuntimeException expectedException = new IllegalStateException("test failure");
        when(joinPoint.proceed()).thenThrow(expectedException);

        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> aspect.logExecution(joinPoint));

        assertSame(expectedException, actualException);
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(message ->
                        assertThat(message).matches("findApplicablePrice -> ERROR \\[\\d+ ms]"));
    }

    private static final class LoggingTarget {
    }
}
