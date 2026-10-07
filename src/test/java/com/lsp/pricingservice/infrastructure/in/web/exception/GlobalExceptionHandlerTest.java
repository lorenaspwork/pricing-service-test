package com.lsp.pricingservice.infrastructure.in.web.exception;

import com.lsp.pricingservice.adapter.in.web.dto.ErrorResponseDTO;
import com.lsp.pricingservice.application.exception.ErrorCode;
import com.lsp.pricingservice.application.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        handler = new GlobalExceptionHandler(messageSource);
    }

    @Test
    void givenServiceException_whenHandleServiceException_thenReturnLocalizedNotFoundError() {
        ServiceException exception = new ServiceException(ErrorCode.PRICE_NOT_FOUND, 1234, 7);

        ResponseEntity<ErrorResponseDTO> result =
                handler.handleServiceException(exception, Locale.ENGLISH);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isNotNull().extracting(
                ErrorResponseDTO::getStatus,
                ErrorResponseDTO::getErrorType,
                ErrorResponseDTO::getCode,
                ErrorResponseDTO::getMessage
        ).containsExactly(
                404,
                "Not Found",
                "PRICE-001",
                "No applicable price was found for product 1,234 and brand 7.");
    }

    @Test
    void givenMissingParameterException_whenHandleMissingParameter_thenReturnLocalizedBadRequest() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("applicationDate", "LocalDateTime");

        ResponseEntity<ErrorResponseDTO> result =
                handler.handleMissingParameter(exception, Locale.forLanguageTag("es"));

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isNotNull().extracting(
                ErrorResponseDTO::getStatus,
                ErrorResponseDTO::getErrorType,
                ErrorResponseDTO::getCode,
                ErrorResponseDTO::getMessage
        ).containsExactly(
                400,
                "Bad Request",
                "MISSING_PARAMETER",
                "Falta el parámetro requerido 'applicationDate'.");
    }

    @Test
    void givenTypeMismatchException_whenHandleTypeMismatch_thenReturnLocalizedBadRequest() {
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException(
                        "not-a-date", Integer.class, "productId", mock(MethodParameter.class),
                        new IllegalArgumentException("invalid value"));

        ResponseEntity<ErrorResponseDTO> result =
                handler.handleTypeMismatch(exception, Locale.ENGLISH);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody()).isNotNull().extracting(
                ErrorResponseDTO::getStatus,
                ErrorResponseDTO::getErrorType,
                ErrorResponseDTO::getCode,
                ErrorResponseDTO::getMessage
        ).containsExactly(
                400,
                "Bad Request",
                "INVALID_PARAMETER",
                "Parameter 'productId' has an invalid format.");
    }

    @Test
    void givenGenericException_whenHandleGenericException_thenReturnLocalizedInternalError() {
        ResponseEntity<ErrorResponseDTO> result =
                handler.handleGenericException(new IllegalStateException(), Locale.forLanguageTag("es"));

        assertThat(result.getStatusCode().value()).isEqualTo(500);
        assertThat(result.getBody()).isNotNull().extracting(
                ErrorResponseDTO::getStatus,
                ErrorResponseDTO::getErrorType,
                ErrorResponseDTO::getCode,
                ErrorResponseDTO::getMessage
        ).containsExactly(
                500,
                "Internal Server Error",
                "INTERNAL_ERROR",
                "Se ha producido un error interno.");
    }
}
