package com.lsp.pricingservice.infrastructure.in.web.exception;

import com.lsp.pricingservice.adapter.in.web.dto.ErrorResponseDTO;
import com.lsp.pricingservice.application.exception.ErrorCode;
import com.lsp.pricingservice.application.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceException(ServiceException exception, Locale locale) {

        log.error("Service exception [{}]: {}",
                exception.getErrorCode().getCode(), exception.getMessage(), exception);

        ErrorCode errorCode = exception.getErrorCode();
        HttpStatus status = getHttpStatus(errorCode);

        String message = getMessage(errorCode.getCode(), locale, exception.getParameters());

        return buildResponse(status, errorCode.getCode(), message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException exception, Locale locale) {
        log.error("Missing required request parameter", exception);

        String message = getMessage("MISSING_PARAMETER", locale, exception.getParameterName());
        return buildResponse(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException exception, Locale locale) {
        log.error("Invalid request parameter", exception);

        String message = getMessage("INVALID_PARAMETER", locale, exception.getName());
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception exception, Locale locale) {
        log.error("Unexpected internal error", exception);

        String message = getMessage("INTERNAL_ERROR", locale);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", message);
    }

    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case PRICE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATED_PRICE -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private String getMessage(String code, Locale locale, Object... parameters) {
        return messageSource.getMessage(code, parameters, locale);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String code, String message) {
        ErrorResponseDTO response = new ErrorResponseDTO();
        response.setStatus(status.value());
        response.setErrorType(status.getReasonPhrase());
        response.setCode(code);
        response.setMessage(message);

        return ResponseEntity.status(status).body(response);
    }
}
