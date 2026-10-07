package com.lsp.pricingservice.infrastructure.in.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PricingControllerIntegrationTest {

    private static final String PRICES_ENDPOINT = "/api/v1/prices";
    private static final String CONTEXT_PATH = "/api";

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("applicablePrices")
    void givenApplicationDateProductIdAndBrandId_whenGetApplicablePrice_thenReturnPrice(
            LocalDateTime applicationDate, Integer productId, Integer brandId, Integer expectedPriceList,
            BigDecimal expectedPrice) throws Exception {

        mockMvc.perform(get(PRICES_ENDPOINT)
                        .contextPath(CONTEXT_PATH)
                        .param("applicationDate", String.valueOf(applicationDate))
                        .param("productId", productId.toString())
                        .param("brandId", brandId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.brandId").value(brandId))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.price").value(expectedPrice.doubleValue()));
    }

    @Test
    void givenNoApplicablePrice_whenGetApplicablePrice_thenReturnNotFoundError() throws Exception {
        mockMvc.perform(get(PRICES_ENDPOINT)
                        .contextPath(CONTEXT_PATH)
                        .header("Accept-Language", "en")
                        .param("applicationDate", "2021-01-01T00:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorType").value("Not Found"))
                .andExpect(jsonPath("$.code").value("PRICE-001"))
                .andExpect(jsonPath("$.message")
                        .value("No applicable price was found for product 35,455 and brand 1."));
    }

    @Test
    void givenTwoApplicablePricesWithSamePriority_whenGetApplicablePrice_thenReturnPriorityConflictError()
            throws Exception {
        mockMvc.perform(get(PRICES_ENDPOINT)
                        .contextPath(CONTEXT_PATH)
                        .header("Accept-Language", "en")
                        .param("applicationDate", "2021-06-14T12:00:00")
                        .param("productId", "35001")
                        .param("brandId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.errorType").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("PRICE-002"))
                .andExpect(jsonPath("$.message")
                        .value("Multiple prices with the maximum priority were found for product 35,001 and brand 1."));
    }

    private static Stream<Arguments> applicablePrices() {
        return Stream.of(
                //Test 1: petición a las 10:00 del día 14 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 10, 0),
                        35455,
                        1,
                        1,
                        new BigDecimal("35.50")
                ),
                //Test 2: petición a las 16:00 del día 14 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 16, 0),
                        35455,
                        1,
                        2,
                        new BigDecimal("25.45")
                ),
                //Test 3: petición a las 21:00 del día 14 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 14, 21, 0),
                        35455,
                        1,
                        1,
                        new BigDecimal("35.50")
                ),
                //Test 4: petición a las 10:00 del día 15 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 15, 10, 0),
                        35455,
                        1,
                        3,
                        new BigDecimal("30.50")
                ),
                //Test 5: petición a las 21:00 del día 16 del producto 35455   para la brand 1 (ZARA)
                Arguments.of(
                        LocalDateTime.of(2020, 6, 16, 21, 0),
                        35455,
                        1,
                        4,
                        new BigDecimal("38.95")
                )
        );
    }

}
