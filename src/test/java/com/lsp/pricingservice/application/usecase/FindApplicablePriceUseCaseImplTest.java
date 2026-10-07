package com.lsp.pricingservice.application.usecase;

import com.lsp.pricingservice.application.exception.ErrorCode;
import com.lsp.pricingservice.application.exception.ServiceException;
import com.lsp.pricingservice.application.port.in.FindApplicablePriceUseCase;
import com.lsp.pricingservice.application.port.out.PriceQueryPort;
import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.support.PriceTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceUseCaseImplTest {

    private static final Integer PRODUCT_ID = PriceTestDataFactory.PRODUCT_ID;
    private static final Integer BRAND_ID = PriceTestDataFactory.BRAND_ID;
    private static final LocalDateTime APPLICATION_DATE = PriceTestDataFactory.applicationDate();

    private FindApplicablePriceUseCase useCase;

    @Mock
    private PriceQueryPort priceQueryPort;

    @BeforeEach
    void setUp() {
        useCase = new FindApplicablePriceUseCaseImpl(priceQueryPort);
    }

    @Test
    void givenExactlyOneValidPrice_whenFindApplicablePrice_thenReturnThatPrice() {
        Price expectedPrice = PriceTestDataFactory.price();
        when(priceQueryPort.findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(List.of(expectedPrice));

        Price result = useCase.findApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result).isSameAs(expectedPrice);
        verify(priceQueryPort).findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenNoApplicablePrices_whenFindApplicablePrice_thenThrowPriceNotFound() {
        when(priceQueryPort.findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(List.of());

        assertThatThrownBy(() -> useCase.findApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRICE_NOT_FOUND));

        verify(priceQueryPort).findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenTwoApplicablePricesWithSamePriority_whenFindApplicablePrice_thenThrowDuplicatedPrice() {
        when(priceQueryPort.findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(List.of(
                        PriceTestDataFactory.priceBuilder().withId(1).withPriority(5).build(),
                        PriceTestDataFactory.priceBuilder().withId(2).withPriority(5).build()));

        assertThatThrownBy(() -> useCase.findApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_PRICE));

        verify(priceQueryPort).findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenTwoApplicablePricesWithDifferentPriorities_whenFindApplicablePrice_thenReturnFirstPrice() {
        Price highestPriorityPrice = PriceTestDataFactory.priceBuilder()
                .withId(1)
                .withPriority(5)
                .build();
        Price lowerPriorityPrice = PriceTestDataFactory.priceBuilder()
                .withId(2)
                .withPriority(3)
                .build();
        when(priceQueryPort.findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(List.of(highestPriorityPrice, lowerPriorityPrice));

        Price result = useCase.findApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result).isSameAs(highestPriorityPrice);
        verify(priceQueryPort).findApplicablePriceCandidates(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

}
