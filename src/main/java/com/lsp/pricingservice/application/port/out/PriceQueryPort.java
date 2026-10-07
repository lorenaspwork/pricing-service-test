package com.lsp.pricingservice.application.port.out;

import com.lsp.pricingservice.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceQueryPort {

    List<Price> findApplicablePriceCandidates(Integer productId, Integer brandId, LocalDateTime applicationDate);
}
