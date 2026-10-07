package com.lsp.pricingservice.infrastructure.in.web.mapper;

import com.lsp.pricingservice.adapter.in.web.dto.PriceResponseDTO;
import com.lsp.pricingservice.domain.model.Price;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    @Mapping(target = "price", source = "amount")
    PriceResponseDTO toDTO(Price price);

}
