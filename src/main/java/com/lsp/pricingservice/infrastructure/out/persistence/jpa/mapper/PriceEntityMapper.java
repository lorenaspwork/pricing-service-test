package com.lsp.pricingservice.infrastructure.out.persistence.jpa.mapper;

import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.infrastructure.out.persistence.jpa.entity.PriceEntity;
import com.lsp.pricingservice.domain.model.vo.ValidityPeriod;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    default Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                new ValidityPeriod(entity.getStartDate(), entity.getEndDate()),
                entity.getPriceList(),
                entity.getPriority(),
                entity.getPrice(),
                entity.getCurrencyIsoCode());
    }
}
