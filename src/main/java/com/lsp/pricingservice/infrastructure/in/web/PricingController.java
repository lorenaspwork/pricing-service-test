package com.lsp.pricingservice.infrastructure.in.web;

import com.lsp.pricingservice.adapter.in.web.api.PricingApi;
import com.lsp.pricingservice.adapter.in.web.dto.PriceResponseDTO;
import com.lsp.pricingservice.application.port.in.FindApplicablePriceUseCase;
import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.infrastructure.in.web.mapper.PriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class PricingController implements PricingApi {

    private final FindApplicablePriceUseCase findApplicablePriceUseCase;

    private final PriceMapper mapper;

    @Override
    public ResponseEntity<PriceResponseDTO> getApplicablePrice(LocalDateTime applicationDate, Integer productId, Integer brandId) {
        Price price = findApplicablePriceUseCase.findApplicablePrice(productId, brandId, applicationDate);

        return ResponseEntity.ok(mapper.toDTO(price));
    }
}
