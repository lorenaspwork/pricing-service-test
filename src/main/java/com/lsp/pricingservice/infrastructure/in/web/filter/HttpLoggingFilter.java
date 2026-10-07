package com.lsp.pricingservice.infrastructure.in.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final int CONTENT_CACHE_LIMIT = 10_000;

    private static final Set<String> LOGGABLE_HEADERS = Set.of(
            "accept",
            "accept-language",
            "content-type",
            "user-agent"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long start = System.nanoTime();

        logRequest(wrappedRequest);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            logResponse(wrappedResponse, elapsedMs);

            wrappedResponse.copyBodyToResponse();
            MDC.remove("requestId");
        }
    }

    private void logRequest(HttpServletRequest request) {

        log.info("""
                
                HTTP REQUEST
                  {} {}
                  Query: {}
                  Headers:
                {}
                  Body: <empty>
                """,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                getHeaders(request)
        );
    }

    private void logResponse(ContentCachingResponseWrapper response, long elapsedMs) {

        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("""
                
                HTTP RESPONSE
                  Status: {}
                  Time: {} ms
                  Body: {}
                """,
                response.getStatus(),
                elapsedMs,
                body.isBlank() ? "<empty>" : body
        );
    }

    private String getHeaders(HttpServletRequest request) {

        StringBuilder headers = new StringBuilder();

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();

            if (!LOGGABLE_HEADERS.contains(headerName.toLowerCase(Locale.ROOT))) {
                continue;
            }

            headers.append("    ")
                    .append(headerName)
                    .append(": ")
                    .append(request.getHeader(headerName))
                    .append(System.lineSeparator());
        }

        return headers.toString();
    }
}