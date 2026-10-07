package com.lsp.pricingservice.infrastructure.out.persistence.jpa.repository;

import com.lsp.pricingservice.infrastructure.out.persistence.jpa.entity.PriceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Limit;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class PriceJpaRepositoryTest {

    private static final Integer PRODUCT_ID = 35455;
    private static final Integer BRAND_ID = 1;
    private static final Integer PRODUCT_ID_01 = 35001;


    @Autowired
    private PriceJpaRepository priceJpaRepository;

    @ParameterizedTest
    @MethodSource("applicablePrices")
    void givenApplicationDateProductIdAndBrandId_whenFindApplicablePrice_thenReturnPriceList(LocalDateTime applicationDate, BigDecimal expectedPrice) {
        List<PriceEntity> prices = findApplicablePrices(PRODUCT_ID, BRAND_ID, applicationDate);

        assertThat(prices)
                .singleElement()
                .extracting(PriceEntity::getPrice)
                .isEqualTo(expectedPrice);
    }

    @Test
    void givenSeveralApplicablePricesWithTheSameMaximumPriority_whenFindApplicablePrice_thenReturnAllTiedPrices() {
        List<PriceEntity> prices = findApplicablePrices(
                PRODUCT_ID_01, BRAND_ID, LocalDateTime.of(2021, 6, 14, 12, 0));

        assertThat(prices)
                .extracting(PriceEntity::getId)
                .containsExactlyInAnyOrder(7, 8);
    }

    @Test
    void givenNoMatchingPrice_whenFindApplicablePrice_thenReturnEmptyList() {
        List<PriceEntity> prices = findApplicablePrices(
                99999, BRAND_ID, LocalDateTime.of(2020, 6, 14, 12, 0));

        assertThat(prices).isEmpty();
    }

    @Test
    void givenMoreThanTwoApplicablePricesWithTheSameMaximumPriority_whenFindApplicablePrice_thenReturnAtMostTwoPrices() {
        PriceEntity thirdTiedPrice = new PriceEntity();
        thirdTiedPrice.setId(9);
        thirdTiedPrice.setBrandId(BRAND_ID);
        thirdTiedPrice.setProductId(PRODUCT_ID_01);
        thirdTiedPrice.setStartDate(LocalDateTime.of(2021, 6, 14, 0, 0));
        thirdTiedPrice.setEndDate(LocalDateTime.of(2021, 12, 31, 23, 59, 59));
        thirdTiedPrice.setPriceList(6);
        thirdTiedPrice.setPriority(1);
        thirdTiedPrice.setPrice(new BigDecimal("69.95"));
        thirdTiedPrice.setCurrencyIsoCode("EUR");
        priceJpaRepository.saveAndFlush(thirdTiedPrice);

        List<PriceEntity> prices = findApplicablePrices(
                PRODUCT_ID_01, BRAND_ID, LocalDateTime.of(2021, 6, 14, 12, 0));

        assertThat(prices).hasSize(2);
    }

    private List<PriceEntity> findApplicablePrices(Integer productId, Integer brandId, LocalDateTime applicationDate) {
        return priceJpaRepository.findApplicablePriceCandidates(
                productId,
                brandId,
                applicationDate,
                Limit.of(2));
    }

    private static Stream<Arguments> applicablePrices() {
        return Stream.of(
                //Test 1: petición a las 10:00 del día 14 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 10, 0), new BigDecimal("35.50")
                ),
                //Test 2: petición a las 16:00 del día 14 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 16, 0), new BigDecimal("25.45")
                ),
                //Test 3: petición a las 21:00 del día 14 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 21, 0), new BigDecimal("35.50")
                ),
                //Test 4: petición a las 10:00 del día 15 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 15, 10, 0), new BigDecimal("30.50")
                ),
                //Test 5: petición a las 21:00 del día 16 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 16, 21, 0), new BigDecimal("38.95")
                ),
                // Price 2's interval includes both exact endpoints.
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 15, 0), new BigDecimal("25.45")
                ),
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 18, 30), new BigDecimal("25.45")
                ),
                // Immediately outside Price 2's interval, the base price applies.
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 14, 59, 59), new BigDecimal("35.50")
                ),
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 18, 30, 1), new BigDecimal("35.50")
                )
        );
    }

}
