package com.lsp.pricingservice.infrastructure.config;

import com.lsp.pricingservice.application.port.in.FindApplicablePriceUseCase;
import com.lsp.pricingservice.application.port.out.PriceQueryPort;
import com.lsp.pricingservice.application.usecase.FindApplicablePriceUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    FindApplicablePriceUseCase findApplicablePriceUseCase(PriceQueryPort priceQueryPort) {
        return new FindApplicablePriceUseCaseImpl(priceQueryPort);
    }
}