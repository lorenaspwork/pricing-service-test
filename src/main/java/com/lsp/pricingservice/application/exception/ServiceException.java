package com.lsp.pricingservice.application.exception;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final Serializable[] parameters;

    public ServiceException(ErrorCode errorCode, Serializable... parameters) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

}
