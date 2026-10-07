package com.lsp.pricingservice.domain.model;

import com.lsp.pricingservice.domain.model.vo.ValidityPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Price {

    private final Integer brandId;

    private final Integer productId;

    private final ValidityPeriod validityPeriod;

    private final Integer priceList;

    private final Integer priority;

    private final BigDecimal amount;

    private final String currencyIsoCode;

    public Price(Integer brandId, Integer productId, ValidityPeriod validityPeriod,
                 Integer priceList, Integer priority, BigDecimal amount, String currencyIsoCode) {
        this.brandId = brandId;
        this.productId = productId;
        this.validityPeriod = Objects.requireNonNull(validityPeriod, "validityPeriod must not be null");
        this.priceList = priceList;
        this.priority = priority;
        this.amount = amount;
        this.currencyIsoCode = currencyIsoCode;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public Integer getProductId() {
        return productId;
    }

    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public LocalDateTime getStartDate() {
        return validityPeriod.startDate();
    }

    public LocalDateTime getEndDate() {
        return validityPeriod.endDate();
    }

    public Integer getPriceList() {
        return priceList;
    }

    public Integer getPriority() {
        return priority;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    @Override
    public String toString() {
        return "Price{" +
                "brandId=" + brandId +
                ", productId=" + productId +
                ", validityPeriod=" + validityPeriod +
                ", priceList=" + priceList +
                ", priority=" + priority +
                ", amount=" + amount +
                ", currencyIsoCode='" + currencyIsoCode + '\'' +
                '}';
    }
}