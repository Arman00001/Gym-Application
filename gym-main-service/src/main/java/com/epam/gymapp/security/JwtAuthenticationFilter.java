package com.epam.gymapp.security;

import com.epam.gymapp.persistence.repository.token.BlacklistedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security filter responsible for authenticating requests using JWT access tokens.
 *
 * <p>
 * This filter reads the Authorization header, extracts the bearer token, checks
 * that the token is not blacklisted, verifies the token, and then loads the
 * corresponding user details.
 * </p>
 *
 * <p>
 * If the token is valid and no authentication is already present in the security
 * context, the filter creates an authentication object and stores it in
 * {@link SecurityContextHolder}.
 * </p>
 *
 * <p>
 * Requests without a bearer token, with an invalid token, or with a blacklisted
 * token are passed to the next filter without authentication being set.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Processes each request and sets authentication in the security context when
     * a valid non-blacklisted JWT token is provided.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param filterChain the remaining filter chain
     * @throws ServletException if an error occurs during request filtering
     * @throws IOException if an input or output error occurs during request filtering
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(JwtUtil.AUTH_TYPE)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(JwtUtil.AUTH_TYPE.length());

        if (blacklistedTokenRepository.existsByToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.isVerified(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.getUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}