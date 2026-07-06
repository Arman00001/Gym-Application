package com.epam.gymapp.ops;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order
public class GymHttpMetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;

    public GymHttpMetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.nanoTime() - startTime;

            String method = request.getMethod();
            String uri = normalizeUri(request.getRequestURI());
            String status = String.valueOf(response.getStatus());

            Counter.builder("gym_http_requests")
                    .description("Total number of Gym API HTTP requests")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", status)
                    .register(meterRegistry)
                    .increment();

            Timer.builder("gym_http_request_duration")
                    .description("Duration of Gym API HTTP requests")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", status)
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
        }
    }

    private String normalizeUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return "unknown";
        }

        if (uri.matches("^/trainees/[^/]+/is-active$")) {
            return "/trainees/{username}/is-active";
        }

        if (uri.matches("^/trainees/[^/]+$")) {
            return "/trainees/{username}";
        }

        if (uri.matches("^/trainers/[^/]+/is-active$")) {
            return "/trainers/{username}/is-active";
        }

        if (uri.matches("^/trainers/not-assigned/[^/]+$")) {
            return "/trainers/not-assigned/{traineeUsername}";
        }

        if (uri.matches("^/trainers/[^/]+$")) {
            return "/trainers/{username}";
        }

        if (uri.matches("^/trainings/trainees/[^/]+$")) {
            return "/trainings/trainees/{username}";
        }

        if (uri.matches("^/trainings/trainers/[^/]+$")) {
            return "/trainings/trainers/{username}";
        }

        if (uri.matches("^/users/[^/]+/login$")) {
            return "/users/{username}/login";
        }

        if (uri.matches("^/users/[^/]+/password$")) {
            return "/users/{username}/password";
        }

        return uri;
    }
}