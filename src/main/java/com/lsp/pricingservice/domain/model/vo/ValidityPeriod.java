package com.lsp.pricingservice.domain.model.vo;

import java.time.LocalDateTime;

public record ValidityPeriod(LocalDateTime startDate, LocalDateTime endDate) {
}
