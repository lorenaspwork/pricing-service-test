package com.lsp.pricingservice.infrastructure.in.web.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpLoggingFilterTest {

    private final HttpLoggingFilter filter = new HttpLoggingFilter();

    @BeforeEach
    void clearMdc() {
        MDC.remove("requestId");
    }

    @AfterEach
    void cleanUpMdc() {
        MDC.remove("requestId");
    }

    @Test
    void givenRequest_whenDoFilter_thenGenerateRequestIdRunChainAndCopyResponseBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/prices");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> requestIdInChain = new AtomicReference<>();
        AtomicBoolean chainExecuted = new AtomicBoolean();

        filter.doFilterInternal(request, response, (wrappedRequest, wrappedResponse) -> {
            chainExecuted.set(true);
            requestIdInChain.set(MDC.get("requestId"));
            ((HttpServletResponse) wrappedResponse).setStatus(HttpServletResponse.SC_CREATED);
            wrappedResponse.getWriter().write("price response");
        });

        assertThat(chainExecuted).isTrue();
        assertThat(requestIdInChain.get()).isNotBlank();
        assertThat(UUID.fromString(requestIdInChain.get())).isNotNull();
        assertThat(MDC.get("requestId")).isNull();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CREATED);
        assertThat(response.getContentAsString()).isEqualTo("price response");
    }

    @Test
    void givenFilterChainThrows_whenDoFilter_thenClearRequestIdAndPropagateException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/prices");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletException expectedException = new ServletException("chain failure");
        AtomicReference<String> requestIdInChain = new AtomicReference<>();

        ServletException actualException = assertThrows(
                ServletException.class,
                () -> filter.doFilterInternal(request, response, (wrappedRequest, wrappedResponse) -> {
                    requestIdInChain.set(MDC.get("requestId"));
                    wrappedResponse.getWriter().write("partial response");
                    throw expectedException;
                }));

        assertSame(expectedException, actualException);
        assertThat(requestIdInChain.get()).isNotBlank();
        assertThat(UUID.fromString(requestIdInChain.get())).isNotNull();
        assertThat(MDC.get("requestId")).isNull();
        assertThat(response.getContentAsString()).isEqualTo("partial response");
    }
}
