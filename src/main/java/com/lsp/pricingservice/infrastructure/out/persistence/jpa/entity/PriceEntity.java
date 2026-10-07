package com.lsp.pricingservice.infrastructure.out.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRICES")
public class PriceEntity {

    @Id
    private Integer id;

    private Integer brandId;

    private Integer productId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer priceList;

    private Integer priority;

    private BigDecimal price;

    private String currencyIsoCode;
}
