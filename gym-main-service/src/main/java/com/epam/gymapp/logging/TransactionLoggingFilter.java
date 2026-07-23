package com.epam.gymapp.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static com.epam.gymapp.logging.TransactionConstants.TRANSACTION_ID;
import static com.epam.gymapp.logging.TransactionConstants.TRANSACTION_ID_HEADER;

/**
 * Logs HTTP request and response details for each transaction.
 *
 * <p>Uses an incoming transaction ID header when available, or generates a new
 * transaction ID otherwise. The transaction ID is added to the response and
 * SLF4J MDC. Request and response bodies are logged with selected sensitive
 * fields masked.</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String transactionId = Optional
                .ofNullable(request.getHeader(TRANSACTION_ID_HEADER))
                .orElse(UUID.randomUUID().toString());

        MDC.put(TRANSACTION_ID, transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request, 0);

        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        log.info(
                "TRANSACTION START: method={}, uri={}",
                request.getMethod(),
                request.getRequestURI()
        );

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            log.error(
                    "TRANSACTION ERROR: method={}, uri={}, error={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;

            String requestBody = getRequestBody(wrappedRequest);
            String responseBody = getResponseBody(wrappedResponse);

            log.info(
                    "TRANSACTION END: method={}, uri={}, status={}, durationMs={}, requestBody={}, responseBody={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    wrappedResponse.getStatus(),
                    durationMs,
                    sanitize(requestBody),
                    sanitize(responseBody)
            );

            wrappedResponse.copyBodyToResponse();
            MDC.remove(TRANSACTION_ID);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return new String(content, StandardCharsets.UTF_8);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return new String(content, StandardCharsets.UTF_8);
    }

    private String sanitize(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        return body
                .replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                .replaceAll("(?i)\"oldPassword\"\\s*:\\s*\"[^\"]*\"", "\"oldPassword\":\"***\"")
                .replaceAll("(?i)\"newPassword\"\\s*:\\s*\"[^\"]*\"", "\"newPassword\":\"***\"")
                .replaceAll("(?i)\"accessToken\"\\s*:\\s*\"[^\"]*\"", "\"accessToken\":\"***\"")
                .replaceAll("(?i)\"refreshToken\"\\s*:\\s*\"[^\"]*\"", "\"refreshToken\":\"***\"");
    }
}