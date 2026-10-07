package com.lsp.pricingservice.support;

import com.lsp.pricingservice.adapter.in.web.dto.PriceResponseDTO;
import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.domain.model.vo.ValidityPeriod;
import com.lsp.pricingservice.infrastructure.out.persistence.jpa.entity.PriceEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PriceTestDataFactory {

    public static final Integer ID = 42;
    public static final Integer BRAND_ID = 7;
    public static final Integer PRODUCT_ID = 1234;
    public static final Integer PRICE_LIST = 3;
    public static final Integer PRIORITY = 8;
    public static final BigDecimal PRICE = new BigDecimal("19.95");
    public static final String CURRENCY_ISO_CODE = "EUR";
    public static final LocalDateTime START_DATE = LocalDateTime.of(2024, 5, 10, 9, 30);
    public static final LocalDateTime END_DATE = LocalDateTime.of(2024, 5, 10, 18, 0);

    private PriceTestDataFactory() {
    }

    public static PriceBuilder priceBuilder() {
        return new PriceBuilder();
    }

    public static Price price() {
        return priceBuilder().build();
    }

    public static PriceEntity priceEntity() {
        return priceBuilder().buildEntity();
    }

    public static PriceResponseDTO priceResponse() {
        return priceBuilder().buildResponse();
    }

    public static LocalDateTime applicationDate() {
        return LocalDateTime.of(2024, 5, 10, 10, 0);
    }

    public static final class PriceBuilder {

        private Integer id = ID;
        private final Integer brandId = BRAND_ID;
        private final Integer productId = PRODUCT_ID;
        private final Integer priceList = PRICE_LIST;
        private final Integer priority = PRIORITY;
        private final BigDecimal price = PRICE;
        private final String currencyIsoCode = CURRENCY_ISO_CODE;
        private final LocalDateTime startDate = START_DATE;
        private final LocalDateTime endDate = END_DATE;

        public PriceBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Price build() {
            return new Price(
                    brandId,
                    productId,
                    new ValidityPeriod(startDate, endDate),
                    priceList,
                    priority,
                    price,
                    currencyIsoCode);
        }

        public PriceEntity buildEntity() {
            PriceEntity entity = new PriceEntity();
            entity.setId(id);
            entity.setBrandId(brandId);
            entity.setProductId(productId);
            entity.setStartDate(startDate);
            entity.setEndDate(endDate);
            entity.setPriceList(priceList);
            entity.setPriority(priority);
            entity.setPrice(price);
            entity.setCurrencyIsoCode(currencyIsoCode);
            return entity;
        }

        public PriceResponseDTO buildResponse() {
            return new PriceResponseDTO(
                    productId,
                    brandId,
                    priceList,
                    startDate,
                    endDate,
                    price);
        }
    }
}
