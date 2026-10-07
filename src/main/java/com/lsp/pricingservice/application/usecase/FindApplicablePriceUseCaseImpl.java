package com.lsp.pricingservice.application.usecase;

import com.lsp.pricingservice.application.exception.ErrorCode;
import com.lsp.pricingservice.application.exception.ServiceException;
import com.lsp.pricingservice.application.port.in.FindApplicablePriceUseCase;
import com.lsp.pricingservice.application.port.out.PriceQueryPort;
import com.lsp.pricingservice.domain.model.Price;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class FindApplicablePriceUseCaseImpl implements FindApplicablePriceUseCase {

    private final PriceQueryPort priceQueryPort;

    @Override
    public Price findApplicablePrice(Integer productId, Integer brandId, LocalDateTime applicationDate) {

        List<Price> prices = priceQueryPort.findApplicablePriceCandidates(productId, brandId, applicationDate);

        if (prices.isEmpty()) {
            throw new ServiceException(ErrorCode.PRICE_NOT_FOUND, productId, brandId);
        }

        if (prices.size() == 2 && prices.get(0).getPriority().equals(prices.get(1).getPriority())) {
            throw new ServiceException(ErrorCode.DUPLICATED_PRICE, productId, brandId, applicationDate);
        }

        return prices.getFirst();
    }
}