package com.lsp.pricingservice.application.port.in;

import com.lsp.pricingservice.domain.model.Price;

import java.time.LocalDateTime;

public interface FindApplicablePriceUseCase {

    Price findApplicablePrice(Integer productId, Integer brandId, LocalDateTime applicationDate);
}
