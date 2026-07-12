package com.epam.gymapp.configuration;

import com.epam.gymapp.logging.TransactionConstants;
import com.epam.gymapp.security.JwtUtil;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class FeignAuthConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public RequestInterceptor bearerAuthRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(
                    HttpHeaders.AUTHORIZATION,
                    JwtUtil.AUTH_TYPE + jwtUtil.generateServiceToken()
            );

            String transactionId = Optional
                    .ofNullable(MDC.get(TransactionConstants.TRANSACTION_ID))
                    .orElse(UUID.randomUUID().toString());

            requestTemplate.header(
                    TransactionConstants.TRANSACTION_ID_HEADER,
                    transactionId
            );

        };
    }
}