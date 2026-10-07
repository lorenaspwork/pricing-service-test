package com.lsp.pricingservice.infrastructure.out.persistence.jpa.adapter;

import com.lsp.pricingservice.application.port.out.PriceQueryPort;
import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.infrastructure.out.persistence.jpa.mapper.PriceEntityMapper;
import com.lsp.pricingservice.infrastructure.out.persistence.jpa.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceQueryPersistenceAdapter implements PriceQueryPort {

    private static final int MAX_CANDIDATES_TO_DETECT_DUPLICATE = 2;

    private final PriceJpaRepository repository;

    private final PriceEntityMapper mapper;

    @Override
    public List<Price> findApplicablePriceCandidates(
            Integer productId,
            Integer brandId,
            LocalDateTime applicationDate) {
        return repository.findApplicablePriceCandidates(
                        productId,
                        brandId,
                        applicationDate,
                        Limit.of(MAX_CANDIDATES_TO_DETECT_DUPLICATE))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

}
