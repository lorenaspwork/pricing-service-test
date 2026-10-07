package com.lsp.pricingservice.infrastructure.in.web;

import com.lsp.pricingservice.adapter.in.web.dto.PriceResponseDTO;
import com.lsp.pricingservice.application.port.in.FindApplicablePriceUseCase;
import com.lsp.pricingservice.domain.model.Price;
import com.lsp.pricingservice.infrastructure.in.web.mapper.PriceMapper;
import com.lsp.pricingservice.support.PriceTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingControllerTest {

    @Mock
    private FindApplicablePriceUseCase findApplicablePriceUseCase;

    @Mock
    private PriceMapper priceMapper;

    private PricingController controller;

    @BeforeEach
    void setUp() {
        controller = new PricingController(findApplicablePriceUseCase, priceMapper);
    }

    @Test
    void givenValidParameters_whenGetApplicablePrice_thenReturnOkWithPriceResponse() {
        TestData testData = testData();

        when(findApplicablePriceUseCase.findApplicablePrice(
                testData.productId(), testData.brandId(), testData.applicationDate()))
                .thenReturn(testData.price());
        when(priceMapper.toDTO(testData.price())).thenReturn(testData.response());

        ResponseEntity<PriceResponseDTO> result =
                controller.getApplicablePrice(
                        testData.applicationDate(), testData.productId(), testData.brandId());

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isSameAs(testData.response());
    }

    @Test
    void givenValidParameters_whenGetApplicablePrice_thenUseCaseIsCalled() {
        TestData testData = testData();

        when(findApplicablePriceUseCase.findApplicablePrice(
                testData.productId(), testData.brandId(), testData.applicationDate()))
                .thenReturn(testData.price());

        controller.getApplicablePrice(
                testData.applicationDate(), testData.productId(), testData.brandId());

        verify(findApplicablePriceUseCase)
                .findApplicablePrice(
                        testData.productId(),
                        testData.brandId(),
                        testData.applicationDate());
    }

    @Test
    void givenUseCaseReturnsPrice_whenGetApplicablePrice_thenMapperIsCalled() {
        TestData testData = testData();

        when(findApplicablePriceUseCase.findApplicablePrice(
                testData.productId(), testData.brandId(), testData.applicationDate()))
                .thenReturn(testData.price());
        when(priceMapper.toDTO(testData.price())).thenReturn(testData.response());

        controller.getApplicablePrice(
                testData.applicationDate(), testData.productId(), testData.brandId());

        verify(priceMapper).toDTO(testData.price());
    }

    @Test
    void givenUseCaseThrowsException_whenGetApplicablePrice_thenPropagateException() {
        TestData testData = testData();
        RuntimeException expectedException = new RuntimeException("use case failure");

        when(findApplicablePriceUseCase.findApplicablePrice(
                testData.productId(), testData.brandId(), testData.applicationDate()))
                .thenThrow(expectedException);

        RuntimeException actualException = assertThrowsExactly(
                RuntimeException.class,
                () -> controller.getApplicablePrice(
                        testData.applicationDate(), testData.productId(), testData.brandId()));

        assertThat(actualException).isSameAs(expectedException);
        verify(findApplicablePriceUseCase)
                .findApplicablePrice(
                        testData.productId(),
                        testData.brandId(),
                        testData.applicationDate());
    }

    private TestData testData() {
        return new TestData(
                PriceTestDataFactory.PRODUCT_ID,
                PriceTestDataFactory.BRAND_ID,
                PriceTestDataFactory.applicationDate(),
                PriceTestDataFactory.price(),
                PriceTestDataFactory.priceResponse());
    }

    private record TestData(
            Integer productId,
            Integer brandId,
            LocalDateTime applicationDate,
            Price price,
            PriceResponseDTO response) {
    }
}
