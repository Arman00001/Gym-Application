package com.epam.gymapp.workload.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the trainer workload service.
 *
 * <p>
 * Disables CSRF, allows access to the H2 console, protects trainer workload
 * endpoints with authentication, and enables HTTP Basic authentication.
 * </p>
 */
@Configuration
public class WebSecurityConfig {

    /**
     * Builds the security filter chain for the workload service.
     *
     * @param http the HTTP security configuration
     * @return the configured security filter chain
     * @throws Exception if the security filter chain cannot be built
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/trainer-workloads/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}