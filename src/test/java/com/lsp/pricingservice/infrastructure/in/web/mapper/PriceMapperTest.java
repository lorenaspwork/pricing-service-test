package com.lsp.pricingservice.infrastructure.in.web.mapper;

import com.lsp.pricingservice.adapter.in.web.dto.PriceResponseDTO;
import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.support.PriceTestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriceMapperTest {

    private final PriceMapper mapper = new PriceMapperImpl();

    @Test
    void givenPrice_whenToDTO_thenMapAllFields() {
        Price price = PriceTestDataFactory.price();

        PriceResponseDTO result = mapper.toDTO(price);

        assertThat(result).extracting(
                PriceResponseDTO::getProductId,
                PriceResponseDTO::getBrandId,
                PriceResponseDTO::getPriceList,
                PriceResponseDTO::getStartDate,
                PriceResponseDTO::getEndDate,
                PriceResponseDTO::getPrice
        ).containsExactly(
                price.getProductId(),
                price.getBrandId(),
                price.getPriceList(),
                price.getStartDate(),
                price.getEndDate(),
                price.getAmount());
    }

    @Test
    void givenNullPrice_whenToDTO_thenReturnNull() {
        assertThat(mapper.toDTO(null)).isNull();
    }
}
