package com.lsp.pricingservice.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PRICE_NOT_FOUND("PRICE-001"),
    DUPLICATED_PRICE("PRICE-002");

    private final String code;

}
