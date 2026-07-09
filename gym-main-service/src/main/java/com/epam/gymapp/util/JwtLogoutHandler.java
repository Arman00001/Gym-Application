package com.epam.gymapp.util;

import com.epam.gymapp.persistence.entity.BlacklistedToken;
import com.epam.gymapp.persistence.repository.token.BlacklistedTokenRepository;
import com.epam.gymapp.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

/**
 * Logout handler which is responsible for invalidating JWT access tokens
 *
 * <p>
 *     When a logout request contains a valid Authorization header with a verified JWT,
 *     this handler stores the token in a blacklist. The token will remain there until
 *     its expiration date.
 * </p>
 * <p>
 *     If the Authorization header is missing, has an unsupported authentication type,
 *     contains an invalid token, or the token is already blacklisted, no action is performed.
 * </p>
 * */
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Extracts the JWT from the Authorization header, verifies it, and stores it in a blacklist
     * with its expiration date.
     *
     * <p>
     *     If the header is empty, the token type is invalid, the token cannot be verified or
     *     it is already blacklisted, then no action is performed
     * </p>
     * @param request the HTTP request containing the Authorization header
     * @param response the HTTP response
     * @param authentication the current authentication, not used in this implementation
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtUtil.AUTH_TYPE)) {
            return;
        }

        String token = authorizationHeader.substring(JwtUtil.AUTH_TYPE.length());

        if (!jwtUtil.isVerified(token)) {
            return;
        }

        if (blacklistedTokenRepository.existsByToken(token)) {
            return;
        }

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiresAt(
                jwtUtil.getExpirationDate(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        blacklistedTokenRepository.save(blacklistedToken);
    }
}

